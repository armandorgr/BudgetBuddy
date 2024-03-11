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
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.InvitationAdapter
import com.example.budgetbuddy.databinding.FragmentInvitationsBinding
import com.example.budgetbuddy.model.DateSent
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.InvitationViewHolder
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.InvitationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class InvitationsFragment : Fragment() {
    private lateinit var binding: FragmentInvitationsBinding
    private lateinit var viewModel: InvitationsViewModel
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var invitationsAdapter: InvitationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[InvitationsViewModel::class.java]
        binding = FragmentInvitationsBinding.inflate(layoutInflater, container, false)

        binding.invitationsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        invitationsAdapter = InvitationAdapter(
            layoutInflater, viewModel.onAccept, viewModel.onDecline,
            homeViewModel.firebaseUser.value!!
        )
        binding.invitationsRecyclerView.adapter = invitationsAdapter
        /* para crear invitaciones de prueba descomentar esto y comentar la ultima parte menos en return
        invitationsAdapter.setData(
            listOf(
                ListItemUiModel.Invitation(
                    InvitationUiModel(
                        "cbeklEpJUxUHOWKtXN1y8KX7MBx2",
                        "Pepe",
                        "%s quiere conectar contigo",
                        INVITATION_TYPE.FRIEND_REQUEST,
                        LocalDateTime.now().toString()
                    )
                ),
                ListItemUiModel.Invitation(
                    InvitationUiModel(
                        "RzyILMaJGzUUdW139dBSxn3Xups1",
                        "Luis",
                        "%s quiere conectar contigo",
                        INVITATION_TYPE.FRIEND_REQUEST,
                        LocalDateTime.now().toString()
                    )
                ),
                ListItemUiModel.Invitation(
                    InvitationUiModel(
                        "Rvv11I8zGAYaWYM8oUNZWTKEV5E2",
                        "Maria",
                        "%s quiere conectar contigo",
                        INVITATION_TYPE.FRIEND_REQUEST,
                        LocalDateTime.now().toString()
                    )
                )
            )
        )*/

        viewModel.setAdapter(invitationsAdapter)
        lifecycleScope.launch {
            viewModel.invitationsList.collect {
                invitationsAdapter.setData(it)
            }
        }
        return binding.root
    }
}