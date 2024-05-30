package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.MessageAdapter
import com.example.budgetbuddy.databinding.FragmentChatBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.ImageLoader
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.viewHolders.MessageViewHolder
import com.example.budgetbuddy.viewmodels.ChatViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragmento en el cual se podrá enviar mensajes de texto y fotos dentro de un grupo
 * La forma de recoger los datos mediante collect fue consulado en la documentación de Kotlin: https://kotlinlang.org/docs/flow.html#flows
 * La forma de trabajar con el binding fue consulada en la documentación de Android: https://developer.android.com/topic/libraries/view-binding
 * @author Armando Guzmán
 * */
@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var selectedGroupUID: String
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private val imageLoader = ImageLoader(this,
        { uri ->
            viewModel.onSuccessGallery(
                uri,
                selectedGroupUID,
                homeViewModel.firebaseUser.value?.uid!!,
                this::onPhotoMessageComplete
            )
        },
        { bitmap ->
            viewModel.onSuccessCamera(
                bitmap,
                selectedGroupUID,
                homeViewModel.firebaseUser.value?.uid!!,
                this::onPhotoMessageComplete
            )
        },
        { viewModel.onPhotoLoadFail(requireContext()) })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        // La forma de trabajar con argumentos entre fragmento fue consultada en la documentación de Android: https://developer.android.com/guide/navigation/use-graph/pass-data
        selectedGroupUID = args.selectedGroupUID
        viewModel.addMemberShipListener(selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!)
        lifecycleScope.launch {
            viewModel.isMember.collect {
                // se valida que se siga siendo miembro del grupo
                if (!it) {
                    val action = ChatFragmentDirections.navChatToGroups(null)
                    findNavController().navigate(action)
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.kicked_out_of_group_message, ""),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        // Se cargan los mensjes del grupo
        viewModel.loadMessages(selectedGroupUID)
        prepareBinding()
        return binding.root
    }

    /**
     * Objeto anonimo que implementa la interfaz [MessageViewHolder.OnImageClick], cuyo
     * método onClick será llamado al hacer clic sobre un mensaje con foto.
     * */
    private val onImageClick = object : MessageViewHolder.OnImageClick {
        override fun onClick(imgPath: String) {
            val alertDialogFactory = AlertDialogFactory(requireContext())
            alertDialogFactory.createFullScreenPhotoDialog(
                binding.root,
                imgPath,
                ListItemImageLoader(requireContext())
            )
        }
    }

    /**
     * Método que sirva para relacionar los elementos de la vista mediante el [binding]
     * con la lógica definida en el ViewModel
     * */
    private fun prepareBinding() {
        binding.sendImgButton.setOnClickListener(this::onAddPhotoClick)
        binding.sendButton.setOnClickListener {
            viewModel.setMessageText(binding.inputEditText.text.toString().trim())
            val validationResult = viewModel.validateMessage(requireContext())
            // Si el mensaje cumple la validación se envía, si no,  se muestra un Toast
            if (validationResult == null) {
                binding.inputEditText.setText("")
                viewModel.sendMessage(selectedGroupUID, homeViewModel.firebaseUser.value?.uid!!)
            } else {
                Toast.makeText(requireContext(), validationResult, Toast.LENGTH_SHORT).show()
            }
        }
        val messagesAdapter = MessageAdapter(
            homeViewModel.firebaseUser.value?.uid!!,
            layoutInflater,
            ListItemImageLoader(requireContext()),
            requireContext(),
            onImageClick
        )
        binding.chatRecyclerView.adapter = messagesAdapter
        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        // Se recogen los mensajes y se ordenan por fecha de envio antes de cargarlos en el adapter
        lifecycleScope.launch {
            viewModel.messages.collect {
                val orderedList = it.sortedWith { g1, g2 ->
                    (g2.message.sentDate!! - g1.message.sentDate!!).toInt()
                }
                messagesAdapter.setData(orderedList)
            }
        }
    }

    /**
     * Método que se llamará al pulsar sobre el botón de añadir foto
     * Este mostrará una ventana emergente en donde el usuario podrá escoger si cargar la foto desde
     * la galería o la cámara.
     * @param view Vista que lanzó el evento
     * */
    private fun onAddPhotoClick(view: View?) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        alertDialogFactory.createPhotoDialog(
            binding.root,
            { imageLoader.getPhotoFromGallery() },
            { imageLoader.getPhotoFromCamera() })
    }

    /**
     * Método que se llamará al terminar la carga de la foto.
     * Mostando un mensaje informativo en cada caso.
     * @param task Tarea de subir la foto, usada para determinar si fue exitosa o no.
     * */
    private fun onPhotoMessageComplete(task: Task<Void>) {
        if (task.isSuccessful) {
            Toast.makeText(
                requireContext(),
                getString(R.string.photo_load_success),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.photo_load_fail),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}