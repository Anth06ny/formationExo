package com.example.anthony.greendao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.anthony.greendao.adapter.RecycleViewAdapter;

public class MainActivity extends AppCompatActivity {

    RecycleViewAdapter recycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);
    }
}
