package com.project.rc_mecha_maint.ui.reportes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.project.rc_mecha_maint.databinding.FragmentReportesBinding
import java.util.*

class FragmentReportes : Fragment() {

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportesViewModel

    // Nuevas variables para saber cuándo ya se cargaron todos los datos
    private var gastosCargados = 0
    private var facturasCargadas = 0
    private val entriesGastos = mutableListOf<Entry>()
    private val entriesFacturas = mutableListOf<BarEntry>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)
        viewModel = ReportesViewModel(requireActivity().application)

        setupCharts()
        return binding.root
    }

    private fun setupCharts() {
        // Obtenemos la fecha actual
        val ahora = Calendar.getInstance()

        // Iteramos los últimos 6 meses
        for (i in 5 downTo 0) {
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }

            // Rango de fechas del mes (inicio y fin)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val inicio = cal.timeInMillis

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val fin = cal.timeInMillis

            // Consulta gastos
            viewModel.getTotalCost(inicio, fin).observe(viewLifecycleOwner) { total ->
                entriesGastos.add(Entry((5 - i).toFloat(), (total ?: 0.0).toFloat()))
                gastosCargados++
                if (gastosCargados == 6) {
                    entriesGastos.sortBy { it.x }
                    drawLineChart(entriesGastos)
                }
            }

            // Consulta facturas
            viewModel.getInvoiceCount(inicio, fin).observe(viewLifecycleOwner) { count ->
                entriesFacturas.add(BarEntry((5 - i).toFloat(), count.toFloat()))
                facturasCargadas++
                if (facturasCargadas == 6) {
                    entriesFacturas.sortBy { it.x }
                    drawBarChart(entriesFacturas)
                }
            }
        }

        // Mostrar el total gastado este mes
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
        val dataSet = LineDataSet(data, "Gastos últimos 6 meses")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        dataSet.valueTextSize = 12f
        binding.lineChartGastos.data = LineData(dataSet)
        binding.lineChartGastos.invalidate()
    }

    private fun drawBarChart(data: List<BarEntry>) {
        val dataSet = BarDataSet(data, "Facturas últimos 6 meses")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextSize = 12f
        binding.barChartFacturas.data = BarData(dataSet)
        binding.barChartFacturas.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
