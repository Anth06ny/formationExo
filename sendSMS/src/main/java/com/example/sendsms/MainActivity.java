package com.example.sendsms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.sendsms.broadcast.SMSSentListener;

public class MainActivity extends Activity implements OnClickListener {

    private Button buttonSend;
    private EditText textPhoneNo;
    private EditText textSMS;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);

        buttonSend.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {

        final String phoneNo = textPhoneNo.getText().toString();
        final String sms = textSMS.getText().toString();

        SMSSentListener.sendSMS(this, phoneNo, sms);
    }

}
