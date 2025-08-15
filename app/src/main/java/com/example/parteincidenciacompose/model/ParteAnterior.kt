package com.example.parteincidenciacompose.model

import com.example.parteincidenciacompose.model.Tarea
import com.example.parteincidenciacompose.model.Incidencia


data class ParteAnterior(
    val unidad: String,
    val agente: String,
    val vehiculo: String,
    val kmsIniciales: String,
    val kmsFinales: String,
    val fechaHoraInicio: String,
    val tareas: List<Tarea>,
    val incidencias: List<Incidencia>
)
