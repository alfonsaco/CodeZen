package edu.alfonsaco.codezen;

import android.app.ComponentCaller;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.alfonsaco.codezen.databinding.ActivityMainBinding;
import edu.alfonsaco.codezen.otros.SettingsActivity;
import edu.alfonsaco.codezen.ui.dev.DevFragment;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private SharedPreferences preferencesTema;

    // Datos del Intent
    public static String email;
    public static String username;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Se aplica el tema antes de realizar cualquier otra acción
        aplicarTema();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ------------------------ CAMBIAR TEMA DE CLAR A OSCURO ----------------------------
        preferencesTema = getSharedPreferences("tema", MODE_PRIVATE);
        // -----------------------------------------------------------------------------------


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
        /*
        toolbar.setNavigationIcon(R.drawable.settings_30dp);
        toolbar.setNavigationContentDescription("Ajustes");

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        */
        // (Para cambiar el tamaño del icono, ir a settings.xml)
        // -----------------------------------------------------------------------------------


        // ------------------------- OBTENER DATOS DEL USUARIO --------------------------------
        firebaseAuth=FirebaseAuth.getInstance();
        email=firebaseAuth.getCurrentUser().getEmail();
        username=firebaseAuth.getCurrentUser().getDisplayName();
    }

    // Inflar el ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.btnAjustes) {
            Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
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
    }

    // PARA OBTENER LOS DATOS AL INICIAR SESIÓN EN GITHUB
    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleOAuthIntent(intent);
    }

    private void handleOAuthIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith("codezen://callback")) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Toast.makeText(MainActivity.this, "CODE: " + code, Toast.LENGTH_SHORT).show();

                BottomNavigationView navView = findViewById(R.id.nav_view);
                navView.setSelectedItemId(R.id.navigation_dev);

                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

                if (currentFragment instanceof DevFragment) {
                    DevFragment devFragment = (DevFragment) currentFragment;
                    DevFragment.obtenerToken(code, devFragment.requireContext());
                } else {
                    // Esperar a que se cargue el fragmento con un pequeño delay
                    new android.os.Handler().postDelayed(() -> {
                        Fragment fragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                        if (fragment instanceof DevFragment) {
                            DevFragment.obtenerToken(code, fragment.requireContext());
                        }
                    }, 500);
                }
            }
        }
    }
    public void updateDevFragmentUI() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            Fragment fragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (fragment instanceof DevFragment) {
            }
        }
    }


}