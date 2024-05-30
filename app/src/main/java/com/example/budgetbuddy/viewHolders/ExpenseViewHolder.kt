package com.example.budgetbuddy.viewHolders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.model.ListItemUiModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ExpenseViewHolder(
    private val containerView: View

) : ListItemViewHolder(containerView) {
    private val titleTextView: TextView by lazy {
        containerView.findViewById(R.id.titleTextView)
    }
    private val amountTextView: TextView by lazy {
        containerView.findViewById(R.id.amountTextView)
    }
    private val payerTextView: TextView by lazy {
        containerView.findViewById(R.id.payerTextView)
    }
    private val dateTextView: TextView by lazy {
        containerView.findViewById(R.id.dateTextView)
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")




    fun bind(expense: Expense) {
        titleTextView.text = expense.title
        amountTextView.text = expense.amount.toString()
        payerTextView.text = expense.payerUserName
        val parsedDate = LocalDateTime.parse(expense.date, dateFormatter)
        val formattedDate = parsedDate.toLocalDate().toString()
        dateTextView.text = formattedDate

    }

    override fun bindData(listItem: ListItemUiModel) {

    }


}
