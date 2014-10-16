package com.example.sendsms;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        try {
            final SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS envoyé!", Toast.LENGTH_LONG).show();
        }
        catch (final Exception e) {
            Toast.makeText(getApplicationContext(), "Echec de l'envoie, merci de réessayer plus tard!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

}
