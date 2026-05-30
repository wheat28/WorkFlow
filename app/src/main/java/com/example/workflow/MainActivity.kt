package com.example.workflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.workflow.navigation.AppNavGraph
import com.example.workflow.ui.theme.WorkFlowTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as WorkFlowApp
        val startRoute = runBlocking {
            if (app.tokenDataStore.getToken() != null) "main" else "login"
        }

        setContent {
            WorkFlowTheme {
                AppNavGraph(app = app, startRoute = startRoute)
            }
        }
    }
}
