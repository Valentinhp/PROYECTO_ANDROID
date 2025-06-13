package com.project.rc_mecha_maint.ui.mas.talleres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.project.rc_mecha_maint.databinding.FragmentDetalleTallerBinding

class FragmentDetalleTaller : Fragment() {

    private var _binding: FragmentDetalleTallerBinding? = null
    private val b get() = _binding!!

    private val args by navArgs<FragmentDetalleTallerArgs>()
    private val workshop by lazy { args.workshop }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleTallerBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tvName   .text = workshop.nombre
        b.tvAddress.text = workshop.direccion
        b.tvPhone  .text = workshop.telefono
        b.ivPhoto  .load(workshop.fotoUrl)

        b.btnLlamar.setOnClickListener { llamarNumero(workshop.telefono) }
        b.btnMapa  .setOnClickListener { abrirMapa(workshop.latitud, workshop.longitud) }
    }

    private fun llamarNumero(tel: String) {
        // … implementa Intent.ACTION_DIAL con Uri.parse("tel:$tel")
    }

    private fun abrirMapa(lat: Double, lng: Double) {
        // … implementa geo Intent con Uri.parse("geo:$lat,$lng?q=$lat,$lng(label)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
