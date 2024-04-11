package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.repositories.GroupRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repo: GroupRepository
) : ViewModel() {
    private val _groupsList: MutableStateFlow<List<ListItemUiModel>> =
        MutableStateFlow(emptyList())
    val groupList: StateFlow<List<ListItemUiModel>> = _groupsList
    private var childEventsAdded: Boolean =
        false // Se usa esta variable para que no se carguen los grupos mas de una vez


    private fun updateList(newGroups: List<ListItemUiModel>) {
        _groupsList.value = newGroups
    }

    private fun addGroup(uid: String, group: Group) {
        val updatedList = _groupsList.value.toMutableList().apply {
            add(ListItemUiModel.Group(uid, group))
        }
        updateList(updatedList)
    }

    private fun removeGroup(groupUID: String) {
        val updatedList = _groupsList.value.toMutableList().apply {
            removeIf { group -> (group as ListItemUiModel.Group).uid == groupUID }
        }
        updateList(updatedList)
    }


    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val key = snapshot.key
            key?.let {
                repo.findGroupByUID(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val group = task.result.getValue(Group::class.java)
                        if (group != null) {
                            addGroup(it, group)
                            Log.d("prueba", "Grupo cargado: ${group.name}")
                        }
                    } else {
                        Log.d("prueba", "Error cargando grupo: ${task.exception?.message}")
                    }
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "OnGroupChaged, previousChildName: $previousChildName")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            snapshot.key?.let { removeGroup(it) }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "OnGroupMoved, previousChildName: $previousChildName")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "OnGroupCancelled, previousChildName: ${error.message}")
        }

    }

    fun loadGroups(currentUserUID: String) {
        if (childEventsAdded) return
        repo.setGroupChildEvents(currentUserUID, childEventListener)
        childEventsAdded = true
    }

}