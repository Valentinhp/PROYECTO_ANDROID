// app/src/main/java/com/project/rc_mecha_maint/data/entity/AutoparteEntity.kt
package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autoparte_table")
data class AutoparteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clave: String,
    val descripcion: String,
    val proveedor: String,  // nombre del taller
    val telefono: String,   // número de teléfono
    val precio: Float       // precio convertido a Float
)
