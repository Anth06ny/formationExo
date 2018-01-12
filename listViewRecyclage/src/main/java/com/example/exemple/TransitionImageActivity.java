package com.example.exemple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.formation.utils.bean.Eleve;

/**
 * Created by Anthony on 19/06/2017.
 */

public class TransitionImageActivity extends AppCompatActivity {

    public static final String ELEVE_EXTRA = "ELEVE_EXTRA";

    private TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transitionimmage);

        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);

        Eleve eleve = getIntent().getParcelableExtra(ELEVE_EXTRA);

        iv.setImageResource(R.mipmap.ic_launcher);
        tv.setText(eleve.getNom());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
