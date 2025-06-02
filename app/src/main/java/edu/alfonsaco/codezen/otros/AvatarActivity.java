package edu.alfonsaco.codezen.otros;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

public class AvatarActivity extends AppCompatActivity {

    private BDD db;

    // Componentes
    private ImageView btnVolverInicio;
    private ImageView imagenAvatarSeleccionado;
    private ImageView avatar1;
    private ImageView avatar2;
    private ImageView avatar3;
    private ImageView avatar4;
    private ImageView avatar5;
    private ImageView avatar6;
    private Button btnGuardarNuevoAvatar;
    private EditText etxtUsername;

    // Variables
    private int idImagen;
    private ImageView[] avatares;
    private String nombreImagen;
    private String nuevoUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.avatar_activity);

        db=new BDD();

        // CERRAR ACTIVIDAD
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ************************ IMAGEN CIRCULAR POR DEFECTO **************************
        imagenAvatarSeleccionado=findViewById(R.id.imagenAvatarSeleccionado);
        obtenerAvatar();
        // *******************************************************************************


        // ****************************** CAMBIO DE IMAGEN *******************************
        avatar1=findViewById(R.id.avatar1);
        avatar2=findViewById(R.id.avatar2);
        avatar3=findViewById(R.id.avatar3);
        avatar4=findViewById(R.id.avatar4);
        avatar5=findViewById(R.id.avatar5);
        avatar6=findViewById(R.id.avatar6);

        avatares=new ImageView[]{avatar1, avatar2, avatar3, avatar4, avatar5, avatar6};

        for(ImageView avatar: avatares) {
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(ImageView img: avatares) {
                        img.setBackgroundResource(0);
                    }

                    avatar.setBackgroundResource(R.drawable.borde_avatar_seleccionado);
                    cambiarAvatar(avatar);
                }
            });
        }
        // *******************************************************************************


        btnGuardarNuevoAvatar=findViewById(R.id.btnGuardarNuevoAvatar);
        btnGuardarNuevoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar();
                guardarUsername();
            }
        });

        // *****************************+ CAMBIO USERNAME *********************************
        etxtUsername=findViewById(R.id.etxtUsername);
        obtenerUsername();
        // *******************************************************************************

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

    // CARGAR EL AVATAR CIRCULAR POR DEFECTO
    private void obtenerAvatar() {
        db.getDb()
                .collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String avatar=snapshot.getString("avatar");
                    nombreImagen=avatar;

                    idImagen=getResources().getIdentifier(avatar, "drawable", getPackageName());
                    imagenAvatarSeleccionado.setImageResource(idImagen);

                    // Imagen circular con borde
                    int colorBorde = resolveColorAttr(androidx.biometric.R.attr.colorPrimary);
                    Glide.with(this).load(idImagen)
                            .transform(new CropCircleWithBorderTransformation(8, colorBorde)).into(imagenAvatarSeleccionado);

                    // Ponerle el borde a la imagen seleccionada en la BDD
                    for (ImageView avatarView : avatares) {
                        if (avatarView.getTag().equals(nombreImagen)) {
                            avatarView.setBackgroundResource(R.drawable.borde_avatar_seleccionado);
                        } else {
                            avatarView.setBackgroundResource(0);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo cargar el avatar");
                });
    }

    // CAMBIAR DE AVATAR POR CLICK
    private void cambiarAvatar(ImageView imagen) {
        nombreImagen=(String) imagen.getTag();
        idImagen=getResources().getIdentifier((String) imagen.getTag(), "drawable", getPackageName());
        imagenAvatarSeleccionado.setImageResource(idImagen);

        // Imagen circular con borde
        int colorBorde = resolveColorAttr(androidx.biometric.R.attr.colorPrimary);
        Glide.with(this).load(idImagen)
                .transform(new CropCircleWithBorderTransformation(8, colorBorde)).into(imagenAvatarSeleccionado);
    }

    // GUARDAR AVATAR NUEVO
    private void guardarAvatar() {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .update("avatar", nombreImagen)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AVATAR", "Avatar actualizado correctamente");
                });
    }

    // CAMBIOS DE USERNAME
    private void obtenerUsername() {
        db.getDb()
                .collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String username=snapshot.getString("username");
                    etxtUsername.setText(username);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo cargar el avatar");
                });
    }

    private void guardarUsername() {
        nuevoUsername=String.valueOf(etxtUsername.getText());

        // Verificaciones
        if(nuevoUsername.length() < 3) {
            Toast.makeText(this, "El nombre de usuario debe contener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if(nuevoUsername.length() > 20) {
            Toast.makeText(this, "El nombre de usuario es demasiado largo", Toast.LENGTH_SHORT).show();
            return;
        }

        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .update("username", nuevoUsername)
                .addOnSuccessListener(aVoid -> {
                    Log.d("USERNAME", "Username actualizado correctamente");
                    setResult(RESULT_OK);
                    finish();
                });
    }
}