package com.example.parteincidenciacompose.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapturaMatriculaScreen(onBack: () -> Unit = {}, onMatriculaDetected: (String) -> Unit = {}) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var processing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capturar matrícula") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            if (!hasCameraPermission) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("La app necesita permiso de cámara para capturar matrículas")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            // Request permission via ActivityResult from the host activity; for now ask user to grant manually
                        }) { Text("Conceder permiso (desde ajustes)") }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        onImageCaptured = { imageProxy ->
                            if (processing) { imageProxy.close(); return@CameraPreview }
                            processing = true
                            processImageProxy(context, imageProxy) { detected ->
                                processing = false
                                if (!detected.isNullOrBlank()) {
                                    onMatriculaDetected(detected)
                                }
                            }
                        },
                        cameraExecutor = cameraExecutor
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPreview(onImageCaptured: (ImageProxy) -> Unit, cameraExecutor: java.util.concurrent.Executor) {
    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalContext.current as androidx.lifecycle.LifecycleOwner)
    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner.value, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Error binding camera use cases", e)
            }
            // Simple capture button overlay logic: when user taps preview, capture
            previewView.setOnClickListener {
                imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        onImageCaptured(image)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraPreview", "Image capture failed", exception)
                    }
                })
            }
        }, ContextCompat.getMainExecutor(ctx))
        previewView
    }, modifier = Modifier.fillMaxSize())
}

private fun processImageProxy(context: Context, imageProxy: ImageProxy, onResult: (String?) -> Unit) {
    try {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val raw = visionText.text ?: ""
                    val plate = extractPlate(raw)
                    onResult(plate)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e("processImageProxy", "Text recognition failed", e)
                    onResult(null)
                    imageProxy.close()
                }
        } else {
            onResult(null)
            imageProxy.close()
        }
    } catch (e: Exception) {
        Log.e("processImageProxy", "Error processing image proxy", e)
        try { imageProxy.close() } catch (_: Exception) {}
        onResult(null)
    }
}

// Very simple plate regex: matches modern EU style 4 digits + 3 letters (e.g., 1234ABC) or common patterns
private fun extractPlate(text: String): String? {
    val normalized = text.replace("\n", " ").replace("-", "").replace(" ", "")
    val patterns = listOf(
        Pattern.compile("\\b\\d{4}[A-Z]{3}\\b"), // 1234ABC
        Pattern.compile("\\b[A-Z]{1,2}\\d{1,4}[A-Z]{0,3}\\b")
    )
    for (p in patterns) {
        val m = p.matcher(normalized.uppercase())
        if (m.find()) return m.group(0)
    }
    return null
}
