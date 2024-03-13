package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.databinding.FragmentNewGroupBinding
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class GroupOverviewFragment : Fragment(){
    private var _binding:FragmentNewGroupBinding? = null
    private val args: GroupOverviewFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private val viewModel: NewGroupViewModel by viewModels()
    private lateinit var homeViewModel:HomeViewModel
    private lateinit var selectedGroup: Group
    private lateinit var selectedGroupUID:String
    private lateinit var friendsAdapter:NewGroupFriendsAdapter
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        selectedGroup = args.selectedGroup
        selectedGroupUID = args.selectedGroupUID
        homeViewModel.firebaseUser.value?.uid?.let { viewModel.setCurrentUserUID(it) }
        friendsAdapter = NewGroupFriendsAdapter(inflater, viewModel.getSelectedList())

        prepareBinding()
        lifecycleScope.launch {
            viewModel.members.collect{
                friendsAdapter.setData(it)
            }
        }
        return binding.root
    }

    private fun showSuccessDialog(message:String){
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.success_title),
            message,
            getString(R.string.ok)
        ){
            findNavController().navigate(R.id.nav_groups)
        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    private fun showFailDialog(message: String){
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.fail_title),
            message,
            getString(R.string.try_again)
        ){

        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    private fun onGroupUpdateComplete(task: Task<Void>){
        if(task.isSuccessful){
            showSuccessDialog(getString(R.string.group_update_success))
        }else{
            showFailDialog(getString(R.string.group_update_fail))
        }
    }

    private fun onGroupDeleteComplete(task: Task<Void>){
        if(task.isSuccessful){
            showSuccessDialog(getString(R.string.group_delete_success))
        }else{
            showFailDialog(getString(R.string.group_delete_fail))
        }
    }

    private fun prepareBinding(){
        selectedGroupUID.let { viewModel.loadMembers(it) }
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.adapter = friendsAdapter

        binding.createGroupBtn.visibility = View.GONE
        binding.deleteGroupBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener{
                viewModel.deleteGroup(selectedGroupUID){
                    onGroupDeleteComplete(it)
                }
            }
        }
        binding.updateGroupBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener{
                if(viewModel.allGood){
                    viewModel.updateGroup(selectedGroupUID){
                        onGroupUpdateComplete(it)
                    }
                }
            }
        }

        viewModel.setGroupName(selectedGroup.name.toString())
        viewModel.setGroupDescription(selectedGroup.description.toString())
        viewModel.startDate.observe(viewLifecycleOwner){
            binding.startDate.text = it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
        viewModel.endDate.observe(viewLifecycleOwner){
            binding.endDate.text = it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
        viewModel.setStartDate(LocalDateTime.parse(selectedGroup.startDate))
        viewModel.setEndDate(LocalDateTime.parse(selectedGroup.endDate))

        binding.startDate.setOnClickListener{
            viewModel.onStartDateClick(requireContext(), binding.root)
        }
        binding.endDate.setOnClickListener{
            viewModel.onEndDateClick(requireContext(), binding.root)
        }
        binding.groupNameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setGroupName(text.toString())
            viewModel.validateGroupName(text.toString(), requireContext())
        })
        binding.groupDescriptionEditText.addTextChangedListener( afterTextChanged = {text ->
            viewModel.setGroupDescription(text.toString())
            viewModel.validateGroupDescription(text.toString(), requireContext())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}