// app/src/main/java/com/project/rc_mecha_maint/ui/mas/talleres/WorkshopAdapter.kt
package com.project.rc_mecha_maint.ui.mas.talleres

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.ItemTallerBinding

class WorkshopAdapter : ListAdapter<Workshop, WorkshopAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Workshop>() {
            override fun areItemsTheSame(a: Workshop, b: Workshop) = a.id == b.id
            override fun areContentsTheSame(a: Workshop, b: Workshop) = a == b
        }
    }

    inner class VH(val b: ItemTallerBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(w: Workshop) {
            b.tvNombreTaller.text    = w.nombre
            b.tvDireccionTaller.text = w.direccion

            // Llamar
            b.btnLlamar.setOnClickListener {
                it.context.startActivity(
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${w.telefono}"))
                )
            }
            // Abrir Maps
            b.btnMapa.setOnClickListener {
                val geo = "geo:${w.latitud},${w.longitud}?q=${Uri.encode(w.nombre)}"
                it.context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(geo))
                )
            }
            // Ir a Comparador
            b.btnCotizar.setOnClickListener {
                val bundle = bundleOf("workshop" to w)
                it.findNavController()
                    .navigate(R.id.action_global_nav_comparador, bundle)
            }
            // Detalle Taller
            b.root.setOnClickListener {
                val bundle = bundleOf("workshop" to w)
                it.findNavController()
                    .navigate(R.id.action_talleres_to_detalleTaller, bundle)
            }
            // Edición larga
            b.root.setOnLongClickListener {
                AddEditTallerDialog.newInstance(w)
                    .show(
                        (it.context as androidx.fragment.app.FragmentActivity)
                            .supportFragmentManager,
                        "AddEditTaller"
                    )
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTallerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}
