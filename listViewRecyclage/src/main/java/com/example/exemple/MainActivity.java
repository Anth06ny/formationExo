package com.example.exemple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.exemple.recycleview.RecycleViewActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button bt_lv, bt_rv;

    //----------------
    // view
    //----------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_lv = (Button) findViewById(R.id.bt_lv);
        bt_rv = (Button) findViewById(R.id.bt_rv);

        bt_lv.setOnClickListener(this);
        bt_rv.setOnClickListener(this);
    }

    //----------------
    // click
    //----------------

    @Override
    public void onClick(final View v) {

        if (v == bt_lv) {
            startActivity(new Intent(this, ListViewActivity.class));
        }
        else if (v == bt_rv) {
            startActivity(new Intent(this, RecycleViewActivity.class));
        }
    }
}
