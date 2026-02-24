package com.example.back2life.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.PostDetalleViewModel

// CORRECCIÓN: Se añade esta línea para permitir el uso de TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetalleScreen(
    postId: String,
    onBack: () -> Unit,
    vm: PostDetalleViewModel = PostDetalleViewModel()
) {
    val estado by vm.estado.collectAsState()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) { vm.cargar(postId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            val post = estado.post
            if (post == null) {
                if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
                if (estado.error != null) Text(estado.error!!, color = MaterialTheme.colorScheme.error)
                return@Column
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(post.titulo, style = MaterialTheme.typography.titleLarge)
                    Text(post.descripcion)
                    Text("Tipo: ${post.tipo}")
                    Text("Lugar: ${post.lugar}")
                    Text("Precio: ${post.precio}")
                    Text("Estado: ${post.estado}")

                    if (vm.esAutor()) {
                        Button(onClick = { vm.marcarEntregado(postId) }) {
                            Text("Marcar como entregada")
                        }
                    }
                }
            }

            Text("Comentarios", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Escribe un comentario") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        vm.addComentario(postId, commentText)
                        commentText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Enviar") }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                items(estado.comentarios) { c ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp)) {
                            Text(c.autorNombre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(c.texto)
                        }
                    }
                }
            }
        }
    }
}