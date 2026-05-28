package com.example.workflow.presentation.resume

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.domain.usecase.resume.CreateResumeUseCase
import com.example.workflow.ui.theme.Indigo60

private val employmentTypes = listOf("FULL_TIME", "PART_TIME", "REMOTE", "INTERNSHIP")

private fun employmentLabel(type: String) = when (type) {
    "FULL_TIME" -> "Полная занятость"
    "PART_TIME" -> "Частичная занятость"
    "REMOTE" -> "Удалённо"
    "INTERNSHIP" -> "Стажировка"
    else -> type
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResumeScreen(
    createResumeUseCase: CreateResumeUseCase,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val viewModel: CreateResumeViewModel = viewModel(
        factory = CreateResumeViewModel.Factory(createResumeUseCase)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var employmentType by remember { mutableStateOf(employmentTypes.first()) }
    var salaryExpected by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is CreateResumeViewModel.UiState.Success) {
            viewModel.resetState()
            onCreated()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        focusedLabelColor = Indigo60
    )
    val fieldShape = RoundedCornerShape(14.dp)
    val isLoading = uiState is CreateResumeViewModel.UiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать резюме") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название резюме *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Желаемая должность *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded && !isLoading,
                onExpandedChange = { if (!isLoading) expanded = it }
            ) {
                OutlinedTextField(
                    value = employmentLabel(employmentType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Тип занятости") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isLoading,
                    shape = fieldShape,
                    colors = fieldColors
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    employmentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(employmentLabel(type)) },
                            onClick = { employmentType = type; expanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = salaryExpected,
                onValueChange = { salaryExpected = it },
                label = { Text("Ожидаемая зарплата") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = fieldShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Город") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("О себе") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            if (uiState is CreateResumeViewModel.UiState.Error) {
                Text(
                    text = (uiState as CreateResumeViewModel.UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            } else {
                Button(
                    onClick = { viewModel.create(title, position, employmentType, salaryExpected, city, about) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
                ) {
                    Text("Сохранить резюме", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
