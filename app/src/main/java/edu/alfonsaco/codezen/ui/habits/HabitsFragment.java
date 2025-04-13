package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentHabitsBinding;
import edu.alfonsaco.codezen.utils.BDD;

public class HabitsFragment extends Fragment implements HabitOptionsBottomSheet.HabitOptionsListener {

    private FragmentHabitsBinding binding;

    // Obtener hábitos usuario
    private BDD db;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // RecyclerView
    private List<Habit> listaHabitos;
    private HabitAdapter habitAdapter;

    // Componentes
    private FloatingActionButton btnAgregarHabito;
    private RecyclerView recyclerHabitos;
    private ProgressBar progressBarHabitos;
    private TextView txtCreaTuPrimerHabito;

    private final ActivityResultLauncher<Intent> launcherHabitos = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Habit nuevoHabito = (Habit) result.getData().getSerializableExtra("habito");
                    agregarHabitoALista(nuevoHabito);
                }
            }
    );

    // AGREGAR TODOS LOS HÁBITOS DEL USUARIO DE LA BDD AL INICIAR LA APP
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=new BDD();
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        listaHabitos=new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listaHabitos = new ArrayList<>();
        recyclerHabitos = binding.recyclerHabitos;
        habitAdapter = new HabitAdapter(listaHabitos, requireContext(), this);
        recyclerHabitos.setAdapter(habitAdapter);
        recyclerHabitos.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAgregarHabito = binding.btnAgregarHabito;
        btnAgregarHabito.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateHabitActivity.class);
            launcherHabitos.launch(intent);
        });
        btnAgregarHabito.setTooltipText("Crear un nuevo hábito");

        cargarHabitosUsuario();

        return root;
    }

    // ********************** CARGAR HÁBITOS DESDE LA BDD **************************
    private void cargarHabitosUsuario() {
        progressBarHabitos=binding.progressBarHabitos;
        txtCreaTuPrimerHabito=binding.txtCreaTuPrimerHabito;

        // Placeholder hasta que carguen los hábitos
        progressBarHabitos.setVisibility(View.VISIBLE);
        txtCreaTuPrimerHabito.setVisibility(View.GONE);
        recyclerHabitos.setVisibility(View.GONE);

        FirebaseUser usuario=firebaseAuth.getCurrentUser();
        if(usuario == null) {
            return;
        }

        firestore.collection("usuarios")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("habitos")
                .get()
                .addOnSuccessListener(query -> {
                    listaHabitos.clear();
                    for(DocumentSnapshot doc : query.getDocuments()) {
                        Habit habito=doc.toObject(Habit.class);
                        if(habito != null) {
                            listaHabitos.add(habito);
                        }
                        habitAdapter.notifyDataSetChanged();
                    }

                    // Quitamos el placeholder
                    progressBarHabitos.setVisibility(View.GONE);
                    recyclerHabitos.setVisibility(View.VISIBLE);

                    // Si no hay hábitos, saldrá el texto de crear tu primer hábito
                    if(listaHabitos.isEmpty()) {
                        txtCreaTuPrimerHabito.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> Log.e("Cargar hábitos", "Error al cargar los hábitos del usuario" + usuario.getDisplayName()));
    }
    // ***************************************************************************


    // ******************* ELIMINAR Y AGREGAR HÁBITOS ****************************
    private void agregarHabitoALista(Habit habitoNuevo) {
        listaHabitos.add(habitoNuevo);
        habitAdapter.notifyItemInserted(listaHabitos.size() - 1);
        txtCreaTuPrimerHabito.setVisibility(View.GONE);
    }

    @Override
    public void interfazBorrarHabitoRecycler(int position) {
        if (position >= 0 && position < listaHabitos.size()) {
            listaHabitos.remove(position);
            habitAdapter.notifyItemRemoved(position);
            habitAdapter.notifyItemRangeChanged(position, listaHabitos.size() - position);
            txtCreaTuPrimerHabito.setVisibility(View.VISIBLE);
        }
    }
    // ***************************************************************************

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}