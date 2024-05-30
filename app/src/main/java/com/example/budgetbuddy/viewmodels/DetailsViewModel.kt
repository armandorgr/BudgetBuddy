package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.repositories.GroupRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel en el cual de define toda la lógica relacionada al fragmento de detálles de grupo
 * @param repo Repositorio de grupos
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 *
 * @author Armando Guzmán
 * */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo: GroupRepository
) : ViewModel() {
    private val _groupData: MutableStateFlow<ListItemUiModel.Group?> = MutableStateFlow(null)
    var groupData: StateFlow<ListItemUiModel.Group?> = _groupData
    private val _isMember: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isMember: StateFlow<Boolean> = _isMember
    private lateinit var listenerReference: ValueEventListener
    private var valueEventAdded: Boolean = false

    /**
     * Objeto anónimo que implementa la interfaz [ValueEventListener] usado para estar al tanto
     * de si el usuario actual sigue siendo miembro o no del grupo
     * */
    private val memberShipEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val memberShipData = snapshot.getValue(ROLE::class.java)
            _isMember.value = memberShipData != null
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled DetailsViewModel")
        }
    }

    /**
     * Objeto anónimo que implementa la interfaz [ValueEventListener] usado para
     * estar pendiente de cualquier cambio de los datos del grupo y asi reflejar dichos
     * cambios en la vista
     * */
    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val group = snapshot.getValue(Group::class.java)
            val key = snapshot.key
            if (key != null && group != null) {
                _groupData.value = ListItemUiModel.Group(key, group)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled DetailsViewModel")
        }
    }

    /**
     * Método que sirve para cargar los datos del grupo seleccionado
     * @param groupUID UID del grupo del cual cargar los datos
     * */
    fun loadGroupData(groupUID: String) {
        if (valueEventAdded) {
            return
        }
        listenerReference = repo.setValueEventListener(groupUID, valueEventListener)
        valueEventAdded = true
    }

    /**
     * Método que sirve para añadir el listener para estar pendiente de si el
     * usuario sigue siendo miembro o no del grupo
     * @param groupUID UID del grupo del cual se quiere saber si el usuario sigue siendo
     * miembro o no
     * @param userUID UID del usuario del cual se estará pendiente de si sigue siendo miembro o no
     * del grupo
     * */
    fun addMemberShipListener(groupUID: String, userUID: String) {
        repo.addMemberShipListener(groupUID, userUID, memberShipEventListener)
    }

}