package edu.alfonsaco.codezen.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.alfonsaco.codezen.ui.habits.habit_utils.Day;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Habit;

public class BDD {
    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();

    public BDD() {
        firebaseAuth=FirebaseAuth.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }
    public String getUsuarioID() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    // ******************* MÉTODOS DE FIRESTORE PARA EL REGISTRO ******************
    public void guardarUsuarioEnFirebase(String username, String email) {
        Map<String, Object> usuario = new HashMap<>();

        usuario.put("username", username);
        usuario.put("email", email);
        usuario.put("fecha_creacion", new Date());
        usuario.put("cont_habitos", 0);
        usuario.put("cont_meditaciones", 0);
        usuario.put("id", firebaseAuth.getCurrentUser().getUid());

        db.collection("usuarios")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(usuario)
                .addOnSuccessListener(aVoid -> Log.d("Registro (Clase BDD)", "Usuario guardado en Firestore"))
                .addOnFailureListener(e -> Log.e("Registro (Clase BDD)", "Error al guardar el usuario en Firestore"));

    }
    // Obtener los usuarios
    public CollectionReference getUsuariosCollection() {
        return db.collection("usuarios");
    }
    // ****************************************************************************


    // *********************** GUARDAR HÁBITO EN FIRESTORE ************************
    public void guardarHabitoEnUsuario(Habit habito) {
        Map<String, Object> habitoBD=new HashMap<>();

        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        // Verificar que haya un usuario autentificado
        if(usuario == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

        habitoBD.put("id", habito.getId());
        habitoBD.put("nombre", habito.getNombre());
        habitoBD.put("descripcion", habito.getDescripcion());
        habitoBD.put("color", habito.getColor());
        habitoBD.put("recordatorio", habito.getRecordatorio());

        // Añadir el hábito
        db.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .document(habito.getId())
                .set(habitoBD)
                .addOnSuccessListener(aVoid -> Log.d("Añadir hábito (Clase BDD)", "Hábito guardado en Firestore para el usuario "+usuario.getDisplayName()))
                .addOnFailureListener(e -> Log.e("Añadir hábito (Clase BDD)", "Error al guardar el hábito en Firestore para el usuario "+usuario.getDisplayName()));

        // Agregar cada día al hábito
        for(Day dia : habito.getDias()) {
            agregarDiasHabitoAlCrear(dia, habito.getId());
        }

        // Aumentar en 1 el contador de hábitos del usuario
        db.collection("usuarios")
                .document(usuario.getUid())
                .update("cont_habitos", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d("Cont hábitos", "Se aumento el contador de hábitos"))
                .addOnFailureListener(e -> Log.e("Cont hábito", "No se pudo aumentar el contador de hábitos"));
    }

    // Días
    private void agregarDiasHabitoAlCrear(Day dia, String idHabito) {
        Map<String, Object> diaDB=new HashMap<>();

        if(getUsuarioID() == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

        diaDB.put("id", dia.getId());
        diaDB.put("completado", dia.isCompletado());
        diaDB.put("color", dia.getColor());

        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .document(dia.getId())
                .set(diaDB)
                .addOnSuccessListener(a -> {
                    Log.d("ÉXITO", "SE INSERTARON LOS DIAS DE FORMA TOTALMENTE EXITOSA");
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL AGREGAR EL DÍA AL HÁBITO");
                });
    }

    // Borrar un hábito
    public void borrarHabito(String idHabito) {
        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        // Verificar que haya un usuario autentificado
        if(usuario == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

        // Eliminar primero los días del hábito
        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .get()
                .addOnSuccessListener(a -> {
                    for(DocumentSnapshot doc : a.getDocuments()) {
                        doc.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL BORRAR LOS DÍAS");
                });

        // Eliminar el hábito
        db.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .document(idHabito)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Borrar hábito", "Se borró el hábito"))
                .addOnFailureListener(e -> Log.e("Borrar hábito", "No se pudo borrar el hábito"));

        // Reducir el contador de hábitos del usuario
        db.collection("usuarios")
                .document(usuario.getUid())
                .update("cont_habitos", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> Log.d("Cont hábitos", "Se redujo el contador de hábitos"))
                .addOnFailureListener(e -> Log.e("Cont hábito", "No se pudo reducir el contador de hábitos"));
    }

    public void editarHabito(Habit habito) {
        Map<String, Object> habitoBD=new HashMap<>();

        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        // Verificar que haya un usuario autentificado
        if(usuario == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

        habitoBD.put("id", habito.getId());
        habitoBD.put("nombre", habito.getNombre());
        habitoBD.put("descripcion", habito.getDescripcion());
        habitoBD.put("color", habito.getColor());
        habitoBD.put("recordatorio", habito.getRecordatorio());

        // Añadir el hábito
        db.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .document(habito.getId())
                .set(habitoBD, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Añadir hábito (Clase BDD)", "Hábito guardado en Firestore para el usuario "+usuario.getDisplayName()))
                .addOnFailureListener(e -> Log.e("Añadir hábito (Clase BDD)", "Error al guardar el hábito en Firestore para el usuario "+usuario.getDisplayName()));

        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(habito.getId())
                .collection("dias")
                .get()
                .addOnSuccessListener(a -> {
                    for(DocumentSnapshot doc : a.getDocuments()) {
                        doc.getReference().update("color", habito.getColor());
                    }
                });

    }
    // **********************************************************************************************


    // ****************************** OBTENER UN DATO EN ESPECÍFICO ********************************
    public interface HabitCallback {
        void onHabitLoaded(Habit habit);
        void onError(Exception e);
    }

    public void obtenerHabito(String idHabito, HabitCallback callback) {
        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        // Verificar que haya un usuario autentificado
        if(usuario == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            callback.onError(new Exception("Usuario no autenticado"));
            return;
        }

        db.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .document(idHabito)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Habit habit = snapshot.toObject(Habit.class);
                        callback.onHabitLoaded(habit);
                    } else {
                        callback.onError(new Exception("Hábito no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL OBTENER EL HÁBITO");
                    callback.onError(e);
                });
    }
}
