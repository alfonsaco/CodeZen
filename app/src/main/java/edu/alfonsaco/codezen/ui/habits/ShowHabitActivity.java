package edu.alfonsaco.codezen.ui.habits;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.datepicker.DayViewDecorator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;
import edu.alfonsaco.codezen.utils.MyDayDecorator;

public class ShowHabitActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolverInicio;
    private TextView txtNombreShow;
    private TextView txtDescripcionShow;
    private MaterialCalendarView calendarView;

    private BDD db;
    private int colorCompletado;
    private int colorConOpacidad;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_show_activity);

        db=new BDD();
        firestore=FirebaseFirestore.getInstance();

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
        String color=intent.getStringExtra("color");

        colorCompletado=Color.parseColor(color);
        colorConOpacidad = Color.argb(190, Color.red(colorCompletado), Color.green(colorCompletado), Color.blue(colorCompletado));

        txtNombreShow.setText(nombre);
        txtDescripcionShow.setText(descripcion);
        // ************************************************************


        // ******************** CALENDAR VIEW *************************
        calendarView=findViewById(R.id.calendarView);
        // Cargamos los días completados
        cargarDiasEnCalendario(id);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String year=String.valueOf(date.getYear());
                String mes=String.valueOf(date.getMonth()+1);
                String dia=String.valueOf(date.getDay());

                if(mes.length() == 1) {
                    mes="0"+mes;
                }
                if(dia.length() == 1) {
                    dia="0"+dia;
                }

                String diaSeleccionado=year+"-"+mes+"-"+dia;
                System.out.println(diaSeleccionado);

                // Cambiamos su estado en la base de datos
                db.verificarCompletadoBoolean(diaSeleccionado, id, completado -> {
                    if(completado) {
                        db.cambiarEstadoDia(false, id, diaSeleccionado);
                    } else {
                        db.cambiarEstadoDia(true, id, diaSeleccionado);
                    }

                    // Le bajo la opacidad al color
                    widget.addDecorator(new MyDayDecorator(date, completado ? Color.TRANSPARENT : colorConOpacidad));
                });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void cargarDiasEnCalendario(String idHabito) {
        firestore.collection("usuarios")
                .document(db.getUsuarioID())
                .collection("habitos")
                .document(idHabito)
                .collection("dias")
                .whereEqualTo("completado", true)
                .get()
                .addOnSuccessListener(documentaSnapshot -> {
                    for(DocumentSnapshot doc : documentaSnapshot) {
                        String fecha = doc.getId();
                        // Convertir la fecha string a CalendarDay
                        String[] partes = fecha.split("-");
                        int year = Integer.parseInt(partes[0]);
                        int month = Integer.parseInt(partes[1]);
                        int day = Integer.parseInt(partes[2]);

                        CalendarDay calendarDay = CalendarDay.from(year, month - 1, day);
                        calendarView.addDecorator(new MyDayDecorator(calendarDay, colorConOpacidad));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "NO SE PUDIERON OBTENER LOS DÍAS COMPLETADOS EN EL CALENDARIO");
                });
    }
}