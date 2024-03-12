package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentNewGroupBinding
import com.example.budgetbuddy.util.DateResult
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class NewGroupFragment : Fragment() {
    private lateinit var binding: FragmentNewGroupBinding
    private val viewModel: NewGroupViewModel by viewModels()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_group,container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        prepareBinding(binding)
        return binding.root
    }


    private fun prepareBinding(binding: FragmentNewGroupBinding){
        binding.startDate.setOnClickListener(this::onStartDateClick)
        binding.endDate.setOnClickListener(this::onEndDateClick)

        binding.groupNameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setGroupName(text.toString())
            viewModel.validateGroupName(text.toString(), requireContext())
        })
        binding.groupDescriptionEditText.addTextChangedListener( afterTextChanged = {text ->
            viewModel.setGroupDescription(text.toString())
            viewModel.validateGroupDescription(text.toString(), requireContext())
        })
        viewModel.startDate.observe(viewLifecycleOwner){
            binding.startDate.text = dateFormatter.format(it)
        }
        viewModel.endDate.observe(viewLifecycleOwner){
            binding.endDate.text = dateFormatter.format(it)
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