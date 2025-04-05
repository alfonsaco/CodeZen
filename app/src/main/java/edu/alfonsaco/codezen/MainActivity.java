package edu.alfonsaco.codezen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.alfonsaco.codezen.databinding.ActivityMainBinding;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ToolBar de los fragments
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
}