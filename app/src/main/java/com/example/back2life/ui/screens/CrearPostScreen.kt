package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.back2life.data.model.PostType
import com.example.back2life.ui.viewmodel.CrearPostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPostScreen(
    onCreated: (String) -> Unit,
    onBack: () -> Unit,
    vm: CrearPostViewModel = CrearPostViewModel()
) {
    val estado by vm.estado.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("0") }
    var fechaExpTexto by remember { mutableStateOf("") } // <-- Recuperamos la variable de fecha
    var tipo by remember { mutableStateOf(PostType.COMIDA) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear publicación") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(desc, { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(fechaExpTexto, { fechaExpTexto = it }, label = { Text("Fecha de caducidad (Ej. 12 Dic)") }, modifier = Modifier.fillMaxWidth()) // <-- Nuevo campo visual
            OutlinedTextField(lugar, { lugar = it }, label = { Text("Lugar de entrega") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(precioTexto, { precioTexto = it }, label = { Text("Precio (0 = donación)") }, modifier = Modifier.fillMaxWidth())

            Text("Categoría:", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = tipo == PostType.COMIDA,
                    onClick = { tipo = PostType.COMIDA },
                    label = { Text("Comida") }
                )
                FilterChip(
                    selected = tipo == PostType.MEDICINA,
                    onClick = { tipo = PostType.MEDICINA },
                    label = { Text("Medicina") }
                )
            }

            if (estado.error != null) Text(estado.error!!, color = MaterialTheme.colorScheme.error)
            if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val precio = precioTexto.toDoubleOrNull() ?: 0.0
                    // <-- Pasamos la fechaExpTexto a la función
                    vm.create(titulo, desc, tipo, precio, lugar, fechaExpTexto) { postId ->
                        onCreated(postId)
                    }
                },
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (estado.cargando) "Publicando..." else "Publicar")
            }
        }
    }
}