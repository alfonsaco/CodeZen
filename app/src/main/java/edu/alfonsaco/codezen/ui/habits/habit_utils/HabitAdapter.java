package edu.alfonsaco.codezen.ui.habits.habit_utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.alfonsaco.codezen.R;
import edu.alfonsaco.codezen.ui.habits.ShowHabitActivity;
import edu.alfonsaco.codezen.utils.BDD;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private List<Habit> listaHabitos;
    private final Context context;
    private final HabitOptionsBottomSheet.HabitOptionsListener listener;

    public HabitAdapter(List<Habit> listaHabitos, Context context, HabitOptionsBottomSheet.HabitOptionsListener listener) {
        this.listaHabitos = listaHabitos;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreHabito;
        TextView txtDescripcionHabito;
        MaterialButton btnHabitoCompletado;
        RecyclerView recyclerDiasHabito;

        String idHabito;
        String color;
        HabitOptionsBottomSheet.HabitOptionsListener listener;
        private BDD db;
        private FirebaseFirestore firestore;
        private ListenerRegistration diasListener;
        private List<Day> listaDias;
        private DayAdapter dayAdapter;

        public ViewHolder(@NonNull View itemView, HabitOptionsBottomSheet.HabitOptionsListener listener) {
            super(itemView);
            this.listener = listener;
            txtNombreHabito = itemView.findViewById(R.id.txtNombreHabito);
            txtDescripcionHabito = itemView.findViewById(R.id.txtDescripcionHabito);
            btnHabitoCompletado = itemView.findViewById(R.id.btnHabitoCompletado);
            recyclerDiasHabito = itemView.findViewById(R.id.recyclerDiasHabito);

            db = new BDD();
            firestore = FirebaseFirestore.getInstance();
            listaDias = new ArrayList<>();

            // Configurar RecyclerView de días
            recyclerDiasHabito.setLayoutManager(new GridLayoutManager(itemView.getContext(), 7, GridLayoutManager.HORIZONTAL, false));
            dayAdapter = new DayAdapter(listaDias, color);
            recyclerDiasHabito.setAdapter(dayAdapter);

            // Click para abrir detalles del hábito
            itemView.setOnClickListener(v -> {
                String nombre = txtNombreHabito.getText().toString();
                String descripcion = txtDescripcionHabito.getText().toString();

                Intent intent = new Intent(itemView.getContext(), ShowHabitActivity.class);
                intent.putExtra("id", idHabito);
                intent.putExtra("nombre", nombre);
                intent.putExtra("descripcion", descripcion);
                intent.putExtra("color", color);
                itemView.getContext().startActivity(intent);
            });

            // Long click para opciones
            itemView.setOnLongClickListener(v -> {
                HabitOptionsBottomSheet sheet = HabitOptionsBottomSheet.newInstance(idHabito, getAdapterPosition(), listener);
                if (itemView.getContext() instanceof AppCompatActivity) {
                    sheet.show(((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(), "HabitBottomSheet");
                }
                return true;
            });

            // Botón de completado
            btnHabitoCompletado.setOnClickListener(v -> {
                if (idHabito != null) {
                    LocalDate hoy = LocalDate.now();
                    String fecha = hoy.toString();

                    db.verificarCompletadoBoolean(fecha, idHabito, completado -> {
                        if (completado) {
                            db.cambiarEstadoDia(false, idHabito, fecha);
                            btnHabitoCompletado.setAlpha(0.4f);
                        } else {
                            db.cambiarEstadoDia(true, idHabito, fecha);
                            btnHabitoCompletado.setAlpha(1f);
                        }
                    });
                }
            });
        }

        public void setupDiasListener(String habitId) {
            // Limpiar listener anterior si existe
            if (diasListener != null) {
                diasListener.remove();
            }

            if (habitId == null) return;

            diasListener = firestore.collection("usuarios")
                    .document(db.getUsuarioID())
                    .collection("habitos")
                    .document(habitId)
                    .collection("dias")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("DiasListener", "Error en listener", error);
                            return;
                        }

                        if (value != null) {
                            listaDias.clear();
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                Day dia = doc.toObject(Day.class);
                                if (dia != null) {
                                    listaDias.add(dia);
                                }
                            }
                            // Ordenar los días por fecha
                            Collections.sort(listaDias, (d1, d2) -> d1.getId().compareTo(d2.getId()));
                            dayAdapter.updateDays(listaDias);
                            recyclerDiasHabito.scrollToPosition(dayAdapter.getItemCount() - 1);
                        }
                    });
        }

        public void actualizarEstadoBoton() {
            if (idHabito != null) {
                LocalDate hoy = LocalDate.now();
                String fecha = hoy.toString();

                db.verificarCompletadoBoolean(fecha, idHabito, completado -> {
                    if (completado) {
                        btnHabitoCompletado.setAlpha(1f);
                    } else {
                        btnHabitoCompletado.setAlpha(0.4f);
                    }
                });
            }
        }

        private void verificarHoyAnadido(LocalDate fechaHoy) {
            String fecha = fechaHoy.toString();

            db.verificarExistenciaDia(idHabito, fecha, existe -> {
                if (!existe) {
                    Day dia = new Day(false, fecha, color);
                    db.anadirDia(dia, idHabito);
                    verificarHoyAnadido(fechaHoy.minusDays(1));
                }
            });
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            if (diasListener != null) {
                diasListener.remove();
            }
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
        holder.color = habito.getColor();
        holder.dayAdapter.setColorHabito(habito.getColor());

        holder.actualizarEstadoBoton();

        // Configurar listener y verificar días
        holder.setupDiasListener(habito.getId());

        // Verificar días en segundo plano
        new Thread(() -> {
            LocalDate hoy = LocalDate.now();
            holder.verificarHoyAnadido(hoy);
            holder.actualizarEstadoBoton();
        }).start();
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    public void updateHabits(List<Habit> newHabits) {
        this.listaHabitos = newHabits;
        notifyDataSetChanged();
    }
}