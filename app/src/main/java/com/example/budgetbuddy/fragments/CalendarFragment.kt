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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.calendar.CalendarAdapter
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewmodels.GroupsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CalendarFragment : Fragment(), CalendarAdapter.OnItemListener {
    private var _binding: FragmentCalendarBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private val groupsViewModel: GroupsViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var selectedDate: LocalDate
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        selectedDate = LocalDate.now()
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        homeViewModel.firebaseUser.observe(viewLifecycleOwner) {
            groupsViewModel.loadGroups(it.uid)
        }

        lifecycleScope.launch {
            groupsViewModel.groupList.collect { groups ->
                withContext(Dispatchers.Main){
                    launch {
                        setMonthView(groups)
                    }
                }
            }
        }

        binding.prevBtn.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            lifecycleScope.launch {
                withContext(Dispatchers.Main){
                    launch {
                        setMonthView(groupsViewModel.groupList.value)
                    }
                }
            }
        }
        binding.nextBtn.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            lifecycleScope.launch {
                withContext(Dispatchers.Main){
                    launch {
                        setMonthView(groupsViewModel.groupList.value)
                    }
                }
            }
        }


        return binding.root
    }

    private fun setMonthView(groups: List<ListItemUiModel.Group>) {
        binding.monthYearTV.text = monthYearFromDate(this.selectedDate)
        val daysInMonth: List<ListItemUiModel.CalendarDayUiModel> =
            daysInMonth(this.selectedDate, groups)
        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonth(
        date: LocalDate,
        groups: List<ListItemUiModel.Group>
    ): List<ListItemUiModel.CalendarDayUiModel> {
        val daysInMonthArray: MutableList<ListItemUiModel.CalendarDayUiModel> = mutableListOf()
        val yearMonth: YearMonth = YearMonth.from(date)
        val daysInMonth: Int = yearMonth.lengthOfMonth();
        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek: Int = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(ListItemUiModel.CalendarDayUiModel("", false))
            } else {
                val localDateTime = LocalDateTime.of(date.year, date.monthValue, (i - dayOfWeek), 0, 0)
                var hasEvent = false
                for(group in groups){
                    if(dateBetweenRange(group.groupUiModel, localDateTime)){
                        hasEvent=true
                        break
                    }
                }
                val day = ListItemUiModel.CalendarDayUiModel((i - dayOfWeek).toString(), hasEvent)
                daysInMonthArray.add(day)
            }
        }
        return daysInMonthArray
    }

    private fun dateBetweenRange(group: Group, date:LocalDateTime):Boolean{
        val startDate = LocalDateTime.parse(group.startDate)
        val endDate = LocalDateTime.parse(group.endDate)
        Log.d("prueba", "startDate: $startDate")
        Log.d("prueba", "endDate: $endDate")
        Log.d("prueba", "date: $date")
        val isEqualToAny = date.isEqual(startDate) || date.isEqual(endDate)
        Log.d("prueba", "isEqualToAny: $isEqualToAny")
        return isEqualToAny || (date.isAfter(startDate) && date.isBefore(endDate))
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formater: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formater)
    }

    override fun onItemClick(position: Int, dayText: String) {
        val selectedDate = LocalDateTime.of(selectedDate.year, selectedDate.monthValue, dayText.toInt(), 0, 0)
        val action = HomeFragmentDirections.navHomeToGroups(selectedDate.toString())
        findNavController().navigate(action)
    }
}