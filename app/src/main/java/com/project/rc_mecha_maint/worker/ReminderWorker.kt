package com.project.rc_mecha_maint.ui.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase        // <–– Import correcto para la BD
import com.project.rc_mecha_maint.data.entity.Reminder
import com.project.rc_mecha_maint.ui.recordatorios.ReminderViewModel  // (opcional: si necesitas el ViewModel, pero no es obligatorio aquí)

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
        const val CHANNEL_ID = "recordatorio_channel"
        const val CHANNEL_NAME = "Recordatorios"
    }

    override suspend fun doWork(): Result {
        // 1) Obtenemos el reminderId desde los inputData
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1L)
        if (reminderId == -1L) {
            // Si no vino un ID válido, fallamos
            return Result.failure()
        }

        // 2) Obtenemos el DAO usando AppDatabase.getDatabase(context)
        val db = AppDatabase.getInstance(context)
        val reminderDao = db.reminderDao()


        // 3) Llamamos al nuevo método getReminderById (suspend)
        val reminder: Reminder? = reminderDao.getReminderById(reminderId)

        if (reminder == null) {
            // Si el recordatorio ya fue borrado antes de que se ejecute el Worker, simplemente devolvemos success
            return Result.success()
        }

        // 4) Mostramos la notificación
        showNotification(reminder)

        // 5) (Opcional) Podrías actualizar o eliminar el recordatorio para marcarlo como “cumplido”. No lo hacemos aquí.

        return Result.success()
    }

    private fun showNotification(reminder: Reminder) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1) Crear canal en Android 8.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 2) Intent para abrir la app al pulsar la notificación (ajusta la Activity si es otra)
        val intent = Intent(context, Class.forName("com.project.rc_mecha_maint.MainActivity"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(), // requestCode único (usa el ID del reminder)
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3) Armar la notificación
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications) // Asegúrate de tener este drawable
            .setContentTitle("Recordatorio: ${reminder.tipo}")
            .setContentText(
                if (reminder.descripcion.isNotEmpty()) reminder.descripcion
                else "Kilómetros: ${reminder.kilometraje}"
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 4) Mostrarla. Usamos el ID del reminder como “notificationId” para que no se solapen.
        notificationManager.notify(reminder.id.toInt(), builder.build())
    }
}
