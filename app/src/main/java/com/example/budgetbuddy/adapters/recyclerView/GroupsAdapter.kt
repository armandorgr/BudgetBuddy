package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.GroupViewHolder

class GroupsAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<GroupViewHolder>() {
    private val listData = mutableListOf<ListItemUiModel>()
    //TODO HACER UNA LISTA FILTRADA COMO CON LOS AMIGOS

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel>) {
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }

    private val onClick = object : GroupViewHolder.OnClickListener {
        override fun onClick(group: ListItemUiModel.Group, position: Int) {
            onClickListener.onItemClick(group, position)
        }

    }

    interface OnClickListener {
        fun onItemClick(group: ListItemUiModel.Group, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = layoutInflater.inflate(R.layout.group_layout, parent, false)
        return GroupViewHolder(view, onClick)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}