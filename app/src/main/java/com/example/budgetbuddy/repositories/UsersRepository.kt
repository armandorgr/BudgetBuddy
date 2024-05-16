package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UsersRepository {
    private val usersRef:String = "users"
    private val groupsRef:String = "groups"
    private val friendsRef:String = "friends"
    private var database:DatabaseReference = Firebase.database.reference

    /**
     * Método usado para guardar un usuario registroda en la base de datos
     * @param user Usuario a registrar en la base de datos haciendo uso de su UID
     * */
    fun writeNewUser(uid:String, user:User):Task<Void>{
        return database.child(usersRef).child(uid).setValue(user)
    }

     fun writeNewUser(user: User, uid:String):Task<Void>{
        return database.child(usersRef).child(uid).setValue(user)
    }

    fun deleteProfilePic(uid:String):Task<Void>{
        return database.child(usersRef).child(uid).child("profilePic").setValue(null)
    }

    fun findUIDByUsername(username: String, onComplete: (UID: String?) -> Unit) {
        database.child(usersRef).orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.childrenCount > 0) {
                            snapshot.children.forEach { userData ->
                                onComplete(userData.key)
                            }
                        } else {
                            onComplete(null)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("prueba", "onCancelled InvitationsRepository")
                    }
                }
            )
    }

    suspend fun findUserByUserName(username:String):User?{
       return try{
            val snapshot = database.child(usersRef).orderByChild("username").equalTo(username).get().await()
            val user = snapshot.getValue(User::class.java)
            user
        }catch (e: Exception){
            null
        }
    }

    suspend fun findUserByEmail(email:String):User?{
        return try{
            val snapshot = database.orderByChild("email").equalTo(email).get().await()
            val user = snapshot.getValue(User::class.java)
            user
        }catch (e: Exception){
            null
        }
    }


    suspend fun deleteUser(
        uid: String,
        friends: List<ListItemUiModel>,
        groups: List<ListItemUiModel.Group>
    ):Boolean{
        return try{
            val childUpdates = hashMapOf<String, Any?>(
                "$usersRef/$uid" to null
            )
            for(friend in friends){
                require(friend is ListItemUiModel.User)
                childUpdates["$usersRef/${friend.uid}/$friendsRef/$uid"] = null
            }
            for(group in groups){
                childUpdates["$groupsRef/${group.uid}/members/$uid"] = null
            }
            val task = database.updateChildren(childUpdates)
            task.await()
            task.isSuccessful
        }catch (e: Exception){
            false
        }
    }

     fun updateUsername(uid:String, newUsername:String):Task<Void>{
        return database.child(usersRef).child(uid).child("username").setValue(newUsername)
    }

    fun getUserFriendsListReference(userUid: String):DatabaseReference{
        return database.child(usersRef).child(userUid).child("friends")
    }

    suspend fun findUserByUID(uid:String):User?{
        return try{
            val snapshot = database.child(usersRef).child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            user
        }catch (e: Exception){
            null
        }
    }

    fun findUserByUIDNotSuspend(uid:String): Task<DataSnapshot>{
        return database.child(usersRef).child(uid).get()
    }

    fun setProfilePic(path:String, currentUserUid:String):Task<Void>{
        return database.child(usersRef).child(currentUserUid).child("profilePic").setValue(path)
    }
}