package edu.alfonsaco.codezen.ui.profile;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.lang.reflect.Type;

import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentProfileBinding;
import edu.alfonsaco.codezen.ui.profile.profile_utils.Logro;
import edu.alfonsaco.codezen.ui.profile.profile_utils.LogrosAdapter;
import edu.alfonsaco.codezen.utils.BDD;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private BDD db;

    // Componentes
    private TextView txtNombreUsuario;
    private ImageView avatarUsuario;
    private ImageView imagenNivel;
    private LinearLayout progressBarNivel;

    // Recycler de logros
    private RecyclerView recyclerLogros;
    private LogrosAdapter adapterLogros;
    private ArrayList<Logro> listaLogros;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db=new BDD();

        // *********************** OBTENER DATOS DEL USUARIO *************************
        txtNombreUsuario=binding.txtNombreUsuario;
        avatarUsuario=binding.avatarUsuario;

        // Avatar
        txtNombreUsuario.setText(MainActivity.username);
        obtenerAvatar();

        // ProgressBar de nivel
        progressBarNivel=binding.progressBarNivel;
        llenarProgressBar();

        // Nivel
        imagenNivel=binding.imagenNivel;
        verificarNivelUsuario();
        // ***************************************************************************


        // RECYCLER
        recyclerLogros=binding.recyclerLogros;
        recyclerLogros.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listaLogros=new ArrayList<>();
        adapterLogros=new LogrosAdapter(listaLogros, this.getContext());
        recyclerLogros.setAdapter(adapterLogros);
        cargarLogros();

        return root;
    }

    // ******************************** CARGAR DATOS INICIALES *************************************
    private void obtenerAvatar() {
        db.getDb()
                .collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String avatar=snapshot.getString("avatar");

                    int idImagen=getResources().getIdentifier(avatar, "drawable", getActivity().getPackageName());
                    avatarUsuario.setImageResource(idImagen);

                    // Imagen circular con borde
                    Glide.with(this).load(idImagen).circleCrop().into(avatarUsuario);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo cargar el avatar");
                });
    }

    // Cargar los logros en el Recycler
    private void cargarLogros() {
        try {
            InputStream is=getContext().getAssets().open("logros.json");

            // Leemos el JSON
            int tamano=is.available();
            byte[] buffer = new byte[tamano];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            // Usamos la librería GSON
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Logro>>(){}.getType();
            ArrayList<Logro> logrosDesdeJSON = gson.fromJson(jsonString, listType);

            listaLogros.clear();
            listaLogros.addAll(logrosDesdeJSON);
            adapterLogros.notifyDataSetChanged();

            for(Logro logro : logrosDesdeJSON) {
                db.verificarExistenciaLogroBDD(logro.getId(), existe -> {
                    if(existe) {
                        logro.setDesbloqueado(true);
                    } else {
                        logro.setDesbloqueado(false);
                    }

                    int pos = listaLogros.indexOf(logro);
                    if (pos != -1) {
                        adapterLogros.notifyItemChanged(pos);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // *********************************************************************************************


    // ************* VERIFICAMOS EL NIVEL DEL USUARIO SEGÚN LA CANTIDAD DE LOGROS ******************
    private void verificarNivelUsuario() {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    long nivel=snapshot.getLong("nivel");

                    if(nivel == 0) {

                    } else if(nivel == 1) {
                        imagenNivel.setImageResource(R.drawable.lvl1);
                    } else if(nivel == 2) {
                        imagenNivel.setImageResource(R.drawable.lvl2);
                    } else if(nivel == 3) {
                        imagenNivel.setImageResource(R.drawable.lvl3);
                    } else if(nivel >= 4) {
                        imagenNivel.setImageResource(R.drawable.lvl4);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "Error al obtener el número de logros");
                });
    }

    private void llenarProgressBar() {
        db.getDb().collection("usuarios")
                .document(db.getUsuarioID())
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot snapshot=task.getResult();

                    if(snapshot.exists()) {
                        int contLogros=snapshot.getLong("cont_logros").intValue();

                        // NIVEL 0
                        if(contLogros < 2) {
                            agregarViewsProgressBar(2);

                        // NIVEL 1
                        } else if(contLogros > 1 && contLogros <= 5) {


                        // NIVEL 2
                        } else if(contLogros > 5 && contLogros <= 9) {


                        // NIVEL 3
                        } else if(contLogros > 9 && contLogros <= 14) {


                        // NIVEL 4
                        } else if(contLogros > 14 && contLogros <= 21) {

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error fatal al obtener el número de logros");
                });
    }

    private void agregarViewsProgressBar(int cantidad) {
        progressBarNivel.removeAllViews();

        for(int i=0; i < cantidad; i++) {
            View view=new View(getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0, // Ancho
                    LinearLayout.LayoutParams.MATCH_PARENT, // Alto
                    1f // Peso de 1 para cada sección
            );

            view.setLayoutParams(layoutParams);

            view.setBackgroundResource(R.drawable.borde_boton_auth_select);

            progressBarNivel.addView(view);
        }
    }
    // *********************************************************************************************

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}