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

/**
 * ViewModel en el cual de define toda la lógica relacionada al fragmento de grupos
 * @param repo Repositorio de grupos
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 *
 * @author Armando Guzmán
 * */
@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repo: GroupRepository
) : ViewModel() {
    private val _groupsList: MutableStateFlow<List<ListItemUiModel.Group>> =
        MutableStateFlow(emptyList())
    val groupList: StateFlow<List<ListItemUiModel.Group>> = _groupsList
    private var listenerReference: ChildEventListener? = null
    private var childEventsAdded: Boolean =
        false // Se usa esta variable para que no se carguen los grupos mas de una vez

    /**
     * Método que sirve para actualizar la lista de grupos cargados
     * @param newGroups Lista con los nuevos grupos a guardar
     * */
    private fun updateList(newGroups: List<ListItemUiModel.Group>) {
        _groupsList.value = newGroups
    }

    /**
     * Método que sirve para añadir un grupo a la lista de grupos cargados
     * @param uid UID del grupo
     * @param group Grupo a añadir a la lista
     * */
    private fun addGroup(uid: String, group: Group) {
        val updatedList = _groupsList.value.toMutableList().apply {
            add(ListItemUiModel.Group(uid, group))
        }
        updateList(updatedList)
    }

    /**
     * Método que sirve para eliminar un grupo de la lista de grupos cargados
     * @param groupUID UID del grupo a eliminar
     * */
    private fun removeGroup(groupUID: String) {
        val updatedList = _groupsList.value.toMutableList().apply {
            removeIf { group -> group.uid == groupUID }
        }
        updateList(updatedList)
    }

    /**
     * Objeto anónimo que implementa la interfaz [ChildEventListener] usado para actualizar la lista
     * de grupos cargados en caso de que se añadan o se eliminen grupos dentro de la base de datos
     * */
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

    /**
     * Método que sirve para cargar los grupos a los que pertenece el usuario actual
     * @param currentUserUID UID del usuario actual
     * */
    fun loadGroups(currentUserUID: String) {
        if (childEventsAdded) return
        listenerReference = repo.setGroupChildEvents(currentUserUID, childEventListener)
        childEventsAdded = true
    }
}