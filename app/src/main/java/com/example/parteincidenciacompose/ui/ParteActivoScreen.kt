package com.example.parteincidenciacompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parteincidenciacompose.viewmodel.ParteViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.parteincidenciacompose.data.ParteEntity
import com.example.parteincidenciacompose.data.ParteConverters
import com.example.parteincidenciacompose.model.Tarea
import com.example.parteincidenciacompose.model.Persona
import com.example.parteincidenciacompose.model.Vehiculo
import com.example.parteincidenciacompose.model.Incidencia
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.ui.text.input.KeyboardType


// ...existing code...

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParteActivoScreen(
    unidad: String = "",
    agente: String = "",
    vehiculo: String = "",
    kms: String = "",
    fechaHora: String = "",
    parteId: Int? = null,
    onNuevaTarea: () -> Unit = {},
    onNuevaIncidencia: () -> Unit = {},
    onFinalizar: (com.example.parteincidenciacompose.model.ParteAnterior) -> Unit = {},
    onBackToMain: () -> Unit = {},
    parteViewModel: ParteViewModel = viewModel()
) {
    val azulito = Color(0xFF1976D2)
    val textoBlanco = Color(0xFFFFFFFF)
    val (isTareasExpanded, setTareasExpanded) = remember { mutableStateOf(true) }
    val (isIncidenciasExpanded, setIncidenciasExpanded) = remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var showIncidenciaDialog by remember { mutableStateOf(false) }
    var showFinalizarConfirm by remember { mutableStateOf(false) }
    var showFinalizarKm by remember { mutableStateOf(false) }
    var kmsFinales by remember { mutableStateOf("") }
    var kmsFinalesState by remember { mutableStateOf("") }

    // Cargar parte activo desde Room al iniciar
    LaunchedEffect(Unit) {
        parteViewModel.loadPartes()
    }
    val partes by parteViewModel.partes.collectAsState()
    // ...existing code...
    val parteSeleccionada = if (parteId != null) partes.firstOrNull { it.id == parteId } else null
    val esFinalizado =
        remember(parteSeleccionada) { parteSeleccionada?.kmsFinales?.isNotBlank() ?: false }
    var unidadState by remember { mutableStateOf(if (parteSeleccionada != null) parteSeleccionada.unidad else unidad) }
    var agenteState by remember { mutableStateOf(if (parteSeleccionada != null) parteSeleccionada.agente else agente) }
    var vehiculoState by remember { mutableStateOf(if (parteSeleccionada != null) parteSeleccionada.vehiculo else vehiculo) }
    var kmsState by remember { mutableStateOf(if (parteSeleccionada != null) parteSeleccionada.kmsIniciales else kms) }
    var fechaHoraState by remember { mutableStateOf(if (parteSeleccionada != null) parteSeleccionada.fechaHoraInicio else fechaHora) }
    var parteActivoId by remember { mutableStateOf(parteSeleccionada?.id) }
    var tareas by remember {
        mutableStateOf(
            if (parteSeleccionada != null) ParteConverters.tareasFromJson(
                parteSeleccionada.tareasJson
            ) else listOf()
        )
    }
    var incidencias by remember {
        mutableStateOf(
            if (parteSeleccionada != null) ParteConverters.incidenciasFromJson(
                parteSeleccionada.incidenciasJson
            ) else listOf()
        )
    }

    // Si cambia el parte seleccionado, actualizar los estados
    LaunchedEffect(parteSeleccionada) {
        if (parteSeleccionada != null) {
            unidadState = parteSeleccionada.unidad
            agenteState = parteSeleccionada.agente
            vehiculoState = parteSeleccionada.vehiculo
            kmsState = parteSeleccionada.kmsIniciales
            kmsFinalesState = parteSeleccionada.kmsFinales
            fechaHoraState = parteSeleccionada.fechaHoraInicio
            parteActivoId = parteSeleccionada.id
            tareas = ParteConverters.tareasFromJson(parteSeleccionada.tareasJson)
            incidencias = ParteConverters.incidenciasFromJson(parteSeleccionada.incidenciasJson)
        } else if (parteId == null) {
            unidadState = unidad
            agenteState = agente
            vehiculoState = vehiculo
            kmsState = kms
            fechaHoraState = fechaHora
            parteActivoId = null
            tareas = listOf()
            incidencias = listOf()
        }
    }

    // Guardar automáticamente el parte activo cada vez que cambian tareas o incidencias

    fun guardarParteActivo() {
        // Si ya hay un parte activo (id != null y != 0), actualiza ese parte
        // Si no, crea uno nuevo (id = 0 para que Room lo autogenere)
        val parteEntity = ParteEntity(
            id = parteActivoId ?: 0,
            unidad = unidadState,
            agente = agenteState,
            vehiculo = vehiculoState,
            kmsIniciales = kmsState,
            kmsFinales = "", // activo
            fechaHoraInicio = fechaHoraState,
            tareasJson = ParteConverters.tareasToJson(tareas),
            incidenciasJson = ParteConverters.incidenciasToJson(incidencias)
        )
        if (parteActivoId != null && parteActivoId != 0) {
            // Actualiza el parte existente (Room REPLACE por id)
            parteViewModel.insertParte(parteEntity)
        } else {
            // Inserta uno nuevo y actualiza el id local tras guardar
            parteViewModel.insertParte(parteEntity)
            // El id real se actualizará en el próximo loadPartes/LaunchedEffect
        }
    }

    LaunchedEffect(tareas, incidencias) {
        guardarParteActivo()
    }
    var tareaAEliminar by remember { mutableStateOf<Int?>(null) }
    var tareaAEditar by remember { mutableStateOf<Int?>(null) }
    var tareaACompletar by remember { mutableStateOf<Int?>(null) }
    var incidenciaSeleccionada by remember { mutableStateOf<Incidencia?>(null) }
    var incidenciaAEditar by remember { mutableStateOf<Incidencia?>(null) }
    var incidenciaAEliminar by remember { mutableStateOf<Incidencia?>(null) }
    var resolucionDescripcion by remember { mutableStateOf("") }
    var resolucionHora by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize()) {
        androidx.compose.material3.Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = {
                        Text(
                            text = if (esFinalizado) "Parte Finalizado" else "Parte Activo",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackToMain) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver a principal",
                                tint = Color.White
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A73E8), // Azul Material You
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(azulito)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .padding(WindowInsets.navigationBars.asPaddingValues()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        enabled = !esFinalizado
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = textoBlanco
                            )
                            Text(
                                "Nueva Tarea",
                                color = textoBlanco,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Button(
                        onClick = { showIncidenciaDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        enabled = !esFinalizado
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = textoBlanco
                            )
                            Text(
                                "Incidencia",
                                color = textoBlanco,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Button(
                        onClick = { showFinalizarConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        enabled = !esFinalizado
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = textoBlanco
                            )
                            Text(
                                "Finalizar",
                                color = textoBlanco,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Datos principales (ahora en Card visualmente atractiva)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = azulito),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Unidad: $unidadState", fontSize = 14.sp, color = textoBlanco)
                            Text("Agente: $agenteState", fontSize = 14.sp, color = textoBlanco)
                            Text("Vehículo: $vehiculoState", fontSize = 14.sp, color = textoBlanco)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Kms iniciales: $kmsState",
                                    fontSize = 14.sp,
                                    color = textoBlanco
                                )
                            }
                            if (esFinalizado) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Kms finales: $kmsFinalesState",
                                        fontSize = 14.sp,
                                        color = textoBlanco
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Inicio: $fechaHoraState",
                                    fontSize = 14.sp,
                                    color = textoBlanco
                                )
                            }
                        }
                    }
                    // Sección de Tareas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(0.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(azulito)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable { setTareasExpanded(!isTareasExpanded) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Tareas Pendientes",
                                    fontWeight = FontWeight.Bold,
                                    color = textoBlanco,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    tareas.size.toString(),
                                    color = textoBlanco,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Icon(
                                    if (isTareasExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = textoBlanco,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            if (isTareasExpanded) {
                                if (tareas.isEmpty()) {
                                    Text(
                                        "No hay tareas pendientes",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 0.dp),
                                        color = Color.Gray
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentPadding = PaddingValues(bottom = 96.dp)
                                    ) {
                                        val tareasOrdenadas = tareas.sortedBy { it.hora }
                                        items(tareasOrdenadas.size) { idx ->
                                            val tarea = tareasOrdenadas[idx]
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                elevation = CardDefaults.cardElevation(1.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            "Descripción: ${tarea.descripcion}",
                                                            fontSize = 14.sp,
                                                            color = Color.Black
                                                        )
                                                        if (tarea.observaciones.isNotBlank()) {
                                                            Text(
                                                                "Observaciones: ${tarea.observaciones}",
                                                                fontSize = 14.sp,
                                                                color = Color.Black
                                                            )
                                                        }
                                                        Text(
                                                            "Hora: ${tarea.hora}",
                                                            fontSize = 14.sp,
                                                            color = Color.Black
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .padding(4.dp),
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        IconButton(onClick = {
                                                            tareaACompletar = idx
                                                            val horaActual = SimpleDateFormat(
                                                                "HH:mm",
                                                                Locale.getDefault()
                                                            ).format(Date())
                                                            resolucionHora = horaActual
                                                            resolucionDescripcion = ""
                                                        }) {
                                                            Icon(
                                                                Icons.Default.CheckCircle,
                                                                contentDescription = "Completar",
                                                                tint = Color(0xFF4CAF50)
                                                            )
                                                        }
                                                        IconButton(onClick = {
                                                            tareaAEditar = idx
                                                        }) {
                                                            Icon(
                                                                Icons.Default.Edit,
                                                                contentDescription = "Editar",
                                                                tint = Color(0xFF1976D2)
                                                            )
                                                        }
                                                        IconButton(onClick = {
                                                            tareaAEliminar = idx
                                                        }) {
                                                            Icon(
                                                                Icons.Default.Delete,
                                                                contentDescription = "Eliminar",
                                                                tint = Color.Red
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            if (tareaAEditar != null) {
                                                val tarea = tareas[tareaAEditar!!]
                                                var descripcion by remember { mutableStateOf(tarea.descripcion) }
                                                var observaciones by remember { mutableStateOf(tarea.observaciones) }
                                                var hora by remember { mutableStateOf(tarea.hora) }
                                                Dialog(onDismissRequest = { tareaAEditar = null }) {
                                                    Surface(
                                                        shape = MaterialTheme.shapes.medium,
                                                        color = Color.White,
                                                        shadowElevation = 8.dp,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp)
                                                            .border(
                                                                2.dp,
                                                                Color.Black,
                                                                MaterialTheme.shapes.medium
                                                            )
                                                    ) {
                                                        Column(modifier = Modifier.padding(16.dp)) {
                                                            Text(
                                                                "Editar Tarea",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 18.sp,
                                                                modifier = Modifier.padding(bottom = 8.dp)
                                                            )
                                                            OutlinedTextField(
                                                                value = descripcion,
                                                                onValueChange = {
                                                                    descripcion = it
                                                                },
                                                                label = { Text("Descripción") },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                minLines = 2,
                                                                maxLines = 4
                                                            )
                                                            OutlinedTextField(
                                                                value = observaciones,
                                                                onValueChange = {
                                                                    observaciones = it
                                                                },
                                                                label = { Text("Observaciones (opcional)") },
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(top = 12.dp),
                                                                minLines = 2,
                                                                maxLines = 4
                                                            )
                                                            OutlinedTextField(
                                                                value = hora,
                                                                onValueChange = { hora = it },
                                                                label = { Text("Hora") },
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(top = 8.dp),
                                                                singleLine = true
                                                            )
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(top = 24.dp),
                                                                horizontalArrangement = Arrangement.End
                                                            ) {
                                                                TextButton(onClick = {
                                                                    tareaAEditar = null
                                                                }) {
                                                                    Text("Cancelar")
                                                                }
                                                                Button(
                                                                    onClick = {
                                                                        if (descripcion.isNotBlank() && hora.isNotBlank()) {
                                                                            tareas =
                                                                                tareas.mapIndexed { i, t ->
                                                                                    if (i == tareaAEditar) Tarea(
                                                                                        descripcion,
                                                                                        observaciones,
                                                                                        hora
                                                                                    ) else t
                                                                                }
                                                                            tareaAEditar = null
                                                                        }
                                                                    },
                                                                    enabled = descripcion.isNotBlank() && hora.isNotBlank(),
                                                                    modifier = Modifier.padding(
                                                                        start = 8.dp
                                                                    )
                                                                ) {
                                                                    Text("Guardar")
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (tareaAEliminar != null) {
                                                Dialog(onDismissRequest = {
                                                    tareaAEliminar = null
                                                }) {
                                                    Surface(
                                                        shape = MaterialTheme.shapes.medium,
                                                        color = Color.White,
                                                        shadowElevation = 8.dp,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp)
                                                            .border(
                                                                2.dp,
                                                                Color.Black,
                                                                MaterialTheme.shapes.medium
                                                            )
                                                    ) {
                                                        Column(modifier = Modifier.padding(16.dp)) {
                                                            Text(
                                                                "¿Seguro que quieres eliminar esta tarea?",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 16.sp,
                                                                modifier = Modifier.padding(bottom = 16.dp)
                                                            )
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.End
                                                            ) {
                                                                TextButton(onClick = {
                                                                    tareaAEliminar = null
                                                                }) {
                                                                    Text("Cancelar")
                                                                }
                                                                Button(
                                                                    onClick = {
                                                                        tareaAEliminar?.let { idx ->
                                                                            tareas =
                                                                                tareas.filterIndexed { i, _ -> i != idx }
                                                                        }
                                                                        tareaAEliminar = null
                                                                    },
                                                                    modifier = Modifier.padding(
                                                                        start = 8.dp
                                                                    )
                                                                ) {
                                                                    Text("Eliminar")
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (tareaACompletar != null) {
                                                Dialog(onDismissRequest = {
                                                    tareaACompletar = null
                                                }) {
                                                    Surface(
                                                        shape = MaterialTheme.shapes.medium,
                                                        color = Color.White,
                                                        shadowElevation = 8.dp,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp)
                                                            .border(
                                                                2.dp,
                                                                Color.Black,
                                                                MaterialTheme.shapes.medium
                                                            )
                                                    ) {
                                                        Column(modifier = Modifier.padding(16.dp)) {
                                                            Text(
                                                                "Completar Tarea",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 18.sp,
                                                                modifier = Modifier.padding(bottom = 8.dp)
                                                            )

                                                            OutlinedTextField(
                                                                value = resolucionDescripcion,
                                                                onValueChange = {
                                                                    resolucionDescripcion = it
                                                                },
                                                                label = { Text("Resolución") },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                minLines = 2,
                                                                maxLines = 4
                                                            )

                                                            OutlinedTextField(
                                                                value = resolucionHora,
                                                                onValueChange = {
                                                                    resolucionHora = it
                                                                },
                                                                label = { Text("Hora de Resolución") },
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(top = 8.dp),
                                                                singleLine = true
                                                            )

                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(top = 24.dp),
                                                                horizontalArrangement = Arrangement.End
                                                            ) {
                                                                TextButton(onClick = {
                                                                    tareaACompletar = null
                                                                }) {
                                                                    Text("Cancelar")
                                                                }
                                                                Button(
                                                                    onClick = {
                                                                        if (resolucionDescripcion.isNotBlank() && resolucionHora.isNotBlank()) {
                                                                            tareaACompletar?.let { idx ->
                                                                                val tarea =
                                                                                    tareas[idx]
                                                                                // Crear una incidencia a partir de la tarea completada
                                                                                val nuevaIncidencia =
                                                                                    Incidencia(
                                                                                        descripcion = tarea.descripcion,
                                                                                        observaciones = tarea.observaciones,
                                                                                        hora = tarea.hora,
                                                                                        horaFinalizacion = resolucionHora,
                                                                                        resolucion = resolucionDescripcion,
                                                                                        personasImplicadas = emptyList(),
                                                                                        vehiculosImplicados = emptyList()
                                                                                    )
                                                                                // Añadir a la lista de incidencias
                                                                                incidencias =
                                                                                    incidencias + nuevaIncidencia
                                                                                // Eliminar de la lista de tareas
                                                                                tareas =
                                                                                    tareas.filterIndexed { i, _ -> i != idx }
                                                                            }
                                                                            tareaACompletar = null
                                                                        }
                                                                    },
                                                                    enabled = resolucionDescripcion.isNotBlank() && resolucionHora.isNotBlank(),
                                                                    modifier = Modifier.padding(
                                                                        start = 8.dp
                                                                    )
                                                                ) {
                                                                    Text("Completar")
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Sección de Incidencias
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(0.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(azulito)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable { setIncidenciasExpanded(!isIncidenciasExpanded) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Incidencias",
                                    fontWeight = FontWeight.Bold,
                                    color = textoBlanco,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    incidencias.size.toString(),
                                    color = textoBlanco,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Icon(
                                    if (isIncidenciasExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = textoBlanco,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            if (isIncidenciasExpanded) {
                                if (incidencias.isEmpty()) {
                                    Text(
                                        "No hay incidencias registradas",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 0.dp),
                                        color = Color.Gray
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentPadding = PaddingValues(bottom = 96.dp)
                                    ) {
                                        val incidenciasOrdenadas =
                                            incidencias.sortedBy { it.horaFinalizacion.ifBlank { it.hora } }
                                        items(incidenciasOrdenadas.size) { idx ->
                                            val incidencia = incidenciasOrdenadas[idx]
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable {
                                                        incidenciaSeleccionada = incidencia
                                                    },
                                                elevation = CardDefaults.cardElevation(1.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            "Descripción: ${incidencia.descripcion}",
                                                            fontSize = 14.sp,
                                                            color = Color.Black
                                                        )
                                                        if (incidencia.observaciones.isNotBlank()) {
                                                            Text(
                                                                "Observaciones: ${incidencia.observaciones}",
                                                                fontSize = 14.sp,
                                                                color = Color.Black
                                                            )
                                                        }
                                                        Text(
                                                            "Hora creación: ${incidencia.hora}",
                                                            fontSize = 14.sp,
                                                            color = Color.Black
                                                        )
                                                        if (incidencia.horaFinalizacion.isNotBlank()) {
                                                            Text(
                                                                "Hora finalización: ${incidencia.horaFinalizacion}",
                                                                fontSize = 14.sp,
                                                                color = Color.Black
                                                            )
                                                        }
                                                        if (incidencia.resolucion.isNotBlank()) {
                                                            Text(
                                                                "Resolución: ${incidencia.resolucion}",
                                                                fontSize = 14.sp,
                                                                color = Color.Black,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }

                                                        // Mostrar solo el número de personas y vehículos implicados
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            "Personas implicadas: ${incidencia.personasImplicadas.size}",
                                                            fontSize = 14.sp,
                                                            color = Color.Black,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            "Vehículos implicados: ${incidencia.vehiculosImplicados.size}",
                                                            fontSize = 14.sp,
                                                            color = Color.Black,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .padding(4.dp),
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        IconButton(onClick = {
                                                            incidenciaAEditar = incidencia
                                                        }) {
                                                            Icon(
                                                                Icons.Default.Edit,
                                                                contentDescription = "Editar",
                                                                tint = Color(0xFF1976D2)
                                                            )
                                                        }
                                                        IconButton(onClick = {
                                                            incidenciaAEliminar = incidencia
                                                        }) {
                                                            Icon(
                                                                Icons.Default.Delete,
                                                                contentDescription = "Eliminar",
                                                                tint = Color.Red
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Bottom fijo
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(azulito)
                        .height(110.dp)
                        .padding(horizontal = 4.dp)
                        .padding(WindowInsets.navigationBars.asPaddingValues()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        enabled = !esFinalizado
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = textoBlanco
                            )
                            Text(
                                "Nueva Tarea",
                                color = textoBlanco,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Button(
                        onClick = { showIncidenciaDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        enabled = !esFinalizado
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = textoBlanco
                            )
                            Text(
                                "Incidencia",
                                color = textoBlanco,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Button(
                        onClick = { showFinalizarConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = azulito),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        enabled = !esFinalizado
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = textoBlanco
                            )
                            Text(
                                "Finalizar",
                                color = textoBlanco,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    // Diálogo de confirmación para finalizar
                    if (showFinalizarConfirm) {
                        Dialog(onDismissRequest = { showFinalizarConfirm = false }) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = Color.White,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "¿Seguro que quieres finalizar el parte activo?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { showFinalizarConfirm = false }) {
                                            Text("Cancelar")
                                        }
                                        Button(
                                            onClick = {
                                                showFinalizarConfirm = false
                                                showFinalizarKm = true
                                            },
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("Aceptar")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Diálogo para pedir los kilómetros finales
                    if (showFinalizarKm) {
                        Dialog(onDismissRequest = { showFinalizarKm = false }) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = Color.White,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Introduce los kilómetros finales:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    OutlinedTextField(
                                        value = kmsFinales,
                                        onValueChange = {
                                            kmsFinales = it.filter { c -> c.isDigit() }
                                        },
                                        label = { Text("Kms finales") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 24.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { showFinalizarKm = false }) {
                                            Text("Cancelar")
                                        }
                                        Button(
                                            onClick = {
                                                android.util.Log.d(
                                                    "ParteActivoScreen",
                                                    "[DIALOGO] Se abre el diálogo de finalización. Estado actual: id=$parteActivoId, kmsFinales=$kmsFinales"
                                                )
                                                showFinalizarKm = false
                                                val idFinal =
                                                    parteActivoId ?: System.currentTimeMillis()
                                                        .toInt()
                                                android.util.Log.d(
                                                    "ParteActivoScreen",
                                                    "Finalizando parte con id=$idFinal, kmsFinales=$kmsFinales"
                                                )
                                                val parteFinal = ParteEntity(
                                                    id = idFinal,
                                                    unidad = unidadState,
                                                    agente = agenteState,
                                                    vehiculo = vehiculoState,
                                                    kmsIniciales = kmsState,
                                                    kmsFinales = kmsFinales,
                                                    fechaHoraInicio = fechaHoraState,
                                                    tareasJson = ParteConverters.tareasToJson(tareas),
                                                    incidenciasJson = ParteConverters.incidenciasToJson(
                                                        incidencias
                                                    )
                                                )
                                                android.util.Log.d(
                                                    "ParteActivoScreen",
                                                    "ParteEntity finalizado: $parteFinal"
                                                )
                                                parteViewModel.finalizarParte(
                                                    parteFinal,
                                                    kmsFinales
                                                )
                                                onFinalizar(
                                                    com.example.parteincidenciacompose.model.ParteAnterior(
                                                        unidad = unidadState,
                                                        agente = agenteState,
                                                        vehiculo = vehiculoState,
                                                        kmsIniciales = kmsState,
                                                        kmsFinales = kmsFinales,
                                                        fechaHoraInicio = fechaHoraState,
                                                        tareas = tareas,
                                                        incidencias = incidencias
                                                    )
                                                )
                                            },
                                            enabled = kmsFinales.isNotBlank(),
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("Finalizar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                    ) {
                        var descripcion by remember { mutableStateOf("") }
                        var observaciones by remember { mutableStateOf("") }
                        // Hora por defecto: actual, editable
                        val horaActual = remember {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        }
                        var hora by remember { mutableStateOf(horaActual) }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Nueva Tarea",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4
                            )
                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = { observaciones = it },
                                label = { Text("Observaciones (opcional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                minLines = 2,
                                maxLines = 4
                            )
                            OutlinedTextField(
                                value = hora,
                                onValueChange = { hora = it },
                                label = { Text("Hora") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                singleLine = true
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        if (descripcion.isNotBlank() && hora.isNotBlank()) {
                                            tareas =
                                                tareas + Tarea(descripcion, observaciones, hora)
                                            showDialog = false
                                        }
                                    },
                                    enabled = descripcion.isNotBlank() && hora.isNotBlank(),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }
            }

            if (showIncidenciaDialog) {
                Dialog(onDismissRequest = { showIncidenciaDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                    ) {
                        var descripcion by remember { mutableStateOf("") }
                        var observaciones by remember { mutableStateOf("") }
                        // Hora por defecto: actual, editable
                        val horaActual = remember {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        }
                        var hora by remember { mutableStateOf(horaActual) }

                        // Estado para personas y vehículos
                        var personasImplicadas by remember { mutableStateOf(listOf<Persona>()) }
                        var vehiculosImplicados by remember { mutableStateOf(listOf<Vehiculo>()) }

                        // Estados para mostrar/ocultar secciones de detalles
                        var mostrarPersonaSeleccionada by remember {
                            mutableStateOf<Persona?>(
                                null
                            )
                        }
                        var mostrarVehiculoSeleccionado by remember {
                            mutableStateOf<Vehiculo?>(
                                null
                            )
                        }

                        // Estados para diálogos de añadir persona/vehículo
                        var mostrarDialogoPersona by remember { mutableStateOf(false) }
                        var mostrarDialogoVehiculo by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                "Nueva Incidencia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Campos básicos
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4
                            )

                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = { observaciones = it },
                                label = { Text("Observaciones (opcional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                minLines = 2,
                                maxLines = 4
                            )

                            OutlinedTextField(
                                value = hora,
                                onValueChange = { hora = it },
                                label = { Text("Hora") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                singleLine = true
                            )

                            // Personas y vehículos implicados (solo número)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Personas implicadas: ${personasImplicadas.size}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { mostrarDialogoPersona = true },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                ) {
                                    Text("Añadir persona")
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Vehículos implicados: ${vehiculosImplicados.size}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { mostrarDialogoVehiculo = true },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                ) {
                                    Text("Añadir vehículo")
                                }
                            }

                            // Botones de acción
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = { showIncidenciaDialog = false },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        if (descripcion.isNotBlank() && hora.isNotBlank()) {
                                            incidencias = incidencias + Incidencia(
                                                descripcion = descripcion,
                                                observaciones = observaciones,
                                                hora = hora,
                                                personasImplicadas = personasImplicadas,
                                                vehiculosImplicados = vehiculosImplicados
                                            )
                                            showIncidenciaDialog = false
                                        }
                                    },
                                    enabled = descripcion.isNotBlank() && hora.isNotBlank(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }

                        // Diálogo para añadir persona
                        if (mostrarDialogoPersona) {
                            Dialog(onDismissRequest = { mostrarDialogoPersona = false }) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.White,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                                ) {
                                    var nombre by remember { mutableStateOf("") }
                                    var dni by remember { mutableStateOf("") }
                                    var fechaNacimiento by remember { mutableStateOf("") }
                                    var hijoDe by remember { mutableStateOf("") }
                                    var lugarNacimiento by remember { mutableStateOf("") }
                                    var direccion by remember { mutableStateOf("") }
                                    var telefono by remember { mutableStateOf("") }
                                    var email by remember { mutableStateOf("") }
                                    var otros by remember { mutableStateOf("") }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "Añadir Persona",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        OutlinedTextField(
                                            value = nombre,
                                            onValueChange = { nombre = it },
                                            label = { Text("Nombre completo") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = dni,
                                            onValueChange = { dni = it },
                                            label = { Text("DNI/NIE") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = fechaNacimiento,
                                            onValueChange = { fechaNacimiento = it },
                                            label = { Text("Fecha de nacimiento") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = hijoDe,
                                            onValueChange = { hijoDe = it },
                                            label = { Text("Hijo de") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = lugarNacimiento,
                                            onValueChange = { lugarNacimiento = it },
                                            label = { Text("Lugar de nacimiento") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = direccion,
                                            onValueChange = { direccion = it },
                                            label = { Text("Dirección") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = telefono,
                                            onValueChange = { telefono = it },
                                            label = { Text("Teléfono") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = email,
                                            onValueChange = { email = it },
                                            label = { Text("Email") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = otros,
                                            onValueChange = { otros = it },
                                            label = { Text("Otros datos") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            minLines = 2,
                                            maxLines = 4
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 24.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = {
                                                mostrarDialogoPersona = false
                                            }) {
                                                Text("Cancelar")
                                            }
                                            Button(
                                                onClick = {
                                                    if (nombre.isNotBlank() && dni.isNotBlank()) {
                                                        val nuevaPersona = Persona(
                                                            nombre = nombre,
                                                            dni = dni,
                                                            fechaNacimiento = fechaNacimiento,
                                                            hijoDe = hijoDe,
                                                            lugarNacimiento = lugarNacimiento,
                                                            direccion = direccion,
                                                            telefono = telefono,
                                                            email = email,
                                                            otros = otros
                                                        )
                                                        personasImplicadas =
                                                            personasImplicadas + nuevaPersona
                                                        mostrarDialogoPersona = false
                                                    }
                                                },
                                                enabled = nombre.isNotBlank() && dni.isNotBlank(),
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text("Añadir")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Diálogo para añadir vehículo
                        if (mostrarDialogoVehiculo) {
                            Dialog(onDismissRequest = { mostrarDialogoVehiculo = false }) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.White,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                                ) {
                                    var marcaModelo by remember { mutableStateOf("") }
                                    var matricula by remember { mutableStateOf("") }
                                    var tipo by remember { mutableStateOf("") }
                                    var color by remember { mutableStateOf("") }
                                    var itv by remember { mutableStateOf("") }
                                    var seguro by remember { mutableStateOf("") }
                                    var otros by remember { mutableStateOf("") }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "Añadir Vehículo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        OutlinedTextField(
                                            value = marcaModelo,
                                            onValueChange = { marcaModelo = it },
                                            label = { Text("Marca y modelo") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = matricula,
                                            onValueChange = { matricula = it },
                                            label = { Text("Matrícula") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = tipo,
                                            onValueChange = { tipo = it },
                                            label = { Text("Tipo de vehículo") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = color,
                                            onValueChange = { color = it },
                                            label = { Text("Color") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = itv,
                                            onValueChange = { itv = it },
                                            label = { Text("ITV") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = seguro,
                                            onValueChange = { seguro = it },
                                            label = { Text("Seguro") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = otros,
                                            onValueChange = { otros = it },
                                            label = { Text("Otros datos") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            minLines = 2,
                                            maxLines = 4
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 24.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = {
                                                mostrarDialogoVehiculo = false
                                            }) {
                                                Text("Cancelar")
                                            }
                                            Button(
                                                onClick = {
                                                    if (marcaModelo.isNotBlank() && matricula.isNotBlank()) {
                                                        val nuevoVehiculo = Vehiculo(
                                                            marcaModelo = marcaModelo,
                                                            matricula = matricula,
                                                            tipo = tipo,
                                                            color = color,
                                                            itv = itv,
                                                            seguro = seguro,
                                                            otros = otros
                                                        )
                                                        vehiculosImplicados =
                                                            vehiculosImplicados + nuevoVehiculo
                                                        mostrarDialogoVehiculo = false
                                                    }
                                                },
                                                enabled = marcaModelo.isNotBlank() && matricula.isNotBlank(),
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text("Añadir")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Diálogo de visualización de detalles de incidencia
            if (incidenciaSeleccionada != null) {
                val incidencia = incidenciaSeleccionada!!
                Dialog(onDismissRequest = { incidenciaSeleccionada = null }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                            .padding(16.dp)
                            .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // Sección Descripción
                            Text(
                                text = "Descripción",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = incidencia.descripcion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )

                            // Sección Observaciones
                            Text(
                                text = "Observaciones",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = if (incidencia.observaciones.isNotBlank()) incidencia.observaciones else "Sin observaciones",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )

                            // Sección Hora
                            Text(
                                text = "Hora",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = incidencia.hora,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )

                            // Sección Resolución
                            if (incidencia.resolucion.isNotBlank()) {
                                Text(
                                    text = "Resolución",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                Text(
                                    text = incidencia.resolucion,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                )
                            }

                            // Sección Personas Involucradas
                            if (incidencia.personasImplicadas.isNotEmpty()) {
                                Text(
                                    text = "Personas Involucradas",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    incidencia.personasImplicadas.forEach { persona ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(1.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    persona.nombre,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text("DNI: ${persona.dni}")
                                                if (persona.fechaNacimiento.isNotBlank()) Text("Fecha Nacimiento: ${persona.fechaNacimiento}")
                                                if (persona.hijoDe.isNotBlank()) Text("Hijo de: ${persona.hijoDe}")
                                                if (persona.lugarNacimiento.isNotBlank()) Text("Lugar Nacimiento: ${persona.lugarNacimiento}")
                                                if (persona.direccion.isNotBlank()) Text("Dirección: ${persona.direccion}")
                                                if (persona.telefono.isNotBlank()) Text("Teléfono: ${persona.telefono}")
                                                if (persona.email.isNotBlank()) Text("Email: ${persona.email}")
                                                if (persona.otros.isNotBlank()) Text(
                                                    "Otros: ${persona.otros}",
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Sección Vehículos Involucrados
                            if (incidencia.vehiculosImplicados.isNotEmpty()) {
                                Text(
                                    text = "Vehículos Involucrados",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    incidencia.vehiculosImplicados.forEach { vehiculo ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(1.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    vehiculo.marcaModelo,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text("Matrícula: ${vehiculo.matricula}")
                                                if (vehiculo.tipo.isNotBlank()) Text("Tipo: ${vehiculo.tipo}")
                                                if (vehiculo.color.isNotBlank()) Text("Color: ${vehiculo.color}")
                                                if (vehiculo.itv.isNotBlank()) Text("ITV: ${vehiculo.itv}")
                                                if (vehiculo.seguro.isNotBlank()) Text("Seguro: ${vehiculo.seguro}")
                                                if (vehiculo.otros.isNotBlank()) Text(
                                                    "Otros: ${vehiculo.otros}",
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Botones de acción
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = { incidenciaSeleccionada = null },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    Text("Cerrar")
                                }

                                Button(
                                    onClick = {
                                        // Abrir el diálogo de edición con la incidencia actual
                                        incidenciaAEditar = incidenciaSeleccionada
                                        incidenciaSeleccionada = null
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Editar")
                                }
                            }
                        }
                    }
                }
            }

            // Diálogo para eliminar incidencia
            if (incidenciaAEliminar != null) {
                Dialog(onDismissRequest = { incidenciaAEliminar = null }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "¿Seguro que quieres eliminar esta incidencia?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { incidenciaAEliminar = null }) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        incidenciaAEliminar?.let { incidencia ->
                                            incidencias =
                                                incidencias.filter { it != incidencia }
                                        }
                                        incidenciaAEliminar = null
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }

            // Diálogo para editar incidencia
            if (incidenciaAEditar != null) {
                val incidencia = incidenciaAEditar!!
                Dialog(onDismissRequest = { incidenciaAEditar = null }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                    ) {
                        var descripcion by remember { mutableStateOf(incidencia.descripcion) }
                        var observaciones by remember { mutableStateOf(incidencia.observaciones) }
                        var hora by remember { mutableStateOf(incidencia.hora) }
                        var resolucion by remember { mutableStateOf(incidencia.resolucion) }

                        // Estado para personas y vehículos - mantenemos los existentes
                        var personasImplicadas by remember { mutableStateOf(incidencia.personasImplicadas) }
                        var vehiculosImplicados by remember { mutableStateOf(incidencia.vehiculosImplicados) }

                        // Estados para mostrar/ocultar secciones de detalles
                        var mostrarPersonaSeleccionada by remember {
                            mutableStateOf<Persona?>(
                                null
                            )
                        }
                        var mostrarVehiculoSeleccionado by remember {
                            mutableStateOf<Vehiculo?>(
                                null
                            )
                        }

                        // Estados para diálogos de añadir persona/vehículo
                        var mostrarDialogoPersona by remember { mutableStateOf(false) }
                        var mostrarDialogoVehiculo by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                "Editar Incidencia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Campos básicos
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4
                            )

                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = { observaciones = it },
                                label = { Text("Observaciones (opcional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                minLines = 2,
                                maxLines = 4
                            )

                            OutlinedTextField(
                                value = hora,
                                onValueChange = { hora = it },
                                label = { Text("Hora") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                singleLine = true
                            )

                            if (resolucion.isNotBlank()) {
                                OutlinedTextField(
                                    value = resolucion,
                                    onValueChange = { resolucion = it },
                                    label = { Text("Resolución") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    minLines = 2,
                                    maxLines = 4
                                )
                            }

                            // Personas y vehículos implicados (solo número)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Personas implicadas: ${personasImplicadas.size}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { mostrarDialogoPersona = true },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                ) {
                                    Text("Añadir persona")
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Vehículos implicados: ${vehiculosImplicados.size}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { mostrarDialogoVehiculo = true },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                ) {
                                    Text("Añadir vehículo")
                                }
                            }

                            // Botones de acción
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = { incidenciaAEditar = null },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        if (descripcion.isNotBlank() && hora.isNotBlank()) {
                                            incidenciaAEditar?.let { incidenciaOriginal ->
                                                val incidenciaActualizada = Incidencia(
                                                    descripcion = descripcion,
                                                    observaciones = observaciones,
                                                    hora = hora,
                                                    horaFinalizacion = incidenciaOriginal.horaFinalizacion,
                                                    resolucion = resolucion,
                                                    personasImplicadas = personasImplicadas,
                                                    vehiculosImplicados = vehiculosImplicados
                                                )
                                                incidencias = incidencias.map {
                                                    if (it == incidenciaOriginal) incidenciaActualizada else it
                                                }
                                            }
                                            incidenciaAEditar = null
                                        }
                                    },
                                    enabled = descripcion.isNotBlank() && hora.isNotBlank(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }

                        // Diálogo para añadir persona
                        if (mostrarDialogoPersona) {
                            Dialog(onDismissRequest = { mostrarDialogoPersona = false }) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.White,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                                ) {
                                    var nombre by remember { mutableStateOf("") }
                                    var dni by remember { mutableStateOf("") }
                                    var fechaNacimiento by remember { mutableStateOf("") }
                                    var hijoDe by remember { mutableStateOf("") }
                                    var lugarNacimiento by remember { mutableStateOf("") }
                                    var direccion by remember { mutableStateOf("") }
                                    var telefono by remember { mutableStateOf("") }
                                    var email by remember { mutableStateOf("") }
                                    var otros by remember { mutableStateOf("") }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "Añadir Persona",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        OutlinedTextField(
                                            value = nombre,
                                            onValueChange = { nombre = it },
                                            label = { Text("Nombre completo") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = dni,
                                            onValueChange = { dni = it },
                                            label = { Text("DNI/NIE") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = fechaNacimiento,
                                            onValueChange = { fechaNacimiento = it },
                                            label = { Text("Fecha de nacimiento") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = hijoDe,
                                            onValueChange = { hijoDe = it },
                                            label = { Text("Hijo de") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = lugarNacimiento,
                                            onValueChange = { lugarNacimiento = it },
                                            label = { Text("Lugar de nacimiento") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = direccion,
                                            onValueChange = { direccion = it },
                                            label = { Text("Dirección") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = telefono,
                                            onValueChange = { telefono = it },
                                            label = { Text("Teléfono") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = email,
                                            onValueChange = { email = it },
                                            label = { Text("Email") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = otros,
                                            onValueChange = { otros = it },
                                            label = { Text("Otros datos") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            minLines = 2,
                                            maxLines = 4
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 24.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = {
                                                mostrarDialogoPersona = false
                                            }) {
                                                Text("Cancelar")
                                            }
                                            Button(
                                                onClick = {
                                                    if (nombre.isNotBlank() && dni.isNotBlank()) {
                                                        val nuevaPersona = Persona(
                                                            nombre = nombre,
                                                            dni = dni,
                                                            fechaNacimiento = fechaNacimiento,
                                                            hijoDe = hijoDe,
                                                            lugarNacimiento = lugarNacimiento,
                                                            direccion = direccion,
                                                            telefono = telefono,
                                                            email = email,
                                                            otros = otros
                                                        )
                                                        personasImplicadas =
                                                            personasImplicadas + nuevaPersona
                                                        mostrarDialogoPersona = false
                                                    }
                                                },
                                                enabled = nombre.isNotBlank() && dni.isNotBlank(),
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text("Añadir")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Diálogo para añadir vehículo
                        if (mostrarDialogoVehiculo) {
                            Dialog(onDismissRequest = { mostrarDialogoVehiculo = false }) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.White,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
                                ) {
                                    var marcaModelo by remember { mutableStateOf("") }
                                    var matricula by remember { mutableStateOf("") }
                                    var tipo by remember { mutableStateOf("") }
                                    var color by remember { mutableStateOf("") }
                                    var itv by remember { mutableStateOf("") }
                                    var seguro by remember { mutableStateOf("") }
                                    var otros by remember { mutableStateOf("") }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "Añadir Vehículo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        OutlinedTextField(
                                            value = marcaModelo,
                                            onValueChange = { marcaModelo = it },
                                            label = { Text("Marca y modelo") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = matricula,
                                            onValueChange = { matricula = it },
                                            label = { Text("Matrícula") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = tipo,
                                            onValueChange = { tipo = it },
                                            label = { Text("Tipo de vehículo") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = color,
                                            onValueChange = { color = it },
                                            label = { Text("Color") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = itv,
                                            onValueChange = { itv = it },
                                            label = { Text("ITV") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = seguro,
                                            onValueChange = { seguro = it },
                                            label = { Text("Seguro") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = otros,
                                            onValueChange = { otros = it },
                                            label = { Text("Otros datos") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            minLines = 2,
                                            maxLines = 4
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 24.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = {
                                                mostrarDialogoVehiculo = false
                                            }) {
                                                Text("Cancelar")
                                            }
                                            Button(
                                                onClick = {
                                                    if (marcaModelo.isNotBlank() && matricula.isNotBlank()) {
                                                        val nuevoVehiculo = Vehiculo(
                                                            marcaModelo = marcaModelo,
                                                            matricula = matricula,
                                                            tipo = tipo,
                                                            color = color,
                                                            itv = itv,
                                                            seguro = seguro,
                                                            otros = otros
                                                        )
                                                        vehiculosImplicados =
                                                            vehiculosImplicados + nuevoVehiculo
                                                        mostrarDialogoVehiculo = false
                                                    }
                                                },
                                                enabled = marcaModelo.isNotBlank() && matricula.isNotBlank(),
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text("Añadir")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        @Composable
        fun PersonaCard(
            persona: Persona,
            onDelete: ((Persona) -> Unit)? = null,
            readOnly: Boolean = false,
            modifier: Modifier = Modifier
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Nombre: ${persona.nombre}", fontWeight = FontWeight.Bold)
                    Text("DNI: ${persona.dni}")
                    if (persona.fechaNacimiento.isNotBlank()) Text("Fecha Nacimiento: ${persona.fechaNacimiento}")
                    if (persona.hijoDe.isNotBlank()) Text("Hijo de: ${persona.hijoDe}")
                    if (persona.lugarNacimiento.isNotBlank()) Text("Lugar Nacimiento: ${persona.lugarNacimiento}")
                    if (persona.direccion.isNotBlank()) Text("Dirección: ${persona.direccion}")
                    if (persona.telefono.isNotBlank()) Text("Teléfono: ${persona.telefono}")
                    if (persona.email.isNotBlank()) Text("Email: ${persona.email}")
                    if (persona.otros.isNotBlank()) Text(
                        "Otros: ${persona.otros}",
                        fontSize = 14.sp
                    )

                    // Solo mostrar botón eliminar si no es de solo lectura y se proporciona callback
                    if (!readOnly && onDelete != null) {
                        TextButton(
                            onClick = { onDelete(persona) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Eliminar", color = Color.Red)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun VehiculoCard(
        vehiculo: Vehiculo,
        onDelete: ((Vehiculo) -> Unit)? = null,
        readOnly: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Marca/Modelo: ${vehiculo.marcaModelo}", fontWeight = FontWeight.Bold)
                Text("Matrícula: ${vehiculo.matricula}")
                if (vehiculo.tipo.isNotBlank()) Text("Tipo: ${vehiculo.tipo}")
                if (vehiculo.color.isNotBlank()) Text("Color: ${vehiculo.color}")
                if (vehiculo.itv.isNotBlank()) Text("ITV: ${vehiculo.itv}")
                if (vehiculo.seguro.isNotBlank()) Text("Seguro: ${vehiculo.seguro}")
                if (vehiculo.otros.isNotBlank()) Text("Otros: ${vehiculo.otros}", fontSize = 14.sp)

                // Solo mostrar botón eliminar si no es de solo lectura y se proporciona callback
                if (!readOnly && onDelete != null) {
                    TextButton(
                        onClick = { onDelete(vehiculo) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                }
            }
        }
    }

    @Composable
    fun PersonaChip(
        persona: Persona,
        onClick: (Persona) -> Unit,
        modifier: Modifier = Modifier
    ) {
        SuggestionChip(
            onClick = { onClick(persona) },
            label = { Text(persona.nombre) },
            modifier = modifier.padding(end = 4.dp, bottom = 4.dp)
        )
    }

    @Composable
    fun VehiculoChip(
        vehiculo: Vehiculo,
        onClick: (Vehiculo) -> Unit,
        modifier: Modifier = Modifier
    ) {
        SuggestionChip(
            onClick = { onClick(vehiculo) },
            label = { Text("${vehiculo.marcaModelo} (${vehiculo.matricula})") },
            modifier = modifier.padding(end = 4.dp, bottom = 4.dp)
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun PersonasSection(
        personas: List<Persona>,
        selectedPersona: Persona?,
        onPersonaSelected: (Persona?) -> Unit,
        onAddPersona: () -> Unit,
        onDeletePersona: (Persona) -> Unit,
        readOnly: Boolean = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Título y botón añadir
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Personas implicadas",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (!readOnly) {
                    TextButton(onClick = onAddPersona) {
                        Text("Añadir")
                    }
                }
            }

            // Chips para las personas
            if (personas.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    personas.forEach { persona ->
                        PersonaChip(
                            persona = persona,
                            onClick = {
                                if (selectedPersona == persona)
                                    onPersonaSelected(null)
                                else
                                    onPersonaSelected(persona)
                            }
                        )
                    }
                }
            } else {
                Text(
                    "No hay personas implicadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }



            @Composable
            fun PersonaCard(
                persona: Persona,
                onDelete: ((Persona) -> Unit)?,
                readOnly: Boolean,
                modifier: Modifier
            ) {
                TODO("Not yet implemented")
            }

            @OptIn(ExperimentalLayoutApi::class)
            @Composable
            fun VehiculosSection(
                vehiculos: List<Vehiculo>,
                selectedVehiculo: Vehiculo?,
                onVehiculoSelected: (Vehiculo?) -> Unit,
                onAddVehiculo: () -> Unit,
                onDeleteVehiculo: (Vehiculo) -> Unit,
                readOnly: Boolean = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Título y botón añadir
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Vehículos implicados",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (!readOnly) {
                            TextButton(onClick = onAddVehiculo) {
                                Text("Añadir")
                            }
                        }
                    }

                    // Chips para los vehículos
                    if (vehiculos.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            vehiculos.forEach { vehiculo ->
                                VehiculoChip(
                                    vehiculo = vehiculo,
                                    onClick = {
                                        if (selectedVehiculo == vehiculo)
                                            onVehiculoSelected(null)
                                        else
                                            onVehiculoSelected(vehiculo)
                                    }
                                )
                            }
                        }
                    } else {
                        Text(
                            "No hay vehículos implicados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Detalles del vehículo seleccionado
                    selectedVehiculo?.let { vehiculo ->
                        VehiculoCard(
                            vehiculo = vehiculo,
                            onDelete = if (!readOnly) onDeleteVehiculo else null,
                            readOnly = readOnly,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

