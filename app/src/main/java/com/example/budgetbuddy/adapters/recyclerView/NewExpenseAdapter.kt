package com.example.budgetbuddy.adapters.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.viewHolders.NewExpenseViewHolder

class NewExpenseAdapter(private val expenses: List<Expense>) : RecyclerView.Adapter<NewExpenseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_new_expense, parent, false)
        return NewExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense)
    }

    override fun getItemCount() = expenses.size
}
