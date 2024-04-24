package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.adapters.recyclerView.GroupsAdapter
import com.example.budgetbuddy.databinding.FragmentGroupsBinding
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewmodels.GroupsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.time.LocalDateTime

/**
 * Clase responsable de vincular la logica de la carga de los grupos a los que pertenece el usuario
 * implementada en el [GroupsViewModel] con la vista
 * */
@AndroidEntryPoint
class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GroupsViewModel by viewModels()
    private val args: GroupsFragmentArgs by navArgs()
    private var filterDate: LocalDateTime? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var groupsAdapter: GroupsAdapter

    /**
     * Objeto anonimo el cual implementa la interfaz [GroupsAdapter.OnClickListener]
     * para dar implementacion al metodo que se ejecutara al hacer click sobre alguno de los grupos
     * cargados
     * */
    private val onClick = object : GroupsAdapter.OnClickListener {
        override fun onItemClick(group: ListItemUiModel.Group, position: Int) {
            //Se crea una accion con los parametros a pasar al fragmento de GroupOverview
            val action = GroupsFragmentDirections.navGroupsToDetails(group.uid)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentGroupsBinding.inflate(layoutInflater, container, false)
        filterDate = try {
            args.filterDate?.let { LocalDateTime.parse(it) }
        } catch (e: InvocationTargetException) {
            null
        }
        //Se cargan los grupos a los que pertenece el usuario actual
        homeViewModel.firebaseUser.value?.uid?.let { viewModel.loadGroups(it) }
        groupsAdapter =
            GroupsAdapter(layoutInflater, ListItemImageLoader(requireContext()), onClick)
        prepareBinding(binding)
        return binding.root
    }

    private fun prepareBinding(binding: FragmentGroupsBinding) {
        binding.GroupsRecyclerView.adapter = groupsAdapter
        binding.GroupsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.searchView.setOnQueryTextListener(viewModel.getSearchViewFilter(groupsAdapter))
        //Se recogen los datos cargados en el viewmodel y al usar collect, cada vez que se actualizen
        //se cargan en el Adapter, por lo cual siempre estara actualizado
        lifecycleScope.launch {
            viewModel.groupList.collect {
                binding.determinateBar.visibility = View.VISIBLE // se hace visible el progess bar
                binding.notFoundTextView.visibility= if (it.isEmpty()) View.VISIBLE else View.GONE // Si no hay grupos se muestra un mensaje de que no hay
                var orderedList = it.sortedWith { g1, g2 ->
                    (g2.groupUiModel.lastUpdated!! - g1.groupUiModel.lastUpdated!!).toInt()
                }
                filterDate?.let { date ->
                    orderedList =
                        orderedList.filter { group -> filterGroupByDate(group.groupUiModel, date) }
                    Log.d("prueba", "lista filtrada: $orderedList")
                }
                groupsAdapter.setData(orderedList)
                binding.determinateBar.visibility = View.INVISIBLE // se hace invisible el progess bar
            }
        }
    }

    private fun filterGroupByDate(group: Group, filterDate: LocalDateTime): Boolean {
        val startDate = LocalDateTime.parse(group.startDate)
        val endDate = LocalDateTime.parse(group.endDate)
        val isEqualToAny = filterDate.isEqual(startDate) || filterDate.isEqual(endDate)
        return isEqualToAny || (filterDate.isAfter(startDate) && filterDate.isBefore(endDate))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Se borra el binding al destruir la vista, para evitar fugas de informacion y liberar recursos
        _binding = null
    }
}