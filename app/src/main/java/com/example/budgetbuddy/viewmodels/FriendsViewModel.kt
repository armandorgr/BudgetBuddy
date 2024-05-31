package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.InvitationsRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel para manejar las operaciones relacionadas con los amigos en la aplicación.
 * Se encarga de cargar la lista de amigos de un usuario y enviar invitaciones de amistad.
 *
 * @author Álvaro Aparicio
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val invitationRepo: InvitationsRepository
) : ViewModel() {
    // MutableStateFlow para contener la lista de amigos
    private val _friendsUidList:MutableStateFlow<List<ListItemUiModel>> = MutableStateFlow<List<ListItemUiModel>>(emptyList())
    val friendsUidList: StateFlow<List<ListItemUiModel>> = _friendsUidList
    private var childEventsAdded: Boolean = false;

    /**
     * Método para agregar un amigo a la lista de amigos.
     * @param uid El ID del amigo que se agregará.
     * @param friend El objeto User que representa al amigo.
     */
    private fun updateList(newFriends: List<ListItemUiModel>) {
        _friendsUidList.value = newFriends
    }

    /**
     * Método para agregar un amigo a la lista de amigos.
     * @param uid El ID del amigo que se agregará.
     * @param friend El objeto User que representa al amigo.
     */
    fun addFriend(uid:String, friend: User) {
        val updatedFriends = _friendsUidList.value.toMutableList().apply {
            add(ListItemUiModel.User(uid, friend))
        }
        _friendsUidList.value = updatedFriends
    }

    /**
     * Método para eliminar un amigo a la lista de amigos.
     * @param uid El ID del amigo que se eliminará.
     */
    private fun removeFriend(uid: String) {
        val updatedList = _friendsUidList.value.toMutableList().apply {
            removeIf { friend -> (friend as ListItemUiModel.User).uid == uid }
        }
        updateList(updatedList)
    }

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "onChildAdded")
            val key = snapshot.key
            key?.let {
                repo.findUserByUIDNotSuspend(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val _friend = task.result.getValue(User::class.java)
                        if (_friend != null) {
                            addFriend(key, _friend)
                        }
                    } else {
                        Log.d("prueba", "Error cargando amigo: ${task.exception?.message}")
                    }
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            snapshot.key?.let { removeFriend(it) }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }


    /**
     * Método para cargar la lista de amigos de un usuario.
     * @param currentUserUid El ID del usuario cuya lista de amigos se cargará.
     */
    fun loadFriends(currentUserUid: String) {
        if (childEventsAdded) return
        val reference = repo.getUserFriendsListReference(currentUserUid)
        reference.addChildEventListener(childEventListener)
        childEventsAdded = true
    }


    /**
     * Método para enviar una invitación de amistad a otro usuario.
     * @param invitation La información de la invitación.
     * @param toUID El ID del usuario al que se enviará la invitación.
     * @param funcion La función que se llamará después de enviar la invitación.
     */
    fun sendInvitation(invitation: InvitationUiModel, toUID: String ,funcion:(task: Task<Void>)->Unit){
        invitationRepo.sendFriendsRequest(toUID, invitation.senderUid!!, invitation, funcion)
    }

}