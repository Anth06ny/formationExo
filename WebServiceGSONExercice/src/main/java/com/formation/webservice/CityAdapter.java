package com.formation.webservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.formation.webservice.bean.CityBean;

import java.util.List;

public class CityAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<CityBean> cityBeanList;

    public CityAdapter(final Context context, final List<CityBean> cityBeanList) {
        //Pour permettre la création de composant graphique
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //la liste qu'on veut afficher
        this.cityBeanList = cityBeanList;
    }

    @Override
    public int getCount() {
        return cityBeanList.size();
    }

    @Override
    public CityBean getItem(final int position) {
        return cityBeanList.get(position);
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
            //cr�ation
            rowView = mInflater.inflate(R.layout.city_cellule, null);

            viewHolder = new ViewHolder();
            viewHolder.cc_tv_cp = (TextView) rowView.findViewById(R.id.cc_tv_cp);
            viewHolder.cc_tv_ville = (TextView) rowView.findViewById(R.id.cc_tv_ville);

            rowView.setTag(viewHolder);
        }
        else {
            //recyclage
            viewHolder = (ViewHolder) rowView.getTag();
        }

        //---------------------
        // Remplissage
        //-------------------------

        //on remplit avec l'objet voulu
        final CityBean cityBean = getItem(position);

        viewHolder.cc_tv_cp.setText(cityBean.getCp() + "");
        viewHolder.cc_tv_ville.setText(cityBean.getVille());
        viewHolder.cityBean = cityBean;

        return rowView;
    }

    //------------------
    // View Holder
    //------------------
    public static class ViewHolder {
        public TextView cc_tv_cp, cc_tv_ville;
        public CityBean cityBean;
    }

}
