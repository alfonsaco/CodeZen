package edu.alfonsaco.codezen.ui.meditation.meditate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.alfonsaco.codezen.R;

public class SpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    private Context context;
    private List<SpinnerItem> items;

    public SpinnerAdapter(Context context, List<SpinnerItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner_musica, parent, false);
        }

        SpinnerItem item = items.get(position);

        ImageView imagenSonido = view.findViewById(R.id.imagenSonido);
        TextView txtSonido = view.findViewById(R.id.txtSonido);

        imagenSonido.setImageResource(item.getIcono());
        txtSonido.setText(item.getTexto());

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }
}
