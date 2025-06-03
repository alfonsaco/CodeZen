package edu.alfonsaco.codezen;

import android.app.ComponentCaller;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.alfonsaco.codezen.databinding.ActivityMainBinding;
import edu.alfonsaco.codezen.otros.SettingsActivity;
import edu.alfonsaco.codezen.ui.dev.DevFragment;
import edu.alfonsaco.codezen.utils.ArchievementsUnlocks;
import edu.alfonsaco.codezen.utils.BDD;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsControllerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BDD db;
    private ArchievementsUnlocks logros;

    private TextView txtRachaToolbar;

    private SharedPreferences preferencesTema;

    // Datos del Intent
    public static String email;
    public static String username;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db=new BDD();
        logros=new ArchievementsUnlocks(db);

        txtRachaToolbar=findViewById(R.id.txtRachaToolbar);

        // Se aplica el tema antes de realizar cualquier otra acción
        aplicarTema();

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
                R.id.navigation_habits, R.id.navigation_dev, R.id.navigation_meditate, R.id.navigation_ranking, R.id.navigation_profile)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // ------------------------- OBTENER DATOS DEL USUARIO --------------------------------
        firebaseAuth=FirebaseAuth.getInstance();
        email=firebaseAuth.getCurrentUser().getEmail();
        username=firebaseAuth.getCurrentUser().getDisplayName();
    }

    // Inflar el ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem rachaItem = menu.findItem(R.id.menu_racha);
        View actionView = rachaItem.getActionView();
        txtRachaToolbar = actionView.findViewById(R.id.txtRachaToolbar);

        // Inicializar con el valor actual
        obtenerRachaDesdeFirebase();

        return super.onCreateOptionsMenu(menu);
    }
    private void obtenerRachaDesdeFirebase() {
        String hoy = obtenerHoy();

        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .collection("rachas")
                .document(hoy)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int racha = documentSnapshot.getLong("dias_racha").intValue();
                        txtRachaToolbar.setText(String.valueOf(racha));
                    }
                });
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

        actualizarRacha();
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
                // Guardar el código temporalmente
                getSharedPreferences("auth_temp", MODE_PRIVATE)
                        .edit()
                        .putString("oauth_code", code)
                        .apply();

                // Navegar al fragmento Dev si no está visible
                BottomNavigationView navView = findViewById(R.id.nav_view);
                navView.setSelectedItemId(R.id.navigation_dev);

                // Procesar el código inmediatamente
                processStoredOAuthCode();
            }
        }
    }

    private void processStoredOAuthCode() {
        SharedPreferences prefs = getSharedPreferences("auth_temp", MODE_PRIVATE);
        String code = prefs.getString("oauth_code", null);

        if (code != null) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);

            if (navHostFragment != null) {
                Fragment fragment = navHostFragment.getChildFragmentManager()
                        .getPrimaryNavigationFragment();

                if (fragment instanceof DevFragment) {
                    DevFragment.obtenerToken(code, this);
                    // Limpiar el código temporal
                    prefs.edit().remove("oauth_code").apply();
                }
            }
        }
    }
    public void updateDevFragmentUI() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            Fragment fragment = navHostFragment.getChildFragmentManager()
                    .getPrimaryNavigationFragment();

            if (fragment instanceof DevFragment) {
                ((DevFragment) fragment).checkLoginStatus();
            }
        }
    }

    private void actualizarRacha() {
        String hoy = obtenerHoy();
        String ayer = obtenerAyer();

        String usuarioId = db.getUsuarioID();

        DocumentReference hoyRef = db.getDb().collection("usuarios")
                .document(usuarioId)
                .collection("rachas")
                .document(hoy);

        DocumentReference ayerRef = db.getDb().collection("usuarios")
                .document(usuarioId)
                .collection("rachas")
                .document(ayer);

        db.getDb().runTransaction((Transaction.Function<Integer>) transaction -> {
            DocumentSnapshot hoySnap = transaction.get(hoyRef);

            // Verificamos si ya se hizo antes la operación
            if (hoySnap.exists()) {
                Long rachaHoy = hoySnap.getLong("dias_racha");
                return (rachaHoy != null) ? rachaHoy.intValue() : 1;
            }

            int nuevaRacha = 1;

            // Ver si ayer hay documento con racha
            DocumentSnapshot ayerSnap = transaction.get(ayerRef);
            if (ayerSnap.exists()) {
                Long rachaAyer = ayerSnap.getLong("dias_racha");
                if (rachaAyer != null) {
                    nuevaRacha = rachaAyer.intValue() + 1;
                }
            }

            Map<String, Object> datosHoy = new HashMap<>();
            datosHoy.put("dias_racha", nuevaRacha);
            datosHoy.put("fecha_ultima", hoy);

            transaction.set(hoyRef, datosHoy);
            return nuevaRacha;

        }).addOnSuccessListener(nuevaRacha -> {
            Log.d("Racha", "Racha registrada correctamente en documento del día");
            logros.logrosRacha(nuevaRacha, MainActivity.this);

            // Actualizar el TextView si está inicializado
            if (txtRachaToolbar != null) {
                txtRachaToolbar.setText(String.valueOf(nuevaRacha));
            }
        }).addOnFailureListener(e -> {
            Log.e("Racha", "Error registrando racha del día", e);
        });
    }

    private String obtenerHoy() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String obtenerAyer() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.DATE, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(calendar.getTime());
    }

}