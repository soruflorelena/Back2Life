package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.back2life.data.model.UserProfile
import com.example.back2life.data.repo.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val authRepo = remember { AuthRepository() }
    var perfil by remember { mutableStateOf<UserProfile?>(null) }
    var cargando by remember { mutableStateOf(true) } // <-- NUEVO: Variable exclusiva para la carga

    LaunchedEffect(Unit) {
        perfil = authRepo.obtenerPerfilActual()
        cargando = false // <-- Apagamos la carga pase lo que pase
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") }, navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Avatar", modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            if (cargando) {
                CircularProgressIndicator() // Solo gira mientras está buscando en la BD
            } else if (perfil != null) {
                Text(perfil!!.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(perfil!!.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                // Si la BD devolvió null (ej. tu cuenta fantasma actual)
                Text("Perfil incompleto o no encontrado.", color = MaterialTheme.colorScheme.error)
                Text("Crea una nueva cuenta para solucionar este problema.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { authRepo.logout(); onLogout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Cerrar Sesión") }
        }
    }
}