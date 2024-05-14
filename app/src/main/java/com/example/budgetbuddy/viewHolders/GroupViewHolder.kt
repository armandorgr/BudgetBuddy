package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader

class GroupViewHolder(
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val onClickListener: OnClickListener,
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
    private val groupCard: ConstraintLayout by lazy {
        containerView.findViewById(R.id.group_card)
    }


    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Group) {
            "Expected ListItemUiModel.Group $listItem"
        }
        val groupData = listItem.groupUiModel
        groupData.pic?.let { imageLoader.loadImage(it, imgView) }
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