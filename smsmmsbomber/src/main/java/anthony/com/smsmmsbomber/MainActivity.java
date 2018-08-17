package anthony.com.smsmmsbomber;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import anthony.com.smsmmsbomber.service.SendMessageService;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvInfo, tvUUID;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        tvUUID = findViewById(R.id.tvUUID);

        refreshScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //On check les permissions
        Permissionutils.requestAllPermissionIfNot(this);
        Permissionutils.makeDefautSmsApp(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //on boucle
        //On check les permissions
        Permissionutils.requestAllPermissionIfNot(this);
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
    }



    /* ---------------------------------
    // private
    // -------------------------------- */

    private void refreshScreen() {

        tvUUID.setText("Numéro de serie : " + SharedPreferenceUtils.getUniqueIDGoodFormat(this));
    }
}
