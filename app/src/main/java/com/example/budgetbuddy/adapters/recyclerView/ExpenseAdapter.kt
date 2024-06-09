package com.example.budgetbuddy.adapters.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.viewHolders.ExpenseViewHolder

/**
 * Clase adaptador se utiliza para mostrar una lista de gastos en un RecyclerView.
 * Utiliza el patrón de adaptador común en Android para asociar los datos de los gastos con las vistas de diseño.
 * @param expenses Lista de gastos a mostrar en el RecyclerView.
 * @author Álvaro Aparicio
 */
class ExpenseAdapter (private var expenses: List<Expense>, private val onClick : (expenseUID : String) -> Unit) : RecyclerView.Adapter<ExpenseViewHolder>() {

    /**
     * Método que crea y devuelve una instancia de `ExpenseViewHolder`.
     * Este método se llama cuando RecyclerView necesita un nuevo ViewHolder del tipo dado para representar un elemento.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(itemView, onClick)
    }

    /**
     * Método que vincula los datos de un gasto específico con su correspondiente `ExpenseViewHolder`.
     * Este método se llama por RecyclerView para mostrar los datos en la posición especificada.
     */
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense)
    }

    /**
     * Método que devuelve el número total de elementos en la lista de gastos.
     * RecyclerView llama a este método para obtener el número total de elementos en el conjunto de datos.
     */
    override fun getItemCount(): Int {
        return expenses.size
    }

    /**
     * Método que actualiza la lista de gastos con una nueva lista y notifica al RecyclerView del cambio.
     * Se llama cuando se actualiza la lista de gastos y se requiere una actualización en el RecyclerView.
     * @param newExpenses Nueva lista de gastos.
     */
    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
    }


