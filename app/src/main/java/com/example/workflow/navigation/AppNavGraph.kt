package com.example.workflow.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workflow.WorkFlowApp
import com.example.workflow.presentation.login.LoginScreen
import com.example.workflow.presentation.register.RegisterScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(app: WorkFlowApp, startRoute: String) {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = startRoute) {

        composable("login") {
            LoginScreen(
                loginUseCase = app.loginUseCase,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                registerSeekerUseCase = app.registerSeekerUseCase,
                registerEmployerUseCase = app.registerEmployerUseCase,
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("main") {
            val scope = rememberCoroutineScope()
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = {
                    scope.launch {
                        app.logoutUseCase()
                        navController.navigate("login") {
                            popUpTo("main") {
                                inclusive = true
                            }
                        }
                    }
                }) {
                    Text("Выйти")
                }
            }
        }
    }
}
