package com.example.fragmentfromscratch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.fragmentfromscratch.adapter.EleveAdapter;
import com.example.fragmentfromscratch.bean.Eleve;

import java.util.ArrayList;

public class ListFragment extends Fragment implements OnItemClickListener {

    private ListView lv;

    private EleveAdapter eleveAdapter;
    private ArrayList<Eleve> eleveList;

    private CallBack OnClickOnList = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        lv = (ListView) rootView.findViewById(R.id.lv);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            eleveList = getArguments().getParcelableArrayList(Constante.EXTRA_LIST_ELEVE);
        }
        if (eleveList == null) {
            eleveList = new ArrayList<Eleve>();
        }
        eleveAdapter = new EleveAdapter(getActivity(), eleveList);

        lv.setAdapter(eleveAdapter);
        lv.setOnItemClickListener(this);

        return rootView;

    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

        final Eleve eleve = eleveList.get(position);

        if (OnClickOnList != null) {
            OnClickOnList.onClickOnEleve(eleve);
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
