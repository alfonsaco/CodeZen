package edu.alfonsaco.codezen.ui.habits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentHabitsBinding;

public class HabitsFragment extends Fragment implements HabitOptionsBottomSheet.HabitOptionsListener {

    private FragmentHabitsBinding binding;
    private List<Habit> listaHabitos;
    private HabitAdapter habitAdapter;
    private FloatingActionButton btnAgregarHabito;
    private RecyclerView recyclerHabitos;

    private final ActivityResultLauncher<Intent> launcherHabitos = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Habit nuevoHabito = (Habit) result.getData().getSerializableExtra("habito");
                    agregarHabitoALista(nuevoHabito);
                }
            }
    );

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

        return root;
    }

    private void agregarHabitoALista(Habit habitoNuevo) {
        listaHabitos.add(habitoNuevo);
        habitAdapter.notifyItemInserted(listaHabitos.size() - 1);
    }

    @Override
    public void interfazBorrarHabitoRecycler(int position) {
        if (position >= 0 && position < listaHabitos.size()) {
            listaHabitos.remove(position);
            habitAdapter.notifyItemRemoved(position);
            habitAdapter.notifyItemRangeChanged(position, listaHabitos.size() - position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}