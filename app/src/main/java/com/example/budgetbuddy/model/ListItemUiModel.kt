package com.example.budgetbuddy.model


sealed class ListItemUiModel {
    data class Invitation(
        val invitationUiModel: InvitationUiModel
    ) : ListItemUiModel()
    data class User(
        val uid:String,
        val userUiModel: com.example.budgetbuddy.model.User,
        var selected: Boolean = false,
        var role:ROLE? = null,
        var editable:Boolean?= false
    ) : ListItemUiModel()
    data class Group(
        val uid:String,
        val groupUiModel: com.example.budgetbuddy.model.Group
    ) : ListItemUiModel()
    data class CalendarDayUiModel(
        val day: String,
        val hasEvent:Boolean
    ):ListItemUiModel()
    data class MessageUiModel(
        val uid: String,
        val message: Message,
        val senderData: com.example.budgetbuddy.model.User?,
    ) : ListItemUiModel()
}
