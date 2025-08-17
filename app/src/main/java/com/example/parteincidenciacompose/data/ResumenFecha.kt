package com.example.parteincidenciacompose.data

data class ResumenFecha(
    val fecha: String,
    val horasCompensadas: Double,
    val horasGastadas: Double
) {
    val saldoDelDia: Double
        get() = horasCompensadas - horasGastadas
}
