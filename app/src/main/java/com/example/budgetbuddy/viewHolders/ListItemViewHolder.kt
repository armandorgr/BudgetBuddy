package com.example.budgetbuddy.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.budgetbuddy.model.ListItemUiModel
import com.google.firebase.auth.FirebaseUser

abstract class ListItemViewHolder(containerView: View) : ViewHolder(containerView) {
    abstract fun bindData(listItem: ListItemUiModel)
}