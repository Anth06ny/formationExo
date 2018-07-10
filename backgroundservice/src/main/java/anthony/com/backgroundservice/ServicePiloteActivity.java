package anthony.com.backgroundservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ServicePiloteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_pilote);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btsatrt) {
            startService(new Intent(this, StartDeviceService.class));
        }
        else if (view.getId() == R.id.btStopt) {

            stopService(new Intent(this, StartDeviceService.class));
        }
    }
}
