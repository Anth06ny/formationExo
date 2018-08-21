package anthony.com.smsmmsbomber;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.formation.utils.exceptions.TechnicalException;

import anthony.com.smsmmsbomber.service.SendMessageService;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvInfo, tvUUID;
    private TextView tvExplication;
    private Button bt_refresh;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        tvUUID = findViewById(R.id.tvUUID);
        tvExplication = findViewById(R.id.tvExplication);
        bt_refresh = findViewById(R.id.bt_refresh);

        bt_refresh.setOnClickListener(this);

        Permissionutils.requestAllPermissionIfNot(this);
        Permissionutils.makeDefautSmsApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreen();
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
    }



    /* ---------------------------------
    // private
    // -------------------------------- */

    private void refreshScreen() {

        if (!Permissionutils.isAllPermission(this)) {
            bt_refresh.setVisibility(View.VISIBLE);
            tvExplication.setVisibility(View.VISIBLE);
        }
        else {
            bt_refresh.setVisibility(View.INVISIBLE);
            tvExplication.setVisibility(View.INVISIBLE);
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
