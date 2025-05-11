package com.nxxr.myudhaar.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nxxr.myudhaar.data.repository.MainRepository
import com.nxxr.myudhaar.ui.screens.AddPersonScreen
import com.nxxr.myudhaar.ui.screens.auth.GoogleSignInScreen
import com.nxxr.myudhaar.ui.screens.SplashScreen
import com.nxxr.myudhaar.ui.screens.HomeScreen
import com.nxxr.myudhaar.viewmodel.HomeViewModel


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signIn")
    object Home : Screen("home")
    object Person : Screen("person")
    object Summary : Screen("summary")
    object Transaction : Screen("transaction")
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.SignIn.route) {
            GoogleSignInScreen(
                navController = navController
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController
            )
        }

        composable(Screen.Person.route) {
            AddPersonScreen(
                onBack = { navController.popBackStack() },
                navController = navController,
                viewModel = HomeViewModel(MainRepository())
            )
        }
//
//        composable(Screen.Summary.route) {
//            SummaryScreen(
//                onBack = { navController.popBackStack() }
//            )
//        }
//
//        composable(Screen.Transaction.route) {
//            TransactionScreen(
//                onBack = { navController.popBackStack() }
//            )
//        }
    }
}
