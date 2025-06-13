package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un mantenimiento programado o realizado.
 * - Se guarda en la tabla "maintenance_table"
 * - Guarda tipo de servicio, taller, costo, la fecha y a qué vehículo pertenece.
 */
@Entity(tableName = "maintenance_table")
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // ID del vehículo al que pertenece este mantenimiento
    val vehicleId: Long,

    // Tipo de servicio, por ejemplo: “Cambio de aceite”
    val tipoServicio: String,

    // Nombre del taller donde se hizo el mantenimiento
    val taller: String,

    // Costo total del mantenimiento (en pesos, por ejemplo)
    val costo: Double,

    // Fecha del mantenimiento (guardada como timestamp en milisegundos)
    val fechaTimestamp: Long
)
