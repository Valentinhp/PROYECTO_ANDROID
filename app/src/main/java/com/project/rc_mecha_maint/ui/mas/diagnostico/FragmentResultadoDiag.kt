// app/src/main/java/com/project/rc_mecha_maint/ui/mas/diagnostico/FragmentResultadoDiag.kt
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

        b.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        // Enviamos failureId como Int (toInt()) y failureName
        b.btnComparar.setOnClickListener {
            val bundle = bundleOf(
                "failureId"   to falla.id.toInt(),
                "failureName" to falla.nombreFalla
            )
            findNavController().navigate(
                R.id.action_global_fragmentComparador,
                bundle
            )
        }

        // Ver talleres: failureId sigue siendo Long
        b.btnVerTalleres.setOnClickListener {
            val bundle = bundleOf(
                "failureId" to falla.id
            )
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
