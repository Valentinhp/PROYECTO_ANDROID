package com.project.rc_mecha_maint.ui.mas.historial

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.History
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.FragmentRegistrarMantenimientoBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FragmentRegistrarMantenimiento : Fragment() {

    private var _binding: FragmentRegistrarMantenimientoBinding? = null
    private val binding get() = _binding!!

    private var vehicleId: Long = 0L
    private var workshopList: List<Workshop> = listOf()
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarMantenimientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vehicleId = arguments?.getLong("vehicleId") ?: 0L
        val categoriaPredef = arguments?.getString("categoria")
        val tallerPredef = arguments?.getParcelable<Workshop>("taller")
        val fallaPredef = arguments?.getString("failureName")
        val montoPredef = arguments?.getDouble("costo")

        binding.etFecha.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    calendar.set(y, m, d)
                    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.etFecha.setText(fmt.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Spinner de categorías
        val categorias = listOf("Aceite", "Frenos", "Motor", "Suspensión", "Llantas", "Otro")
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = catAdapter
        categoriaPredef?.let {
            val index = categorias.indexOf(it)
            if (index >= 0) binding.spinnerCategoria.setSelection(index)
        }

        // Spinner de talleres
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(requireContext()).workshopDao()
            workshopList = dao.getAllSync()
            val nombres = workshopList.map { it.nombre }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerTaller.adapter = adapter

            tallerPredef?.let { t ->
                val idx = workshopList.indexOfFirst { it.id == t.id }
                if (idx >= 0) binding.spinnerTaller.setSelection(idx)
            }
        }

        fallaPredef?.let { binding.etFalla.setText(it) }
        montoPredef?.let { binding.etMonto.setText(it.toString()) }

        binding.btnGuardar.setOnClickListener {
            val categoria = binding.spinnerCategoria.selectedItem.toString()
            val monto = binding.etMonto.text.toString().toDoubleOrNull()
            val falla = binding.etFalla.text.toString().trim()
            val idxTaller = binding.spinnerTaller.selectedItemPosition
            val rating = binding.ratingBar.rating.toInt()

            // Validaciones
            if (monto == null || idxTaller < 0 || binding.etFecha.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taller = workshopList[idxTaller]
            // Descripción concatenada
            val desc = buildString {
                append("$categoria | Taller: ${taller.nombre}")
                if (falla.isNotEmpty()) append(" | Falla: $falla")
            }

            // Ahora sí incluimos 'categoria' al crear History
            val registro = History(
                vehicleId     = vehicleId,
                fechaTimestamp= calendar.timeInMillis,
                descripcion   = desc,
                categoria     = categoria,
                costo         = monto,
                calificacion  = if (rating > 0) rating else null
            )

            lifecycleScope.launch {
                val db = AppDatabase.getInstance(requireContext())
                db.historyDao().insert(registro)

                if (rating > 0) {
                    val nuevoTotal = taller.ratingCount + 1
                    val nuevoPromedio = ((taller.rating * taller.ratingCount) + rating) / nuevoTotal
                    val actualizado = taller.copy(
                        rating = nuevoPromedio,
                        ratingCount = nuevoTotal
                    )
                    db.workshopDao().update(actualizado)
                }

                Toast.makeText(requireContext(), "Mantenimiento guardado", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    R.id.action_nav_registrarMantenimiento_to_nav_historial,
                    bundleOf("vehicleId" to vehicleId)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
