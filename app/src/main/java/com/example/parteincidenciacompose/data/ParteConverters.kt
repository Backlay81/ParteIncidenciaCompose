package com.example.parteincidenciacompose.data

import com.example.parteincidenciacompose.model.Incidencia
import com.example.parteincidenciacompose.model.Tarea
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ParteConverters {
    private val gson = Gson()

    fun tareasToJson(tareas: List<Tarea>): String = gson.toJson(tareas)
    fun incidenciasToJson(incidencias: List<Incidencia>): String = gson.toJson(incidencias)

    fun tareasFromJson(json: String): List<Tarea> =
        gson.fromJson(json, object : TypeToken<List<Tarea>>() {}.type)

    fun incidenciasFromJson(json: String): List<Incidencia> =
        gson.fromJson(json, object : TypeToken<List<Incidencia>>() {}.type)
}
