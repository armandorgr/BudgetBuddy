package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.GroupCategoryViewHolder

class GroupCategoryAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClick: GroupCategoryViewHolder.OnClick
) : RecyclerView.Adapter<GroupCategoryViewHolder>() {
    private val listData: MutableList<ListItemUiModel.CategoryUiModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel.CategoryUiModel>){
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupCategoryViewHolder {
        val view = layoutInflater.inflate(R.layout.group_category_layout, parent, false)
        return GroupCategoryViewHolder(view, onClick)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: GroupCategoryViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}