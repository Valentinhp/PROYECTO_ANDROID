package com.project.rc_mecha_maint.ui.vehiculos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle

/**
 * VehicleAdapter extiende ListAdapter para manejar la lista de Vehicle.
 * Usa DiffUtil para actualizar solo lo que cambia en la lista.
 */
class VehicleAdapter(
    private val onEditClick: (Vehicle) -> Unit,
    private val onDeleteClick: (Vehicle) -> Unit
) : ListAdapter<Vehicle, VehicleAdapter.VehicleViewHolder>(VehicleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        // Inflar el layout de item_vehicle.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada ítem.
     */
    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMarcaModelo: TextView = itemView.findViewById(R.id.textViewMarcaModelo)
        private val tvAnio: TextView = itemView.findViewById(R.id.textViewAnio)
        private val tvMatricula: TextView = itemView.findViewById(R.id.textViewMatricula)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(vehicle: Vehicle) {
            // Mostrar datos del vehículo en las vistas
            tvMarcaModelo.text = "${vehicle.marca} - ${vehicle.modelo}"
            tvAnio.text = "Año: ${vehicle.anio}"
            tvMatricula.text = vehicle.matricula

            // Configurar botón Editar: llama al callback con el objeto completo
            btnEdit.setOnClickListener {
                onEditClick(vehicle)
            }

            // Configurar botón Eliminar: llama al callback con el objeto completo
            btnDelete.setOnClickListener {
                onDeleteClick(vehicle)
            }
        }
    }
}

/**
 * DiffUtil para comparar dos listas de Vehicle y solo actualizar lo que cambió.
 */
class VehicleDiffCallback : DiffUtil.ItemCallback<Vehicle>() {
    override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
        // Mismo ítem si el id coincide
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
        // Mismos datos si cada campo coincide
        return oldItem == newItem
    }
}
