package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.InvitationAdapter
import com.example.budgetbuddy.databinding.FragmentInvitationsBinding
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.InvitationViewHolder
import java.time.LocalDateTime

class InvitationsFragment : Fragment() {
    private lateinit var binding: FragmentInvitationsBinding
    private val onAccept = object : InvitationAdapter.OnClickListener{
        override fun onItemClick(invitation: InvitationUiModel) {

        }
    }

    private val onDecline = object : InvitationAdapter.OnClickListener{
        override fun onItemClick(invitation: InvitationUiModel) {

        }
    }

    private val invitationsAdapter by lazy {
        InvitationAdapter(layoutInflater, onAccept, onDecline)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvitationsBinding.inflate(layoutInflater, container, false)
        binding.invitationsRecyclerView.adapter = invitationsAdapter
        binding.invitationsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        invitationsAdapter.setData(
            listOf(
                ListItemUiModel.Invitation(
                    InvitationUiModel(
                        "Pepe",
                        "%s quiere conectar contigo",
                        INVITATION_TYPE.FRIEND_REQUEST,
                        LocalDateTime.of(2024,3,7,13,25)
                    )
                ),
                ListItemUiModel.Invitation(
                    InvitationUiModel(
                        "Pedrito",
                        "%s quiere conectar contigo",
                        INVITATION_TYPE.FRIEND_REQUEST,
                        LocalDateTime.of(2024,3,8,13,25)
                    )
                )
            )
        )
        return binding.root
    }
}