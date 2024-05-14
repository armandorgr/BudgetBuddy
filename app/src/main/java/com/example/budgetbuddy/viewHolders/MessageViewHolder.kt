package com.example.budgetbuddy.viewHolders

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.MESSAGE_TYPE
import com.example.budgetbuddy.model.Message
import com.example.budgetbuddy.util.ListItemImageLoader
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MessageViewHolder(
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val context: Context,
    private val onImageClick: OnImageClick
) : ListItemViewHolder(containerView) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val profilePicView: ImageView by lazy {
        containerView.findViewById(R.id.senderPicImageView)
    }
    private val usernameTextView: TextView by lazy {
        containerView.findViewById(R.id.senderUsernameView)
    }
    private val messageTextView: TextView by lazy {
        containerView.findViewById(R.id.messageTextView)
    }
    private val pictureImageView: ImageView by lazy {
        containerView.findViewById(R.id.imageMessageView)
    }
    private val dateSentTextView: TextView by lazy {
        containerView.findViewById(R.id.dateSentTextView)
    }

    private fun getDateSent(dateSent: Any?): LocalDateTime {
        var dateResult: LocalDateTime = LocalDateTime.now()
        dateSent?.let {
            require(it is Long)
            dateResult = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        return dateResult
    }
    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.MessageUiModel){
            "Expected ListItemUiModel.MessageUiModel got $listItem"
        }
        val messageData = listItem.message
        val senderData = listItem.senderData
        showAndHideContent(messageData)


        if(senderData!=null){
            usernameTextView.text = senderData.username
            senderData.profilePic?.let { imageLoader.loadImage(it, profilePicView) }
        }else{
            usernameTextView.text = context.getString(R.string.account_deleted_message)
        }
        dateSentTextView.text = dateFormatter.format(getDateSent(messageData.sentDate))
    }

    private fun showAndHideContent(messageData: Message){
        if(messageData.type == MESSAGE_TYPE.TEXT){
            messageTextView.visibility = View.VISIBLE
            messageTextView.text = messageData.text
            pictureImageView.visibility = View.GONE
        }else{
            pictureImageView.visibility = View.VISIBLE
            messageTextView.visibility = View.GONE
            imageLoader.loadImage(messageData.imgPath!!, pictureImageView)
            pictureImageView.setOnClickListener{
                onImageClick.onClick(messageData.imgPath)
            }
        }
    }

    interface OnImageClick{
        fun onClick(imgPath:String)
    }
}