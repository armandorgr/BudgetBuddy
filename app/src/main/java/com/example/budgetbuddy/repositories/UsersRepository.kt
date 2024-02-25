package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

    fun findUserByUserName(username:String):Task<DataSnapshot>{
        return database.orderByChild("username").equalTo(username).get()
    }
}