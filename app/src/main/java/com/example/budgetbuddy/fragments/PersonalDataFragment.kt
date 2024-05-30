package com.example.budgetbuddy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.databinding.FragmentPersonalDataBinding
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Clase responsable de vincular la logica definida en el viewmodel [RegisterViewModel] con el fragmento [PersonalDataFragment]
 * Este fragmento sirve para en caso de inciar sesion con Google, se le pedira al usuario sus datos personales y nombre de usuario
 * para guardarlos en la base de datos.
 * La forma de trabajar con la autenticación de Firebase fue consultada en la documentacion de Firebase: https://firebase.google.com/docs/auth/android/start
 * La forma de trabajar con el binding fue consulada en la documentación de Android: https://developer.android.com/topic/libraries/view-binding
 * @author Armando Guzmán
 * */
@AndroidEntryPoint
class PersonalDataFragment : Fragment(), OnClickListener {
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentPersonalDataBinding
    private lateinit var currentUser:FirebaseUser

    override fun onStart() {
        super.onStart()
        //se valida si el usuario ya esta guardado en la base de datos
        currentUser = auth.currentUser!!
        var user: User? = null
        lifecycleScope.launch {
            currentUser.uid.let { user=viewModel.findUserByUID(it) }
            if(user!=null){
                updateUi()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_personal_data, container, false)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        prepareBinding(binding)
        return view
    }

    /**
     * Sirve para vincular los widgets del fragmento [PersonalDataFragment] con los metodos definidos en el viewmodel [RegisterViewModel]
     * @param binding Binding contenedor de todas las referencias a widgets de la vista
     * */
    private fun prepareBinding(binding: FragmentPersonalDataBinding) {
        binding.createUserBtn.setOnClickListener(this)

        binding.usernameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setUserName(text.toString())
            viewModel.validateUserName(text.toString(), requireContext())
        })
        binding.firstNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setFirstName(text.toString())
            viewModel.validateFirstName(text.toString(), requireContext())
        })
        binding.lastNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setLastName(text.toString())
            viewModel.validateLastName(text.toString(), requireContext())
        })
    }

    /**
     * Metodo que sirve para simplificar la creacion de un custom AlertDialog
     * @param layout Id del layout a mostrar
     * @param data Informacion que se usara para crear el dialog
     * */
    private fun showDialog(layout:Int, data:Result){
        val dialogFactory = AlertDialogFactory(requireContext())
        var dialogLayout = layout
        dialogFactory.createDialog(layout, binding.root, data)
    }

    /**
     * Metodo que se ejecutara al hacer click sobre el boton de crear usuario
     * se valida que los datos introducidos son correctos y se intenta crear el usuario
     * @param v Vista que activo el evento
     * */
    override fun onClick(v: View?) {
        lifecycleScope.launch {
            if (viewModel.personalDataGood) { //Si esta correcto se intenta crear un usuario en la base de datos
                Utilities.hideKeyboard(requireActivity(), requireContext())
                binding.determinateBar.visibility = View.VISIBLE;
                binding.personalDataFrame.alpha = 0.4f
                val user = User(
                    binding.firstNameEditText.text.toString(),
                    binding.lastNameEditText.text.toString(),
                    binding.usernameEditText.text.toString()
                )
                val data:Result?
                // se valida que el nombre de usuario no exista ya en la base de datos
                if(viewModel.username.value?.let { viewModel.findUser(it) } != null){
                    data = Result(
                        getString(R.string.fail_title),
                        getString(R.string.username_already_exits),
                        getString(R.string.try_again)
                    ){
                        binding.personalDataFrame.alpha = 1f
                    }
                    showDialog(R.layout.error_dialog, data)
                }else{
                    val result = viewModel.createNewUser(user, currentUser.uid)
                    if(result){
                        data = Result(
                            getString(R.string.success_title),
                            getString(R.string.success_registro_text),
                            getString(R.string.go_to_home)
                        ){
                            updateUi()
                        }
                        showDialog(R.layout.success_dialog, data)
                    }else{
                        data = Result(
                            getString(R.string.fail_title),
                            getString(R.string.fail_creating_user),
                            getString(R.string.try_again)
                        ){
                            binding.personalDataFrame.alpha = 1f
                        }
                        showDialog(R.layout.error_dialog, data)
                    }
                }
                binding.determinateBar.visibility = View.INVISIBLE;
            }
        }
    }

    /**
     * Metodo que sirve para hacer un [Intent] parar ir a [HomeActivity]
     * */
    private fun updateUi() {
        val intent: Intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }
}