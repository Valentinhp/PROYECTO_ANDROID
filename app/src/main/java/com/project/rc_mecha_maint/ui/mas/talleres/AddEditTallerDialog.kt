package com.project.rc_mecha_maint.ui.mas.talleres

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.DialogAddEditTallerBinding

class AddEditTallerDialog : DialogFragment() {

    private var _binding: DialogAddEditTallerBinding? = null
    private val b get() = _binding!!

    private val args by navArgs<AddEditTallerDialogArgs>()
    private val vm by viewModels<WorkshopViewModel> {
        WorkshopViewModelFactory(requireActivity().application)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditTallerBinding.inflate(LayoutInflater.from(context))

        // Si viene workshop, precargamos campos
        val existing = args.workshop
        existing?.let {
            b.etName    .setText(it.nombre)
            b.etAddress .setText(it.direccion)
            b.etLat     .setText(it.latitud.toString())
            b.etLng     .setText(it.longitud.toString())
            b.etPhone   .setText(it.telefono)
            b.etPhotoUrl.setText(it.fotoUrl)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Agregar Taller" else "Editar Taller")
            .setView(b.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre    = b.etName   .text.toString()
                val direccion = b.etAddress.text.toString()
                val lat       = b.etLat    .text.toString().toDoubleOrNull() ?: 0.0
                val lng       = b.etLng    .text.toString().toDoubleOrNull() ?: 0.0
                val telefono  = b.etPhone  .text.toString()
                val fotoUrl   = b.etPhotoUrl.text.toString()

                val w = Workshop(
                    id = existing?.id ?: 0L,
                    nombre    = nombre,
                    direccion = direccion,
                    latitud   = lat,
                    longitud  = lng,
                    telefono  = telefono,
                    fotoUrl   = fotoUrl
                )
                vm.saveWorkshop(w)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
