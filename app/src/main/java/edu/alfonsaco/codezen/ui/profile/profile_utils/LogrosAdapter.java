package edu.alfonsaco.codezen.ui.profile.profile_utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.alfonsaco.codezen.R;

public class LogrosAdapter extends RecyclerView.Adapter<LogrosAdapter.ViewHolder> {

    private List<Logro> listaLogros;
    private Context context;

    public LogrosAdapter(List<Logro> listaMeditaciones, Context context) {
        this.listaLogros = listaMeditaciones;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreLogro;
        TextView txtDescripcionLogro;
        ImageView imagenLogro;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombreLogro=itemView.findViewById(R.id.txtNombreLogro);
            txtDescripcionLogro=itemView.findViewById(R.id.txtDescripcionLogro);
            imagenLogro=itemView.findViewById(R.id.imagenLogro);
        }
    }

    @NonNull
    @Override
    public LogrosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_logro, parent, false);
        return new LogrosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogrosAdapter.ViewHolder holder, int position) {
        Logro logro= listaLogros.get(position);

        holder.txtNombreLogro.setText(logro.getNombre());
        holder.txtDescripcionLogro.setText(logro.getDescripcion());

        String rutaImagen=logro.getRuta();
        int idImagen=context.getResources().getIdentifier(rutaImagen, "drawable", context.getPackageName());
        holder.imagenLogro.setImageResource(idImagen);
    }

    @Override
    public int getItemCount() {
        return listaLogros.size();
    }
}
