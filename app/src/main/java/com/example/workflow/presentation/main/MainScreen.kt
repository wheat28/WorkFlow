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
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.presentation.ui.theme.Indigo60
import com.example.workflow.presentation.ui.theme.Indigo90
import com.example.workflow.presentation.ui.theme.Indigo95
import com.example.workflow.presentation.ui.theme.Gray40
import com.example.workflow.presentation.applications.MyApplicationsScreen
import com.example.workflow.presentation.employer.EmployerDashboardScreen
import com.example.workflow.presentation.employer.EmployerVacanciesScreen
import com.example.workflow.presentation.favorites.FavoritesScreen
import com.example.workflow.presentation.profile.ProfileScreen
import com.example.workflow.presentation.vacancies.VacancyListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    tokenDataStore: TokenDataStore,
    userType: String,
    onLogout: () -> Unit,
    onVacancyClick: (String) -> Unit,
    onCreateVacancy: () -> Unit,
    onCreateResume: () -> Unit,
    onEditResume: (String) -> Unit,
    onEditEmployerProfile: () -> Unit = {},
    onEditSeekerProfile: () -> Unit = {}
) {

    var selectBottomBar by rememberSaveable { mutableIntStateOf(0) }

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
                        selected = selectBottomBar == 0,
                        onClick = { selectBottomBar = 0 },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Вакансии") },
                        label = { Text("Вакансии") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectBottomBar == 1,
                        onClick = { selectBottomBar = 1 },
                        icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Дашборд") },
                        label = { Text("Дашборд") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectBottomBar == 2,
                        onClick = { selectBottomBar = 2 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") },
                        colors = navItemColors
                    )

                } else {
                    NavigationBarItem(
                        selected = selectBottomBar == 0,
                        onClick = { selectBottomBar = 0 },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Вакансии") },
                        label = { Text("Вакансии") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectBottomBar == 1,
                        onClick = { selectBottomBar = 1 },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                        label = { Text("Избранное") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectBottomBar == 2,
                        onClick = { selectBottomBar = 2 },
                        icon = { Icon(Icons.Outlined.Description, contentDescription = "Отклики") },
                        label = { Text("Отклики") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectBottomBar == 3,
                        onClick = { selectBottomBar = 3 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") },
                        colors = navItemColors
                    )
                }
            }
        }
    ) { innerPadding ->
        if (userType == "EMPLOYER") {
            when (selectBottomBar) {

                0 -> EmployerVacanciesScreen(
                    onVacancyClick = onVacancyClick,
                    onCreateVacancy = onCreateVacancy,
                    modifier = Modifier.padding(innerPadding)
                )

                1 -> EmployerDashboardScreen(
                    modifier = Modifier.padding(innerPadding)
                )

                2 -> ProfileScreen(
                    tokenDataStore = tokenDataStore,
                    onLogout = onLogout,
                    onCreateResume = onCreateResume,
                    onEditResume = onEditResume,
                    onEditEmployerProfile = onEditEmployerProfile,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        } else {
            when (selectBottomBar) {

                0 -> VacancyListScreen(
                    onVacancyClick = onVacancyClick,
                    modifier = Modifier.padding(innerPadding)
                )

                1 -> FavoritesScreen(
                    onVacancyClick = onVacancyClick,
                    modifier = Modifier.padding(innerPadding)
                )

                2 -> MyApplicationsScreen(
                    modifier = Modifier.padding(innerPadding)
                )

                3 -> ProfileScreen(
                    tokenDataStore = tokenDataStore,
                    onLogout = onLogout,
                    onCreateResume = onCreateResume,
                    onEditResume = onEditResume,
                    onEditSeekerProfile = onEditSeekerProfile,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
