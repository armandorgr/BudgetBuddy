package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.databinding.FragmentFriendsBinding
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.PromptResult
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.InvitationsViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@AndroidEntryPoint
class FriendsFragment : Fragment() {
    private lateinit var friendsViewModel: FriendsViewModel
    private var _binding: FragmentFriendsBinding? = null
    private val viewModel: InvitationsViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var membersAdapter: NewGroupFriendsAdapter
    private val _members: MutableStateFlow<List<ListItemUiModel.User>> =
        MutableStateFlow(emptyList())
    val members: StateFlow<List<ListItemUiModel.User>> = _members
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Se inicializa el adaptador
        membersAdapter = NewGroupFriendsAdapter()
        binding.recyclerView.adapter = membersAdapter

        lifecycleScope.launch {
            friendsViewModel.friendsUidList.collect { friendsList ->
                // Se filtra la lista de amigos para incluir solo amigos
                val friends = friendsList.filterIsInstance<ListItemUiModel.User>()
                // Se actualizan los datos del adaptador con la lista de amigos
                membersAdapter.setData(friends)
            }
        }
    }




    private fun onSendInvitationClick(view: View?) {
        val dialogFactory = AlertDialogFactory(requireContext())
        val data = PromptResult(
            getString(R.string.send_invitation_title),
            getString(R.string.send_invitation_hint),
            { dialog ->
                val txt = dialog.findViewById<EditText>(R.id.newEditText).text.toString()
                if (txt != "") {
                    lifecycleScope.launch {
                        val userUID: User? = viewModel.findUIDByUsername(txt)
                        Log.d("prueba", "$userUID")
                        if (userUID != null) {
                            homeViewModel.firebaseUser.value?.uid?.let {
                                val invitation = InvitationUiModel(
                                    it,
                                    homeViewModel.currentUser.value?.username,
                                    "%s quiere conectar contigo",
                                    INVITATION_TYPE.FRIEND_REQUEST,
                                    LocalDateTime.now().toString()
                                )
                                viewModel.writeNewInvitation(txt, it, invitation)
                                dialog.dismiss()
                            }
                        } else {
                            dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText =
                                "No existe un usuario con este username"
                        }
                    }
                }
            },
            {}
        )
        dialogFactory.createPromptDialog(binding.root, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
