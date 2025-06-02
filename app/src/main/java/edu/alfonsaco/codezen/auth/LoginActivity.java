package edu.alfonsaco.codezen.auth;

import android.content.Intent;
import android.os.Bundle;
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

import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.Verifications;

public class LoginActivity extends AppCompatActivity {

    private Button btnCambiarARegistro;
    private Button btnLogin;
    private ImageView btnVolverInicio;
    private TextView txtBotonRecuperarContra;
    private ImageView iconoApp;

    // Firebase
    private SignInButton btnGoogleIniciar;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    // Campos de texto
    private EditText etxtEmail;
    private EditText etxtContra;

    // Verificar email
    private Verifications verifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_login_activity);

        verifications=new Verifications();

        iconoApp=findViewById(R.id.iconoApp);
        Glide.with(this).load(R.mipmap.ic_launcher).circleCrop().into(iconoApp);

        // Cambiar al Registro
        btnCambiarARegistro=findViewById(R.id.btnCambiarARegistro);
        btnCambiarARegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailContra();
            }
        });


        // ********** INICIO DE SESIÓN EN FIREBASE ***********
        firebaseAuth=FirebaseAuth.getInstance();

        // Google
        btnGoogleIniciar=findViewById(R.id.btnGoogleIniciar);
        // Cambiar texto del botón
        ((TextView) btnGoogleIniciar.getChildAt(0)).setText("Inicia sesión con Google");
        btnGoogleIniciar.setOnClickListener(new View.OnClickListener() {
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

        // Email y Contraseña
        etxtEmail=findViewById(R.id.etxtEmail);
        etxtContra=findViewById(R.id.etxtContra);
        // *******************************************************

        // Recuperar contraseña
        txtBotonRecuperarContra=findViewById(R.id.txtBotonRecuperarContra);
        txtBotonRecuperarContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, PasswordRecoverActivity.class);
                startActivity(intent);
            }
        });


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

    // ************************* MÉTODOS DE INICIO DE SESIÓN EN FIREBASE ******************************
    // Google
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

                        String emailUsuario=firebaseAuth.getCurrentUser().getEmail();
                        String nombreUsuario=firebaseAuth.getCurrentUser().getDisplayName();
                        Intent intent=new Intent(this, MainActivity.class);
                        finish();

                    } else {
                        Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Email y contraseña
    private void loginEmailContra() {
        String email=String.valueOf(etxtEmail.getText());
        String contra=String.valueOf(etxtContra.getText());

        // Verificaciones
        if(email.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!verifications.esEmail(email)) {
            Toast.makeText(this, "El email insertado no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Iniciar sesión
        firebaseAuth.signInWithEmailAndPassword(email, contra)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                // Email verificado
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                // Email no verificado → Forzar logout y mostrar aviso
                                firebaseAuth.signOut();
                                Toast.makeText(this,
                                        "Debes verificar tu email",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        // Error de autenticación
                        Toast.makeText(this,
                                "Credenciales incorrectas o usuario no registrado",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // ***************************************************************************************************


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioActual= firebaseAuth.getCurrentUser();

        if(usuarioActual != null && usuarioActual.isEmailVerified()) {
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}