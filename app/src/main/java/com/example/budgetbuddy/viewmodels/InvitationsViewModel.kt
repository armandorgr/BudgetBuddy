package com.example.budgetbuddy.viewmodels

import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy.adapters.recyclerView.InvitationAdapter
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.InvitationsRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InvitationsViewModel @Inject constructor(
    private val repo: InvitationsRepository,
    private val userRepo: UsersRepository
) : ViewModel() {
    private val _invitationsList = MutableStateFlow<List<ListItemUiModel>>(emptyList())
    val invitationsList: StateFlow<List<ListItemUiModel>> = _invitationsList
    private var childEventsAdded = false

    private lateinit var adapter: InvitationAdapter

    suspend fun findUIDByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            userRepo.findUserByUserName(username)
        }
    }

    fun writeNewInvitation(uid:String, currentUserUid: String, invitation: InvitationUiModel){
        repo.writeNewInvitation(uid, currentUserUid, invitation)
    }

    fun setAdapter(adapter: InvitationAdapter) {
        this.adapter = adapter
    }

    fun updateList(newInvitations: List<ListItemUiModel>) {
        _invitationsList.value = newInvitations
    }

    fun addInvitation(invitation: ListItemUiModel) {
        val updatedInvitations = _invitationsList.value.toMutableList().apply {
            add(invitation)
        }
        _invitationsList.value = updatedInvitations
    }

    val onAccept = object : InvitationAdapter.OnClickListener {
        override fun onItemClick(invitation: InvitationUiModel, currentUser: FirebaseUser) {
            if (invitation.senderUid != null) {
                if (invitation.type == INVITATION_TYPE.FRIEND_REQUEST) {
                    repo.confirmFriendRequestInvitation(currentUser.uid, invitation.senderUid)
                }else{
                    repo.confirmGroupInvitation(currentUser.uid, invitation.senderUid)
                }
            }
        }
    }

    val onDecline = object : InvitationAdapter.OnClickListener {
        override fun onItemClick(invitation: InvitationUiModel, currentUser: FirebaseUser) {
            invitation.senderUid?.let {
                repo.deleteInvitation(currentUser.uid, it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("prueba", "correcto")
                    } else {
                        Log.d("prueba", "mal: ${task.exception?.message}")
                    }
                }
            }
        }
    }

    val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val invitation = snapshot.getValue(InvitationUiModel::class.java)
            invitation?.let { addInvitation(ListItemUiModel.Invitation(it)) }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val invitation = snapshot.getValue(InvitationUiModel::class.java)
            val updatedList = _invitationsList.value.toMutableList().apply {
                removeIf { invi ->
                    require(invi is ListItemUiModel.Invitation) {
                        "Expected Invitation"
                    }
                    invi.invitationUiModel.senderUid == invitation?.senderUid
                }
            }
            updateList(updatedList)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }

    fun loadInvitations(uid: String) {
        if(childEventsAdded) return
        val reference = repo.getInvitationsReference(uid)
        reference.addChildEventListener(childEventListener)
        childEventsAdded = true
    }
}