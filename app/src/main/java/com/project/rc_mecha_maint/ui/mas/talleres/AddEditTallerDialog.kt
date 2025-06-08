package com.project.rc_mecha_maint.ui.mas.talleres

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.rc_mecha_maint.databinding.DialogAddEditTallerBinding
import com.project.rc_mecha_maint.data.entity.Workshop

class AddEditTallerDialog : DialogFragment() {

    private lateinit var b: DialogAddEditTallerBinding
    private val vm by activityViewModels<WorkshopViewModel>()
    private var taller: Workshop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taller = arguments?.getParcelable(ARG_TALLER)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        b = DialogAddEditTallerBinding.inflate(layoutInflater)

        // Modo edición: precarga datos
        taller?.let { t ->
            b.edtNombre.setText(t.nombre)
            b.edtDireccion.setText(t.direccion)
            b.edtTelefono.setText(t.telefono)
            b.edtLat.setText(t.latitud.toString())
            b.edtLng.setText(t.longitud.toString())
            b.edtFoto.setText(t.fotoUrl)
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (taller == null) "Nuevo taller" else "Editar taller")
            .setView(b.root)
            .setPositiveButton("Guardar") { _, _ -> guardar() }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    private fun guardar() {
        val nuevo = Workshop(
            id        = taller?.id ?: 0,
            nombre    = b.edtNombre.text.toString(),
            direccion = b.edtDireccion.text.toString(),
            latitud   = b.edtLat.text.toString().toDoubleOrNull() ?: 0.0,
            longitud  = b.edtLng.text.toString().toDoubleOrNull() ?: 0.0,
            telefono  = b.edtTelefono.text.toString(),
            fotoUrl   = b.edtFoto.text.toString()
        )
        vm.save(nuevo)
    }

    companion object {
        private const val ARG_TALLER = "taller"
        fun nuevaInstancia(taller: Workshop?) =
            AddEditTallerDialog().apply {
                arguments = Bundle().apply { putParcelable(ARG_TALLER, taller) }
            }
    }
}
