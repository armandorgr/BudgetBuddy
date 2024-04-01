package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.activities.HomeActivity
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.databinding.FragmentNewGroupBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.ImageLoader
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
 * Clase responsable de vincular la logica definida en el ViewModel [NewGroupViewModel] con la vista de [NewGroupFragment]
 * */
@AndroidEntryPoint
class NewGroupFragment : Fragment() {
    private lateinit var binding: FragmentNewGroupBinding
    private val viewModel: NewGroupViewModel by viewModels()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private lateinit var friendsViewModel: FriendsViewModel
    private lateinit var friendsAdapter: NewGroupFriendsAdapter
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        friendsViewModel = ViewModelProvider(requireActivity())[FriendsViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_group,container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        //Se carga el adapter con un lista de los amigos seleccionados
        friendsAdapter = NewGroupFriendsAdapter(layoutInflater, viewModel.getSelectedList(), ListItemImageLoader(requireContext()))
        prepareBinding(binding)
        //se cargan los amigos del usuario actual, con collect cada vez que se actualice la lista, el adapter tambien se
        //actulizara
        lifecycleScope.launch {
            friendsViewModel.friendsUidList.collect{
                friendsAdapter.setData(it)
            }
        }
        return binding.root
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
     * Metodo que sirve para mostrar una ventada emergente con un mensaje de exito
     * */
    private fun showSuccessDialog(){
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.success_title),
            getString(R.string.new_group_success_text),
            getString(R.string.ok)
        ){
            val activity = requireActivity() as HomeActivity
            activity.goToGroups()
        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    /**
     * Metodo que sirve para vincular los eventos de la vista del fragmento [NewGroupFragment] con los metodos
     * definidos en [NewGroupViewModel]
     * @param binding Binding generado por el view binding contenedor de las referencias a las vistas
     * */
    private fun prepareBinding(binding: FragmentNewGroupBinding){
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.friendsRecyclerView.adapter = friendsAdapter
        binding.searchView.setOnQueryTextListener(viewModel.getSearchViewFilter(friendsAdapter))
        binding.startDate.setOnClickListener{
            viewModel.onStartDateClick(requireContext(), binding.root)
        }
        binding.endDate.setOnClickListener{
            viewModel.onEndDateClick(requireContext(), binding.root)
        }
        binding.createGroupBtn.setOnClickListener{
            if(viewModel.allGood){
                if(homeViewModel.firebaseUser.value != null){
                    binding.determinateBar.visibility = View.VISIBLE
                    viewModel.createNewGroup(homeViewModel.firebaseUser.value!!.uid){
                        binding.determinateBar.visibility = View.INVISIBLE
                        if(it.isSuccessful){
                            showSuccessDialog()
                        }else{
                            showFailDialog(getString(R.string.group_create_fail))
                        }
                    }
                }
            }
        }
        //Se añaden eventos a los editText para obtener el contenido y validadorlo
        binding.groupNameEditText.addTextChangedListener(afterTextChanged = {text ->
            viewModel.setGroupName(text.toString())
            viewModel.validateGroupName(text.toString(), requireContext())
        })
        binding.groupDescriptionEditText.addTextChangedListener( afterTextChanged = {text ->
            viewModel.setGroupDescription(text.toString())
            viewModel.validateGroupDescription(text.toString(), requireContext())
        })
        //Se observan las propiedades de fecha del viewmodel, de modo que cada vez que cambien se formatean y se muestran por pantalla
        viewModel.startDate.observe(viewLifecycleOwner){
            binding.startDate.text = it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
        viewModel.endDate.observe(viewLifecycleOwner){
            binding.endDate.text = it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }
    }
}