package com.example.back2life.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // <-- IMPORTANTE: Requiere la librería Coil en tu build.gradle
import com.example.back2life.data.model.PostType
import com.example.back2life.ui.viewmodel.CrearPostViewModel
import java.io.ByteArrayOutputStream

// FUNCIÓN MÁGICA: Exprime la imagen y la convierte a texto Base64
fun comprimirUriABase64(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val original = BitmapFactory.decodeStream(inputStream)

        // Redimensionamos la foto para que no rebase el límite de Firestore (1 MB)
        val max = 500f // Tamaño máximo en píxeles (calidad de miniatura)
        val ratio = original.width.toFloat() / original.height.toFloat()
        val width = if (ratio > 1) max.toInt() else (max * ratio).toInt()
        val height = if (ratio > 1) (max / ratio).toInt() else max.toInt()

        val escalada = Bitmap.createScaledBitmap(original, width, height, true)

        val outputStream = ByteArrayOutputStream()
        // La comprimimos a formato JPEG con un 50% de calidad
        escalada.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val bytes = outputStream.toByteArray()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) { "" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPostScreen(
    onCreated: (String) -> Unit,
    onBack: () -> Unit,
    vm: CrearPostViewModel = CrearPostViewModel()
) {
    val estado by vm.estado.collectAsState()
    val context = LocalContext.current // Lo necesitamos para procesar la imagen

    var titulo by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("0") }
    var fechaExpTexto by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(PostType.COMIDA) }

    // VARIABLES PARA LA SELECCIÓN DE FOTO
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imagenSeleccionada = uri }
    )

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

            // BOTÓN PARA SELECCIONAR FOTO
            Button(
                onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Seleccionar una Foto")
            }

            // VISTA PREVIA DE LA FOTO EN LA PANTALLA
            if (imagenSeleccionada != null) {
                AsyncImage(
                    model = imagenSeleccionada,
                    contentDescription = "Vista previa",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(desc, { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(fechaExpTexto, { fechaExpTexto = it }, label = { Text("Fecha de caducidad (Ej. 12 Dic)") }, modifier = Modifier.fillMaxWidth())
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

                    // JUSTO AQUÍ: Exprimimos la foto a texto en el momento de darle click
                    val base64String = imagenSeleccionada?.let { comprimirUriABase64(context, it) } ?: ""

                    // Le pasamos la foto convertida a texto a tu ViewModel
                    vm.create(titulo, desc, tipo, precio, lugar, fechaExpTexto, base64String) { postId ->
                        onCreated(postId)
                    }
                },
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (estado.cargando) "Publicando..." else "Publicar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}