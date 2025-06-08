package com.project.rc_mecha_maint.ui.mas.talleres

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.project.rc_mecha_maint.databinding.FragmentDetalleTallerBinding
import com.project.rc_mecha_maint.data.entity.Workshop

class FragmentDetalleTaller : Fragment() {

    private var _b: FragmentDetalleTallerBinding? = null
    private val b get() = _b!!
    private val args by navArgs<FragmentDetalleTallerArgs>()
    private val vm by activityViewModels<WorkshopViewModel>()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentDetalleTallerBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        vm.workshops.observe(viewLifecycleOwner) { lista ->
            lista.find { it.id == args.tallerId }?.let { mostrar(it) }
        }
    }

    private fun mostrar(w: Workshop) = with(b) {
        txtNombre.text = w.nombre
        txtDireccion.text = w.direccion
        imgFoto.load(w.fotoUrl) { crossfade(true) }

        btnCall.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${w.telefono}")))
        }
        btnMaps.setOnClickListener {
            val geo = "geo:${w.latitud},${w.longitud}?q=${Uri.encode(w.nombre)}"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geo)))
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
