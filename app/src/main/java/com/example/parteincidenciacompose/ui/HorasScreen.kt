package com.example.parteincidenciacompose.ui

// import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.TextFieldDefaults
import com.example.parteincidenciacompose.ui.DatePickerDialogIfNeeded


// Data class para los movimientos
data class Movimiento(
    val cantidad: String,
    val fecha: String,
    val motivo: String,
    val positivo: Boolean
)


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
    var fecha by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(hoy.timeInMillis)))
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var horasCompensar by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    val movimientos = remember { mutableStateListOf<Movimiento>() }

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
        if (showSpendDialog) {
            AlertDialog(
                onDismissRequest = { showSpendDialog = false },
                title = { Text("Gastar horas de compensación", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = fecha,
                                onValueChange = {},
                                label = { Text("Fecha") },
                                modifier = Modifier.fillMaxWidth().pointerInput(Unit) {},
                                readOnly = true,
                                enabled = false,
                            )
                        }
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaInicio = when {
                                    digits.length > 4 -> horaInicio
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            },
                            label = { Text("Hora inicio (HH:mm)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaFin = when {
                                    digits.length > 4 -> horaFin
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            },
                            label = { Text("Hora fin (HH:mm)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = horasCompensar,
                            onValueChange = { horasCompensar = it },
                            label = { Text("Horas a gastar") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = motivo,
                            onValueChange = { motivo = it },
                            label = { Text("Motivo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        movimientos.add(
                            Movimiento(
                                cantidad = "-" + horasCompensar + " h",
                                fecha = fecha,
                                motivo = motivo,
                                positivo = false
                            )
                        )
                        horaInicio = ""
                        horaFin = ""
                        horasCompensar = ""
                        motivo = ""
                        val hoy = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
                        fechaMillis = hoy.timeInMillis
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(hoy.timeInMillis))
                        showSpendDialog = false
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showSpendDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
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
            // Calcular saldo actual
            val saldo = movimientos.fold(0.0) { acc, mov ->
                val cantidad = mov.cantidad.replace("+", "").replace("-", "").replace(" h", "").replace(",", ".").toDoubleOrNull() ?: 0.0
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
                    tint = Color(0xFF43A047), // Verde
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = (if (saldo >= 0) "+" else "-") + String.format(Locale.getDefault(), "%.1f h", kotlin.math.abs(saldo)),
                        color = Color(0xFF43A047), // Verde
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
            // Historial de movimientos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Historial de movimientos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                movimientos.forEach { mov ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (mov.positivo) Icons.Default.Add else Icons.Default.RemoveCircle,
                            contentDescription = null,
                            tint = if (mov.positivo) Color(0xFF43A047) else Color(0xFFE53935),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = mov.cantidad,
                            color = if (mov.positivo) Color(0xFF43A047) else Color(0xFFE53935),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = mov.fecha,
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = mov.motivo,
                            color = Color.DarkGray,
                            fontSize = 15.sp
                        )
                    }
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Añadir horas de compensación", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = fecha,
                                onValueChange = {},
                                label = { Text("Fecha") },
                                modifier = Modifier.fillMaxWidth().pointerInput(Unit) {},
                                readOnly = true,
                                enabled = false, // para evitar el teclado
                            )
                        }
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaInicio = when {
                                    digits.length > 4 -> horaInicio // no permitir más de 4 dígitos
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            },
                            label = { Text("Hora inicio (HH:mm)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                horaFin = when {
                                    digits.length > 4 -> horaFin // no permitir más de 4 dígitos
                                    digits.length > 2 -> digits.substring(0, 2) + ":" + digits.substring(2)
                                    else -> digits
                                }
                            },
                            label = { Text("Hora fin (HH:mm)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = horasCompensar,
                            onValueChange = { horasCompensar = it },
                            label = { Text("Horas a compensar") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = motivo,
                            onValueChange = { motivo = it },
                            label = { Text("Motivo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Añadir movimiento
                        movimientos.add(
                            Movimiento(
                                cantidad = "+${horasCompensar} h",
                                fecha = fecha,
                                motivo = motivo,
                                positivo = true
                            )
                        )
                        // Limpiar campos
                        horaInicio = ""
                        horaFin = ""
                        horasCompensar = ""
                        motivo = ""
                        // Opcional: resetear fecha a hoy
                        val hoy = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
                        fechaMillis = hoy.timeInMillis
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(hoy.timeInMillis))
                        showAddDialog = false
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        // El calendario debe ir fuera del AlertDialog para evitar problemas de diálogos anidados
        DatePickerDialogIfNeeded(
            show = showDatePicker,
            initialDate = fechaMillis,
            onDateSelected = {
                fechaMillis = it
                val date = Date(it)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fecha = sdf.format(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}



