package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewHolders.InvitationViewHolder
import com.example.budgetbuddy.viewHolders.ListItemViewHolder
import com.google.firebase.auth.FirebaseUser

/**
 * Clase la cual sirve para cargar las invitaciones cargadas del usuario en un [RecyclerView]
 * La manera empleada para el uso del [RecyclerView.Adapter] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 * @param layoutInflater LayoutInflater usado para inflar las vistas
 * @param imageLoader Objeto usado para cargar la imagenes de los elementos
 * @param context Contexto usado para acceder a los recursos de la apllicacion
 * @param onAcceptListener Objeto de clase que implemente la interfaz [InvitationAdapter.OnClickListener]
 * cuyo método OnClick será llamado hacer clic sobre la opción de aceptar la invitación
 * @param onDeclineListener Objeto de clase que implemente la interfaz [InvitationAdapter.OnClickListener]
 * cuyo método OnClick será llamado hacer clic sobre la opción de rechazar la invitación
 * @param currentUser Usuario actual en la aplicación
 * @author Armando Guzmán
 * */

class InvitationAdapter(
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ListItemImageLoader,
    private val context: Context,
    private val onAcceptListener: OnClickListener,
    private val onDeclineListener: OnClickListener,
    private val currentUser: FirebaseUser
) : RecyclerView.Adapter<ListItemViewHolder>() {

    /**
     * Lista que contiene los datos cargados en el [RecyclerView]
     * */
    private val listData:MutableList<ListItemUiModel> = mutableListOf()


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
        return InvitationViewHolder(view, imageLoader, context,
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

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}