package com.construction.expense.presentation.expense.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.construction.expense.databinding.FragmentAddEditExpenseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditExpenseFragment : Fragment() {

    private var _binding: FragmentAddEditExpenseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditExpenseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        // UI is intentionally focused on modern look/feel; wiring to ViewModel can be layered in next.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

