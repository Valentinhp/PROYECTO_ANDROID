package com.project.rc_mecha_maint.data.entity

/**
 * Mapea el resultado de la consulta:
 *   SELECT categoria AS categoria, SUM(monto) AS total
 * GROUP BY categoria
 */
data class CategoriaTotal(
    val categoria: String,
    val total: Double
)
