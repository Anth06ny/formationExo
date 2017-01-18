package com.example.anthony.greendao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anthony.greendao.R;
import com.example.anthony.greendao.bean.Classe;
import com.example.anthony.greendao.bean.Eleve;
import com.example.anthony.greendao.bean.Enseignant;

import java.util.List;

/**
 * Created by Anthony on 08/01/2016.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    public enum TYPE {CLASSE, ENSEIGNANT, ELEVE}

    private TYPE type;
    private List<Object> list;
    private RVAdapterCallBack RVAdapterCallBack;

    public RecycleViewAdapter(List<Object> list, TYPE type, RVAdapterCallBack RVAdapterCallBack) {
        this.list = list;
        this.type = type;
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
        return list.size();
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.ViewHolder holder, int position) {

        switch (type) {
            case CLASSE:
                final Classe classe = (Classe) list.get(position);
                holder.ec_tv_nom.setText(classe.getName());
                holder.ec_tv_prenom.setText("");
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RVAdapterCallBack != null) {
                            RVAdapterCallBack.onClasseClic(classe);
                        }
                    }
                });
                break;
            case ENSEIGNANT:
                final Enseignant enseignant = (Enseignant) list.get(position);
                holder.ec_tv_nom.setText(enseignant.getNom());
                holder.ec_tv_prenom.setText(enseignant.getPrenom());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RVAdapterCallBack != null) {
                            RVAdapterCallBack.onEnseignantClic(enseignant);
                        }
                    }
                });
                break;
            case ELEVE:
                final Eleve eleve = (Eleve) list.get(position);
                holder.ec_tv_nom.setText(eleve.getNom());
                holder.ec_tv_prenom.setText(eleve.getPrenom());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RVAdapterCallBack != null) {
                            RVAdapterCallBack.onEleveClic(eleve);
                        }
                    }
                });
                break;
        }
    }

    /* ---------------------------------
    // ViewHolder
    // -------------------------------- */
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ec_tv_nom, ec_tv_prenom;
        public ImageView ec_iv;
        public View root;

        public ViewHolder(View itemView, final RVAdapterCallBack RVAdapterCallBack) {
            super(itemView);
            ec_tv_nom = (TextView) itemView.findViewById(R.id.ec_tv_nom);
            ec_tv_prenom = (TextView) itemView.findViewById(R.id.ec_tv_prenom);
            ec_iv = (ImageView) itemView.findViewById(R.id.ec_iv);
            root = itemView.findViewById(R.id.root);
        }
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    /* ---------------------------------
        // Interface
        // -------------------------------- */
    public interface RVAdapterCallBack<T> {
        void onEleveClic(Eleve eleve);

        void onEnseignantClic(Enseignant enseignant);

        void onClasseClic(Classe classe);
    }
}
