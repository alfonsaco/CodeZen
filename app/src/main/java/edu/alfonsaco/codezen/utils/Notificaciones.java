package edu.alfonsaco.codezen.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Calendar;

public class Notificaciones {

    private Context context;

    public Notificaciones(Context context) {
        this.context = context;
    }

    public void programarRecordatorio(String hora, String nombre) {
        crearCanalDeNotificacion();

        // Verificar si la app tiene permiso para alarmas exactas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Si no tiene el permiso, mostrar un mensaje y redirigir a los ajustes
                Toast.makeText(context, "Necesitas permitir alarmas exactas en los ajustes", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                return;
            }
        }

        String[] partes = hora.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);

        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.HOUR_OF_DAY, horas);
        calendario.set(Calendar.MINUTE, minutos);
        calendario.set(Calendar.SECOND, 0);

        if (calendario.before(Calendar.getInstance())) {
            // Si la hora ya pasó, ponlo para el día siguiente
            calendario.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Pasamos el dato para la notificación
        Intent intent = new Intent(context, NotificacionBroadcaster.class);
        intent.putExtra("nombre", nombre);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);
        }
    }

    private void crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal Recordatorios";
            String description = "Canal para las notificaciones de hábitos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
