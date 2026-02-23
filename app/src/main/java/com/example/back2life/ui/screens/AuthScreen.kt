package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var esLogin by remember { mutableStateOf(true) } // <-- Controla si mostramos Login o Registro

    LaunchedEffect(estado.esLogueado) {
        if (estado.esLogueado) onAuthed()
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Back 2 Life",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (esLogin) "Bienvenido de nuevo" else "Crea una cuenta",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (estado.error != null) {
            Text(estado.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (estado.cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { if (esLogin) vm.login(email, contra) else vm.registrar(email, contra) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esLogin) "Iniciar Sesión" else "Registrarme")
            }

            TextButton(onClick = { esLogin = !esLogin }) {
                Text(if (esLogin) "¿No tienes cuenta? Regístrate aquí" else "¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}