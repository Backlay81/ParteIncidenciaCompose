package com.example.parteincidenciacompose.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovimientoViewModel(app: Application) : ViewModel() {
    private val dao = AppDatabase.getDatabase(app).movimientoDao()
    val movimientos: StateFlow<List<MovimientoEntity>> =
        dao.getAllMovimientos().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val resumenPorFecha: StateFlow<List<ResumenFecha>> =
        dao.getResumenPorFecha().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addMovimiento(movimiento: MovimientoEntity) {
        viewModelScope.launch {
            dao.insertMovimiento(movimiento)
        }
    }

    fun deleteMovimiento(movimiento: MovimientoEntity) {
        viewModelScope.launch {
            dao.deleteMovimiento(movimiento)
        }
    }
}

class MovimientoViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovimientoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovimientoViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
