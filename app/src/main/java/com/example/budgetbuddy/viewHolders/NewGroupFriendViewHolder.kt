package com.example.budgetbuddy.viewHolders

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader

/**
 * Clase ViewHolder encargada de vincular los datos de un amigo dentro de un grupo en el layout cargado en
 * el RecyclerView
 * @param containerView Vista del layout de la categoría
 * @param imageLoader Objeto usado para cargar las fotos de los amigos
 * @param context Contexto usado para acceder a los recursos de la aplicación
 * @param onCheckClickListener Objeto de clase que implementa la interfaz [NewGroupFriendViewHolder.OnCheckClickListener] cuyo método
 * onCheckClicked será llamada al marcar el check box de un amigo
 * @param onUnCheckClickListener Objeto de clase que implementa la interfaz [NewGroupFriendViewHolder.OnCheckClickListener] cuyo método
 * onCheckClicked será llamada al desmarcar el check box de un amigo
 * @param onChangeRoleListener Objeto de clase que implementa la interfaz [NewGroupFriendViewHolder.onChangeRoleListener] cuyo método
 * onClick será llamada al pulsar sobre la opción de cambiar rol de un amigo
 *
 * La manera empleada para el uso del [RecyclerView.ViewHolder] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 *
 * @author Armando Guzmán
 * */
class NewGroupFriendViewHolder(
    private val context: Context,
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val onCheckClickListener: OnCheckClickListener,
    private val onUnCheckClickListener: OnCheckClickListener,
    private val onChangeRoleListener: OnChangeRoleListener?,
) : ListItemViewHolder(containerView) {

    private val userNameView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val roleView: TextView by lazy {
        containerView.findViewById(R.id.role_text_view)
    }
    private val checkBox: CheckBox by lazy {
        containerView.findViewById(R.id.checkbox)
    }
    private val profilePic: ImageView by lazy {
        containerView.findViewById(R.id.item_img_view)
    }
    private val editRoleIcon: ImageView by lazy {
        containerView.findViewById(R.id.editRoleIcon)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.User) {
            "Expected ListItemUiModel.User $listItem"
        }
        val userData = listItem.userUiModel
        checkBox.isChecked = listItem.selected

        userData.profilePic?.let { path -> imageLoader.loadImage(path, profilePic) }
        listItem.role?.let {
            roleView.visibility = View.VISIBLE
            roleView.text = context.getString(it.resourceID)
        }
        Log.d("prueba", "Elemento ${listItem.userUiModel.username} selected: ${listItem.selected}")

        if (listItem.editable != true) {
            checkBox.isClickable = false
            editRoleIcon.visibility = View.GONE
            Log.d("prueba", " editable: ${listItem.editable}")
        } else {
            if (listItem.editable!! && onChangeRoleListener != null) {
                Log.d("prueba", " editable ----: ${listItem.editable}")
                editRoleIcon.visibility = View.VISIBLE
                editRoleIcon.setOnClickListener {
                    onChangeRoleListener.onClick(listItem, adapterPosition)
                }
            }
            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    onCheckClickListener.onCheckClicked(listItem, adapterPosition)
                } else {
                    onUnCheckClickListener.onCheckClicked(listItem, adapterPosition)
                }
            }
        }
        userNameView.text = userData.username
    }

    interface OnChangeRoleListener {
        fun onClick(user: ListItemUiModel.User, position: Int)
    }

    interface OnCheckClickListener {
        fun onCheckClicked(user: ListItemUiModel.User, position: Int)
    }
}