package edu.alfonsaco.codezen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnVolverDesdeAjustes;

    // Tema claro / oscuro
    private RadioButton rdClaro, rdOscuro;
    public SharedPreferences preferencesTema;
    public String temaGuardado;

    private Button btnCerrarSesion;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    // Componentes
    private TextView txtEmail;
    private TextView txtUsername;

    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Botón para volver a la ventana de Fragments
        btnVolverDesdeAjustes=findViewById(R.id.btnVolverDesdeAjustes);
        btnVolverDesdeAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gestión del tema de la aplicación
        rdClaro=findViewById(R.id.rdClaro);
        rdOscuro=findViewById(R.id.rdOscuro);

        preferencesTema=getSharedPreferences("tema", MODE_PRIVATE);
        temaGuardado=preferencesTema.getString("modo_tema", "claro");

        // Verificar el tema guardado por defecto
        verificarTema(temaGuardado);

        rdClaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPreferencias("claro");
                cambiarTema("claro");
            }
        });

        rdOscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPreferencias("oscuro");
                cambiarTema("oscuro");
            }
        });

        // Cerrar sesión en FireBase
        btnCerrarSesion=findViewById(R.id.btnCerrarSesión);
        firebaseAuth=FirebaseAuth.getInstance();
        // Para que nos deje elegir usuario diferente cada vez que cerremos sesión
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                // Cerrar cliente, para poder seleccionar otra cuenta distinta
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    volverAInicio();
                });
            }
        });


        // Obtener los datos del Usuario
        txtUsername=findViewById(R.id.txtUsername);
        txtEmail=findViewById(R.id.txtEmail);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        email=intent.getStringExtra("email");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ******************* MÉTODOS PARA CAMBIAR EL TEMA CLARO OSCURO **********************
    // Para verificar si el tema guardado es claro u oscuro, y seleccioanr los RadioButtons
    public void verificarTema(String tema) {
        if(tema.equals("claro")) {
            rdClaro.setChecked(true);
        } else {
            rdOscuro.setChecked(true);
        }

        cambiarTema(tema);
    }

    private void guardarPreferencias(String tema) {
        SharedPreferences.Editor editor=preferencesTema.edit();
        editor.putString("modo_tema", tema);
        editor.apply();
    }

    private void cambiarTema(String tema) {
        if(tema.equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    // ************************************************************************************


    // **************************** MÉTODOS PARA EL SIGN OUT ******************************
    private void volverAInicio() {
        Intent intent=new Intent(SettingsActivity.this, AuthSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
    // ************************************************************************************


    @Override
    protected void onStart() {
        super.onStart();

        txtUsername.setText(username);
        txtEmail.setText(email);
    }
}