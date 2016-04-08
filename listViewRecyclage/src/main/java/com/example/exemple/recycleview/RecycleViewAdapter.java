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
    private RecycleViewAdapterListener recycleViewAdapterListener;

    public RecycleViewAdapter(List<Eleve> eleveBeanList, RecycleViewAdapterListener recycleViewAdapterListener) {
        this.eleveBeanList = eleveBeanList;
        this.recycleViewAdapterListener = recycleViewAdapterListener;

        notifyDataSetChanged();
    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eleve_cellule, parent, false);
        return new RecycleViewAdapter.ViewHolder(view, recycleViewAdapterListener);
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
        public Eleve eleve;

        public ViewHolder(View itemView, final RecycleViewAdapterListener recycleViewAdapterListener) {
            super(itemView);
            ec_tv_nom = (TextView) itemView.findViewById(com.formation.utils.R.id.ec_tv_nom);
            ec_tv_prenom = (TextView) itemView.findViewById(R.id.ec_tv_prenom);
            ec_iv = (ImageView) itemView.findViewById(R.id.ec_iv);

            itemView.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recycleViewAdapterListener != null) {
                        recycleViewAdapterListener.onEleveClic(eleve);
                    }
                }
            });
        }
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface RecycleViewAdapterListener {
        void onEleveClic(Eleve eleve);
    }
}
