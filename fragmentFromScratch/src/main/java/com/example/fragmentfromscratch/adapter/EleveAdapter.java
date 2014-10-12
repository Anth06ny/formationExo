package com.example.fragmentfromscratch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fragmentfromscratch.R;
import com.example.fragmentfromscratch.bean.Eleve;

import java.util.List;

public class EleveAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<Eleve> eleveList;

    public EleveAdapter(final Context context, final List<Eleve> eleveList) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.eleveList = eleveList;
    }

    @Override
    public int getCount() {
        return eleveList.size();
    }

    @Override
    public Eleve getItem(final int position) {
        return eleveList.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View rowView = convertView;

        //---------------------
        // inflate
        //-------------------------
        final ViewHolder viewHolder;
        if (rowView == null) {
            //création
            rowView = mInflater.inflate(R.layout.eleve_cellule, null);

            viewHolder = new ViewHolder();
            viewHolder.ec_tv_nom = (TextView) rowView.findViewById(R.id.ec_tv_nom);
            viewHolder.ec_tv_prenom = (TextView) rowView.findViewById(R.id.ec_tv_prenom);
            viewHolder.ec_iv = (ImageView) rowView.findViewById(R.id.ec_iv);

            rowView.setTag(viewHolder);
        } else {
            //recyclage
            viewHolder = (ViewHolder) rowView.getTag();
        }

        //on remplit avec l'objet voulu
        final Eleve eleve = eleveList.get(position);

        viewHolder.ec_tv_nom.setText(eleve.getNom());
        viewHolder.ec_tv_prenom.setText(eleve.getPrenom());

        return rowView;
    }

    //------------------
    // View Holder
    //------------------
    public static class ViewHolder {
        public TextView ec_tv_nom, ec_tv_prenom;
        public ImageView ec_iv;
        public Eleve eleve;
    }

}
