package com.zbar_reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class MainActivity extends Activity implements ZBarScannerView.ResultHandler, View.OnClickListener {

    private Button bt_start;
    private TextView tv_resultat;
    private FrameLayout cameraPreview;

    private ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_start = (Button) findViewById(R.id.bt_start);
        tv_resultat = (TextView) findViewById(R.id.tv_resultat);
        cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);

        mScannerView = new ZBarScannerView(this);
        cameraPreview.addView(mScannerView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera(); // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.setResultHandler(null);
        mScannerView.stopCamera(); // Stop camera on pause
    }

    @Override
    public void onClick(View v) {
        if (mScannerView.isActivated()) {
            mScannerView.stopCamera();
        }
        else {
            mScannerView.startCamera();
        }

    }

    /* ******************************
    ****        ZBAR
    * *******************************/

    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        tv_resultat.setText("Scan : " + result.getContents() + "\nScan format : " + result.getBarcodeFormat());
    }

}
