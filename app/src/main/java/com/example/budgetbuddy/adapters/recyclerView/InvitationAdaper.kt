package com.example.budgetbuddy.adapters.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.InvitationViewHolder
import com.example.budgetbuddy.viewHolders.ListItemViewHolder
import com.google.firebase.auth.FirebaseUser

class InvitationAdapter(
    private val layoutInflater: LayoutInflater,
    private val onAcceptListener: OnClickListener,
    private val onDeclineListener: OnClickListener,
    private val currentUser: FirebaseUser
) : RecyclerView.Adapter<ListItemViewHolder>() {

    private val listData = mutableListOf<ListItemUiModel>()

    fun setData(newItems: List<ListItemUiModel>) {
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }

    interface OnClickListener {
         fun onItemClick(invitation: InvitationUiModel, currentUser: FirebaseUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = layoutInflater.inflate(R.layout.invitation_layout, parent, false)
        return InvitationViewHolder(view,
            object : InvitationViewHolder.OnClickListener {
                override fun onClick(invitation: InvitationUiModel, position: Int) {
                    onAcceptListener.onItemClick(invitation, currentUser)
                }
            }, object : InvitationViewHolder.OnClickListener {
                override fun onClick(invitation: InvitationUiModel, position: Int) {
                    onDeclineListener.onItemClick(invitation, currentUser)
                }

            })
    }

    fun removeItem(position: Int){
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}