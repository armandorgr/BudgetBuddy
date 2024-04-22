package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.calendar.CalendarAdapter
import com.example.budgetbuddy.model.ListItemUiModel

class CalendarViewHolder
    (
    private val containerView: View,
    private val onItemListener: CalendarAdapter.OnItemListener
) : ListItemViewHolder(containerView), View.OnClickListener {


    private val dayOfMonth: TextView by lazy {
        containerView.findViewById(R.id.cellDayTextView)
    }
    private val hasEventCircle: ImageView by lazy {
        containerView.findViewById(R.id.hasEventCircle)
    }

    override fun onClick(p0: View?) {
        this.onItemListener.onItemClick(adapterPosition, this.dayOfMonth.text as String)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.CalendarDayUiModel) {
            "Expected ListItemUiModel.CalendarDayUiModel got $listItem"
        }
        dayOfMonth.text = listItem.day

        if (listItem.hasEvent) {
            hasEventCircle.visibility = View.VISIBLE
            hasEventCircle.setOnClickListener(this)
        } else {
            hasEventCircle.visibility = View.GONE
        }
    }
}