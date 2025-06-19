// app/src/main/java/com/project/rc_mecha_maint/ui/mas/talleres/FragmentDetalleTaller.kt
package com.project.rc_mecha_maint.ui.mas.talleres

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.FragmentDetalleTallerBinding

class FragmentDetalleTaller : Fragment() {
    private var _b: FragmentDetalleTallerBinding? = null
    private val b get() = _b!!
    private lateinit var taller: Workshop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taller = requireArguments().getParcelable("workshop")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentDetalleTallerBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Datos básicos
        b.tvName.text       = taller.nombre
        b.ratingBar.rating  = taller.rating
        b.tvAddress.text    = taller.direccion
        b.tvPhone.text      = taller.telefono
        Glide.with(this)
            .load(taller.fotoUrl)
            .centerCrop()
            .into(b.ivPhoto)

        // Llamar
        b.btnLlamar.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:${taller.telefono}"))
            )
        }
        // Mapa
        b.btnMapa.setOnClickListener {
            val geo = "geo:${taller.latitud},${taller.longitud}?q=${Uri.encode(taller.nombre)}"
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse(geo)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
