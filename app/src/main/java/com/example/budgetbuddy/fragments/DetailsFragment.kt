package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.tab.DETAILS_TABS_PAIR
import com.example.budgetbuddy.adapters.tab.DetailsMenuAdapter
import com.example.budgetbuddy.databinding.FragmentDetailsBinding
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewmodels.DetailsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var selectedGroupUID: String
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentDetailsBinding? = null
    private val viewModel: DetailsViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        selectedGroupUID = args.selectedGroupUID

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = DetailsMenuAdapter(childFragmentManager, lifecycle, selectedGroupUID)
        viewPager.adapter = adapter


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = resources.getString(DETAILS_TABS_PAIR[position])
        }.attach()

        viewModel.loadGroupData(selectedGroupUID)
        lifecycleScope.launch {
            viewModel.groupData.collect {
                it?.let { group ->
                    Log.d("prueba", "listener activo ${group.groupUiModel.name}")
                    binding.groupNameView.text = group.groupUiModel.name ?: ""
                    group.groupUiModel.pic?.let { path ->
                        ListItemImageLoader(requireContext()).loadImage(
                            path,
                            binding.groupPicImageView
                        )
                    }
                    binding.membersNumView.text = getString(
                        R.string.members_num_text,
                        group.groupUiModel.members?.size.toString()
                    )
                }
            }
        }
        viewModel.addMemberShipListener(selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!)
        lifecycleScope.launch {
            viewModel.isMember.collect {
                if (!it) {
                    val action = DetailsFragmentDirections.navDetailsToGroups(null)
                    try{
                        findNavController().navigate(action)
                        Toast.makeText(
                            requireActivity(),
                            getString(
                                R.string.kicked_out_of_group_message,
                                viewModel.groupData.value?.groupUiModel?.name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }catch (e: IllegalArgumentException){
                           Log.d("prueba", "${e.message}")
                    }
                }
            }
        }

        binding.groupDataLinearLayout.setOnClickListener {
            viewModel.groupData.value?.let { group ->
                val action = DetailsFragmentDirections.navDetailsToGroupOverview(
                    selectedGroupUID,
                    group.groupUiModel
                )
                findNavController().navigate(action)
            }
        }
        binding.chatIcon.setOnClickListener{
            viewModel.groupData.value?.let { group ->
                val action = DetailsFragmentDirections.navDetailsToNavChat(group.uid)
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("prueba", "onDestroy")
        _binding = null
    }
}