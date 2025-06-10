package com.project.rc_mecha_maint.ui.mas.diagnostico

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.data.entity.Symptom
import com.project.rc_mecha_maint.databinding.ItemSintomaBinding

/**
 * Adapter para mostrar síntomas con CheckBox.
 */
class SintomaAdapter(
    private val onCheckedChange: (id: Long, checked: Boolean) -> Unit
) : ListAdapter<Symptom, SintomaAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Symptom>() {
        override fun areItemsTheSame(old: Symptom, new: Symptom) = old.id == new.id
        override fun areContentsTheSame(old: Symptom, new: Symptom) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemSintomaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val b: ItemSintomaBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: Symptom) {
            b.chkSintoma.text = item.nombre
            b.chkSintoma.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(item.id, isChecked)
            }
        }
    }
}
