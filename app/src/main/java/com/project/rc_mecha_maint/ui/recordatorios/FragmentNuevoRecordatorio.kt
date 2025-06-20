package com.project.rc_mecha_maint.ui.recordatorios

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FragmentNuevoRecordatorio : Fragment() {

    private val args: FragmentNuevoRecordatorioArgs by navArgs()
    private val viewModel: ReminderViewModel by viewModels()

    private var _binding: FragmentNuevoRecordatorioBinding? = null
    private val binding get() = _binding!!

    // Un único Calendar para fecha + hora
    private val calendar = Calendar.getInstance()

    // ID si venimos a editar
    private var currentReminderId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevoRecordatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si es edición, precargamos
        args.reminder?.let { rem ->
            currentReminderId = rem.id
            binding.etTipo.setText(rem.tipo)
            binding.etKilometraje.setText(rem.kilometraje.toString())
            binding.etDescripcion.setText(rem.descripcion)
            binding.switchNotificar.isChecked = rem.notificar

            calendar.timeInMillis = rem.fechaTimestamp

            binding.tvFechaSeleccionada.text = String.format(
                "Fecha: %02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
            binding.tvHoraSeleccionada.text = String.format(
                "Hora: %02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }

        binding.btnSeleccionarFecha.setOnClickListener {
            val hoy = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.tvFechaSeleccionada.text = String.format(
                        "Fecha: %02d/%02d/%04d",
                        day, month + 1, year
                    )
                },
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSeleccionarHora.setOnClickListener {
            val ahora = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    binding.tvHoraSeleccionada.text = String.format(
                        "Hora: %02d:%02d",
                        hour, minute
                    )
                },
                ahora.get(Calendar.HOUR_OF_DAY),
                ahora.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnGuardar.setOnClickListener {
            guardarRecordatorio()
        }
    }

    private fun guardarRecordatorio() {
        val tipo = binding.etTipo.text.toString().trim()
        val kmText = binding.etKilometraje.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val notificar = binding.switchNotificar.isChecked

        // Validaciones
        if (tipo.isEmpty()) {
            Snackbar.make(binding.root, "Ingresa el tipo de mantenimiento", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (binding.tvFechaSeleccionada.text.endsWith("----")) {
            Snackbar.make(binding.root, "Selecciona una fecha", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (binding.tvHoraSeleccionada.text.endsWith("--")) {
            Snackbar.make(binding.root, "Selecciona una hora", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (kmText.isEmpty()) {
            Snackbar.make(binding.root, "Ingresa el kilometraje", Snackbar.LENGTH_SHORT).show()
            return
        }
        val kilometraje = kmText.toIntOrNull()
        if (kilometraje == null) {
            Snackbar.make(binding.root, "Kilometraje no válido", Snackbar.LENGTH_SHORT).show()
            return
        }

        val timestamp = calendar.timeInMillis
        if (timestamp <= System.currentTimeMillis()) {
            Snackbar.make(binding.root, "Selecciona fecha y hora futuras", Snackbar.LENGTH_SHORT).show()
            return
        }

        val reminder = Reminder(
            id             = currentReminderId ?: 0L,
            tipo           = tipo,
            fechaTimestamp = timestamp,
            kilometraje    = kilometraje,
            descripcion    = descripcion,
            notificar      = notificar
        )

        if (currentReminderId != null) {
            // Edición
            viewModel.updateReminder(reminder)
            if (notificar) scheduleNotification(reminder)
            else cancelNotification(reminder.id)
            findNavController().popBackStack()
        } else {
            // Inserción nueva con callback
            viewModel.insertReminder(reminder) { newId ->
                reminder.id = newId
                // La notificación ya se programó dentro de insertReminder()
                findNavController().popBackStack()
            }
        }
    }

    private fun scheduleNotification(reminder: Reminder) {
        val delay = reminder.fechaTimestamp - System.currentTimeMillis()
        if (delay <= 0) return

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

    private fun cancelNotification(reminderId: Long) {
        WorkManager.getInstance(requireContext())
            .cancelAllWorkByTag("reminder_$reminderId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
