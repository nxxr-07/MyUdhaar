package com.nxxr.myudhaar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxxr.myudhaar.data.model.Person
import com.nxxr.myudhaar.data.model.Supplier
import com.nxxr.myudhaar.data.model.Transaction
import com.nxxr.myudhaar.data.repository.MainRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val repository: MainRepository
) : ViewModel() {

    // Persons
    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons.asStateFlow()

    // Suppliers
    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    // Recent entries
    data class RecentEntry(val partyName: String, val transaction: Transaction)

    private val _recentPersonEntries = MutableStateFlow<List<RecentEntry>>(emptyList())
    val recentPersonEntries: StateFlow<List<RecentEntry>> = _recentPersonEntries.asStateFlow()

    private val _recentSupplierEntries = MutableStateFlow<List<RecentEntry>>(emptyList())
    val recentSupplierEntries: StateFlow<List<RecentEntry>> = _recentSupplierEntries.asStateFlow()

    // Popup state
    private val _isPopupOpen = MutableStateFlow(false)
    val isPopupOpen: StateFlow<Boolean> = _isPopupOpen.asStateFlow()

    // Form state
    private val _selectedPersonId = MutableStateFlow<String?>(null)
    val selectedPersonId: StateFlow<String?> = _selectedPersonId.asStateFlow()

    private val _selectedSupplierId = MutableStateFlow<String?>(null)
    val selectedSupplierId: StateFlow<String?> = _selectedSupplierId.asStateFlow()

    private val _transactionAmount = MutableStateFlow("")
    val transactionAmount: StateFlow<String> = _transactionAmount.asStateFlow()

    private val _transactionDesc = MutableStateFlow("")
    val transactionDesc: StateFlow<String> = _transactionDesc.asStateFlow()

    private val _transactionIsCredit = MutableStateFlow(true)
    val transactionIsCredit: StateFlow<Boolean> = _transactionIsCredit.asStateFlow()

    // Udhaar totals for persons
    private val _personTotal = MutableStateFlow(0.0)
    val personTotal: StateFlow<Double> = _personTotal.asStateFlow()
    private val _personToday = MutableStateFlow(0.0)
    val personToday: StateFlow<Double> = _personToday.asStateFlow()

    // Udhaar totals for suppliers
    private val _supplierTotal = MutableStateFlow(0.0)
    val supplierTotal: StateFlow<Double> = _supplierTotal.asStateFlow()
    private val _supplierToday = MutableStateFlow(0.0)
    val supplierToday: StateFlow<Double> = _supplierToday.asStateFlow()

    // UI events
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            // Load persons
            repository.fetchAllPersons().onSuccess { personsList ->
                _persons.value = personsList
                refreshPersonTransactions(personsList)
            }.onFailure {
                Log.e("HomeViewModel", "Failed to load persons", it)
                _uiEvent.emit(UiEvent.ShowError("Failed to load persons"))
            }
            // Load suppliers
            repository.fetchAllSuppliers().onSuccess { suppliersList ->
                _suppliers.value = suppliersList
                refreshSupplierTransactions(suppliersList)
            }.onFailure {
                Log.e("HomeViewModel", "Failed to load suppliers", it)
                _uiEvent.emit(UiEvent.ShowError("Failed to load suppliers"))
            }
        }
    }

    private suspend fun refreshPersonTransactions(personsList: List<Person>) {
        val entries = mutableListOf<RecentEntry>()
        for (p in personsList) {
            repository.fetchTransactions(p.id).onSuccess { txs ->
                txs.forEach { entries.add(RecentEntry(p.name, it)) }
            }
        }
        val sorted = entries.sortedByDescending { it.transaction.timestamp }
        _recentPersonEntries.value = sorted
        computeTotals(sorted, forPerson = true)
    }

    private suspend fun refreshSupplierTransactions(suppliersList: List<Supplier>) {
        val entries = mutableListOf<RecentEntry>()
        for (s in suppliersList) {
            repository.fetchSupplierTransactions(s.id).onSuccess { txs ->
                txs.forEach { entries.add(RecentEntry(s.name, it)) }
            }
        }
        val sorted = entries.sortedByDescending { it.transaction.timestamp }
        _recentSupplierEntries.value = sorted
        computeTotals(sorted, forPerson = false)
    }

    private fun computeTotals(entries: List<RecentEntry>, forPerson: Boolean) {
        val now = System.currentTimeMillis()
        val startOfDay = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val total = entries.sumOf { if (it.transaction.isCredit) it.transaction.amount else -it.transaction.amount }
        val today = entries.filter { it.transaction.timestamp >= startOfDay }
            .sumOf { if (it.transaction.isCredit) it.transaction.amount else -it.transaction.amount }

        if (forPerson) {
            _personTotal.value = total
            _personToday.value = today
        } else {
            _supplierTotal.value = total
            _supplierToday.value = today
        }
    }

    fun openPopup(isCredit: Boolean) {
        _isPopupOpen.value = true
        _transactionIsCredit.value = isCredit
    }

    fun closePopup() {
        _isPopupOpen.value = false
        resetForm()
    }

    private fun resetForm() {
        _selectedPersonId.value = null
        _selectedSupplierId.value = null
        _transactionAmount.value = ""
        _transactionDesc.value = ""
    }

    fun onPersonSelected(id: String) { _selectedPersonId.value = id }
    fun onSupplierSelected(id: String) { _selectedSupplierId.value = id }
    fun onAmountChange(v: String) { _transactionAmount.value = v }
    fun onDescChange(v: String) { _transactionDesc.value = v }

    /** Submit only transaction — person must already exist */
    fun submitPersonTransaction() {
        viewModelScope.launch {
            val personId = _selectedPersonId.value
            val amount = _transactionAmount.value.toDoubleOrNull()
            val desc = _transactionDesc.value.trim()
            val isCredit = _transactionIsCredit.value

            when {
                personId.isNullOrEmpty() -> {
                    _uiEvent.emit(UiEvent.ShowError("Select a person"))
                }
                amount == null || amount <= 0 -> {
                    _uiEvent.emit(UiEvent.ShowError("Enter a valid amount"))
                }
                else -> {
                    val transaction = Transaction(
                        personId = personId,
                        amount = amount,
                        description = desc,
                        isCredit = isCredit,
                        timestamp = System.currentTimeMillis()
                    )

                    repository.addTransaction(personId, transaction).onSuccess {
                        _uiEvent.emit(UiEvent.ShowSuccess)
                        closePopup()
                        refreshData()
                    }.onFailure {
                        _uiEvent.emit(UiEvent.ShowError("Failed to add transaction"))
                    }
                }
            }
        }
    }

    fun submitSupplierTransaction() {
        viewModelScope.launch {
            val supplierId = _selectedSupplierId.value
            val amount = _transactionAmount.value.toDoubleOrNull()
            val desc = _transactionDesc.value.trim()
            val isCredit = _transactionIsCredit.value

            when {
                supplierId.isNullOrEmpty() -> {
                    _uiEvent.emit(UiEvent.ShowError("Select a person"))
                }
                amount == null || amount <= 0 -> {
                    _uiEvent.emit(UiEvent.ShowError("Enter a valid amount"))
                }
                else -> {
                    val transaction = Transaction(
                        personId = supplierId,
                        amount = amount,
                        description = desc,
                        isCredit = isCredit,
                        timestamp = System.currentTimeMillis()
                    )

                    repository.addSupplierTransaction(supplierId, transaction).onSuccess {
                        _uiEvent.emit(UiEvent.ShowSuccess)
                        closePopup()
                        refreshData()
                    }.onFailure {
                        _uiEvent.emit(UiEvent.ShowError("Failed to add transaction"))
                    }
                }
            }
        }
    }

    /** Add a new person (for AddPersonScreen) */
    fun addPerson(type:String, name: String, phone: String?, details: String?) {
        viewModelScope.launch {
            val trimmedName = name.trim()
            if (trimmedName.isEmpty()) {
                _uiEvent.emit(UiEvent.ShowError("Name cannot be empty"))
                return@launch
            }
            if(type == "Supplier"){
                val supplier = Supplier(
                    name = trimmedName,
                    phone = phone?.takeIf { it.isNotBlank() },
                    details = details?.trim().orEmpty()
                )

                repository.addSupplier(supplier).onSuccess {
                    _uiEvent.emit(UiEvent.ShowSuccess)
                    refreshData()
                }.onFailure {
                    _uiEvent.emit(UiEvent.ShowError("Failed to add Supplier"))
                }
            }else{
                val person = Person(
                    name = trimmedName,
                    phone = phone?.takeIf { it.isNotBlank() },
                    details = details?.trim().orEmpty()
                )

                repository.addPerson(person).onSuccess {
                    _uiEvent.emit(UiEvent.ShowSuccess)
                    refreshData()
                }.onFailure {
                    _uiEvent.emit(UiEvent.ShowError("Failed to add person"))
                }
            }

        }
    }


    /** UI Events */
    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        object ShowSuccess : UiEvent()
    }
}
