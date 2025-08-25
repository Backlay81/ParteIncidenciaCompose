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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalLifecycleOwner
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

    // initialize permission state
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capturar matrícula") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            if (!hasCameraPermission) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Card(modifier = Modifier.padding(16.dp), elevation = CardDefaults.cardElevation(6.dp)) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("La app necesita permiso de cámara para capturar matrículas")
                            Spacer(modifier = Modifier.height(8.dp))
                            val azulito = androidx.compose.ui.graphics.Color(0xFF1976D2)
                            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }, colors = ButtonDefaults.buttonColors(containerColor = azulito)) {
                                Text("Conceder permiso", color = androidx.compose.ui.graphics.Color.White)
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    var takePictureLambda: (() -> Unit)? = null
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
                        cameraExecutor = cameraExecutor,
                        onTakePictureReady = { lp -> takePictureLambda = lp }
                    )

                    // Capture button overlay (styled like app buttons)
                    val azulito = androidx.compose.ui.graphics.Color(0xFF1976D2)
                    Button(
                        onClick = { takePictureLambda?.invoke() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 24.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        contentPadding = PaddingValues(18.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Capturar", tint = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    onImageCaptured: (ImageProxy) -> Unit,
    cameraExecutor: java.util.concurrent.Executor,
    onTakePictureReady: (takePicture: () -> Unit) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Error binding camera use cases", e)
            }
            // Expose a takePicture lambda for the UI to call
            val takePicture = {
                try {
                    imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            onImageCaptured(image)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraPreview", "Image capture failed", exception)
                        }
                    })
                } catch (e: Exception) {
                    Log.e("CameraPreview", "takePicture error", e)
                }
            }
            onTakePictureReady(takePicture as () -> Unit)
        }, ContextCompat.getMainExecutor(ctx))
        previewView
    }, modifier = Modifier.fillMaxSize())
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
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
