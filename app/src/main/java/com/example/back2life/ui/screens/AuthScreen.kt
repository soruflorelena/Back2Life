package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(onAuthed: () -> Unit, vm: AuthViewModel = AuthViewModel()) {
    val estado by vm.state.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }
    var esLogin by remember { mutableStateOf(true) }

    // Verificar los datos ingresados
    val emailValido = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val contraValida = contra.length >= 6
    val nombreValido = nombre.trim().isNotEmpty()

    // Activar el botón de registro cuando el formulario esté correcto
    val formularioValido = if (esLogin) {
        emailValido && contraValida
    } else {
        emailValido && contraValida && nombreValido
    }

    LaunchedEffect(estado.esLogueado) { if (estado.esLogueado) onAuthed() }
    LaunchedEffect(nombre, email, contra) { vm.limpiarError() }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Back 2 Life", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        Text(if (esLogin) "Bienvenido de nuevo" else "Crea una cuenta", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 24.dp))

        // Campo de nombre en el registro
        if (!esLogin) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                isError = nombre.isNotEmpty() && !nombreValido,
                supportingText = {
                    if (nombre.isNotEmpty() && !nombreValido) Text("El nombre no puede estar vacío")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Campo de correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isNotEmpty() && !emailValido,
            supportingText = {
                if (email.isNotEmpty() && !emailValido) {
                    Text("Ingresa un correo válido.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Campo de contraseña
        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Candado") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = contra.isNotEmpty() && !contraValida,
            supportingText = {
                if (contra.isNotEmpty() && !contraValida) {
                    Text("Debe tener al menos 6 carácteres")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (estado.error != null) {
            Text(
                text = estado.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (estado.cargando) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        } else {
            Button(
                onClick = {
                    if (esLogin) vm.login(email, contra) { onAuthed() }
                    else vm.registrar(email, contra, nombre) { onAuthed() }
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (esLogin) "Iniciar Sesión" else "Registrarme", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    esLogin = !esLogin
                    vm.limpiarError()
                    nombre = ""
                    email = ""
                    contra = ""
                }
            ) {
                Text(if (esLogin) "¿No tienes cuenta? Regístrate aquí" else "¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}