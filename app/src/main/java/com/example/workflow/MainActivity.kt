package com.example.workflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.usecase.auth.LogoutUseCase
import com.example.workflow.presentation.navigation.NavGraph
import com.example.workflow.presentation.navigation.Screen
import com.example.workflow.presentation.ui.theme.WorkFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenDataStore: TokenDataStore

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startRoute = runBlocking {
            if (tokenDataStore.getToken() != null) Screen.Main.route else Screen.Login.route
        }

        setContent {
            WorkFlowTheme {
                NavGraph(
                    tokenDataStore = tokenDataStore,
                    onLogout = { logoutUseCase() },
                    startRoute = startRoute
                )
            }
        }
    }
}
