package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.navArgs
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentChatBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args:ChatFragmentArgs by navArgs()
    private lateinit var selectedGroupUID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        selectedGroupUID = args.selectedGroupUID
        Log.d("prueba", "grupo seleccionado: $selectedGroupUID")
        return binding.root
    }

}