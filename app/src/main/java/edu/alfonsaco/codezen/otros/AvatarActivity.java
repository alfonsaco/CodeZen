package edu.alfonsaco.codezen.otros;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;

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

    // Variables
    private int idImagen;
    private ImageView[] avatares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_avatar);

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
                    cambiarAvatar(avatar);
                }
            });
        }
        // *******************************************************************************


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // CARGAR EL AVATAR CIRCULAR POR DEFECTO
    private void obtenerAvatar() {
        db.getDb()
                .collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String avatar=snapshot.getString("avatar");

                    idImagen=getResources().getIdentifier(avatar, "drawable", getPackageName());
                    imagenAvatarSeleccionado.setImageResource(idImagen);

                    Glide.with(this).load(idImagen)
                            .circleCrop().into(imagenAvatarSeleccionado);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo cargar el avatar");
                });
    }

    // CAMBIAR DE AVATAR POR CLICK
    private void cambiarAvatar(ImageView imagen) {
        idImagen=getResources().getIdentifier((String) imagen.getTag(), "drawable", getPackageName());
        imagenAvatarSeleccionado.setImageResource(idImagen);

        Glide.with(this).load(idImagen)
                .circleCrop().into(imagenAvatarSeleccionado);
    }
}