package edu.alfonsaco.codezen.ui.habits.habit_utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.ui.habits.ShowHabitActivity;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private List<Habit> listaHabitos;
    private Context context;
    private final HabitOptionsBottomSheet.HabitOptionsListener listener;

    public HabitAdapter(List<Habit> listaHabitos, Context context, HabitOptionsBottomSheet.HabitOptionsListener listener) {
        this.listaHabitos = listaHabitos;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreHabito;
        TextView txtDescripcionHabito;
        Button btnHabitoCompletado;

        String idHabito;
        HabitOptionsBottomSheet.HabitOptionsListener listener;

        RecyclerView recyclerDiasHabito;

        public ViewHolder(@NonNull View itemView, HabitOptionsBottomSheet.HabitOptionsListener listener) {
            super(itemView);
            this.listener = listener;
            txtNombreHabito = itemView.findViewById(R.id.txtNombreHabito);
            txtDescripcionHabito = itemView.findViewById(R.id.txtDescripcionHabito);
            btnHabitoCompletado = itemView.findViewById(R.id.btnHabitoCompletado);

            // Recycler de dias de cada hábito
            recyclerDiasHabito=itemView.findViewById(R.id.recyclerDiasHabito);
            recyclerDiasHabito.setLayoutManager(new GridLayoutManager(itemView.getContext(), 7, GridLayoutManager.HORIZONTAL, false));


            // VAMOS A SHOWACTIVITY
            itemView.setOnClickListener(v -> {
                String nombre=txtNombreHabito.getText().toString();
                String descripcion=txtDescripcionHabito.getText().toString();

                Intent intent = new Intent(itemView.getContext(), ShowHabitActivity.class);
                intent.putExtra("id", idHabito);
                intent.putExtra("nombre", nombre);
                intent.putExtra("descripcion", descripcion);
                itemView.getContext().startActivity(intent);
            });

            // ABRIMOS OPCIONES DE ELIMINAR Y EDITAR HÁBITO
            itemView.setOnLongClickListener(v -> {
                HabitOptionsBottomSheet sheet = HabitOptionsBottomSheet.newInstance(idHabito, getAdapterPosition(), listener);

                if (itemView.getContext() instanceof AppCompatActivity) {
                    sheet.show(((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(), "HabitBottomSheet");
                }
                return true;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habito = listaHabitos.get(position);
        holder.txtNombreHabito.setText(habito.getNombre().toUpperCase());
        holder.txtDescripcionHabito.setText(habito.getDescripcion());
        holder.btnHabitoCompletado.setBackgroundColor(Color.parseColor(habito.getColor()));
        holder.idHabito = habito.getId();

        // FECHAS DE DÍAS DE HÁBITOS
        /*
        DayAdapter adapter = (DayAdapter) holder.recyclerDiasHabito.getAdapter();
        if (adapter == null) {
            adapter = new DayAdapter(habito.getDias(), habito.getColor());
            holder.recyclerDiasHabito.setAdapter(adapter);
        } else {
            adapter.actualizarRecycler(habito.getDias(), habito.getColor());
        }*/
        DayAdapter adaptar=new DayAdapter(habito.getDias(), habito.getColor());
        holder.recyclerDiasHabito.setAdapter(adaptar);
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }


}