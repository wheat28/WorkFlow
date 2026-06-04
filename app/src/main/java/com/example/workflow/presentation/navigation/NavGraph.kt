package com.example.workflow.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.presentation.applications.VacancyApplicationsScreen
import com.example.workflow.presentation.employer.EmployerPublicProfileScreen
import com.example.workflow.presentation.resume.ResumeDetailScreen
import com.example.workflow.presentation.apply.ApplyScreen
import com.example.workflow.presentation.employer.CreateVacancyScreen
import com.example.workflow.presentation.employer.EditEmployerProfileScreen
import com.example.workflow.presentation.employer.EditVacancyScreen
import com.example.workflow.presentation.profile.EditSeekerProfileScreen
import com.example.workflow.presentation.resume.EditResumeScreen
import com.example.workflow.presentation.login.LoginScreen
import com.example.workflow.presentation.main.MainScreen
import com.example.workflow.presentation.register.RegisterScreen
import com.example.workflow.presentation.resume.CreateResumeScreen
import com.example.workflow.presentation.vacancy.VacancyDetailScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    tokenDataStore: TokenDataStore,
    onLogout: suspend () -> Unit,
    startRoute: String
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Main.route) {
            val scope = rememberCoroutineScope()
            val userType by tokenDataStore.userTypeFlow.collectAsState(initial = null)
            val userId by tokenDataStore.userIdFlow.collectAsState(initial = null)

            val onLogoutAction: () -> Unit = {
                scope.launch {
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            }

            if (userType != null && userId != null) {
                MainScreen(
                    tokenDataStore = tokenDataStore,
                    userType = userType!!,
                    onLogout = onLogoutAction,
                    onVacancyClick = { vacancyId ->
                        navController.navigate(Screen.VacancyDetail.createRoute(vacancyId))
                    },
                    onCreateVacancy = { navController.navigate(Screen.CreateVacancy.route) },
                    onCreateResume = { navController.navigate(Screen.CreateResume.route) },
                    onEditResume = { resumeId ->
                        navController.navigate(Screen.EditResume.createRoute(resumeId)) },
                    onEditEmployerProfile = { navController.navigate(Screen.EditEmployerProfile.route) },
                    onEditSeekerProfile = { navController.navigate(Screen.EditSeekerProfile.route) }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(Screen.CreateVacancy.route) {
            CreateVacancyScreen(
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.VacancyDetail.route) { backStackEntry ->
            val userType by tokenDataStore.userTypeFlow.collectAsState(initial = "SEEKER")
            val appliedSignal by backStackEntry.savedStateHandle
                .getStateFlow("is_applied", false)
                .collectAsState()

            VacancyDetailScreen(
                userType = userType ?: "SEEKER",
                appliedSignal = appliedSignal,
                onBack = {
                    navController.popBackStack() },
                onApply = { id ->
                    navController.navigate(Screen.Apply.createRoute(id)) },
                onViewApplications = if (userType == "EMPLOYER") {
                    { id ->
                        navController.navigate(Screen.VacancyApplications.createRoute(id)) }
                } else null,
                onEditVacancy = if (userType == "EMPLOYER") {
                    { id ->
                        navController.navigate(Screen.EditVacancy.createRoute(id)) }
                } else null,
                onDeleted = if (userType == "EMPLOYER") {
                    { navController.popBackStack() }
                } else null,
                onViewEmployerProfile = if (userType == "SEEKER") {
                    { employerId ->
                        navController.navigate(Screen.EmployerPublicProfile.createRoute(employerId)) }
                } else null
            )
        }

        composable(Screen.EmployerPublicProfile.route) {
            EmployerPublicProfileScreen(
                onBack = { navController.popBackStack() },
                onVacancyClick = { vacancyId ->
                    navController.navigate(Screen.VacancyDetail.createRoute(vacancyId)) }
            )
        }

        composable(Screen.EditVacancy.route) {
            EditVacancyScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.VacancyApplications.route) {
            VacancyApplicationsScreen(
                onBack = { navController.popBackStack() },
                onViewResume = { resumeId ->
                    navController.navigate(Screen.ResumeDetail.createRoute(resumeId)) }
            )
        }

        composable(Screen.ResumeDetail.route) {
            ResumeDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Apply.route) {
            ApplyScreen(
                onBack = { navController.popBackStack() },
                onApplied = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("is_applied", true)
                    navController.popBackStack()
                },
                onCreateResume = { navController.navigate(Screen.CreateResume.route) }
            )
        }

        composable(Screen.EditResume.route) {
            EditResumeScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() }
            )
        }

        composable(Screen.EditEmployerProfile.route) {
            EditEmployerProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.EditSeekerProfile.route) {
            EditSeekerProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateResume.route) {
            CreateResumeScreen(
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Login: Screen("login")
    data object Register: Screen("register")
    data object Main: Screen("main")
    data object CreateVacancy: Screen("create_vacancy")
    data object CreateResume: Screen("create_resume")
    data object EditEmployerProfile: Screen("edit_employer_profile")
    data object EditSeekerProfile: Screen("edit_seeker_profile")
    data object VacancyDetail: Screen("vacancy/{vacancyId}")
    data object EmployerPublicProfile: Screen("employer_profile/{employerId}")
    data object EditVacancy: Screen("edit_vacancy/{vacancyId}")
    data object VacancyApplications: Screen("vacancy_applications/{vacancyId}")
    data object ResumeDetail: Screen("resume_detail/{resumeId}")
    data object Apply: Screen("apply/{vacancyId}")
    data object EditResume: Screen("edit_resume/{resumeId}")
    fun createRoute(id: String): String {
        return route.replace(Regex("\\{[^}]+\\}"), id)
    }
}

