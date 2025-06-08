package com.project.rc_mecha_maint.ui.mas.comparador

import android.view.*
import androidx.recyclerview.widget.*
import com.project.rc_mecha_maint.databinding.ItemComparadorBinding
import com.project.rc_mecha_maint.data.entity.Workshop

data class PriceItem(val taller: Workshop, val precio: Int)

object PriceDiff : DiffUtil.ItemCallback<PriceItem>() {
    override fun areItemsTheSame(a: PriceItem, b: PriceItem) = a.taller.id == b.taller.id
    override fun areContentsTheSame(a: PriceItem, b: PriceItem) = a == b
}

class ComparadorAdapter(
    private val onDetail: (Workshop) -> Unit
) : ListAdapter<PriceItem, ComparadorAdapter.VH>(PriceDiff) {

    inner class VH(val b: ItemComparadorBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemComparadorBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, p: Int) {
        val item = getItem(p)
        with(h.b) {
            txtNombre.text = item.taller.nombre
            txtPrecio.text = "$${item.precio} MXN"
            root.setOnClickListener { onDetail(item.taller) }
        }
    }
}
