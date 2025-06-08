package edu.alfonsaco.codezen.otros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseUser;

import edu.alfonsaco.codezen.AuthSelectActivity;
import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.ui.meditation.focus.BlockActivity;
import edu.alfonsaco.codezen.utils.ArchievementsUnlocks;
import edu.alfonsaco.codezen.utils.BDD;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnVolverDesdeAjustes;
    private BDD db;

    // Tema claro / oscuro
    private RadioButton rdClaro, rdOscuro;
    public SharedPreferences preferencesTema;
    public String temaGuardado;

    private Button btnCerrarSesion;
    private Button btnBorrarCuenta;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    // Componentes
    private TextView txtEmail;
    private TextView txtUsername;
    private ImageView imagenUsuario;
    private ImageView irAAvatar;
    private LinearLayout btnPlayStore;
    private LinearLayout linearInstagram;
    private LinearLayout btnEnlaceReferido;

    private ActivityResultLauncher<Intent> instagramLauncher;
    private ActivityResultLauncher<Intent> playLauncher;
    private ActivityResultLauncher<Intent> referidoLauncher;

    private String username= MainActivity.username;
    private String email=MainActivity.email;

    private ArchievementsUnlocks logros;

    // Launcher cambio de Avatar
    private ActivityResultLauncher<Intent> launcherAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settings_activity);

        // Inicializar clases
        db=new BDD();
        logros=new ArchievementsUnlocks(db);

        imagenUsuario=findViewById(R.id.imagenUsuario);
        obtenerAvatar();

        // Botón para volver a la ventana de Fragments
        btnVolverDesdeAjustes=findViewById(R.id.btnVolverInicio);
        btnVolverDesdeAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gestión del tema de la aplicación
        rdClaro=findViewById(R.id.rdClaro);
        rdOscuro=findViewById(R.id.rdOscuro);

        preferencesTema=getSharedPreferences("tema", MODE_PRIVATE);
        temaGuardado=preferencesTema.getString("modo_tema", "claro");

        // Verificar el tema guardado por defecto
        verificarTema(temaGuardado);

        rdClaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPreferencias("claro");
                cambiarTema("claro");
            }
        });

        rdOscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPreferencias("oscuro");
                cambiarTema("oscuro");
            }
        });

        // Cerrar sesión en FireBase
        btnCerrarSesion=findViewById(R.id.btnCerrarSesión);
        firebaseAuth=FirebaseAuth.getInstance();
        // Para que nos deje elegir usuario diferente cada vez que cerremos sesión
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                // Cerrar cliente, para poder seleccionar otra cuenta distinta
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    volverAInicio();
                });
            }
        });

        // Borrar cuenta en FireBase
        btnBorrarCuenta=findViewById(R.id.btnBorrarCuenta);
        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
                prefs.edit().clear().apply();

                borrarCuenta();
            }
        });

        // Obtener los datos del Usuario
        txtUsername=findViewById(R.id.txtUsername);
        txtEmail=findViewById(R.id.txtEmail);
        obtenerUsername();

        // IR A CAMBIAR AVATAR
        launcherAvatar = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        obtenerAvatar();
                        obtenerUsername();
                    }
                }
        );

        irAAvatar=findViewById(R.id.irAAvatar);
        irAAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, AvatarActivity.class);
                launcherAvatar.launch(intent);
            }
        });


        // Desbloquear logro Play Store
        btnPlayStore=findViewById(R.id.linearPlayStore);
        playLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    logros.mostrarLogroDesbloqueado(SettingsActivity.this, "Supporter", "logro8", "sup_01");
                }
        );
        btnPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombrePaquete=getPackageName();
                try {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+nombrePaquete));
                    playLauncher.launch(intent);

                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + nombrePaquete)));
                }
            }
        });

        // Desbloquear Supporter V.2
        linearInstagram=findViewById(R.id.linearInstagram);
        instagramLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    logros.mostrarLogroDesbloqueado(SettingsActivity.this, "Supporter v.2", "logro9", "sup_02");
                }
        );
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.instagram.com"));
                    instagramLauncher.launch(intent);

                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.instagram.com")));
                }
            }
        });

        // Logro de Better Call Codezen
        btnEnlaceReferido=findViewById(R.id.btnEnlaceReferido);
        referidoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    logros.mostrarLogroDesbloqueado(SettingsActivity.this, "Better Call CodeZen", "logro13", "saul");
                }
        );
        btnEnlaceReferido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link="https://codezen.com/invite/3kI8skl";
                String mensaje="¡Descarga esta aplicación! "+link;

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mensaje);

                if(intent.resolveActivity(getPackageManager())!=null) {
                    referidoLauncher.launch(Intent.createChooser(intent, "Compartir vía"));
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void borrarCuenta() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
        View viewAlert=getLayoutInflater().inflate(R.layout.dialog_finalizar_meditacion, null);
        builder.setView(viewAlert);

        AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView txtTitulo=dialog.findViewById(R.id.txtTitulo);
        TextView txtTexto=dialog.findViewById(R.id.txtTexto);
        Button btnCancelar=dialog.findViewById(R.id.btnCancelar);
        Button btnFinalizar=dialog.findViewById(R.id.btnFinalizar);

        txtTitulo.setText("ELIMINAR CUENTA");
        txtTexto.setText("¿Estás seguro de que quieres eliminar la cuenta? Una vez lo hayas hecho, no podrás recuperarla");
        btnFinalizar.setText("ELIMINAR");

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.eliminarUsuarioBDD();

                FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
                usuario.delete()
                        .addOnCompleteListener(a -> {
                            Log.d("USUARIO ELIMINADO", "Usuario eliminado correctamente");
                        })
                        .addOnFailureListener(e ->  {
                            Log.e("ERROR ELIMINANDO USUARIO", "Error al eliminar el usuario");
                        });

                firebaseAuth.signOut();
                volverAInicio();
            }
        });
    }

    private void obtenerUsername() {
        db.getDb()
                .collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String username=snapshot.getString("username");
                    txtUsername.setText(username);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo cargar el avatar");
                });
    }

    private void obtenerAvatar() {
        db.getDb()
                .collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String avatar=snapshot.getString("avatar");

                    int idImagen=getResources().getIdentifier(avatar, "drawable", getPackageName());
                    imagenUsuario.setImageResource(idImagen);
                    // Imagen circular con borde
                    int colorBorde = resolveColorAttr(androidx.biometric.R.attr.colorPrimary);
                    Glide.with(this).load(idImagen)
                            .transform(new CropCircleWithBorderTransformation(8, colorBorde)).into(imagenUsuario);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo cargar el avatar");
                });
    }
    // Obtener el color de los Attr
    @ColorInt
    private int resolveColorAttr(@AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{attr});
        int color = a.getColor(0, Color.BLACK);
        a.recycle();
        return color;
    }

    // ******************* MÉTODOS PARA CAMBIAR EL TEMA CLARO OSCURO **********************
    // Para verificar si el tema guardado es claro u oscuro, y seleccioanr los RadioButtons
    public void verificarTema(String tema) {
        if(tema.equals("claro")) {
            rdClaro.setChecked(true);
        } else {
            rdOscuro.setChecked(true);
        }

        cambiarTema(tema);
    }

    private void guardarPreferencias(String tema) {
        SharedPreferences.Editor editor=preferencesTema.edit();
        editor.putString("modo_tema", tema);
        editor.apply();
    }

    private void cambiarTema(String tema) {
        if(tema.equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    // ************************************************************************************


    // **************************** MÉTODOS PARA EL SIGN OUT ******************************
    private void volverAInicio() {
        Intent intent=new Intent(SettingsActivity.this, AuthSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
    // ************************************************************************************


    @Override
    protected void onStart() {
        super.onStart();

        txtUsername.setText(username);
        txtEmail.setText(email);
    }
}