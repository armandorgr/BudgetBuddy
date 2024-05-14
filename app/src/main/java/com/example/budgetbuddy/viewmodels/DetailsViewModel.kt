package com.example.budgetbuddy.viewmodels

import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.repositories.GroupRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo: GroupRepository
) : ViewModel() {
    private val _groupData: MutableStateFlow<ListItemUiModel.Group?> = MutableStateFlow(null)
    var groupData: StateFlow<ListItemUiModel.Group?> = _groupData
    private val _isMember: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isMember:StateFlow<Boolean> = _isMember
    private var valueEventAdded: Boolean = false

    private val memberShipEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val memberShipData = snapshot.getValue(ROLE::class.java)
            _isMember.value = memberShipData!=null
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val group = snapshot.getValue(Group::class.java)
            val key = snapshot.key
            if (key != null && group != null) {
                _groupData.value = ListItemUiModel.Group(key, group)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }

    fun loadGroupData(groupUID: String) {
        if (valueEventAdded) {
            return
        }
        repo.setValueEventListener(groupUID, valueEventListener)
        valueEventAdded = true
    }

    fun addMemberShipListener(groupUID: String, userUID: String){
        repo.addMemberShipListener(groupUID, userUID, memberShipEventListener)
    }
}