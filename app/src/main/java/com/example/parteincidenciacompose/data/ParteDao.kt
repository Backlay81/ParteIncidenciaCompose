package com.example.parteincidenciacompose.data

import androidx.room.Update
import com.example.parteincidenciacompose.data.ParteEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ParteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParte(parte: ParteEntity)

    @Query("SELECT * FROM partes ORDER BY id DESC")
    suspend fun getAllPartes(): List<ParteEntity>

    @Query("DELETE FROM partes WHERE id = :id")
    suspend fun deleteParteById(id: Int)

    @Query("UPDATE partes SET kmsFinales = :kmsFinales WHERE id = :id")
    suspend fun updateKmsFinales(id: Int, kmsFinales: String)

    @Update
    suspend fun updateParte(parte: ParteEntity)
}

