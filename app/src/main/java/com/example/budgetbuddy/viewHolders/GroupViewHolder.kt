package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader

/**
 * Clase ViewHolder encargada de vincular los datos de un grupo en el layout cargado en
 * el RecyclerView
 * @param containerView Vista del layout de la categoría
 * @param imageLoader Objeto usado para cargar las fotos de los grupos
 * @param onClickListener Objeto de clase que implementa la interfaz [GroupCategoryViewHolder.OnClick]
 * cuyo método onClick será llamada cada vez que se pulse sobre una de los items cargados
 *
 * La manera empleada para el uso del [RecyclerView.ViewHolder] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 *
 * @author Armando Guzmán
 * */
class GroupViewHolder(
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val onClickListener: OnClickListener,
) : ListItemViewHolder(containerView) {

    private val imgView: ImageView by lazy {
        containerView.findViewById(R.id.item_img_view)
    }
    private val textView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val descriptionView: TextView by lazy {
        containerView.findViewById(R.id.item_description_view)
    }
    private val category: TextView by lazy {
        containerView.findViewById(R.id.category)
    }


    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Group) {
            "Expected ListItemUiModel.Group $listItem"
        }
        val groupData = listItem.groupUiModel
        groupData.pic?.let { imageLoader.loadImage(it, imgView) }
        textView.text = groupData.name
        descriptionView.text = groupData.description
        containerView.setOnClickListener {
            onClickListener.onClick(listItem, adapterPosition)
        }
        groupData.category?.stringID?.let { category.text = containerView.context.getString(it) }
        groupData.category?.colorID?.let { category.background.setTint(containerView.context.getColor(it)) }
    }

    interface OnClickListener {
        fun onClick(group: ListItemUiModel.Group, position: Int)
    }
}