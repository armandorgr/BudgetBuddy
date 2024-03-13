package com.example.budgetbuddy.model


sealed class ListItemUiModel {
    data class Invitation(
        val invitationUiModel: InvitationUiModel
    ) : ListItemUiModel()

    data class User(
        val uid:String,
        val userUiModel: com.example.budgetbuddy.model.User,
        var selected: Boolean = false
    ) : ListItemUiModel()

    data class Group(
        val uid:String,
        val groupUiModel: com.example.budgetbuddy.model.Group
    ) : ListItemUiModel()
}
