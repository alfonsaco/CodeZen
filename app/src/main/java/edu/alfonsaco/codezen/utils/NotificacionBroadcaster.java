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

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("TEXTO TITULO")
                    .setContentText("TEXTO TEXTOSO")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1001, builder.build());

        }
    }
}
