package com.example.fragmentfromscratch;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.formation.utils.adapter.EleveAdapter;
import com.formation.utils.bean.Eleve;

import java.util.ArrayList;

public class ListFragment extends Fragment implements OnItemClickListener, View.OnClickListener {

    private static final String SAVE_LIST_EXTRA = "SAVE_LIST_EXTRA";

    //composants graphique
    private ListView lv;
    private Button bt;

    private EleveAdapter eleveAdapter;
    private ArrayList<Eleve> eleveList;

    private CallBack OnClickOnList = null;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        lv = (ListView) rootView.findViewById(R.id.lv);
        bt = (Button) rootView.findViewById(R.id.bt);

        bt.setOnClickListener(this);

        if(eleveList == null) {
            eleveList = new ArrayList<>();
        }

        eleveAdapter = new EleveAdapter(getActivity(), eleveList);

        lv.setAdapter(eleveAdapter);
        lv.setOnItemClickListener(this);

        return rootView;

    }

    @Override
    /**
     * Sauvegarde du fragment
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_LIST_EXTRA, eleveList);

    }

    @Override
    /**
     * On restaure le fragment
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            ArrayList<Eleve> temp = savedInstanceState.getParcelableArrayList(SAVE_LIST_EXTRA);
            if(temp != null) {
                eleveList.clear();
                eleveList.addAll(temp);
            }
        }
    }

    /* -------------------------
    //  Click
    //------------------------- */

    @Override
    public void onClick(View v) {
        //click sur le bouton d'ajout
        if(v == bt) {
            eleveList.add(new Eleve("Bob", "" + eleveList.size(), true));
            eleveAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        //click sur un élève
        if (OnClickOnList != null) {
            OnClickOnList.onClickOnEleve(eleveList.get(position));
        }

    }

    //----------
    // getter setter
    //----------------
    public void setOnClickListListener(final CallBack onClickOnList) {
        OnClickOnList = onClickOnList;
    }

    //------------
    // interface
    //-------------
    public interface CallBack {
        void onClickOnEleve(Eleve eleve);
    }


}
