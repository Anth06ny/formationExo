package com.example.fragmentfromscratch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.fragmentfromscratch.ListFragment.CallBack;
import com.formation.utils.bean.Eleve;

import java.util.ArrayList;

public class MainActivity extends Activity implements CallBack {

    private final static String RESTART_EXTRA = "RESTART_EXTRA";

    private FrameLayout fl_fragment2;

    //nos fragments
    private ListFragment listFragment;
    private DetailFragment detailFragment = null;

    private static final int MENU_CHERCHER = 3;
    private static final int MENU_QUIT = 4;

    protected ArrayList<Eleve> eleveList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
    }



    @Override
    protected void onStart() {
        super.onStart();


        //On verifie si les fragments n'existent pas déjà. Ceux-ci peuvent avoir été recréées par le systeme lors d'une
        // rotation d'écran par exemple. On les récupère grâce à leur tag.
        listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.class.toString());
        detailFragment = (DetailFragment) getFragmentManager().findFragmentByTag(DetailFragment.class.toString());


        //Si la liste n'existe pas on la crée.
        if(listFragment == null) {
            listFragment = new ListFragment();
        }

        if (MyApplication.getInstance().isTwoPane()) {
            //On est obligé de recréer le fragment car le systeme n'autorise pas le déplacement de fragment dans un autre
            // frameLayout
            if(detailFragment != null) {
                detailFragment = (DetailFragment) recreateFragment(detailFragment);
            }
            else {
                //on crée le 2eme
                detailFragment = new DetailFragment();
            }
            //on le positionne sur le 2eme emplacement
            getFragmentManager().beginTransaction().replace(R.id.fl_fragment2, detailFragment, DetailFragment.class.toString())
                        .commit();
        }

        //on positionne le fragment sur l'emplacement fragment1
        getFragmentManager().beginTransaction().replace(R.id.fl_fragment1, listFragment, ListFragment.class.toString()).commit();

        //On définit le callBack
        listFragment.setOnClickListListener(this);

    }


    //-------------------
    //callback fragment
    //--------------------

    @Override
    public void onClickOnEleve(final Eleve eleve) {

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

            ft.replace(R.id.fl_fragment1, detailFragment,  DetailFragment.class.toString());
            ft.addToBackStack(null); // permet de revenir à l'écran d'avant avec un back bouton
            ft.commit();
        }
    }

    /* -------------------------
    // private
    //------------------------- */

    /**
     * permet de créer un nouveau fragment avec les sauvegardes de l'ancien.
     * @param fragment
     * @return
     */
    private static Fragment recreateFragment(Fragment fragment){
        try {
            Fragment.SavedState oldState= fragment.getFragmentManager().saveFragmentInstanceState(fragment);
            Fragment newInstance = fragment.getClass().newInstance();
            newInstance.setInitialSavedState(oldState);
            return newInstance;
        }
        catch (Exception e) // InstantiationException, IllegalAccessException
        {
            return null;
        }
    }

}
