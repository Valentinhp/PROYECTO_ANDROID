package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un mantenimiento programado.
 * tableName debe coincidir con el DAO: "maintenance_table"
 */
@Entity(tableName = "maintenance_table")
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Nombre o tipo de servicio (p. ej. “Cambio de aceite”)
    val tipoServicio: String,

    // Fecha en milisegundos desde Epoch
    val fechaTimestamp: Long
)
