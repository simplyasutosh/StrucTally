package com.construction.expense.presentation.expense.add

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.construction.expense.domain.model.*
import com.construction.expense.domain.usecase.category.GetCategoriesWithSubCategoriesUseCase
import com.construction.expense.domain.usecase.expense.AddExpenseUseCase
import com.construction.expense.domain.usecase.expense.UpdateExpenseUseCase
import com.construction.expense.domain.usecase.expense.AddExpenseResult
import com.construction.expense.domain.usecase.expense.BudgetAlert
import com.construction.expense.domain.usecase.expense.ValidationException
import com.construction.expense.domain.usecase.ocr.ProcessReceiptUseCase
import com.construction.expense.domain.usecase.room.GetActiveRoomsByProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for Add/Edit Expense screen
 *
 * Manages:
 * - Complex form with cascading dropdowns
 * - OCR receipt processing
 * - Budget alert display
 * - Form validation
 * - Add/Edit mode switching
 */
@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val getCategoriesUseCase: GetCategoriesWithSubCategoriesUseCase,
    private val getRoomsUseCase: GetActiveRoomsByProjectUseCase,
    private val processReceiptUseCase: ProcessReceiptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseFormState())
    val uiState: StateFlow<ExpenseFormState> = _uiState.asStateFlow()

    private val projectId: String = savedStateHandle.get<String>("projectId") ?: ""
    private val expenseId: String? = savedStateHandle.get<String>("expenseId")

    init {
        loadFormData()

        if (expenseId != null) {
            // Editing mode - load existing expense
            loadExpense(expenseId)
        } else {
            // Adding mode - set default values
            _uiState.update { it.copy(
                projectId = projectId,
                date = System.currentTimeMillis()
            )}
        }
    }

    /**
     * Load categories and rooms
     */
    private fun loadFormData() {
        viewModelScope.launch {
            combine(
                getCategoriesUseCase(),
                getRoomsUseCase(projectId)
            ) { categoriesWithSubs, rooms ->
                Pair(categoriesWithSubs, rooms)
            }.collect { (categoriesWithSubs, rooms) ->
                _uiState.update { state ->
                    state.copy(
                        categories = categoriesWithSubs.map { it.category },
                        allSubCategories = categoriesWithSubs.associate {
                            it.category.id to it.subCategories
                        },
                        rooms = rooms
                    )
                }
            }
        }
    }

    /**
     * Load existing expense for editing
     */
    private fun loadExpense(expenseId: String) {
        // TODO: Implement when GetExpenseByIdUseCase is available
        // For now, just log
        Timber.d("Loading expense for editing: $expenseId")
    }

    /**
     * Category selected - update subcategories and room visibility
     */
    fun onCategorySelected(categoryId: Int) {
        val category = _uiState.value.categories.find { it.id == categoryId }
        val subCategories = _uiState.value.allSubCategories[categoryId] ?: emptyList()

        _uiState.update { state ->
            state.copy(
                categoryId = categoryId,
                categoryName = category?.name ?: "",
                subCategories = subCategories,
                subCategoryId = "", // Reset
                showRoomField = category?.hasRoomTracking == true,
                roomId = if (category?.hasRoomTracking != true) null else state.roomId,
                errors = state.errors - "categoryId"
            )
        }
    }

    /**
     * Subcategory selected
     */
    fun onSubCategorySelected(subCategoryId: String) {
        val subCategory = _uiState.value.subCategories.find { it.id == subCategoryId }

        _uiState.update { state ->
            state.copy(
                subCategoryId = subCategoryId,
                subCategoryName = subCategory?.name ?: "",
                errors = state.errors - "subCategoryId"
            )
        }
    }

    /**
     * Room selected
     */
    fun onRoomSelected(roomId: Int) {
        val room = _uiState.value.rooms.find { it.id == roomId }

        _uiState.update { state ->
            state.copy(
                roomId = roomId,
                roomName = room?.name,
                errors = state.errors - "roomId"
            )
        }
    }

    /**
     * Payment mode selected
     */
    fun onPaymentModeSelected(paymentMode: PaymentMode) {
        _uiState.update { it.copy(paymentMode = paymentMode) }
    }

    /**
     * Date selected
     */
    fun onDateSelected(dateMillis: Long) {
        _uiState.update { it.copy(
            date = dateMillis,
            errors = it.errors - "date"
        )}
    }

    /**
     * Process receipt with OCR
     */
    fun processReceipt(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingOCR = true) }

            processReceiptUseCase(imageUri)
                .onSuccess { receiptData ->
                    // Populate form with OCR data
                    _uiState.update { state ->
                        state.copy(
                            amount = receiptData.amount?.toString() ?: state.amount,
                            date = receiptData.date ?: state.date,
                            vendorName = receiptData.vendorName ?: state.vendorName,
                            invoiceNumber = receiptData.invoiceNumber ?: state.invoiceNumber,
                            gstAmount = receiptData.gstAmount?.toString() ?: state.gstAmount,
                            vendorGst = receiptData.gstNumber ?: state.vendorGst,
                            receiptUri = imageUri,
                            ocrConfidence = receiptData.confidence,
                            isProcessingOCR = false
                        )
                    }

                    Timber.d("OCR completed with confidence: ${receiptData.confidence}")
                }
                .onFailure { e ->
                    Timber.e(e, "OCR failed")
                    _uiState.update { it.copy(
                        isProcessingOCR = false,
                        receiptUri = imageUri // Still save the image
                    )}
                }
        }
    }

    /**
     * Validate and save expense
     */
    fun saveExpense() {
        viewModelScope.launch {
            val state = _uiState.value

            // Create expense model
            val expense = Expense(
                id = expenseId ?: UUID.randomUUID().toString(),
                projectId = state.projectId,
                date = state.date,
                categoryId = state.categoryId,
                categoryName = state.categoryName,
                subCategoryId = state.subCategoryId,
                subCategoryName = state.subCategoryName,
                roomId = state.roomId,
                roomName = state.roomName,
                milestoneId = state.milestoneId,
                milestoneName = null,
                amount = state.amount.toDoubleOrNull() ?: 0.0,
                vendorName = state.vendorName,
                vendorContact = state.vendorContact,
                paymentMode = state.paymentMode,
                transactionId = state.transactionId,
                invoiceNumber = state.invoiceNumber,
                gstAmount = state.gstAmount?.toDoubleOrNull(),
                vendorGst = state.vendorGst,
                notes = state.notes,
                receiptUrl = state.receiptUri?.toString(),
                receiptThumbnailUrl = null,
                isSynced = false,
                createdDate = System.currentTimeMillis(),
                modifiedDate = System.currentTimeMillis(),
                status = ExpenseStatus.ACTIVE
            )

            _uiState.update { it.copy(isSaving = true) }

            val result = if (expenseId != null) {
                updateExpenseUseCase(expense)
            } else {
                addExpenseUseCase(expense)
            }

            result
                .onSuccess { addResult ->
                    _uiState.update { state ->
                        state.copy(
                            isSaving = false,
                            budgetAlerts = (addResult as? AddExpenseResult)?.budgetAlerts ?: emptyList(),
                            saveSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    val errors = if (e is ValidationException) {
                        e.errors
                    } else {
                        mapOf("general" to (e.message ?: "Unknown error"))
                    }

                    _uiState.update { state ->
                        state.copy(
                            isSaving = false,
                            errors = errors
                        )
                    }
                }
        }
    }

    /**
     * Dismiss budget alerts
     */
    fun dismissBudgetAlerts() {
        _uiState.update { it.copy(budgetAlerts = emptyList()) }
    }

    /**
     * Clear form (reset to defaults)
     */
    fun clearForm() {
        _uiState.update { ExpenseFormState(
            projectId = projectId,
            date = System.currentTimeMillis(),
            categories = it.categories,
            allSubCategories = it.allSubCategories,
            rooms = it.rooms
        )}
    }

    /**
     * Clear save success flag
     */
    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    // ===== FIELD UPDATE METHODS =====

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount, errors = it.errors - "amount") }
    }

    fun updateVendorName(name: String) {
        _uiState.update { it.copy(vendorName = name, errors = it.errors - "vendorName") }
    }

    fun updateVendorContact(contact: String) {
        _uiState.update { it.copy(vendorContact = contact.ifBlank { null }) }
    }

    fun updateTransactionId(transactionId: String) {
        _uiState.update { it.copy(transactionId = transactionId.ifBlank { null }) }
    }

    fun updateInvoiceNumber(invoiceNumber: String) {
        _uiState.update { it.copy(invoiceNumber = invoiceNumber.ifBlank { null }) }
    }

    fun updateGstAmount(gstAmount: String) {
        _uiState.update { it.copy(
            gstAmount = gstAmount.ifBlank { null },
            errors = it.errors - "gstAmount"
        )}
    }

    fun updateVendorGst(vendorGst: String) {
        _uiState.update { it.copy(
            vendorGst = vendorGst.ifBlank { null },
            errors = it.errors - "vendorGst"
        )}
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(
            notes = notes.ifBlank { null },
            errors = it.errors - "notes"
        )}
    }
}

/**
 * Expense form state
 */
data class ExpenseFormState(
    val projectId: String = "",
    val date: Long = System.currentTimeMillis(),
    val categoryId: Int = 0,
    val categoryName: String = "",
    val subCategoryId: String = "",
    val subCategoryName: String = "",
    val roomId: Int? = null,
    val roomName: String? = null,
    val milestoneId: Int? = null,
    val amount: String = "",
    val vendorName: String = "",
    val vendorContact: String? = null,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val transactionId: String? = null,
    val invoiceNumber: String? = null,
    val gstAmount: String? = null,
    val vendorGst: String? = null,
    val notes: String? = null,
    val receiptUri: Uri? = null,

    // UI state
    val categories: List<Category> = emptyList(),
    val subCategories: List<SubCategory> = emptyList(),
    val allSubCategories: Map<Int, List<SubCategory>> = emptyMap(),
    val rooms: List<RoomModel> = emptyList(),
    val showRoomField: Boolean = false,
    val isProcessingOCR: Boolean = false,
    val isSaving: Boolean = false,
    val ocrConfidence: Float? = null,
    val budgetAlerts: List<BudgetAlert> = emptyList(),
    val errors: Map<String, String> = emptyMap(),
    val saveSuccess: Boolean = false
)
