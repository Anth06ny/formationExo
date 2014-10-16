package com.boutonexemple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.boutonexemple.button.MyBootStrapButtonWithIcon;

public class MainActivity extends Activity implements View.OnClickListener {

    private MyBootStrapButtonWithIcon myBootStrapButtonWithIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBootStrapButtonWithIcon = (MyBootStrapButtonWithIcon) findViewById(R.id.button3);
        myBootStrapButtonWithIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
