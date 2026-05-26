package com.example.workflow.presentation.apply

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.ApplyForVacancyUseCase
import com.example.workflow.domain.usecase.GetMyResumesUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    vacancyId: String,
    seekerId: String,
    getMyResumesUseCase: GetMyResumesUseCase,
    applyForVacancyUseCase: ApplyForVacancyUseCase,
    onBack: () -> Unit,
    onApplied: () -> Unit,
    onCreateResume: () -> Unit
) {
    val viewModel: ApplyViewModel = viewModel(
        factory = ApplyViewModel.Factory(getMyResumesUseCase, applyForVacancyUseCase, seekerId, vacancyId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedResumeId by remember { mutableStateOf<String?>(null) }
    var coverLetter by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is ApplyViewModel.UiState.Success) onApplied()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Откликнуться") },
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
            is ApplyViewModel.UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            }
            is ApplyViewModel.UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ApplyViewModel.UiState.Ready -> {
                if (state.resumes.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(innerPadding).padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "У вас нет резюме",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Создайте резюме, чтобы откликнуться на вакансию",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = onCreateResume,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
                            ) {
                                Text("Создать резюме")
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Выберите резюме", style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        state.resumes.forEach { resume ->
                            ResumeSelectCard(
                                resume = resume,
                                isSelected = resume.id == selectedResumeId,
                                onClick = { selectedResumeId = resume.id }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = coverLetter,
                            onValueChange = { coverLetter = it },
                            label = { Text("Сопроводительное письмо (необязательно)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Indigo60,
                                focusedLabelColor = Indigo60
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                selectedResumeId?.let { viewModel.apply(it, coverLetter) }
                            },
                            enabled = selectedResumeId != null,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Coral40)
                        ) {
                            Text("Откликнуться", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
            is ApplyViewModel.UiState.Submitting -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Coral40)
                }
            }
            is ApplyViewModel.UiState.Success -> {}
        }
    }
}

@Composable
private fun ResumeSelectCard(
    resume: ResumeResponseDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, Indigo60, RoundedCornerShape(20.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Indigo90 else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(resume.title, style = MaterialTheme.typography.titleSmall,
                color = if (isSelected) Indigo60 else MaterialTheme.colorScheme.onSurface)
            Text(resume.position, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
