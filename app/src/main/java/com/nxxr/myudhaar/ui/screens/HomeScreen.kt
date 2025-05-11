package com.nxxr.myudhaar.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nxxr.myudhaar.data.repository.MainRepository
import com.nxxr.myudhaar.navigation.CustomTopAppBar
import com.nxxr.myudhaar.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel = remember { HomeViewModel(MainRepository()) }


    val persons by viewModel.persons.collectAsState()
    val isPopupOpen by viewModel.isPopupOpen.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val transactionAmount by viewModel.transactionAmount.collectAsState()
    val transactionDesc by viewModel.transactionDesc.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val recentPersonEntries by viewModel.recentPersonEntries.collectAsState()
    val recentSupplierEntries by viewModel.recentSupplierEntries.collectAsState()

    val selectedSupplierId by viewModel.selectedSupplierId.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()


    var selectedTab by rememberSaveable { mutableStateOf("Person") }

    val total = if (selectedTab == "Person") viewModel.personTotal.collectAsState().value
    else viewModel.supplierTotal.collectAsState().value

    val today = if (selectedTab == "Person") viewModel.personToday.collectAsState().value
    else viewModel.supplierToday.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeViewModel.UiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                HomeViewModel.UiEvent.ShowSuccess -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "MyUdhaar",
                goBack = {},
                onLogout = { navController.navigate("signIn") },
                onAddPerson = { navController.navigate("person") },
                showLogout = true,
                showAddPerson = true
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            UdhaarBanner(todays = today, total = total)
            UdhaarSegmentedControl(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // List based on selected tab
            if (selectedTab == "Person") {
                PersonTransactionList(
                    persons = persons,
                    recentEntries = recentPersonEntries,
                    viewModel = viewModel,
                    isPopupOpen = isPopupOpen,
                    selectedPersonId = selectedPersonId,
                    transactionAmount = transactionAmount,
                    transactionDesc = transactionDesc
                )
            } else {
                SupplierTransactionList(
                    suppliers = suppliers,
                    recentEntries = recentSupplierEntries,
                    viewModel = viewModel,
                    isPopupOpen = isPopupOpen,
                    selectedSupplierId = selectedSupplierId,
                    transactionAmount = transactionAmount,
                    transactionDesc = transactionDesc
                )
            }
        }
    }
}



@Composable
fun UdhaarSegmentedControl(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("Person", "Supplier")
    Row(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        tabs.forEach { tab ->
            val selected = tab == selectedTab
            Text(
                text = tab,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent
                    )
                    .padding(vertical = 12.dp),
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun TransactionRow(
    personName: String = "ABC",
    description: String = "XYZ",
    amount: Double = 20.00,
    isCredit: Boolean = true,
    timestamp: Long = System.currentTimeMillis()
) {
    // Format the timestamp once per recomposition
    val formatted = remember(timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = personName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Text(
                text = "₹${"%.2f".format(amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isCredit) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}



@Composable
fun HomeButton(
    text: String,
    iconId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animate the FAB’s scale when it enters the composition
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}



@Composable
fun UdhaarBanner(todays: Double, total: Double) {
    Surface(
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Today's Udhaar", style = MaterialTheme.typography.bodySmall)
                Text("₹${"%.2f".format(todays)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }
            Column {
                Text("Total Udhaar", style = MaterialTheme.typography.bodySmall)
                Text("₹${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}
