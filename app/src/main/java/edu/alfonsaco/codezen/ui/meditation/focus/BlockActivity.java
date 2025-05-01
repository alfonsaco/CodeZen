package edu.alfonsaco.codezen.ui.meditation.focus;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.alfonsaco.codezen.R;

public class BlockActivity extends AppCompatActivity {

    private Button btnFinalizarFocus;
    private TextView txtTiempoRestanteFocus;
    private TextView txtSegundos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.focus_block);

        txtSegundos=findViewById(R.id.txtSegundos);
        txtTiempoRestanteFocus=findViewById(R.id.txtTiempoRestanteFocus);

        Intent intent=getIntent();
        int segundos=intent.getIntExtra("segundos", 0);
        int minutos=intent.getIntExtra("minutos", 0);
        int horas=intent.getIntExtra("horas", 0);

        iniciarContador(segundos, minutos, horas);


        // MÉTODO POR DEFECTO DE ANDROID STUDIO PARA BLOQUEAR PANTALLA
        startLockTask();
        // EVITAMOS QUE SALGA AL PULSAR ATRÁS
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });

        // ******************* CERRAR LA SESIÓN CON UN DIÁLOGO ************************
        btnFinalizarFocus=findViewById(R.id.btnFinalizarFocus);
        btnFinalizarFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAlerta();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void iniciarContador(int segundos, int minutos, int horas) {
        int milis=(horas * 60 * 60 * 1000) + (minutos * 60 * 1000) + (segundos * 1000);

        CountDownTimer countDownTimer=new CountDownTimer(milis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int horasRestantes=(int) (millisUntilFinished / (1000 * 60 * 60));
                int minutosRestantes=(int) (millisUntilFinished / (1000 * 60)) % 60;
                int segundosRestantes=(int) (millisUntilFinished / 1000) % 60;

                String horasRestantesTexto=String.format("%02d", horasRestantes);
                String minutosRestantesTexto=String.format("%02d", minutosRestantes);
                String segundosRestantesTexto=String.format("%02d", segundosRestantes);

                txtTiempoRestanteFocus.setText(horasRestantesTexto+":"+minutosRestantesTexto);
                txtSegundos.setText(segundosRestantesTexto);
            }

            @Override
            public void onFinish() {
                txtSegundos.setText("00");
                txtTiempoRestanteFocus.setText("00:00");

                stopLockTask();
                finish();
            }
        }.start();
    }

    private void mostrarAlerta() {
        AlertDialog.Builder builder=new AlertDialog.Builder(BlockActivity.this);
        View viewAlert=getLayoutInflater().inflate(R.layout.dialog_finalizar_meditacion, null);
        builder.setView(viewAlert);

        AlertDialog dialog=builder.create();
        dialog.show();

        TextView txtTitulo=dialog.findViewById(R.id.txtTitulo);
        TextView txtTexto=dialog.findViewById(R.id.txtTexto);
        Button btnCancelar=dialog.findViewById(R.id.btnCancelar);
        Button btnFinalizar=dialog.findViewById(R.id.btnFinalizar);

        txtTitulo.setText("FINALIZAR FOCUS");
        txtTexto.setText("¿Estás seguro de que quieres finalizar el focus?");

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLockTask();
                finish();
            }
        });
    }
}