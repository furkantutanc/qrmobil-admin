package com.qrmobil.admin.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qrmobil.admin.ui.screens.CategoriesScreen
import com.qrmobil.admin.ui.screens.DashboardScreen
import com.qrmobil.admin.ui.screens.FeedbacksScreen
import com.qrmobil.admin.ui.screens.LoginScreen
import com.qrmobil.admin.ui.screens.MenuManagementScreen
import com.qrmobil.admin.ui.screens.QrCodeScreen
import com.qrmobil.admin.ui.screens.RegistrationScreen
import com.qrmobil.admin.ui.screens.RestaurantInfoScreen
import com.qrmobil.admin.ui.screens.RestaurantPreviewScreen
import com.qrmobil.admin.ui.screens.WelcomeScreen
import com.qrmobil.admin.viewmodel.LoginViewModel
import com.qrmobil.admin.viewmodel.RegistrationViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) + slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(200)) }
    ) {
        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("register") {
            val registrationViewModel: RegistrationViewModel = viewModel()
            RegistrationScreen(
                viewModel = registrationViewModel,
                onRegistrationComplete = {
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onLogoutClick = {
                    navController.navigate("welcome") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onCategoryClick = { navController.navigate("categories") },
                onMenuClick = { navController.navigate("menu") },
                onInfoClick = { navController.navigate("info") },
                onFeedbackClick = { navController.navigate("feedbacks") },
                onQrClick = { navController.navigate("qr") },
                onPreviewClick = { navController.navigate("preview") }
            )
        }
        composable("categories") { CategoriesScreen(onBack = { navController.popBackStack() }) }
        composable("menu") { MenuManagementScreen(onBack = { navController.popBackStack() }) }
        composable("info") { RestaurantInfoScreen(onBack = { navController.popBackStack() }) }
        composable("feedbacks") { FeedbacksScreen(onBack = { navController.popBackStack() }) }
        composable("qr") { QrCodeScreen(onBack = { navController.popBackStack() }) }
        composable("preview") { RestaurantPreviewScreen(onBack = { navController.popBackStack() }) }
    }
}
