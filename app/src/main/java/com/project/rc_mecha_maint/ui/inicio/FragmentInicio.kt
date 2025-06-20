package com.project.rc_mecha_maint.ui.inicio

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.UserProfileRepository
import com.project.rc_mecha_maint.databinding.FragmentInicioBinding
import com.project.rc_mecha_maint.ui.mas.perfil.UserProfileViewModel
import com.project.rc_mecha_maint.ui.mas.perfil.UserProfileViewModelFactory
import com.project.rc_mecha_maint.ui.recordatorios.ReminderViewModel
import com.project.rc_mecha_maint.ui.recordatorios.ReminderViewModelFactory
import com.project.rc_mecha_maint.ui.reportes.ReportesViewModel
import com.project.rc_mecha_maint.ui.vehiculos.VehicleViewModel
import com.project.rc_mecha_maint.ui.vehiculos.VehicleViewModelFactory
import java.util.*

class FragmentInicio : Fragment() {

    // ──────────── ViewBinding
    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    // ──────────── ViewModels
    private val userProfileVM: UserProfileViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).userProfileDao()
        UserProfileViewModelFactory(UserProfileRepository(dao))
    }

    private val reminderVM: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireActivity().application)
    }

    private val reportesVM: ReportesViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

    private val vehicleVM: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }

    // ──────────── Ciclo de vida
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 1) Header : logo + “Bienvenido a …” + saludo con nombre */
        val appName = getString(R.string.app_name)
        userProfileVM.user.observe(viewLifecycleOwner) { user ->
            val nombre = user?.nombre ?: "Usuario"
            val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val saludo = when (hora) {
                in 6..11  -> getString(R.string.saludo_manana)
                in 12..17 -> getString(R.string.saludo_tarde)
                else      -> getString(R.string.saludo_noche)
            }
            binding.tvBienvenidoApp.text =
                getString(R.string.bienvenido_app_format, appName)
            binding.tvSaludo.text = "$saludo, $nombre"
        }

        /* 2) Próximo mantenimiento */
        reminderVM.allReminders.observe(viewLifecycleOwner) { lista ->
            val ahora = System.currentTimeMillis()
            val siguiente = lista
                .filter { it.fechaTimestamp >= ahora }
                .minByOrNull { it.fechaTimestamp }
            if (siguiente != null) {
                // Cálculo de cuántos días faltan
                val diff = (siguiente.fechaTimestamp - ahora) / 86_400_000L   // ms a días
                val diasStr = if (diff > 0) "· Faltan $diff d" else ""
                binding.tvProximoDetalle.text =
                    "${siguiente.tipo} $diasStr"
            } else {
                binding.tvProximoDetalle.text = getString(R.string.sin_mantenimientos)
            }
        }

        /* 3) Gastos del mes (historial + facturas) */
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            clear(Calendar.MINUTE)
            clear(Calendar.SECOND)
            clear(Calendar.MILLISECOND)
        }
        val inicioMes = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val finMes = cal.timeInMillis - 1

        reportesVM.getTotalCost(inicioMes, finMes)
            .observe(viewLifecycleOwner) { total ->
                val monto = total ?: 0.0
                binding.tvGastoDetalle.text =
                    getString(R.string.gasto_mes_format, monto)
            }

        /* 4) Vehículo principal (mayor kilometraje) */
        vehicleVM.allVehicles.observe(viewLifecycleOwner) { autos ->
            if (autos.isNullOrEmpty()) return@observe
            val principal = autos.maxByOrNull { it.kilometraje }!!
            binding.tvVehiculoMarcaModelo.text =
                "${principal.marca} ${principal.modelo}"
            binding.tvVehiculoKilometraje.text =
                getString(R.string.kilometraje_format, principal.kilometraje)
            if (!principal.photoUri.isNullOrBlank()) {
                binding.imgVehiculo.setImageURI(Uri.parse(principal.photoUri))
            } else {
                binding.imgVehiculo.setImageResource(R.drawable.ic_default_car)
            }
        }

        /* 5) Navegaciones rápidas */
        binding.cardProximo.setOnClickListener {
            findNavController().navigate(R.id.nav_recordatorios)
        }
        binding.cardGastos.setOnClickListener {
            findNavController().navigate(R.id.nav_reportes)
        }
        binding.cardVehiculo.setOnClickListener {
            findNavController().navigate(R.id.nav_vehiculos)
        }
        binding.btnVerHistorial.setOnClickListener {
            findNavController().navigate(R.id.nav_historial)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
