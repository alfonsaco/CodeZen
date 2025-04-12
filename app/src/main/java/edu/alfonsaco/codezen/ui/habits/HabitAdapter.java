package edu.alfonsaco.codezen.ui.habits;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;

import java.util.List;

import edu.alfonsaco.codezen.R;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private List<Habit> listaHabitos;
    private static Context context;

    public HabitAdapter(List<Habit> listaHabitos, Context context) {
        this.listaHabitos = listaHabitos;
        this.context = context;
    }

    // Para declarar los objetos
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreHabito;
        TextView txtDescripcionHabito;
        Button btnHabitoCompletado;
        String idHabito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreHabito = itemView.findViewById(R.id.txtNombreHabito);
            txtDescripcionHabito = itemView.findViewById(R.id.txtDescripcionHabito);
            btnHabitoCompletado = itemView.findViewById(R.id.btnHabitoCompletado);

            // Mostrar hábitos y estadísticas
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ShowHabitActivity.class);
                    context.startActivity(intent);
                }
            });

            // Mostrar opciones de editar o borrar hábito
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    HabitOptionsBottomSheet habitOptionsBottomSheet=new HabitOptionsBottomSheet();
                    Bundle bundle=new Bundle();
                    bundle.putString("id", idHabito);
                    habitOptionsBottomSheet.setArguments(bundle);

                    if (context instanceof AppCompatActivity) {
                        habitOptionsBottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "HabitBottomSheet");
                    } else {
                        Toast.makeText(context, "No se pudo abrir el menú", Toast.LENGTH_SHORT).show();
                    }

                    return false;
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
        Habit habito=listaHabitos.get(position);
        holder.txtNombreHabito.setText(habito.getNombre());
        holder.txtDescripcionHabito.setText(habito.getDescripcion());
        holder.btnHabitoCompletado.setBackgroundColor(Color.parseColor(habito.getColor()));
        holder.idHabito="ID_habit_"+habito.getNombre();
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }
}
