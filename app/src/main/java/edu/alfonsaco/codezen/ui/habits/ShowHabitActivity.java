package edu.alfonsaco.codezen.ui.habits;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;

public class ShowHabitActivity extends AppCompatActivity {

    private ImageView btnVolverInicio;
    private TextView txtNombreShow;
    private TextView txtDescripcionShow;
    private CalendarView calendarView;

    private BDD db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_show_activity);

        db=new BDD();

        // VOLVER AL INICIO
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ********** OBTENER DATOS DEL HÁBITO CON EL INTENT **********
        txtNombreShow=findViewById(R.id.txtNombreShow);
        txtDescripcionShow=findViewById(R.id.txtDescripcionShow);

        Intent intent=getIntent();
        String nombre=intent.getStringExtra("nombre");
        String descripcion=intent.getStringExtra("descripcion");
        String id=intent.getStringExtra("id");

        txtNombreShow.setText(nombre);
        txtDescripcionShow.setText(descripcion);
        // ************************************************************

        // ******************** CALENDAR VIEW *************************
        calendarView=findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dia=String.valueOf(dayOfMonth);
                String mes=String.valueOf(month+1);

                if(mes.length() == 1) {
                    mes="0"+mes;
                }
                if(dia.length() == 1) {
                    dia="0"+dia;
                }

                String diaSeleccionado=year+"-"+mes+"-"+dia;
                System.out.println(diaSeleccionado);

                db.verificarCompletadoBoolean(diaSeleccionado, id, completado -> {
                    if(completado) {
                        System.out.println("DIA COMPLETADO");
                    } else {
                        System.out.println("DIA NO COMPLETADO");
                    }
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}