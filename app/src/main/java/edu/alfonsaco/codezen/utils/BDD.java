package edu.alfonsaco.codezen.utils;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BDD {
    private FirebaseAuth firebaseAuth;

    private BDD() {
        firebaseAuth=FirebaseAuth.getInstance();
    }

    // ******************* MÉTODOS DE FIRESTORE PARA EL REGISTRO ******************
    public void guardarUsuarioEnFirebase(String username, String email) {
        Map<String, Object> usuario = new HashMap<>();

        usuario.put("username", username);
        usuario.put("email", email);
        usuario.put("fecha_creacion", new Date());
        usuario.put("id", firebaseAuth.getCurrentUser().getUid());

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(usuario)
                .addOnSuccessListener(aVoid -> Log.d("Registro (Clase BDD)", "Usuario guardado en Firestore"))
                .addOnFailureListener(e -> Log.e("Registro (Clase BDD)", "Error al guardar el usuario en Firestore"));

        
    }
}
