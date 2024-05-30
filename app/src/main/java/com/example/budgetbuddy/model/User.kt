package com.example.budgetbuddy.model


/**
 * Clase que representa a un usuario.
 * @param firstName Nombres del usuario
 * @param lastName Apellidos del usuario
 * @param username Nombre de usuario.
 * @param profilePic Ruta de la foto de perfil de usuario, si este no tiene, este será nulo.
 * */
data class User(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    var profilePic: String? = null
)
