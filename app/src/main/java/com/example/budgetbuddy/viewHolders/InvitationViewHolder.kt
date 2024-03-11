package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class InvitationViewHolder(
    private val containerView: View,
    private val onAcceptListener: OnClickListener,
    private val onDeclineListener: OnClickListener
) : ListItemViewHolder(containerView) {

    private val textView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val agoView: TextView by lazy {
        containerView.findViewById(R.id.item_ago_view)
    }
    private val acceptBtn: Button by lazy {
        containerView.findViewById(R.id.acceptBtn)
    }
    private val declineBtn: Button by lazy {
        containerView.findViewById(R.id.declineBtn)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Invitation) {
            "Expected ListItemUiModel.Invitation $listItem"
        }
        val invitationData = listItem.invitationUiModel

        acceptBtn.setOnClickListener {
            onAcceptListener.onClick(invitationData, adapterPosition)
        }
        declineBtn.setOnClickListener {
            onDeclineListener.onClick(invitationData, adapterPosition)
        }

        textView.text =
            invitationData.text?.let { String.format(it, invitationData.senderUsername) }
        agoView.text = invitationData.dateSent?.let { getTimeAgo((LocalDateTime.parse(it))) }
    }

    fun getTimeAgo(date: LocalDateTime): String {
        val secondsDifference = ChronoUnit.SECONDS.between(date, LocalDateTime.now())
        val hoursDifference = ChronoUnit.HOURS.between(date, LocalDateTime.now())
        val minutesDifference = ChronoUnit.MINUTES.between(date, LocalDateTime.now())
        val daysDifference = ChronoUnit.DAYS.between(date, LocalDateTime.now())
        val monthsDifference = ChronoUnit.MONTHS.between(date, LocalDateTime.now())

        return if (monthsDifference > 0) {
            "$monthsDifference month/s ago"
        } else if (daysDifference > 0) {
            "$daysDifference days ago"
        } else if (hoursDifference > 0) {
            "$hoursDifference hours ago"
        } else if (minutesDifference > 0) {
            "$minutesDifference minutes ago"
        } else {
            "${secondsDifference}seconds ago"
        }
    }

    interface OnClickListener {
        fun onClick(invitation: InvitationUiModel, position: Int)
    }
}