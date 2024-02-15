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


class DetailsMenuAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return DETAILS_TABS_PAIR.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) GastosFragment() else SaldosFragment()
    }
}
