package com.example.budgetbuddy.viewHolders

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader

class NewGroupFriendViewHolder(
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val onCheckClickListener: OnCheckClickListener,
    private val onUnCheckClickListener: OnCheckClickListener
) : ListItemViewHolder(containerView) {

    private val userNameView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val checkBox: CheckBox by lazy {
        containerView.findViewById(R.id.checkbox)
    }
    private val profilePic: ImageView by lazy {
        containerView.findViewById(R.id.item_img_view)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.User) {
            "Expected ListItemUiModel.User $listItem"
        }
        val userData = listItem.userUiModel
        checkBox.isChecked = listItem.selected

        userData.profilePic?.let { path -> imageLoader.loadImage(path, profilePic) }

        Log.d("prueba", "Elemento ${listItem.userUiModel.username} selected: ${listItem.selected}")
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                onCheckClickListener.onCheckClicked(listItem, adapterPosition)
            } else {
                onUnCheckClickListener.onCheckClicked(listItem, adapterPosition)
            }
        }
        userNameView.text = userData.username
    }

    interface OnCheckClickListener {
        fun onCheckClicked(user: ListItemUiModel.User, position: Int)
    }
}