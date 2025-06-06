package com.project.rc_mecha_maint.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize  // <-- Import de Parcelize

/**
 * Entidad Reminder para Room.
 * Con @Parcelize implementa Parcelable para poder enviarse entre Fragments.
 */
@Parcelize
@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val tipo: String,

    val fechaTimestamp: Long,

    val kilometraje: Int,

    val descripcion: String,

    val notificar: Boolean
) : Parcelable  // <-- Implementa Parcelable gracias a @Parcelize
