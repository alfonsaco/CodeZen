package edu.alfonsaco.codezen.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.Verifications;

public class PasswordRecoverActivity extends AppCompatActivity {

    // Componentes
    private EditText etxtEmailRecuperarContra;
    private Button btnEmailRecuperarContra;
    private ImageView btnVolverInicio;

    private FirebaseAuth firebaseAuth;
    private Verifications verifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_password_recover);

        verifications=new Verifications();
        firebaseAuth=FirebaseAuth.getInstance();

        // Componentes
        etxtEmailRecuperarContra=findViewById(R.id.etxtEmailRecuperarContra);
        btnEmailRecuperarContra=findViewById(R.id.btnEmailRecuperarContra);

        btnEmailRecuperarContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarEmail();
            }
        });

        // Cerrar actividad
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void enviarEmail() {
        String email=String.valueOf(etxtEmailRecuperarContra.getText());

        // Verificaciones
        if(email.isEmpty()) {
            Toast.makeText(this, "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!verifications.esEmail(email)) {
            Toast.makeText(this, "El email introducido no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar contraseña nueva
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(this, "Se ha enviado un email para recuperar la contraseña", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "No se ha podido enviar el email", Toast.LENGTH_SHORT).show();

                if(task.getResult() != null) {
                    if(task.getException().getMessage().contains("user-not-found")) {
                        Toast.makeText(this, "No existe ningún usuario con ese email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}