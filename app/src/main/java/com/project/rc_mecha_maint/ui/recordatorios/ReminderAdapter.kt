// ui/recordatorios/ReminderAdapter.kt

package com.project.rc_mecha_maint.ui.recordatorios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Reminder
import java.text.SimpleDateFormat
import java.util.*

// Función auxiliar para formatear la fecha a día/mes/año
private fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

class ReminderAdapter(
    private val onEditClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recordatorio, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(reminder)
    }

    // ViewHolder interno para contener vistas de cada item
    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        private val tvKilometraje: TextView = itemView.findViewById(R.id.tvKilometraje)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(reminder: Reminder) {
            // Asignamos datos a los TextViews
            tvFecha.text = reminder.fechaTimestamp.toFormattedDate()
            tvTipo.text = reminder.tipo
            tvKilometraje.text = "Km: ${reminder.kilometraje}"

            // Listener para editar
            btnEditar.setOnClickListener {
                onEditClick(reminder)
            }
            // Listener para eliminar
            btnEliminar.setOnClickListener {
                onDeleteClick(reminder)
            }
        }
    }
}

// DiffUtil para optimizar cambios en la lista
class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}
