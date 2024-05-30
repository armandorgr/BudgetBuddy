package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.adapters.recyclerView.InvitationAdapter
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.repositories.InvitationsRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel en el cual de define la lógica para cargar los datos de las invitacione del usuario actual
 * @param repo Repositorio de invitaciones
 * @param userRepo Repositorio de usuarios
 * @param groupsRepo Repositorio de grupos
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 *  La forma de trabajar con la autenticación de Firebase fue consultada en la documentacion de Firebase: https://firebase.google.com/docs/auth/android/start
 * @author Armando Guzmán
 * */
@HiltViewModel
class InvitationsViewModel @Inject constructor(
    private val repo: InvitationsRepository,
    private val userRepo: UsersRepository,
    private val groupsRepo: GroupRepository
) : ViewModel() {
    private val _invitationsList: MutableStateFlow<List<ListItemUiModel>> =
        MutableStateFlow(emptyList())
    val invitationsList: StateFlow<List<ListItemUiModel>> = _invitationsList
    private var childEventsAdded = false

    /**
     * Método que sirve para actualizar la lista actual de invitaciones cargadas
     * @param newInvitations Lista de invitaciones a guardar
     * */
    fun updateList(newInvitations: List<ListItemUiModel>) {
        _invitationsList.value = newInvitations
    }

    /**
     * Método que sirve para añadir una invitacion a la lista de invitaciones
     * cargadas
     * @param invitation Invitación a añadir
     * */
    fun addInvitation(invitation: ListItemUiModel) {
        val updatedInvitations = _invitationsList.value.toMutableList().apply {
            add(invitation)
        }
        _invitationsList.value = updatedInvitations
    }

    /**
     * Objeto anónimo que implementa la interfaz [InvitationAdapter.OnClickListener]
     * usado para definir que ocurre al aceptar una invitación
     * */
    val onAccept = object : InvitationAdapter.OnClickListener {
        override fun onItemClick(invitation: InvitationUiModel, currentUser: FirebaseUser) {
            if (invitation.senderUid != null) {
                if (invitation.type == INVITATION_TYPE.FRIEND_REQUEST) {
                    userRepo.findUserByUIDNotSuspend(invitation.senderUid).addOnCompleteListener {
                        if (it.result.exists()) {
                            repo.confirmFriendRequestInvitation(
                                currentUser.uid,
                                invitation.senderUid
                            )
                        } else {
                            repo.deleteInvitation(currentUser.uid, invitation.senderUid)
                        }
                    }
                } else {
                    groupsRepo.findGroupByUID(invitation.senderUid).addOnCompleteListener {
                        if (it.result.exists()) {
                            repo.confirmGroupInvitation(currentUser.uid, invitation.senderUid)
                        } else {
                            repo.deleteInvitation(currentUser.uid, invitation.senderUid)
                        }
                    }
                }
            }
        }
    }

    /**
     * Objeto anónimo que implementa la interfaz [InvitationAdapter.OnClickListener]
     * usado para definir que ocurre al rechazar una invitación
     * */
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

    /**
     * Objeto anónimo que implementa la interfaz [ChildEventListener]
     * usado para actualizar la lista de invitaciones cargadas cada vez que añada
     * o elimine una invitación dentro de la base de datos
     * */
    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val invitation = snapshot.getValue(InvitationUiModel::class.java)
            invitation?.let { addInvitation(ListItemUiModel.Invitation(it)) }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "onChildChanged invitations")
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
            Log.d("prueba", "onChildMoved invitations")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled invitations")
        }

    }

    /**
     * Método que sirve para cargar las invitaciones del usuario cuyo UID
     * ha sido pasado por argumento
     * @param uid UID del usuario del cual se quieren cargar las invitaciones
     * */
    fun loadInvitations(uid: String) {
        if (childEventsAdded) return
        val reference = repo.getInvitationsReference(uid)
        reference.addChildEventListener(childEventListener)
        childEventsAdded = true
    }
}