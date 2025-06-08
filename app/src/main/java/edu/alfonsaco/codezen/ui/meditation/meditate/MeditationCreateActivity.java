package edu.alfonsaco.codezen.ui.meditation.meditate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

import edu.alfonsaco.codezen.R;

public class MeditationCreateActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolver;
    private ImageView btnHistorialMeditaciones;
    private NumberPicker minutosPicker;
    private NumberPicker segundosPicker;
    private ImageView btnPlayMeditacion;
    private Spinner spinnerAudio;
    private MaterialSwitch switchAlarma;
    private ImageView imagenCampana;

    // Variables
    private int sonidoSeleccionado=0;
    private boolean alarmaActiva=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.meditate_create_activity);

        // Botón para volver al fragment
        btnVolver=findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // IR AL HISTORIAL DE MEDITACIONES
        btnHistorialMeditaciones=findViewById(R.id.btnHistorialMeditaciones);
        btnHistorialMeditaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeditationCreateActivity.this, HistoryMeditateActivity.class);
                startActivity(intent);
            }
        });


        // ********************** AUDIO *********************************
        spinnerAudio=findViewById(R.id.spinnerAudio);

        List<SpinnerItem> items=new ArrayList<>();
        items.add(new SpinnerItem("Quitar música", R.drawable.sound_mute, 0));
        items.add(new SpinnerItem("Ruido blanco", R.drawable.play,1));
        items.add(new SpinnerItem("Ondas binaurales", R.drawable.play,2));
        items.add(new SpinnerItem("Sonido de lluvia", R.drawable.play,3));
        items.add(new SpinnerItem("Música Backroom", R.drawable.play,4));

        spinnerAudio.setAdapter(new SpinnerAdapter(this, items));

        spinnerAudio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sonidoSeleccionado=items.get(position).getValorAudio();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sonidoSeleccionado=0;
            }
        });
        // **************************************************************


        // ************* SELECTOR DE TIEMPO DE MEDITACIÓN ***************
        minutosPicker=findViewById(R.id.minutosPicker);
        segundosPicker=findViewById(R.id.segundosPicker);

        minutosPicker.setMinValue(0);
        minutosPicker.setMaxValue(59);

        segundosPicker.setMinValue(0);
        segundosPicker.setMaxValue(59);
        // ***************************************************************


        // ************************** ALARMAS ****************************
        switchAlarma=findViewById(R.id.switchAlarma);
        imagenCampana=findViewById(R.id.imagenCampana);

        switchAlarma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchAlarma.isChecked()) {
                    alarmaActiva=true;
                    imagenCampana.setImageResource(R.drawable.alarm);

                } else {
                    alarmaActiva=false;
                    imagenCampana.setImageResource(R.drawable.no_alarm);
                }
            }
        });
        // ***************************************************************


        // ********************** CREAR MEDITACIÓN ***********************
        btnPlayMeditacion=findViewById(R.id.btnPlayMeditacion);
        btnPlayMeditacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearMeditacion();
            }
        });
        // ****************************************************************

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para crear la meditación
    private void crearMeditacion() {
        int minutos=minutosPicker.getValue();
        int segundos=segundosPicker.getValue();

        // Verificamos que sea una meditación válida
        if(minutos < 1) {
            Toast.makeText(this, "Esa meditación es muy corta, ¿No crees?", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent=new Intent(this, MeditationActivity.class);
        intent.putExtra("minutos", minutos);
        intent.putExtra("segundos", segundos);
        intent.putExtra("audio", sonidoSeleccionado);
        intent.putExtra("alarma", alarmaActiva);
        startActivity(intent);
    }
}