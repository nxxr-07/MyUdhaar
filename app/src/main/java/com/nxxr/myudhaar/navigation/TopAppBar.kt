package com.nxxr.myudhaar.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxxr.myudhaar.R
import com.nxxr.myudhaar.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    showGoBack: () -> Boolean = { false },
    goBack: () -> Unit,
    onLogout: () -> Unit,
    onAddPerson: () -> Unit,
    showLogout: Boolean,
    showAddPerson: Boolean
){
    val authViewModel : AuthViewModel = viewModel()
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
        navigationIcon = {
            if( showGoBack()){
                IconButton(onClick = { goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { onAddPerson() }) {
                Icon(painterResource(R.drawable.add_person), contentDescription = "Add Person")
            }
            if(showLogout){
                IconButton(
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.logout),
                        contentDescription = "Logout",
                        Modifier.padding(4.dp)
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview
@Composable
fun PreviewCustomTopAppBar(){
    CustomTopAppBar(title = "Home", showGoBack = { false }, goBack = {}, {}, {}, false, false)

}