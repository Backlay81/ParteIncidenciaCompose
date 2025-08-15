package com.example.parteincidenciacompose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IncidenciaCard(
    descripcion: String,
    observaciones: String?,
    hora: String,
    resumen: String?,
    numPersonas: Int,
    numVehiculos: Int,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Botones de acción en la esquina superior derecha
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (onEdit != null) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF1976D2))
                    }
                }
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Descripción: $descripcion", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (!observaciones.isNullOrBlank()) {
                    Text("Observaciones: $observaciones", fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
                }
                Text("Hora: $hora", fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
                if (!resumen.isNullOrBlank()) {
                    Text("Resumen: $resumen", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Personas implicadas: $numPersonas", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text("Vehículos implicados: $numVehiculos", fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }
    }
}
