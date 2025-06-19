package com.project.rc_mecha_maint.ui.reportes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.observe
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.databinding.FragmentReportesBinding
import java.text.SimpleDateFormat
import java.util.*

class FragmentReportes : Fragment() {

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportesViewModel

    private var historyLoaded = 0
    private var invoiceCountLoaded = 0

    private val entriesHistorial = mutableListOf<Entry>()
    private val entriesInvoiceCount = mutableListOf<BarEntry>()
    private val monthLabels = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)
        viewModel = ReportesViewModel(requireActivity().application)
        setupCharts()
        return binding.root
    }

    private fun setupCharts() {
        // 1) Limpiar listas y contadores
        monthLabels.clear()
        entriesHistorial.clear()
        entriesInvoiceCount.clear()
        historyLoaded = 0
        invoiceCountLoaded = 0

        val sdf = SimpleDateFormat("MMM", Locale.getDefault())
        val now = Calendar.getInstance()

        // 2) Últimos 3 meses (i = 2 → hace dos meses, …, i = 0 → mes actual)
        for (i in 2 downTo 0) {
            // a) calcular inicio del mes
            val calStart = now.clone() as Calendar
            calStart.add(Calendar.MONTH, -i)
            calStart.set(Calendar.DAY_OF_MONTH, 1)
            calStart.set(Calendar.HOUR_OF_DAY, 0); calStart.set(Calendar.MINUTE, 0)
            calStart.set(Calendar.SECOND, 0); calStart.set(Calendar.MILLISECOND, 0)
            val inicio = calStart.timeInMillis

            // b) calcular fin del mes
            val calEnd = calStart.clone() as Calendar
            calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
            calEnd.set(Calendar.HOUR_OF_DAY, 23); calEnd.set(Calendar.MINUTE, 59)
            calEnd.set(Calendar.SECOND, 59); calEnd.set(Calendar.MILLISECOND, 999)
            val fin = calEnd.timeInMillis

            // c) etiqueta para el eje X
            monthLabels.add(sdf.format(calStart.time))

            // d) combinar historial + facturas con un MediatorLiveData local
            val histLive = viewModel.getHistoryCost(inicio, fin)
            val invLive = viewModel.getInvoiceCost(inicio, fin)
            val combined = MediatorLiveData<Double>()
            var hVal: Double? = null
            var iVal: Double? = null

            combined.addSource(histLive) { h ->
                hVal = h ?: 0.0
                if (iVal != null) combined.value = hVal!! + iVal!!
            }
            combined.addSource(invLive) { inv ->
                iVal = inv ?: 0.0
                if (hVal != null) combined.value = hVal!! + iVal!!
            }

            // e) cuando lleguen ambos, añade al gráfico de líneas
            combined.observe(viewLifecycleOwner) { total ->
                entriesHistorial.add(Entry((2 - i).toFloat(), total.toFloat()))
                if (++historyLoaded == 3) drawLineChart()
            }

            // f) mientras tanto, cuenta facturas normalmente
            viewModel.getInvoiceCount(inicio, fin).observe(viewLifecycleOwner) { count ->
                entriesInvoiceCount.add(BarEntry((2 - i).toFloat(), count.toFloat()))
                if (++invoiceCountLoaded == 3) drawBarChart()
            }
        }

        // 3) Mes actual: total y pie
        val startMonth = now.clone() as Calendar
        startMonth.set(Calendar.DAY_OF_MONTH, 1)
        startMonth.set(Calendar.HOUR_OF_DAY, 0); startMonth.set(Calendar.MINUTE, 0)
        startMonth.set(Calendar.SECOND, 0); startMonth.set(Calendar.MILLISECOND, 0)
        val endMonth = now.clone() as Calendar
        endMonth.set(Calendar.DAY_OF_MONTH, endMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
        endMonth.set(Calendar.HOUR_OF_DAY, 23); endMonth.set(Calendar.MINUTE, 59)
        endMonth.set(Calendar.SECOND, 59); endMonth.set(Calendar.MILLISECOND, 999)

        // Total gastado este mes (historial + facturas)
        viewModel.getTotalCost(startMonth.timeInMillis, endMonth.timeInMillis)
            .observe(viewLifecycleOwner) { total ->
                binding.tvTotalMes.text =
                    "Total gastado este mes: $${"%,.2f".format(total ?: 0.0)}"
            }

        // PieChart de categorías combinado
        viewModel.getCombinedByCategory(startMonth.timeInMillis, endMonth.timeInMillis)
            .observe(viewLifecycleOwner) { list ->
                val pieEntries = list.map { PieEntry(it.total.toFloat(), it.categoria) }
                drawPieChart(pieEntries)
            }
    }

    private fun drawLineChart() {
        entriesHistorial.sortBy { it.x }
        val set = LineDataSet(entriesHistorial, "Gastos últimos 3 meses").apply {
            setColors(*ColorTemplate.JOYFUL_COLORS)
            valueTextSize = 11f
            lineWidth = 2.5f
            circleRadius = 5f
        }
        binding.lineChartGastos.apply {
            data = LineData(set)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(monthLabels)
                labelRotationAngle = -45f
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(v: Float) = "$${"%,.2f".format(v)}"
            }
            marker = CustomMarkerView(context, R.layout.marker_view)
            description.isEnabled = false
            legend.isEnabled = true
            animateY(800)
            invalidate()
        }
    }

    private fun drawBarChart() {
        entriesInvoiceCount.sortBy { it.x }
        val set = BarDataSet(entriesInvoiceCount, "Facturas últimos 3 meses").apply {
            setColors(*ColorTemplate.COLORFUL_COLORS)
            valueTextSize = 11f
        }
        binding.barChartFacturas.apply {
            data = BarData(set)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(monthLabels)
                labelRotationAngle = -45f
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            marker = CustomMarkerView(context, R.layout.marker_view)
            description.isEnabled = false
            legend.isEnabled = true
            animateY(800)
            invalidate()
        }
    }

    private fun drawPieChart(entries: List<PieEntry>) {
        val set = PieDataSet(entries, "").apply {
            sliceSpace = 3f
            selectionShift = 5f
            valueTextSize = 12f
            setColors(*ColorTemplate.MATERIAL_COLORS)
        }
        binding.pieChartCategorias.apply {
            data = PieData(set).apply {
                setValueFormatter(PercentFormatter(binding.pieChartCategorias))
                setValueTextSize(12f)
                setValueTextColor(Color.WHITE)
            }
            setUsePercentValues(true)
            isDrawHoleEnabled = true
            holeRadius = 45f
            transparentCircleRadius = 50f
            setCenterText("Categorías")
            setCenterTextSize(16f)
            description.isEnabled = false
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            }
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
