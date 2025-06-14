package com.project.rc_mecha_maint.ui.mas.comparador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.databinding.ItemAutoparteBinding

class AutoparteAdapter(
    private val onGuardar: (AutoparteEntity) -> Unit
) : ListAdapter<AutoparteEntity, AutoparteAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AutoparteEntity>() {
            override fun areItemsTheSame(a: AutoparteEntity, b: AutoparteEntity) =
                a.id == b.id
            override fun areContentsTheSame(a: AutoparteEntity, b: AutoparteEntity) =
                a == b
        }
    }

    inner class VH(val b: ItemAutoparteBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AutoparteEntity) {
            b.tvClave.text = item.clave
            b.tvDescripcion.text = item.descripcion
            b.tvPrecio.text = "$${item.precio}"
            b.btnGuardar.setOnClickListener { onGuardar(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAutoparteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}
