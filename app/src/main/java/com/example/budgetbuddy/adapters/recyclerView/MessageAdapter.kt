package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewHolders.MessageViewHolder

/*
* Constantes usadas para identificar si el mensaje ha sido enviado por el usuario actual
* u otro usuario.
* */
private const val VIEW_TYPE_OWN_MESSAGE = 0
private const val VIEW_TYPE_OTHER_MESSAGE = 1

/**
 * Clase adaptador que sirve para cargar los mensajes cargados de un chat de grupo
 * en un [RecyclerView]
 * @param currentUserUID UID del usuario actual, usado para determinar si algún mensaje es suyo o no.
 * @param layoutInflater LayoutInflater usado para inflar las vistas.
 * @param imageLoader Objeto de la clase [ListItemImageLoader] usado para cargar las fotos de los usuarios y los mensajes.
 * @param context Contexto usado para acceder a los recursos de la aplicación
 * @param onImageClick Objeto de clase que implemente la interfaz [MessageViewHolder.OnImageClick] cuyo método onImageClick será
 * llamando al pulsar sobre un mensaje con foto.
 * */
class MessageAdapter(
    private val currentUserUID: String,
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ListItemImageLoader,
    private val context: Context,
    private val onImageClick: MessageViewHolder.OnImageClick
) : RecyclerView.Adapter<MessageViewHolder>() {
    private val listData = mutableListOf<ListItemUiModel.MessageUiModel>()

    /**
     * Metodo que sirve para establecer la lista de los mensajes cargados y actualizar el [RecyclerView]
     * @param newItems Lista con los nuevos mensajes a establecer en el [RecyclerView]
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel.MessageUiModel>) {
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when(viewType){
            // Se usa un layout diferente en función de si el mensaje es del usuario actual o de otro usuario.
            VIEW_TYPE_OWN_MESSAGE -> {
                val view = layoutInflater.inflate(R.layout.own_message_layout, parent, false)
                MessageViewHolder(view, imageLoader, context, onImageClick)
            }

            VIEW_TYPE_OTHER_MESSAGE -> {
                val view = layoutInflater.inflate(R.layout.others_message_layout, parent, false)
                MessageViewHolder(view, imageLoader, context, onImageClick)
            }

            else -> throw IllegalArgumentException("Unknown view type requested: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].message.senderUID?.equals(currentUserUID) == true) {
            VIEW_TYPE_OWN_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}