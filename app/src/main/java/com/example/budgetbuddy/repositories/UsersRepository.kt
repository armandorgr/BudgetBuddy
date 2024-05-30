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

/**
 * Clase repositorio de los usuarios de la aplicación, esta clase contiene todos los métodos necesarios
 * para realizar operaciones CRUD dentro de la colección de 'users' de Firebase Realtime Database
 *
 * La forma de trabajar con la base de datos fue consultada en la documentación de Firebase Realtime Database:
 * https://firebase.google.com/docs/database/android/read-and-write?hl=es
 * https://firebase.google.com/docs/database/android/lists-of-data?hl=es
 *
 * @author Armando Guzmán
 * */
class UsersRepository {
    private val usersRef: String = "users"
    private val groupsRef: String = "groups"
    private val friendsRef: String = "friends"
    private var database: DatabaseReference = Firebase.database.reference

    /**
     * Método usado para guardar un usuario registroda en la base de datos
     * @param uid UID con el cual se identificará univocamente al usuario dentro la base de datos
     * @param user Usuario a registrar en la base de datos haciendo uso de su UID
     * @return La tarea de guardar el usuario en la base de datos.
     * */
    fun writeNewUser(uid: String, user: User): Task<Void> {
        return database.child(usersRef).child(uid).setValue(user)
    }

    /**
     * Método que sirve para eliminar la foto de perfil de un usuario
     * @param uid UID del usuario cuya foto de perfil será eliminada
     * @return La tarea de eliminar la foto de perfil
     * */
    fun deleteProfilePic(uid: String): Task<Void> {
        return database.child(usersRef).child(uid).child("profilePic").setValue(null)
    }

    /**
     * Método que sirve para encontrar el UID de un usuario proporcionando su nombre de usuario
     * El UID encontrado, se pasará por argumento a la función de [onComplete]
     * @param username Nommbre de usuario usado para encontrar el UID
     * @param onComplete Función que se llamará pasando por argumento el UID encontrado, el
     * cual puede ser nulo si no existe el usuario.
     * */
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

    /**
     * Método que sirve para encontrar un usuario proporcionando su nombre de usuario
     * @param username Nombre de usuario usado, para encontrar el usuario dentro de la base de datos
     * @return El usuario encontrado, será nulo si no existe
     * */
    suspend fun findUserByUserName(username: String): User? {
        return try {
            val snapshot =
                database.child(usersRef).orderByChild("username").equalTo(username).get().await()
            val user = snapshot.getValue(User::class.java)
            user
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Método que sirve para eliminar un usuario
     * @param uid UID del usuario a eliminar
     * @param friends Lista con los amigos del usuario, usada para eliminar de estos la referencia al usuario eliminado
     * @param groups Lista con los grupos a los que pertenece el usuario, usada para eliminar la referencia de miembro
     * del usuario eliminado
     * @return Valor que indica si se ha borrado el usuario o no
     * */
    suspend fun deleteUser(
        uid: String,
        friends: List<ListItemUiModel>,
        groups: List<ListItemUiModel.Group>
    ): Boolean {
        return try {
            val childUpdates = hashMapOf<String, Any?>(
                "$usersRef/$uid" to null
            )
            for (friend in friends) {
                require(friend is ListItemUiModel.User)
                childUpdates["$usersRef/${friend.uid}/$friendsRef/$uid"] = null
            }
            for (group in groups) {
                childUpdates["$groupsRef/${group.uid}/members/$uid"] = null
            }
            val task = database.updateChildren(childUpdates)
            task.await()
            task.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Método que sirve para actualizar el nombre de usuario de un usuario dentro de la base de datos
     * @param uid UID del usuario a actualizar el nombre de usuario
     * @param newUsername Nombre de usuario nuevo
     * @return Tarea de cambiar el nombre de usuario
     * */
    fun updateUsername(uid: String, newUsername: String): Task<Void> {
        return database.child(usersRef).child(uid).child("username").setValue(newUsername)
    }

    /**
     * Método que sirve par obtener una referencia a la propiedad de 'friends' de un usuario
     * @param userUid UID del usuario del cual obtener la referencia de su propiedad 'friends'
     * @return Referencia a la propiedad de friends del usuario
     * */
    fun getUserFriendsListReference(userUid: String): DatabaseReference {
        return database.child(usersRef).child(userUid).child("friends")
    }

    /**
     * Método que sirve para obtener un usuario proporcionando su UID
     * @param uid UID del usuario usado para encontralo dentro de la base de datos
     * @return El usuario, si este no existe el valor devuelto será nulo.
     * */
    suspend fun findUserByUID(uid: String): User? {
        return try {
            val snapshot = database.child(usersRef).child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            user
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Método que sirve para obtener un usuario de manera síncrona proporcionando su UID
     * @param uid UID del usuario usado para encontralo dentro de la base de datos
     * @return La tarea de encontrar el usuario
     * */
    fun findUserByUIDNotSuspend(uid: String): Task<DataSnapshot> {
        return database.child(usersRef).child(uid).get()
    }

    /**
     * Método que sirve para establecer la foto de perfil de un usuario
     * @param path Ruta de la imagen dentro de Firebase Storage
     * @param currentUserUid UID del usuario actual, en el cual se establecerá la foto de perfil
     * @return La tarea de establecer la foto de perfil
     * */
    fun setProfilePic(path: String, currentUserUid: String): Task<Void> {
        return database.child(usersRef).child(currentUserUid).child("profilePic").setValue(path)
    }
}