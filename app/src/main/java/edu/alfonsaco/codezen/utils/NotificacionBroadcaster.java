package edu.alfonsaco.codezen.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import edu.alfonsaco.codezen.R;

public class NotificacionBroadcaster extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Verificamos permisos
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            String nombre=intent.getStringExtra("nombre");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(R.drawable.icono_vector)
                    .setContentTitle(nombre.toUpperCase())
                    .setContentText("Es hora de "+nombre)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1001, builder.build());

        }
    }
}
