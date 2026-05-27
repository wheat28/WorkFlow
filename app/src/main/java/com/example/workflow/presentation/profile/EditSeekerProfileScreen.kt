package com.example.workflow.presentation.profile

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
import com.example.workflow.domain.usecase.seeker.GetSeekerByIdUseCase
import com.example.workflow.domain.usecase.seeker.UpdateSeekerUseCase
import com.example.workflow.ui.theme.Indigo60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSeekerProfileScreen(
    seekerId: String,
    getSeekerByIdUseCase: GetSeekerByIdUseCase,
    updateSeekerUseCase: UpdateSeekerUseCase,
    tokenDataStore: TokenDataStore,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: EditSeekerProfileViewModel = viewModel(
        factory = EditSeekerProfileViewModel.Factory(
            getSeekerByIdUseCase, updateSeekerUseCase, tokenDataStore, seekerId
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var about by rememberSaveable { mutableStateOf("") }
    var fieldsInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is EditSeekerProfileViewModel.UiState.Ready && !fieldsInitialized) {
            firstName = viewModel.firstName.value
            lastName = viewModel.lastName.value
            phone = viewModel.phone.value
            city = viewModel.city.value
            about = viewModel.about.value
            fieldsInitialized = true
        }
        if (uiState is EditSeekerProfileViewModel.UiState.Success) onSaved()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        focusedLabelColor = Indigo60
    )
    val fieldShape = RoundedCornerShape(14.dp)
    val isLoading = uiState is EditSeekerProfileViewModel.UiState.Loading
    val isSaving = uiState is EditSeekerProfileViewModel.UiState.Saving

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать профиль") },
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
            Text(
                text = viewModel.email.collectAsStateWithLifecycle().value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Имя *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving,
                shape = fieldShape,
                colors = fieldColors
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Фамилия *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving,
                shape = fieldShape,
                colors = fieldColors
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving,
                shape = fieldShape,
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
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
            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("О себе") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isSaving,
                shape = fieldShape,
                colors = fieldColors
            )

            if (uiState is EditSeekerProfileViewModel.UiState.Error) {
                Text(
                    (uiState as EditSeekerProfileViewModel.UiState.Error).message,
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
                    onClick = { viewModel.save(firstName, lastName, phone, city, about) },
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
