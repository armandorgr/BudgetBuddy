package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.GROUP_CATEGORY
import com.example.budgetbuddy.model.ListItemUiModel

class GroupCategoryViewHolder(
    private val containerView: View,
    private val onClick: OnClick
) : ListItemViewHolder(containerView) {
    private val categoryIcon: ImageView by lazy {
        containerView.findViewById(R.id.categoryIcon)
    }
    private val categoryCard: ConstraintLayout by lazy {
        containerView.findViewById(R.id.categoryCard)
    }
    private val categoryTextView: TextView by lazy {
        containerView.findViewById(R.id.category)
    }
    private val categorySelectIcon: ImageView by lazy {
        containerView.findViewById(R.id.unselectIcon)
    }


    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.CategoryUiModel) {
            "Expected ListItemUiModel.CategoryUiModel got $listItem"
        }
        val context = containerView.context
        categoryIcon.setImageDrawable(context.getDrawable(listItem.category.iconID))
        categoryTextView.text = context.getString(listItem.category.stringID)
        categoryCard.background.setTint(context.getColor(listItem.category.colorID))
        categorySelectIcon.visibility = if (listItem.isSelected) View.VISIBLE else View.INVISIBLE
        categoryCard.setOnClickListener{
            onClick.onClick(listItem)
        }
    }

    interface OnClick {
        fun onClick(category: ListItemUiModel.CategoryUiModel)
    }
}