package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.ImageLoader
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.util.PickerData
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val OSCURE = 0.3f
private const val NORMAL = 1f

/**
 * Clase del fragmento que sirve para cargar los datos de un grupo,
 * asi como actualizar sus datos y eliminarlo.
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
    private lateinit var friendsViewModel: FriendsViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var selectedGroup: Group
    private lateinit var selectedGroupUID: String
    private var deletingGroup = false
    private var leavingGroup = false

    //TODO CAMBIAR NOMBRES INVERTIDOS DE friends y members
    private lateinit var friendsAdapter: NewGroupFriendsAdapter
    private lateinit var membersAdapter: NewGroupFriendsAdapter
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val imageLoader = ImageLoader(this,
        { uri -> viewModel.onSuccessGallery(uri, requireContext(), binding.groupPic) },
        { bitmap -> viewModel.onSuccessCamera(bitmap, requireContext(), binding.groupPic) },
        { viewModel.onPhotoLoadFail(requireContext()) })


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

        friendsAdapter = NewGroupFriendsAdapter(
            requireContext(),
            inflater,
            viewModel.getSelectedList(),
            ListItemImageLoader(requireContext()),
            onChangeRoleListener
        )
        membersAdapter = NewGroupFriendsAdapter(
            requireContext(),
            inflater,
            viewModel.getSelectedList(),
            ListItemImageLoader(requireContext())
        )
        prepareBinding()
        //Se cargan en el Adapter los miembros del grupo cargado
        //Al hacer collect cada vez que se cambie la lista, se ejecuta el codigo
        // que lo pasa al Adapter y la lista de actualiza
        lifecycleScope.launch {
            launch {
                viewModel.members.collect {
                    deleteMembersInFriends(it)
                    friendsAdapter.setData(it)
                    addFriendsAreNoMembers(it)
                }
            }
            launch {
                friendsViewModel.friendsUidList.collect {
                    val filteredList = it.toMutableList().apply {
                        removeIf { item ->
                            require(item is ListItemUiModel.User)
                            viewModel.members.value.any { member -> member.uid == item.uid }
                        }
                        map { item -> (item as ListItemUiModel.User).selected = false }
                    }
                    membersAdapter.setData(filteredList)
                    viewModel.cleanSelectedList(filteredList)
                }
            }
        }
        viewModel.setOnCurrentUserBanned {
            try {
                if (!deletingGroup && !leavingGroup) {
                    findNavController().navigate(
                        GroupOverviewFragmentDirections.navGroupOverviewToGroups(
                            null
                        )
                    )
                }
            } catch (e: IllegalStateException) {
                Log.d("prueba", "illegal state exception")
            }
        }
        return binding.root
    }

    private fun addFriendsAreNoMembers(members: List<ListItemUiModel.User>) {
        val filteredList = friendsViewModel.friendsUidList.value.toMutableList().apply {
            removeIf { item ->
                require(item is ListItemUiModel.User)
                members.any { member -> member.uid == item.uid }
            }
            map { item -> (item as ListItemUiModel.User).selected = false }
        }
        membersAdapter.setData(filteredList)
    }

    private fun deleteMembersInFriends(members: List<ListItemUiModel>) {
        for (i in members) {
            require(i is ListItemUiModel.User)
            membersAdapter.removeItem(i)
        }
    }

    //TODO POER ABAJO DEL TODO
    private val onChangeRoleListener = object : NewGroupFriendsAdapter.OnChangeRoleListener {
        override fun onChangeRole(user: ListItemUiModel.User, position: Int) {
            showRoleSpinnerDialog(user)
        }
    }

    private fun showRoleSpinnerDialog(user: ListItemUiModel.User) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val rolesArray = Utilities.ROLES_LIST.map { role -> getString(role.resourceID) }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            rolesArray
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val data = PickerData(
            getString(R.string.change_role_title, user.userUiModel.username),
            adapter,
            { dialog, valueSelected ->
                val role = Utilities.ROLES_LIST.first { role -> getString(role.resourceID) == valueSelected }
                viewModel.changeMemberRole(selectedGroupUID, user.uid, role, requireContext())
                dialog.dismiss()
                binding.frame.alpha =
                    NORMAL //Hacer que la pantalla vuelva a la normalidad al cerrar la ventana.
            }
        ) {
            binding.frame.alpha = NORMAL
        }
        binding.frame.alpha = OSCURE // Oscurecer la imagen al mostrar la ventana.
        alertDialogFactory.createPickerDialog(binding.root, data)
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
            findNavController().navigate(
                GroupOverviewFragmentDirections.navGroupOverviewToGroups(
                    null
                )
            )
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
        ) {}
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    /**
     * Metodo que se ejecutara cuando la actualizacion del grupo se complete, si la tarea tiene exito
     * se muestra una venta de exito y si no, una de error.
     * @param task Tarea devuelta por el metodo del repositorio al intentar actualizar el grupo
     * */
    private fun onGroupUpdateComplete(task: Task<Void>) {
        binding.determinateBar.visibility = View.GONE
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
            deletingGroup = false
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
        binding.membersRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.adapter = friendsAdapter
        binding.membersRecyclerView.adapter = membersAdapter

        binding.groupPic.setOnClickListener(this::onAddPhotoClick)

        selectedGroup.pic?.let {
            ListItemImageLoader(requireContext()).loadImage(it, binding.groupPic) { uri ->
                viewModel.setGroupPhoto(uri)
            }
        }

        binding.createGroupBtn.visibility = View.GONE //Se esconde el boton de crear grupo
        //Se hace visible el boton de borrar grupo
        binding.deleteGroupBtn.apply {
            visibility = View.VISIBLE
            //Se añade evento en caso de click para intentar borrar el grupo
            setOnClickListener {
                deletingGroup = true
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
                    binding.determinateBar.visibility = View.VISIBLE
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
        binding.searchViewMembers.setOnQueryTextListener(
            viewModel.getSearchViewFilter(
                membersAdapter
            )
        )

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
        binding.leaveGroup.setOnClickListener(this::onLeaveGroupClick)

        lifecycleScope.launch {
            viewModel.currentUserRole.collect { role ->
                if (role == ROLE.ADMIN) {
                    friendsAdapter.setEditable(true)
                    binding.deleteGroupBtn.visibility = View.VISIBLE
                    binding.groupNameEditText.isEnabled = true
                    binding.groupDescriptionEditText.isEnabled = true
                    binding.startDate.isClickable = true
                    binding.endDate.isClickable = true
                    binding.groupPic.isClickable = true
                } else {
                    Log.d("prueba", "Rol cambiado a Member")
                    friendsAdapter.setEditable(false)
                    binding.deleteGroupBtn.visibility = View.GONE
                    binding.groupNameEditText.isEnabled = false
                    binding.groupDescriptionEditText.isEnabled = false
                    binding.startDate.isClickable = false
                    binding.endDate.isClickable = false
                    binding.groupPic.isClickable = false
                }
            }
        }
    }

    private fun onLeaveGroupClick(view: View?) {
        leavingGroup = true
        viewModel.leaveGroup(selectedGroupUID) {
            if (it.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.leave_group_success),
                    Toast.LENGTH_SHORT
                ).show()
                val action = GroupOverviewFragmentDirections.navGroupOverviewToGroups(null)
                findNavController().navigate(action)
            } else {
                leavingGroup = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.leave_group_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun onAddPhotoClick(view: View?) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val onDelete = if (viewModel.getGroupPhoto() != null) { ->
            viewModel.onDeletePhoto(
                requireContext(),
                binding.groupPic
            )
        } else null
        alertDialogFactory.createPhotoDialog(
            binding.root,
            { imageLoader.getPhotoFromGallery() },
            { imageLoader.getPhotoFromCamera() },
            onDelete
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Se borra el bidindin al destruirse la vista para evitar fugas de informacion
        _binding = null
    }
}