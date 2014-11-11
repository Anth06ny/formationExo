package com.example.fragmentfromscratch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formation.utils.bean.Eleve;

public class DetailFragment extends Fragment {

    private Eleve eleve = null;
    private TextView tv;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        tv = (TextView) rootView.findViewById(R.id.tv);

        //        setHasOptionsMenu(optionMenu);

        return rootView;

    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshText();
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(final Eleve eleve) {
        this.eleve = eleve;
    }

    //Comme il est public on ne sait pas si il sera appelé depuis l'UIThread donc au cas ou...
    public void refreshText() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (eleve == null) {
                    tv.setText("Aucun éléve");
                }
                else {
                    tv.setText(eleve.getPrenom() + " " + eleve.getNom());
                }

            }
        });

    }

}
