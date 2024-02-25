package com.example.budgetbuddy.adapters.tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.budgetbuddy.R
import com.example.budgetbuddy.fragments.GastosFragment
import com.example.budgetbuddy.fragments.SaldosFragment

val DETAILS_TABS_PAIR = listOf(
    R.string.Gastos,
    R.string.saldos
)

/**
 * Adaptador usado para crear las pestañas del menú del fragmento Details
 * */
class DetailsMenuAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
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
        return if (position == 0) GastosFragment() else SaldosFragment()
    }
}