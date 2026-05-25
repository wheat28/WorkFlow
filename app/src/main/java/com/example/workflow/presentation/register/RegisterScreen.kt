package com.example.workflow.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.domain.usecase.RegisterEmployerUseCase
import com.example.workflow.domain.usecase.RegisterSeekerUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerSeekerUseCase: RegisterSeekerUseCase,
    registerEmployerUseCase: RegisterEmployerUseCase,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.Factory(registerSeekerUseCase, registerEmployerUseCase)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var companyName by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Регистрация") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Соискатель") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Работодатель") }
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState != RegisterUiState.Loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState != RegisterUiState.Loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            if (selectedTab == 0) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState != RegisterUiState.Loading)
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Фамилия") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState != RegisterUiState.Loading)
            } else {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Название компании") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState != RegisterUiState.Loading)
                OutlinedTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = { Text("Отрасль") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState != RegisterUiState.Loading)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState != RegisterUiState.Loading,
                    minLines = 2)
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Сайт") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState != RegisterUiState.Loading)
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState != RegisterUiState.Loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Город") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState != RegisterUiState.Loading)

            if (uiState is RegisterUiState.Error) {
                Text(
                    text = (uiState as RegisterUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (uiState == RegisterUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (selectedTab == 0)
                            viewModel.registerSeeker(email, password, firstName, lastName, phone, city)
                        else
                            viewModel.registerEmployer(email, password, companyName, description, website, city, industry, phone)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зарегистрироваться")
                }
            }

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Уже есть аккаунт? Войти")
            }
        }
    }
}
