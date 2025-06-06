package com.project.rc_mecha_maint.ui.mas.historial


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.History
import java.text.SimpleDateFormat
import java.util.*

/**
 * HistoryAdapter: extiende ListAdapter para mostrar los elementos de History.
 *
 * @param onAgregarFactura Click listener que recibe el History actual.
 * @param onVerFacturas    Click listener que recibe el History actual.
 */
class HistoryAdapter(
    private val onAgregarFactura: (History) -> Unit,
    private val onVerFacturas: (History) -> Unit
) : ListAdapter<History, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = getItem(position)
        holder.bind(historyItem)
    }

    /**
     * ViewHolder interno para reciclar vistas.
     */
    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textFecha: TextView = itemView.findViewById(R.id.textFechaHistory)
        private val textDescripcion: TextView = itemView.findViewById(R.id.textDescripcionHistory)
        private val textCosto: TextView = itemView.findViewById(R.id.textCostoHistory)
        private val btnAgregar: Button = itemView.findViewById(R.id.btnAgregarFactura)
        private val btnVer: Button = itemView.findViewById(R.id.btnVerFacturas)

        fun bind(history: History) {
            // Convertir timestamp a fecha legible
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaTexto = sdf.format(Date(history.fechaTimestamp))

            textFecha.text = fechaTexto
            textDescripcion.text = history.descripcion
            textCosto.text = "$ ${String.format(Locale.getDefault(), "%.2f", history.costo)}"

            // Listeners para botones
            btnAgregar.setOnClickListener {
                onAgregarFactura(history)
            }
            btnVer.setOnClickListener {
                onVerFacturas(history)
            }
        }
    }

    /**
     * DiffCallback: compara dos listas con DiffUtil para optimizar cambios.
     */
    class DiffCallback : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }
}
