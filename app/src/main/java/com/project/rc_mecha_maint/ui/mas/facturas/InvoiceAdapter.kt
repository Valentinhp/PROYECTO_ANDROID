package com.project.rc_mecha_maint.ui.mas.facturas

// ui/facturas/InvoiceAdapter.kt


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Invoice
import coil.load
import java.text.SimpleDateFormat
import java.util.*

/**
 * InvoiceAdapter: extiende ListAdapter para mostrar facturas en un historial.
 *
 * @param onEliminarFactura Callback al tocar “Eliminar”.
 * @param onReprocesarOCR   Callback al tocar “Reprocesar OCR”.
 */
class InvoiceAdapter(
    private val onEliminarFactura: (Invoice) -> Unit,
    private val onReprocesarOCR: (Invoice) -> Unit
) : ListAdapter<Invoice, InvoiceAdapter.InvoiceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_factura, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoiceItem = getItem(position)
        holder.bind(invoiceItem)
    }

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageThumbnail: ImageView = itemView.findViewById(R.id.imageThumbnailFact)
        private val textMonto: TextView = itemView.findViewById(R.id.textMontoFact)
        private val textFecha: TextView = itemView.findViewById(R.id.textFechaFact)
        private val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarFact)
        private val btnReprocesar: Button = itemView.findViewById(R.id.btnReprocesarOCR)

        fun bind(invoice: Invoice) {
            // Mostrar miniatura (usando Coil)
            imageThumbnail.load(Uri.parse(invoice.rutaImagen)) {
                placeholder(R.drawable.ic_image_placeholder) // ícono por defecto si no carga
                error(R.drawable.ic_broken_image)             // ícono si falla
            }

            // Formatear fecha
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textFecha.text = sdf.format(Date(invoice.fechaTimestamp))

            // Mostrar monto
            textMonto.text = "$ ${String.format(Locale.getDefault(), "%.2f", invoice.monto)}"

            // Listeners para botones
            btnEliminar.setOnClickListener {
                onEliminarFactura(invoice)
            }
            btnReprocesar.setOnClickListener {
                onReprocesarOCR(invoice)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Invoice>() {
        override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
            return oldItem == newItem
        }
    }
}
