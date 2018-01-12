package com.formation.webservice.wsocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.webservice.R;

public class WSocketActivity extends AppCompatActivity implements View.OnClickListener, WSClient.WSClientI {

    //IHM
    private Button bt_open;
    private Button bt_close;
    private TextView textView2;
    private TextView tv_status;
    private EditText et_message;
    private Button bt_envoyer;
    private TextView yv_console;
    private ScrollView sv;

    private WSClient wsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsocket);

        bt_open = (Button) findViewById(R.id.bt_open);
        bt_close = (Button) findViewById(R.id.bt_close);
        textView2 = (TextView) findViewById(R.id.textView2);
        tv_status = (TextView) findViewById(R.id.tv_status);
        et_message = (EditText) findViewById(R.id.et_message);
        bt_envoyer = (Button) findViewById(R.id.bt_envoyer);
        yv_console = (TextView) findViewById(R.id.yv_console);
        sv = (ScrollView) findViewById(R.id.sv);

        bt_open.setOnClickListener(this);
        bt_close.setOnClickListener(this);
        bt_envoyer.setOnClickListener(this);

        tv_status.setText("Close");
        tv_status.setTextColor(getResources().getColor(R.color.status_close));

        wsClient = new WSClient();
        wsClient.setWsClientI(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bt_open) {
            wsClient.open();
        }
        else if (v == bt_close) {
            wsClient.close();
        }
        else if (v == bt_envoyer) {
            if (et_message.getText().toString().length() == 0) {
                Toast.makeText(this, "Message vide", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    wsClient.sendMessage(et_message.getText().toString());
                    yv_console.append(Html.fromHtml(et_message.getText().toString() + "<BR/>"));
                    et_message.setText("");
                }
                catch (Exception e) {
                    wsErrorReceive(e);
                }
            }
        }
    }

    /* ---------------------------------
    //  CallBack WSocketClient
    // -------------------------------- */

    @Override
    public void wsOpen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText("Open");
                tv_status.setTextColor(getResources().getColor(R.color.status_open));
                appendMessageOnUIThread("<font color=\\\"#299733\\\"></font>OPEN");
            }
        });
    }

    @Override
    public void wsClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText("Close");
                tv_status.setTextColor(getResources().getColor(R.color.status_close));
                appendMessageOnUIThread("<font color=\"#E50A00\"></font>CLOSE");
            }
        });
    }

    @Override
    public void wsMessageReceive(String msg) {
        appendMessageOnUIThread("<font color=\\\"#299733\\\">></font>" + msg);
    }

    @Override
    public void wsErrorReceive(Throwable e) {
        e.printStackTrace();
        appendMessageOnUIThread("<font color=\\\"#E50A00\\\">ERROR></font>" + e.getMessage());
    }

    public void appendMessageOnUIThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                yv_console.append(Html.fromHtml(message + "<BR/>"));
            }
        });
    }
}
