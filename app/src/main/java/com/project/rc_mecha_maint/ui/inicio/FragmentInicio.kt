package com.project.rc_mecha_maint.ui.inicio

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FragmentInicio : Fragment(R.layout.fragment_inicio) {

    private val maintenanceDao by lazy {
        AppDatabase.getInstance(requireContext()).maintenanceDao()
    }
    private val invoiceDao by lazy {
        AppDatabase.getInstance(requireContext()).invoiceDao()
    }

    private lateinit var tvProximoDetalle: TextView
    private lateinit var tvGastoDetalle: TextView
    private lateinit var btnVerHistorial: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1) Vincular vistas
        tvProximoDetalle = view.findViewById(R.id.tvProximoDetalle)
        tvGastoDetalle   = view.findViewById(R.id.tvGastoDetalle)
        btnVerHistorial  = view.findViewById(R.id.btnVerHistorial)

        // 2) Cargar datos desde la base
        lifecycleScope.launch {
            cargarProximoMantenimiento()
            cargarGastoMensual()
        }

        // 3) Navegación usando la action nueva
        btnVerHistorial.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_historial)
        }
    }

    private suspend fun cargarProximoMantenimiento() {
        val now = System.currentTimeMillis()
        val next = maintenanceDao.getNextMaintenance(now)
        if (next != null) {
            val diff = next.fechaTimestamp - now
            val dias = TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
            tvProximoDetalle.text = "${next.tipoServicio} – $dias días restantes"
        } else {
            tvProximoDetalle.text = "Sin mantenimientos pendientes"
        }
    }

    private suspend fun cargarGastoMensual() {
        val (start, end) = mesActualRango()
        val total = invoiceDao.getTotalSpent(start, end) ?: 0.0
        tvGastoDetalle.text = "$ ${"%.2f".format(total)}"
    }

    private fun mesActualRango(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }
}
