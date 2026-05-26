package com.example.workflow.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.GetMyResumesUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@Composable
fun ProfileScreen(
    tokenDataStore: TokenDataStore,
    getMyResumesUseCase: GetMyResumesUseCase,
    onLogout: () -> Unit,
    onCreateResume: () -> Unit,
    onEditResume: (String) -> Unit,
    resumeRefreshKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val userType by tokenDataStore.userTypeFlow.collectAsState(initial = null)
    val userId by tokenDataStore.userIdFlow.collectAsState(initial = null)

    val roleLabel = when (userType) {
        "SEEKER" -> "Соискатель"
        "EMPLOYER" -> "Работодатель"
        else -> ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Шапка
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Indigo90, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (userType == "SEEKER") "С" else "Р",
                    style = MaterialTheme.typography.titleLarge,
                    color = Indigo60
                )
            }
            Text(
                text = roleLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Контент + кнопка выхода
        if (userType == "SEEKER" && userId != null) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(getMyResumesUseCase, userId!!)
            )
            val resumeState by profileViewModel.resumeState.collectAsStateWithLifecycle()

            LaunchedEffect(resumeRefreshKey) {
                if (resumeRefreshKey > 0) profileViewModel.loadResumes()
            }

            // Список резюме — занимает всё доступное место
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Мои резюме",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = onCreateResume,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Add, contentDescription = null,
                                modifier = Modifier.size(16.dp), tint = Indigo60
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text("Создать", color = Indigo60, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                when (val state = resumeState) {
                    is ProfileViewModel.ResumeState.Loading -> item {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Indigo60)
                        }
                    }
                    is ProfileViewModel.ResumeState.Error -> item {
                        Text(
                            state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    is ProfileViewModel.ResumeState.Success -> {
                        if (state.resumes.isEmpty()) {
                            item {
                                Text(
                                    "Резюме ещё нет. Создайте первое!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(state.resumes) { resume -> ResumeCard(resume, onClick = { onEditResume(resume.id) }) }
                        }
                    }
                }
            }

            // Кнопка выхода — всегда внизу, вне LazyColumn
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Coral40)
            ) {
                Text("Выйти из аккаунта", style = MaterialTheme.typography.labelLarge)
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Coral40)
            ) {
                Text("Выйти из аккаунта", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ResumeCard(resume: ResumeResponseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                resume.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                resume.position,
                style = MaterialTheme.typography.bodyMedium,
                color = Indigo60
            )
            if (resume.salaryExpected != null) {
                Text(
                    "от ${resume.salaryExpected} ${resume.currency}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            resume.city?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
