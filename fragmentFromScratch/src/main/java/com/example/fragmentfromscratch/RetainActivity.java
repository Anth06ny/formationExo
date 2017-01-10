package com.example.fragmentfromscratch;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.formation.utils.bean.Eleve;

public class RetainActivity extends Activity implements ListFragment.CallBack {

    private FrameLayout fl_fragment2;

    //nos fragments
    private ListFragment listFragment;
    private DetailFragment detailFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_pane);

        //On définit si on utilise 1 ou 2 layout en fonction de l'appareil.
        //le fragment 2
        fl_fragment2 = (FrameLayout) findViewById(R.id.fl_fragment2);

        //Si on souhaite afficher 2 fragments en même temps
        if (MyApplication.getInstance().isTwoPane()) {
            fl_fragment2.setVisibility(View.VISIBLE);
        }
        else {
            fl_fragment2.setVisibility(View.GONE);
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        listFragment = new ListFragment();
        ft.replace(R.id.fl_fragment1, listFragment, ListFragment.class.toString());

        if (MyApplication.getInstance().isTwoPane()) {
            detailFragment = new DetailFragment();
            //on le positionne sur le 2eme emplacement
            ft.replace(R.id.fl_fragment2, detailFragment, DetailFragment.class.toString());
        }
        //on positionne le fragment sur l'emplacement fragment1
        ft.commit();

        //On définit le callBack
        listFragment.setOnClickListListener(this);
    }

    @Override
    public void onClickOnEleve(Eleve eleve) {
        if (MyApplication.getInstance().isTwoPane()) {
            //On met à jour le 2eme fragment
            detailFragment.setEleve(eleve);
            detailFragment.refreshScreen();
        }
        else {
            //on remplace le fragment visible par celui du detail
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            detailFragment = new DetailFragment();
            detailFragment.setEleve(eleve);

            ft.replace(R.id.fl_fragment1, detailFragment, DetailFragment.class.toString());
            ft.addToBackStack(null); // permet de revenir à l'écran d'avant avec un back bouton
            ft.commit();
        }
    }
}
