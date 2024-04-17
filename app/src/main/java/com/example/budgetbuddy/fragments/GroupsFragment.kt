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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.adapters.recyclerView.GroupsAdapter
import com.example.budgetbuddy.databinding.FragmentGroupsBinding
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewmodels.GroupsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Clase responsable de vincular la logica de la carga de los grupos a los que pertenece el usuario
 * implementada en el [GroupsViewModel] con la vista
 * */
@AndroidEntryPoint
class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GroupsViewModel by viewModels()
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
            val action = GroupsFragmentDirections.navGroupsToOverview(group.groupUiModel, group.uid)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentGroupsBinding.inflate(layoutInflater, container, false)
        //Se cargan los grupos a los que pertenece el usuario actual
        homeViewModel.firebaseUser.value?.uid?.let { viewModel.loadGroups(it) }
        groupsAdapter = GroupsAdapter(layoutInflater, ListItemImageLoader(requireContext()), onClick)
        prepareBinding(binding)
        return binding.root
    }

    private fun prepareBinding(binding: FragmentGroupsBinding) {
        binding.GroupsRecyclerView.adapter = groupsAdapter
        binding.GroupsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        //Se recogen los datos cargados en el viewmodel y al usar collect, cada vez que se actualizen
        //se cargan en el Adapter, por lo cual siempre estara actualizado
        lifecycleScope.launch {
            viewModel.groupList.collect {
                groupsAdapter.setData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Se borra el binding al destruir la vista, para evitar fugas de informacion y liberar recursos
        homeViewModel.firebaseUser.value?.uid?.let { viewModel.resetLoad(it) }
        _binding = null
    }
}