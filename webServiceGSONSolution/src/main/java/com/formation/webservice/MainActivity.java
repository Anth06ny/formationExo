package com.formation.webservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.formation.webservice.bean.CityBean;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    //Composant graphique
    private EditText et;
    private Button bt_ok;
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;

    //metier
    private CityAdapter cityAdapter;

    //donnees
    private ArrayList<CityBean> cityBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.et);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        rv = (RecyclerView) findViewById(R.id.rv);

        cityBeanArrayList = new ArrayList<>();

        //est ce que la taille de la recycle view va changer ?
        rv.setHasFixedSize(true);
        //A ajouter obligatoirement
        rv.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        cityAdapter = new CityAdapter(cityBeanArrayList);
        rv.setAdapter(cityAdapter);

        bt_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bt_ok) {
            if (StringUtils.isNotBlank(et.getText().toString())) {
                new WSAsyncTask(et.getText().toString()).execute();
            }
        }
    }

    private void updateError(Exception e) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(e.getMessage()).setIcon(R.drawable.ic_launcher).setPositiveButton("Ok", null).show();
    }

    public class WSAsyncTask extends AsyncTask<Void, Void, Exception> {

        private Dialog waintingDialog;
        private List<CityBean> result = null;
        private String cp;

        public WSAsyncTask(String cp) {
            this.cp = cp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waintingDialog = ProgressDialog.show(MainActivity.this, "", "Chargement en cours...");
            waintingDialog.show();
        }

        @Override
        protected Exception doInBackground(Void... params) {

            try {
                result = CityWS.getCity(cp);
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        }

        @Override
        protected void onPostExecute(Exception le) {
            waintingDialog.dismiss();

            if (le != null) {
                updateError(le);
            }
            else {
                cityBeanArrayList.clear();
                cityBeanArrayList.addAll(result);
                cityAdapter.notifyDataSetChanged();
            }
        }
    }
}
