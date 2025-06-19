package com.project.rc_mecha_maint.ui.mas.facturas

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.data.entity.Workshop
import java.text.SimpleDateFormat
import java.util.*

class InvoiceAdapter(
    private val talleres: List<Workshop>,
    private val onEliminarFactura: (Invoice) -> Unit,
    private val onReprocesarOCR: (Invoice) -> Unit
) : ListAdapter<Invoice, InvoiceAdapter.InvoiceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_factura, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageThumbnail  = itemView.findViewById<ImageView>(R.id.imgFacturaThumb)
        private val textFecha       = itemView.findViewById<TextView>(R.id.tvFecha)
        private val textConcepto    = itemView.findViewById<TextView>(R.id.tvConcepto)
        private val textMonto       = itemView.findViewById<TextView>(R.id.tvMonto)
        private val textCatTaller   = itemView.findViewById<TextView>(R.id.tvCategoriaTaller)
        private val ratingBar       = itemView.findViewById<RatingBar>(R.id.ratingBarItem)
        private val textCotizacion  = itemView.findViewById<TextView>(R.id.tvCotizacion)
        private val btnEliminar     = itemView.findViewById<Button>(R.id.btnEliminarFact)
        private val btnReprocesar   = itemView.findViewById<Button>(R.id.btnReprocesarOCR)

        fun bind(invoice: Invoice) {
            // Miniatura
            imageThumbnail.load(Uri.parse(invoice.rutaImagen)) {
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_broken_image)
            }

            // Fecha
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textFecha.text = sdf.format(Date(invoice.fechaTimestamp))

            // Concepto y Monto
            textConcepto.text = invoice.concepto
            textMonto.text    = "€ ${"%.2f".format(invoice.monto)}"

            // Categoría · Taller
            val categoria    = invoice.categoria.ifBlank { "—" }
            val tallerNombre = talleres
                .firstOrNull { it.id == invoice.tallerId }
                ?.nombre ?: "—"
            textCatTaller.text = "Categoría: $categoria · Taller: $tallerNombre"

            // Rating
            ratingBar.rating = invoice.calificacion?.toFloat() ?: 0f

            // Cotización
            textCotizacion.visibility =
                if (invoice.esCotizacion == true) View.VISIBLE else View.GONE

            // Botones
            btnEliminar   .setOnClickListener { onEliminarFactura(invoice) }
            btnReprocesar .setOnClickListener { onReprocesarOCR(invoice) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Invoice>() {
        override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice) =
            oldItem == newItem
    }
}
