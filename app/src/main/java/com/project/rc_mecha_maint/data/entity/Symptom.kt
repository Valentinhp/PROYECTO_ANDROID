package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un síntoma.
 */
@Entity(tableName = "symptom_table")
data class Symptom(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nombre: String,
    val categoria: String
)
