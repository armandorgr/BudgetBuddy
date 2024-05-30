package com.example.budgetbuddy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.databinding.FragmentSplashScreenBinding
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragmento en donde se determina si el usuario ha iniciado sesión o no, y si lo ha hecho, se valida si
 * existen sus datos personales dentro de la base de datos.
 *
 * Si el usuario no ha iniciado sesión será redirigido hacia [LoginFragment]
 *
 * Si el usuario si ha iniciado sesión y existen datos dentro dentro la base de datos será redirigo hacia [HomeActivity]
 *
 * Si el usuario si ha iniciado sesión, pero no existen datos dentro de la base de datos, será redirigido hacia [PersonalDataFragment]
 *
 * La forma de trabajar con el binding fue consulada en la documentación de Android: https://developer.android.com/topic/libraries/view-binding
 * La forma de trabajar con la autenticación de Firebase fue consultada en la documentacion de Firebase: https://firebase.google.com/docs/auth/android/start
 * @author Armando Guzmán
 * */
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private var _binding:FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val registerViewModel:RegisterViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        lifecycleScope.launch {
            if(currentUser != null){
                //El usuario esta iniciado secion
                val userData = registerViewModel.findUserByUID(currentUser.uid)
                if(userData != null){
                    //Ha iniciado sesion y hay datos en la bd
                    goToHome()
                }else{
                    //Ha iniciado sesion pero no hay datos en la bd
                    goToPersonalData()
                }
            }else{
                //El usuario no ha iniciado sesion
                goToLogin()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    /**
     * Método que sirve para ir a [HomeActivity]
     * */
    private fun goToHome(){
        val intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }

    /**
     * Método que sirve para ir a [LoginFragment]
     * */
    private fun goToLogin(){
        val action = SplashScreenFragmentDirections.navSplashToLogin()
        findNavController().navigate(action)
    }

    /**
     * Método que sirve para ir a [PersonalDataFragment]
     * */
    private fun goToPersonalData(){
        val action = SplashScreenFragmentDirections.navSplashToPersonalData()
        findNavController().navigate(action)
    }
}