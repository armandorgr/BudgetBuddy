package com.example.budgetbuddy.repositories

import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ROLE
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Clase repositorio de los invitaciones de la aplicación, esta clase contiene todos los métodos necesarios
 * para realizar operaciones CRUD dentro de la propiedad de los usuarios de 'invitations' de Firebase Realtime Database
 *
 * La forma de trabajar con la base de datos fue consultada en la documentación de Firebase Realtime Database:
 * https://firebase.google.com/docs/database/android/read-and-write?hl=es
 * https://firebase.google.com/docs/database/android/lists-of-data?hl=es
 *
 * @author Armando Guzmán
 * */
class InvitationsRepository {
    private val usersRef: String = "users"
    private val groupsRef: String = "groups"
    private val database: DatabaseReference = Firebase.database.reference

    /**
     * Método que sirve para crear una nueva invitación dentro de la propiedad de 'invitations' de un usuario
     * dentro de la base de datos
     * @param uid UID del usuario al cual se está invitando
     * @param fromUid UID del usuario que está enviando la invitación
     * @param invitation Objeto de la invitación con los datos relativos a esta.
     * */
    fun writeNewInvitation(
        uid: String,
        fromUid: String,
        invitation: InvitationUiModel
    ) {
        database.child(usersRef).child(uid).child("invitations").child(fromUid).setValue(invitation)
    }

    /**
     * Método que sirve para crear una nueva invitación de amistad dentro de la propiedad de 'invitations' de un usuario
     * dentro de la base de datos
     * @param uid UID del usuario al cual se está invitando
     * @param fromUid UID del usuario que está enviando la invitación
     * @param invitation Objeto de la invitación con los datos relativos a esta.
     * @param onComplete Función que se ejecutará al terminar de guardar la invitación en la base de datos
     * */
    fun sendFriendsRequest(
        uid: String,
        fromUid: String,
        invitation: InvitationUiModel,
        onComplete: (task: Task<Void>) -> Unit
    ) {
        database.child(usersRef).child(uid).child("invitations").child(fromUid).setValue(invitation)
            .addOnCompleteListener(onComplete)
    }

    /**
     * Método que sirve para obtener una referencia a la propiedad de 'invitations' de un usuario
     * @param uid UID del usuario al cual se está invitando
     * @return Referencia a la propiedad de 'invitations'
     * */
    fun getInvitationsReference(uid: String): DatabaseReference {
        return database.child(usersRef).child(uid).child("invitations")
    }

    /**
     * Método que sirve para eliminar una invitación dentro de la propiedad de 'invitations' de un
     * usuario
     * @param userUid UID del usuario que ha recibido la invitación que se desea borrar.
     * @param senderUid UID del usuario que ha enviado la invitación que se desea borrar, usado
     * para identificar que invitación borrar.
     * @return La tarea de eliminación de la invitación
     * */
    fun deleteInvitation(userUid: String, senderUid: String): Task<Void> {
        return database.child(usersRef).child(userUid).child("invitations").child(senderUid)
            .removeValue()
    }

    /**
     * Método que sirve aceptar una invitacióm de amistad, lo que implica borrar la invitación y crear
     * una referencia dentro de la propiedad de 'friends' dentro de ambos usuarios.
     * @param currentUserUid UID del usuario actual que acepta la invitación
     * @param invitationSenderUid UID del usuario que ha invitado al usuario actual a ser su amigo.
     * */
    fun confirmFriendRequestInvitation(currentUserUid: String, invitationSenderUid: String) {
        val childUpdate = hashMapOf<String, Any?>(
            "$currentUserUid/friends/$invitationSenderUid" to true,
            "$invitationSenderUid/friends/$currentUserUid" to true,
            "$currentUserUid/invitations/$invitationSenderUid" to null
        )
        database.child(usersRef).updateChildren(childUpdate)
    }

    /**
     * Método que sirve aceptar una invitacióm de unirse a un grupo, lo que implica borrar la invitación y crear
     * una referencia dentro de la propiedad de 'members' dentro del grupo y otra
     * referencia al grupo dentro de la propiedad 'Groups' del usuario.
     * @param currentUserUid UID del usuario actual que acepta la invitación.
     * @param invitationSenderUid UID del grupo al cual se ha invitado el usuario actual a formar parte.
     * */
     fun confirmGroupInvitation(currentUserUid: String, invitationSenderUid: String, onSuccess: (uid: String) -> Unit) {
        val childUpdate = hashMapOf<String, Any?>(
            "$usersRef/$currentUserUid/groups/$invitationSenderUid" to true,
            "$groupsRef/$invitationSenderUid/members/$currentUserUid" to ROLE.MEMBER,
            "$usersRef/$currentUserUid/invitations/$invitationSenderUid" to null
        )
        database.updateChildren(childUpdate).addOnSuccessListener{
            onSuccess(invitationSenderUid)
        }
    }
}