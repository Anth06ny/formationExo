package com.example.fragmentfromscratch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.fragmentfromscratch.ListFragment.CallBack;
import com.example.fragmentfromscratch.bean.Eleve;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements CallBack {

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

        //on charge une base d'eleve
        if (eleveList == null) {
            eleveList = getEleves();
        }


        //on lance le 1er fragment et on lui passe comme argument la liste d'�l�ve
        listFragment = new ListFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constante.EXTRA_LIST_ELEVE, eleveList);
        listFragment.setArguments(bundle);
        //on s'inscrit au callback
        listFragment.setOnClickListListener(this);
        //on positionne le fragment sur l'emplacement fragment1
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment1, listFragment).commit();

        //le fragment 2
        fl_fragment2 = (FrameLayout) findViewById(R.id.fl_fragment2);

        //Si on souhaite afficher 2 fragment en même temps
        if (MyApplication.getInstance().isTwoPane()) {
            //on crée le 2eme
            detailFragment = new DetailFragment();
            //on le positionne sur le 2eme emplacement
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment2, detailFragment).commit();
            //on rend le 2eme emplacement visible
            fl_fragment2.setVisibility(View.VISIBLE);
        } else {
            fl_fragment2.setVisibility(View.GONE);
        }
    }

    //-----------------
    // menu
    //-----------------

    @SuppressWarnings("null")
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (menu != null) {
            menu.clear();
        }
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, MENU_CHERCHER, 0, "Chercher sur Google");
        menu.add(0, MENU_QUIT, 0, "Quit");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (item.getItemId() == MENU_CHERCHER && detailFragment != null) {
            final Uri uri = Uri.parse("http://www.google.com/#q=" + detailFragment.getEleve().getNom());
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------
    //callback fragment
    //--------------------

    @Override
    public void onClickOnEleve(final Eleve eleve) {

        if (MyApplication.getInstance().isTwoPane()) {
            detailFragment.setEleve(eleve);
            detailFragment.refreshText();
        } else {
            //on remplace le fragment visible par celui de l'ajout
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            detailFragment = new DetailFragment();
            detailFragment.setEleve(eleve);

            ft.replace(R.id.fl_fragment1, detailFragment);
            ft.addToBackStack(null); // permet de revenir à l'écran d'avant avec un back bouton
            ft.commit();
        }
    }

    //donnee
    private static ArrayList<Eleve> getEleves() {
        final Eleve eleve1 = new Eleve("Jean", "Pierre");
        final Eleve eleve2 = new Eleve("Marie", "Laure");
        final Eleve eleve3 = new Eleve("Anne", "Cécile");

        final ArrayList<Eleve> list = new ArrayList<Eleve>();
        list.add(eleve1);
        list.add(eleve2);
        list.add(eleve3);

        return list;
    }

}
