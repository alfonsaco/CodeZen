package edu.alfonsaco.codezen.ui.habits.habit_utils;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import edu.alfonsaco.codezen.ui.habits.EditHabitActivity;
import edu.alfonsaco.codezen.utils.BDD;

public class HabitOptionsBottomSheet extends BottomSheetDialogFragment {

    // Componentes
    private ImageView btnCloseSheet;
    private LinearLayout btnEliminarHabito;
    private LinearLayout btnIrAEditarHabito;

    // Clases
    private HabitOptionsListener listener;
    private BDD bd;

    // CONFIGURAR INTERFAZ DEL BOTTOM SHEET
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
            dismiss();

            // Dialog personalizado
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_borrar, null);
            builder.setView(dialogView);


            AlertDialog alert=builder.create();
            alert.show();

            Button btnBorrar=alert.findViewById(R.id.btnBorrar);
            Button btnCancelar=alert.findViewById(R.id.btnCancelar);

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bd.borrarHabito(idHabito);
                    if (listener != null) {
                        listener.interfazBorrarHabitoRecycler(posicion);
                    }
                    alert.dismiss();
                }
            });
        });

        btnCloseSheet.setOnClickListener(v -> dismiss());

        return view;
    }

    // INTERFAZ DE BORRAR HÁBITOS DEL RECYCLERVIEW
    public interface HabitOptionsListener {
        void interfazBorrarHabitoRecycler(int position);
    }
}