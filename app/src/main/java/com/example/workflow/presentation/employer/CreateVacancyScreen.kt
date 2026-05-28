package com.example.workflow.presentation.employer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.workflow.domain.usecase.vacancy.CreateVacancyUseCase
import com.example.workflow.ui.theme.Indigo60

private val employmentTypes = listOf("FULL_TIME", "PART_TIME", "REMOTE", "INTERNSHIP")
private val experienceOptions = listOf("NO_EXPERIENCE", "1_3", "3_6", "6_PLUS")

fun employmentLabel(type: String) = when (type) {
    "FULL_TIME" -> "Полная занятость"
    "PART_TIME" -> "Частичная занятость"
    "REMOTE" -> "Удалённо"
    "INTERNSHIP" -> "Стажировка"
    else -> type
}

fun experienceLabel(value: String) = when (value) {
    "NO_EXPERIENCE" -> "Без опыта"
    "1_3" -> "1–3 года"
    "3_6" -> "3–6 лет"
    "6_PLUS" -> "Более 6 лет"
    else -> value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVacancyScreen(
    createVacancyUseCase: CreateVacancyUseCase,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val viewModel: CreateVacancyViewModel = viewModel(
        factory = CreateVacancyViewModel.Factory(createVacancyUseCase)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var employmentType by remember { mutableStateOf(employmentTypes.first()) }
    var experience by remember { mutableStateOf(experienceOptions.first()) }
    var city by remember { mutableStateOf("") }
    var salaryFrom by remember { mutableStateOf("") }
    var salaryTo by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("RUB") }

    LaunchedEffect(uiState) {
        if (uiState is CreateVacancyViewModel.UiState.Success) {
            viewModel.resetState()
            onCreated()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        unfocusedBorderColor = Indigo60.copy(alpha = 0.4f),
        focusedLabelColor = Indigo60
    )
    val fieldShape = RoundedCornerShape(14.dp)
    val isLoading = uiState is CreateVacancyViewModel.UiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая вакансия") },
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
                label = { Text("Название вакансии *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            EnumDropdown(
                label = "Тип занятости",
                options = employmentTypes,
                selected = employmentType,
                onSelected = { employmentType = it },
                labelOf = ::employmentLabel,
                enabled = !isLoading
            )

            EnumDropdown(
                label = "Опыт работы",
                options = experienceOptions,
                selected = experience,
                onSelected = { experience = it },
                labelOf = ::experienceLabel,
                enabled = !isLoading
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

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = salaryFrom,
                    onValueChange = { salaryFrom = it },
                    label = { Text("Зарплата от") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = fieldShape,
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = salaryTo,
                    onValueChange = { salaryTo = it },
                    label = { Text("до") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = fieldShape,
                    colors = fieldColors
                )
            }

            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Валюта") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = fieldShape,
                colors = fieldColors
            )

            if (uiState is CreateVacancyViewModel.UiState.Error) {
                Text(
                    text = (uiState as CreateVacancyViewModel.UiState.Error).message,
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
                    onClick = {
                        viewModel.create(title, description, employmentType, experience, city, salaryFrom, salaryTo, currency)
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
                ) {
                    Text("Опубликовать", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnumDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    labelOf: (String) -> String,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = it }) {
        OutlinedTextField(
            value = labelOf(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Indigo60,
                unfocusedBorderColor = Indigo60.copy(alpha = 0.4f),
                focusedLabelColor = Indigo60
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labelOf(option)) },
                    onClick = { onSelected(option); expanded = false }
                )
            }
        }
    }
}
