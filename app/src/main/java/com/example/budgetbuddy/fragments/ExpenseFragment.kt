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

    //
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentGroupId?.let { groupId ->
            expenseViewModel.loadExpenses(groupId)
        }

        expenseViewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }

    }
   //

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SaldosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(currentGroupId: String) =
            ExpenseFragment().apply {
                arguments = Bundle().apply {
                    putString("currentGroupId", currentGroupId)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
