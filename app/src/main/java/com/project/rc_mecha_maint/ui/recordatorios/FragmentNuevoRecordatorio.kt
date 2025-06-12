package com.project.rc_mecha_maint.ui.recordatorios

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.project.rc_mecha_maint.data.entity.Reminder
import com.project.rc_mecha_maint.databinding.FragmentNuevoRecordatorioBinding
import com.project.rc_mecha_maint.ui.worker.ReminderWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.*

/**
 * Fragment que muestra el formulario para crear o editar un Reminder.
 * Usa View Binding para manejar las vistas sin findViewById.
 */
class FragmentNuevoRecordatorio : Fragment() {

    // Recibimos el Reminder (si es edición); si es null, es nuevo.
    private val args: FragmentNuevoRecordatorioArgs by navArgs()

    // ViewModel para operaciones de BD y notificaciones
    private val viewModel: ReminderViewModel by viewModels()

    private var _binding: FragmentNuevoRecordatorioBinding? = null
    private val binding get() = _binding!!

    // Timestamp de la fecha elegida
    private var fechaTimestamp: Long = 0L

    // ID del Reminder actual (null si es nuevo)
    private var currentReminderId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevoRecordatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si venimos a editar, precargamos los valores
        args.reminder?.let { rem ->
            currentReminderId = rem.id
            binding.etTipo.setText(rem.tipo)
            fechaTimestamp = rem.fechaTimestamp
            // Mostrar fecha en pantalla
            Calendar.getInstance().apply {
                timeInMillis = fechaTimestamp
                binding.tvFechaSeleccionada.text =
                    "Fecha: ${get(Calendar.DAY_OF_MONTH)}/${get(Calendar.MONTH) + 1}/${get(Calendar.YEAR)}"
            }
            binding.etKilometraje.setText(rem.kilometraje.toString())
            binding.etDescripcion.setText(rem.descripcion)
            binding.switchNotificar.isChecked = rem.notificar
        }

        // Abrir selector de fecha
        binding.btnSeleccionarFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    cal.set(year, month, day, 0, 0, 0)
                    fechaTimestamp = cal.timeInMillis
                    binding.tvFechaSeleccionada.text =
                        "Fecha: $day/${month + 1}/$year"
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Guardar o actualizar Reminder
        binding.btnGuardar.setOnClickListener {
            val tipo      = binding.etTipo.text.toString().trim()
            val kmText    = binding.etKilometraje.text.toString().trim()
            val descripcion = binding.etDescripcion.text.toString().trim()
            val notificar = binding.switchNotificar.isChecked

            // Validaciones
            if (tipo.isEmpty()) {
                Snackbar.make(binding.root, "Ingresa el tipo de mantenimiento", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fechaTimestamp == 0L) {
                Snackbar.make(binding.root, "Selecciona una fecha", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (kmText.isEmpty()) {
                Snackbar.make(binding.root, "Ingresa el kilometraje", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kilometraje = kmText.toIntOrNull()
            if (kilometraje == null) {
                Snackbar.make(binding.root, "Kilometraje no válido", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Construir objeto Reminder
            val reminder = Reminder(
                id               = currentReminderId ?: 0L,
                tipo             = tipo,
                fechaTimestamp   = fechaTimestamp,
                kilometraje      = kilometraje,
                descripcion      = descripcion,
                notificar        = notificar
            )

            // Insertar o actualizar en BD
            if (currentReminderId != null) {
                viewModel.updateReminder(reminder)
            } else {
                viewModel.insertReminder(reminder)
            }

            // Programar o cancelar notificación
            if (notificar) {
                scheduleNotification(reminder)
            } else {
                cancelNotification(reminder.id)
            }

            // Volver atrás
            findNavController().popBackStack()
        }
    }

    /**
     * Programa una notificación usando WorkManager.
     * Se etiqueta con "reminder_<id>" para poder cancelarla luego.
     */
    private fun scheduleNotification(reminder: Reminder) {
        val delay = reminder.fechaTimestamp - System.currentTimeMillis()
        if (delay <= 0) return  // si ya pasó, no programamos

        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_REMINDER_ID, reminder.id)
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_${reminder.id}")
            .build()

        WorkManager.getInstance(requireContext())
            .enqueue(work)
    }

    /**
     * Cancela cualquier notificación pendiente para este reminder.
     */
    private fun cancelNotification(reminderId: Long) {
        WorkManager.getInstance(requireContext())
            .cancelAllWorkByTag("reminder_$reminderId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
