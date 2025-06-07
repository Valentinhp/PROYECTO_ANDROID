package com.project.rc_mecha_maint.ui.mas.costos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.project.rc_mecha_maint.R
import androidx.navigation.fragment.findNavController
import com.project.rc_mecha_maint.databinding.FragmentCostosBinding
import com.project.rc_mecha_maint.ui.reportes.ReportesViewModel
import java.util.*

class FragmentCostos : Fragment() {

    private var _binding: FragmentCostosBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCostosBinding.inflate(inflater, container, false)
        viewModel = ReportesViewModel(requireActivity().application)

        // Calculamos primeros y últimos milisegundos del mes actual
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val inicioMes = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val finMes = cal.timeInMillis

        // Observamos total y promedio
        viewModel.getTotalCost(inicioMes, finMes).observe(viewLifecycleOwner) { total ->
            binding.tvTotalCostos.text = "Total gastado este mes: $${"%.2f".format(total ?: 0.0)}"
        }
        viewModel.getAverageCost().observe(viewLifecycleOwner) { avg ->
            binding.tvAvgCost.text = "Promedio por servicio: $${"%.2f".format(avg ?: 0.0)}"
        }

        // Botón para ir a reportes
        binding.btnVerReportes.setOnClickListener {
            findNavController().navigate(R.id.action_costos_to_reportes)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
