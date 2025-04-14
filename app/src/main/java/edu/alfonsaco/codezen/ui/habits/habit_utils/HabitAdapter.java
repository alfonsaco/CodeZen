package edu.alfonsaco.codezen.ui.habits.habit_utils;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
                Intent intent = new Intent(itemView.getContext(), ShowHabitActivity.class);
                intent.putExtra("id", idHabito);
                intent.putExtra("posicion", getAdapterPosition());
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
        holder.idHabito = "ID_habit_" + habito.getNombre().replace(" ", "_");

        // Recycler de hábitos

        //List<Day> diasHabitos=habito.getDias();
        List<Day> diasHabitos=new ArrayList<>();

        // Rellenamos por defecto con una cantidad de días para que el Recycler no quede vacío + días de la semana actual
        int diaSemana=obtenerDiaSemana();
        for(int i=0; i< (196 + diaSemana); i++) {
            Day dia=new Day();
            diasHabitos.add(dia);
        }

        DayAdapter adapterDias=new DayAdapter(diasHabitos);
        holder.recyclerDiasHabito.setAdapter(adapterDias);
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    // Método para obtener el día de la semana, para saber cuantos Divs agregar
    private int obtenerDiaSemana() {
        Date dia=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dia);

        int diaSemana=calendar.get(Calendar.DAY_OF_WEEK);
        int diaHoy=0;

        switch (diaSemana) {
            case 1:
                diaHoy=7;
                break;
            case 2:
                diaHoy=1;
                break;
            case 3:
                diaHoy=2;
                break;
            case 4:
                diaHoy=3;
                break;
            case 5:
                diaHoy=4;
                break;
            case 6:
                diaHoy=5;
                break;
            case 7:
                diaHoy=6;
                break;
        }

        return diaHoy;
    }
}