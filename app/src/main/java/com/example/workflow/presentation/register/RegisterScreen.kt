package com.example.workflow.presentation.register

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Indigo60,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Indigo60
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Соискатель",
                            color = if (selectedTab == 0) Indigo60 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Работодатель",
                            color = if (selectedTab == 1) Indigo60 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Indigo60,
                focusedLabelColor = Indigo60
            )
            val fieldShape = RoundedCornerShape(14.dp)

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, enabled = uiState != RegisterUiState.Loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Пароль") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, enabled = uiState != RegisterUiState.Loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                shape = fieldShape, colors = fieldColors
            )

            if (selectedTab == 0) {
                OutlinedTextField(
                    value = firstName, onValueChange = { firstName = it },
                    label = { Text("Имя") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, enabled = uiState != RegisterUiState.Loading,
                    shape = fieldShape, colors = fieldColors
                )
                OutlinedTextField(
                    value = lastName, onValueChange = { lastName = it },
                    label = { Text("Фамилия") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, enabled = uiState != RegisterUiState.Loading,
                    shape = fieldShape, colors = fieldColors
                )
            } else {
                OutlinedTextField(
                    value = companyName, onValueChange = { companyName = it },
                    label = { Text("Название компании") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, enabled = uiState != RegisterUiState.Loading,
                    shape = fieldShape, colors = fieldColors
                )
                OutlinedTextField(
                    value = industry, onValueChange = { industry = it },
                    label = { Text("Отрасль") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, enabled = uiState != RegisterUiState.Loading,
                    shape = fieldShape, colors = fieldColors
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(),
                    enabled = uiState != RegisterUiState.Loading,
                    minLines = 2, shape = fieldShape, colors = fieldColors
                )
                OutlinedTextField(
                    value = website, onValueChange = { website = it },
                    label = { Text("Сайт") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, enabled = uiState != RegisterUiState.Loading,
                    shape = fieldShape, colors = fieldColors
                )
            }

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Телефон") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, enabled = uiState != RegisterUiState.Loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = fieldShape, colors = fieldColors
            )
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Город") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, enabled = uiState != RegisterUiState.Loading,
                shape = fieldShape, colors = fieldColors
            )

            if (uiState is RegisterUiState.Error) {
                Text(
                    text = (uiState as RegisterUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (uiState == RegisterUiState.Loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            } else {
                Button(
                    onClick = {
                        if (selectedTab == 0)
                            viewModel.registerSeeker(email, password, firstName, lastName, phone, city)
                        else
                            viewModel.registerEmployer(email, password, companyName, description, website, city, industry, phone)
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
                ) {
                    Text("Зарегистрироваться", style = MaterialTheme.typography.labelLarge)
                }
            }

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Уже есть аккаунт? Войти", color = Indigo60)
            }
        }
    }
}
