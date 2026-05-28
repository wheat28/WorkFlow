package com.example.workflow.presentation.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.workflow.presentation.common.EmployerVacanciesListSkeleton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Coral90
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Green90
import com.example.workflow.ui.theme.Indigo60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerVacanciesScreen(
    getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
    employerId: String,
    onVacancyClick: (String) -> Unit,
    onCreateVacancy: () -> Unit,
    refreshKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val viewModel: EmployerVacanciesViewModel = viewModel(
        factory = EmployerVacanciesViewModel.Factory(getEmployerVacanciesUseCase, employerId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) viewModel.loadVacancies()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.weight(1f)
        ) {
            when (val state = uiState) {
                is EmployerVacanciesViewModel.UiState.Loading -> EmployerVacanciesListSkeleton()
                is EmployerVacanciesViewModel.UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is EmployerVacanciesViewModel.UiState.Success -> {
                    if (state.vacancies.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.WorkOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "У вас пока нет вакансий",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.vacancies) { vacancy ->
                                EmployerVacancyCard(vacancy, onClick = { onVacancyClick(vacancy.id) })
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onCreateVacancy,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
        ) {
            Text("Создать вакансию", style = MaterialTheme.typography.labelLarge)
        }
    }
}


private fun applicationLabel(count: Int) = when {
    count % 100 in 11..19 -> "откликов"
    count % 10 == 1 -> "отклик"
    count % 10 in 2..4 -> "отклика"
    else -> "откликов"
}

@Composable
private fun EmployerVacancyCard(vacancy: VacancyResponseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = vacancy.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (vacancy.isActive) "Активна" else "Закрыта",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (vacancy.isActive) Green90 else Coral90,
                        labelColor = if (vacancy.isActive) Green40 else Coral40
                    ),
                    border = null
                )
            }

            Text(
                text = vacancy.city.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (vacancy.applicationCount > 0) {
                Text(
                    text = "${vacancy.applicationCount} ${applicationLabel(vacancy.applicationCount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Indigo60
                )
            }

            if (vacancy.salaryFrom != null || vacancy.salaryTo != null) {
                val salary = buildString {
                    if (vacancy.salaryFrom != null) append("от ${vacancy.salaryFrom}")
                    if (vacancy.salaryTo != null) append(" до ${vacancy.salaryTo}")
                    if (vacancy.currency != null) append(" ${vacancy.currency}")
                }
                Text(
                    text = salary,
                    style = MaterialTheme.typography.titleSmall,
                    color = Green40
                )
            }
        }
    }
}
