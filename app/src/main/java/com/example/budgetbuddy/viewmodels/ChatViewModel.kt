package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.repositories.GroupRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel(){
    private val _isMember: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isMember:StateFlow<Boolean>  = _isMember
    private var valueEventAdded: Boolean = false

    private val memberShipEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val memberShipData = snapshot.getValue(ROLE::class.java)
            _isMember.value = memberShipData!=null
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled chatViewModel")
        }
    }

    fun addMemberShipListener(groupUID: String, userUID: String){
        if(valueEventAdded) return
        groupRepository.addMemberShipListener(groupUID, userUID, memberShipEventListener)
        valueEventAdded = true
    }
}