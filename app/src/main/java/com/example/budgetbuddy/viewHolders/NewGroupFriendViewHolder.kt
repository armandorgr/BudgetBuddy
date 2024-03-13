package com.example.budgetbuddy.viewHolders

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
class NewGroupFriendViewHolder(
    private val containerView: View,
    private val onCheckClickListener: OnCheckClickListener,
    private val onUnCheckClickListener: OnCheckClickListener
) : ListItemViewHolder(containerView) {

    private val userNameView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val checkBox: CheckBox by lazy {
        containerView.findViewById(R.id.checkbox)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.User) {
            "Expected ListItemUiModel.User $listItem"
        }
        val userData = listItem.userUiModel
        checkBox.isChecked = listItem.selected
        checkBox.setOnClickListener{
            if(checkBox.isChecked){
                onCheckClickListener.onCheckClicked(listItem, adapterPosition)
            }else{
                onUnCheckClickListener.onCheckClicked(listItem, adapterPosition)
            }
        }
        userNameView.text = userData.username
    }

    interface OnCheckClickListener {
        fun onCheckClicked(user: ListItemUiModel.User, position: Int)
    }
}