package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentRegisterBinding
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val viewModel:RegisterViewModel by viewModels()
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val binding:FragmentRegisterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        view.findViewById<TextView>(R.id.textViewLogin).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_register_to_login))
        prepareBinding(binding)
        return view
    }

    private fun createAccount(email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                val user = auth.currentUser
                Toast.makeText(requireContext(), "Usuario registrado ${user}", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(), "${it.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Método usado para preparar el binding de las vistas con los campos del viewmodel
     * y añadir eventos para realizar las validaciones cada vez que se escriben en los EditText
     * @param binding binding del fragmento usado para acceder a los controles de la vista
     * */
    private fun prepareBinding(binding:FragmentRegisterBinding){
        binding.signUpBtn.setOnClickListener{
            if(viewModel.allGood){
                createAccount(viewModel.email.value.toString(), viewModel.password.value.toString())
            }else{
                Toast.makeText(context, "Incorecto", Toast.LENGTH_LONG).show()
            }
        }

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