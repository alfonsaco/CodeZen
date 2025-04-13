package edu.alfonsaco.codezen.ui.habits;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.utils.BDD;

public class HabitOptionsBottomSheet extends BottomSheetDialogFragment {

    // Componentes
    private ImageView btnCloseSheet;
    private LinearLayout btnEliminarHabito;
    private LinearLayout btnIrAEditarHabito;

    // Clases
    private HabitOptionsListener listener;
    private BDD bd;

    // INTERFAZ DEL BOTTOM SHEET
    public static HabitOptionsBottomSheet newInstance(String id, int position, HabitOptionsListener listener) {
        HabitOptionsBottomSheet fragment = new HabitOptionsBottomSheet();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putInt("posicion", position);
        fragment.setArguments(args);
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_habitos, container, false);

        btnEliminarHabito = view.findViewById(R.id.btnEliminarHabito);
        btnIrAEditarHabito = view.findViewById(R.id.btnIrAEditarHabito);
        btnCloseSheet = view.findViewById(R.id.btnCloseSheet);

        bd = new BDD();
        Bundle bundle = getArguments();
        String idHabito = bundle.getString("id");
        int posicion = bundle.getInt("posicion");

        btnIrAEditarHabito.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditHabitActivity.class);
            startActivity(intent);
            dismiss();
        });

        btnEliminarHabito.setOnClickListener(v -> {
            bd.borrarHabito(idHabito);
            if (listener != null) {
                listener.interfazBorrarHabitoRecycler(posicion);
            }
            dismiss();
        });

        btnCloseSheet.setOnClickListener(v -> dismiss());

        return view;
    }

    public interface HabitOptionsListener {
        void interfazBorrarHabitoRecycler(int position);
    }
}