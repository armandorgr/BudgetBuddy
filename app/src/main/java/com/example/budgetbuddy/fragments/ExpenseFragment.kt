package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.ExpenseAdapter
import com.example.budgetbuddy.databinding.FragmentExpenseBinding
import com.example.budgetbuddy.databinding.FragmentFriendsBinding
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.viewmodels.ExpenseViewModel
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
/**
 * Fragmento que muestra una lista de gastos.
 * Este fragmento utiliza un RecyclerView junto con ExpenseAdapter para mostrar la lista de gastos.
 * La implementación del RecyclerView sigue las recomendaciones de la documentación oficial de Android:
 * [RecyclerView | Android Developers](https://developer.android.com/guide/topics/ui/layout/recyclerview)
 * @property expenseAdapter Adaptador para el RecyclerView que muestra la lista de gastos.
 * @property expenseViewModel ViewModel para manejar la lógica de los gastos.
 * @property _binding Referencia mutable al binding del fragmento.
 * @property currentGroupId ID del grupo actual.
 * @property binding Binding generado por ViewBinding.
 * @property homeViewModel ViewModel para la actividad principal.
 * @author Álvaro Aparicio
 */
class ExpenseFragment : Fragment() {

    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private var _binding: FragmentExpenseBinding? = null
    private var currentGroupId: String? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentGroupId = it.getString("currentGroupId")
        }
    }
    /**
     * Se llama cuando se crea la vista del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        val recyclerView = binding.expenseRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //
        expenseAdapter = ExpenseAdapter(emptyList())
        recyclerView.adapter= expenseAdapter
        //
        binding.floatingBtn2.setOnClickListener{
         currentGroupId?.let {
            val action = ExpenseFragmentDirections.navToNewExpense(it)
            findNavController().navigate(action)
        }
        }
        return binding.root
    }

    /**
     * Se llama cuando la vista del fragmento está completamente creada.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentGroupId?.let { groupId ->
            expenseViewModel.loadExpenses(groupId)
        }

        expenseViewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }

    }


    /**
     * Método estático para crear una nueva instancia de ExpenseFragment.
     * @param currentGroupId ID del grupo actual.
     */
    companion object {

        @JvmStatic
        fun newInstance(currentGroupId: String) =
            ExpenseFragment().apply {
                arguments = Bundle().apply {
                    putString("currentGroupId", currentGroupId)
                }
            }
    }

    /**
     * Se llama cuando la vista del fragmento está a punto de ser destruida.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
