package com.project.rc_mecha_maint.ui.mas.diagnostico

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.databinding.FragmentResultadoDiagBinding

class FragmentResultadoDiag : Fragment() {
    private var _binding: FragmentResultadoDiagBinding? = null
    private val b get() = _binding!!

    // Recibimos el objeto Failure
    private val args: FragmentResultadoDiagArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultadoDiagBinding.inflate(inflater, container, false)

        val falla = args.failure

        b.tvFalla.text = "Falla detectada: ${falla.nombreFalla}"
        b.tvDescripcion.text = "Descripción: ${falla.descripcion}"
        b.tvRecomendacion.text = "Recomendación: ${falla.recomendacion}"
        b.imgDiag.setImageResource(R.drawable.ic_diagnosis)

        // Volver
        b.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        // Ir a Comparador (pasa nombre de la falla vía bundle)
        b.btnComparar.setOnClickListener {
            val bundle = bundleOf("failureName" to falla.nombreFalla)
            findNavController().navigate(
                R.id.action_global_nav_comparador,
                bundle
            )
        }

        // Ir a Talleres (pasa id de la falla vía bundle)
        b.btnVerTalleres.setOnClickListener {
            val bundle = bundleOf("failureId" to falla.id)
            findNavController().navigate(
                R.id.action_global_nav_talleres,
                bundle
            )
        }

        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
