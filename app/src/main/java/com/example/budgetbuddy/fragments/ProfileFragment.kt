package com.example.budgetbuddy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private lateinit var dialogFactory:AlertDialogFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser!!
        loadCurrentUser(firebaseUser)
        prepareBinding(binding)
        dialogFactory = AlertDialogFactory(requireContext())
        return binding.root
    }

    private fun prepareBinding(binding: FragmentProfileBinding){
        binding.logoutConstraintLayout.setOnClickListener{
            binding.profileFrame.alpha = 0.3f
            auth.signOut()
            val data = Result(
                getString(R.string.success_title),
                getString(R.string.logout_success),
                getString(R.string.go_to_login)
            ){
                goToLoginActivity()
            }
            logOut(data)
        }
    }

    private fun logOut(data:Result){
        val layout = R.layout.success_dialog
        dialogFactory.createDialog(layout, binding.root, data)
    }

    private fun goToLoginActivity(){
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