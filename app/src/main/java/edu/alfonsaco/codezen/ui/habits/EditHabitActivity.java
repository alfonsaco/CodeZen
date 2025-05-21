package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Habit;
import edu.alfonsaco.codezen.utils.BDD;

public class EditHabitActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolverInicio;
    private EditText etxtNombreHabito;
    private EditText etxtDescripcion;
    private Button btnGestionarRecordatorio;
    private MaterialSwitch switchRecordatorios;
    private Button btnEditarHabito;

    // Selección de color
    private String colorSeleccionado;
    private ArrayList<View> listaColores;

    // Recordatorio
    private String nuevaHoraRecordatorio="";
    private int horas;
    private int minutos;

    private BDD db;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_edit_activity);

        db=new BDD();

        // BOTÓN VOLVER ATRÁS
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
                    String tagColor=color.getTag().toString();
                    colorSeleccionado=tagColor;

                    for (View vColor : listaColores) {
                        View contenedor = (FrameLayout) vColor.getParent();
                        contenedor.setBackgroundColor(0);
                    }

                    FrameLayout contenedor=(FrameLayout) color.getParent();
                    contenedor.setBackgroundColor(Color.BLACK);
                }
            });
        }

        // *********************** OBTENER DATOS DEL INTENT ***********************
        etxtNombreHabito=findViewById(R.id.etxtNombreHabito);
        etxtDescripcion=findViewById(R.id.etxtDescripcion);
        btnGestionarRecordatorio=findViewById(R.id.btnGestionarRecordatorio);
        switchRecordatorios=findViewById(R.id.switchAlarma);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        String nombre=intent.getStringExtra("nombre");
        String descripcionn=intent.getStringExtra("descripcion");
        String recordatorio=intent.getStringExtra("recordatorio");
        colorSeleccionado=intent.getStringExtra("color");

        for(View color : listaColores) {
            if(color.getTag().toString().equals(colorSeleccionado)) {
                FrameLayout contenedor=(FrameLayout) color.getParent();
                contenedor.setBackgroundColor(Color.BLACK);
            }
        }

        etxtNombreHabito.setText(nombre);
        etxtDescripcion.setText(descripcionn);

        if(recordatorio.equals("")) {
            btnGestionarRecordatorio.setText("00:00");
            btnGestionarRecordatorio.setEnabled(false);
            switchRecordatorios.setChecked(false);
        } else {
            btnGestionarRecordatorio.setText(recordatorio);
            btnGestionarRecordatorio.setEnabled(true);
            switchRecordatorios.setChecked(true);
        }
        // *************************************************************************


        // *************************** RECORDATORIOS ********************************
        switchRecordatorios.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchRecordatorios.isChecked()) {
                    btnGestionarRecordatorio.setEnabled(true);
                    nuevaHoraRecordatorio=btnGestionarRecordatorio.getText().toString();
                } else {
                    btnGestionarRecordatorio.setEnabled(false);
                    nuevaHoraRecordatorio="";
                }
            }
        });

        String horaActual=btnGestionarRecordatorio.getText().toString();
        horas=Integer.parseInt(horaActual.split(":")[0]);
        minutos=Integer.parseInt(horaActual.split(":")[1]);

        btnGestionarRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker timePicker=new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(horas)
                        .setMinute(minutos)
                        .setTitleText("Selecciona la hora")
                        .build();

                timePicker.show(getSupportFragmentManager(), "time_picker");

                timePicker.addOnPositiveButtonClickListener(v1 -> {
                    // Formateamos la hora, para ponerla en el botón
                    String nuevaHora=String.valueOf(timePicker.getHour());
                    String nuevoMinuto=String.valueOf(timePicker.getMinute());

                    if(nuevaHora.length() == 1) {
                        nuevaHora="0"+nuevaHora;
                    }
                    if(nuevoMinuto.length() == 1) {
                        nuevoMinuto="0"+nuevoMinuto;
                    }

                    horas=Integer.parseInt(nuevaHora);
                    minutos=Integer.parseInt(nuevoMinuto);

                    nuevaHoraRecordatorio=nuevaHora+":"+nuevoMinuto;
                    btnGestionarRecordatorio.setText(nuevaHoraRecordatorio);
                });
            }
        });
        // ***************************************************************************


        btnEditarHabito=findViewById(R.id.btnEditarHabito);
        btnEditarHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarHabito();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void editarHabito() {
        String nombreHabito=String.valueOf(etxtNombreHabito.getText());
        String descripcion=String.valueOf(etxtDescripcion.getText());

        // Verificaciones
        if(nombreHabito.isEmpty() ||descripcion.isEmpty())  {
            Toast.makeText(this, "Rellena los campos de nombre y descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        // Agregar hábito a la Base de Datos
        Habit habito=new Habit(id, nombreHabito, descripcion, colorSeleccionado, nuevaHoraRecordatorio, null);
        db.editarHabito(habito);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("habito", habito);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}