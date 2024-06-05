package com.example.budgetbuddy.adapters.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.budgetbuddy.R
import com.example.budgetbuddy.fragments.ExpenseFragment
import com.example.budgetbuddy.fragments.GastosFragment
import com.example.budgetbuddy.fragments.SaldosFragment

val DETAILS_TABS_PAIR = listOf(
    R.string.Gastos,
    R.string.saldos
)

/**
 * Adaptador usado para crear las pestañas del menú del fragmento Details
 * La forma de implementar el ViewPager se obtuvo de la documentación de Android Developer:
 * https://developer.android.com/guide/navigation/navigation-swipe-view-2?hl=es-419
 * @author Armando Guzmán
 * */
class DetailsMenuAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,private val currentGroupId: String) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    /**
     * Método que sirve para obtener el numero de pestañas a crear en el menú
     * @return El numero de pestañas a crear en el menú
     * */
    override fun getItemCount(): Int {
        return DETAILS_TABS_PAIR.size
    }

    /**
     * Método que sirve para cambiar de fragment en función de la posicion del menú
     * @param position posición actual del menú
     * @return fragmento a mostrar en el ViewPager
     * */
    override fun createFragment(position: Int): Fragment {
        val fragment : Fragment
        if(position == 0){
            fragment = ExpenseFragment()
        } else{
            fragment = SaldosFragment()
        }

        val data: Bundle = Bundle()
        data.putString("currentGroupId",currentGroupId)
        fragment.arguments = data

        return fragment
    }
}
