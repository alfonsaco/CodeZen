package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;

public class CreateHabitActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolverInicio;
    private Button btnAgregarNuevoHabito;
    private EditText etxtNombreHabito;
    private EditText etxtDescripcion;

    // Selección de color
    private String colorSeleccionado;
    private ArrayList<View> listaColores;
    private boolean haSeleccionadoColor=false;

    // FirebaseAuth
    private BDD bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_create_activity);

        btnAgregarNuevoHabito=findViewById(R.id.btnAgregarNuevoHabito);
        etxtNombreHabito=findViewById(R.id.etxtNombreHabito);
        etxtDescripcion=findViewById(R.id.etxtDescripcion);

        // BOTÓN PARA VOLVER ATRÁS
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // SELECCIÓN DE COLOR
        listaColores= new ArrayList<>();
        listaColores.add(findViewById(R.id.color1));
        listaColores.add(findViewById(R.id.color2));
        listaColores.add(findViewById(R.id.color3));
        listaColores.add(findViewById(R.id.color4));
        listaColores.add(findViewById(R.id.color5));
        listaColores.add(findViewById(R.id.color6));
        listaColores.add(findViewById(R.id.color7));
        listaColores.add(findViewById(R.id.color8));
        listaColores.add(findViewById(R.id.color9));
        listaColores.add(findViewById(R.id.color10));
        listaColores.add(findViewById(R.id.color11));
        listaColores.add(findViewById(R.id.color12));
        listaColores.add(findViewById(R.id.color13));
        listaColores.add(findViewById(R.id.color14));
        listaColores.add(findViewById(R.id.color15));
        listaColores.add(findViewById(R.id.color16));

        for(View color : listaColores) {
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    haSeleccionadoColor=true;

                    String tagColor=color.getTag().toString();
                    colorSeleccionado=tagColor;
                }
            });
        }


        // AGREGAR EL HÁBITO NUEVO
        btnAgregarNuevoHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarHabito();
            }
        });


        // AGREGAR HÁBITO A LA BASE DE DATOS
        bd=new BDD();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void agregarHabito() {
        String nombreHabito=String.valueOf(etxtNombreHabito.getText());
        String descripcion=String.valueOf(etxtDescripcion.getText());

        // Verificaciones
        if(nombreHabito.isEmpty() ||descripcion.isEmpty())  {
            Toast.makeText(this, "Rellena los campos de nombre y descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!haSeleccionadoColor) {
            Toast.makeText(this, "Debes seleccionar un color para tu hábito", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos el nuevo hábito que vamos a añadir
        Habit habito=new Habit(nombreHabito, descripcion, colorSeleccionado);


        // Agregar hábito a la Base de Datos
        String id="ID_habit_"+nombreHabito.replace(" ", "_");
        bd.guardarHabitoEnUsuario(id, nombreHabito, descripcion, colorSeleccionado);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("habito", habito);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}