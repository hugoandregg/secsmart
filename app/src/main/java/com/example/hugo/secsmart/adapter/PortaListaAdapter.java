package com.example.hugo.secsmart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hugo.secsmart.R;
import com.example.hugo.secsmart.dominio.Porta;

import java.util.List;

/**
 * Created by hugo on 05/06/15.
 */
public class PortaListaAdapter extends ArrayAdapter<Porta> {

    public PortaListaAdapter(Context context, int
            textViewResourceId,List<Porta> objects) {
        super(context, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_portas, null);
        }

        Porta item = getItem(position);
        if (item!= null) {
            TextView titleText = (TextView) view.findViewById(R.id.nomeText);
            titleText.setText( item.getNumero() );
            TextView cnpjText = (TextView) view.findViewById(R.id.cnpjText);
            cnpjText.setText(item.getEstado());

        }

        return view;
    }
}
