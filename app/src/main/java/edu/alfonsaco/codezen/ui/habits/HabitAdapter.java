package edu.alfonsaco.codezen.ui.habits;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.alfonsaco.codezen.R;

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

        public ViewHolder(@NonNull View itemView, HabitOptionsBottomSheet.HabitOptionsListener listener) {
            super(itemView);
            this.listener = listener;
            txtNombreHabito = itemView.findViewById(R.id.txtNombreHabito);
            txtDescripcionHabito = itemView.findViewById(R.id.txtDescripcionHabito);
            btnHabitoCompletado = itemView.findViewById(R.id.btnHabitoCompletado);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ShowHabitActivity.class);
                itemView.getContext().startActivity(intent);
            });

            itemView.setOnLongClickListener(v -> {
                HabitOptionsBottomSheet sheet = HabitOptionsBottomSheet.newInstance(
                        idHabito,
                        getAdapterPosition(),
                        listener
                );
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
        holder.txtNombreHabito.setText(habito.getNombre());
        holder.txtDescripcionHabito.setText(habito.getDescripcion());
        holder.btnHabitoCompletado.setBackgroundColor(Color.parseColor(habito.getColor()));
        holder.idHabito = "ID_habit_" + habito.getNombre();
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }
}