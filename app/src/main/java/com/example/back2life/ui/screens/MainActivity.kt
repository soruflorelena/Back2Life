package com.example.back2life.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.back2life.ui.navigation.AppNav
import com.example.back2life.ui.navigation.Ruta
import com.example.back2life.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authVm = AuthViewModel()

        setContent {
            val estado by authVm.state.collectAsState()
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNav(start = if (estado.esLogueado) Ruta.Feed.camino else Ruta.Auth.camino)
            }
        }
    }
}
