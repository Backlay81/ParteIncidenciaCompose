package com.example.parteincidenciacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parteincidenciacompose.ui.MainScreen
import com.example.parteincidenciacompose.ui.theme.ParteIncidenciaComposeTheme

class MainActivity : ComponentActivity() {
    companion object {
        // Lista global de partes anteriores
        val partesAnteriores = mutableListOf<com.example.parteincidenciacompose.model.ParteAnterior>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParteIncidenciaComposeTheme {
                NavHostScreen()
            }
        }
    }
}


@Composable
fun NavHostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNuevoParteClick = { navController.navigate("nuevo_parte") },
                onVerPartesAnterioresClick = { navController.navigate("partes_anteriores") },
                onHorasClick = { navController.navigate("horas") },
                onTomaMatriculasClick = { navController.navigate("toma_matriculas") }
            )
        }

        composable("toma_matriculas") {
            androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    val azulito = androidx.compose.ui.graphics.Color(0xFF1976D2)

                    androidx.compose.material3.Card(
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp),
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium
                    ) {
                        androidx.compose.material3.Button(
                            onClick = { navController.navigate("captura_matricula") },
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = azulito)
                        ) {
                            androidx.compose.material3.Text(
                                text = "Usar cámara",
                                color = androidx.compose.ui.graphics.Color.White,
                                modifier = androidx.compose.ui.Modifier.padding(8.dp)
                            )
                        }
                    }

                    androidx.compose.material3.Card(
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth(),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp),
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium
                    ) {
                        androidx.compose.material3.Button(
                            onClick = { navController.navigate("rellenar_plantilla") },
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = azulito)
                        ) {
                            androidx.compose.material3.Text(
                                text = "Usar plantilla",
                                color = androidx.compose.ui.graphics.Color.White,
                                modifier = androidx.compose.ui.Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        composable("captura_matricula") {
            val ctx = androidx.compose.ui.platform.LocalContext.current
            com.example.parteincidenciacompose.ui.CapturaMatriculaScreen(
                onBack = { navController.popBackStack() },
                onMatriculaDetected = { matricula ->
                    // Save detected matrícula into filled_values.json under first field labelled 'Matrícula'
                    try {
                        val am = ctx.assets
                        val jsonStr = am.open("field_map.json").bufferedReader().use { it.readText() }
                        val fm = org.json.JSONObject(jsonStr)
                        val fields = fm.optJSONArray("fields") ?: org.json.JSONArray()
                        var targetPdfName: String? = null
                        for (i in 0 until fields.length()) {
                            val f = fields.optJSONObject(i) ?: continue
                            val label = f.optString("label", "")
                            if (label.contains("Matrícula", ignoreCase = true) && targetPdfName == null) {
                                targetPdfName = f.optString("pdfName")
                            }
                        }
                        if (targetPdfName != null) {
                            val outObj = org.json.JSONObject()
                            outObj.put(targetPdfName, matricula)
                            ctx.openFileOutput("filled_values.json", android.content.Context.MODE_PRIVATE).use { fos ->
                                fos.write(outObj.toString(2).toByteArray())
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    navController.navigate("rellenar_plantilla")
                }
            )
        }
        composable("horas") {
            com.example.parteincidenciacompose.ui.HorasScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("nuevo_parte") {
            com.example.parteincidenciacompose.ui.NuevoParteScreen(
                onParteCreado = { unidad, agente, vehiculo, kms ->
                    navController.navigate(
                        "parte_activo?unidad=${unidad}&agente=${agente}&vehiculo=${vehiculo}&kms=${kms}"
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("rellenar_plantilla") {
            com.example.parteincidenciacompose.ui.RellenarPlantillaScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "parte_activo?unidad={unidad}&agente={agente}&vehiculo={vehiculo}&kms={kms}",
            arguments = listOf(
                navArgument("unidad") { type = androidx.navigation.NavType.StringType },
                navArgument("agente") { type = androidx.navigation.NavType.StringType },
                navArgument("vehiculo") { type = androidx.navigation.NavType.StringType },
                navArgument("kms") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val unidad = backStackEntry.arguments?.getString("unidad") ?: ""
            val agente = backStackEntry.arguments?.getString("agente") ?: ""
            val vehiculo = backStackEntry.arguments?.getString("vehiculo") ?: ""
            val kms = backStackEntry.arguments?.getString("kms") ?: ""
            val fechaHora = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            com.example.parteincidenciacompose.ui.ParteActivoScreen(
                unidad = unidad,
                agente = agente,
                vehiculo = vehiculo,
                kms = kms,
                fechaHora = fechaHora,
                onNuevaTarea = {},
                onNuevaIncidencia = {},
                onFinalizar = { parteAnterior ->
                    navController.popBackStack("main", inclusive = false)
                },
                onBackToMain = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "parte_activo/{parteId}",
            arguments = listOf(
                navArgument("parteId") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val parteId = backStackEntry.arguments?.getInt("parteId") ?: 0
            com.example.parteincidenciacompose.ui.ParteActivoScreen(
                parteId = parteId,
                onNuevaTarea = {},
                onNuevaIncidencia = {},
                onFinalizar = { parteAnterior ->
                    navController.popBackStack("main", inclusive = false)
                },
                onBackToMain = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable("partes_anteriores") {
            val parteViewModel: com.example.parteincidenciacompose.viewmodel.ParteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            com.example.parteincidenciacompose.ui.PartesAnterioresScreen(
                parteViewModel = parteViewModel,
                onBack = { navController.popBackStack() },
                onVerDetalle = { id -> navController.navigate("parte_activo/$id") }
            )
        }

        composable(
            route = "parte_detalle/{id}",
            arguments = listOf(
                navArgument("id") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            navController.navigate("parte_activo/$id")
        }
    }
}

