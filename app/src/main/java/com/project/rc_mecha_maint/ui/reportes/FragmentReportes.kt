package com.project.rc_mecha_maint.ui.reportes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.github.mikephil.charting.data.*
import com.project.rc_mecha_maint.databinding.FragmentReportesBinding
import java.util.*

class FragmentReportes : Fragment() {

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)
        viewModel = ReportesViewModel(requireActivity().application)

        setupCharts()
        return binding.root
    }

    private fun setupCharts() {
        // Obtenemos fecha actual
        val ahora = Calendar.getInstance()

        val entriesGastos = mutableListOf<Entry>()
        val entriesFacturas = mutableListOf<BarEntry>()

        // Iteramos los últimos 6 meses (0 = mes actual, -1 = mes pasado, ...)
        for (i in 5 downTo 0) {
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }

            // Inicio: día 1 a las 00:00:00.000
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val inicio = cal.timeInMillis

            // Fin: último día a las 23:59:59.999
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val fin = cal.timeInMillis

            // Observamos gasto y factura para este mes
            viewModel.getTotalCost(inicio, fin).observe(viewLifecycleOwner) { total ->
                entriesGastos.add(Entry((5 - i).toFloat(), (total ?: 0.0).toFloat()))
                if (entriesGastos.size == 6) drawLineChart(entriesGastos)
            }
            viewModel.getInvoiceCount(inicio, fin).observe(viewLifecycleOwner) { count ->
                entriesFacturas.add(BarEntry((5 - i).toFloat(), count.toFloat()))
                if (entriesFacturas.size == 6) drawBarChart(entriesFacturas)
            }
        }

        // Total de este mes (i = 0)
        val calMes = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val inicioMes = calMes.timeInMillis
        calMes.set(Calendar.DAY_OF_MONTH, calMes.getActualMaximum(Calendar.DAY_OF_MONTH))
        calMes.set(Calendar.HOUR_OF_DAY, 23)
        calMes.set(Calendar.MINUTE, 59)
        calMes.set(Calendar.SECOND, 59)
        calMes.set(Calendar.MILLISECOND, 999)
        val finMes = calMes.timeInMillis

        viewModel.getTotalCost(inicioMes, finMes).observe(viewLifecycleOwner) { total ->
            binding.tvTotalMes.text = "Total gastado este mes: $${"%.2f".format(total ?: 0.0)}"
        }
    }

    private fun drawLineChart(data: List<Entry>) {
        val ds = LineDataSet(data, "Gastos últimos 6 meses")
        binding.lineChartGastos.data = LineData(ds)
        binding.lineChartGastos.invalidate() // refresca
    }

    private fun drawBarChart(data: List<BarEntry>) {
        val ds = BarDataSet(data, "Facturas últimos 6 meses")
        binding.barChartFacturas.data = BarData(ds)
        binding.barChartFacturas.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
