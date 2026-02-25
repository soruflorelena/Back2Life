package com.example.back2life.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpen: (String) -> Unit
) {
    val vm = remember { FeedViewModel() }
    val estado by vm.estado.collectAsState()

    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back 2 Life", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Crear publicación") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            // Barra de los filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = estado.filtroActual == "Todos",
                    onClick = { vm.cambiarFiltro("Todos") },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = estado.filtroActual == "COMIDA",
                    onClick = { vm.cambiarFiltro("COMIDA") },
                    label = { Text("Comida") }
                )
                FilterChip(
                    selected = estado.filtroActual == "MEDICINA",
                    onClick = { vm.cambiarFiltro("MEDICINA") },
                    label = { Text("Medicina") }
                )
            }

            if (estado.cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (estado.postsFiltrados.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay publicaciones disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.postsFiltrados) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { onOpen(post.id) },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = post.titulo,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (post.precio <= 0) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                            contentColor = androidx.compose.ui.graphics.Color.White
                                        ) {
                                            Text(
                                                text = "DONACIÓN",
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                fontWeight = FontWeight.ExtraBold,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "$${post.precio} MXN",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = post.descripcion,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val tipoFormateado = post.tipo.name.lowercase().replaceFirstChar { it.uppercase() }
                                    Text(text = tipoFormateado, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                    Text(text = post.lugar, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

