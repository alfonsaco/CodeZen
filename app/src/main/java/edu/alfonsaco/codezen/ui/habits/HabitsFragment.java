package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentHabitsBinding;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Day;
import edu.alfonsaco.codezen.ui.habits.habit_utils.Habit;
import edu.alfonsaco.codezen.ui.habits.habit_utils.HabitAdapter;
import edu.alfonsaco.codezen.ui.habits.habit_utils.HabitOptionsBottomSheet;
import edu.alfonsaco.codezen.utils.ArchievementsUnlocks;
import edu.alfonsaco.codezen.utils.BDD;

public class HabitsFragment extends Fragment implements HabitOptionsBottomSheet.HabitOptionsListener {

    private FragmentHabitsBinding binding;
    private BDD db;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private List<Habit> listaHabitos;
    private HabitAdapter habitAdapter;

    // Componentes
    private ImageButton btnAgregarHabito;
    private RecyclerView recyclerHabitos;
    private ProgressBar progressBarHabitos;
    private TextView txtCreaTuPrimerHabito;

    private ArchievementsUnlocks logros;
    private ActivityResultLauncher<Intent> createHabitLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new BDD();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        listaHabitos = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar componentes
        progressBarHabitos = binding.progressBarHabitos;
        txtCreaTuPrimerHabito = binding.txtCreaTuPrimerHabito;
        recyclerHabitos = binding.recyclerHabitos;
        btnAgregarHabito = binding.btnAgregarHabito;
        // Ocultamos el botón hasta que carguen los hábitos
        btnAgregarHabito.setVisibility(View.GONE);

        // Configurar RecyclerView
        habitAdapter = new HabitAdapter(listaHabitos, requireContext(), this);
        recyclerHabitos.setAdapter(habitAdapter);
        recyclerHabitos.setLayoutManager(new LinearLayoutManager(getContext()));

        // *************** VERIFICAR LOGRO CONSEGUIDO ******************
        logros=new ArchievementsUnlocks(db);
        // Registrar el launcher
        createHabitLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            logros.logrosHabitos(listaHabitos, getContext());
                        }
                    }
                });
        // Configurar botón de agregar
        btnAgregarHabito.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateHabitActivity.class);
            createHabitLauncher.launch(intent);
        });
        btnAgregarHabito.setTooltipText("Crear un nuevo hábito");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarHabitosIniciales();
        setupFirestoreListener();
    }

    private void cargarHabitosIniciales() {
        mostrarLoading(true);

        FirebaseUser usuario = firebaseAuth.getCurrentUser();
        if (usuario == null) return;

        firestore.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .get()
                .addOnSuccessListener(query -> {
                    listaHabitos.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        procesarDocumentoHabito(doc);
                    }
                    actualizarVisibilidad();
                })
                .addOnFailureListener(e -> {
                    Log.e("HabitsFragment", "Error al cargar hábitos", e);
                    mostrarLoading(false);
                });
    }

    // ********************* ACTUALIZAR EN TIEMPO REAL EL RECYCLER, VERIFICANDO LA BDD ***************************
    private void setupFirestoreListener() {
        FirebaseUser usuario = firebaseAuth.getCurrentUser();
        if (usuario == null) return;

        firestore.collection("usuarios")
                .document(usuario.getUid())
                .collection("habitos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("HabitsFragment", "Error en listener", error);
                        return;
                    }

                    // Evitamos que se actualice en el caso de borrar, para evitar problemas con la animación de eliminación
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    procesarDocumentoHabito(dc.getDocument());
                                    break;
                                case MODIFIED:
                                    actualizarHabitoExistente(dc.getDocument());
                                    break;
                                case REMOVED:
                                    eliminarHabito(dc.getDocument().getId());
                                    break;
                            }
                        }
                    }
                });
    }

    private void procesarDocumentoHabito(DocumentSnapshot doc) {
        Habit habito = doc.toObject(Habit.class);
        if (habito != null) {
            doc.getReference().collection("dias").get()
                    .addOnSuccessListener(diasSnapshot -> {
                        List<Day> dias = new ArrayList<>();
                        for (DocumentSnapshot diaDoc : diasSnapshot.getDocuments()) {
                            Day dia = diaDoc.toObject(Day.class);
                            if (dia != null) dias.add(dia);
                        }
                        habito.setDias(dias);

                        // Verificar si ya existe para evitar duplicados
                        int posicionExistente = encontrarPosicionHabito(habito.getId());
                        if (posicionExistente == -1) {
                            listaHabitos.add(habito);
                            habitAdapter.notifyItemInserted(listaHabitos.size() - 1);
                        } else {
                            listaHabitos.set(posicionExistente, habito);
                            habitAdapter.notifyItemChanged(posicionExistente);
                        }

                        actualizarVisibilidad();
                    });
        }
    }
    // **************************************************************************************************

    //
    private void actualizarHabitoExistente(DocumentSnapshot doc) {
        Habit habito = doc.toObject(Habit.class);
        if (habito != null) {
            int posicion = encontrarPosicionHabito(habito.getId());
            if (posicion != -1) {
                doc.getReference().collection("dias").get()
                        .addOnSuccessListener(diasSnapshot -> {
                            List<Day> dias = new ArrayList<>();
                            for (DocumentSnapshot diaDoc : diasSnapshot.getDocuments()) {
                                Day dia = diaDoc.toObject(Day.class);
                                if (dia != null) dias.add(dia);
                            }
                            habito.setDias(dias);
                            listaHabitos.set(posicion, habito);
                            habitAdapter.notifyItemChanged(posicion);
                        });
            }
        }
    }

    private void eliminarHabito(String idHabito) {
        int posicion = encontrarPosicionHabito(idHabito);
        if (posicion != -1) {
            listaHabitos.remove(posicion);
            habitAdapter.notifyItemRemoved(posicion);
            habitAdapter.notifyItemRangeChanged(posicion, listaHabitos.size() - posicion);
            actualizarVisibilidad();
        }
    }

    private int encontrarPosicionHabito(String idHabito) {
        for (int i = 0; i < listaHabitos.size(); i++) {
            if (listaHabitos.get(i).getId().equals(idHabito)) {
                return i;
            }
        }
        return -1;
    }

    private void actualizarVisibilidad() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                progressBarHabitos.setVisibility(View.GONE);
                btnAgregarHabito.setVisibility(View.VISIBLE);
                recyclerHabitos.setVisibility(listaHabitos.isEmpty() ? View.GONE : View.VISIBLE);
                txtCreaTuPrimerHabito.setVisibility(listaHabitos.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }
    }

    private void mostrarLoading(boolean mostrar) {
        progressBarHabitos.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        recyclerHabitos.setVisibility(mostrar ? View.GONE : View.VISIBLE);
        txtCreaTuPrimerHabito.setVisibility(View.GONE);
    }

    @Override
    public void interfazBorrarHabitoRecycler(int posicion) {
        actualizarVisibilidad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}