package com.example.budgetbuddy.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.budgetbuddy.model.ListItemUiModel

/**
 * Clase abstracta de la cual heredarán todos los demás [ViewHolder], esta define el método que todas
 * deberan implementar para vincular los datos de los item con los layout
 * @param containerView Vista contenedora del layout del item
 *
 * La manera empleada para el uso del [RecyclerView.ViewHolder] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 *
 * @author Armando Guzmán
 * */
abstract class ListItemViewHolder(containerView: View) : ViewHolder(containerView) {

    /**
     * Método que sirve para vincular los datos contenidos del item, con el layout usado
     * para mostrarlo en la vista
     * @param listItem Objeto contenedor de los datos
     * */
    abstract fun bindData(listItem: ListItemUiModel)
}