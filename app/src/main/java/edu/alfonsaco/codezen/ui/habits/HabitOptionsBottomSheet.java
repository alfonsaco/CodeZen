package edu.alfonsaco.codezen.ui.habits;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.alfonsaco.codezen.R;

// DIÁLOGO BOTTOM SHEET QUE SE MUESTRA AL HACER ON LONG CLICK EN CADA HÁBITO
public class HabitOptionsBottomSheet extends BottomSheetDialogFragment {

    private ImageView btnCloseSheet;
    private LinearLayout btnEliminarHabito;
    private LinearLayout btnIrAEditarHabito;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet_dialog_habitos, container, false);

        btnEliminarHabito=view.findViewById(R.id.btnEliminarHabito);
        btnIrAEditarHabito=view.findViewById(R.id.btnIrAEditarHabito);

        //EDITAR HÁBITO
        btnIrAEditarHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), EditHabitActivity.class);
                startActivity(intent);
                dismiss();
            }
        });

        // ELIMINAR HÁBITO
        btnEliminarHabito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // CERRAR DIÁLOGO
        btnCloseSheet=view.findViewById(R.id.btnCloseSheet);
        btnCloseSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
