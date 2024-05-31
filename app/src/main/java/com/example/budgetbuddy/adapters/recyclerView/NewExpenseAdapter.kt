package com.example.budgetbuddy.adapters.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.viewHolders.NewExpenseViewHolder
/**
 * Clase adaptador para mostrar una lista de nuevos gastos en un RecyclerView.
 * @param expenses Lista de nuevos gastos a mostrar en el RecyclerView.
 * @author Álvaro Aparicio
 */
class NewExpenseAdapter(private val expenses: List<Expense>) : RecyclerView.Adapter<NewExpenseViewHolder>() {

    /**
     * Método que crea y devuelve una instancia de `NewExpenseViewHolder`.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_new_expense, parent, false)
        return NewExpenseViewHolder(view)
    }

    /**
     * Método que vincula los datos de un gasto específico con su correspondiente `NewExpenseViewHolder`.
     */
    override fun onBindViewHolder(holder: NewExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense)
    }

    /**
     * Método que devuelve el número total de elementos en la lista de nuevos gastos.
     */
    override fun getItemCount() = expenses.size
}
