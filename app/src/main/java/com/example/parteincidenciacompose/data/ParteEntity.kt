package com.example.parteincidenciacompose.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.parteincidenciacompose.model.ParteAnterior

@Entity(tableName = "partes")
data class ParteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val unidad: String,
    val agente: String,
    val vehiculo: String,
    val kmsIniciales: String,
    val kmsFinales: String,
    val fechaHoraInicio: String,
    val tareasJson: String,
    val incidenciasJson: String
)
