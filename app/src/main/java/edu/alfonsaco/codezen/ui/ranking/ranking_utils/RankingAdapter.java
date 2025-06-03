package edu.alfonsaco.codezen.ui.ranking.ranking_utils;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import edu.alfonsaco.codezen.R;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;

    public RankingAdapter(List<Usuario> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsernameRanking;
        TextView txtPosicion;
        TextView txtCantidadLogros;
        ImageView imagenMedalla;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUsernameRanking=itemView.findViewById(R.id.txtUsernameRanking);
            txtPosicion=itemView.findViewById(R.id.txtPosicion);
            txtCantidadLogros=itemView.findViewById(R.id.txtCantidadLogros);
            imagenMedalla=itemView.findViewById(R.id.imagenMedalla);
        }
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false);
        return new RankingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        Usuario usuario=listaUsuarios.get(position);

        // Datos
        holder.txtPosicion.setText(String.valueOf(position+1));
        holder.txtCantidadLogros.setText(String.valueOf(usuario.getContLogros()));
        holder.txtUsernameRanking.setText(usuario.getUsername());

        int nivel=usuario.getNivel();

        if(nivel == 0) {
            holder.imagenMedalla.setImageResource(R.drawable.lvl0);
        } else if(nivel == 1) {
            holder.imagenMedalla.setImageResource(R.drawable.lvl1);
        } else if(nivel == 2) {
            holder.imagenMedalla.setImageResource(R.drawable.lvl2);
        } else if(nivel == 3) {
            holder.imagenMedalla.setImageResource(R.drawable.lvl3);
        } else if(nivel == 4) {
            holder.imagenMedalla.setImageResource(R.drawable.lvl4);
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }
}
