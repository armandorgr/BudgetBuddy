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
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentChatBinding
import com.example.budgetbuddy.viewmodels.ChatViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args:ChatFragmentArgs by navArgs()
    private lateinit var selectedGroupUID: String
    private val viewModel:ChatViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        selectedGroupUID = args.selectedGroupUID
        viewModel.addMemberShipListener(selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!)
        lifecycleScope.launch {
            viewModel.isMember.collect{
                if(!it){
                    val action = ChatFragmentDirections.navChatToGroups(null)
                    findNavController().navigate(action)
                    Toast.makeText(requireActivity(), getString(R.string.kicked_out_of_group_message,""), Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

}