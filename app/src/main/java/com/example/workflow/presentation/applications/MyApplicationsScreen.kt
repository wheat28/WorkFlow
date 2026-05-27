package com.example.workflow.presentation.applications

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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.workflow.presentation.common.ApplicationsListSkeleton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.usecase.application.CancelApplicationUseCase
import com.example.workflow.domain.usecase.application.GetMyApplicationsUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    getMyApplicationsUseCase: GetMyApplicationsUseCase,
    cancelApplicationUseCase: CancelApplicationUseCase,
    seekerId: String,
    refreshKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val viewModel: MyApplicationsViewModel = viewModel(
        factory = MyApplicationsViewModel.Factory(getMyApplicationsUseCase, cancelApplicationUseCase, seekerId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    var cancelTargetId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) viewModel.load()
    }

    if (cancelTargetId != null) {
        AlertDialog(
            onDismissRequest = { cancelTargetId = null },
            title = { Text("Отменить отклик?") },
            text = { Text("Отклик будет удалён и работодатель его больше не увидит.") },
            confirmButton = {
                TextButton(onClick = {
                    cancelTargetId?.let { viewModel.cancel(it) }
                    cancelTargetId = null
                }) {
                    Text("Отменить отклик", color = Coral40)
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTargetId = null }) {
                    Text("Оставить")
                }
            }
        )
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is MyApplicationsViewModel.UiState.Loading -> ApplicationsListSkeleton()
            is MyApplicationsViewModel.UiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is MyApplicationsViewModel.UiState.Success -> {
                if (state.applications.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Description,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Вы ещё не откликались на вакансии",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.applications, key = { it.id }) { application ->
                            ApplicationCard(
                                application = application,
                                onCancel = if (application.status == "PENDING") {
                                    { cancelTargetId = application.id }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationCard(
    application: ApplicationResponseDto,
    onCancel: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = application.vacancyTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor(application.status).copy(alpha = 0.12f),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = statusLabel(application.status),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor(application.status),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = formatDate(application.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            application.coverLetter?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            if (onCancel != null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Coral40),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Coral40)
                ) {
                    Text("Отменить отклик", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private fun formatDate(isoDate: String): String {
    return try {
        val date = Instant.parse(isoDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        when (date) {
            today -> "Сегодня"
            today.minusDays(1) -> "Вчера"
            else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru")))
        }
    } catch (e: Exception) {
        isoDate
    }
}

private fun statusLabel(status: String) = when (status) {
    "PENDING" -> "На рассмотрении"
    "ACCEPTED" -> "Принят"
    "REJECTED" -> "Отклонён"
    else -> status
}

@Composable
private fun statusColor(status: String) = when (status) {
    "ACCEPTED" -> Green40
    "REJECTED" -> Coral40
    else -> Indigo60
}
