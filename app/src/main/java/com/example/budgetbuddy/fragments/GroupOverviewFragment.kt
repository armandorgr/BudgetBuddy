package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.databinding.FragmentNewGroupBinding
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Clase del fragmento que sirve para cargar los datos de un grupo, asi como actualizar sus datos y eliminarlo
 * */
@AndroidEntryPoint
class GroupOverviewFragment : Fragment() {
    private var _binding: FragmentNewGroupBinding? = null

    /**
     * Argumentos pasados al fragmento al hacer click sobre un Grupo cargados del usuario
     * en el fragmento [GroupsFragment]
     * */
    private val args: GroupOverviewFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private val viewModel: NewGroupViewModel by viewModels()
    private lateinit var friendsViewModel:FriendsViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var selectedGroup: Group
    private lateinit var selectedGroupUID: String
    private lateinit var friendsAdapter: NewGroupFriendsAdapter
    private lateinit var membersAdapter: NewGroupFriendsAdapter
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendsViewModel = ViewModelProvider(requireActivity())[FriendsViewModel::class.java]
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        // Se recogen los argumentos pasados desde el fragmento de Grupos
        selectedGroup = args.selectedGroup
        selectedGroupUID = args.selectedGroupUID

        homeViewModel.firebaseUser.value?.uid?.let { viewModel.setCurrentUserUID(it) }
        //Se cargn los miembros del grupo cargado
        selectedGroupUID.let { viewModel.loadMembers(it) }
        friendsAdapter = NewGroupFriendsAdapter(inflater, viewModel.getSelectedList())
        membersAdapter = NewGroupFriendsAdapter(inflater, viewModel.getSelectedList())
        prepareBinding()
        //Se cargan en el Adapter los miembros del grupo cargado
        //Al hacer collect cada vez que se cambie la lista, se ejecuta el codigo
        // que lo pasa al Adapter y la lista de actualiza
        lifecycleScope.launch {
                viewModel.members.collect {
                    friendsAdapter.setData(it)
                }
        }
        lifecycleScope.launch {
            friendsViewModel.friendsUidList.collect{
                val filteredList = it.toMutableList().apply {
                    removeIf { item -> selectedGroup.members?.contains((item as ListItemUiModel.User).uid) ?: false}
                    map { item -> (item as ListItemUiModel.User).selected = false }
                }
                membersAdapter.setData(filteredList)
            }
        }
        return binding.root
    }

    /**
     * Metodo que sirve para mostrar una ventana emergent con un mensaje de exito y un boton.
     * Al hacer click sobre el boton de ok o simplemente fuera de la ventana, se ira al fragmento de [GroupsFragment]
     * @param message Mensaje a mostrar sobre la ventana de exito
     * */
    private fun showSuccessDialog(message: String) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.success_title),
            message,
            getString(R.string.ok)
        ) {
            findNavController().navigate(R.id.nav_groups)
        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    /**
     * Metodo que sirve para mostrar una ventana emergent con un mensaje de error y un boton.
     * Al hacer click sobre el boton de ok o simplemente fuera de la ventana, no se hara nada mas que cerrar la ventana
     * @param message Mensaje a mostrar sobre la ventana de error
     * */
    private fun showFailDialog(message: String) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.fail_title),
            message,
            getString(R.string.try_again)
        ) {

        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    /**
     * Metodo que se ejecutara cuando la actualizacion del grupo se complete, si la tarea tiene exito
     * se muestra una venta de exito y si no, una de error.
     * @param task Tarea devuelta por el metodo del repositorio al intentar actualizar el grupo
     * */
    private fun onGroupUpdateComplete(task: Task<Void>) {
        if (task.isSuccessful) {
            showSuccessDialog(getString(R.string.group_update_success))
        } else {
            showFailDialog(getString(R.string.group_update_fail))
        }
    }

    /**
     * Metodo que se ejecutara cuando la eliminacion del grupo se complete, si la tarea tiene exito
     * se muestra una venta de exito y si no, una de error.
     * @param task Tarea devuelta por el metodo del repositorio al intentar eliminar el grupo
     * */
    private fun onGroupDeleteComplete(task: Task<Void>) {
        if (task.isSuccessful) {
            showSuccessDialog(getString(R.string.group_delete_success))
        } else {
            showFailDialog(getString(R.string.group_delete_fail))
        }
    }

    /**
     * Metodo que sirve para vincular la vista con la logica del [NewGroupViewModel]
     * */
    private fun prepareBinding() {
        binding.title.text = getString(R.string.group_overview_title)
        binding.friendsTitle.text = getString(R.string.members_text)
        binding.membersLinearLayout.visibility = View.VISIBLE
        binding.membersTitle.visibility = View.VISIBLE

        binding.leaveGroup.visibility = View.VISIBLE
        binding.membersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.adapter = friendsAdapter
        binding.membersRecyclerView.adapter = membersAdapter

        binding.createGroupBtn.visibility = View.GONE //Se esconde el boton de crear grupo
        //Se hace visible el boton de borrar grupo
        binding.deleteGroupBtn.apply {
            visibility = View.VISIBLE
            //Se añade evento en caso de click para intentar borrar el grupo
            setOnClickListener {
                viewModel.deleteGroup(selectedGroupUID) {
                    onGroupDeleteComplete(it)
                }
            }
        }
        //Se hace visible el boton de actualizar
        binding.updateGroupBtn.apply {
            visibility = View.VISIBLE
            //Se añade evento en caso de click para intentar actualizar el grupo
            setOnClickListener {
                if (viewModel.allGood) {
                    viewModel.updateGroup(selectedGroupUID) {
                        onGroupUpdateComplete(it)
                    }
                }
            }
        }

        //Se cargan los datos del grupo cargado en la vista
        viewModel.setGroupName(selectedGroup.name.toString())
        viewModel.setGroupDescription(selectedGroup.description.toString())
        // Se observan las propiedades de startDate y endDate para que cada vez que se
        //cambien estas se formateen y se muestren en la vista
        viewModel.startDate.observe(viewLifecycleOwner) {
            binding.startDate.text =
                it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
        viewModel.endDate.observe(viewLifecycleOwner) {
            binding.endDate.text =
                it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
        //Se cargan las fechas del grupo seleccionado en la vista
        viewModel.setStartDate(LocalDateTime.parse(selectedGroup.startDate))
        viewModel.setEndDate(LocalDateTime.parse(selectedGroup.endDate))

        binding.searchView.setOnQueryTextListener(viewModel.getSearchViewFilter(friendsAdapter))
        binding.searchViewMembers.setOnQueryTextListener(viewModel.getSearchViewFilter(membersAdapter))

        //Se añaden eventos en caso de que el usuario quiera cambiar y los datos y validar estos antes de actualizar
        binding.startDate.setOnClickListener {
            viewModel.onStartDateClick(requireContext(), binding.root)
        }
        binding.endDate.setOnClickListener {
            viewModel.onEndDateClick(requireContext(), binding.root)
        }
        binding.groupNameEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setGroupName(text.toString())
            viewModel.validateGroupName(text.toString(), requireContext())
        })
        binding.groupDescriptionEditText.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setGroupDescription(text.toString())
            viewModel.validateGroupDescription(text.toString(), requireContext())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Se borra el bidindin al destruirse la vista para evitar fugas de informacion
        _binding = null
    }
}