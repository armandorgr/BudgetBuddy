package com.example.budgetbuddy.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.budgetbuddy.model.ListItemUiModel

abstract class ListItemViewHolder(containerView: View) : ViewHolder(containerView) {
    abstract fun bindData(listItem: ListItemUiModel)
}