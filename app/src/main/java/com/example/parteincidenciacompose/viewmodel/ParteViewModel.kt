package com.example.parteincidenciacompose.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.parteincidenciacompose.data.ParteDatabase
import com.example.parteincidenciacompose.data.ParteEntity
import com.example.parteincidenciacompose.repository.ParteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParteViewModel(application: Application) : AndroidViewModel(application) {
    fun finalizarParte(parte: ParteEntity, kmsFinales: String) {
        android.util.Log.d("ParteViewModel", "Finalizando parte: $parte con kmsFinales=$kmsFinales")
        viewModelScope.launch {
            val parteFinalizado = parte.copy(
                kmsFinales = kmsFinales,
                estado = "FINALIZADO" // Siempre asegurar que el estado es FINALIZADO
            )
            android.util.Log.d("ParteViewModel", "Actualizando parte finalizado: $parteFinalizado")
            repository.updateParte(parteFinalizado)
            loadPartes()
        }
    }
    private val repository: ParteRepository
    private val _partes = MutableStateFlow<List<ParteEntity>>(emptyList())
    val partes: StateFlow<List<ParteEntity>> = _partes

    init {
        val dao = ParteDatabase.getDatabase(application).parteDao()
        repository = ParteRepository(dao)
        loadPartes()
    }

    fun loadPartes() {
        viewModelScope.launch {
            _partes.value = repository.getAllPartes()
        }
    }

    fun insertParte(parte: ParteEntity) {
        android.util.Log.d("ParteViewModel", "Insertando parte: $parte")
        viewModelScope.launch {
            // Si el parte ya existe y está finalizado, mantener el estado FINALIZADO
            val existente = _partes.value.find { it.id == parte.id }
            val estadoFinal = when {
                existente?.estado == "FINALIZADO" -> "FINALIZADO"
                else -> parte.estado
            }
            val parteAGuardar = parte.copy(estado = estadoFinal)
            repository.insertParte(parteAGuardar)
            loadPartes()
        }
    }

    fun deleteParteById(id: Int) {
        viewModelScope.launch {
            repository.deleteParteById(id)
            loadPartes()
        }
    }
}
