package com.example.parteincidenciacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import com.example.parteincidenciacompose.ui.MainScreen
import com.example.parteincidenciacompose.ui.theme.ParteIncidenciaComposeTheme
import com.example.parteincidenciacompose.ui.NuevoParteScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parteincidenciacompose.MainActivity.Companion.partesAnteriores

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
                onCapturaMatriculaClick = { navController.navigate("captura_matricula") }
            )
        }
        composable("captura_matricula") {
            com.example.parteincidenciacompose.ui.CapturaMatriculaScreen(
                onBack = { navController.popBackStack() },
                onMatriculaDetected = { matricula ->
                    // For now just navigate back; later we can pass data via Nav args or shared ViewModel
                    navController.popBackStack()
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

