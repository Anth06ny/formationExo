package com.formation.webservice;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.formation.utils.PopupsManager;
import com.formation.webservice.bean.CityBean;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText et;
    private Button bt_ok;
    private ListView lv;
    private CityAdapter cityAdapter;
    private ArrayList<CityBean> cityBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.et);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        lv = (ListView) findViewById(R.id.lv);

        cityBeanArrayList = new ArrayList<>();

        cityAdapter = new CityAdapter(this, cityBeanArrayList);
        lv.setAdapter(cityAdapter);

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
        PopupsManager.showPopup(this, e.getMessage(), null);
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

            waintingDialog = PopupsManager.createProgressPopup(MainActivity.this, "Chargement en cours");
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
