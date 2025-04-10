package edu.alfonsaco.codezen.ui.habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.alfonsaco.codezen.R;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private List<Habit> listaHabitos;
    private Context context;

    public HabitAdapter(List<Habit> listaHabitos) {
        this.listaHabitos = listaHabitos;
        this.context = context;
    }

    // Para declarar los objetos
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreHabito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreHabito = itemView.findViewById(R.id.txtNombreHabito);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Has pulsado en un hábito", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NonNull
    @Override
    public HabitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitAdapter.ViewHolder holder, int position) {

        holder.txtNombreHabito.setText("TEXTO DE EJEMPLO");


    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }
}
