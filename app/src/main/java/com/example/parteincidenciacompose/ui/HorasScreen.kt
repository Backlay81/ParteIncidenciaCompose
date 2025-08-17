package com.example.parteincidenciacompose.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parteincidenciacompose.data.MovimientoEntity
import com.example.parteincidenciacompose.data.MovimientoViewModel
import com.example.parteincidenciacompose.data.MovimientoViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

val azulito = Color(0xFF1A73E8)
val textoBlanco = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorasScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showSpendDialog by remember { mutableStateOf(false) }
    val hoy = remember { Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) } }
    var fechaMillis by remember { mutableStateOf<Long?>(hoy.timeInMillis) }
    var fecha by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(hoy.timeInMillis))) }
    var showDatePicker by remember { mutableStateOf(false) }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var horasCompensar by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    val owner = LocalViewModelStoreOwner.current
    val movimientoViewModel: MovimientoViewModel = viewModel(
        factory = MovimientoViewModelFactory(context.applicationContext as android.app.Application),
        viewModelStoreOwner = owner!!
    )
    val movimientos = movimientoViewModel.movimientos.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Horas",
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
                    onClick = { showSpendDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = azulito),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.RemoveCircle,
                            contentDescription = null,
                            tint = textoBlanco,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Gastar",
                            color = textoBlanco,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Button(
                    onClick = { /* Acción Consultar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = azulito),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = textoBlanco,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Consultar",
                            color = textoBlanco,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = azulito),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = textoBlanco,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Añadir",
                            color = textoBlanco,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            val saldo = movimientos.fold(0.0) { acc, mov ->
                val cantidad = mov.cantidad.replace(",", ".").toDoubleOrNull() ?: 0.0
                if (mov.positivo) acc + cantidad else acc - cantidad
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Saldo de horas",
                    tint = Color(0xFF43A047),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = (if (saldo >= 0) "+" else "") + String.format(Locale.getDefault(), "%.1f h", kotlin.math.abs(saldo)),
                        color = Color(0xFF43A047),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "disponibles",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Historial variables
            var modoSeleccion by remember { mutableStateOf(false) }
            val seleccionados = remember { mutableStateListOf<Int>() }
            var showEditDialog by remember { mutableStateOf(false) }
            var movimientoAEditar by remember { mutableStateOf<MovimientoEntity?>(null) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            val movimientosAEliminar = remember { mutableStateListOf<MovimientoEntity>() }
            val expandedMovimientos = remember { mutableStateMapOf<Int, Boolean>() }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Historial de movimientos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { modoSeleccion = !modoSeleccion; if (!modoSeleccion) seleccionados.clear() }) {
                    Icon(
                        imageVector = if (modoSeleccion) Icons.Outlined.EditNote else Icons.Default.Edit,
                        contentDescription = if (modoSeleccion) "Cancelar selección" else "Seleccionar movimientos"
                    )
                }
                if (seleccionados.isNotEmpty()) {
                    // If exactly one selected, show edit button
                    if (seleccionados.size == 1) {
                        IconButton(onClick = {
                            val id = seleccionados.first()
                            movimientoAEditar = movimientos.firstOrNull { it.id == id }
                            showEditDialog = movimientoAEditar != null
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar movimiento seleccionado")
                        }
                    }

                    // Delete selected
                    IconButton(
                        onClick = {
                            showEditDialog = false
                            movimientoAEditar = null
                            movimientosAEliminar.clear()
                            movimientosAEliminar.addAll(movimientos.filter { seleccionados.contains(it.id) })
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = "Eliminar movimientos seleccionados",
                            tint = Color(0xFFE53935)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(movimientos) { mov ->
                    val isExpanded = expandedMovimientos[mov.id] == true
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMovimientos[mov.id] = !(expandedMovimientos[mov.id] ?: false) }
                            .padding(vertical = 8.dp)
                            .padding(start = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (modoSeleccion) {
                                Checkbox(
                                    checked = seleccionados.contains(mov.id),
                                    onCheckedChange = { if (it) seleccionados.add(mov.id) else seleccionados.remove(mov.id) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            val cantidadMostrada = (if (mov.positivo) "+" else "-") + mov.cantidad + " h"
                            Text(
                                text = cantidadMostrada,
                                color = if (mov.positivo) Color(0xFF43A047) else Color(0xFFE53935),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = mov.fecha, color = Color.Gray, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = mov.motivo, color = Color.DarkGray, fontSize = 18.sp)
                        }
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!mov.horaInicio.isNullOrBlank()) {
                                    Text(text = "Inicio: ${mov.horaInicio}", color = Color.Gray, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                if (!mov.horaFin.isNullOrBlank()) {
                                    Text(text = "Fin: ${mov.horaFin}", color = Color.Gray, fontSize = 16.sp)
                                }
                            }
                        }
                        Divider(color = Color.LightGray, thickness = 1.dp)
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog && movimientosAEliminar.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        movimientosAEliminar.clear()
                    },
                    title = { Text("¿Eliminar movimiento(s)?", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Estás seguro de que quieres eliminar los movimientos seleccionados? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        Button(onClick = {
                            movimientosAEliminar.forEach { movimientoViewModel.deleteMovimiento(it) }
                            seleccionados.clear()
                            modoSeleccion = false
                            showDeleteDialog = false
                            movimientosAEliminar.clear()
                        }) { Text("Eliminar") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false; movimientosAEliminar.clear() }) { Text("Cancelar") }
                    }
                )
            }

            // Edit dialog
            if (showEditDialog && movimientoAEditar != null) {
                var cantidadSinSigno by remember { mutableStateOf(movimientoAEditar!!.cantidad) }
                var cantidadEdit by remember { mutableStateOf((if (movimientoAEditar!!.positivo) "+" else "-") + cantidadSinSigno) }
                var motivoEdit by remember { mutableStateOf(movimientoAEditar!!.motivo) }
                var fechaEdit by remember { mutableStateOf(movimientoAEditar!!.fecha) }
                var positivoEdit by remember { mutableStateOf(movimientoAEditar!!.positivo) }
                var horaInicioEdit by remember { mutableStateOf(movimientoAEditar!!.horaInicio ?: "") }
                var horaFinEdit by remember { mutableStateOf(movimientoAEditar!!.horaFin ?: "") }
                var showDatePickerEdit by remember { mutableStateOf(false) }
                var fechaMillisEdit by remember {
                    mutableStateOf(
                        try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(movimientoAEditar!!.fecha)?.time } catch (e: Exception) { null }
                    )
                }

                AlertDialog(
                    onDismissRequest = {
                        showEditDialog = false
                        movimientoAEditar = null
                        seleccionados.clear()
                        modoSeleccion = false
                    },
                    title = { Text("Editar movimiento", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().clickable { showDatePickerEdit = true }) {
                                OutlinedTextField(value = fechaEdit, onValueChange = {}, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth(), readOnly = true, enabled = false)
                            }
                            OutlinedTextField(value = horaInicioEdit, onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaInicioEdit = when {
                                    digits.length > 4 -> horaInicioEdit
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            }, label = { Text("Hora inicio (HH:mm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = horaFinEdit, onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaFinEdit = when {
                                    digits.length > 4 -> horaFinEdit
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            }, label = { Text("Hora fin (HH:mm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = cantidadEdit, onValueChange = {
                                val numeroLimpio = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                                cantidadSinSigno = numeroLimpio
                                cantidadEdit = (if (positivoEdit) "+" else "-") + numeroLimpio
                            }, label = { Text("Horas a compensar/gastar") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = motivoEdit, onValueChange = { motivoEdit = it }, label = { Text("Motivo") }, modifier = Modifier.fillMaxWidth())
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = positivoEdit, onCheckedChange = {
                                    positivoEdit = it
                                    cantidadEdit = (if (it) "+" else "-") + cantidadSinSigno
                                })
                                Text("Es positivo (añadir horas)")
                            }
                            if (showDatePickerEdit) {
                                DatePickerDialogIfNeeded(show = showDatePickerEdit, initialDate = fechaMillisEdit, onDateSelected = {
                                    fechaMillisEdit = it
                                    val date = Date(it)
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    fechaEdit = sdf.format(date)
                                    showDatePickerEdit = false
                                }, onDismiss = { showDatePickerEdit = false })
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            movimientoAEditar?.let {
                                movimientoViewModel.addMovimiento(it.copy(cantidad = cantidadSinSigno, motivo = motivoEdit, fecha = fechaEdit, positivo = positivoEdit, horaInicio = if (horaInicioEdit.isNotBlank()) horaInicioEdit else null, horaFin = if (horaFinEdit.isNotBlank()) horaFinEdit else null))
                            }
                            showEditDialog = false
                            movimientoAEditar = null
                            seleccionados.clear()
                            modoSeleccion = false
                        }) { Text("Guardar") }
                    },
                    dismissButton = { OutlinedButton(onClick = { showEditDialog = false; movimientoAEditar = null; seleccionados.clear(); modoSeleccion = false }) { Text("Cancelar") } }
                )
            }

            // Add dialog
            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("Añadir horas de compensación", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                                OutlinedTextField(value = fecha, onValueChange = {}, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth(), readOnly = true, enabled = false)
                            }
                            OutlinedTextField(value = horaInicio, onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaInicio = when {
                                    digits.length > 4 -> horaInicio
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            }, label = { Text("Hora inicio (HH:mm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = horaFin, onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaFin = when {
                                    digits.length > 4 -> horaFin
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            }, label = { Text("Hora fin (HH:mm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = horasCompensar, onValueChange = { input -> horasCompensar = input.filter { it.isDigit() || it == '.' || it == ',' } }, label = { Text("Horas a compensar") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo") }, modifier = Modifier.fillMaxWidth())
                        }
                    },
                    confirmButton = { Button(onClick = {
                        val cantidadLimpia = horasCompensar.replace("+", "").replace("-", "").trim()
                        movimientoViewModel.addMovimiento(MovimientoEntity(cantidad = cantidadLimpia, fecha = fecha, motivo = motivo, positivo = true, horaInicio = if (horaInicio.isNotBlank()) horaInicio else null, horaFin = if (horaFin.isNotBlank()) horaFin else null))
                        horaInicio = ""; horaFin = ""; horasCompensar = ""; motivo = ""
                        val hoy = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
                        fechaMillis = hoy.timeInMillis
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(hoy.timeInMillis))
                        showAddDialog = false
                    }) { Text("Guardar") } },
                    dismissButton = { OutlinedButton(onClick = { showAddDialog = false }) { Text("Cancelar") } }
                )
            }

            // Spend dialog
            if (showSpendDialog) {
                AlertDialog(
                    onDismissRequest = { showSpendDialog = false },
                    title = { Text("Gastar horas de compensación", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                                OutlinedTextField(value = fecha, onValueChange = {}, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth(), readOnly = true, enabled = false)
                            }
                            OutlinedTextField(value = horaInicio, onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaInicio = when {
                                    digits.length > 4 -> horaInicio
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            }, label = { Text("Hora inicio (HH:mm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = horaFin, onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaFin = when {
                                    digits.length > 4 -> horaFin
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            }, label = { Text("Hora fin (HH:mm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = horasCompensar, onValueChange = { input -> horasCompensar = input.filter { it.isDigit() || it == '.' || it == ',' } }, label = { Text("Horas a gastar") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo") }, modifier = Modifier.fillMaxWidth())
                        }
                    },
                    confirmButton = { Button(onClick = {
                        val cantidadLimpia = horasCompensar.replace("+", "").replace("-", "").trim()
                        movimientoViewModel.addMovimiento(MovimientoEntity(cantidad = cantidadLimpia, fecha = fecha, motivo = motivo, positivo = false, horaInicio = if (horaInicio.isNotBlank()) horaInicio else null, horaFin = if (horaFin.isNotBlank()) horaFin else null))
                        horaInicio = ""; horaFin = ""; horasCompensar = ""; motivo = ""
                        val hoy = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
                        fechaMillis = hoy.timeInMillis
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(hoy.timeInMillis))
                        showSpendDialog = false
                    }) { Text("Guardar") } },
                    dismissButton = { OutlinedButton(onClick = { showSpendDialog = false }) { Text("Cancelar") } }
                )
            }

            // Date picker outside dialogs
            if (showDatePicker) {
                DatePickerDialogIfNeeded(show = showDatePicker, initialDate = fechaMillis, onDateSelected = {
                    fechaMillis = it
                    val date = Date(it)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    fecha = sdf.format(date)
                    showDatePicker = false
                }, onDismiss = { showDatePicker = false })
            }
        }
    }
}