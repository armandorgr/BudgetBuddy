package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UsersRepository {
    private val ref:String = "users"
    private var database:DatabaseReference = Firebase.database.getReference(ref)

    /**
     * Método usado para guardar un usuario registroda en la base de datos
     * @param user Usuario a registrar en la base de datos haciendo uso de su UID
     * */
    fun writeNewUser(uid:String, user:User):Task<Void>{
        return database.child(uid).setValue(user)
    }

     fun writeNewUser(user: User, uid:String):Task<Void>{
        return database.child(uid).setValue(user)
    }

    suspend fun findUserByUserName(username:String):User?{
       return try{
            val snapshot = database.orderByChild("username").equalTo(username).get().await()
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

    suspend fun updateEmail(uid:String, newEmail:String):Boolean{
        return try{
            val task = database.child(uid).child("email").setValue(newEmail)
            task.await()
            task.isSuccessful
        }catch (e: Exception){
            false
        }
    }

    suspend fun findUserByUID(uid:String):User?{
        return try{
            val snapshot = database.child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            user
        }catch (e: Exception){
            null
        }
    }
}