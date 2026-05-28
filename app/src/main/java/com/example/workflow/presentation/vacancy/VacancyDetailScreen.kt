package com.example.workflow.presentation.vacancy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.application.CheckAppliedUseCase
import com.example.workflow.domain.usecase.favorite.CheckFavoriteUseCase
import com.example.workflow.domain.usecase.vacancy.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.workflow.presentation.common.VacancyDetailSkeleton
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyDetailScreen(
    vacancyId: String,
    getVacancyByIdUseCase: GetVacancyByIdUseCase,
    userType: String,
    checkFavoriteUseCase: CheckFavoriteUseCase? = null,
    addFavoriteUseCase: AddFavoriteUseCase? = null,
    removeFavoriteUseCase: RemoveFavoriteUseCase? = null,
    checkAppliedUseCase: CheckAppliedUseCase? = null,
    deleteVacancyUseCase: DeleteVacancyUseCase? = null,
    appliedSignal: Boolean = false,
    onBack: () -> Unit,
    onApply: (String) -> Unit = {},
    onViewApplications: ((String) -> Unit)? = null,
    onEditVacancy: ((String) -> Unit)? = null,
    onDeleted: (() -> Unit)? = null,
    onViewEmployerProfile: ((String) -> Unit)? = null
) {
    val viewModel: VacancyDetailViewModel = viewModel(
        factory = VacancyDetailViewModel.Factory(
            getVacancyByIdUseCase, vacancyId,
            checkFavoriteUseCase, addFavoriteUseCase, removeFavoriteUseCase,
            checkAppliedUseCase, deleteVacancyUseCase
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appliedSignal) {
        if (appliedSignal) viewModel.markAsApplied()
    }

    LaunchedEffect(uiState) {
        if (uiState is VacancyDetailViewModel.UiState.Deleted) onDeleted?.invoke()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Вакансия") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (userType == "SEEKER") {
                        val isFav = (uiState as? VacancyDetailViewModel.UiState.Success)?.isFavorite ?: false
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFav) "Убрать из избранного" else "Добавить в избранное",
                                tint = if (isFav) Coral40 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (userType == "EMPLOYER" && onEditVacancy != null) {
                        val vacId = (uiState as? VacancyDetailViewModel.UiState.Success)?.vacancy?.id
                        IconButton(onClick = { vacId?.let { onEditVacancy(it) } }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (userType == "EMPLOYER" && deleteVacancyUseCase != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить вакансию",
                                tint = Coral40
                            )
                        }
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить вакансию?") },
                text = { Text("Это действие нельзя отменить. Вакансия будет удалена безвозвратно.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteVacancy()
                    }) {
                        Text("Удалить", color = Coral40)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }

        when (val state = uiState) {
            is VacancyDetailViewModel.UiState.Loading -> {
                VacancyDetailSkeleton(modifier = Modifier.padding(innerPadding))
            }
            is VacancyDetailViewModel.UiState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is VacancyDetailViewModel.UiState.Success -> {
                VacancyDetailContent(
                    vacancy = state.vacancy,
                    userType = userType,
                    isApplied = state.isApplied,
                    onApply = { onApply(state.vacancy.id) },
                    onViewApplications = if (onViewApplications != null) {
                        { onViewApplications(state.vacancy.id) }
                    } else null,
                    onViewEmployerProfile = if (userType == "SEEKER" && onViewEmployerProfile != null) {
                        { onViewEmployerProfile(state.vacancy.employerId) }
                    } else null,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is VacancyDetailViewModel.UiState.Deleted -> {}
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VacancyDetailContent(
    vacancy: VacancyResponseDto,
    userType: String,
    isApplied: Boolean,
    onApply: () -> Unit,
    onViewApplications: (() -> Unit)? = null,
    onViewEmployerProfile: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = vacancy.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = vacancy.companyName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Indigo60,
                    modifier = if (onViewEmployerProfile != null) {
                        Modifier.clickable { onViewEmployerProfile() }
                    } else Modifier
                )
                Text(
                    text = vacancy.city.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (vacancy.salaryFrom != null || vacancy.salaryTo != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val salary = buildString {
                        if (vacancy.salaryFrom != null) append("от ${vacancy.salaryFrom}")
                        if (vacancy.salaryTo != null) append(" до ${vacancy.salaryTo}")
                        if (vacancy.currency != null) append(" ${vacancy.currency}")
                    }
                    Text(
                        text = salary,
                        style = MaterialTheme.typography.titleMedium,
                        color = Green40
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(vacancy.employmentType, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Indigo90,
                            labelColor = Indigo60
                        ),
                        border = null
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(vacancy.experience, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = null
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Описание",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = vacancy.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (vacancy.skills.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Навыки",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        vacancy.skills.forEach { skill ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(skill, style = MaterialTheme.typography.labelMedium) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = null
                            )
                        }
                    }
                }
            }
        }

        if (userType == "EMPLOYER" && onViewApplications != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onViewApplications,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
            ) {
                Text("Отклики", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (userType == "SEEKER") {
            Spacer(modifier = Modifier.height(4.dp))
            if (isApplied) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Green40,
                        disabledContentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Text("Вы откликнулись", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                Button(
                    onClick = onApply,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Coral40)
                ) {
                    Text("Откликнуться", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
