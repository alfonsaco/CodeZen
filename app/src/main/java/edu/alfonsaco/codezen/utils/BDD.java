package edu.alfonsaco.codezen.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BDD {
    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();

    public BDD() {
        firebaseAuth=FirebaseAuth.getInstance();
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


    // ********************* GUARDAR HÁBITO EN FIRESTORE **********************
    public void guardarHabitoEnUsuario(String idHabito, String nombreHabito, String descripcionHabito, String colorHabito) {
        Map<String, Object> habito=new HashMap<>();

        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        // Verificar que haya un usuario autentificado
        if(usuario == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

        habito.put("id_habito", idHabito);
        habito.put("nombre", nombreHabito);
        habito.put("descripcion", descripcionHabito);
        habito.put("color", colorHabito);

        // Añadir el hábito
        db.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .document(idHabito)
                .set(habito)
                .addOnSuccessListener(aVoid -> Log.d("Añadir hábito (Clase BDD)", "Hábito guardado en Firestore para el usuario "+usuario.getDisplayName()))
                .addOnFailureListener(e -> Log.e("Añadir hábito (Clase BDD)", "Error al guardar el hábito en Firestore para el usuario "+usuario.getDisplayName()));

        // Aumentar en 1 el contador de hábitos del usuario
        db.collection("usuarios")
                .document(usuario.getUid())
                .update("cont_habitos", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d("Cont hábitos", "Se aumento el contador de hábitos"))
                .addOnFailureListener(e -> Log.e("Cont hábito", "No se pudo aumentar el contador de hábitos"));
    }

    // Borrar un hábito
    public void borrarHabito(String idHabito) {
        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        // Verificar que haya un usuario autentificado
        if(usuario == null) {
            Log.e("Usuario no autentificado", "El usuario no está autentificado. No se puede agregar el hábito a la BDD");
            return;
        }

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
}
