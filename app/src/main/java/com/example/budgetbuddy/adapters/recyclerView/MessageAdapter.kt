package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.MESSAGE_TYPE
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewHolders.MessageViewHolder

private const val VIEW_TYPE_OWN_MESSAGE = 0
private const val VIEW_TYPE_OTHER_MESSAGE = 1

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