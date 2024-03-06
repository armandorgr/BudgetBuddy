package com.example.budgetbuddy.fragments

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.activities.MainActivity
import com.example.budgetbuddy.databinding.FragmentProfileBinding
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.PromptResult
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.ResultOkCancel
import com.example.budgetbuddy.util.TwoPromptResult
import com.example.budgetbuddy.viewmodels.ProfileViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: User
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var dialogFactory: AlertDialogFactory
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        firebaseUser = auth.currentUser!!
        loadCurrentUser(firebaseUser)
        prepareBinding(binding)
        dialogFactory = AlertDialogFactory(requireContext())
        return binding.root
    }

    private fun prepareBinding(binding: FragmentProfileBinding) {
        binding.logoutConstraintLayout.setOnClickListener {
            binding.profileFrame.alpha = 0.3f
            auth.signOut()
            val data = Result(
                getString(R.string.success_title),
                getString(R.string.logout_success),
                getString(R.string.go_to_login)
            ) {
                goToLoginActivity()
            }
            logOut(data)
        }
        binding.newEmailConstraintLayout.setOnClickListener {
            reauthenticate(this::onChangeEmail)
        }
        binding.newPasswordConstraintLayout.setOnClickListener{
            reauthenticate(this::onPasswordChange)
        }
        binding.deleteAccountConstraintLayout.setOnClickListener{
            reauthenticate(this::onDeleteAccount)
        }
    }


    private fun reauthenticate(onCompleteListener: (p: Task<Void>) -> Unit) {
        binding.profileFrame.alpha = 0.3f
        val data = TwoPromptResult(
            getString(R.string.reathenticate),
            getString(R.string.email),
            getString(R.string.password),
            { dialog ->
                val email =
                    dialog.findViewById<TextInputEditText>(R.id.EmailEditText).text.toString()
                val password =
                    dialog.findViewById<TextInputEditText>(R.id.passwordEditText).text.toString()
                if (email != "" && password != "") {
                    val credential: AuthCredential =
                        EmailAuthProvider.getCredential(email, password)
                    firebaseUser.reauthenticate(credential).addOnCompleteListener {
                        dialog.dismiss()
                        onCompleteListener(it)
                    }
                }
            },
            {
                binding.profileFrame.alpha = 1f
            }
        )
        dialogFactory.createTwoPromptLayout(binding.root, data)
    }

    private fun onChangeEmail(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if (task.isSuccessful) {
            val data = PromptResult(
                getString(R.string.new_email_title),
                getString(R.string.new_email),
                { dialog ->
                    val txt = dialog.findViewById<EditText>(R.id.newEditText).text.toString()
                    val response: String? = viewModel.validateEmail(txt)
                    dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText = response ?: ""
                    if (response == null) {
                        firebaseUser.verifyBeforeUpdateEmail(txt)
                            .addOnCompleteListener(this::onEmailChangeComplete)
                        dialog.dismiss()
                    }
                },
                {
                    binding.profileFrame.alpha = 1f
                }
            )
            dialogFactory.createPromptDialog(binding.root, data)
        } else {
            failReauthentication(task.exception?.message ?: getString(R.string.fail_reauthentication))
        }
    }

    private fun onPasswordChange(task: Task<Void>){
        binding.profileFrame.alpha = 0.3f
        if(task.isSuccessful){
            val data = PromptResult(
                getString(R.string.change_password_title),
                getString(R.string.change_password_hint),
                {dialog ->
                    val txt = dialog.findViewById<TextInputEditText>(R.id.newEditText).text.toString()
                    val response: String? = viewModel.validatePassword(txt)
                    dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText = response ?: ""
                    if(response == null){
                        firebaseUser.updatePassword(txt).addOnCompleteListener(this::onPasswordChangeComplete)
                        dialog.dismiss()
                    }
                },
                {
                    binding.profileFrame.alpha = 1f
                }
            )
            dialogFactory.createPromptDialog(binding.root, data)
        }else{
            failReauthentication(task.exception?.message ?: getString(R.string.fail_reauthentication))
        }
    }

    private fun onDeleteAccount(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if(task.isSuccessful){
            val data = ResultOkCancel(
                getString(R.string.delete_account),
                getString(R.string.delete_account_message),
                {
                    firebaseUser.delete().addOnCompleteListener{
                        lifecycleScope.launch {
                            if(viewModel.deleteUser(firebaseUser.uid)){
                                onDeleteAccountComplete(it)
                            }
                        }
                    }
                    it.dismiss()
                },
                {
                    binding.profileFrame.alpha = 1f;
                }
            )
            dialogFactory.createOkCancelDialog(binding.root, data)
        }else{
            failReauthentication(task.exception?.message ?: getString(R.string.fail_reauthentication))
        }
    }

    private fun onDeleteAccountComplete(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if(task.isSuccessful){
            val data = Result(
                getString(R.string.success_title),
                getString(R.string.delete_account_success),
                getString(R.string.ok)
            ){
                goToLoginActivity()
            }
            dialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
        }else{
            failedChange(task.exception?.message ?: getString(R.string.delete_account_fail))
        }
    }



    private fun onPasswordChangeComplete(result: Task<Void>){
        binding.profileFrame.alpha = 0.3f
        if(result.isSuccessful){
            successChange(getString(R.string.change_password_success))
        }else{
            failedChange(result.exception?.message ?: getString(R.string.change_password_fail))
        }
    }

    private fun onEmailChangeComplete(result: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if (result.isSuccessful) {
            successChange(getString(R.string.success_email_change))
        } else {
            failedChange(result.exception?.message ?: getString(R.string.fail_email_change))
        }
    }

    private fun failedChange(message:String){
        val data = Result(
            getString(R.string.fail_title),
            message,
            getString(R.string.ok)
        ) {
            binding.profileFrame.alpha = 1f
        }
        dialogFactory.createDialog(R.layout.error_dialog, binding.root, data)
    }

    private fun failReauthentication(message: String) {
        binding.profileFrame.alpha = 0.3f
        val data = Result(
            getString(R.string.fail_title),
            message,
            getString(R.string.try_again)
        ) {
            binding.profileFrame.alpha = 1f
        }
        dialogFactory.createDialog(R.layout.error_dialog, binding.root, data)
    }

    private fun logOut(data: Result) {
        val layout = R.layout.success_dialog
        dialogFactory.createDialog(layout, binding.root, data)
    }

    private fun successChange(message: String) {
        val data = Result(
            getString(R.string.success_title),
            message,
            getString(R.string.ok)
        ) {
            binding.profileFrame.alpha = 1f
        }
        dialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    private fun goToLoginActivity() {
        val intent: Intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }


    private fun loadCurrentUser(firebaseUser: FirebaseUser) {
        lifecycleScope.launch {
            val usr = firebaseUser.uid.let { viewModel.findUser(it) }
            binding.usernameTextView.text = usr?.username
        }
    }
}