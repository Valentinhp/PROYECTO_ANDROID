package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autoparte_table")
data class AutoparteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clave: String,
    val descripcion: String,
    val precio: Double,
    val proveedor: String,
    val maintenanceId: Long? = null
)
