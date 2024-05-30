package com.example.budgetbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetbuddy.R


/**
 * A simple [Fragment] subclass.
 * Use the [SaldosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaldosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var currentGroupId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentGroupId = it.getString("currentUserId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saldos, container, false)
    }

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
        fun newInstance(currentGroupId: String)  =
            SaldosFragment().apply {
                arguments = Bundle().apply {
                    putString("currentGroupId", currentGroupId)
                }
            }
    }
}