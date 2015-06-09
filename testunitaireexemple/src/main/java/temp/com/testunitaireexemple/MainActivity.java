package temp.com.testunitaireexemple;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private final String SAVE_HELLO_WORLD_KEY = "SAVE_HELLO_WORLD_KEY";

    private TextView tv_hello_world;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_hello_world = (TextView) findViewById(R.id.tv_hello_world);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String saveValue = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(SAVE_HELLO_WORLD_KEY, null);
        //On remet la valeur sauvegardé s'il y en a une
        if (saveValue != null) {
            tv_hello_world.setText(saveValue);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //On sauvegarde en préférence la valeur dans helloWorld
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString(SAVE_HELLO_WORLD_KEY, tv_hello_world.getText().toString())
                .apply();

    }
}
