package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.alfonsaco.codezen.R;

public class CreateHabitActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolverInicio;
    private Button btnAgregarNuevoHabito;
    private EditText etxtNombreHabito;
    private EditText etxtDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_habit);

        // Botón para volver atrás
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnAgregarNuevoHabito=findViewById(R.id.btnAgregarNuevoHabito);
        etxtNombreHabito=findViewById(R.id.etxtNombreHabito);
        etxtDescripcion=findViewById(R.id.etxtDescripcion);

        btnAgregarNuevoHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarHabito();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void agregarHabito() {
        String nombreHabito=String.valueOf(etxtNombreHabito.getText());
        String descripcion=String.valueOf(etxtDescripcion.getText());

        Habit habito=new Habit(nombreHabito, descripcion, "");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("habito", habito);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}