package com.project.rc_mecha_maint.ui.vehiculos

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle

class VehicleAdapter(
    private val onEditClick: (Vehicle) -> Unit,
    private val onDeleteClick: (Vehicle) -> Unit
) : ListAdapter<Vehicle, VehicleAdapter.VehicleViewHolder>(VehicleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VehicleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vehicle, parent, false)
        )

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgThumb  = itemView.findViewById<ImageView>(R.id.imgVehicleThumbnail)
        private val tvName    = itemView.findViewById<TextView>(R.id.tvVehicleName)
        private val tvInfo    = itemView.findViewById<TextView>(R.id.tvVehicleInfo)
        private val btnEdit   = itemView.findViewById<ImageButton>(R.id.buttonEdit)
        private val btnDelete = itemView.findViewById<ImageButton>(R.id.buttonDelete)

        fun bind(vehicle: Vehicle) {
            // Carga con Glide
            if (vehicle.photoUri != null) {
                Glide.with(itemView)
                    .load(Uri.parse(vehicle.photoUri))
                    .placeholder(R.drawable.ic_directions_car)
                    .error(R.drawable.ic_directions_car)
                    .centerCrop()
                    .into(imgThumb)
            } else {
                imgThumb.setImageResource(R.drawable.ic_directions_car)
            }

            tvName.text = "${vehicle.marca} ${vehicle.modelo}"
            tvInfo.text = "Año: ${vehicle.anio} • Matrícula: ${vehicle.matricula}"

            btnEdit.setOnClickListener   { onEditClick(vehicle) }
            btnDelete.setOnClickListener { onDeleteClick(vehicle) }
        }
    }
}

private class VehicleDiffCallback : DiffUtil.ItemCallback<Vehicle>() {
    override fun areItemsTheSame(old: Vehicle, new: Vehicle)    = old.id == new.id
    override fun areContentsTheSame(old: Vehicle, new: Vehicle) = old == new
}
