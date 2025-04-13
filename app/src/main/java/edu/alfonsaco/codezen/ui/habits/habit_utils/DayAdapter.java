package edu.alfonsaco.codezen.ui.habits.habit_utils;

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

    // CONSTRUCTOR
    public DayAdapter(List<Day> listaDias) {
        this.listaDias = listaDias;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);

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

    }

    @Override
    public int getItemCount() {
        return listaDias.size();
    }
}
