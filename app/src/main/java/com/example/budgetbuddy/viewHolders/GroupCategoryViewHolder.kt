package com.example.budgetbuddy.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.calendar.CalendarAdapter
import com.example.budgetbuddy.model.ListItemUiModel

/**
 * Clase ViewHolder encargada de vincular los datos de una categoría de grupo en el layout cargado en
 * el RecyclerView
 * @param containerView Vista del layout de la categoría
 * @param onClick Objeto de clase que implementa la interfaz [GroupCategoryViewHolder.OnClick]
 * cuyo método onClick será llamada cada vez que se pulse sobre una de los items cargados
 * usado para responder al clic sobre alguno de los días
 *
 * La manera empleada para el uso del [RecyclerView.ViewHolder] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 *
 * @author Armando Guzmán
 * */
class GroupCategoryViewHolder(
    private val containerView: View,
    private val onClick: OnClick
) : ListItemViewHolder(containerView) {
    private val categoryIcon: ImageView by lazy {
        containerView.findViewById(R.id.categoryIcon)
    }
    private val categoryCard: ConstraintLayout by lazy {
        containerView.findViewById(R.id.categoryCard)
    }
    private val categoryTextView: TextView by lazy {
        containerView.findViewById(R.id.category)
    }
    private val categorySelectIcon: ImageView by lazy {
        containerView.findViewById(R.id.unselectIcon)
    }


    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.CategoryUiModel) {
            "Expected ListItemUiModel.CategoryUiModel got $listItem"
        }
        val context = containerView.context
        categoryIcon.setImageDrawable(context.getDrawable(listItem.category.iconID))
        categoryTextView.text = context.getString(listItem.category.stringID)
        categoryCard.background.setTint(context.getColor(listItem.category.colorID))
        categorySelectIcon.visibility = if (listItem.isSelected) View.VISIBLE else View.INVISIBLE
        categoryCard.setOnClickListener{
            onClick.onClick(listItem)
        }
    }

    interface OnClick {
        fun onClick(category: ListItemUiModel.CategoryUiModel)
    }
}