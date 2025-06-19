package com.project.rc_mecha_maint.ui.mas.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.History
import java.text.SimpleDateFormat
import java.util.*

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

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textFecha: TextView = itemView.findViewById(R.id.textFechaHistory)
        private val textDescripcion: TextView = itemView.findViewById(R.id.textDescripcionHistory)
        private val textCosto: TextView = itemView.findViewById(R.id.textCostoHistory)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarItem)
        private val btnAgregar: Button = itemView.findViewById(R.id.btnAgregarFactura)
        private val btnVer: Button = itemView.findViewById(R.id.btnVerFacturas)

        fun bind(history: History) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textFecha.text = sdf.format(Date(history.fechaTimestamp))
            textDescripcion.text = history.descripcion
            textCosto.text = "$ ${String.format(Locale.getDefault(), "%.2f", history.costo)}"
            ratingBar.rating = history.calificacion?.toFloat() ?: 0f

            btnAgregar.setOnClickListener { onAgregarFactura(history) }
            btnVer.setOnClickListener { onVerFacturas(history) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }
}
