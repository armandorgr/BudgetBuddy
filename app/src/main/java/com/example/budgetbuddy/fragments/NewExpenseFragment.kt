package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
/**
 * Fragmento para la creación de un nuevo gasto.
 * Utiliza ViewModel para manejar la lógica de creación y validación del gasto.
 * Implementa funcionalidades como la selección de fecha y la validación de los campos de entrada.
 * Referencias:
 * ViewModel: https://developer.android.com/topic/libraries/architecture/viewmodel
 * Data Binding: https://developer.android.com/topic/libraries/data-binding
 *
 * @property viewModel ViewModel para la lógica del fragmento.
 * @property args Argumentos pasados al fragmento.
 * @property selectedGroupUID ID del grupo seleccionado.
 * @property dateFormatter Formateador de fecha para el campo de fecha.
 * @property binding Binding generado por ViewBinding.
 * @property homeViewModel ViewModel para la actividad principal.
 * @property showSuccessDialog Método para mostrar un cuadro de diálogo de éxito.
 * @property showFailDialog Método para mostrar un cuadro de diálogo de fracaso.
 * @autor Álvaro Aparicio
 */
@AndroidEntryPoint
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
        homeViewModel.firebaseUser.value?.uid?.let {
        viewModelNewGroup.setCurrentUserUID(it)
        }
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
     * Al hacer click sobre el boton de ok o simplemente fuera de la ventana, se ira al fragmento de [ExpenseFragment]
     * @param message Mensaje a mostrar sobre la ventana de exito
     * */
    private fun showSuccessDialog(message: String) {
        val alertDialogFactory = AlertDialogFactory(requireContext())
        val data = Result(
            getString(R.string.success_title),
            message,
            getString(R.string.ok)
        ) {
            //findNavController().navigate(NewExpenseFragmentDirections.navNewToExpense(selectedGroupUID))
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
            val lista : MutableList<String> = viewModelNewGroup.members.value.map { member -> member.uid }.toMutableList()
            lista.add(homeViewModel.firebaseUser.value!!.uid)
            Log.d("prueba entra metodo",String())
            if (viewModel.allGood) {
                Log.d("PRUEBA", homeViewModel.firebaseUser.value.toString())
                homeViewModel.firebaseUser.value?.let {firebaseUser ->
                    Log.d("prueba", "firebaseuser");
                    viewModel.createNewExpense(
                        homeViewModel.firebaseUser.value!!.uid,
                        homeViewModel.currentUser.value?.username!!,
                        selectedGroupUID,
                        lista
                    ) {
                        Log.d("prueba entra METODO2",String())
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