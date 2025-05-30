package edu.alfonsaco.codezen.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.alfonsaco.codezen.ui.habits.habit_utils.Day;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Habit;
import edu.alfonsaco.codezen.ui.meditation.meditate.Meditation;

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

        // Seleccion de Avatar automatica
        int numAvatar= (int) (1 + Math.random() * 6);
        String[] avatares={"avatar1", "avatar2", "avatar3", "avatar4", "avatar5", "avatar6"};

        usuario.put("username", username);
        usuario.put("email", email);
        usuario.put("fecha_creacion", new Date());
        usuario.put("cont_habitos", 0);
        usuario.put("cont_meditaciones", 0);
        usuario.put("id", firebaseAuth.getCurrentUser().getUid());
        usuario.put("cont_logros", 0);
        usuario.put("nivel", 0);
        usuario.put("avatar", avatares[numAvatar]);

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

        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .document(dia.getId())
                .set(diaDB)
                .addOnFailureListener(e -> {
                    e.printStackTrace();
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


    // ********************************** MÉTODOS DÍAS HÁBITO **************************************
    public interface FechaCompletadaCallback {
        void onResultado(boolean completado);
    }
    public void verificarCompletadoBoolean(String fecha, String idHabito, FechaCompletadaCallback callback) {
        if(getUsuarioID() == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            callback.onResultado(false);
            return;
        }

        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .document(fecha)
                .get()
                .addOnSuccessListener(a -> {
                    if (a.exists()) {
                        Boolean completado = a.getBoolean("completado");
                        callback.onResultado(completado != null && completado);
                    } else {
                        callback.onResultado(false);
                    }
                });
    }
    public void cambiarEstadoDia(Boolean estado, String idHabito, String fecha) {
        if(getUsuarioID() == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .document(fecha)
                .update("completado", estado)
                .addOnSuccessListener(a -> {
                    Log.d("PROCESO TERMINADO", "SE ACTUALIZÓ EL ESTADO");
                });
    }

    // Método para verificar si existe un día en la BDD, por si agregarlo o no
    public interface ExisteDiaCallback {
        void onResultado(boolean existe);
    }
    public void verificarExistenciaDia(String idHabito, String fecha, ExisteDiaCallback callback) {
        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .document(fecha)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists() && documentSnapshot != null) {
                        callback.onResultado(true);
                    } else {
                        callback.onResultado(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL VERIFICAR LA EXISTENCIA DEL DÍA");
                });
    }

    public void anadirDia(Day dia, String idHabito) {
        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .document(dia.getId())
                .set(dia)
                .addOnSuccessListener(a -> {
                    Log.d("ÉXITO", "DÍA AÑADIDO CORRECTAMENTE");
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL AÑADIR DÍA AL HÁBITO");
                });
    }
    // **********************************************************************************************

    // ****************************** OBTENER UN DATO EN ESPECÍFICO ********************************
    public interface HabitCallback {
        void onHabitLoaded(Habit habit);
        void onError(Exception e);
    }
    // *********************************************************************************************

    // ************************************** MEDITACIONES ******************************************
    public void agregarMeditacion(Meditation meditacion) {
        Map<String, Object> meditacionBD=new HashMap<>();

        // Verificar usuario
        if(getUsuarioID() == null) {
            return;
        }

        meditacionBD.put("id", meditacion.getId());
        meditacionBD.put("fecha", meditacion.getFecha());
        meditacionBD.put("duracion", meditacion.getDuracion());

        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("meditaciones")
                .document(meditacion.getId())
                .set(meditacionBD)
                .addOnSuccessListener(aVoid -> Log.d("AÑADIR MEDITACION", "Meditación guardada en Firestore"))
                .addOnFailureListener(e -> Log.e("AÑADIR MEDITACION", "Error al guardar la meditación en Firestore"));


        // Incrementar el contador de meditación
        db.collection("usuarios")
                .document(getUsuarioID())
                .update("cont_meditaciones", FieldValue.increment(1))
                .addOnSuccessListener(a -> {
                    Log.d("MEDITACION", "SE SUMÓ EL CONTADOR DE MEDITACION");
                })
                .addOnFailureListener(e -> {
                    Log.e("MEDITACION", "NO SE PUDO SUMAR EL CONTADOR DE MEDITACION");
                });
    }
    // *********************************************************************************************

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


    // ***************************************** PERFIL ********************************************
    public void agregarLogroABDD(String id, String nombre) {
        Map<String, Object> logroDB=new HashMap<>();

        // Verificamos usuario
        if(getUsuarioID() == null) {
            return;
        }

        LocalDate hoy= LocalDate.now();

        logroDB.put("id", id);
        logroDB.put("nombre", nombre);
        logroDB.put("fecha_desbloqueo", hoy.toString());

        getDb().collection("usuarios")
                .document(getUsuarioID())
                .collection("logros")
                .document(id)
                .set(logroDB)
                .addOnCompleteListener(a -> {
                    Log.d("BIEN", "Logro agregado correctamente");
                })
                .addOnFailureListener(e -> {
                   Log.e("LOGRO", "Error al agregar el logro");
                });
    }

    public void verificarExistenciaLogroBDD(String id, ExisteDiaCallback callback) {
        db.collection("usuarios")
                .document(getUsuarioID())
                .collection("logros")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists() && documentSnapshot != null) {
                        callback.onResultado(true);
                    } else {
                        callback.onResultado(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL VERIFICAR LA EXISTENCIA DEL DÍA");
                });
    }
    // *********************************************************************************************
}