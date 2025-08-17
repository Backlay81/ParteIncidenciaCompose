package com.example.parteincidenciacompose.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC")
    fun getAllMovimientos(): Flow<List<MovimientoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovimiento(movimiento: MovimientoEntity)

    @Delete
    suspend fun deleteMovimiento(movimiento: MovimientoEntity)
    
    // Para obtener movimientos agrupados por fecha
    @Query("SELECT fecha, SUM(CASE WHEN positivo = 1 THEN CAST(REPLACE(REPLACE(REPLACE(cantidad, '+', ''), ' h', ''), ',', '.') AS REAL) ELSE 0 END) as horasCompensadas, " +
           "SUM(CASE WHEN positivo = 0 THEN CAST(REPLACE(REPLACE(REPLACE(cantidad, '-', ''), ' h', ''), ',', '.') AS REAL) ELSE 0 END) as horasGastadas " +
           "FROM movimientos GROUP BY fecha ORDER BY fecha DESC")
    fun getResumenPorFecha(): Flow<List<ResumenFecha>>
}
