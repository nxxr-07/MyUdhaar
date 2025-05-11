package com.nxxr.myudhaar.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nxxr.myudhaar.data.model.Person
import com.nxxr.myudhaar.data.model.Supplier
import com.nxxr.myudhaar.viewmodel.HomeViewModel


// --------------FOR PERSONS---------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonTransactionBottomSheet(
    viewModel: HomeViewModel,
    persons: List<Person>,
    selectedPersonId: String?,
    transactionAmount: String,
    transactionDesc: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    onAmountChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(persons) {
        println("🔍 TransactionBottomSheet received persons: ${persons.map { it.name }}")
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Transaction", style = MaterialTheme.typography.titleMedium)

            // Person selection dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = persons.find { it.id == selectedPersonId }?.name ?: "Select person",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Person") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    persons.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                viewModel.onPersonSelected(person.id)
                                expanded = false
                            }
                        )
                    }
                }
            }


            TextField(
                value = transactionAmount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = transactionDesc,
                onValueChange = onDescChange,
                label = { Text("Description") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSubmit) {
                    Text("Save")
                }
            }
        }
    }
}

//---------------FOR SUPPLIERS-----------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierTransactionBottomSheet(
    viewModel: HomeViewModel,
    suppliers: List<Supplier>,
    selectedSupplierId: String?,
    transactionAmount: String,
    transactionDesc: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    onAmountChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(suppliers) {
        println("🔍 TransactionBottomSheet received persons: ${suppliers.map { it.name }}")
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Supplier Transaction", style = MaterialTheme.typography.titleMedium)

            // Person selection dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = suppliers.find { it.id == selectedSupplierId }?.name ?: "Select Supplier",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Supplier") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    suppliers.forEach { supplier ->
                        DropdownMenuItem(
                            text = { Text(supplier.name) },
                            onClick = {
                                viewModel.onSupplierSelected(supplier.id)
                                expanded = false
                            }
                        )
                    }
                }
            }


            TextField(
                value = transactionAmount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = transactionDesc,
                onValueChange = onDescChange,
                label = { Text("Description") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSubmit) {
                    Text("Save")
                }
            }
        }
    }
}
