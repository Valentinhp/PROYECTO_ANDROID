package com.project.rc_mecha_maint.ui.mas.talleres

import android.view.*
import androidx.recyclerview.widget.*
import coil.load
import com.project.rc_mecha_maint.databinding.ItemTallerBinding
import com.project.rc_mecha_maint.data.entity.Workshop

class WorkshopAdapter(
    private val onCall : (Workshop) -> Unit,
    private val onMaps : (Workshop) -> Unit,
    private val onDetail: (Workshop) -> Unit
) : ListAdapter<Workshop, WorkshopAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(a: Workshop, b: Workshop) = a.id == b.id
        override fun areContentsTheSame(a: Workshop, b: Workshop) = a == b
    }

    inner class VH(val b: ItemTallerBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemTallerBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, p: Int) {
        val w = getItem(p)
        with(h.b) {
            txtNombre.text = w.nombre
            txtDireccion.text = w.direccion
            imgFoto.load(w.fotoUrl) { crossfade(true) }
            btnCall.setOnClickListener { onCall(w) }
            btnMapa.setOnClickListener { onMaps(w) }
            root.setOnClickListener   { onDetail(w) }
        }
    }
}
