package com.example.budgetbuddy.viewHolders

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.calendar.CalendarAdapter
import com.example.budgetbuddy.model.ListItemUiModel

class CalendarViewHolder
    (
    private val containerView: View,
    private val context: Context,
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
            hasEventCircle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.event_item_anim))
            hasEventCircle.setOnClickListener(this)
        } else {
            hasEventCircle.visibility = View.GONE
        }
    }
}