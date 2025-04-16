package edu.alfonsaco.codezen.ui.habits.habit_utils;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.zip.Inflater;

import edu.alfonsaco.codezen.R;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private List<Day> listaDias;
    private String colorHabito;

    // CONSTRUCTOR
    public DayAdapter(List<Day> listaDias, String colorHabito) {
        this.listaDias = listaDias;
        this.colorHabito = colorHabito;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        View diaView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            diaView=itemView.findViewById(R.id.dayIndicator);
        }
    }

    @NonNull
    @Override
    public DayAdapter.DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.DayViewHolder holder, int position) {
        Day dia=listaDias.get(position);
        String color=dia.getColor();

        if(dia.isCompletado()) {
            holder.diaView.setBackgroundColor(Color.parseColor(color));
            holder.diaView.setAlpha(1);
        } else {
            holder.diaView.setBackgroundColor(Color.parseColor(color));
            holder.diaView.setAlpha(0.3f);

            // borrar esto, solo es para pruebas
            if(position % 21 == 0 || position % 9 == 0) {
                // borrar esto, solo es para pruebas
                holder.diaView.setBackgroundColor(Color.parseColor(color));
                holder.diaView.setAlpha(1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaDias.size();
    }

    // Método para actualizar los datos
    public void actualizarRecycler(List<Day> nuevosDias, String color) {
        this.listaDias = nuevosDias;
        this.colorHabito = color;

        notifyDataSetChanged();
    }

}
