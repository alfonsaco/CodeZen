package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentHabitsBinding;

public class HabitsFragment extends Fragment {

    private FragmentHabitsBinding binding;
    private List<Habit> listaHabitos;
    private HabitAdapter habitAdapter;

    // Launcher para agregar nuevos elementos al Combo
    private final ActivityResultLauncher<Intent> launcherHabitos = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Habit nuevoHabito = (Habit) result.getData().getSerializableExtra("habito");
                    agregarHabitoALista(nuevoHabito);
                }
            }
    );

    // Componentes
    private FloatingActionButton btnAgregarHabito;
    private RecyclerView recyclerHabitos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Botón para agregar Hábito
        btnAgregarHabito= binding.btnAgregarHabito;
        btnAgregarHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(requireActivity(), CreateHabitActivity.class);
                launcherHabitos.launch(intent);
            }
        });
        btnAgregarHabito.setTooltipText("Crear un nuevo hábito");


        // --------- CONFIGURAR RECYCLERVIEW ----------
        recyclerHabitos=binding.recyclerHabitos;
        listaHabitos=new ArrayList<>();

        recyclerHabitos.setLayoutManager(new LinearLayoutManager(getContext()));
        // --------------------------------------------

        return root;
    }

    private void agregarHabitoALista(Habit habitoNuevo) {
        listaHabitos.add(habitoNuevo);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}