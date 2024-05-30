package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Message
import com.example.budgetbuddy.util.Utilities
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Clase repositorio de los mensajes de la aplicación, esta clase contiene todos los métodos necesarios
 * para realizar operaciones CRUD dentro de la propiedad de los grupos de 'messages' de Firebase Realtime Database
 *
 * La forma de trabajar con la base de datos fue consultada en la documentación de Firebase Realtime Database:
 * https://firebase.google.com/docs/database/android/read-and-write?hl=es
 * https://firebase.google.com/docs/database/android/lists-of-data?hl=es
 *
 * @author Armando Guzmán
 * */
class MessageRepository {
    private val groupsRef: String = "groups"
    private val messagesRef: String = "messages"
    private val database: DatabaseReference = Firebase.database.reference

    /**
     * Método que sirve para guardar un mensaje dentro de la propiedad de 'messages' de un grupo
     * @param groupUID UID del grupo en la cual se guardará el mensaje dentro de su propiedad 'messages'
     * @param message Mensaje a guardar
     * */
    fun writeNewMessage(groupUID: String, message: Message) {
        val key = database.child(groupsRef).child(groupUID).child(messagesRef).push().key
        if (key == null) {
            Log.w("prueba", "Couldn't get push key for messages")
            return
        }
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID/$messagesRef/$key/text" to message.text,
            "$groupsRef/$groupUID/$messagesRef/$key/senderUID" to message.senderUID,
            "$groupsRef/$groupUID/$messagesRef/$key/sentDate" to ServerValue.TIMESTAMP,
            "$groupsRef/$groupUID/$messagesRef/$key/type" to message.type,
            "$groupsRef/$groupUID/$messagesRef/$key/imgPath" to (Utilities.PROFILE_PIC_ST + key),
        )
        database.updateChildren(childUpdates)
    }

    /**
     * Método que sirve para obtener un nuevo UID único disponible dentro de la propiedad de 'messages'
     * de un grupo
     * @param groupUID UID del grupo usado para obtener un UID único para un nuevo mensaje
     * @return El nuevo UID único
     * */
    fun getNewKey(groupUID: String): String? {
        return database.child(groupsRef).child(groupUID).child(messagesRef).push().key
    }

    /**
     * Método que sirve para escribir un nuevo mensaje dentro de la propiedad de 'messages' de un grupo con una
     * imagen adjunta
     * @param groupUID UID del grupo en la cual se guardará el mensaje dentro de su propiedad 'messages'
     * @param key UID del mensaje usado para guardar la imagen con dicho UID como nombre
     * @param message Mensaje a guardar
     * @return Tarea de subir el mensaje
     * */
    fun writeNewImageMessage(groupUID: String, key: String, message: Message): Task<Void> {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID/$messagesRef/$key/text" to message.text,
            "$groupsRef/$groupUID/$messagesRef/$key/senderUID" to message.senderUID,
            "$groupsRef/$groupUID/$messagesRef/$key/sentDate" to ServerValue.TIMESTAMP,
            "$groupsRef/$groupUID/$messagesRef/$key/type" to message.type,
            "$groupsRef/$groupUID/$messagesRef/$key/imgPath" to (Utilities.PROFILE_PIC_ST + "images/" + key),
        )
        return database.updateChildren(childUpdates)
    }

    /**
     * Método que sirve añadir un [ChildEventListener] a la propiedad de 'messages' de un grupo
     * @param groupUID UID del grupo sobre cuya propiedad 'messages' se añadirá el listener
     * @param childEventListener Listener a añadir
     * */
    fun addChildEventListener(groupUID: String, childEventListener: ChildEventListener) {
        database.child(groupsRef).child(groupUID).child("messages")
            .addChildEventListener(childEventListener)
    }
}