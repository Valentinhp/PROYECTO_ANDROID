package com.project.rc_mecha_maint.ui.mas.talleres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.DialogAddEditTallerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditTallerDialog : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_WORKSHOP = "workshop"
        fun newInstance(w: Workshop?) = AddEditTallerDialog().apply {
            arguments = Bundle().apply { putParcelable(ARG_WORKSHOP, w) }
        }
    }

    private var _b: DialogAddEditTallerBinding? = null
    private val b get() = _b!!
    private var existing: Workshop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        existing = arguments?.getParcelable(ARG_WORKSHOP)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = DialogAddEditTallerBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Si es edición, precarga los campos
        existing?.let { w ->
            b.etName.setText(w.nombre)
            b.etAddress.setText(w.direccion)
            b.etLat.setText(w.latitud.toString())
            b.etLng.setText(w.longitud.toString())
            b.etPhone.setText(w.telefono)
            b.etPhotoUrl.setText(w.fotoUrl)
        }

        // Cancelar
        b.btnCancel.setOnClickListener { dismiss() }

        // Guardar
        b.btnSave.setOnClickListener {
            val name  = b.etName.text.toString().trim()
            val addr  = b.etAddress.text.toString().trim()
            val lat   = b.etLat.text.toString().toDoubleOrNull()
            val lng   = b.etLng.text.toString().toDoubleOrNull()
            val phone = b.etPhone.text.toString().trim()
            val url   = b.etPhotoUrl.text.toString().trim()

            if (name.isEmpty() || addr.isEmpty() || lat==null || lng==null || phone.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.msg_completa_campos),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val w = Workshop(
                id       = existing?.id ?: 0L,
                nombre   = name,
                direccion= addr,
                latitud  = lat,
                longitud = lng,
                telefono = phone,
                fotoUrl  = url
            )

            // Inserta en Room en background
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).workshopDao().insert(w)
                withContext(Dispatchers.Main) { dismiss() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
