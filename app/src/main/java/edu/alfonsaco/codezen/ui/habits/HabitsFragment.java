package edu.alfonsaco.codezen.ui.habits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.databinding.FragmentHabitsBinding;

public class HabitsFragment extends Fragment {

    private FragmentHabitsBinding binding;

    // Componentes
    private FloatingActionButton btnAgregarHabito;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Botón para agregar Hábito
        btnAgregarHabito= binding.btnAgregarHabito;
        btnAgregarHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Has pulsado en el botón", Toast.LENGTH_SHORT).show();
            }
        });
        btnAgregarHabito.setTooltipText("Crear un nuevo hábito");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}