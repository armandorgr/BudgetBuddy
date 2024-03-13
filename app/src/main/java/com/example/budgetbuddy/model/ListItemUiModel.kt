package com.example.budgetbuddy.model

import java.time.LocalDateTime

sealed class ListItemUiModel {
    data class Invitation(
        val invitationUiModel: InvitationUiModel
    ) : ListItemUiModel()

    data class User(
        val userUiModel: com.example.budgetbuddy.model.User,
        var selected:Boolean = false
    ) : ListItemUiModel()
}
