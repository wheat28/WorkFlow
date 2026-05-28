package com.example.workflow.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.workflow.WorkFlowApp
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90
import com.example.workflow.ui.theme.Indigo95
import com.example.workflow.ui.theme.Gray40
import com.example.workflow.presentation.applications.MyApplicationsScreen
import com.example.workflow.presentation.employer.EmployerDashboardScreen
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
    onEditEmployerProfile: () -> Unit = {},
    onEditSeekerProfile: () -> Unit = {},
    vacanciesRefreshKey: Int = 0,
    resumeRefreshKey: Int = 0,
    applicationsRefreshKey: Int = 0,
    employerProfileRefreshKey: Int = 0,
    seekerProfileRefreshKey: Int = 0
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var favoritesRemovedKey by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = Indigo60,
                unselectedIconColor = Gray40,
                selectedTextColor = Indigo60,
                unselectedTextColor = Gray40,
                indicatorColor = Indigo90
            )
            NavigationBar(containerColor = Indigo95) {
                if (userType == "EMPLOYER") {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Вакансии") },
                        label = { Text("Вакансии") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Дашборд") },
                        label = { Text("Дашборд") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") },
                        colors = navItemColors
                    )
                } else {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Вакансии") },
                        label = { Text("Вакансии") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                        label = { Text("Избранное") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Outlined.Description, contentDescription = "Отклики") },
                        label = { Text("Отклики") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") },
                        colors = navItemColors
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
                1 -> EmployerDashboardScreen(
                    getEmployerStatsUseCase = app.getEmployerStatsUseCase,
                    employerId = userId,
                    modifier = Modifier.padding(innerPadding)
                )
                2 -> ProfileScreen(
                    tokenDataStore = app.tokenDataStore,
                    getMyResumesUseCase = app.getMyResumesUseCase,
                    getEmployerByIdUseCase = app.getEmployerByIdUseCase,
                    onLogout = onLogout,
                    onCreateResume = onCreateResume,
                    onEditResume = onEditResume,
                    onEditEmployerProfile = onEditEmployerProfile,
                    employerProfileRefreshKey = employerProfileRefreshKey,
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
                    onEditSeekerProfile = onEditSeekerProfile,
                    resumeRefreshKey = resumeRefreshKey,
                    seekerProfileRefreshKey = seekerProfileRefreshKey,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
