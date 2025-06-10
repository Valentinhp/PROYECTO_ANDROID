package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa el perfil de usuario.
 */
@Entity(tableName = "user_profile_table")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val rol: String,
    val fotoURI: String
)
