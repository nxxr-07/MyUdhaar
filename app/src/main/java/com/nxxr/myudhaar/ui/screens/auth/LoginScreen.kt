package com.nxxr.myudhaar.ui.screens.auth


import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.base.R

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = AuthViewModel(LocalContext.current),
    onLoginSuccess: () -> Unit
) {
    val state by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val credentials = Identity.getSignInClient(context)
        credentials.getSignInCredentialFromIntent(result.data)?.googleIdToken?.let { idToken ->
            authViewModel.signInWithGoogle(idToken)
        }
    }

    // Launch One Tap if ready
    LaunchedEffect(state) {
        if (state is AuthState.SignInIntent) {
            val intentSender = (state as AuthState.SignInIntent).intentSender
            launcher.launch(IntentSenderRequest.Builder(intentSender).build())
        } else if (state is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "My Udhaar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Track your Udhaar with ease",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { authViewModel.launchOneTap() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.elevatedButtonElevation()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.googleg_standard_color_18),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }

            if (state is AuthState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            if (state is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (state as AuthState.Error).message,
                    color = Color.Red
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen(){
    LoginScreen(
        onLoginSuccess = {}
    )
}