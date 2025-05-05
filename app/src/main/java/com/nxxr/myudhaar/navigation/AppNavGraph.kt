package com.nxxr.myudhaar.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nxxr.myudhaar.ui.screens.SplashScreen
import com.nxxr.myudhaar.ui.screens.auth.*
import com.nxxr.myudhaar.ui.screens.home.HomeScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
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

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Home.route) {
            HomeScreen(
                onAddPerson = { navController.navigate(Screen.Person.route) },
                onSummary = { navController.navigate(Screen.Summary.route) },
                onTransaction = { navController.navigate(Screen.Transaction.route) }
            )
        }

//        composable(Screen.Person.route) {
//            AddPersonScreen(
//                onBack = { navController.popBackStack() }
//            )
//        }
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
