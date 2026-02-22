package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onAuthed: () -> Unit,
    vm: AuthViewModel = AuthViewModel()
) {
    val estado by vm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }

    LaunchedEffect(estado.esLogueado) {
        if (estado.esLogueado) onAuthed()
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Back 2 Life - Login", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        if (estado.error != null) {
            Text(estado.error!!, color = MaterialTheme.colorScheme.error)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { vm.login(email, contra) }, enabled = !estado.cargando) {
                Text("Iniciar sesión")
            }
            OutlinedButton(onClick = { vm.registrar(email, contra) }, enabled = !estado.cargando) {
                Text("Registrarse")
            }
        }
    }
}