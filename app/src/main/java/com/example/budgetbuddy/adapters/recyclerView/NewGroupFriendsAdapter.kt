package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.text.BoringLayout
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
 * La manera empleada para el uso del [RecyclerView.Adapter] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 * @param context Contexto usado para acceder a los recursos de la aplicación
 * @param layoutInflater LayoutInflater usado para inflar las vistas
 * @param selectedList Lista de usuarios seleccionados
 * @param imageLoader Objeto usado para cargar la imagenes de los elementos
 * @param onChangeRoleListener Objeto de clase que implemente la interfaz [NewGroupFriendsAdapter.OnChangeRoleListener] cuyo método
 * será llamado al querer cambiar el rol de un usuario.
 * @param onCheckClickListener Objeto de clase que implemente la interfaz [NewGroupFriendsAdapter.OnCheckClickListener] cuyo método
 * será llamado al pulsar sobre el check box del usuario para marcarlo
 * @param onUnCheckClickListener Objeto de clase que implemente la interfaz [NewGroupFriendsAdapter.OnCheckClickListener] cuyo método
 * será llamado al pulsar sobre el check box del usuario para desmarcarlo
 * @author Armando Guzmán
 * */
class NewGroupFriendsAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val selectedList: MutableList<ListItemUiModel.User>,
    private val imageLoader: ListItemImageLoader,
    private val onChangeRoleListener: OnChangeRoleListener? = null,
    private val onCheckClickListener: OnCheckClickListener? = null,
    private val onUnCheckClickListener: OnCheckClickListener? = null,
) : RecyclerView.Adapter<NewGroupFriendViewHolder>() {

    private var editable: Boolean = true

    /**
     * Lista la cual contendra todos los amigos cargados del usuario
     * */
    private val listData: MutableList<ListItemUiModel> = mutableListOf()

    /**
     * Lista que solo contendra los amigos que se necesiten mostrar
     * */
    private val shownData: MutableList<ListItemUiModel> = mutableListOf()

    /**
     * Metodo que sirve para establecer la lista de los amigos cargados y actualizar el [RecyclerView]
     * @param newItems Lista con los nuevos amigos a establecer en el [RecyclerView]
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel>) {
        listData.clear()
        newItems.forEach { (it as ListItemUiModel.User).editable = editable }
        val filteredData = newItems.toMutableList()
            .apply { filter { item -> !selectedList.any { item2 -> item2.uid == (item as ListItemUiModel.User).uid } } }
        Log.d("prueba", "filtered data: $filteredData")
        listData.addAll(filteredData)
        shownData.clear()
        shownData.addAll(filteredData)
        notifyDataSetChanged()
    }

    fun setEditable(editable: Boolean) {
        this.editable = editable
        listData.forEach { (it as ListItemUiModel.User).editable = editable }
        shownData.forEach { (it as ListItemUiModel.User).editable = editable }
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
    @SuppressLint("NotifyDataSetChanged")
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
            Log.d(
                "prueba",
                "Elemento eleminado: ${selectedList.removeIf { i -> i.uid == user.uid }}"
            )
            Log.d("prueba", "selected users: ${selectedList}")
            onUnCheckClickListener?.onCheckClick(user.userUiModel, position)
            user.selected = false
        }
    }

    private val onChangeRoleEvent = if (onChangeRoleListener != null) {
        object : NewGroupFriendViewHolder.OnChangeRoleListener {
            override fun onClick(user: ListItemUiModel.User, position: Int) {
                onChangeRoleListener?.onChangeRole(user, position)
            }
        }
    } else {
        null
    }

    fun removeItem(item: ListItemUiModel.User) {
        listData.removeIf {
            require(it is ListItemUiModel.User)
            it.uid == item.uid
        }
        shownData.removeIf {
            require(it is ListItemUiModel.User)
            it.uid == item.uid
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGroupFriendViewHolder {
        val view = layoutInflater.inflate(R.layout.new_group_friend_layout, parent, false)
        return NewGroupFriendViewHolder(
            context,
            view,
            imageLoader,
            onCheckClickEvent,
            onUnCheckEvent,
            onChangeRoleEvent,
        )
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

    interface OnChangeRoleListener {
        fun onChangeRole(user: ListItemUiModel.User, position: Int)
    }
}