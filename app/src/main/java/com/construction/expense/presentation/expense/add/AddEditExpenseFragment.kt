package com.construction.expense.presentation.expense.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.construction.expense.R
import com.construction.expense.databinding.FragmentAddEditExpenseBinding
import com.construction.expense.domain.model.PaymentMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        setupToolbar()
        setupDropdowns()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDropdowns() {
        // Setup payment mode dropdown
        val paymentModes = PaymentMode.values().map { it.name }
        val paymentModeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            paymentModes
        )
        binding.paymentModeAutoComplete.setAdapter(paymentModeAdapter)
        binding.paymentModeAutoComplete.setText(PaymentMode.CASH.name, false)
        
        binding.paymentModeAutoComplete.setOnItemClickListener { _, _, position, _ ->
            viewModel.onPaymentModeSelected(PaymentMode.values()[position])
        }

        // Setup category dropdown - ensure it opens when clicked or focused
        binding.categoryAutoComplete.setOnClickListener {
            if (binding.categoryAutoComplete.adapter != null && binding.categoryAutoComplete.adapter!!.count > 0) {
                binding.categoryAutoComplete.showDropDown()
            }
        }
        
        binding.categoryAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.categoryAutoComplete.adapter != null && binding.categoryAutoComplete.adapter!!.count > 0) {
                binding.categoryAutoComplete.showDropDown()
            }
        }
        
        binding.categoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                val categories = viewModel.uiState.value.categories
                if (position < categories.size) {
                    viewModel.onCategorySelected(categories[position].id)
                }
            }
        }

        // Setup text change listeners for other fields
        binding.amountEditText.addTextChangedListener { text ->
            viewModel.updateAmount(text?.toString() ?: "")
        }

        binding.vendorNameEditText.addTextChangedListener { text ->
            viewModel.updateVendorName(text?.toString() ?: "")
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateCategoryDropdown(state)
                }
            }
        }
    }

    private var categoryAdapter: ArrayAdapter<String>? = null
    
    private fun updateCategoryDropdown(state: ExpenseFormState) {
        // Update category dropdown adapter only if categories have changed
        val categoryNames = state.categories.map { it.name }
        
        if (categoryAdapter == null || categoryAdapter?.count != categoryNames.size) {
            categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
            )
            binding.categoryAutoComplete.setAdapter(categoryAdapter)
        } else {
            // Update existing adapter items
            categoryAdapter?.clear()
            categoryAdapter?.addAll(categoryNames)
            categoryAdapter?.notifyDataSetChanged()
        }
        
        // Set selected category text if available
        if (state.categoryName.isNotEmpty()) {
            binding.categoryAutoComplete.setText(state.categoryName, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
