package com.example.budgetbuddy.model


/**
 * Clase que representa a un usuario registrado.
 * */
data class User(
    var pic:String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    var profilePic: String? = null
)
