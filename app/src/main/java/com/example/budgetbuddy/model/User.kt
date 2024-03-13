package com.example.budgetbuddy.model


/**
 * Clase que representa a un usuario registrado.
 * */
data class User(
    var uid: String? = null,
    val firstName:String? = null,
    val lastName:String? = null,
    val username:String? = null
)
