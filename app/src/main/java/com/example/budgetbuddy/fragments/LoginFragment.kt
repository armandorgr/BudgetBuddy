package com.example.budgetbuddy.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.databinding.FragmentLoginBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.PromptResult
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragmento desde donde el usuario podrá iniciar sesión mediante correo electrónico o mediante una cuenta de Google
 * La forma de trabajar con el binding fue consulada en la documentación de Android: https://developer.android.com/topic/libraries/view-binding
 * La forma de trabajar con la autenticación de Firebase fue consultada en la documentacion de Firebase: https://firebase.google.com/docs/auth/android/start
 * @author Armando Guzmán
 * */
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var intent: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: RegisterViewModel by viewModels()

    /**
     * Metodo que sirve para hacer un intent e ir al activity HOME
     * */
    private fun goToHome() {
        val intent: Intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
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

    private fun onSignInWithEmailPasswordComplete(task: Task<AuthResult>){
        val dialogFactory = AlertDialogFactory(requireContext())
        val dialogLayout: Int
        val data: Result?
        if (task.isSuccessful) {
            val user = auth.currentUser
            dialogLayout = R.layout.success_dialog
            data = Result(
                getString(R.string.success_title),
                getString(R.string.loggedin_success),
                getString(R.string.go_to_home)
            ) {
                binding.frame.alpha = 1f
                goToHome()
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
        data.let { it1 ->
            dialogFactory.createDialog(
                dialogLayout, binding.root,
                it1
            )
        }
        binding.determinateBar.visibility = View.INVISIBLE
    }

    /**
     * Metodo que sirve para iniciar sesion mediante correo y contraseña
     * @param email Direccion de correo con la cual se intentara iniciar sesion
     * @param password Contraseña con la cual se intentara iniciar sesion
     * */
    private fun signInWithEmailPassword(email: String, password: String) {
        binding.frame.alpha = 0.4f
        binding.determinateBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this::onSignInWithEmailPasswordComplete)
    }

    /**
     * Método que se llama al pulsar sobre la opción de resetear contraseña en la vista.
     * Se mostrará una ventana emergente, desde donde el usuari podra introducir una dirección de correo electrónico
     * a la cual se le enviará un correo para que cambie su contraseña
     * @param view Vista que disparó el evento
     * */
    private fun onForgotPasswordClick(view: View?){
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = PromptResult(
            getString(R.string.reset_password),
                getString(R.string.email),
            { dialog ->
                val txt = dialog.findViewById<TextView>(R.id.newEditText).text.toString()
                if(txt.isNotEmpty()){
                    auth.sendPasswordResetEmail(txt)
                    Toast.makeText(requireContext(), getString(R.string.reset_password_email_sent), Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            },
            {}
        )
        alertDialogFactory.createPromptDialog(binding.root, data)
    }

    /**
     * Método usado para conseguir autorización de firebase mediante las
     * credenciales obtenidas de Google
     * @param idToken token usado para obtener las credenciales de Google
     * */
    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.frame.alpha = 0.4f
        binding.determinateBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this::onSignInWithGoogleComplete)
    }

    /**
     * Método que se llamará cuando termine el inicio de sesión mediante Google
     * @param task La tarea de inico de sesión que se usará para validar si fue exítosa o no.
     * */
    private fun onSignInWithGoogleComplete(task: Task<AuthResult>) {
        val dialogFactory = AlertDialogFactory(requireContext())
        val dialogLayout: Int
        val data: Result?
        if (task.isSuccessful) {
            val user = auth.currentUser
            dialogLayout = R.layout.success_dialog
            data = Result(getString(R.string.success_title), getString(R.string.loggedin_success), getString(R.string.go_to_home)
            ) {
                binding.frame.alpha = 1f
                binding.determinateBar.visibility = View.INVISIBLE
                lifecycleScope.launch {
                    updateUI(user!!)
                }
            }
        } else {
            dialogLayout = R.layout.error_dialog
            data = Result(getString(R.string.fail_title), getString(R.string.fail_login), getString(R.string.try_again)
            ) {
                binding.frame.alpha = 1f
                binding.determinateBar.visibility = View.INVISIBLE
            }
        }
        data.let { it1 ->
            dialogFactory.createDialog(
                dialogLayout, binding.root,
                it1
            )
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
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email != "" && password != "") {
                signInWithEmailPassword(email, password)
                Utilities.hideKeyboard(requireActivity(), requireContext())
            }
        }
        binding.google.setOnClickListener {
            signIn()
        }

        binding.forgotPasswordTextView.setOnClickListener(this::onForgotPasswordClick)
        return view
    }

    /**
     * Método que sirve para actualizar la interfaz una vez se haya iniciado sesión correctamente.
     * @param user Usuario con el cual se ha iniciado sesión
     * */
    private suspend fun updateUI(user: FirebaseUser) {
        val userData = viewModel.findUserByUID(user.uid)
        if(userData!=null){
            goToHome()
        }else{
            val action = LoginFragmentDirections.navLoginToPersonalData()
            findNavController().navigate(action)
        }
    }
}