package com.example.budgetbuddy.adapters.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.CalendarViewHolder

class CalendarAdapter(
    private val daysOfMonth:List<ListItemUiModel.CalendarDayUiModel>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams:ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.1666666666).toInt()
        return CalendarViewHolder(view, onItemListener)
    }

    override fun getItemCount(): Int = daysOfMonth.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bindData(daysOfMonth[position])
    }

    interface OnItemListener{
        fun onItemClick(position: Int, dayText:String)
    }
}