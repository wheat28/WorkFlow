package com.example.workflow.presentation.employer

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.employer.UpdateEmployerUseCase
import com.example.workflow.ui.theme.Indigo60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployerProfileScreen(
    employerId: String,
    getEmployerByIdUseCase: GetEmployerByIdUseCase,
    updateEmployerUseCase: UpdateEmployerUseCase,
    tokenDataStore: TokenDataStore,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: EditEmployerProfileViewModel = viewModel(
        factory = EditEmployerProfileViewModel.Factory(getEmployerByIdUseCase, updateEmployerUseCase, tokenDataStore, employerId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var companyName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var website by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var industry by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var fieldsInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is EditEmployerProfileViewModel.UiState.Ready && !fieldsInitialized) {
            val e = (uiState as EditEmployerProfileViewModel.UiState.Ready).employer
            companyName = e.companyName
            description = e.description ?: ""
            website = e.website ?: ""
            city = e.city ?: ""
            industry = e.industry ?: ""
            phone = e.phone ?: ""
            fieldsInitialized = true
        }
        if (uiState is EditEmployerProfileViewModel.UiState.Success) onSaved()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        focusedLabelColor = Indigo60
    )
    val fieldShape = RoundedCornerShape(14.dp)
    val isLoading = uiState is EditEmployerProfileViewModel.UiState.Loading
    val isSaving = uiState is EditEmployerProfileViewModel.UiState.Saving

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль компании") },
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
            OutlinedTextField(
                value = companyName, onValueChange = { companyName = it },
                label = { Text("Название компании *") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("О компании") },
                modifier = Modifier.fillMaxWidth(), minLines = 3,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Город") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = industry, onValueChange = { industry = it },
                label = { Text("Отрасль") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = website, onValueChange = { website = it },
                label = { Text("Сайт") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                enabled = !isSaving, shape = fieldShape, colors = fieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            if (uiState is EditEmployerProfileViewModel.UiState.Error) {
                Text(
                    (uiState as EditEmployerProfileViewModel.UiState.Error).message,
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
                    onClick = { viewModel.save(companyName, description, website, city, industry, phone) },
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
