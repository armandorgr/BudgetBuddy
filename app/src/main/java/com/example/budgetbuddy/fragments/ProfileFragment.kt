package com.example.budgetbuddy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.MainActivity
import com.example.budgetbuddy.databinding.FragmentProfileBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.PromptResult
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.ResultOkCancel
import com.example.budgetbuddy.util.TwoPromptResult
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.ProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dialogFactory: AlertDialogFactory
    private val GOOGLE_PROVIDER = "google.com"
    private val PASSWORD_PROVIDER = "password"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        prepareBinding(binding)
        dialogFactory = AlertDialogFactory(requireContext())
        return binding.root
    }

    private fun prepareBinding(binding: FragmentProfileBinding) {
        binding.usernameTextView.text = homeViewModel.currentUser.value?.username
        if (homeViewModel.provider.value == GOOGLE_PROVIDER) {
            binding.newEmailConstraintLayout.visibility = View.GONE
            binding.newPasswordConstraintLayout.visibility = View.GONE
        }
        binding.logoutConstraintLayout.setOnClickListener {
            binding.profileFrame.alpha = 0.3f
            homeViewModel.auth.signOut()
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
        binding.newPasswordConstraintLayout.setOnClickListener {
            reauthenticate(this::onPasswordChange)
        }
        binding.deleteAccountConstraintLayout.setOnClickListener {
            if (homeViewModel.provider.value == GOOGLE_PROVIDER) {
                reauthenticateWithGoogle(this::onDeleteAccount)
            } else {
                reauthenticate(this::onDeleteAccount)
            }

        }
        binding.changeUsernameConstraintLayout.setOnClickListener(this::onChangeUsername)
    }


    private fun onChangeUsername(view: View?) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        val data = PromptResult(
            getString(R.string.change_username_title),
            getString(R.string.change_username_hint),
            { dialog ->
                binding.determinateBar.visibility = View.VISIBLE
                val txt = dialog.findViewById<EditText>(R.id.newEditText).text.toString()
                var response: String? = viewModel.validateUsername(txt)
                dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText =
                    response ?: ""
                lifecycleScope.launch {
                    if (viewModel.findUserByUsername(txt) != null) {
                        response = getString(R.string.username_already_exits)
                        dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText =
                            response
                    } else {
                        if (response == null) {
                            viewModel.updateUsername(homeViewModel.firebaseUser.value!!.uid, txt)
                                .addOnCompleteListener {
                                    onChangeUsernameComplete(it)
                                    binding.determinateBar.visibility = View.INVISIBLE
                                    dialog.dismiss()
                                }
                        }
                    }

                }
            },
            {
                binding.profileFrame.alpha = 1f
            }
        )
        dialogFactory.createPromptDialog(binding.root, data)
    }

    private fun reauthenticateWithGoogle(onCompleteListener: (p: Task<Void>) -> Unit) {
        binding.profileFrame.alpha = 0.3f
        val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (acct != null) {
            val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null)
            homeViewModel.firebaseUser.value?.reauthenticate(credential)
                ?.addOnCompleteListener(onCompleteListener)
        }
    }

    private fun reauthenticate(onCompleteListener: (p: Task<Void>) -> Unit) {
        binding.profileFrame.alpha = 0.3f
        val data = TwoPromptResult(
            getString(R.string.reathenticate),
            getString(R.string.email),
            getString(R.string.password),
            { dialog ->
                binding.determinateBar.visibility = View.VISIBLE
                val email =
                    dialog.findViewById<TextInputEditText>(R.id.EmailEditText).text.toString()
                val password =
                    dialog.findViewById<TextInputEditText>(R.id.passwordEditText).text.toString()
                if (email != "" && password != "") {
                    val credential: AuthCredential =
                        EmailAuthProvider.getCredential(email, password)
                    homeViewModel.firebaseUser.value?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            dialog.dismiss()
                            onCompleteListener(it)
                        }
                }
            },
            {
                binding.profileFrame.alpha = 1f
            }
        )
        dialogFactory.createTwoPromptLayout(binding.root, data, true)
    }

    private fun onChangeEmail(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        if (task.isSuccessful) {
            val data = PromptResult(
                getString(R.string.new_email_title),
                getString(R.string.new_email),
                { dialog ->
                    val txt = dialog.findViewById<EditText>(R.id.newEditText).text.toString()
                    val response: String? = viewModel.validateEmail(txt)
                    dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText =
                        response ?: ""
                    if (response == null) {
                        binding.determinateBar.visibility = View.VISIBLE
                        homeViewModel.firebaseUser.value?.verifyBeforeUpdateEmail(txt)
                            ?.addOnCompleteListener {
                                dialog.dismiss()
                                binding.determinateBar.visibility = View.INVISIBLE
                                onEmailChangeComplete(it)
                            }

                    }
                },
                {
                    binding.profileFrame.alpha = 1f
                }
            )
            dialogFactory.createPromptDialog(binding.root, data)
        } else {
            failReauthentication(
                task.exception?.message ?: getString(R.string.fail_reauthentication)
            )
        }
    }

    private fun onPasswordChange(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        if (task.isSuccessful) {
            val data = PromptResult(
                getString(R.string.change_password_title),
                getString(R.string.change_password_hint),
                { dialog ->
                    binding.determinateBar.visibility = View.INVISIBLE
                    val txt =
                        dialog.findViewById<TextInputEditText>(R.id.newEditText).text.toString()
                    val response: String? = viewModel.validatePassword(txt)
                    dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText =
                        response ?: ""
                    if (response == null) {
                        homeViewModel.firebaseUser.value?.updatePassword(txt)
                            ?.addOnCompleteListener {
                                dialog.dismiss()
                                binding.determinateBar.visibility = View.INVISIBLE
                                onPasswordChangeComplete(it)
                            }

                    }
                },
                {
                    binding.profileFrame.alpha = 1f
                }
            )
            dialogFactory.createPromptDialog(binding.root, data, true)
        } else {
            failReauthentication(
                task.exception?.message ?: getString(R.string.fail_reauthentication)
            )
        }
    }

    private fun onDeleteAccount(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        if (task.isSuccessful) {
            val data = ResultOkCancel(
                getString(R.string.delete_account),
                getString(R.string.delete_account_message),
                {
                    binding.determinateBar.visibility = View.VISIBLE
                    homeViewModel.firebaseUser.value?.delete()?.addOnCompleteListener {
                        lifecycleScope.launch {
                            if (viewModel.deleteUser(homeViewModel.firebaseUser.value!!.uid)) {
                                onDeleteAccountComplete(it)
                                binding.determinateBar.visibility = View.INVISIBLE
                            } else {
                                failedChange(getString(R.string.delete_account_fail))
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
        } else {
            failReauthentication(
                task.exception?.message ?: getString(R.string.fail_reauthentication)
            )
        }
    }

    private fun onDeleteAccountComplete(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if (task.isSuccessful) {
            val data = Result(
                getString(R.string.success_title),
                getString(R.string.delete_account_success),
                getString(R.string.ok)
            ) {
                goToLoginActivity()
            }
            dialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
        } else {
            failedChange(task.exception?.message ?: getString(R.string.delete_account_fail))
        }
    }

    private fun onChangeUsernameComplete(it: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if (it.isSuccessful) {
            successChange(getString(R.string.change_username_success))
            lifecycleScope.launch {
                val usr = homeViewModel.firebaseUser.value?.uid?.let { viewModel.findUser(it) }
                if (usr != null) homeViewModel.updateUser(usr)
                binding.usernameTextView.text = usr?.username
            }
        } else {
            failedChange(getString(R.string.change_username_fail))
        }
    }

    private fun onPasswordChangeComplete(result: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        if (result.isSuccessful) {
            successChange(getString(R.string.change_password_success))
        } else {
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

    private fun failedChange(message: String) {
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
}