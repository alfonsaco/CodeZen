package edu.alfonsaco.codezen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.alfonsaco.codezen.databinding.ActivityMainBinding;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private SharedPreferences preferencesTema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Se aplica el tema antes de realizar cualquier otra acción
        aplicarTema();

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ----CAMBIAR TEMA CLARO OSCURO ----
        preferencesTema = getSharedPreferences("tema", MODE_PRIVATE);
        // ----------------------------------


        // --------------------------- TOOLBAR DE LOS FRAMENTS --------------------------------
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        // Ocultamos el texto del Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_habits, R.id.navigation_dev, R.id.navigation_meditate, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Icono de ajustes a la izquierda
        toolbar.setNavigationIcon(R.drawable.settings_30dp);
        toolbar.setNavigationContentDescription("Ajustes");

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        // (Para cambiar el tamaño del icono, ir a settings.xml)
        // -----------------------------------------------------------------------------------


        // ------------------------- OBTENER DATOS DEL INTENT --------------------------------

    }

    // Inflar el ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.btnNotificaciones) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void aplicarTema() {
        SharedPreferences preferencias=getSharedPreferences("tema", MODE_PRIVATE);
        String tema = preferencias.getString("modo_tema", "claro");

        if (tema.equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intentDatosUsuario=getIntent();
        String nombreUsuario=intentDatosUsuario.getStringExtra("nombre");
        String emailUsuario=intentDatosUsuario.getStringExtra("email");

        Toast.makeText(this, "HA INICIADO SESIÓN EL USUARIO "+nombreUsuario+" CON EL EMAIL "+emailUsuario, Toast.LENGTH_SHORT).show();
    }
}