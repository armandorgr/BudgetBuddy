package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.viewHolders.GroupCategoryViewHolder

/**
 *Clase adaptador que sirve para cargar las categorías de grupos en el [RecyclerView] de GroupFragment
 * @param layoutInflater LayoutInflater usado para inflar las vistas
 * @param onClick objecto de clase que implemente la interfaz [GroupCategoryViewHolder.OnClick] cuyo método
 * onClick será llamado al hacer clic sobre alguno de los elementos cargados.
 * La manera empleada para el uso del [RecyclerView.Adapter] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 * @author Armando Guzmán
 * */
class GroupCategoryAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClick: GroupCategoryViewHolder.OnClick
) : RecyclerView.Adapter<GroupCategoryViewHolder>() {
    private val listData: MutableList<ListItemUiModel.CategoryUiModel> = mutableListOf()

    /**
     * Método que sirve para cargar una nueva lista de elementos de tipo [ListItemUiModel.CategoryUiModel]
     * @param newItems Lista de nuevos elementos a cargar
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel.CategoryUiModel>){
        listData.clear()
        listData.addAll(newItems)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupCategoryViewHolder {
        val view = layoutInflater.inflate(R.layout.group_category_layout, parent, false)
        return GroupCategoryViewHolder(view, onClick)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: GroupCategoryViewHolder, position: Int) {
        holder.bindData(listData[position])
    }
}