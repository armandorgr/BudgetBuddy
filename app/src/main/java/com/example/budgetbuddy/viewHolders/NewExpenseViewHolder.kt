package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.Expense

class NewExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleEditText: EditText = itemView.findViewById(R.id.newExpenseTitle)
    private val amountEditText: EditText = itemView.findViewById(R.id.newExpenseAmount)
    private val dateText: TextView = itemView.findViewById(R.id.date)


    fun bind(expense: Expense) {
        titleEditText.setText(expense.title)
        amountEditText.setText(expense.amount.toString())
        dateText.setText(expense.date)
    }
}