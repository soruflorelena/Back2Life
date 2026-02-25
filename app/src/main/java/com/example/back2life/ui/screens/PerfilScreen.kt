package com.example.back2life.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpen: (String) -> Unit
) {
    val vm = remember { PerfilViewModel() }
    val estado by vm.estado.collectAsState()
    var mostrarDialogoEdicion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargarDatos() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }, navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            // Cabecera del perfil
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Avatar", modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                if (estado.cargando) {
                    CircularProgressIndicator()
                } else if (estado.perfil != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(estado.perfil!!.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { mostrarDialogoEdicion = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar nombre", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    Text(estado.perfil!!.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { vm.logout(); onLogout() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cerrar Sesión") }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Mis Publicaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Lista de publicaciones
            if (!estado.cargando && estado.misPosts.isEmpty()) {
                Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Aún no tienes publicaciones.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(16.dp)) {
                items(estado.misPosts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpen(post.id) },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(post.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                if (post.precio <= 0) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = androidx.compose.ui.graphics.Color(0xFF4CAF50), contentColor = androidx.compose.ui.graphics.Color.White) {
                                        Text("DONACIÓN", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelMedium)
                                    }
                                } else {
                                    Text("$${post.precio} MXN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val estadoFormat = post.estado.name.lowercase().replaceFirstChar { it.uppercase() }
                            Text("Estado: $estadoFormat", style = MaterialTheme.typography.labelMedium, color = if(post.estado.name == "DISPONIBLE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }

        // Editar nombre ventana
        if (mostrarDialogoEdicion && estado.perfil != null) {
            var editNombre by remember { mutableStateOf(estado.perfil!!.nombre) }
            AlertDialog(
                onDismissRequest = { mostrarDialogoEdicion = false },
                title = { Text("Cambiar Nombre") },
                text = {
                    OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nuevo nombre") }, singleLine = true)
                },
                confirmButton = {
                    Button(onClick = {
                        vm.actualizarNombre(editNombre)
                        mostrarDialogoEdicion = false
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoEdicion = false }) { Text("Cancelar") }
                }
            )
        }
    }
}