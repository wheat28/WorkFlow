package com.example.workflow.presentation.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.usecase.application.GetVacancyApplicationsUseCase
import com.example.workflow.domain.usecase.application.UpdateApplicationStatusUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Coral90
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Green90
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

private val filters = listOf(
    null to "Все",
    "PENDING" to "На рассмотрении",
    "ACCEPTED" to "Принятые",
    "REJECTED" to "Отклонённые"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyApplicationsScreen(
    vacancyId: String,
    getVacancyApplicationsUseCase: GetVacancyApplicationsUseCase,
    updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    onBack: () -> Unit,
    onViewResume: (resumeId: String) -> Unit = {}
) {
    val viewModel: VacancyApplicationsViewModel = viewModel(
        factory = VacancyApplicationsViewModel.Factory(
            getVacancyApplicationsUseCase, updateApplicationStatusUseCase, vacancyId
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeFilter by viewModel.filterStatus.collectAsStateWithLifecycle()

    var pendingAction by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    pendingAction?.let { (appId, newStatus, message) ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = { Text(if (newStatus == "ACCEPTED") "Принять кандидата?" else "Отклонить кандидата?") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateStatus(appId, newStatus)
                    pendingAction = null
                }) {
                    Text(
                        if (newStatus == "ACCEPTED") "Принять" else "Отклонить",
                        color = if (newStatus == "ACCEPTED") Green40 else Coral40
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отклики", maxLines = 1) },
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { (status, label) ->
                    FilterChip(
                        selected = activeFilter == status,
                        onClick = { viewModel.setFilter(status) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Indigo90,
                            selectedLabelColor = Indigo60
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = activeFilter == status,
                            selectedBorderColor = Indigo60,
                            borderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is VacancyApplicationsViewModel.UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Indigo60)
                    }
                    is VacancyApplicationsViewModel.UiState.Error -> {
                        Text(
                            text = state.message,
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is VacancyApplicationsViewModel.UiState.Success -> {
                        if (state.applications.isEmpty()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Inbox,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (activeFilter == null) "Откликов пока нет" else "Нет откликов в этой категории",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.applications, key = { it.id }) { application ->
                                    ApplicantCard(
                                        application = application,
                                        onAccept = {
                                            pendingAction = Triple(
                                                application.id, "ACCEPTED",
                                                "Вы принимаете отклик от ${application.seekerFirstName} ${application.seekerLastName}."
                                            )
                                        },
                                        onReject = {
                                            pendingAction = Triple(
                                                application.id, "REJECTED",
                                                "Отклик от ${application.seekerFirstName} ${application.seekerLastName} будет отклонён."
                                            )
                                        },
                                        onViewResume = { onViewResume(application.resumeId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicantCard(
    application: ApplicationResponseDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onViewResume: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${application.seekerFirstName} ${application.seekerLastName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatusChip(application.status)
            }

            application.coverLetter?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
            }

            OutlinedButton(
                onClick = onViewResume,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo60),
                border = androidx.compose.foundation.BorderStroke(1.dp, Indigo60)
            ) {
                Text("Смотреть резюме", style = MaterialTheme.typography.labelMedium)
            }

            if (application.status == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green40)
                    ) {
                        Text("Принять", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Coral40)
                    ) {
                        Text("Отклонить", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (label, bg, fg) = when (status) {
        "ACCEPTED" -> Triple("Принят", Green90, Green40)
        "REJECTED" -> Triple("Отклонён", Coral90, Coral40)
        else -> Triple("На рассмотрении", Indigo90, Indigo60)
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = bg, labelColor = fg),
        border = null
    )
}
