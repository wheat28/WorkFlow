package com.example.workflow.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.workflow.WorkFlowApp
import com.example.workflow.presentation.applications.MyApplicationsScreen
import com.example.workflow.presentation.employer.EmployerVacanciesScreen
import com.example.workflow.presentation.favorites.FavoritesScreen
import com.example.workflow.presentation.profile.ProfileScreen
import com.example.workflow.presentation.vacancies.VacancyListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    app: WorkFlowApp,
    userType: String,
    userId: String,
    onLogout: () -> Unit,
    onVacancyClick: (String) -> Unit,
    onCreateVacancy: () -> Unit,
    onCreateResume: () -> Unit,
    onEditResume: (String) -> Unit,
    vacanciesRefreshKey: Int = 0,
    resumeRefreshKey: Int = 0,
    applicationsRefreshKey: Int = 0
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var favoritesRemovedKey by rememberSaveable { mutableIntStateOf(0) }

    val topBarTitle = when {
        userType == "EMPLOYER" -> when (selectedTab) {
            0 -> "Мои вакансии"
            else -> "Профиль"
        }
        else -> when (selectedTab) {
            0 -> "Вакансии"
            1 -> "Избранное"
            2 -> "Мои отклики"
            else -> "Профиль"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                if (userType == "EMPLOYER") {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Вакансии") },
                        label = { Text("Вакансии") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") }
                    )
                } else {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Вакансии") },
                        label = { Text("Вакансии") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                        label = { Text("Избранное") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Outlined.Description, contentDescription = "Отклики") },
                        label = { Text("Отклики") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (userType == "EMPLOYER") {
            when (selectedTab) {
                0 -> EmployerVacanciesScreen(
                    getEmployerVacanciesUseCase = app.getEmployerVacanciesUseCase,
                    employerId = userId,
                    onVacancyClick = onVacancyClick,
                    onCreateVacancy = onCreateVacancy,
                    refreshKey = vacanciesRefreshKey,
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> ProfileScreen(
                    tokenDataStore = app.tokenDataStore,
                    getMyResumesUseCase = app.getMyResumesUseCase,
                    onLogout = onLogout,
                    onCreateResume = onCreateResume,
                    onEditResume = onEditResume,
                    resumeRefreshKey = resumeRefreshKey,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        } else {
            when (selectedTab) {
                0 -> VacancyListScreen(
                    getVacanciesUseCase = app.getVacanciesUseCase,
                    onVacancyClick = onVacancyClick,
                    getFavoritesUseCase = app.getFavoritesUseCase,
                    addFavoriteUseCase = app.addFavoriteUseCase,
                    removeFavoriteUseCase = app.removeFavoriteUseCase,
                    seekerId = userId,
                    favoritesRemovedKey = favoritesRemovedKey,
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> FavoritesScreen(
                    getFavoritesUseCase = app.getFavoritesUseCase,
                    removeFavoriteUseCase = app.removeFavoriteUseCase,
                    seekerId = userId,
                    onVacancyClick = onVacancyClick,
                    onFavoriteRemoved = { favoritesRemovedKey++ },
                    modifier = Modifier.padding(innerPadding)
                )
                2 -> MyApplicationsScreen(
                    getMyApplicationsUseCase = app.getMyApplicationsUseCase,
                    cancelApplicationUseCase = app.cancelApplicationUseCase,
                    seekerId = userId,
                    refreshKey = applicationsRefreshKey,
                    modifier = Modifier.padding(innerPadding)
                )
                3 -> ProfileScreen(
                    tokenDataStore = app.tokenDataStore,
                    getMyResumesUseCase = app.getMyResumesUseCase,
                    onLogout = onLogout,
                    onCreateResume = onCreateResume,
                    onEditResume = onEditResume,
                    resumeRefreshKey = resumeRefreshKey,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
