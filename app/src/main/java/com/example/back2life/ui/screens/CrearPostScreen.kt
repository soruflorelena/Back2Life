package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.back2life.data.model.PostType
import com.example.back2life.ui.viewmodel.CrearPostViewModel

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
    var tipo by remember { mutableStateOf(PostType.COMIDA) }

    LaunchedEffect(estado.postCreadoId) {
        val id = estado.postCreadoId ?: return@LaunchedEffect
        vm.consumeCreated()
        onCreated(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear publicación") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(desc, { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(lugar, { lugar = it }, label = { Text("Lugar") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(precioTexto, { precioTexto = it }, label = { Text("Precio (0 = donación)") }, modifier = Modifier.fillMaxWidth())

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

            Button(
                onClick = {
                    val precio = precioTexto.toDoubleOrNull() ?: 0.0
                    vm.create(titulo, desc, tipo, precio, lugar)
                },
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar")
            }
        }
    }
}