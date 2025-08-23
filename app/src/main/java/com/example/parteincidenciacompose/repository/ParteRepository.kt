package com.example.parteincidenciacompose.repository

import com.example.parteincidenciacompose.data.ParteDao
import com.example.parteincidenciacompose.data.ParteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParteRepository(private val parteDao: ParteDao) {
    suspend fun updateKmsFinales(id: Int, kmsFinales: String) = withContext(Dispatchers.IO) {
        parteDao.updateKmsFinales(id, kmsFinales)
    }
    suspend fun insertParte(parte: ParteEntity) = withContext(Dispatchers.IO) {
        parteDao.insertParte(parte)
    }

    suspend fun getAllPartes(): List<ParteEntity> = withContext(Dispatchers.IO) {
        parteDao.getAllPartes()
    }
    suspend fun deleteParteById(id: Int) = withContext(Dispatchers.IO) {
        parteDao.deleteParteById(id)
    }

    suspend fun updateParte(parte: ParteEntity) = withContext(Dispatchers.IO) {
        parteDao.updateParte(parte)
    }
}
