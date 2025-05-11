package com.nxxr.myudhaar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxxr.myudhaar.R
import com.nxxr.myudhaar.data.model.Supplier
import com.nxxr.myudhaar.viewmodel.HomeViewModel

@Composable
fun SupplierTransactionList(
    suppliers: List<Supplier>,
    recentEntries: List<HomeViewModel.RecentEntry>,
    viewModel: HomeViewModel,
    isPopupOpen: Boolean,
    selectedSupplierId: String?,
    transactionAmount: String,
    transactionDesc: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Recent Supplier Transactions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (recentEntries.isEmpty()) {
                Text(
                    text = "No transactions yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 80.dp)
                ) {
                    items(recentEntries) { entry ->
                        TransactionRow(
                            personName = entry.partyName,
                            description = entry.transaction.description,
                            amount = entry.transaction.amount,
                            isCredit = entry.transaction.isCredit
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeButton("Received", R.drawable.arrow_downward, Modifier.weight(1f)) {
                viewModel.openPopup(isCredit = true)
            }
            HomeButton("Given", R.drawable.arrow_upward, Modifier.weight(1f)) {
                viewModel.openPopup(isCredit = false)
            }
        }

        if (isPopupOpen) {
            SupplierTransactionBottomSheet(
                suppliers = suppliers,
                selectedSupplierId = selectedSupplierId,
                transactionAmount = transactionAmount,
                transactionDesc = transactionDesc,
                onDismiss = viewModel::closePopup,
                onSubmit = viewModel::submitSupplierTransaction,
                onAmountChange = viewModel::onAmountChange,
                onDescChange = viewModel::onDescChange,
                viewModel = viewModel
            )
        }
    }
}
