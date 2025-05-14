package edu.alfonsaco.codezen.ui.profile;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
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

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private BDD db;

    // Componentes
    private TextView txtNombreUsuario;
    private ShapeableImageView avatarUsuario;

    // Recycler de logros
    private RecyclerView recyclerLogros;
    private LogrosAdapter adapterLogros;
    private ArrayList<Logro> listaLogros;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db=new BDD();

        // Obtener datos del usuario
        txtNombreUsuario=binding.txtNombreUsuario;
        avatarUsuario=binding.avatarUsuario;

        txtNombreUsuario.setText(MainActivity.username);
        obtenerAvatar();

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // *********************************************************************************************

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}