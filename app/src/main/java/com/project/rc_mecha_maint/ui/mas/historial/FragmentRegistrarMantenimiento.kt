package com.project.rc_mecha_maint.ui.mas.historial

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.dao.HistoryDao
import com.project.rc_mecha_maint.data.entity.History
import com.project.rc_mecha_maint.databinding.FragmentRegistrarMantenimientoBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FragmentRegistrarMantenimiento : Fragment() {

    private var _binding: FragmentRegistrarMantenimientoBinding? = null
    private val binding get() = _binding!!

    private var vehicleId: Long = 0L
    private val historyDao: HistoryDao by lazy {
        AppDatabase.getInstance(requireContext()).historyDao()
    }
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

        // 1) Recibir el ID del vehículo
        vehicleId = arguments?.getLong("vehicleId") ?: 0L

        // 2) Selector de fecha
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

        // 3) Botón Guardar
        binding.btnGuardar.setOnClickListener {
            val tipo      = binding.etTipo.text.toString().trim()
            val taller    = binding.etTaller.text.toString().trim()
            val monto     = binding.etMonto.text.toString().toDoubleOrNull()

            if (tipo.isEmpty() || taller.isEmpty() || monto == null) {
                Toast.makeText(requireContext(),
                    "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Combina tipo + taller en la descripción
            val descripcion = "$tipo | Taller: $taller"

            val registro = History(
                vehicleId      = vehicleId,
                fechaTimestamp = calendar.timeInMillis,
                descripcion    = descripcion,
                costo          = monto
            )

            lifecycleScope.launch {
                historyDao.insert(registro)
                Toast.makeText(requireContext(),
                    "Mantenimiento guardado", Toast.LENGTH_SHORT).show()
                // Volver al Historial pasándole el mismo vehicleId
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
