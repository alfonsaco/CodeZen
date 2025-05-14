package edu.alfonsaco.codezen.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.alfonsaco.codezen.R;

// CLASE DEDICADA AL DESBLOQUEO DE CADA LOGRO
public class ArchievementsUnlocks {
    private BDD db;

    public ArchievementsUnlocks(BDD db) {
        this.db = db;
    }

    // ****************************** MENSAJE LOGRO DESBLOQUEADO ***********************************
    private void mostrarLogroDesbloqueado(Context context, String nombre, String ruta) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.logro_desbloqueado, null);

        // Poner los datos del logro
        TextView txtNombre=layout.findViewById(R.id.txtLogroDesbloqueado);
        txtNombre.setText(nombre);
        ImageView imagenLogro=layout.findViewById(R.id.imagenLogroDesbloqueado);
        imagenLogro.setImageResource(context.getResources().getIdentifier(ruta, "drawable", context.getPackageName()));

        FrameLayout decorView = (FrameLayout) ((Activity) context).getWindow().getDecorView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP;

        layout.setAlpha(0f);
        decorView.addView(layout, params);

        // Animar entrada y salida
        layout.animate().alpha(1f).setDuration(500).start(); // Fade in
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            layout.animate().alpha(0f).setDuration(500).withEndAction(() -> {
                decorView.removeView(layout);
            }).start(); // Fade out
        }, 2000);
    }
    // *********************************************************************************************



    // ********************************* DESBLOQUEO DE LOGROS **************************************
    // VERIFICAR LOS LOGROS DE HÁBITOS
    public void logrosHabitos(Context context) {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Long numHabitos=snapshot.getLong("cont_habitos");

                    if(numHabitos == 1) {
                        mostrarLogroDesbloqueado(context,"Primeros pasos", "logro1");

                    } else if(numHabitos == 5) {

                    } else if(numHabitos == 15) {

                    }

                    Log.d("HABITOS", "NÚMERO DE HÁBITOS ACTUAL: "+numHabitos);
                })
                .addOnFailureListener(e -> {
                    Log.e("EROR HABITOS", "Error al obtener el número de hábitos");
                });
    }

    // VERIFICAR LOS LOGROS DE MEDITACIONES
    public void logrosMeditaciones() {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Long numMeditaciones=snapshot.getLong("cont_meditaciones");

                    if(numMeditaciones == 5) {

                    } else if(numMeditaciones == 30) {

                    } else if(numMeditaciones == 100) {

                    } else if(numMeditaciones == 300) {

                    }

                    Log.d("MEDITACIONES", "NÚMERO DE MEDITACIONES ACTUAL: "+numMeditaciones);
                })
                .addOnFailureListener(e -> {
                    Log.e("EROR MEDITACIONES", "Error al obtener el número de meditaciones");
                });
    }
    // *********************************************************************************************
}
