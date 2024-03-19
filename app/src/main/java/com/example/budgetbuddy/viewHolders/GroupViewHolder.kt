package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel

class GroupViewHolder(
    private val containerView: View,
    private val onClickListener: OnClickListener
) : ListItemViewHolder(containerView) {

    private val imgView: ImageView by lazy {
        containerView.findViewById(R.id.item_img_view)
    }
    private val textView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val descriptionView: TextView by lazy {
        containerView.findViewById(R.id.item_description_view)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Group) {
            "Expected ListItemUiModel.Group $listItem"
        }
        val groupData = listItem.groupUiModel
        textView.text = groupData.name
        descriptionView.text = groupData.description
        containerView.setOnClickListener {
            onClickListener.onClick(listItem, adapterPosition)
        }
    }

    interface OnClickListener {
        fun onClick(group: ListItemUiModel.Group, position: Int)
    }
}