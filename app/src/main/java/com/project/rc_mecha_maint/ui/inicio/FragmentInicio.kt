package com.project.rc_mecha_maint.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.rc_mecha_maint.R

/**
 * FragmentInicio: pantalla inicial vacía.
 * Se creó en Sprint 0 para que la navegación tuviera un destino.
 */
class FragmentInicio : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout fragment_inicio.xml
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }
}
