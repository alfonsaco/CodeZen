package edu.alfonsaco.codezen.ui.meditation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.alfonsaco.codezen.databinding.FragmentMeditateBinding;
import edu.alfonsaco.codezen.ui.meditation.meditate.MeditationCreateActivity;

public class MeditateFragment extends Fragment {

    private FragmentMeditateBinding binding;

    private LinearLayout irAMeditar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeditateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // IR A LA MEDITACIÓN
        irAMeditar=binding.irAMeditar;
        irAMeditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MeditationCreateActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}