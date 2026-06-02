package com.example.workflow.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
fun AppNavGraph(tokenDataStore: TokenDataStore, onLogout: suspend () -> Unit, startRoute: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startRoute,
        enterTransition = { slideInHorizontally(tween(280)) { it } + fadeIn(tween(280)) },
        exitTransition = { slideOutHorizontally(tween(280)) { -it / 3 } + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(tween(280)) { -it / 3 } + fadeIn(tween(280)) },
        popExitTransition = { slideOutHorizontally(tween(280)) { it } + fadeOut(tween(200)) }
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("main") {
            val scope = rememberCoroutineScope()
            val userType by tokenDataStore.userTypeFlow.collectAsState(initial = null)
            val userId by tokenDataStore.userIdFlow.collectAsState(initial = null)
            val vacanciesRefreshKey by it.savedStateHandle
                .getStateFlow("vacancies_refresh_key", 0)
                .collectAsState()
            val resumeRefreshKey by it.savedStateHandle
                .getStateFlow("resume_refresh_key", 0)
                .collectAsState()
            val applicationsRefreshKey by it.savedStateHandle
                .getStateFlow("applications_refresh_key", 0)
                .collectAsState()
            val employerProfileRefreshKey by it.savedStateHandle
                .getStateFlow("employer_profile_refresh_key", 0)
                .collectAsState()
            val seekerProfileRefreshKey by it.savedStateHandle
                .getStateFlow("seeker_profile_refresh_key", 0)
                .collectAsState()

            val onLogoutAction: () -> Unit = {
                scope.launch {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            }

            if (userType != null && userId != null) {
                MainScreen(
                    tokenDataStore = tokenDataStore,
                    userType = userType!!,
                    onLogout = onLogoutAction,
                    onVacancyClick = { vacancyId ->
                        navController.navigate("vacancy/$vacancyId")
                    },
                    onCreateVacancy = { navController.navigate("create_vacancy") },
                    onCreateResume = { navController.navigate("create_resume") },
                    onEditResume = { resumeId -> navController.navigate("edit_resume/$resumeId") },
                    onEditEmployerProfile = { navController.navigate("edit_employer_profile") },
                    onEditSeekerProfile = { navController.navigate("edit_seeker_profile") },
                    vacanciesRefreshKey = vacanciesRefreshKey,
                    resumeRefreshKey = resumeRefreshKey,
                    applicationsRefreshKey = applicationsRefreshKey,
                    employerProfileRefreshKey = employerProfileRefreshKey,
                    seekerProfileRefreshKey = seekerProfileRefreshKey
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable("create_vacancy") {
            CreateVacancyScreen(
                onBack = { navController.popBackStack() },
                onCreated = {
                    val current = navController.getBackStackEntry("main")
                        .savedStateHandle.get<Int>("vacancies_refresh_key") ?: 0
                    navController.getBackStackEntry("main")
                        .savedStateHandle["vacancies_refresh_key"] = current + 1
                    navController.popBackStack()
                }
            )
        }

        composable("vacancy/{id}") { backStackEntry ->
            val userType by tokenDataStore.userTypeFlow.collectAsState(initial = "SEEKER")
            val appliedSignal by backStackEntry.savedStateHandle
                .getStateFlow("is_applied", false)
                .collectAsState()

            VacancyDetailScreen(
                userType = userType ?: "SEEKER",
                appliedSignal = appliedSignal,
                onBack = { navController.popBackStack() },
                onApply = { id -> navController.navigate("apply/$id") },
                onViewApplications = if ((userType ?: "") == "EMPLOYER") {
                    { id -> navController.navigate("vacancy_applications/$id") }
                } else null,
                onEditVacancy = if ((userType ?: "") == "EMPLOYER") {
                    { id -> navController.navigate("edit_vacancy/$id") }
                } else null,
                onDeleted = if ((userType ?: "") == "EMPLOYER") {
                    {
                        runCatching {
                            val entry = navController.getBackStackEntry("main")
                            val current = entry.savedStateHandle.get<Int>("vacancies_refresh_key") ?: 0
                            entry.savedStateHandle["vacancies_refresh_key"] = current + 1
                        }
                        navController.popBackStack()
                    }
                } else null,
                onViewEmployerProfile = if ((userType ?: "") == "SEEKER") {
                    { employerId -> navController.navigate("employer_profile/$employerId") }
                } else null
            )
        }

        composable("employer_profile/{employerId}") {
            EmployerPublicProfileScreen(
                onBack = { navController.popBackStack() },
                onVacancyClick = { vacancyId -> navController.navigate("vacancy/$vacancyId") }
            )
        }

        composable("edit_vacancy/{vacancyId}") {
            EditVacancyScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("vacancies_refresh_key") ?: 0
                        entry.savedStateHandle["vacancies_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                },
                onDeleted = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("vacancies_refresh_key") ?: 0
                        entry.savedStateHandle["vacancies_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                    navController.popBackStack()
                }
            )
        }

        composable("vacancy_applications/{vacancyId}") {
            VacancyApplicationsScreen(
                onBack = { navController.popBackStack() },
                onViewResume = { resumeId -> navController.navigate("resume_detail/$resumeId") }
            )
        }

        composable("resume_detail/{resumeId}") {
            ResumeDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("apply/{vacancyId}") {
            ApplyScreen(
                onBack = { navController.popBackStack() },
                onApplied = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle?.set("is_applied", true)
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("applications_refresh_key") ?: 0
                        entry.savedStateHandle["applications_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                },
                onCreateResume = { navController.navigate("create_resume") }
            )
        }

        composable("edit_resume/{resumeId}") {
            EditResumeScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("resume_refresh_key") ?: 0
                        entry.savedStateHandle["resume_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                },
                onDeleted = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("resume_refresh_key") ?: 0
                        entry.savedStateHandle["resume_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                }
            )
        }

        composable("edit_employer_profile") {
            EditEmployerProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("employer_profile_refresh_key") ?: 0
                        entry.savedStateHandle["employer_profile_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                }
            )
        }

        composable("edit_seeker_profile") {
            EditSeekerProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("seeker_profile_refresh_key") ?: 0
                        entry.savedStateHandle["seeker_profile_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                }
            )
        }

        composable("create_resume") {
            CreateResumeScreen(
                onBack = { navController.popBackStack() },
                onCreated = {
                    runCatching {
                        val entry = navController.getBackStackEntry("main")
                        val current = entry.savedStateHandle.get<Int>("resume_refresh_key") ?: 0
                        entry.savedStateHandle["resume_refresh_key"] = current + 1
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}
