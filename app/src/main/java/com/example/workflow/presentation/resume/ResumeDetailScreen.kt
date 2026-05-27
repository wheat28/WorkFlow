package com.example.workflow.presentation.resume

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.data.remote.dto.WorkExperienceResponseDto
import com.example.workflow.domain.usecase.resume.GetResumeByIdUseCase
import com.example.workflow.presentation.common.ResumeDetailSkeleton
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeDetailScreen(
    resumeId: String,
    getResumeByIdUseCase: GetResumeByIdUseCase,
    onBack: () -> Unit
) {
    val viewModel: ResumeDetailViewModel = viewModel(
        factory = ResumeDetailViewModel.Factory(getResumeByIdUseCase, resumeId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Резюме") },
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
            is ResumeDetailViewModel.UiState.Loading -> {
                ResumeDetailSkeleton(modifier = Modifier.padding(innerPadding))
            }
            is ResumeDetailViewModel.UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ResumeDetailViewModel.UiState.Success -> {
                ResumeDetailContent(
                    resume = state.resume,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResumeDetailContent(resume: ResumeResponseDto, modifier: Modifier = Modifier) {
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
                    text = resume.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = resume.position,
                    style = MaterialTheme.typography.titleSmall,
                    color = Indigo60
                )
                resume.city?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                resume.salaryExpected?.let {
                    Text(
                        text = "от $it ${resume.currency}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Green40
                    )
                }
                SuggestionChip(
                    onClick = {},
                    label = { Text(employmentTypeLabel(resume.employmentType), style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Indigo90, labelColor = Indigo60),
                    border = null
                )
            }
        }

        resume.about?.let { about ->
            if (about.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("О себе", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(about, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        if (resume.skills.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Навыки", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        resume.skills.forEach { skill ->
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

        if (resume.workExperiences.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Опыт работы", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    resume.workExperiences.forEach { exp ->
                        WorkExperienceItem(exp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkExperienceItem(exp: WorkExperienceResponseDto) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(exp.position, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Text(exp.companyName, style = MaterialTheme.typography.bodyMedium, color = Indigo60)
        val period = if (exp.endDate != null) "${exp.startDate} — ${exp.endDate}" else "${exp.startDate} — по н.в."
        Text(period, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        exp.description?.let {
            if (it.isNotBlank()) {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun employmentTypeLabel(type: String) = when (type) {
    "FULL_TIME" -> "Полная занятость"
    "PART_TIME" -> "Частичная занятость"
    "REMOTE" -> "Удалённо"
    "INTERNSHIP" -> "Стажировка"
    else -> type
}
