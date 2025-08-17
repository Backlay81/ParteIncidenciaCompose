package com.example.parteincidenciacompose.ui

import com.example.parteincidenciacompose.viewmodel.ParteViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parteincidenciacompose.data.ParteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MobileOff


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartesAnterioresScreen(
    parteViewModel: ParteViewModel,
    onBack: () -> Unit = {},
    onVerDetalle: (Int) -> Unit = {}
) {
    // Obtener todos los partes y eliminar duplicados manteniendo solo uno por cada conjunto de datos principales
    val partesAll = parteViewModel.partes.collectAsState().value
    
    // Separar partes finalizados (con kmsFinales) y activos
    val partesFinalizados = partesAll.filter { it.kmsFinales.isNotBlank() }
    val partesActivos = partesAll.filter { it.kmsFinales.isBlank() }
    
    // Para los partes activos, agrupar por unidad+agente+vehiculo+fecha y quedarnos solo con uno por grupo
    val partesActivosUnicos = partesActivos
        .groupBy { it.unidad + it.agente + it.vehiculo + it.fechaHoraInicio }
        .map { (_, similares) -> similares.maxByOrNull { it.id } ?: similares.first() }
    
    // Combinar los finalizados con los activos únicos
    val partes = partesFinalizados + partesActivosUnicos

    // Agrupar partes por mes y año
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("es"))
    val partesOrdenados = partes.sortedByDescending { parte ->
        try { dateFormat.parse(parte.fechaHoraInicio) } catch (e: Exception) { Date(0) }
    }
    val ultimos3Partes = partesOrdenados.take(3)
    val idsUltimos3 = ultimos3Partes.map { it.id }.toSet()
    val partesRestantes = partesOrdenados.drop(3)
    val partesAgrupados = partesRestantes
        .groupBy { parte ->
            val date = try { dateFormat.parse(parte.fechaHoraInicio) } catch (e: Exception) { Date(0) }
            monthYearFormat.format(date ?: Date(0)).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

    // Estado para controlar qué grupos están expandidos
    val expandedGroups = remember { mutableStateMapOf<String, Boolean>() }
    var recientesExpanded by remember { mutableStateOf(true) }
    
    var parteAEliminar by remember { mutableStateOf<ParteEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Partes Anteriores",
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
                    containerColor = Color(0xFF1A73E8), // Azul Material You
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 0.dp, vertical = 8.dp)
        ) {
            if (partes.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MobileOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No hay partes anteriores",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mostrar los últimos 5 partes siempre expandidos
                    if (ultimos3Partes.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { recientesExpanded = !recientesExpanded }
                                    .padding(vertical = 8.dp)
                                    .animateContentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recientes",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (recientesExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = if (recientesExpanded) "Colapsar" else "Expandir",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (recientesExpanded) {
                            items(ultimos3Partes) { parte ->
                                ParteCardModern2(
                                    parte = parte,
                                    onEliminar = { parteAEliminar = parte },
                                    onDetalle = { onVerDetalle(parte.id) }
                                )
                            }
                        }
                    }
                    // Mostrar el resto agrupado y colapsable
                    partesAgrupados.forEach { (mesAnio, partesGrupo) ->
                        // Si algún parte de este grupo está en los últimos 3, mostrar expandido
                        val tieneRecientes = partesGrupo.any { idsUltimos3.contains(it.id) }
                        val expanded = expandedGroups.getOrPut(mesAnio) { tieneRecientes }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedGroups[mesAnio] = !(expandedGroups[mesAnio] ?: false)
                                    }
                                    .padding(vertical = 8.dp)
                                    .animateContentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mesAnio,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (expandedGroups[mesAnio] == true) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = if (expandedGroups[mesAnio] == true) "Colapsar" else "Expandir",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (expandedGroups[mesAnio] == true) {
                            items(partesGrupo) { parte ->
                                // Evitar mostrar duplicados si ya está en recientes
                                if (!idsUltimos3.contains(parte.id)) {
                                    ParteCardModern2(
                                        parte = parte,
                                        onEliminar = { parteAEliminar = parte },
                                        onDetalle = { onVerDetalle(parte.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Diálogo de confirmación para eliminar
            if (parteAEliminar != null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { parteAEliminar = null },
                    title = { Text("Eliminar parte") },
                    text = { Text("¿Seguro que quieres eliminar este parte?") },
                    confirmButton = {
                        TextButton(onClick = {
                            parteAEliminar?.let { parte -> parteViewModel.deleteParteById(parte.id) }
                            parteAEliminar = null
                        }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { parteAEliminar = null }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

// --- Composable para mostrar cada parte ---
@Composable
fun ParteCardModern2(
    parte: ParteEntity,
    onEliminar: (() -> Unit)? = null,
    onDetalle: (() -> Unit)? = null
) {
    val esFinalizado = parte.kmsFinales.trim().isNotBlank()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = parte.fechaHoraInicio,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (esFinalizado) "Finalizado" else "Activo",
                            color = Color.White
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (esFinalizado)
                            Color(0xFFFF9800) // Naranja para Finalizados
                        else
                            Color(0xFF388E3C) // Verde para Activos
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unidad: ${parte.unidad}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Agente: ${parte.agente}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Vehículo: ${parte.vehiculo}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Mostrar kilómetros solo para partes finalizados
            if (esFinalizado) {
                Text(
                    text = "Kms iniciales: ${parte.kmsIniciales} → Kms finales: ${parte.kmsFinales}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onEliminar?.invoke() },
                    enabled = onEliminar != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ELIMINAR", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { onDetalle?.invoke() },
                    enabled = onDetalle != null
                ) {
                    Text("VER DETALLE", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

