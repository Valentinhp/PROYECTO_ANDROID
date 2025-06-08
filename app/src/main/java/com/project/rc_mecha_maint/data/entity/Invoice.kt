// data/entity/Invoice.kt

package com.project.rc_mecha_maint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Invoice: representa una factura fotográfica asociada a un historial.
 *
 * @param id             Identificador autogenerado.
 * @param historyId      ID del historial al que pertenece esta factura.
 * @param rutaImagen     Ruta/URI de la imagen guardada.
 * @param fechaTimestamp Fecha de carga (System.currentTimeMillis()).
 * @param monto          Monto extraído (o corregido) del OCR.
 */
@Entity(tableName = "invoice_table")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val historyId: Long,
    val rutaImagen: String,
    val fechaTimestamp: Long,
    val monto: Double

)
