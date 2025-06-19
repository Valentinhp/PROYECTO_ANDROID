// app/src/main/java/com/project/rc_mecha_maint/data/entity/Workshop.kt
package com.project.rc_mecha_maint.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "workshop_table")
data class Workshop(
    @PrimaryKey          // <-- ya no autoGenerate
    val id: Long,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val telefono: String,
    val fotoUrl: String,
    val rating: Float = 0f      // ← nuevo campo
) : Parcelable
