package com.example.back2life.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.back2life.ui.viewmodel.PostDetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetalleScreen(postId: String, onBack: () -> Unit) {
    val vm = remember { PostDetalleViewModel() }
    val estado by vm.estado.collectAsState()

    var commentText by remember { mutableStateOf("") }
    var mostrarDialogoEdicion by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        vm.cargar(postId)
    }

    LaunchedEffect(estado.fueEliminado) {
        if (estado.fueEliminado) onBack()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle") }, navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }) },
        bottomBar = {
            if (estado.post != null) {
                BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Escribe un mensaje...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        IconButton(onClick = {
                            if (commentText.isNotBlank()) {
                                vm.addComentario(postId, commentText)
                                commentText = ""
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            val post = estado.post

            if (post == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (estado.cargando) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando detalles...")
                    } else if (estado.error != null) {
                        Text("Ocurrió un error:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(estado.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        Text("No se encontró la publicación.")
                    }
                }
                return@Column
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // --- DECODIFICACIÓN DE LA IMAGEN EN DETALLES ---
                    if (post.fotoBase64.isNotBlank() && post.fotoBase64.length > 100) {
                        try {
                            val imageBytes = Base64.decode(post.fotoBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Foto",
                                    modifier = Modifier.fillMaxWidth().height(220.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } catch (e: Exception) { }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(post.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                        if (post.precio <= 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                contentColor = androidx.compose.ui.graphics.Color.White
                            ) {
                                Text("DONACIÓN", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelLarge)
                            }
                        } else {
                            Text("$${post.precio} MXN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(post.descripcion, style = MaterialTheme.typography.bodyLarge)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

                    val tipoFormat = post.tipo.name.lowercase().replaceFirstChar { it.uppercase() }
                    Text("📦 Categoría: $tipoFormat", style = MaterialTheme.typography.bodyMedium)
                    Text("📍 Lugar: ${post.lugar}", style = MaterialTheme.typography.bodyMedium)
                    Text("⏳ Caduca: ${post.fechaExp}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)

                    val estadoFormat = post.estado.name.lowercase().replaceFirstChar { it.uppercase() }
                    Text("📌 Estado: $estadoFormat", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                    if (vm.esAutor()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Acciones del creador:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

                        if (post.estado.name == "DISPONIBLE") {
                            Button(
                                onClick = { vm.marcarEntregado(postId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Marcar como Entregada") }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { mostrarDialogoEdicion = true },
                                modifier = Modifier.weight(1f)
                            ) { Text("Editar") }

                            OutlinedButton(
                                onClick = { vm.eliminarPost(postId) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }

            // LISTA DE COMENTARIOS
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                items(estado.comentarios) { c ->
                    val isMyComment = c.autorId == vm.currentUserId()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = if (isMyComment) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = if (isMyComment) 16.dp else 0.dp,
                                bottomEnd = if (isMyComment) 0.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(containerColor = if (isMyComment) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(c.autorNombre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(c.texto, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        // --- VENTANA EMERGENTE PARA EDITAR ---
        if (mostrarDialogoEdicion && estado.post != null) {
            var editTitulo by remember { mutableStateOf(estado.post!!.titulo) }
            var editDesc by remember { mutableStateOf(estado.post!!.descripcion) }
            var editPrecio by remember { mutableStateOf(estado.post!!.precio.toString()) }

            AlertDialog(
                onDismissRequest = { mostrarDialogoEdicion = false },
                title = { Text("Editar Publicación") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = editTitulo, onValueChange = { editTitulo = it }, label = { Text("Título") }, singleLine = true)
                        OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Descripción") }, minLines = 2)
                        OutlinedTextField(value = editPrecio, onValueChange = { editPrecio = it }, label = { Text("Precio (0 = Donación)") }, singleLine = true)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val p = editPrecio.toDoubleOrNull() ?: 0.0
                        vm.editarPost(postId, editTitulo, editDesc, p)
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