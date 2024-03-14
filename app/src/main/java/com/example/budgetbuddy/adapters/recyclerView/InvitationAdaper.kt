package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.InvitationViewHolder
import com.example.budgetbuddy.viewHolders.ListItemViewHolder
import com.google.firebase.auth.FirebaseUser

/**
 * Clase la cual sirve para cargar las invitaciones cargadas del usuario en un [RecyclerView]
 * */
class InvitationAdapter(
    private val layoutInflater: LayoutInflater,
    private val onAcceptListener: OnClickListener,
    private val onDeclineListener: OnClickListener,
    private val currentUser: FirebaseUser
) : RecyclerView.Adapter<ListItemViewHolder>() {

    /**
     * Lista que contiene los datos cargados en el [RecyclerView]
     * */
    private val listData = mutableListOf<ListItemUiModel>()


    /**
     * Metodo que sirve para establecer la lista de los invitaciones cargados y actualizar el [RecyclerView]
     * @param newItems Lista con las nuevas invitaciones a establecer en el [RecyclerView]
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel>) {
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Interfaz la cual declara un unico metodo el cual sera el que se ejecute al hacer click sobre uno
     * de los elementos del [RecyclerView]
     * */
    interface OnClickListener {
        /**
         * Metodo abstracto que sera el que se
         * ejecute al hacer un click sobre un elemento del [RecyclerView]
         * @param invitation Invitacion sobre la cual se ha hecho click
         * @param currentUser Usuario actualmente logeado
         * */
        fun onItemClick(invitation: InvitationUiModel, currentUser: FirebaseUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = layoutInflater.inflate(R.layout.invitation_layout, parent, false)
        return InvitationViewHolder(view,
            object : InvitationViewHolder.OnClickListener {
                override fun onClick(invitation: InvitationUiModel, position: Int) {
                    onAcceptListener.onItemClick(invitation, currentUser)
                }
            }, object : InvitationViewHolder.OnClickListener {
                override fun onClick(invitation: InvitationUiModel, position: Int) {
                    onDeclineListener.onItemClick(invitation, currentUser)
                }
            })
    }

    /**
     * Metodo que sirve para eliminar un elemento de la lista de invitaciones
     * y por tanto tambein del [RecyclerView]
     * @param position Posicion que ocupa el elemento a eliminar en el Adapter
     * */
    fun removeItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}