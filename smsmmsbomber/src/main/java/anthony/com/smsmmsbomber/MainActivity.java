package anthony.com.smsmmsbomber;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.formation.utils.exceptions.TechnicalException;
import com.squareup.otto.Subscribe;

import anthony.com.smsmmsbomber.service.SendMessageService;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvInfo, tvUUID, tvLog;
    private TextView tvExplication;
    private Button bt_refresh, btLog;

    public static boolean LOG_ON = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        tvUUID = findViewById(R.id.tvUUID);
        tvExplication = findViewById(R.id.tvExplication);
        bt_refresh = findViewById(R.id.bt_refresh);
        tvLog = findViewById(R.id.tvLog);
        btLog = findViewById(R.id.btLog);

        bt_refresh.setOnClickListener(this);
        btLog.setOnClickListener(this);

        Permissionutils.requestAllPermissionIfNot(this);
        Permissionutils.makeDefautSmsApp(this);
        MyApplication.getBus().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getBus().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        refreshScreen();
    }




    /* ---------------------------------
    // Click
    // -------------------------------- */

    @Override
    public void onClick(final View v) {
        if (R.id.btStartService == v.getId()) {
            SendMessageService.startservice(this);
        }
        else if (R.id.btStopService == v.getId()) {
            SendMessageService.stopService(this);
        }
        else if (v.getId() == R.id.bt_refresh) {
            Permissionutils.requestAllPermissionIfNot(this);
            Permissionutils.makeDefautSmsApp(this);
        }
        else if (v.getId() == R.id.btLog) {
            LOG_ON = !LOG_ON;
            refreshScreen();
        }
    }



    /* ---------------------------------
    // private
    // -------------------------------- */

    @Subscribe
    public void addLog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.append(message + "\n");
            }
        });
    }

    private void refreshScreen() {

        if (!Permissionutils.isAllPermission(this) || !Permissionutils.isDefautApp(this)) {
            bt_refresh.setVisibility(View.VISIBLE);
            tvExplication.setVisibility(View.VISIBLE);
        }
        else {
            bt_refresh.setVisibility(View.GONE);
            tvExplication.setVisibility(View.GONE);
        }

        if (LOG_ON) {
            btLog.setText("Log activé");
        }
        else {
            btLog.setText("Log désactivé");
        }

        try {
            tvUUID.setText("Numéro de serie : " + SharedPreferenceUtils.getUniqueIDGoodFormat(this));
        }
        catch (TechnicalException e) {
            tvUUID.setText("Numéro de serie : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
