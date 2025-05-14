package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Day;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Habit;
import edu.alfonsaco.codezen.utils.ArchievementsUnlocks;
import edu.alfonsaco.codezen.utils.BDD;
import edu.alfonsaco.codezen.utils.Notificaciones;

public class CreateHabitActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolverInicio;
    private Button btnAgregarNuevoHabito;
    private EditText etxtNombreHabito;
    private EditText etxtDescripcion;
    private MaterialSwitch switchRecordatorios;
    private Button btnGestionarRecordatorio;

    // Selección de color
    private String colorSeleccionado;
    private ArrayList<View> listaColores;
    private boolean haSeleccionadoColor=false;

    // Recordatorio
    private String nuevaHoraRecordatorio="";
    private int horas;
    private int minutos;

    // FirebaseAuth
    private BDD bd;

    // Notificaciones
    private Notificaciones notificaciones;
    private String recordatorioPendiente = "";

    private ArchievementsUnlocks logros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_create_activity);

        btnAgregarNuevoHabito=findViewById(R.id.btnEditarHabito);
        etxtNombreHabito=findViewById(R.id.etxtNombreHabito);
        etxtDescripcion=findViewById(R.id.etxtDescripcion);
        switchRecordatorios=findViewById(R.id.switchAlarma);
        btnGestionarRecordatorio=findViewById(R.id.btnGestionarRecordatorio);

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
                Toast.makeText(CreateHabitActivity.this, nuevaHoraRecordatorio, Toast.LENGTH_SHORT).show();
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


        // AGREGAR EL HÁBITO NUEVO
        btnAgregarNuevoHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarHabito();
            }
        });


        // AGREGAR HÁBITO A LA BASE DE DATOS
        bd=new BDD();
        notificaciones=new Notificaciones(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // MÉTODOS PARA LOS LOGROS
        logros=new ArchievementsUnlocks(bd);
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

        // CREACIÓN AUTOMÁTICA DEL ID EN FIREBASE
        DocumentReference nuevoHabitoRef = bd.getDb()
                .collection("usuarios")
                .document(bd.getUsuarioID())
                .collection("habitos")
                .document();

        // Obtener el ID generado
        String idHabito=nuevoHabitoRef.getId();


        // FECHAS DE DÍAS DE HÁBITOS
        // Obtenemos la fecha de aquí a 196 días
        ArrayList<Day> diasHabitos=new ArrayList<>();
        LocalDate hoy=LocalDate.now();

        // Insertamos la fecha de cada uno en cada uno de los "Days"
        int diaSemana=obtenerDiaSemana();

        for(int i=0; i< (196 + diaSemana); i++) {
            Day dia=new Day();
            dia.setId(hoy.minusDays(i).toString());
            dia.setCompletado(false);
            dia.setColor(colorSeleccionado);

            diasHabitos.add(dia);
        }

        Habit habito=new Habit(idHabito, nombreHabito, descripcion, colorSeleccionado, nuevaHoraRecordatorio, diasHabitos);
        bd.guardarHabitoEnUsuario(habito);

        // Guardar recordatorio
        if (switchRecordatorios.isChecked() && !nuevaHoraRecordatorio.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {

                recordatorioPendiente = nuevaHoraRecordatorio;
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001);
            } else {
                Notificaciones notificaciones = new Notificaciones(this);
                notificaciones.programarRecordatorio(nuevaHoraRecordatorio, nombreHabito);
                terminarActividadConResultado(habito);
            }
        } else {
            terminarActividadConResultado(habito);
        }

        logros.logrosHabitos();
    }

    private void terminarActividadConResultado(Habit habito) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("habito", habito);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String nombre=String.valueOf(etxtNombreHabito.getText());

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ahora sí programamos la notificación
                if (!recordatorioPendiente.isEmpty()) {
                    Notificaciones notificaciones = new Notificaciones(this);
                    notificaciones.programarRecordatorio(recordatorioPendiente, nombre);
                }
            } else {
                Toast.makeText(this, "No se permitió enviar notificaciones. El recordatorio no será creado.", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    // Método para obtener el día de la semana, para saber cuantos Divs agregar
    private int obtenerDiaSemana() {
        Date dia=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dia);

        int diaSemana=calendar.get(Calendar.DAY_OF_WEEK);
        int diaHoy=0;

        switch (diaSemana) {
            case 1:
                diaHoy=7;
                break;
            case 2:
                diaHoy=1;
                break;
            case 3:
                diaHoy=2;
                break;
            case 4:
                diaHoy=3;
                break;
            case 5:
                diaHoy=4;
                break;
            case 6:
                diaHoy=5;
                break;
            case 7:
                diaHoy=6;
                break;
        }

        return diaHoy;
    }
}