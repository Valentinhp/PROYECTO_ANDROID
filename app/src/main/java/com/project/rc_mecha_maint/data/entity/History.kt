// data/entity/History.kt

package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad History: representa un servicio hecho a un vehículo.
 *
 * @param id             Identificador único (autogenerado).
 * @param vehicleId      ID del vehículo al que pertenece este historial.
 * @param fechaTimestamp Fecha del servicio, guardada en milisegundos.
 * @param descripcion    Descripción breve del trabajo realizado.
 * @param costo          Costo del servicio (double).
 */
@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val vehicleId: Long,
    val fechaTimestamp: Long,
    val descripcion: String,
    val costo: Double,
    val calificacion: Int? = null // ⭐ Calificación opcional de 1 a 5

)
