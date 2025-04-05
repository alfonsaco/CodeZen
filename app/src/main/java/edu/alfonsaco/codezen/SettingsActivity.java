package edu.alfonsaco.codezen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnVolverDesdeAjustes;

    private RadioButton rdClaro, rdOscuro;
    private SharedPreferences preferencesTema;

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
        String temaGuardado=preferencesTema.getString("modo_tema", "claro");
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ******************* MÉTODOS PARA CAMBIAR EL TEMA CLARO OSCURO **********************
    // Para verificar si el tema guardado es claro u oscuro, y seleccioanr los RadioButtons
    private void verificarTema(String tema) {
        if(tema.equals("claro")) {
            rdClaro.setChecked(true);
        } else {
            rdOscuro.setChecked(true);
        }
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
}