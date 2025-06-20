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
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Reminder

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        // Debe coincidir con la clave usada al programar la WorkRequest
        const val KEY_REMINDER_ID = "reminderId"
        private const val CHANNEL_ID = "recordatorio_channel"
        private const val CHANNEL_NAME = "Recordatorios"
    }

    override suspend fun doWork(): Result {
        // 1) Obtener el ID del reminder de los inputData
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1L)
        if (reminderId < 0) return Result.failure()

        // 2) Cargar el Reminder desde la base de datos
        val db = AppDatabase.getInstance(applicationContext)
        val reminder = db.reminderDao().getReminderById(reminderId)
            ?: return Result.success() // si ya no existe, salimos exitosos

        // 3) Mostrar la notificación
        showNotification(reminder)

        return Result.success()
    }

    private fun showNotification(reminder: Reminder) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android O+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordatorios de mantenimiento"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la MainActivity al pulsar la notificación
        val intent = Intent(applicationContext, com.project.rc_mecha_maint.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            reminder.id.toInt(),  // requestCode único
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val text = if (reminder.descripcion.isNotBlank()) {
            reminder.descripcion
        } else {
            "Kilómetros: ${reminder.kilometraje}"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)  // verifica que exista este drawable
            .setContentTitle("Recordatorio: ${reminder.tipo}")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar usando el ID del reminder para evitar duplicados
        notificationManager.notify(reminder.id.toInt(), notification)
    }
}
