package com.project.rc_mecha_maint.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Ahora con @Parcelize hacemos que Vehicle sea Parcelable,
 * y guardamos la URI de la foto en photoUri.
 */
@Parcelize
@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val matricula: String,
    val kilometraje: Int,         // ← nuevo
    val tipoCombustible: String,  // ← nuevo
    val photoUri: String? = null
) : Parcelable
