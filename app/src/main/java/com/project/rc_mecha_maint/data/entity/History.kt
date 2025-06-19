// app/src/main/java/com/project/rc_mecha_maint/data/entity/History.kt

package com.project.rc_mecha_maint.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad History: representa un servicio hecho a un vehículo.
 *
 * @param id             Identificador único (autogenerado).
 * @param vehicleId      ID del vehículo al que pertenece este historial.
 * @param fechaTimestamp Fecha del servicio, guardada en milisegundos.
 * @param descripcion    Descripción breve del trabajo realizado.
 * @param categoria      Categoría o tipo de servicio (por ejemplo "Aceite", "Frenos", etc.).
 * @param costo          Costo del servicio (double).
 * @param calificacion   Calificación opcional de 1 a 5 estrellas.
 */
@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "vehicleId")
    val vehicleId: Long,

    @ColumnInfo(name = "fechaTimestamp")
    val fechaTimestamp: Long,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "categoria")
    val categoria: String,

    @ColumnInfo(name = "costo")
    val costo: Double,

    @ColumnInfo(name = "calificacion")
    val calificacion: Int? = null // ⭐ Calificación opcional de 1 a 5
)
