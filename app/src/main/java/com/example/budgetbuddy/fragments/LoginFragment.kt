package com.example.budgetbuddy.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentLoginBinding
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var intent: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                }
            }
        }
    }
    /**
     * Método que sirve para iniciar sesión con Google
     * */
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        intent.launch(signInIntent)
    }

    /**
     * Método usado para conseguir autorización de firebase mediante las
     * credenciales obtenidas de Google
     * @param idToken token usado para obtener las credenciales de Google
     * */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val view = binding.root

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = Firebase.auth
        binding.textViewSignUp.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_login_to_register))
        binding.google.setOnClickListener {
            signIn()
        }
        return view
    }

    /**
     * Método que sirve para actualizar la interfaz una vez se haya iniciado sesión correctamente.
     * @param user Usuario con el cual se ha iniciado sesión
     * */
    private fun updateUI(user: FirebaseUser?) {
        Toast.makeText(requireContext(), user?.displayName, Toast.LENGTH_LONG).show()
    }


}