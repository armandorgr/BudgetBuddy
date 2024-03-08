package com.example.budgetbuddy.model

import java.time.LocalDateTime

sealed class ListItemUiModel {
    data class Invitation(
        val invitationUiModel: InvitationUiModel
    ) : ListItemUiModel()
}
