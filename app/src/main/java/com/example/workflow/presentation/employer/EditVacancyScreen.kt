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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.vacancy.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.vacancy.SetVacancyActiveUseCase
import com.example.workflow.domain.usecase.vacancy.UpdateVacancyUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVacancyScreen(
    vacancyId: String,
    getVacancyByIdUseCase: GetVacancyByIdUseCase,
    updateVacancyUseCase: UpdateVacancyUseCase,
    deleteVacancyUseCase: DeleteVacancyUseCase,
    setVacancyActiveUseCase: SetVacancyActiveUseCase,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit
) {
    val viewModel: EditVacancyViewModel = viewModel(
        factory = EditVacancyViewModel.Factory(
            getVacancyByIdUseCase, updateVacancyUseCase, deleteVacancyUseCase, setVacancyActiveUseCase, vacancyId
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isActive by viewModel.isActive.collectAsStateWithLifecycle()
    val toggleError by viewModel.toggleError.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            is EditVacancyViewModel.UiState.Saved -> onSaved()
            is EditVacancyViewModel.UiState.Deleted -> onDeleted()
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать") },
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
            is EditVacancyViewModel.UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            }
            is EditVacancyViewModel.UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is EditVacancyViewModel.UiState.Ready,
            is EditVacancyViewModel.UiState.Saving -> {
                val vacancy = when (state) {
                    is EditVacancyViewModel.UiState.Ready -> state.vacancy
                    is EditVacancyViewModel.UiState.Saving -> state.vacancy
                    else -> return@Scaffold
                }
                val isSaving = state is EditVacancyViewModel.UiState.Saving
                EditVacancyForm(
                    vacancy = vacancy,
                    isSaving = isSaving,
                    isActive = isActive,
                    toggleError = toggleError,
                    onSetActive = { viewModel.setActive(it) },
                    onClearToggleError = { viewModel.clearToggleError() },
                    onSave = { title, desc, empType, exp, city, salFrom, salTo, cur ->
                        viewModel.save(title, desc, empType, exp, city, salFrom, salTo, cur)
                    },
                    onDelete = { viewModel.delete() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditVacancyForm(
    vacancy: VacancyResponseDto,
    isSaving: Boolean,
    isActive: Boolean,
    toggleError: String?,
    onSetActive: (Boolean) -> Unit,
    onClearToggleError: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf(vacancy.title) }
    var description by rememberSaveable { mutableStateOf(vacancy.description) }
    var employmentType by rememberSaveable { mutableStateOf(vacancy.employmentType) }
    var experience by rememberSaveable { mutableStateOf(vacancy.experience) }
    var city by rememberSaveable { mutableStateOf(vacancy.city.orEmpty()) }
    var salaryFrom by rememberSaveable { mutableStateOf(vacancy.salaryFrom?.toString() ?: "") }
    var salaryTo by rememberSaveable { mutableStateOf(vacancy.salaryTo?.toString() ?: "") }
    var currency by rememberSaveable { mutableStateOf(vacancy.currency ?: "RUB") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        unfocusedBorderColor = Indigo60.copy(alpha = 0.4f),
        focusedLabelColor = Indigo60
    )
    val fieldShape = RoundedCornerShape(14.dp)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить вакансию?") },
            text = { Text("Это действие нельзя отменить. Все отклики на вакансию также будут удалены.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
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
                    text = if (isActive) "Активна" else "Закрыта",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isActive) Green40 else Coral40
                )
                Text(
                    text = if (isActive) "Вакансия открыта для откликов" else "Приём откликов приостановлен",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isActive,
                onCheckedChange = onSetActive,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Green40,
                    checkedTrackColor = Green40.copy(alpha = 0.3f),
                    uncheckedThumbColor = Coral40,
                    uncheckedTrackColor = Coral40.copy(alpha = 0.3f)
                )
            )
        }

        if (toggleError != null) {
            Text(
                toggleError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            LaunchedEffect(toggleError) { onClearToggleError() }
        }

        if (vacancy.applicationCount > 0) {
            Text(
                text = "Откликов: ${vacancy.applicationCount}",
                style = MaterialTheme.typography.bodySmall,
                color = Indigo60
            )
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название вакансии *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving,
            shape = fieldShape,
            colors = fieldColors
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            enabled = !isSaving,
            shape = fieldShape,
            colors = fieldColors
        )

        EnumDropdown(
            label = "Тип занятости",
            options = listOf("FULL_TIME", "PART_TIME", "REMOTE", "INTERNSHIP"),
            selected = employmentType,
            onSelected = { employmentType = it },
            labelOf = ::employmentLabel,
            enabled = !isSaving
        )

        EnumDropdown(
            label = "Опыт работы",
            options = listOf("NO_EXPERIENCE", "1_3", "3_6", "6_PLUS"),
            selected = experience,
            onSelected = { experience = it },
            labelOf = ::experienceLabel,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Город") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving,
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
                enabled = !isSaving,
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
                enabled = !isSaving,
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
            enabled = !isSaving,
            shape = fieldShape,
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isSaving) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Indigo60)
            }
        } else {
            Button(
                onClick = { onSave(title, description, employmentType, experience, city, salaryFrom, salaryTo, currency) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
            ) {
                Text("Сохранить", style = MaterialTheme.typography.labelLarge)
            }

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Coral40),
                border = androidx.compose.foundation.BorderStroke(1.dp, Coral40)
            ) {
                Text("Удалить вакансию", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
