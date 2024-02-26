package com.example.budgetbuddy.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentLoginBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.Result
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
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }

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

    private fun signInWithEmailPassword(email: String, password: String) {
        val dialogFactory = AlertDialogFactory(requireContext())
        var dialogLayout: Int = 0
        var data: Result? = null
        binding.frame.alpha = 0.4f
        binding.determinateBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {
                val user = auth.currentUser
                dialogLayout = R.layout.success_dialog
                data = Result(
                    getString(R.string.success_title),
                    getString(R.string.loggedin_success),
                    getString(R.string.go_to_home)
                ) {
                    binding.frame.alpha = 1f
                    updateUI(user)
                }
            } else {
                dialogLayout = R.layout.error_dialog
                data = Result(
                    getString(R.string.fail_title),
                    getString(R.string.account_doesnt_exist),
                    getString(R.string.try_again)
                ) {
                    binding.frame.alpha = 1f
                }
            }
            data?.let { it1 ->
                dialogFactory.createDialog(
                    dialogLayout, binding.root,
                    it1
                )
            }
            binding.determinateBar.visibility = View.INVISIBLE
        }
    }

    /**
     * Método usado para conseguir autorización de firebase mediante las
     * credenciales obtenidas de Google
     * @param idToken token usado para obtener las credenciales de Google
     * */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val dialogFactory = AlertDialogFactory(requireContext())
        var dialogLayout: Int = 0
        var data: Result? = null
        binding.frame.alpha = 0.4f
        binding.determinateBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    dialogLayout = R.layout.success_dialog
                    data = Result(
                        getString(R.string.success_title),
                        getString(R.string.loggedin_success),
                        getString(R.string.go_to_home)
                    ) {
                        binding.frame.alpha = 1f
                        binding.determinateBar.visibility = View.INVISIBLE
                        updateUI(user)
                    }
                } else {
                    dialogLayout = R.layout.error_dialog
                    data = Result(
                        getString(R.string.fail_title),
                        getString(R.string.fail_login),
                        getString(R.string.try_again)
                    ) {
                        binding.frame.alpha = 1f
                        binding.determinateBar.visibility = View.INVISIBLE
                    }
                }
                data?.let { it1 ->
                    dialogFactory.createDialog(
                        dialogLayout, binding.root,
                        it1
                    )
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val view = binding.root

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = Firebase.auth
        binding.textViewSignUp.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_login_to_register))
        binding.signUpBtn.setOnClickListener {
            signInWithEmailPassword(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
            hideKeyboard()
        }
        binding.google.setOnClickListener {
            signIn()
        }
        return view
    }


    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Método que sirve para actualizar la interfaz una vez se haya iniciado sesión correctamente.
     * @param user Usuario con el cual se ha iniciado sesión
     * */
    private fun updateUI(user: FirebaseUser?) {
        Toast.makeText(requireContext(), user?.email ?: "null", Toast.LENGTH_LONG).show()
    }


}