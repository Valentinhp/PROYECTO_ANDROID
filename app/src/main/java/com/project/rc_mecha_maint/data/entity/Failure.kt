package com.project.rc_mecha_maint.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Entidad que representa una falla diagnóstica.
 * Ahora implementa Parcelable para poder enviarse entre fragments.
 */
@Parcelize
@Entity(tableName = "failure_table")
data class Failure(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Nombre de la falla */
    val nombreFalla: String,

    /** Descripción detallada */
    val descripcion: String,

    /** Recomendación para el usuario */
    val recomendacion: String,

    /** IDs de síntomas en JSON, e.g. "[1,3,5]" */
    val sintomasJSON: String
) : Parcelable
