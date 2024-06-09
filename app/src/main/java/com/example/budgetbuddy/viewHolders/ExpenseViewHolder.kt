package com.example.budgetbuddy.viewHolders

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.fragments.ExpenseFragmentDirections
import com.example.budgetbuddy.fragments.GroupsFragmentDirections
import com.example.budgetbuddy.fragments.NewExpenseFragmentDirections
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.model.ListItemUiModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
/**
 * Clase ViewHolder responsable de mostrar elementos de gasto en un RecyclerView.
 *
 * @param containerView La vista que representa el diseño del elemento de gasto.
 *
 * @author Álvaro Aparicio
 */
class ExpenseViewHolder(
    private val containerView: View,
    private val onClick : (expenseUID : String) -> Unit
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
    private val deleteButton : Button by lazy{
        containerView.findViewById(R.id.deleteButton)
    }




    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    /*
    init {
        containerView.setOnClickListener {
            val action = ExpenseFragmentDirections.navToNewExpense(it)
            containerView.findNavController().navigate(action)
        }
    }
*/

    /**
     * Vincula los datos del gasto a la vista.
     *
     * @param expense El objeto de gasto que contiene los datos a mostrar.
     */
    fun bind(expense: Expense) {
        titleTextView.text = expense.title
        amountTextView.text = expense.amount.toString()
        payerTextView.text = expense.payerUserName
        val parsedDate = LocalDateTime.parse(expense.date, dateFormatter)
        val formattedDate = parsedDate.toLocalDate().toString()
        dateTextView.text = formattedDate


        deleteButton.setOnClickListener{
            onClick(expense.expenseUID.toString())
        }
    }

    override fun bindData(listItem: ListItemUiModel) {

    }


}
