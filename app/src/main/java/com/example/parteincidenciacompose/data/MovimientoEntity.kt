package com.example.parteincidenciacompose.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cantidad: String,
    val fecha: String,
    val motivo: String,
    val positivo: Boolean,
    val horaInicio: String? = null,
    val horaFin: String? = null
)
