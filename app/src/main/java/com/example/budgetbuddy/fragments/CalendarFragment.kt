package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.adapters.calendar.CalendarAdapter
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewmodels.GroupsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Fragmento en donde se mostrará un calendario con los días del mes seleccionado, mostrando con un circulo azul,
 * aquellos días cuya fecha este contenida dentro de la fechas de inicio y fin de al menos un grupo.
 * Al hacer clic sobre alguno de estos días, el usuario será redirigido hacia el fragmento de grupos mostrando únicamente
 * aquellos grupos que cumplan esa fecha.
 * El funcionamiento básico fue extraído de aquí  https://www.youtube.com/watch?v=Ba0Q-cK1fJo
 * y luego fue adaptado para cumplir con nuestras necesidades.
 * La forma de recoger los datos mediante collect fue consulado en la documentación de Kotlin: https://kotlinlang.org/docs/flow.html#flows
 * La forma de trabajar con el binding fue consulada en la documentación de Android: https://developer.android.com/topic/libraries/view-binding
 * @author Armando Guzmán
 * */
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

        // Se recogen los grupos cargados
        lifecycleScope.launch {
            groupsViewModel.groupList.collect { groups ->
                withContext(Dispatchers.Main) {
                    launch {
                        setMonthView(groups)
                    }
                }
            }
        }

        binding.prevBtn.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    launch {
                        setMonthView(groupsViewModel.groupList.value)
                    }
                }
            }
        }
        binding.nextBtn.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    launch {
                        setMonthView(groupsViewModel.groupList.value)
                    }
                }
            }
        }


        return binding.root
    }

    /**
     * Método que sirve para establecer el mes actual a mostrar en el calendario.
     * @param groups Lista de grupos a usar para rellenar aquellos dias en los cuales cae un grupo.
     * */
    private fun setMonthView(groups: List<ListItemUiModel.Group>) {
        binding.monthYearTV.text = monthYearFromDate(this.selectedDate)
        val daysInMonth: List<ListItemUiModel.CalendarDayUiModel> =
            daysInMonth(this.selectedDate, groups)
        val calendarAdapter = CalendarAdapter(daysInMonth, requireContext(), this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    /**
     * Método usado para obtener una lista de los días correspondientes al mes actual.
     * @param date Fecha usada para determinar la cantidad de días.
     * @param groups Lista de grupos usada para filtrar los días y colorearlos.
     * @return Lista de días del mes actual
     * */
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
                val localDateTime =
                    LocalDateTime.of(date.year, date.monthValue, (i - dayOfWeek), 0, 0)
                var hasEvent = false
                for (group in groups) {
                    if (dateBetweenRange(group.groupUiModel, localDateTime)) {
                        hasEvent = true
                        break
                    }
                }
                val day = ListItemUiModel.CalendarDayUiModel((i - dayOfWeek).toString(), hasEvent)
                daysInMonthArray.add(day)
            }
        }
        return daysInMonthArray
    }

    /**
     * Método usado para validar si una fecha está incluida dentro de la fecha de inicio y fin de un grupo
     * @param group Grupo usado para obtener su fecha de inicio y fin validar.
     * @param date Fecha que se quiere validar si esta incluida dentro de la fecha de inicio y fin del grupo proporcionado.
     * @return true si está incluida y false si no.
     * */
    private fun dateBetweenRange(group: Group, date: LocalDateTime): Boolean {
        val startDate = LocalDateTime.parse(group.startDate)
        val endDate = LocalDateTime.parse(group.endDate)
        val isEqualToAny = date.isEqual(startDate) || date.isEqual(endDate)
        return isEqualToAny || (date.isAfter(startDate) && date.isBefore(endDate))
    }

    /**
     * Método que sirve para obtener el mes y año de la fecha proporcionada.
     * @param date Fecha de la cual se quiere extraer el mes y año.
     * @return Representaciónn en cadena de texto del mes y año de la fecha proporcionada.
     * */
    private fun monthYearFromDate(date: LocalDate): String {
        val formater: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formater)
    }

    override fun onItemClick(position: Int, dayText: String) {
        val selectedDate =
            LocalDateTime.of(selectedDate.year, selectedDate.monthValue, dayText.toInt(), 0, 0)
        val action = HomeFragmentDirections.navHomeToGroups(selectedDate.toString())
        findNavController().navigate(action)
    }
}