package com.example.exemple;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exemple.dao.MaBaseSQLite;
import com.formation.utils.BDDUtils;

public class MainActivity extends Activity implements OnClickListener {

    private TextView tv_heure;
    private Button bt_go;
    private Button bt_copy;

    //----------------
    // view
    //----------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_heure = (TextView) findViewById(R.id.tv_heure);
        bt_go = (Button) findViewById(R.id.bt_go);
        bt_copy = (Button) findViewById(R.id.bt_copy);

        bt_go.setOnClickListener(this);
        bt_copy.setOnClickListener(this);
    }

    //----------------
    // click
    //----------------

    @Override
    public void onClick(final View v) {

        if (v == bt_go) {

            final Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra(Constante.EXTRA_SECOND_ACTIVITY_MSG, "Hello from MainActivity");
            startActivity(intent);
        }
        else if (v == bt_copy) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                BDDUtils.CopySQLiteBaseToDownload(this, MaBaseSQLite.NOM_BDD);
            }
            else {
                Toast.makeText(MainActivity.this, "La permission est nécéssaire pour acceder au répetoire Download!!",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            BDDUtils.CopySQLiteBaseToDownload(this, MaBaseSQLite.NOM_BDD);
        }
    }
}
