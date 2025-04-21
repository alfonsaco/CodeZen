package edu.alfonsaco.codezen.ui.meditation.meditate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import edu.alfonsaco.codezen.R;

public class MeditationActivity extends AppCompatActivity {

    // Variables
    private int minutos;
    private int segundos;

    private boolean contadorActivo=true;
    private CountDownTimer countDown;

    // Componentes
    private ImageView btnFinalizarMeditacion;
    private TextView txtDuracionCompleta;
    private TextView txtTiempoRestante;
    private ImageView btnPararReanudar;
    private CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.meditate_activity);

        // BOTÓN DE SALIR DE LA MEDITACIÓN
        btnFinalizarMeditacion=findViewById(R.id.btnFinalizarMeditacion);
        btnFinalizarMeditacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contadorActivo) {
                    alertaFinalizarMeditacion();
                } else {
                    finish();
                }
            }
        });
        // Para evitar salir de la meditación al pulsar "Atrás"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(contadorActivo) {
                    alertaFinalizarMeditacion();
                } else {
                    finish();
                }
            }
        });


        // ******************* INSTAURAR DURACIÓN MEDITACIÓN ********************
        Intent intent=getIntent();
        minutos=intent.getIntExtra("minutos", 0);
        segundos=intent.getIntExtra("segundos", 0);

        String minutosTexto=String.valueOf(minutos);
        String segundosTexto=String.valueOf(segundos);

        if(minutosTexto.length() == 1) {
            minutosTexto="0"+minutosTexto;
        }

        minutosTexto=minutosTexto.length() == 1 ? "0"+minutosTexto : minutosTexto;
        segundosTexto=segundosTexto.length() == 1 ? "0"+segundosTexto : segundosTexto;

        txtDuracionCompleta=findViewById(R.id.txtDuracionCompleta);
        txtTiempoRestante=findViewById(R.id.txtTiempoRestante);
        circularProgressBar=findViewById(R.id.circularProgressBar);

        txtDuracionCompleta.setText(minutosTexto+":"+segundosTexto);
        empezarContador(minutos, segundos);


        // BOTÓN DE DETENER O REANUDAR LA MOTIVACIÓN
        btnPararReanudar=findViewById(R.id.btnPararReanudar);
        btnPararReanudar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contadorActivo) {
                    btnPararReanudar.setImageResource(R.drawable.play);
                    contadorActivo=false;
                    countDown.cancel();

                } else {
                    btnPararReanudar.setImageResource(R.drawable.pause);
                    contadorActivo=true;
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void empezarContador(int minutos, int segundos) {
        int tiempoMilis=(minutos * 60 + segundos) * 1000;
        circularProgressBar.setProgressMax(tiempoMilis);

        countDown=new CountDownTimer(tiempoMilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String minutosRestantes=String.valueOf((int) (millisUntilFinished / 1000) / 60);
                String segundosRestantes=String.valueOf((int) (millisUntilFinished / 1000) % 60);

                minutosRestantes=minutosRestantes.length() == 1 ? "0"+minutosRestantes : minutosRestantes;
                segundosRestantes=segundosRestantes.length() == 1 ? "0"+segundosRestantes : segundosRestantes;

                String tiempoRestante=minutosRestantes+":"+segundosRestantes;
                txtTiempoRestante.setText(tiempoRestante);

                circularProgressBar.setProgress(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Toast.makeText(MeditationActivity.this, "Meditación terminada", Toast.LENGTH_SHORT).show();
                contadorActivo=false;
            }
        };

        countDown.start();
    }

    // MÉTODO PARA EL BOTÓN DE SALIR Y EL BACK PRESSED
    private void alertaFinalizarMeditacion() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MeditationActivity.this);
        View dialogView=getLayoutInflater().from(MeditationActivity.this).inflate(R.layout.dialog_finalizar_meditacion, null);
        builder.setView(dialogView);

        AlertDialog dialog=builder.create();
        dialog.show();

        Button btnCancelar=dialog.findViewById(R.id.btnCancelar);
        Button btnFinalizar=dialog.findViewById(R.id.btnFinalizar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // CERRAMOS LA ACTIVIDAD
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDown.cancel();
                dialog.dismiss();

                finish();
            }
        });
    }
}