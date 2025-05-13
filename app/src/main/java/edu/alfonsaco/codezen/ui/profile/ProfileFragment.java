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

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentProfileBinding;
import edu.alfonsaco.codezen.utils.BDD;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private BDD db;

    // Componentes
    private TextView txtNombreUsuario;
    private ImageView avatarUsuario;

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

        return root;
    }

    // Obtener la imagen del usuario
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}