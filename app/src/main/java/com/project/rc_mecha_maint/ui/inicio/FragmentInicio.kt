package com.project.rc_mecha_maint.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.rc_mecha_maint.databinding.FragmentInicioBinding
import com.project.rc_mecha_maint.ui.reportes.ReportesViewModel
import java.util.*

class FragmentInicio : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReportesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el ViewModel
        viewModel = ReportesViewModel(requireActivity().application)

        // Cargamos y mostramos el gasto del mes
        cargarGastoMensual()
    }

    private fun cargarGastoMensual() {
        // Calcula el primer instante del mes en curso
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val inicioMes = cal.timeInMillis

        // Calcula el último instante del mes en curso
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val finMes = cal.timeInMillis

        // Observa el LiveData que devuelve el total gastado en facturas
        viewModel.getTotalSpent(inicioMes, finMes).observe(viewLifecycleOwner) { total ->
            val gasto = total ?: 0.0
            // Aquí usamos el TextView que sí existe en el binding:
            binding.tvGastoDetalle.text =
                "Total gastado este mes: $${"%,.2f".format(gasto)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
