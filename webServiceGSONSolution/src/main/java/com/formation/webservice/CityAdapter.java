package com.formation.webservice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formation.webservice.bean.CityBean;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final List<CityBean> cityBeanList;

    public CityAdapter(List<CityBean> cityBeanList) {
        this.cityBeanList = cityBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_cellule, parent, false);
        return new CityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CityBean cityBean = cityBeanList.get(position);

        holder.cc_tv_cp.setText(cityBean.getCp() + "");
        holder.cc_tv_ville.setText(cityBean.getVille());
    }

    @Override
    public int getItemCount() {
        return cityBeanList.size();
    }

    //------------------
    // View Holder
    //------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cc_tv_cp, cc_tv_ville;

        public ViewHolder(View itemView) {
            super(itemView);
            cc_tv_cp = (TextView) itemView.findViewById(R.id.cc_tv_cp);
            cc_tv_ville = (TextView) itemView.findViewById(R.id.cc_tv_ville);
        }
    }
}
