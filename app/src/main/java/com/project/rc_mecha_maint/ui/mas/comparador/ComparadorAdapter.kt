// app/src/main/java/com/project/rc_mecha_maint/ui/mas/comparador/ComparadorAdapter.kt
package com.project.rc_mecha_maint.ui.mas.comparador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.databinding.ItemComparadorBinding

class ComparadorAdapter(
    private val onLlamar: (String) -> Unit   // recibe el teléfono
) : ListAdapter<AutoparteEntity, ComparadorAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AutoparteEntity>() {
            override fun areItemsTheSame(a: AutoparteEntity, b: AutoparteEntity) =
                a.id == b.id
            override fun areContentsTheSame(a: AutoparteEntity, b: AutoparteEntity) =
                a == b
        }
    }

    inner class VH(val b: ItemComparadorBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: AutoparteEntity) {
            // Mostramos nombre del taller y precio
            b.txtProveedor.text = item.proveedor
            b.txtPrecio.text    = "$${item.precio}"

            b.btnLlamar.setOnClickListener {
                onLlamar(item.telefono)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemComparadorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}
