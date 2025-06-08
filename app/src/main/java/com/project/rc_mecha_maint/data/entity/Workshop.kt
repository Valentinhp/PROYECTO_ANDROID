package com.project.rc_mecha_maint.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Taller mecánico.
 * id = autogenerado → podemos editar / borrar con Room.
 */
@Parcelize
@Entity(tableName = "workshop_table")
data class Workshop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val telefono: String,
    val fotoUrl: String
) : Parcelable
