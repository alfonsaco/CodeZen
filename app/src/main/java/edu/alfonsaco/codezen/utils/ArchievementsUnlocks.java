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

import java.util.List;

import edu.alfonsaco.codezen.R;

// CLASE DEDICADA AL DESBLOQUEO DE CADA LOGRO
public class ArchievementsUnlocks {
    private BDD db;

    public ArchievementsUnlocks(BDD db) {
        this.db = db;
    }

    // ****************************** MENSAJE LOGRO DESBLOQUEADO ***********************************
    public void mostrarLogroDesbloqueado(Context context, String nombre, String ruta, String id) {
        db.verificarExistenciaLogroBDD(id, existe -> {
            if(!existe) {
                // ********************** MOSTRAR ALERTA LOGRO ***************************
                LayoutInflater inflater = LayoutInflater.from(context);
                View layout = inflater.inflate(R.layout.logro_desbloqueado, null);

                // Poner los datos del logro
                TextView txtNombre=layout.findViewById(R.id.txtNombreLogroDesbloqueado);
                txtNombre.setText(nombre);
                ImageView imagenLogro=layout.findViewById(R.id.imagenLogroDesbloqueado);
                imagenLogro.setImageResource(context.getResources().getIdentifier(ruta, "drawable", context.getPackageName()));

                FrameLayout decorView = (FrameLayout) ((Activity) context).getWindow().getDecorView();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.TOP;

                // Animar entrada y salida
                layout.setTranslationY(-100f);
                layout.setAlpha(0f);
                decorView.addView(layout, params);

                layout.animate().translationY(0f).alpha(1f).setDuration(600).setStartDelay(2000).start();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    layout.animate()
                            .translationY(-300f).alpha(0f).setDuration(500).withEndAction(() ->
                                    decorView.removeView(layout)).start();
                }, 5000);


                // ***************** AGREGAR LOGRO A LA BASE DE DATOS ******************
                db.agregarLogroABDD(id, nombre);

            }
        });
    }
    // *********************************************************************************************



    // ********************************* DESBLOQUEO DE LOGROS **************************************
    // VERIFICAR LOS LOGROS DE HÁBITOS
    public void logrosHabitos(List listaHabitos, Context context) {
        if(listaHabitos.isEmpty()) {
            mostrarLogroDesbloqueado(context, "Primeros pasos", "logro1", "hab_01");
        } else if(listaHabitos.size() == 4) {
            mostrarLogroDesbloqueado(context, "Level Up", "logro7", "hab_02");
        } else if(listaHabitos.size() == 14) {
            mostrarLogroDesbloqueado(context, "GigaChad", "logro8", "hab_03");
        }
    }

    // VERIFICAR LOS LOGROS DE MEDITACIONES
    public void logrosMeditaciones(Context context) {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Long numMeditaciones=snapshot.getLong("cont_meditaciones");

                    if(numMeditaciones == 5) {
                        mostrarLogroDesbloqueado(context, "Reboot", "logro1", "med_01");
                    } else if(numMeditaciones == 30) {
                        mostrarLogroDesbloqueado(context, "Aficionado", "logro1", "med_02");
                    } else if(numMeditaciones == 100) {
                        mostrarLogroDesbloqueado(context, "Zen", "logro1", "med_03");
                    } else if(numMeditaciones == 300) {
                        mostrarLogroDesbloqueado(context, "300", "logro7", "med_04");
                    }

                    Log.d("MEDITACIONES", "NÚMERO DE MEDITACIONES ACTUAL: "+numMeditaciones);
                })
                .addOnFailureListener(e -> {
                    Log.e("EROR MEDITACIONES", "Error al obtener el número de meditaciones");
                });
    }
    // *********************************************************************************************
}
