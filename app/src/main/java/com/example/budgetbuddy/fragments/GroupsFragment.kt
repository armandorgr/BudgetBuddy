package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.GroupsAdapter
import com.example.budgetbuddy.databinding.FragmentGroupsBinding
import com.example.budgetbuddy.viewmodels.GroupsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupsFragment : Fragment() {
    private lateinit var binding:FragmentGroupsBinding
    private val viewModel: GroupsViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var groupsAdapter: GroupsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding = FragmentGroupsBinding.inflate(layoutInflater, container, false)

        homeViewModel.firebaseUser.value?.uid?.let { viewModel.loadGroups(it) }
        groupsAdapter = GroupsAdapter(layoutInflater, viewModel.onClick)
        prepareBinding(binding)
        return binding.root
    }

    private fun prepareBinding(binding: FragmentGroupsBinding){
        binding.GroupsRecyclerView.adapter = groupsAdapter
        binding.GroupsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        lifecycleScope.launch {
            viewModel.groupList.collect{
                groupsAdapter.setData(it)
            }
        }
    }
}