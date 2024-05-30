package com.example.budgetbuddy.model

/**
 * Clase que encapsula clases de datos usadas para representar las entidades de la aplicación
 * en las vistas, por eso estas clases añaden atributos que  son necesarios para su representación
 * visual, pero no son requeridos siempre en la aplicación.
 *
 * @author Armando Guzmán
 * */
sealed class ListItemUiModel {
    /**
     * Clase que encapsula la clase modelo [InvitationUiModel], añadiendo cualquier atributo
     * que sea necesario para su representación visual e interacción en la vista.
     * @param invitationUiModel Invitación a encapsular.
     * @author Armando Guzmán
     * */
    data class Invitation(
        val invitationUiModel: InvitationUiModel
    ) : ListItemUiModel()
    /**
     * Clase que encapsula la clase modelo [User], añadiendo cualquier atributo
     * que sea necesario para su representación visual e interacción en la vista.
     * @param uid UID del usuario obtenido de la base de datos
     * @param userUiModel Usuario a encapsular.
     * @param selected Valor que sirve para saber si el usuario fue seleccionado o no.
     * @param role Role que ocupa el usuario dentro de un grupo.
     * @param editable Valor que sirve para saber si el usuario puede ser modificado o no.
     * @author Armando Guzmán
     * */
    data class User(
        val uid:String,
        val userUiModel: com.example.budgetbuddy.model.User,
        var selected: Boolean = false,
        var role:ROLE? = null,
        var editable:Boolean?= false
    ) : ListItemUiModel()
    /**
     * Clase que encapsula la clase modelo [Group], añadiendo cualquier atributo
     * que sea necesario para su representación visual e interacción en la vista.
     * @param uid UID del grupo obtenido de la base de datos.
     * @param groupUiModel Grupo a encapsular.
     * @author Armando Guzmán
     * */
    data class Group(
        val uid:String,
        val groupUiModel: com.example.budgetbuddy.model.Group
    ) : ListItemUiModel()
    /**
     * Clase que sirve para encapsular los atributos necesarios para representar un día dentro del calendario
     * @param day Valor del día del mes a representar.
     * @param hasEvent Valor que representa si en este día coincide algún grupo o no.
     * @author Armando Guzmán
     * */
    data class CalendarDayUiModel(
        val day: String,
        val hasEvent:Boolean
    ):ListItemUiModel()
    /**
     * Clase que encapsula la clase modelo [Message], añadiendo cualquier atributo
     * que sea necesario para su representación visual e interacción en la vista.
     * @param uid UID del mensaje obtenido de la base de datos.
     * @param message Mensaje a encapsular.
     * @param senderData Datos del usuario que ha enviado el mensaje, si no existen datos, estos
     * serán nulos
     * @author Armando Guzmán
     * */
    data class MessageUiModel(
        val uid: String,
        val message: Message,
        val senderData: com.example.budgetbuddy.model.User?,
    ) : ListItemUiModel()
    /**
     * Clase que encapsula la clase modelo [GROUP_CATEGORY], añadiendo cualquier atributo
     * que sea necesario para su representación visual e interacción en la vista.
     * @param category Categoría a encapsular.
     * @param isSelected Valor que indica si la categoría ha sido seleccionada o no.
     * @author Armando Guzmán
     * */
    data class CategoryUiModel(
        val category: GROUP_CATEGORY,
        var isSelected: Boolean
    ) : ListItemUiModel()
}
