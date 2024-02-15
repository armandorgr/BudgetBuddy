package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.tab.DETAILS_TABS_PAIR
import com.example.budgetbuddy.adapters.tab.DetailsMenuAdapter
import com.example.budgetbuddy.databinding.FragmentDetailsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailsBinding.bind(view)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = DetailsMenuAdapter(parentFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = resources.getString(DETAILS_TABS_PAIR[position])
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

}