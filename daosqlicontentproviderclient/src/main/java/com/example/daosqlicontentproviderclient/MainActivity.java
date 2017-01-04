package com.example.daosqlicontentproviderclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.tv);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            EleveBDDManager.addEleve(this);
        }
        else if (v.getId() == R.id.load) {
            textView.setText("");
            for (Eleve eleve : EleveBDDManager.getEleves(this)) {
                textView.append(eleve + "\n");
            }
        }
    }
}
