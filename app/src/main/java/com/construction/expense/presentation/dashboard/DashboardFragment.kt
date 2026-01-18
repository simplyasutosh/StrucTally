package com.construction.expense.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.construction.expense.databinding.FragmentDashboardBinding
import com.construction.expense.domain.model.CategoryExpenseSummary
import com.construction.expense.domain.model.Expense
import com.construction.expense.presentation.dashboard.DashboardUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dashboard Fragment - Home screen showing project overview
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe UI state
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    bind(uiState)
                }
            }
        }
    }

    private fun bind(state: DashboardUiState) {
        // Header
        binding.tvProjectTitle.text = state.selectedProject?.name ?: "My House Construction"
        binding.tvTotalAmount.text = formatRupee(state.totalSpent)
        binding.tvThisMonthAmount.text = formatRupee(state.thisMonthExpenses)
        binding.tvTotalItems.text = state.expenseCount.toString()

        val updatedText = if (state.selectedProject != null) {
            "Updated: ${formatDate(System.currentTimeMillis())}"
        } else {
            "Updated: —"
        }
        binding.tvUpdated.text = updatedText

        // Top categories
        binding.containerTopCategories.removeAllViews()
        val topCats = state.categoryBreakdown.take(4)
        if (topCats.isEmpty()) {
            binding.containerTopCategories.addView(makeMutedRow("No category data yet"))
        } else {
            topCats.forEachIndexed { index, item ->
                binding.containerTopCategories.addView(inflateCategoryRow(index, item, state.totalSpent))
            }
        }

        // Recent expenses
        binding.containerRecentExpenses.removeAllViews()
        val recent = state.recentExpenses.take(3)
        if (recent.isEmpty()) {
            binding.containerRecentExpenses.addView(makeMutedRow("No recent expenses yet"))
        } else {
            recent.forEach { expense ->
                binding.containerRecentExpenses.addView(inflateExpenseRow(expense))
            }
        }
    }

    private fun inflateCategoryRow(index: Int, item: CategoryExpenseSummary, totalSpent: Double): View {
        val v = layoutInflater.inflate(
            com.construction.expense.R.layout.item_category_progress,
            binding.containerTopCategories,
            false
        )
        v.findViewById<TextView>(com.construction.expense.R.id.tv_category_name).text = item.categoryName
        v.findViewById<TextView>(com.construction.expense.R.id.tv_category_amount).text = formatRupee(item.totalSpent)

        // Set progress width percent (roughly matching screenshot bars)
        val pct = if (totalSpent > 0) (item.totalSpent / totalSpent).coerceIn(0.05, 1.0) else 0.1
        val lp = v.findViewById<View>(com.construction.expense.R.id.progress_fill).layoutParams
        if (lp is androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) {
            lp.matchConstraintPercentWidth = pct.toFloat()
            v.findViewById<View>(com.construction.expense.R.id.progress_fill).layoutParams = lp
        }

        // Colorize bar per row
        val fill = v.findViewById<View>(com.construction.expense.R.id.progress_fill)
        val colorRes = when (index % 4) {
            0 -> com.construction.expense.R.color.st_blue_bar
            1 -> com.construction.expense.R.color.st_green_bar
            2 -> com.construction.expense.R.color.st_yellow_bar
            else -> com.construction.expense.R.color.st_purple_bar
        }
        fill.setBackgroundColor(requireContext().getColor(colorRes))

        return v
    }

    private fun inflateExpenseRow(expense: Expense): View {
        val v = layoutInflater.inflate(
            com.construction.expense.R.layout.item_recent_expense,
            binding.containerRecentExpenses,
            false
        )
        v.findViewById<TextView>(com.construction.expense.R.id.tv_vendor).text = expense.vendorName
        val sub = "${expense.subCategoryName.ifBlank { expense.categoryName }} • ${formatDate(expense.date)}"
        v.findViewById<TextView>(com.construction.expense.R.id.tv_subtitle).text = sub
        v.findViewById<TextView>(com.construction.expense.R.id.tv_amount).text =
            "₹${String.format("%,.0f", expense.amount)}"
        return v
    }

    private fun makeMutedRow(text: String): View {
        val tv = TextView(requireContext())
        tv.text = text
        tv.setTextColor(requireContext().getColor(com.construction.expense.R.color.st_on_surface_variant))
        tv.textSize = 14f
        tv.setPadding(4, 8, 4, 8)
        return tv
    }

    private fun formatRupee(amount: Double): String = "₹${String.format("%,.0f", amount)}"

    private fun formatDate(millis: Long): String {
        val fmt = SimpleDateFormat("MMM d", Locale.getDefault())
        return fmt.format(Date(millis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
