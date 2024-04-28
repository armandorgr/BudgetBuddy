package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.ROLE
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime

class GroupRepository {
    private val usersRef: String = "users"
    private val groupsRef: String = "groups"
    private val invitationsRef: String = "invitations"
    private val database: DatabaseReference = Firebase.database.reference

    fun leaveGroup(userUid:String, groupUid:String, onComplete: (task: Task<Void>) -> Unit){
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUid/members/$userUid" to null,
            "$usersRef/$userUid/$groupsRef/$groupUid" to null
        )
        database.updateChildren(childUpdates).addOnCompleteListener(onComplete)
    }

    fun createNewGroup(group: Group, currentUserUid: String, members:List<String>, username:String ,onComplete:(task:Task<Void>, uid:String)->Unit){
        val key = database.child(groupsRef).push().key
        if (key == null) {
            Log.w("prueba", "Couldn't get push key for posts")
            return
        }
        group.pic += key

        val invitation = InvitationUiModel(
            group.pic,
            key,
            username,
            INVITATION_TYPE.GROUP_REQUEST,
            ServerValue.TIMESTAMP
            )

        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$key/description" to group.description.toString(),
            "$groupsRef/$key/name" to group.name.toString(),
            "$groupsRef/$key/endDate" to group.endDate.toString(),
            "$groupsRef/$key/startDate" to group.startDate.toString(),
            "$groupsRef/$key/lastUpdated" to ServerValue.TIMESTAMP,
            "$groupsRef/$key/members" to group.members,
            "$groupsRef/$key/pic" to group.pic,
            "$usersRef/$currentUserUid/$groupsRef/$key" to true
        )
        for(member in members){
            childUpdates["$usersRef/$member/$invitationsRef/$key"] = invitation
        }
        database.updateChildren(childUpdates).addOnCompleteListener {
            onComplete(it, key)
        }
    }

    fun setGroupPic(path:String, uid:String){
        database.child(groupsRef).child(uid).child("pic").setValue(path)
    }

    fun updateGroup(group: Group, groupUID: String, membersToDelete:List<String>, friendsToInvite:List<String>): Task<Void> {

        val invitation = InvitationUiModel(
            group.pic,
            groupUID,
            group.name,
            INVITATION_TYPE.GROUP_REQUEST,
            ServerValue.TIMESTAMP
        )

        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID/description" to group.description.toString(),
            "$groupsRef/$groupUID/name" to group.name.toString(),
            "$groupsRef/$groupUID/endDate" to group.endDate.toString(),
            "$groupsRef/$groupUID/startDate" to group.startDate.toString(),
            "$groupsRef/$groupUID/pic" to group.pic,
            "$groupsRef/$groupUID/lastUpdated" to ServerValue.TIMESTAMP
        )
        // Se borran los miembros no seleccionados
        for (i in membersToDelete){
            childUpdates["$groupsRef/$groupUID/members/$i"] = null
            childUpdates["$usersRef/$i/groups/$groupUID"] = null
        }
        // Se invitan los amigos seleccionados
        for(friend in friendsToInvite){
            childUpdates["$usersRef/$friend/$invitationsRef/$groupUID"] = invitation
        }
        return database.updateChildren(childUpdates)
    }

    fun deleteGroup(groupUID: String, members: List<ListItemUiModel.User>): Task<Void> {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID" to null
        )
        for (member in members) {
            childUpdates["$usersRef/${member.uid}/$groupsRef/$groupUID"] = null
        }
        return database.updateChildren(childUpdates)
    }

    fun setGroupChildEvents(currentUserUid: String, childEventListener: ChildEventListener):ChildEventListener {
        return database.child(usersRef).child(currentUserUid).child(groupsRef)
            .addChildEventListener(childEventListener)
    }

    fun removeChildEvents(currentUserUid: String, childEventListener: ChildEventListener){
        database.child(usersRef).child(currentUserUid).child(groupsRef).removeEventListener(childEventListener)
    }

    fun setValueEventListener(groupUID: String, valueEventListener: ValueEventListener){
        database.child(groupsRef).child(groupUID).addValueEventListener(valueEventListener);
    }

    fun setGroupMembersChildEvents(groupUID: String, childEventListener: ChildEventListener) {
        database.child(groupsRef).child(groupUID).child("members")
            .addChildEventListener(childEventListener)
    }

    fun findGroupByUID(groupUID: String): Task<DataSnapshot> {
        return database.child(groupsRef).child(groupUID).get()
    }

    fun changeMemberRole(groupUID: String, userUid: String, newRole:ROLE, onComplete: (task: Task<Void>) -> Unit){
        database.child(groupsRef).child(groupUID).child("members").child(userUid).setValue(newRole).addOnCompleteListener(onComplete)
    }

    fun addMemberShipListener(groupUID: String, userUid: String, valueEventListener: ValueEventListener){
        database.child(groupsRef).child(groupUID).child("members").child(userUid).addValueEventListener(valueEventListener)
    }
}