package com.example.back2life.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.example.back2life.ui.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onCreate: () -> Unit,
    onOpen: (String) -> Unit,
    onProfile: () -> Unit,
    vm: FeedViewModel = FeedViewModel()
) {
    val estado by vm.estado.collectAsState()

    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicaciones", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("Nueva Publicación")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {

            if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 8.dp))
            if (!estado.cargando && estado.posts.isEmpty()) {
                Text("Aún no hay publicaciones.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.secondary)
            }
            if (estado.error != null) Text(estado.error!!, color = MaterialTheme.colorScheme.error)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(estado.posts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                                Text(
                                    text = if (post.precio <= 0) "GRATIS" else "$${post.precio}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold
                                )
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
                                Text(
                                    text = "$tipoFormateado",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = post.lugar,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}