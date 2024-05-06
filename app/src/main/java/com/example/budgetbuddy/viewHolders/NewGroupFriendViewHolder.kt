package com.example.budgetbuddy.viewHolders

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader

class NewGroupFriendViewHolder(
    private val context: Context,
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val onCheckClickListener: OnCheckClickListener,
    private val onUnCheckClickListener: OnCheckClickListener,
    private val onChangeRoleListener: OnChangeRoleListener?,
) : ListItemViewHolder(containerView) {

    private val userNameView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val roleView: TextView by lazy {
        containerView.findViewById(R.id.role_text_view)
    }
    private val checkBox: CheckBox by lazy {
        containerView.findViewById(R.id.checkbox)
    }
    private val profilePic: ImageView by lazy {
        containerView.findViewById(R.id.item_img_view)
    }
    private val editRoleIcon: ImageView by lazy{
        containerView.findViewById(R.id.editRoleIcon)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.User) {
            "Expected ListItemUiModel.User $listItem"
        }
        val userData = listItem.userUiModel
        checkBox.isChecked = listItem.selected

        userData.profilePic?.let { path -> imageLoader.loadImage(path, profilePic) }
        listItem.role?.let {
            roleView.visibility = View.VISIBLE
            roleView.text = context.getString(it.resourceID)
        }
        Log.d("prueba", "Elemento ${listItem.userUiModel.username} selected: ${listItem.selected}")

        if(listItem.editable != true){
            checkBox.isClickable = false
            editRoleIcon.visibility = View.GONE
            Log.d("prueba", " editable: ${listItem.editable}")
        }else{
            if(listItem.editable!! && onChangeRoleListener!=null){
                Log.d("prueba", " editable ----: ${listItem.editable}")
                editRoleIcon.visibility = View.VISIBLE
                editRoleIcon.setOnClickListener {
                    onChangeRoleListener.onClick(listItem, adapterPosition)
                }
            }
            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    onCheckClickListener.onCheckClicked(listItem, adapterPosition)
                } else {
                    onUnCheckClickListener.onCheckClicked(listItem, adapterPosition)
                }
            }
        }
        userNameView.text = userData.username
    }

    interface OnChangeRoleListener{
        fun onClick(user: ListItemUiModel.User, position: Int)
    }

    interface OnCheckClickListener {
        fun onCheckClicked(user: ListItemUiModel.User, position: Int)
    }
}