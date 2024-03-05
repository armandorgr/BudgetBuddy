package com.example.budgetbuddy.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalDataFragment : Fragment(), OnClickListener {
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentPersonalDataBinding
    private lateinit var currentUser:FirebaseUser

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser!!
        var user: User? = null
        lifecycleScope.launch {
            currentUser.uid.let { user=viewModel.findUserByUID(it) }
            if(user!=null){
                updateUi()
            }
        }
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Método usado para preparar el binding de las vistas con los campos del viewmodel
     * y añadir eventos para realizar las validaciones cada vez que se escriben en los EditText
     * @param binding binding del fragmento usado para acceder a los controles de la vista
     * */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_personal_data, container, false)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        prepareBinding(binding)
        return view
    }

    private fun prepareBinding(binding: FragmentPersonalDataBinding) {
        binding.createUserBtn.setOnClickListener(this)

        binding.usernameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setUserName(text.toString())
            viewModel.validateUserName(text.toString())
        })
        binding.firstNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setFirstName(text.toString())
            viewModel.validateFirstName(text.toString())
        })
        binding.lastNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setLastName(text.toString())
            viewModel.validateLastName(text.toString())
        })
    }

    private fun showDialog(layout:Int, data:Result){
        val dialogFactory = AlertDialogFactory(requireContext())
        var dialogLayout = layout
        dialogFactory.createDialog(layout, binding.root, data)
    }

    override fun onClick(v: View?) {
        lifecycleScope.launch {
            if (viewModel.personalDataGood) { //Si esta correcto se intenta crear un usuario en la base de datos
                hideKeyboard()
                binding.determinateBar.visibility = View.VISIBLE;
                binding.personalDataFrame.alpha = 0.4f
                val user = User(
                    binding.firstNameEditText.text.toString(),
                    binding.lastNameEditText.text.toString(),
                    currentUser.email,
                    binding.usernameEditText.text.toString()
                )
                val data:Result?

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

    private fun updateUi() {
        val intent: Intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }
}