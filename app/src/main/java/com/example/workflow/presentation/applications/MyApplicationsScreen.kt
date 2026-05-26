package com.example.workflow.presentation.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.usecase.CancelApplicationUseCase
import com.example.workflow.domain.usecase.GetMyApplicationsUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

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

    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) viewModel.load()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is MyApplicationsViewModel.UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Indigo60)
            }
            is MyApplicationsViewModel.UiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            is MyApplicationsViewModel.UiState.Success -> {
                if (state.applications.isEmpty()) {
                    Text(
                        text = "Вы ещё не откликались на вакансии",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.applications, key = { it.id }) { application ->
                            ApplicationCard(
                                application = application,
                                onCancel = if (application.status == "PENDING") {
                                    { viewModel.cancel(application.id) }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = application.vacancyTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Статус: ${statusLabel(application.status)}",
                style = MaterialTheme.typography.bodySmall,
                color = statusColor(application.status)
            )
            application.coverLetter?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            if (onCancel != null) {
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

private fun statusLabel(status: String) = when (status) {
    "PENDING" -> "На рассмотрении"
    "ACCEPTED" -> "Принят"
    "REJECTED" -> "Отклонён"
    else -> status
}

@Composable
private fun statusColor(status: String) = when (status) {
    "ACCEPTED" -> Green40
    "REJECTED" -> androidx.compose.ui.graphics.Color(0xFFE84545)
    else -> Indigo60
}
