package com.example.back2life.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class) // <-- Requerido para TopAppBar en Material 3
@Composable
fun FeedScreen(
    onCreate: () -> Unit,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit,
    vm: FeedViewModel = FeedViewModel()
) {
    val estado by vm.estado.collectAsState()

    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicaciones") },
                actions = {
                    TextButton(onClick = {
                        vm.cerrarSesion() // <-- Ahora el VM maneja esto
                        onLogout()
                    }) { Text("Salir") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) { Text("+") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {

            if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (estado.error != null) Text(estado.error!!, color = MaterialTheme.colorScheme.error)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(estado.posts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpen(post.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // <-- Un poco de sombra mejora el aspecto visual
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(post.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(post.descripcion, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                            Text("Caduca: ${post.fechaExp}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            Text("Lugar: ${post.lugar}", style = MaterialTheme.typography.bodySmall)
                            Text("Precio: $${post.precio}", style = MaterialTheme.typography.bodySmall) // <-- Asumiendo MXN
                        }
                    }
                }
            }
        }
    }
}