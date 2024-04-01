package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewHolders.NewGroupFriendViewHolder

/**
 * Clase la cual sirve para cargar la lista de los amigos del usuario a un [RecyclerView]
 * y añadirle a cada elemento un evento el cual se ejecutara cuando se haga click sobre el checkBox del amigos
 * */
class NewGroupFriendsAdapter(
    private val layoutInflater: LayoutInflater,
    private val selectedList : MutableList<ListItemUiModel.User>,
    private val imageLoader: ListItemImageLoader,
    private val onCheckClickListener: OnCheckClickListener? = null,
    private val onUnCheckClickListener: OnCheckClickListener? = null,
) : RecyclerView.Adapter<NewGroupFriendViewHolder>() {
    /**
     * Lista la cual contendra todos los amigos cargados del usuario
     * */
    private val listData = mutableListOf<ListItemUiModel>()
    /**
     * Lista que solo contendra los amigos que se necesiten mostrar
     * */
    private val shownData = mutableListOf<ListItemUiModel>()

    /**
     * Metodo que sirve para establecer la lista de los amigos cargados y actualizar el [RecyclerView]
     * @param newItems Lista con los nuevos amigos a establecer en el [RecyclerView]
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel>) {
        listData.clear()
        val filteredData = newItems.toMutableList().apply { filter { item -> !selectedList.any { item2 -> item2.uid == (item as ListItemUiModel.User).uid }}}
        Log.d("prueba", "filtered data: $filteredData")
        listData.addAll(filteredData)
        shownData.clear()
        shownData.addAll(filteredData)
        notifyDataSetChanged()
    }

    /**
     * Metodo que sirve para filtrar los amigos que se muestran, solo se mostraran
     * los que contengan en su nombre de usuario [usernameQuery]
     * @param usernameQuery Valor que cual se validara si esta contenido en el nombre de usuario
     * de los amigos cargadas, si no lo contiene se oculta.
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun filterData(usernameQuery: String) {
        shownData.clear()
        shownData.addAll(listData.filter { item ->
            require(item is ListItemUiModel.User) {
                "Expected user"
            }
            item.userUiModel.username?.contains(usernameQuery) ?: false
        })
        notifyDataSetChanged()
    }

    /**
     * Metodo que sirve para hacer a todos los amigos visibles de nuevo
     * */
    fun resetData() {
        shownData.clear()
        shownData.addAll(listData)
        notifyDataSetChanged()
    }

    /**
     * Objeto anonimo que implementa la interfaz [NewGroupFriendViewHolder.OnCheckClickListener]
     * En la implementacion de desarrolla el codigo que se ejecutara cuando se marque y desmarque
     * el checkBox del Layout del amigo
     * */
    private val onCheckClickEvent = object : NewGroupFriendViewHolder.OnCheckClickListener {
        override fun onCheckClicked(user: ListItemUiModel.User, position: Int) {
            user.selected = true
            Log.d("prueba", "Elemento añadido: ${selectedList.add(user)}")
            Log.d("prueba", "selected users: ${selectedList}")
            onCheckClickListener?.onCheckClick(user.userUiModel, position)
        }
    }
    private val onUnCheckEvent = object : NewGroupFriendViewHolder.OnCheckClickListener {
        override fun onCheckClicked(user: ListItemUiModel.User, position: Int) {
            Log.d("prueba", "Elemento eleminado: ${selectedList.remove(user)}")
            Log.d("prueba", "selected users: ${selectedList}")
            onUnCheckClickListener?.onCheckClick(user.userUiModel, position)
            user.selected = false
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGroupFriendViewHolder {
        val view = layoutInflater.inflate(R.layout.new_group_friend_layout, parent, false)
        return NewGroupFriendViewHolder(view, imageLoader ,onCheckClickEvent, onUnCheckEvent)
    }

    override fun getItemCount(): Int {
        return shownData.size
    }

    override fun onBindViewHolder(holder: NewGroupFriendViewHolder, position: Int) {
        holder.bindData(shownData[position])
    }

    /**
     * Interfaz que declara un solo metodo, este metodo una vez implementado sera el que se ejecute cuando
     * se haga click sobre el checkBox
     * */
    interface OnCheckClickListener {
        /**
         * Metodo que, una vez implementad, sera el que se ejecute cuando se haga click sobre el checkBox
         * @param user Amigo sobre el cual ha hecho click
         * @param position Posicion que ocupa el amigo en el Adapter
         * */
        fun onCheckClick(user: User, position: Int)
    }
}