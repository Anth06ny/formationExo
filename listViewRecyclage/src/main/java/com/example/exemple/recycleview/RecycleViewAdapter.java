package com.example.exemple.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.exemple.R;
import com.formation.utils.bean.Eleve;

import java.util.List;

/**
 * Created by Anthony on 08/01/2016.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<Eleve> eleveBeanList;
    private RVAdapterCallBack RVAdapterCallBack;

    public RecycleViewAdapter(List<Eleve> eleveBeanList, RVAdapterCallBack RVAdapterCallBack) {
        this.eleveBeanList = eleveBeanList;
        this.RVAdapterCallBack = RVAdapterCallBack;

        notifyDataSetChanged();
    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eleve_cellule, parent, false);
        return new RecycleViewAdapter.ViewHolder(view, RVAdapterCallBack);
    }

    @Override
    public int getItemCount() {
        return eleveBeanList.size();
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.ViewHolder holder, int position) {
        Eleve eleve = eleveBeanList.get(position);

        holder.ec_tv_nom.setText(eleve.getNom());
        holder.ec_tv_nom.setText(eleve.getNom());
        holder.ec_tv_prenom.setText(eleve.getPrenom());
        holder.eleve = eleve;
    }

    /* ---------------------------------
    // ViewHolder
    // -------------------------------- */
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ec_tv_nom, ec_tv_prenom;
        public ImageView ec_iv;
        public View root;
        public Eleve eleve;

        public ViewHolder(View itemView, final RVAdapterCallBack RVAdapterCallBack) {
            super(itemView);
            ec_tv_nom = (TextView) itemView.findViewById(com.formation.utils.R.id.ec_tv_nom);
            ec_tv_prenom = (TextView) itemView.findViewById(R.id.ec_tv_prenom);
            ec_iv = (ImageView) itemView.findViewById(R.id.ec_iv);
            root = itemView.findViewById(R.id.root);

            itemView.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RVAdapterCallBack != null) {
                        RVAdapterCallBack.onEleveClic(eleve, ec_iv, ec_tv_nom);
                    }
                }
            });

            itemView.findViewById(R.id.root).setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (RVAdapterCallBack != null) {
                                RVAdapterCallBack.onEleveLongClic(eleve);
                                return true;
                            }
                            return false;
                        }
                    });
        }
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */
    public interface RVAdapterCallBack {
        void onEleveClic(Eleve eleve, ImageView ec_iv, TextView ec_tv_nom);

        void onEleveLongClic(Eleve eleve);
    }
}
