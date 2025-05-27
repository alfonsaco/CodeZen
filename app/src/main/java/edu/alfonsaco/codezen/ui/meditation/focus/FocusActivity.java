package edu.alfonsaco.codezen.ui.meditation.focus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.alfonsaco.codezen.R;

public class FocusActivity extends AppCompatActivity {

    private ImageView btnVolver;
    private ImageView btnIniciarFocus;

    private NumberPicker segundosPicker;
    private NumberPicker minutosPicker;
    private NumberPicker horasPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.focus_activity);

        // COMPONENTES
        segundosPicker=findViewById(R.id.segundosPicker);
        minutosPicker=findViewById(R.id.minutosPicker);
        horasPicker=findViewById(R.id.horasPicker);

        segundosPicker.setMinValue(0);
        segundosPicker.setMaxValue(59);
        minutosPicker.setMinValue(0);
        minutosPicker.setMaxValue(59);
        horasPicker.setMinValue(0);
        horasPicker.setMaxValue(23);

        // VOLVER
        btnVolver=findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // INICIAR FOCUS
        btnIniciarFocus=findViewById(R.id.btnIniciarFocus);
        btnIniciarFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((minutosPicker.getValue() < 1 && horasPicker.getValue() < 1)) {
                    Toast.makeText(FocusActivity.this, "Esta sesión es muy corta", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Intent intent=new Intent(FocusActivity.this, BlockActivity.class);
                intent.putExtra("segundos", segundosPicker.getValue());
                intent.putExtra("minutos", minutosPicker.getValue());
                intent.putExtra("horas", horasPicker.getValue());
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}