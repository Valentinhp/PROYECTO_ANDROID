// ui/recordatorios/FragmentNuevoRecordatorio.kt

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
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Fragment que muestra el formulario para crear o editar un Reminder.
 * Se usa View Binding para acceder a las vistas definidas en XML.
 */
class FragmentNuevoRecordatorio : Fragment() {

    // Recibimos el objeto Reminder (si venimos a editar). Puede ser null.
    private val args: FragmentNuevoRecordatorioArgs by navArgs()

    // ViewModel para gestionar inserción/actualización en la base de datos
    private val viewModel: ReminderViewModel by viewModels()

    // View Binding: _binding es nullable y binding no lo es (para evitar errores)
    private var _binding: FragmentNuevoRecordatorioBinding? = null
    private val binding get() = _binding!!

    // Guardaremos aquí la fecha seleccionada en milisegundos
    private var fechaTimestamp: Long = 0L

    // Si estamos editando, esta variable tendrá el ID; si es nuevo, será null
    private var currentReminderId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflamos el layout usando View Binding
        _binding = FragmentNuevoRecordatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Si recibimos un Reminder por argumentos, precargamos la información
        args.reminder?.let { reminder ->
            currentReminderId = reminder.id
            binding.etTipo.setText(reminder.tipo)
            fechaTimestamp = reminder.fechaTimestamp
            // Formateamos la fecha a DD/MM/YYYY
            val cal = Calendar.getInstance().apply { timeInMillis = fechaTimestamp }
            val dia = cal.get(Calendar.DAY_OF_MONTH)
            val mes = cal.get(Calendar.MONTH) + 1
            val año = cal.get(Calendar.YEAR)
            binding.tvFechaSeleccionada.text = "Fecha: $dia/$mes/$año"

            binding.etKilometraje.setText(reminder.kilometraje.toString())
            binding.etDescripcion.setText(reminder.descripcion)
            binding.switchNotificar.isChecked = reminder.notificar
        }

        // 2) Cuando el usuario pulsa "Seleccionar fecha", abrimos DatePickerDialog
        binding.btnSeleccionarFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Al elegir fecha, convertimos a timestamp y lo mostramos en texto
                    calendario.set(year, month, dayOfMonth, 0, 0, 0)
                    fechaTimestamp = calendario.timeInMillis
                    binding.tvFechaSeleccionada.text = "Fecha: $dayOfMonth/${month + 1}/$year"
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 3) Al pulsar "Guardar", validamos campos y guardamos o actualizamos
        binding.btnGuardar.setOnClickListener {
            val tipo = binding.etTipo.text.toString().trim()
            val kmText = binding.etKilometraje.text.toString().trim()
            val descripcion = binding.etDescripcion.text.toString().trim()
            val notificar = binding.switchNotificar.isChecked

            // Validaciones sencillas
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

            // Convertimos el texto de km a número
            val kilometraje = try {
                kmText.toInt()
            } catch (e: NumberFormatException) {
                Snackbar.make(binding.root, "Kilometraje no válido", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto Reminder. Si es edición, conservamos el ID
            val reminder = Reminder(
                id = currentReminderId ?: 0L,
                tipo = tipo,
                fechaTimestamp = fechaTimestamp,
                kilometraje = kilometraje,
                descripcion = descripcion,
                notificar = notificar
            )

            // Insertamos o actualizamos según corresponda
            if (currentReminderId != null) {
                viewModel.updateReminder(reminder)
            } else {
                viewModel.insertReminder(reminder)
            }

            // 4) Programar o cancelar notificación
            if (notificar) {
                scheduleNotification(reminder)
            } else {
                cancelNotification(reminder.id)
            }

            // Volvemos al fragmento de lista de recordatorios
            findNavController().popBackStack()
        }
    }

    /**
     * Programar una notificación con WorkManager.
     * Calcula cuántos milisegundos faltan desde ahora hasta la fecha elegida.
     * Luego crea un OneTimeWorkRequest que se ejecutará tras ese retraso.
     */
    private fun scheduleNotification(reminder: Reminder) {
        val delayMillis = reminder.fechaTimestamp - System.currentTimeMillis()
        if (delayMillis <= 0) {
            // Si la fecha ya pasó o es inmediata, no programamos
            return
        }

        // Construimos los datos que recibirá el worker (ID del reminder)
        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_REMINDER_ID, reminder.id)
            .build()

        // Creamos el OneTimeWorkRequest con el retraso correspondiente
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            // Agregamos una etiqueta única por reminder.id para poder cancelar después
            .addTag("reminder_${reminder.id}")
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    /**
     * Si el usuario desactivó la opción “Notificar” o eliminó el recordatorio,
     * cancelamos cualquier trabajo pendiente asociado a este ID.
     */
    private fun cancelNotification(reminderId: Long) {
        WorkManager.getInstance(requireContext())
            .cancelAllWorkByTag("reminder_$reminderId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Liberar referencia al binding para evitar fugas de memoria
        _binding = null
    }
}
