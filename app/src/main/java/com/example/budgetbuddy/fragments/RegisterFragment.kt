package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentRegisterBinding
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val viewModel:RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentRegisterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        view.findViewById<TextView>(R.id.textViewLogin).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_register_to_login))
        prepareBinding(binding)

        return view
    }

    private fun prepareBinding(binding:FragmentRegisterBinding){
        binding.usernameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setUserName(text.toString())
            viewModel.validateUserName(text.toString())
        })
        binding.emailEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setEmail(text.toString())
            viewModel.validateEmail(text.toString())
        })
        binding.firstNameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setFirstName(text.toString())
            viewModel.validateFirstName(text.toString())
        })
        binding.lastNameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setLastName(text.toString())
            viewModel.validateLastName(text.toString())
        })
        binding.passwordEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setPassword(text.toString())
            viewModel.validatePassword(text.toString(), binding.repeatPasswordEditText.text.toString())
        })
        binding.repeatPasswordEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setRepeatPassword(text.toString())
            viewModel.validatePassword(text.toString(), binding.passwordEditText.text.toString())
        })

    }
}