package edu.alfonsaco.codezen.utils;

import android.util.Log;
import android.widget.Toast;

// CLASE DEDICADA AL DESBLOQUEO DE CADA LOGRO
public class ArchievementsUnlocks {
    private BDD db;

    public ArchievementsUnlocks(BDD db) {
        this.db = db;
    }

    // VERIFICAR LOS LOGROS DE HÁBITOS
    public void logrosHabitos() {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Long numHabitos=snapshot.getLong("cont_habitos");

                    if(numHabitos == 1) {

                    } else if(numHabitos == 5) {

                    } else if(numHabitos == 15) {

                    }

                    Log.d("HABITOS", "NÚMERO DE HÁBITOS ACTUAL: "+numHabitos);
                })
                .addOnFailureListener(e -> {
                    Log.e("EROR HABITOS", "Error al obtener el número de hábitos");
                });
    }
}
