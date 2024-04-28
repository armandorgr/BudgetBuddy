package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.MessageAdapter
import com.example.budgetbuddy.databinding.FragmentChatBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.ImageLoader
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewmodels.ChatViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var selectedGroupUID: String
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private val imageLoader = ImageLoader(this,
        { uri -> viewModel.onSuccessGallery(uri, selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!, this::onPhotoMessageComplete) },
        { bitmap -> viewModel.onSuccessCamera(bitmap, selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!, this::onPhotoMessageComplete) },
        { viewModel.onPhotoLoadFail(requireContext()) })
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        selectedGroupUID = args.selectedGroupUID
        viewModel.addMemberShipListener(selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!)
        lifecycleScope.launch {
            viewModel.isMember.collect {
                if (!it) {
                    val action = ChatFragmentDirections.navChatToGroups(null)
                    findNavController().navigate(action)
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.kicked_out_of_group_message, ""),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        viewModel.loadMessages(selectedGroupUID)
        prepareBinding()
        return binding.root
    }

    private fun prepareBinding() {
        binding.sendImgButton.setOnClickListener(this::onAddPhotoClick)
        binding.sendButton.setOnClickListener {
            viewModel.setMessageText(binding.inputEditText.text.toString().trim())
            val validationResult = viewModel.validateMessage(requireContext())
            if (validationResult == null) {
                viewModel.sendMessage(selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!)
            } else {
                Toast.makeText(requireContext(), validationResult, Toast.LENGTH_SHORT).show()
            }
        }
        val messagesAdapter = MessageAdapter(
            homeViewModel.firebaseUser.value?.uid!!,
            layoutInflater,
            ListItemImageLoader(requireContext()),
            requireContext()
        )
        binding.chatRecyclerView.adapter = messagesAdapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        lifecycleScope.launch {
            viewModel.messages.collect{
                val orderedList = it.sortedWith { g1, g2 ->
                    (g2.message.sentDate!! - g1.message.sentDate!!).toInt()
                }
                messagesAdapter.setData(orderedList)
            }
        }
    }

    private fun onAddPhotoClick(view: View?){
        val alertDialogFactory = AlertDialogFactory(requireContext())
        alertDialogFactory.createPhotoDialog(binding.root, {imageLoader.getPhotoFromGallery()}, {imageLoader.getPhotoFromCamera()})
    }

    private fun onPhotoMessageComplete(task: Task<Void>) {
        if(task.isSuccessful){
            Toast.makeText(requireContext(), "foto subida correctamente", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireContext(), "error rrrrrrrrr", Toast.LENGTH_SHORT).show()

        }
    }
}