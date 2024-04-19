package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewHolders.GroupViewHolder

/**
 * Clase la cual sirve para cargar la lista de los grupos a los que pertenece el usuario a un [RecyclerView]
 * y añadirle a cada elemento un evento el cual se ejecutara cuando se haga click sobre el layout del grupo
 * La manera empleada para el uso del [RecyclerView.Adapter] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 * */
class GroupsAdapter(
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ListItemImageLoader,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<GroupViewHolder>() {
    private val listData = mutableListOf<ListItemUiModel.Group>()
    //TODO HACER UNA LISTA FILTRADA COMO CON LOS AMIGOS

    /**
     * Metodo que sirve para establecer la lista de los grupos cargados y actualizar el [RecyclerView]
     * @param newItems Lista con los nuevos grupos a establecer en el [RecyclerView]
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel.Group>) {
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Objeto anonimo que implementa la interfaz [GroupViewHolder.OnClickListener]
     * ejecutando el metodo onItemClick del objeto pasado como parametro al constructor del Adapter
     * */
    private val onClick = object : GroupViewHolder.OnClickListener {
        override fun onClick(group: ListItemUiModel.Group, position: Int) {
            onClickListener.onItemClick(group, position)
        }

    }

    /**
     * Interfaz la cual declara un unico metodo el cual sera el que se ejecute al hacer click sobre uno
     * de los elementos del [RecyclerView]
     * */
    interface OnClickListener {
        /**
         * Metodo abstracto que sera el que se
         * ejecute al hacer un click sobre un elemento del [RecyclerView]
         * @param group Grupo sobre el cual se ha hecho click
         * @param position Posicion que ocupa el objeto en el Adapter
         * */
        fun onItemClick(group: ListItemUiModel.Group, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = layoutInflater.inflate(R.layout.group_layout, parent, false)
        return GroupViewHolder(view, imageLoader, onClick)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}