package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Clase repositorio de los grupos de la aplicación, esta clase contiene todos los métodos necesarios
 * para realizar operaciones CRUD dentro de la colección de 'Groups' de Firebase Realtime Database
 *
 * La forma de trabajar con la base de datos fue consultada en la documentación de Firebase Realtime Database:
 * https://firebase.google.com/docs/database/android/read-and-write?hl=es
 * https://firebase.google.com/docs/database/android/lists-of-data?hl=es
 *
 * @author Armando Guzmán
 * */
class GroupRepository {
    private val usersRef: String = "users"
    private val groupsRef: String = "groups"
    private val invitationsRef: String = "invitations"
    private val balancesRef: String = "balances"
    private val database: DatabaseReference = Firebase.database.reference

    /**
     * Método que sirve para que un usuario deje de pertenecer a un grupo.
     * @param userUid UID del usuario, el cual dejará de formar parte del grupo.
     * @param groupUid UID del grupo del cual el usuario dejará de formar parte.
     * @param onComplete Función que se llamará al terminar la tarea, pasando por argumento
     * el resultado de esta.
     * */
    fun leaveGroup(userUid: String, groupUid: String, onComplete: (task: Task<Void>) -> Unit) {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUid/members/$userUid" to null,
            "$usersRef/$userUid/$groupsRef/$groupUid" to null
        )
        database.updateChildren(childUpdates).addOnCompleteListener(onComplete)
    }

    /**
     * Método que sirve para crear un nuevo grupo, esto no solo implica crear el grupo dentro de la base
     * de datos, sino que también implica invitar a todos aquellos amigos que el usuario que creo
     * el grupo, haya seleccionado.
     * @param group Grupo cuyos datos se guardarán en la base de datos.
     * @param currentUserUid UID del usuario actual.
     * @param members Lista que contiene los UID de los usuarios que se van a invitar
     * @param username Nombre de usuario usado para crear la invitación
     * @param onComplete Función que se llamará al terminar la tarea, pasando por argumento
     * el resultado de esta y el uid del grupo creado.
     * */
    fun createNewGroup(
        group: Group,
        currentUserUid: String,
        members: List<String>,
        username: String,
        onComplete: (task: Task<Void>, uid: String) -> Unit
    ) {
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

        for (i in members.indices) {
            for (j in i + 1 until members.size) {
                val user1 = members[i]
                val user2 = members[j]
                val balance = Balance(
                    user1 = user1,
                    user2 = user2,
                    amountUser1 = 0.0,
                    amountUser2 = 0.0
                )
                val keyBalance = user1 + user2
                val childUpdates = hashMapOf<String, Any?>(
                    "$groupsRef/$key/$balancesRef/$keyBalance/user1" to balance.user1,
                    "$groupsRef/$key/$balancesRef/$keyBalance/user2" to balance.user2,
                    "$groupsRef/$key/$balancesRef/$keyBalance/amountUser1" to balance.amountUser1,
                    "$groupsRef/$key/$balancesRef/$keyBalance/amountUser2" to balance.amountUser2,
                )
                database.updateChildren(childUpdates).addOnCompleteListener {
                    onComplete(it, keyBalance)
                }
            }
        }

        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$key/description" to group.description.toString(),
            "$groupsRef/$key/name" to group.name.toString(),
            "$groupsRef/$key/endDate" to group.endDate.toString(),
            "$groupsRef/$key/startDate" to group.startDate.toString(),
            "$groupsRef/$key/lastUpdated" to ServerValue.TIMESTAMP,
            "$groupsRef/$key/members" to group.members,
            "$groupsRef/$key/pic" to group.pic,
            "$usersRef/$currentUserUid/$groupsRef/$key" to true,
            "$groupsRef/$key/category" to group.category,
        )
        for (member in members) {
            childUpdates["$usersRef/$member/$invitationsRef/$key"] = invitation
        }
        database.updateChildren(childUpdates).addOnCompleteListener {
            onComplete(it, key)
        }
    }

    /**
     * Método que sirve para actualizar un  grupo, esto no solo implica actualizar los datos del grupo dentro de la base
     * de datos, sino que también implica invitar a todos aquellos amigos que el usuario que creo
     * el grupo, haya seleccionado y eliminar todos aquelllos miembros que haya deseleccionados.
     * @param group Grupo cuyos datos se guardarán en la base de datos.
     * @param groupUID UID del grupoo a actualizar.
     * @param membersToDelete Lista que contiene los UID de los usuarios que se van a eliminar.
     * @param friendsToInvite Lista que contiene los UID de los usuarios que se van a invitar.
     * @return La tarea de actualización del grupo.
     * */
    fun updateGroup(
        group: Group,
        groupUID: String,
        membersToDelete: List<String>,
        friendsToInvite: List<String>
    ): Task<Void> {

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
            "$groupsRef/$groupUID/lastUpdated" to ServerValue.TIMESTAMP,
            "$groupsRef/$groupUID/category" to group.category,
        )
        // Se borran los miembros no seleccionados
        for (i in membersToDelete) {
            childUpdates["$groupsRef/$groupUID/members/$i"] = null
            childUpdates["$usersRef/$i/groups/$groupUID"] = null
        }
        // Se invitan los amigos seleccionados
        for (friend in friendsToInvite) {
            childUpdates["$usersRef/$friend/$invitationsRef/$groupUID"] = invitation
        }
        return database.updateChildren(childUpdates)
    }

    /**
     * Método que sirve para eliminar un grupo de la base de datos, también se eliminan
     * las referencias a dicho grupo dentro de cada uno de los miembros.
     * @param groupUID UID del grupo a borrar.
     * @param members Lista de los miembros, usada para eliminar las referencias al grupo.
     * @return La tarea de eliminar el grupo.
     * */
    fun deleteGroup(groupUID: String, members: List<ListItemUiModel.User>): Task<Void> {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID" to null
        )
        for (member in members) {
            childUpdates["$usersRef/${member.uid}/$groupsRef/$groupUID"] = null
        }
        return database.updateChildren(childUpdates)
    }

    /**
     * Método que sirve para añadir un [ChildEventListener] a la propiedad de grupos del usuario actual.
     * @param currentUserUid UID del usuario actual
     * @param childEventListener Objeto de clase que implementa la interfaz [ChildEventListener] cuyos métodos
     * se ejecutarán cada vez que ocurra un cambio en alguno de los hijos de la propiedad de 'Groups' del usuario actual.
     * @return Una referencia al listener añadido
     * */
    fun setGroupChildEvents(
        currentUserUid: String,
        childEventListener: ChildEventListener
    ): ChildEventListener {
        return database.child(usersRef).child(currentUserUid).child(groupsRef)
            .addChildEventListener(childEventListener)
    }

    /**
     * Método que sirve para añadir un [ValueEventListener] a un grupo en específico dentro de la colección de 'Groups'
     * @param groupUID UID del grupo al cual añadir el listener.
     * @param valueEventListener Objeto de clase que implementa la interfaz [ValueEventListener] cuyos métodos
     * se ejecutarán cada vez que ocurra un cambio en alguna propiedad del grupo dentro de la base de datos.
     * @return Una referencia al listener añadido
     * */
    fun setValueEventListener(
        groupUID: String,
        valueEventListener: ValueEventListener
    ): ValueEventListener {
        return database.child(groupsRef).child(groupUID).addValueEventListener(valueEventListener)
    }

    /**
     * Método que sirve para añadir un [ChildEventListener] a la propiedad de miembros de un grupo en específico y así
     * estar pendiente de los cambios ocurrido en estos.
     * @param groupUID UID del grupo al cual añadir el listener.
     * @param childEventListener Objeto de clase que implementa la interfaz [ChildEventListener] cuyos métodos
     * se ejecutarán cada vez que ocurra un cambio en alguno de los miembros del grupo.
     * */
    fun setGroupMembersChildEvents(groupUID: String, childEventListener: ChildEventListener) {
        database.child(groupsRef).child(groupUID).child("members")
            .addChildEventListener(childEventListener)
    }

    /**
     * Método que sirve para encontrar un grupo mediante su UID.
     * @param groupUID UID del grupo a buscar dentro de la base de datos.
     * @return Tarea de encontrar el grupo, esta contiene el Objeto de grupo si fue exítosa.
     * */
    fun findGroupByUID(groupUID: String): Task<DataSnapshot> {
        return database.child(groupsRef).child(groupUID).get()
    }

    /**
     * Método que sirve para cambiar el rol de un miembro de un grupo.
     * @param groupUID UID del grupo sobre el cual se modificará el rol del miembro.
     * @param userUid UID del miembro al cual se le cambiará su rol
     * @param newRole Rol que se le asignará al miembro del grupo
     * @param onComplete Función que se llamará al completarse la tarea de modificar el rol del miembro,
     * pasando por argumento dicha tarea.
     * */
    fun changeMemberRole(
        groupUID: String,
        userUid: String,
        newRole: ROLE,
        onComplete: (task: Task<Void>) -> Unit
    ) {
        database.child(groupsRef).child(groupUID).child("members").child(userUid).setValue(newRole)
            .addOnCompleteListener(onComplete)
    }

    /**
     * Método que sirve para añadir un listener al UID de un miembro de un grupo, y de esta manera saber si este sigue siendo miembro de este o no.
     * @param groupUID UID del grupos sobre el cual se obtendrán sus miembros.
     * @param userUid UID del miembro sobre el cual se añadirá el listener para saber si sigue siendo miembro o no del grupo.
     * @param valueEventListener Listener a añadir
     * */
    fun addMemberShipListener(
        groupUID: String,
        userUid: String,
        valueEventListener: ValueEventListener
    ) {
        database.child(groupsRef).child(groupUID).child("members").child(userUid)
            .addValueEventListener(valueEventListener)
    }

}