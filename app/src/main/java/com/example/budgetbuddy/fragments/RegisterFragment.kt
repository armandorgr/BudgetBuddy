package com.example.budgetbuddy.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.databinding.FragmentRegisterBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        view.findViewById<TextView>(R.id.textViewLogin).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_register_to_login))
        prepareBinding(binding)
        return view
    }

    /**
     * Metodo que se llamara cuando se complete la tarea de crear una nueva cuenta mediante correo y contraseña
     * Si es exitosa se creara un nuevo usuario en la base de datos y se muestra un mensaje de exito, si no lo es, se muestra un mensaje de error
     * @param task Tarea que devuelve el metodo para crear una nueva cuenta, esta contiene el resultado de dicha tarea.
     * */
    private fun onCreateUserWithEmailPasswordComplete(task: Task<AuthResult>){
        val dialogFactory = AlertDialogFactory(requireContext())
        var dialogLayout:Int = 0
        var data:Result? = null
        if (task.isSuccessful) {
            val user = auth.currentUser
            if (user?.uid != null) {
                lifecycleScope.launch {
                    val result = viewModel.createNewUser(user.uid)
                    if (result){
                        dialogLayout = R.layout.success_dialog
                        data = Result(getString(R.string.success_title), getString(R.string.success_registro_text), getString(R.string.go_to_home)
                        ) {
                            updateUI(auth.currentUser)
                        }
                    }
                    data?.let { it1 ->
                        dialogFactory.createDialog(dialogLayout, binding.root,
                            it1
                        )
                    }
                }
            }
        } else {
            dialogLayout = R.layout.error_dialog
            data = Result(getString(R.string.fail_title), task.exception?.message ?: "error", getString(R.string.try_again)
            ) {
                binding.frame.alpha = 1f
            }
        }
        data?.let { it1 ->
            dialogFactory.createDialog(dialogLayout, binding.root,
                it1
            )
        }

    }
    /**
     * Metodo que sirve para crear una nueva cuenta mediante correo electronico y contraseña
     * @param email Direccion de correo con la cual se intentara crear la nueva cuenta
     * @param password Contraseña con la cual se intentara crear la nueva cuenta
     * */
    private fun createAccount(email: String, password: String) {
        val dialogFactory = AlertDialogFactory(requireContext())
        var dialogLayout = 0
        var data:Result? = null
        binding.determinateBar.visibility = View.VISIBLE;
        binding.frame.alpha = 0.4f
        lifecycleScope.launch {
            if (viewModel.username.value?.let { viewModel.findUser(it) } != null) {
                dialogLayout = R.layout.error_dialog
                data = Result(getString(R.string.fail_title), getString(R.string.username_already_exits),getString(R.string.try_again)
                ) {
                    binding.frame.alpha = 1f
                }
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { onCreateUserWithEmailPasswordComplete(it) }
            }
            binding.determinateBar.visibility = View.INVISIBLE
            data?.let { it1 -> dialogFactory.createDialog(dialogLayout, binding.root, it1)
            }
        }
    }

    /**
     * Método que sirve para actualizar la interfaz una vez se haya iniciado sesión correctamente.
     * @param user Usuario con el cual se ha iniciado sesión
     * */
    private fun updateUI(user: FirebaseUser?) {
        val intent:Intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }

    /**
     * Método usado para preparar el binding de las vistas con los campos del viewmodel
     * y añadir eventos para realizar las validaciones cada vez que se escriben en los EditText
     * @param binding binding del fragmento usado para acceder a los controles de la vista
     * */
    private fun prepareBinding(binding: FragmentRegisterBinding) {
        binding.signUpBtn.setOnClickListener {
            if (viewModel.allGood) {
                Utilities.hideKeyboard(requireActivity(), requireContext())
                createAccount(viewModel.email.value.toString(), viewModel.password.value.toString())
            }
        }
        //Se añaden eventos a los widgets de las vistas para aplicar la logica definida en el ViewModel
        binding.usernameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setUserName(text.toString())
            viewModel.validateUserName(text.toString())
        })
        binding.emailEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setEmail(text.toString())
            viewModel.validateEmail(text.toString())
        })
        binding.firstNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setFirstName(text.toString())
            viewModel.validateFirstName(text.toString())
        })
        binding.lastNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setLastName(text.toString())
            viewModel.validateLastName(text.toString())
        })
        binding.passwordEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setPassword(text.toString())
            viewModel.validatePassword(
                text.toString(),
                binding.repeatPasswordEditText.text.toString()
            )
        })
        binding.repeatPasswordEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setRepeatPassword(text.toString())
            viewModel.validatePassword(text.toString(), binding.passwordEditText.text.toString())
        })

    }
}