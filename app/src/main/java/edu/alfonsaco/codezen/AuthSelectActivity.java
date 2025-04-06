package edu.alfonsaco.codezen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.alfonsaco.codezen.auth.LoginActivity;
import edu.alfonsaco.codezen.auth.RegisterActivity;

public class AuthSelectActivity extends AppCompatActivity {

    private Button btnIrRegistro;
    private Button btnIrIniciarSesion;

    // Cambiar modo
    private SharedPreferences temaPreferences;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarTema();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_select);

        // ----CAMBIAR TEMA CLARO OSCURO ----
        temaPreferences = getSharedPreferences("tema", MODE_PRIVATE);
        // ----------------------------------

        // Botones para ir al Registro o a Iniciar Sesión
        btnIrRegistro = findViewById(R.id.btnIrRegistro);
        btnIrIniciarSesion = findViewById(R.id.btnIrIniciarSesion);

        btnIrIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AuthSelectActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AuthSelectActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Firebase
        firebaseAuth=FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para aplicar el tema claro u oscuro
    private void aplicarTema() {
        SharedPreferences tema=getSharedPreferences("tema", MODE_PRIVATE);
        String modoTema=tema.getString("modo_tema", "claro");

        if(modoTema.equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }

    // Comprobamos si ya se ha iniciado sesión con Firebase
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuario=firebaseAuth.getCurrentUser();

        if(usuario != null) {
            Intent intent=new Intent(AuthSelectActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }
}