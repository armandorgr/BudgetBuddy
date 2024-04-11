package com.example.budgetbuddy.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.MainActivity
import com.example.budgetbuddy.databinding.FragmentProfileBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.ImageLoader
import com.example.budgetbuddy.util.PromptResult
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.ResultOkCancel
import com.example.budgetbuddy.util.TwoPromptResult
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.ProfileViewModel
import com.example.budgetbuddy.viewmodels.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Clase responsable de vincular la logica definida en el viewmodel [ProfileViewModel] con los widgets del fragmento [ProfileFragment]
 * Este fragmento servira para manejar la cuenta del usuario y cerrar sesion.
 * */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dialogFactory: AlertDialogFactory
    private val GOOGLE_PROVIDER = "google.com"
    private val PASSWORD_PROVIDER = "password"
    private val imageLoader = ImageLoader(this,this::onSuccessGallery,this::onSuccessCamera,this::onPhotoLoadFail)

    private fun onPhotoLoadFail() {
        Toast.makeText(requireContext(), getString(R.string.select_photo_error), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        prepareBinding(binding)
        dialogFactory = AlertDialogFactory(requireContext())
        return binding.root
    }

    /**
     * Metodo que sirve para vincular los eventos que se producen en la vista con los metodos
     * definidos en el viewmodel [ProfileViewModel]
     * @param binding Binding contenedor de las referencias a todos los widgets del fragmento
     * */
    private fun prepareBinding(binding: FragmentProfileBinding) {
        if(homeViewModel.provider.value?.equals(PASSWORD_PROVIDER) == true){
            binding.addPhotoIcon.visibility = View.VISIBLE
            binding.proflePic.setOnClickListener(this::onAddPhotoClick)
        }
        
        binding.usernameTextView.text = homeViewModel.currentUser.value?.username

        val profilePic = homeViewModel.currentUser.value?.profilePic

        if(profilePic != null){
            viewModel.loadProfilePic(requireContext(), profilePic, binding.proflePic)
        }
        // si se inicio sesion con Google se ocultan las opciones de cambio de correo y contraseña
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

    private fun onDeleteProfilePic(){
        val path = homeViewModel.currentUser.value?.profilePic?.substring(2)
        path?.let {
            homeViewModel.firebaseUser.value?.uid?.let { it1 ->
                viewModel.deleteProfilePic(it, it1){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(requireContext(), getString(R.string.delete_photo_success),Toast.LENGTH_SHORT).show()
                        homeViewModel.currentUser.value?.profilePic = null
                        Glide.with(requireContext()).load(R.drawable.default_profile_pic).into(binding.proflePic)
                    }else{
                        Toast.makeText(requireContext(), path,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun onAddPhotoClick(view: View?) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val onDelete = if(homeViewModel.currentUser.value?.profilePic != null) this::onDeleteProfilePic else null
        alertDialogFactory.createPhotoDialog(binding.root, { imageLoader.getPhotoFromGallery() },{ imageLoader.getPhotoFromCamera() }, onDelete)
    }

    private fun onSuccessCamera(img: Bitmap) {
        homeViewModel.firebaseUser.value?.uid?.let {uid->
            viewModel.uploadProfilePicByBitmap(Utilities.PROFILE_PIC_ST ,img, uid){it, path->
                if(it.isSuccessful){
                    Glide.with(requireContext()).load(img).into(binding.proflePic)
                    homeViewModel.currentUser.value?.profilePic = path
                }else{
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onSuccessGallery(uri: Uri) {
        homeViewModel.firebaseUser.value?.uid?.let {uid->
            viewModel.uploadProfilePicByUri(Utilities.PROFILE_PIC_ST, uri, uid){it, path ->
                if(it.isSuccessful){
                    Glide.with(requireContext()).load(uri).into(binding.proflePic)
                    homeViewModel.currentUser.value?.profilePic = path
                }else{
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Metodo que se ejecuta cuando se hace click sobre el boton para cambiar nombre de usuario
     * Se muestra un alertDialog para introducir el nuevo nombre y valida que no exista ya.
     * Si es correcto se cambia el nombre de usuario y se muestra un mensaje de exito
     * @param view Vista que activo el evento
     * */
    private fun onChangeUsername(view: View?) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        val data = PromptResult(
            getString(R.string.change_username_title),
            getString(R.string.change_username_hint),
            { dialog ->
                Utilities.hideKeyboard(requireActivity(), requireContext())
                binding.determinateBar.visibility = View.VISIBLE

                val txt = dialog.findViewById<EditText>(R.id.newEditText).text.toString()
                var response: String? = registerViewModel.validateUserName(txt, requireContext())

                dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText = response ?: ""
                lifecycleScope.launch {
                    if (viewModel.findUserByUsername(txt) != null) {
                        response = getString(R.string.username_already_exits)
                        dialog.findViewById<TextInputLayout>(R.id.promptTextLayout).helperText = response
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
                binding.determinateBar.visibility = View.INVISIBLE
                binding.profileFrame.alpha = 1f
            }
        )
        dialogFactory.createPromptDialog(binding.root, data)
    }

    /**
     * Para firebase ciertas acciones como borrar la cuenta o cambiar el correo son acciones sensibles,
     * por lo que se debe haber autenticado recientemente para poder hacer dichas acciones.
     * Este metodo sirve para reautenticar la cuenta de Google con la que se ha iniciado sesion
     * @param onCompleteListener Metodo que se ejecutara cuando la reautenticacion termine
     * */
    private fun reauthenticateWithGoogle(onCompleteListener: (p: Task<Void>) -> Unit) {
        binding.profileFrame.alpha = 0.3f
        val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (acct != null) {
            val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null)
            homeViewModel.firebaseUser.value?.reauthenticate(credential)
                ?.addOnCompleteListener(onCompleteListener)
        }
    }

    /**
     * Para firebase ciertas acciones como borrar la cuenta o cambiar el correo son acciones sensibles,
     * por lo que se debe haber autenticado recientemente para poder hacer dichas acciones.
     * Este metodo muestra un dialog para introducir correo y contraseña y se intenta reautenticar
     * @param onCompleteListener Metodo que se ejecutara cuando la reautenticacion termine
     * */
    private fun reauthenticate(onCompleteListener: (p: Task<Void>) -> Unit) {
        binding.profileFrame.alpha = 0.3f
        val data = TwoPromptResult(
            getString(R.string.reathenticate),
            getString(R.string.email),
            getString(R.string.password),
            { dialog ->
                Utilities.hideKeyboard(requireActivity(), requireContext())
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
                binding.determinateBar.visibility = View.INVISIBLE
                binding.profileFrame.alpha = 1f
            }
        )
        dialogFactory.createTwoPromptLayout(binding.root, data, true)
    }

    /**
     * Metodo que se ejecutara cuando se haga click sobre el boton de cambiar email.
     * Este metodo se para como onCompleteListener para el metodo de [reauthenticate]
     * ya que cambiar el correo es una accion sensible
     * @param task Resultado de la reautenticacion
     * */
    private fun onChangeEmail(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        if (task.isSuccessful) {
            val data = PromptResult(
                getString(R.string.new_email_title),
                getString(R.string.new_email),
                { dialog ->
                    Utilities.hideKeyboard(requireActivity(), requireContext())
                    val txt = dialog.findViewById<EditText>(R.id.newEditText).text.toString()
                    val response: String? = registerViewModel.validateEmail(txt, requireContext())
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
                    binding.determinateBar.visibility = View.INVISIBLE
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

    /**
     * Metodo que se ejecutara cuando se haga click sobre cambiar contraseña
     * Este metodo se pasa como onCompleteListener al metodo [reauthenticate]
     * ya que cambiar la contraseña es una accion sensible
     * @param task Resultado de la reautenticacion
     * */
    private fun onPasswordChange(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        if (task.isSuccessful) {
            val data = PromptResult(
                getString(R.string.change_password_title),
                getString(R.string.change_password_hint),
                //Este es la funcion que se ejecutara cuando se presione el boton del dialog
                { dialog ->
                    Utilities.hideKeyboard(requireActivity(), requireContext())
                    binding.determinateBar.visibility = View.INVISIBLE
                    val txt =
                        dialog.findViewById<TextInputEditText>(R.id.newEditText).text.toString()
                    val response: String? = registerViewModel.validatePassword(txt, txt, requireContext())
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
                    binding.determinateBar.visibility = View.INVISIBLE
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

    /**
     * Metodo que se ejecutara cuando se presione el boton de borrar cuenta
     * Este metodo se pasa como onCompleteListener al metodo [reauthenticate]
     * Ya que borrar la cuenta es una accion sensible
     * @param task Resultado de la reautenticacion
     * */
    private fun onDeleteAccount(task: Task<Void>) {
        binding.profileFrame.alpha = 0.3f
        binding.determinateBar.visibility = View.INVISIBLE
        if (task.isSuccessful) {
            Utilities.hideKeyboard(requireActivity(), requireContext())
            val data = ResultOkCancel(
                getString(R.string.delete_account),
                getString(R.string.delete_account_message),
                {
                    // este es la funcion que se ejecutara cuando se haga click sobre el boton ok del dialog
                    binding.determinateBar.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        if (viewModel.deleteUser(homeViewModel.firebaseUser.value!!.uid)) {
                            val path = homeViewModel.currentUser.value?.profilePic?.substring(2)
                            path?.let {
                                homeViewModel.firebaseUser.value?.uid?.let { it1 ->
                                    viewModel.deleteProfilePic(it, it1){}
                                }
                            }
                            homeViewModel.firebaseUser.value?.delete()?.addOnCompleteListener {t->
                                onDeleteAccountComplete(t)
                                binding.determinateBar.visibility = View.INVISIBLE
                            }
                        } else {
                            failedChange(getString(R.string.delete_account_fail))
                        }
                    }
                    it.dismiss()
                },
                {
                    //esto se ejecuta cuando se hace cancel
                    binding.determinateBar.visibility = View.INVISIBLE
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

    /**
     * Metodo que se ejecutara cuando se complete la tarea de borrar la cuenta
     * @param task El resultado de borrar la cuenta si es exitosa, se muestra mensaje de exito, de error si fallo
     * */
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

    /**
     * Metodo que se ejecutara cuando se complete la tarea de cambiar el nombre de usuarios
     * @param it Resultado de la tarea de cambiar el nombre de usuario, si es correcto se muestra mensaje de exito y de error si fallo
     * */
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