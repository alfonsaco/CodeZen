package edu.alfonsaco.codezen.ui.meditation.meditate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.alfonsaco.codezen.R;

public class MeditacionAdapter extends RecyclerView.Adapter<MeditacionAdapter.ViewHolder> {

    private List<Meditation> listaMeditaciones;
    private Context context;

    public MeditacionAdapter(List<Meditation> listaMeditaciones, Context context) {
        this.listaMeditaciones = listaMeditaciones;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDia;
        TextView txtDuracion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDia=itemView.findViewById(R.id.txtDia);
            txtDuracion=itemView.findViewById(R.id.txtDuracion);
        }
    }

    @NonNull
    @Override
    public MeditacionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meditacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeditacionAdapter.ViewHolder holder, int position) {
        Meditation meditacion=listaMeditaciones.get(position);

        holder.txtDia.setText(meditacion.getFecha());
        holder.txtDuracion.setText(meditacion.getDuracion());
    }

    @Override
    public int getItemCount() {
        return listaMeditaciones.size();
    }
}
