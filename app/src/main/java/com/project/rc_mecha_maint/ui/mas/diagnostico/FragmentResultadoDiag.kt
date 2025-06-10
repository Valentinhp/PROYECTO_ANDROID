package com.project.rc_mecha_maint.ui.mas.diagnostico

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.databinding.FragmentResultadoDiagBinding

/**
 * Fragment para mostrar el resultado del diagnóstico.
 */
class FragmentResultadoDiag : Fragment() {
    private var _binding: FragmentResultadoDiagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentResultadoDiagBinding.inflate(inflater, container, false).also {
        _binding = it

        // Obtenemos el objeto Failure pasado como argumento
        val falla = FragmentResultadoDiagArgs.fromBundle(requireArguments()).failure

        // Asignamos los textos en los TextViews
        it.tvFalla.text = "Falla detectada: ${falla.nombreFalla}"
        it.tvDescripcion.text = "Descripción: ${falla.descripcion}"
        it.tvRecomendacion.text = "Recomendación: ${falla.recomendacion}"

        // Asignamos una imagen decorativa (icono de diagnóstico)
        it.imgDiag.setImageResource(R.drawable.ic_diagnosis)

        // Botón para volver al fragmento anterior
        it.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }
    }.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
