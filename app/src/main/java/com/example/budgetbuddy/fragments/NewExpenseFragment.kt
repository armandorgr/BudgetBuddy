package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentNewExpenseBinding
import com.example.budgetbuddy.databinding.FragmentNewGroupBinding
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.NewExpenseViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import java.time.format.DateTimeFormatter

class NewExpenseFragment : Fragment() {
    private val viewModel: NewExpenseViewModel by viewModels()
    private val args: NewExpenseFragmentArgs by navArgs()
    private val viewModelNewGroup: NewGroupViewModel by viewModels()
    private lateinit var selectedGroupUID: String
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private lateinit var binding: FragmentNewExpenseBinding
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewExpenseBinding.inflate(inflater, container, false)
        selectedGroupUID = args.selectedGroupUID
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        homeViewModel.firebaseUser.value?.uid
       // Toast.makeText(requireContext(),selectedGroupUID.toString(),Toast.LENGTH_SHORT).show()
        prepareBinding(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    /**
     * Metodo que sirve para mostrar una ventana emergente con un mensaje de error y un boton.
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
           // findNavController().navigate(NewExpenseFragmentDirections.navNewGroupToGroups(null))
        }
        alertDialogFactory.createDialog(R.layout.success_dialog, binding.root, data)
    }

    /**
     * Metodo que sirve para vincular los eventos de la vista del fragmento [NewExpenseFragment] con los métodos
     * definidos en [NewExpenseViewModel]
     * @param binding Binding generado por el view binding contenedor de las referencias a las vistas
     * */
    private fun prepareBinding(binding: FragmentNewExpenseBinding) {
        binding.date.isClickable = true
        binding.newExpenseTitle.isEnabled = true
        binding.newExpenseAmount.isClickable = true
        binding.date.setOnClickListener {
            viewModel.onStartDateClick(requireContext(), binding.root)
        }

         //Se añaden eventos a los editText para obtener el contenido y validadorlo
        binding.newExpenseTitle.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setNewExpenseTitle(text.toString())
            viewModel.validateNewExpenseTitle(text.toString(), requireContext())
        })

        binding.newExpenseAmount.addTextChangedListener(afterTextChanged = { text ->
            viewModel.setNewExpenseAmount(text.toString().toDouble())
        })

        viewModel.startDate.observe(viewLifecycleOwner) {
            binding.date.text =
                it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }

        viewModel.endDate.observe(viewLifecycleOwner) {
            binding.date.text =
                it?.let { dateFormatter.format(it) } ?: getString(R.string.date_placeholder)
        }

        viewModelNewGroup.loadMembers(selectedGroupUID)

        binding.btnSaveExpense.setOnClickListener {
            if (viewModel.allGood) {
                homeViewModel.firebaseUser.value?.let {firebaseUser ->
                    viewModel.createNewExpense(
                        homeViewModel.firebaseUser.value!!.uid,
                        homeViewModel.currentUser.value?.username!!,
                        selectedGroupUID
                    ) {

                        if (it.isSuccessful) {
                            showSuccessDialog(getString(R.string.expense_create_success))
                        } else {
                            showFailDialog(getString(R.string.expense_create_fail))
                        }
                    }
                }
            }
        }
    }

}