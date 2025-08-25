package com.example.parteincidenciacompose.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RellenarPlantillaScreen(
    onBack: () -> Unit = {},
    context: Context = androidx.compose.ui.platform.LocalContext.current
) {
    val json = remember { loadFieldMap(context) }
    val fieldsArray = json.optJSONArray("fields") ?: JSONArray()
    val values = remember { mutableStateMapOf<String, String>() }
    // initialize empty values if needed
    for (i in 0 until fieldsArray.length()) {
        val f = fieldsArray.optJSONObject(i) ?: continue
        val name = f.optString("pdfName")
        if (!values.containsKey(name)) values[name] = ""
    }
    // try load previously saved values
    remember {
        try {
            val file = context.openFileInput("filled_values.json")
            val txt = file.bufferedReader().use { it.readText() }
            val jo = JSONObject(txt)
            val keys = jo.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                values[k] = jo.optString(k, "")
            }
        } catch (e: Exception) {
            // ignore if not present
        }
    }

    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    val previewBitmap = remember { loadPreviewBitmap(context) }

    val azulito = Color(0xFF1A73E8)
    val textoBlanco = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Rellenar plantilla",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulito,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(azulito)
                    .height(100.dp)
                    .padding(horizontal = 4.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val saved = saveValuesJson(context, values)
                            Toast.makeText(context, if (saved) "Valores guardados" else "Error guardando", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulito),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = textoBlanco,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Guardar",
                            color = textoBlanco,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        val success = PdfFillUtil.exportPdfPlaceholder(context)
                        Toast.makeText(context, if (success) "PDF exportado" else "Exportar PDF no implementado completamente", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulito),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = textoBlanco,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Exportar",
                            color = textoBlanco,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(scroll)
            .padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            if (previewBitmap != null) {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(6.dp)) {
                    Image(bitmap = previewBitmap.asImageBitmap(), contentDescription = "preview", modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp))
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Text("Vista previa no disponible", modifier = Modifier.padding(12.dp))
                }
            }

            for (i in 0 until fieldsArray.length()) {
                val f = fieldsArray.optJSONObject(i) ?: continue
                val pdfName = f.optString("pdfName")
                val label = f.optString("label", pdfName)
                OutlinedTextField(
                    value = values[pdfName] ?: "",
                    onValueChange = { values[pdfName] = it },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // provide space at bottom so last input isn't obscured by the fixed bottom bar
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

private fun loadFieldMap(context: Context): JSONObject {
    return try {
        val isr = context.assets.open("field_map.json").readBytes()
        JSONObject(String(isr))
    } catch (e: Exception) {
        JSONObject().put("fields", JSONArray())
    }
}

private fun loadPreviewBitmap(context: Context) = try {
    val name = "TOMA_DE_MATRICULAS_page_1.png"
    val `is` = context.assets.open(name)
    BitmapFactory.decodeStream(`is`)
} catch (e: Exception) { null }

private fun saveValuesJson(context: Context, values: Map<String, String>): Boolean {
    return try {
        val out = JSONObject()
        for ((k, v) in values) out.put(k, v)
        context.openFileOutput("filled_values.json", Context.MODE_PRIVATE).use { fos ->
            fos.write(out.toString(2).toByteArray())
        }
        true
    } catch (e: Exception) {
        e.printStackTrace(); false
    }
}
