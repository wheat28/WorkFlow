package com.example.workflow.presentation.resume

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.domain.usecase.resume.GetResumeByIdUseCase
import com.example.workflow.domain.usecase.resume.SetResumeActiveUseCase
import com.example.workflow.domain.usecase.resume.UpdateResumeUseCase
import com.example.workflow.ui.theme.Coral40
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
fun EditResumeScreen(
    resumeId: String,
    getResumeByIdUseCase: GetResumeByIdUseCase,
    updateResumeUseCase: UpdateResumeUseCase,
    setResumeActiveUseCase: SetResumeActiveUseCase,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: EditResumeViewModel = viewModel(
        factory = EditResumeViewModel.Factory(getResumeByIdUseCase, updateResumeUseCase, setResumeActiveUseCase, resumeId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isActive by viewModel.isActive.collectAsStateWithLifecycle()
    val toggleError by viewModel.toggleError.collectAsStateWithLifecycle()

    var title by rememberSaveable { mutableStateOf("") }
    var position by rememberSaveable { mutableStateOf("") }
    var employmentType by rememberSaveable { mutableStateOf(employmentTypes.first()) }
    var salaryExpected by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var about by rememberSaveable { mutableStateOf("") }
    var fieldsInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is EditResumeViewModel.UiState.Ready && !fieldsInitialized) {
            val r = (uiState as EditResumeViewModel.UiState.Ready).resume
            title = r.title
            position = r.position
            employmentType = r.employmentType
            salaryExpected = r.salaryExpected?.toString() ?: ""
            city = r.city ?: ""
            about = r.about ?: ""
            fieldsInitialized = true
        }
        if (uiState is EditResumeViewModel.UiState.Success) onSaved()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        focusedLabelColor = Indigo60
    )
    val fieldShape = RoundedCornerShape(14.dp)
    val isLoading = uiState is EditResumeViewModel.UiState.Loading
    val isSaving = uiState is EditResumeViewModel.UiState.Saving

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать резюме") },
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
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Indigo60)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isActive) "Активно" else "Скрыто",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isActive) Indigo60 else Coral40
                    )
                    Text(
                        text = if (isActive) "Резюме видно работодателям" else "Резюме скрыто от работодателей",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = { viewModel.setActive(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Indigo60,
                        checkedTrackColor = Indigo60.copy(alpha = 0.3f),
                        uncheckedThumbColor = Coral40,
                        uncheckedTrackColor = Coral40.copy(alpha = 0.3f)
                    )
                )
            }

            if (toggleError != null) {
                Text(
                    toggleError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                LaunchedEffect(toggleError) { viewModel.clearToggleError() }
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Название резюме *") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = position, onValueChange = { position = it },
                label = { Text("Желаемая должность *") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded && !isSaving,
                onExpandedChange = { if (!isSaving) expanded = it }
            ) {
                OutlinedTextField(
                    value = employmentLabel(employmentType), onValueChange = {},
                    readOnly = true, label = { Text("Тип занятости") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isSaving, shape = fieldShape, colors = fieldColors
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
                value = salaryExpected, onValueChange = { salaryExpected = it },
                label = { Text("Ожидаемая зарплата") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Город") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = about, onValueChange = { about = it },
                label = { Text("О себе") },
                modifier = Modifier.fillMaxWidth(), minLines = 3,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )

            if (uiState is EditResumeViewModel.UiState.Error) {
                Text(
                    (uiState as EditResumeViewModel.UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isSaving) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            } else {
                Button(
                    onClick = { viewModel.save(title, position, employmentType, salaryExpected, city, about) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
                ) {
                    Text("Сохранить", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
