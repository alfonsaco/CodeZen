package edu.alfonsaco.codezen.ui.habits;

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

public class EditHabitActivity extends AppCompatActivity {

    private ImageView btnVolverInicio;
    private EditText etxtNombreHabito;
    private EditText etxtDescripcion;
    private Button btnGestionarRecordatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_edit_activity);

        // BOTÓN VOLVER ATRÁS
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ******************** OBTENER DATOS DEL INTENT ********************
        etxtNombreHabito=findViewById(R.id.etxtNombreHabito);
        etxtDescripcion=findViewById(R.id.etxtDescripcion);
        btnGestionarRecordatorio=findViewById(R.id.btnGestionarRecordatorio);

        Intent intent=getIntent();
        String nombre=intent.getStringExtra("nombre");
        String descripcionn=intent.getStringExtra("descripcion");
        String recordatorio=intent.getStringExtra("recordatorio");

        etxtNombreHabito.setText(nombre);
        etxtDescripcion.setText(descripcionn);

        if(recordatorio.equals("")) {
            btnGestionarRecordatorio.setText("00:00");
        } else {
            btnGestionarRecordatorio.setText(recordatorio);
        }
        // *******************************************************************

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}