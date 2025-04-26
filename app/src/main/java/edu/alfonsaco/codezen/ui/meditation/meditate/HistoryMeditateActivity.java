package edu.alfonsaco.codezen.ui.meditation.meditate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;

public class HistoryMeditateActivity extends AppCompatActivity {

    // Componentes
    private ImageView btnVolver;
    private TextView txtTotaslMeditaciones;

    // Recycler
    private RecyclerView recyclerMeditaciones;
    private MeditacionAdapter adapterMeditaciones;
    private ArrayList<Meditation> listaMeditaciones;

    private BDD db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.meditate_history);

        db=new BDD();


        // RECYCLER
        recyclerMeditaciones=findViewById(R.id.recyclerMeditaciones);
        listaMeditaciones=new ArrayList<>();
        adapterMeditaciones=new MeditacionAdapter(listaMeditaciones, this);
        recyclerMeditaciones.setAdapter(adapterMeditaciones);
        recyclerMeditaciones.setLayoutManager(new LinearLayoutManager(this));
        cargarMeditaciones();

        // Volver a la actividad de meditaciones
        btnVolver=findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // PONER LA CANTIDAD DE MEDITACIONES
        txtTotaslMeditaciones=findViewById(R.id.txtTotaslMeditaciones);
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long contMeditaciones=documentSnapshot.getLong("cont_meditaciones");
                    if(contMeditaciones != null) {
                        txtTotaslMeditaciones.setText(contMeditaciones.toString());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL OBTENER EL CONTADOR DE MEDITACIONES");
                });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void cargarMeditaciones() {
        if(db.getUsuarioID() == null) {
            return;
        }

        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .collection("meditaciones")
                .get()
                .addOnSuccessListener(consulta -> {
                    listaMeditaciones.clear();

                    for(DocumentSnapshot doc : consulta.getDocuments()) {
                        String duracion=doc.getString("duracion");
                        String minutos=duracion.split(":")[0];
                        String segundos=duracion.split(":")[1];

                        String duracionFinal=minutos+" minutos y "+segundos+" segundos";

                        String fecha=doc.getString("fecha");
                        String id=doc.getString("id");

                        Meditation meditacion=new Meditation(id, fecha, duracionFinal);
                        listaMeditaciones.add(meditacion);
                    }

                    adapterMeditaciones.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "ERROR AL OBTENER LAS MEDITACIONES");
                });
    }
}