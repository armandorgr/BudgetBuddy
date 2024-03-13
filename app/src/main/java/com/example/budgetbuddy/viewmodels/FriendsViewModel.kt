package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {
    private val _friendsUidList = MutableStateFlow<List<ListItemUiModel>>(emptyList())
    private var childEventsAdded: Boolean = false;
    val friendsUidList: StateFlow<List<ListItemUiModel>> = _friendsUidList

    fun updateList(newFriends: List<ListItemUiModel>) {
        _friendsUidList.value = newFriends
    }

    fun resetChecked() {
        for (friend in _friendsUidList.value) {
            (friend as ListItemUiModel.User).selected = false
        }
    }

    fun addFriend(friend: User) {
        val updatedFriends = _friendsUidList.value.toMutableList().apply {
            add(ListItemUiModel.User(friend))
        }
        _friendsUidList.value = updatedFriends
    }

    private fun removeFriend(uid: String) {
        val updatedList = _friendsUidList.value.toMutableList().apply {
            removeIf { friend -> (friend as ListItemUiModel.User).userUiModel.uid == uid }
        }
        updateList(updatedList)
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newFriends = mutableListOf<User>()
            for (friend in snapshot.children) {
                if (friend.key != null) {
                    repo.findUserByUIDNotSuspend(friend.key!!).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val _friend = it.result.getValue(User::class.java)
                            _friend?.uid = friend.key
                            if (_friend != null) {
                                addFriend(_friend)
                            }
                        } else {
                            Log.d("prueba", "Error cargando amigo: ${it.exception?.message}")
                        }
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "loadPost:onCancelled ${error.toException()}")
        }
    }

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "onChildAdded")
            val key = snapshot.key
            key?.let {
                repo.findUserByUIDNotSuspend(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val _friend = task.result.getValue(User::class.java)
                        _friend?.uid = it
                        if (_friend != null) {
                            addFriend(_friend)
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

    fun loadFriends(currentUserUid: String) {
        if (childEventsAdded) return
        val reference = repo.getUserFriendsListReference(currentUserUid)
        reference.addChildEventListener(childEventListener)
        childEventsAdded = true
    }
}