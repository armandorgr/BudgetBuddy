package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.databinding.FragmentNewGroupBinding
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.DateResult
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.FieldPosition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@AndroidEntryPoint
class NewGroupFragment : Fragment() {
    private lateinit var binding: FragmentNewGroupBinding
    private val viewModel: NewGroupViewModel by viewModels()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private lateinit var friendsViewModel: FriendsViewModel
    private lateinit var friendsAdapter: NewGroupFriendsAdapter
    private lateinit var homeViewModel: HomeViewModel

    private val searchViewFilter = object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            if(query!=null){
                friendsAdapter.filterData(query)
            }else{
                friendsAdapter.resetData()
            }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText!=null){
                friendsAdapter.filterData(newText)
            }else{
                friendsAdapter.resetData()
            }
            return true
        }
    }

    override fun onPause() {
        super.onPause()
        //friendsAdapter.resetData()
        friendsViewModel.resetChecked()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        friendsViewModel = ViewModelProvider(requireActivity())[FriendsViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_group,container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        friendsAdapter = NewGroupFriendsAdapter(layoutInflater, viewModel.getSelectedList())
        prepareBinding(binding)
        lifecycleScope.launch {
            friendsViewModel.friendsUidList.collect{
                friendsAdapter.setData(it)
            }
        }
        return binding.root
    }

    private fun showSuccessDialog(){
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.success_title),
            getString(R.string.new_group_success_text),
            getString(R.string.ok)
        ){
            val activity = requireActivity() as HomeActivity
            activity.goToGroups()
        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    private fun prepareBinding(binding: FragmentNewGroupBinding){
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.adapter = friendsAdapter
        binding.searchView.setOnQueryTextListener(searchViewFilter)
        binding.startDate.setOnClickListener(this::onStartDateClick)
        binding.endDate.setOnClickListener(this::onEndDateClick)
        binding.createGroupBtn.setOnClickListener{
            if(viewModel.allGood){
                if(homeViewModel.firebaseUser.value != null){
                    binding.determinateBar.visibility = View.VISIBLE
                    viewModel.createNewGroup(homeViewModel.firebaseUser.value!!.uid){
                        binding.determinateBar.visibility = View.INVISIBLE
                        if(it.isSuccessful){
                            showSuccessDialog()
                        }else{

                        }
                    }
                }
            }
        }
        binding.groupNameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setGroupName(text.toString())
            viewModel.validateGroupName(text.toString(), requireContext())
        })
        binding.groupDescriptionEditText.addTextChangedListener( afterTextChanged = {text ->
            viewModel.setGroupDescription(text.toString())
            viewModel.validateGroupDescription(text.toString(), requireContext())
        })
        viewModel.startDate.observe(viewLifecycleOwner){
            binding.startDate.text = it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
        viewModel.endDate.observe(viewLifecycleOwner){
            binding.endDate.text = it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
    }

    private fun onEndDateClick(view: View?) {
        val dateResult = DateResult(
            getString(R.string.end_date_title),
            {
                val datePicker = it.findViewById<DatePicker>(R.id.datePicker)
                val response:String? = viewModel.validateEndDate(getDate(datePicker), requireContext())
                if(response != null){
                    showToast(response)
                }
            },{}
        )
        viewModel.showDatePickerDialog(requireContext(), dateResult, binding.root)
    }

    private fun getDate(datePicker: DatePicker): LocalDateTime {
        return LocalDateTime.of(
            datePicker.year,
            datePicker.month + 1,
            datePicker.dayOfMonth,
            0,
            0,
            0
        )
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun onStartDateClick(view: View?) {
        val dateResult = DateResult(
            getString(R.string.new_group),
            {
                val datePicker = it.findViewById<DatePicker>(R.id.datePicker)
                val response:String? = viewModel.validateStartDate(getDate(datePicker), requireContext())
                if(response != null){
                    showToast(response)
                }
            }, {}
        )
        viewModel.showDatePickerDialog(requireContext(), dateResult, binding.root)
    }
}