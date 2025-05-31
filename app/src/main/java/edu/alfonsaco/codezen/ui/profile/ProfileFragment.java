package edu.alfonsaco.codezen.ui.profile;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
                        int cantidadColoreados=0;

                        // NIVEL 0
                        if(contLogros < 2) {
                            if(contLogros == 0) {
                                cantidadColoreados=0;
                            }
                            if(contLogros == 1) {
                                cantidadColoreados=1;
                            }
                            agregarViewsProgressBar(2, cantidadColoreados);

                        // NIVEL 1
                        } else if(contLogros > 1 && contLogros <= 5) {
                            cantidadColoreados=contLogros - 1;
                            agregarViewsProgressBar(4, cantidadColoreados);

                        // NIVEL 2
                        } else if(contLogros > 5 && contLogros <= 10) {
                            cantidadColoreados=contLogros - 5;
                            agregarViewsProgressBar(5, cantidadColoreados);

                        // NIVEL 3
                        } else if(contLogros > 10 && contLogros <= 15) {
                            cantidadColoreados=contLogros - 10;
                            agregarViewsProgressBar(5, cantidadColoreados);

                        // NIVEL 4
                        } else if(contLogros > 15 && contLogros <= 21) {
                            cantidadColoreados=contLogros - 15;
                            agregarViewsProgressBar(6, cantidadColoreados);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error fatal al obtener el número de logros");
                });
    }

    private void agregarViewsProgressBar(int cantidad, int coloreados) {
        progressBarNivel.removeAllViews();
        int cantidadColorearRestante=coloreados;
        boolean colorear;

        for(int i=0; i < cantidad; i++) {
            // Determinamos si colorear o no
            if(cantidadColorearRestante > 0) {
                cantidadColorearRestante--;
                colorear=true;
            } else {
                colorear=false;
            }

            // Contenedor para cada segmento con fondo y efecto
            FrameLayout frame = new FrameLayout(getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            );
            frame.setLayoutParams(layoutParams);

            frame.setBackgroundResource(R.drawable.fondo_casilla_nivel_no_cumplido);

            if(colorear) {
                frame.setBackgroundResource(R.drawable.fondo_casilla_nivel);

                // ************* VIEW INTERNO MEJORA EFECTO ***********************
                View viewInterno=new View(getContext());

                // Altura
                int alto20dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

                FrameLayout.LayoutParams internoParams = new FrameLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        alto20dp
                );

                int margenSuperiorDp = 17;
                int margenSuperiorPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, margenSuperiorDp, getResources().getDisplayMetrics());

                internoParams.setMargins(0, margenSuperiorPx, 0, 0);
                viewInterno.setLayoutParams(internoParams);

                viewInterno.setBackgroundColor(Color.WHITE);
                viewInterno.setAlpha(0.3f);

                frame.addView(viewInterno);
                // *+***************************************************************
            }

            // Añadir el frame al progressBarNivel
            progressBarNivel.addView(frame);
        }
    }
    // *********************************************************************************************

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}