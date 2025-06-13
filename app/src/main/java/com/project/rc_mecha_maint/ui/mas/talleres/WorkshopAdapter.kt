package com.project.rc_mecha_maint.ui.mas.talleres

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.ItemTallerBinding

class TallerAdapter(
    private val onLlamar: (Workshop) -> Unit,
    private val onCotizar: (Workshop) -> Unit
) : ListAdapter<Workshop, TallerAdapter.VH>(DiffCallback) {

    inner class VH(private val b: ItemTallerBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Workshop) {
            b.tvNombreTaller.text    = item.nombre
            b.tvDireccionTaller.text = item.direccion
            b.btnLlamar.setOnClickListener  { onLlamar(item) }
            b.btnCotizar.setOnClickListener { onCotizar(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTallerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(a: Workshop, b: Workshop) = a.id == b.id
        override fun areContentsTheSame(a: Workshop, b: Workshop) = a == b
    }
}
