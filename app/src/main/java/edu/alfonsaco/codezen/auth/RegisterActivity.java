package edu.alfonsaco.codezen.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;
import edu.alfonsaco.codezen.utils.Verifications;

public class RegisterActivity extends AppCompatActivity {

    private Button btnCambiarAInicio;
    private Button btnCrearCuenta;
    private ImageView btnVolverInicio;
    private ImageView iconoApp;

    // Firebase
    private SignInButton btnGoogleRegistro;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    // Campos de texto
    private EditText etxtUsuario;
    private EditText etxtEmail;
    private EditText etxtContra;

    private BDD baseDeDatos;
    private Verifications verifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_register_activity);

        iconoApp=findViewById(R.id.iconoApp);
        Glide.with(this).load(R.mipmap.ic_launcher).circleCrop().into(iconoApp);

        // Cambiar de actividad al Inicio
        btnCambiarAInicio=findViewById(R.id.btnCambiarAInicio);

        btnCambiarAInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // ********** INICIO DE SESIÓN EN FIREBASE ***********
        firebaseAuth=FirebaseAuth.getInstance();

        // Google
        btnGoogleRegistro=findViewById(R.id.btnGoogleRegistro);
        // Cambiar texto del botón
        ((TextView) btnGoogleRegistro.getChildAt(0)).setText("Inicia sesión con Google");
        btnGoogleRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesionGoogle();
            }
        });

        // Configuración del cliente
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.id_toke_firebase_auth)).requestEmail().build();
        googleSignInClient= GoogleSignIn.getClient(this, gso);

        // Launcher
        googleSignInLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        handleGoogleSignInResult(result.getData());
                    }
                }
        );

        // Email y contraseña
        etxtUsuario=findViewById(R.id.etxtUsuario);
        etxtEmail=findViewById(R.id.etxtEmail);
        etxtContra=findViewById(R.id.etxtContra);
        btnCrearCuenta=findViewById(R.id.btnCrearCuenta);
        verifications=new Verifications();

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificacionesCrearCuenta();
            }
        });
        // *******************************************************

        baseDeDatos=new BDD();

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


    // ********************* MÉTODOS DE INICIO DE SESIÓN POR GOOGLE EN FIREBASE *************************
    private void iniciarSesionGoogle() {
        Intent intentSignIn=googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(intentSignIn);
    }

    private void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(this, "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if(user != null) {
                            baseDeDatos.getDb().collection("usuarios")
                                    .whereEqualTo("email", user.getEmail())
                                    .get()
                                    .addOnCompleteListener(a -> {
                                        if (a.isSuccessful()) {
                                            if (a.getResult().isEmpty()) {
                                                // Usuario no existe
                                                String nombreCortado = user.getDisplayName();
                                                int random = (int) (1000 + Math.random() * 100000);

                                                if(nombreCortado == null || nombreCortado.isEmpty()) {
                                                    nombreCortado = "usuario"+random;
                                                } else {
                                                    if(nombreCortado.length() > 20) {
                                                        nombreCortado = nombreCortado.substring(0, 20);
                                                    }
                                                }

                                                baseDeDatos.guardarUsuarioEnFirebase(nombreCortado, user.getEmail());
                                            }

                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Error al verificar usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ERROR", "Error verificando usuario");
                                    });
                        }


                    } else {
                        Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Email y contraseña
    private void verificacionesCrearCuenta() {
        String nombreUsuario=String.valueOf(etxtUsuario.getText());
        String email=String.valueOf(etxtEmail.getText());
        String contra=String.valueOf(etxtContra.getText());

        // Verificaciones
        if(nombreUsuario.isEmpty() || email.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(nombreUsuario.length() < 3) {
            Toast.makeText(this, "El nombre de usuario debe contener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if(nombreUsuario.length() > 20) {
            Toast.makeText(this, "El nombre de usuario es demasiado largo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(contra.length() < 6) {
            Toast.makeText(this, "La contraseña es demasiado corta", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!verifications.esEmail(email)) {
            Toast.makeText(this, "El email insertado no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar usuario repetido
        baseDeDatos.getUsuariosCollection()
                .whereEqualTo("username", nombreUsuario)
                .limit(1)
                .get()
                .addOnCompleteListener(taskUsername -> {
                    if(taskUsername.isSuccessful()) {
                        if(!taskUsername.getResult().isEmpty()) {
                            Toast.makeText(this,"Ya existe un usuario con este nombre de usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            crearCuentaEmail(email, contra, nombreUsuario);
                        }
                    }
                });
    }
    private void crearCuentaEmail(String email, String contra, String nombreUsuario) {
        firebaseAuth.createUserWithEmailAndPassword(email, contra).addOnCompleteListener(this, task -> {
            // Verificar email repetido. Esto lo hace directamente el FirebaseAuth
            if(task.getException() != null && task.getException().getMessage().contains("email address is already in use by another account")) {
                Toast.makeText(this, "Ya existe una cuenta asociada a este email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se crea la cuenta
            if(task.isSuccessful()) {
                FirebaseUser usuario=firebaseAuth.getCurrentUser();

                // Agregar el username al usuario creado
                UserProfileChangeRequest cambioDatos= new UserProfileChangeRequest.Builder().setDisplayName(nombreUsuario).build();

                usuario.updateProfile(cambioDatos).addOnCompleteListener(taskPerfil -> {
                    if (taskPerfil.isSuccessful()) {
                        // Enviar verificación de email
                        usuario.sendEmailVerification().addOnCompleteListener(taskEmail -> {
                            if (taskEmail.isSuccessful()) {
                                Toast.makeText(this, "Cuenta creada. Verifica tu email antes de continuar.", Toast.LENGTH_LONG).show();

                                // Guardamos el usuario en Firestore
                                baseDeDatos.guardarUsuarioEnFirebase(nombreUsuario, email);

                                // Cerrar sesión y volver al login
                                firebaseAuth.signOut();
                                Intent intent = new Intent(this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Error al enviar email de verificación", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Error al crear el perfil de usuario", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ***************************************************************************************************


    // Verificamos si el usuario está autentificado al iniciar la app. Si es así, le llevamos a MainActivity
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
    }
}