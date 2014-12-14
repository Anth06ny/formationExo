package com.example.fragmentfromscratch;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formation.utils.bean.Eleve;

public class DetailFragment extends Fragment {

    private static final String ELEVE_EXTRA = "ELEVE_EXTRA";

    private Eleve eleve = null;
    private TextView tv;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        tv = (TextView) rootView.findViewById(R.id.tv);
        return rootView;

    }

    @Override
    /**
     * Sauvegarde du fragment
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ELEVE_EXTRA, eleve);
    }

    @Override
    /**
     * Restauration du fragment
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null ) {
            Eleve temp = savedInstanceState.getParcelable(ELEVE_EXTRA);
            if (temp != null) {
                eleve = temp;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshScreen();
    }

    /* -------------------------
    // Refresh
    //------------------------- */

    //Comme il est public on ne sait pas si il sera appelé depuis l'UIThread donc au cas ou...
    public void refreshScreen() {
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


    /* -------------------------
    // Getter setter
    //------------------------- */

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(final Eleve eleve) {
        this.eleve = eleve;
    }


}
