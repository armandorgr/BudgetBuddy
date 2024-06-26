package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.adapters.recyclerView.InvitationAdapter
import com.example.budgetbuddy.databinding.FragmentInvitationsBinding
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.InvitationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Clase responsable de vincular la logica definida en [InvitationsViewModel] con la vista del fragmento [InvitationsFragment]
 * La forma de recoger los datos mediante collect fue consulado en la documentación de Kotlin: https://kotlinlang.org/docs/flow.html#flows
 * La forma de trabajar con el binding fue consulada en la documentación de Android: https://developer.android.com/topic/libraries/view-binding
 * @author Armando Guzmán
 * */
@AndroidEntryPoint
class InvitationsFragment : Fragment() {
    private lateinit var binding: FragmentInvitationsBinding
    private lateinit var viewModel: InvitationsViewModel
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var invitationsAdapter: InvitationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[InvitationsViewModel::class.java]
        binding = FragmentInvitationsBinding.inflate(layoutInflater, container, false)

        binding.invitationsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val imageLoader = ListItemImageLoader(requireContext())

        invitationsAdapter = InvitationAdapter(
            layoutInflater, imageLoader, requireContext(), viewModel.onAccept, viewModel.onDecline,
            homeViewModel.firebaseUser.value!!
        )
        binding.invitationsRecyclerView.adapter = invitationsAdapter

        //Se cargan las invitaciones del usuario logeado con collect, para que cada vez que el valor de esta lista
        //cambia el adapter tambien se actaliza
        lifecycleScope.launch {
            viewModel.invitationsList.collect {
                binding.notFoundTextView.visibility= if (it.isEmpty()) View.VISIBLE else View.GONE // Si no tiene invitaciones se muestra un mensaje de que no hay
                invitationsAdapter.setData(it)
            }
        }
        return binding.root
    }
}