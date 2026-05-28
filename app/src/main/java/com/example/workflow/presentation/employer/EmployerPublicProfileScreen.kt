package com.example.workflow.presentation.employer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import androidx.compose.foundation.background
import com.example.workflow.presentation.common.shimmerBrush
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerPublicProfileScreen(
    employerId: String,
    getEmployerByIdUseCase: GetEmployerByIdUseCase,
    getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
    onBack: () -> Unit,
    onVacancyClick: (String) -> Unit
) {
    val viewModel: EmployerPublicProfileViewModel = viewModel(
        factory = EmployerPublicProfileViewModel.Factory(
            getEmployerByIdUseCase, getEmployerVacanciesUseCase, employerId
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль компании") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is EmployerPublicProfileViewModel.UiState.Loading -> {
                EmployerProfileSkeleton(modifier = Modifier.padding(innerPadding))
            }
            is EmployerPublicProfileViewModel.UiState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is EmployerPublicProfileViewModel.UiState.Success -> {
                EmployerPublicProfileContent(
                    employer = state.employer,
                    vacancies = state.vacancies,
                    onVacancyClick = onVacancyClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun EmployerPublicProfileContent(
    employer: EmployerResponseDto,
    vacancies: List<VacancyResponseDto>,
    onVacancyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .padding(end = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Business,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = Indigo60
                            )
                        }
                        Column {
                            Text(
                                text = employer.companyName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            employer.industry?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        employer.city?.let {
                            InfoRow(icon = { Icon(Icons.Outlined.LocationOn, null, tint = Indigo60, modifier = Modifier.size(18.dp)) }, text = it)
                        }
                        employer.website?.let {
                            InfoRow(icon = { Icon(Icons.Outlined.Language, null, tint = Indigo60, modifier = Modifier.size(18.dp)) }, text = it)
                        }
                        employer.phone?.let {
                            InfoRow(icon = { Icon(Icons.Outlined.Phone, null, tint = Indigo60, modifier = Modifier.size(18.dp)) }, text = it)
                        }
                    }
                }
            }
        }

        employer.description?.let { desc ->
            if (desc.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("О компании", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        if (vacancies.isNotEmpty()) {
            item {
                Text(
                    text = "Открытые вакансии · ${vacancies.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(vacancies, key = { it.id }) { vacancy ->
                PublicVacancyCard(vacancy = vacancy, onClick = { onVacancyClick(vacancy.id) })
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Нет открытых вакансий",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(icon: @Composable () -> Unit, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PublicVacancyCard(vacancy: VacancyResponseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = vacancy.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (vacancy.salaryFrom != null || vacancy.salaryTo != null) {
                val salary = buildString {
                    if (vacancy.salaryFrom != null) append("от ${vacancy.salaryFrom}")
                    if (vacancy.salaryTo != null) append(" до ${vacancy.salaryTo}")
                    if (vacancy.currency != null) append(" ${vacancy.currency}")
                }
                Text(text = salary, style = MaterialTheme.typography.bodyMedium, color = Green40)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(vacancy.employmentType, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Indigo90, labelColor = Indigo60),
                    border = null
                )
                vacancy.city?.let {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(it, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = null
                    )
                }
            }
        }
    }
}

@Composable
private fun EmployerProfileSkeleton(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(brush, RoundedCornerShape(12.dp)))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.fillMaxWidth(0.6f).height(20.dp).background(brush, RoundedCornerShape(6.dp)))
                        Box(Modifier.fillMaxWidth(0.4f).height(14.dp).background(brush, RoundedCornerShape(6.dp)))
                    }
                }
                repeat(3) {
                    Box(Modifier.fillMaxWidth(0.5f).height(14.dp).background(brush, RoundedCornerShape(6.dp)))
                }
            }
        }
        repeat(3) {
            Box(Modifier.fillMaxWidth().height(90.dp).background(brush, RoundedCornerShape(16.dp)))
        }
    }
}
