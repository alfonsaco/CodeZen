package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Habit;
import edu.alfonsaco.codezen.ui.habits.habit_utils.HabitOptionsBottomSheet;
import edu.alfonsaco.codezen.utils.BDD;

public class ShowHabitActivity extends AppCompatActivity {

    private ImageView btnIrEdit;
    private ImageView btnVolverInicio;

    private BDD bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habit_show_activity);

        // VOLVER AL INICIO
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}