package com.example.back2life

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.back2life.ui.navigation.AppNav
import com.example.back2life.ui.navigation.Ruta
import com.example.back2life.ui.theme.Back2LifeTheme
import com.example.back2life.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciamos el ViewModel de Autenticación para saber si hay sesión iniciada
        val authVm = AuthViewModel()

        setContent {
            Back2LifeTheme { // Aplicamos tu tema visual
                val estado by authVm.state.collectAsState()

                Surface(color = MaterialTheme.colorScheme.background) {
                    // Si el usuario ya está logueado, lo mandamos al Feed, si no, al Login
                    AppNav(start = if (estado.esLogueado) Ruta.Feed.camino else Ruta.Auth.camino)
                }
            }
        }
    }
}