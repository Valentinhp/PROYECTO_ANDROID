package com.project.rc_mecha_maint.data.model

data class Service(
    val id: Int,
    val failureId: Int,
    val clave: String,
    val descripcion: String,
    val precio: Double,
    val workshopId: Int
)
