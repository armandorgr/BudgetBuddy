package com.example.budgetbuddy.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.FriendsAdapter
import com.example.budgetbuddy.databinding.FragmentFriendsBinding
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.InvitationsViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.android.gms.tasks.Tasks


@AndroidEntryPoint
class FriendsFragment : Fragment() {
    private val friendsViewModel: FriendsViewModel by viewModels()
    private var _binding: FragmentFriendsBinding? = null
    private val viewModel: InvitationsViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private var alertDialog: AlertDialog? = null
    private val userRepository: UsersRepository = UsersRepository()
    private val usersRef: String = "users"
    private val invitationsRef: String = "invitations"

    private lateinit var membersAdapter: FriendsAdapter
    private val _members: MutableStateFlow<List<ListItemUiModel.User>> =
        MutableStateFlow(emptyList())
    val members: StateFlow<List<ListItemUiModel.User>> = _members
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageLoader = ListItemImageLoader(requireContext())
        // Se inicializa el adaptador
        membersAdapter = FriendsAdapter(layoutInflater,imageLoader)
        binding.recyclerView.adapter = membersAdapter

      // Configuración del click listener para el botón "Añadir amigo" del "fragment_friends.xml"
        binding.button.setOnClickListener {
            showAddFriendDialog()
        }

        // Se observa el cambio de texto del SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { membersAdapter.filter(it) }
                return true
            }
        })
        lifecycleScope.launch {
            friendsViewModel.friendsUidList.collect { friendsList ->
                // Se filtra la lista de amigos para incluir solo amigos
                val friends = friendsList.filterIsInstance<ListItemUiModel.User>()
                // Se actualizan los datos del adaptador con la lista de amigos
                membersAdapter.setData(friends)
            }
        }
    }

    private fun showAddFriendDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_prompt_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(getString(R.string.empty))

        // Configuración de botones OK y Cancel
        alertDialogBuilder.setPositiveButton(R.string.ok) { dialog, which ->
            val recipientUsername = dialogView.findViewById<EditText>(R.id.newEditText).text.toString()
            println(recipientUsername.toString())
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            println(currentUserUid.toString())
            if (currentUserUid != null) {
                sendFriendRequest(currentUserUid, recipientUsername) { task ->
                    if (task.isSuccessful) {
                        // La solicitud de amistad se envió correctamente
                        Toast.makeText(requireContext(), "Solicitud de amistad enviada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        // Error al enviar la solicitud de amistad
                        Toast.makeText(requireContext(), "Error al enviar la solicitud de amistad", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { dialog, which ->
            // Lógica para manejar la acción Cancel
            dialog.dismiss()
        }
        val titleTextView = dialogView.findViewById<TextView>(R.id.alertDialogTitle)
        titleTextView.text = getString(R.string.añadirAmigo)
        // Crear y mostrar el cuadro de diálogo
        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }


    fun sendFriendRequest(senderUid: String, recipientUsername: String, onComplete: (task: Task<Void>) -> Unit) {
        lifecycleScope.launch {
            try {
                // Buscar el UID del destinatario por su nombre de usuario
                userRepository.findUIDByUsername(recipientUsername){UID->
                    Log.d("prueba","El uid es: " + UID)
                    if(UID!=null){
                        val invitation = InvitationUiModel(
                            senderUid = senderUid,
                            senderName = "",
                            type = INVITATION_TYPE.FRIEND_REQUEST,
                            dateSent = LocalDateTime.now().toString()
                        )

                        // Actualizar la base de datos de Firebase con las nuevas invitaciones
                        friendsViewModel.sendInvitation(invitation, UID){
                            if(it.isSuccessful){
                                println("Enviada correctamente!")
                            }else{
                                println("La invitacion no se ha podido enviar!")
                            }
                        }

                    }
                }

            } catch (e: Exception) {
                // Manejar cualquier excepción que pueda ocurrir
                val exception = Exception("Ocurrió un error: ${e.message}")
                onComplete(Tasks.forException(exception))
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
