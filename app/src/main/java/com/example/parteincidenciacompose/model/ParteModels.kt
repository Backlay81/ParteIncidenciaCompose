package com.example.parteincidenciacompose.model

data class Tarea(val descripcion: String, val observaciones: String, val hora: String)

data class Persona(
    val nombre: String,
    val dni: String,
    val fechaNacimiento: String = "",
    val hijoDe: String = "",
    val lugarNacimiento: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val email: String = "",
    val otros: String = ""
)

data class Vehiculo(
    val marcaModelo: String,
    val matricula: String,
    val tipo: String = "",
    val color: String = "",
    val itv: String = "",
    val seguro: String = "",
    val otros: String = ""
)

data class Incidencia(
    val descripcion: String,
    val observaciones: String,
    val hora: String, // hora de creación
    val horaFinalizacion: String = "", // hora de finalización
    val resolucion: String = "",
    val personasImplicadas: List<Persona> = emptyList(),
    val vehiculosImplicados: List<Vehiculo> = emptyList()
)
