package com.example.anthony.databinding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bt)
    protected Button bt;

    @BindViews({R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4})
    protected List<TextView> tvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.bt);

        //bind
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt})
    public void onClick(View view) {
        if (R.id.bt == view.getId()) {
            ButterKnife.apply(tvList, new ButterKnife.Action<TextView>() {
                @Override
                public void apply(@NonNull TextView view, int index) {
                    view.setText("TextView " + index);
                }
            });
        }
    }
}
