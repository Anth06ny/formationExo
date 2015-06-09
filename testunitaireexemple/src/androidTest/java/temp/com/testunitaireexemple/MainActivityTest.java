package temp.com.testunitaireexemple;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Created by amonteiro on 09/06/2015.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private TextView tv_hello_world;

    //Data
    private String helloWorldValue;

    public MainActivityTest() {
        super(MainActivity.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mainActivity = getActivity();

        tv_hello_world = (TextView) mainActivity.findViewById(R.id.tv_hello_world);
        helloWorldValue = getActivity().getString(R.string.hello_world);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //valeur par defaut
                tv_hello_world.setText(helloWorldValue);
            }
        });

    }

    /**
     * Verfie qu'on a bien la valeur attendue
     */
    public void testCorrectValue() {
        assertEquals(tv_hello_world.getText(), helloWorldValue);
    }

    /**
     * Provoque un redemarrage de l'activite et verifie que les valeurs sont bien sauvegardees
     */
    public void testRestartActivitySaveValue() {
        final String saveValue = "saveValue";

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_hello_world.setText(saveValue);
            }
        });

        // Close the activity
        mainActivity.finish();
        // Required to force creation of a new activity
        setActivity(null);

        // Relaunch the activity
        mainActivity = this.getActivity();

        tv_hello_world = (TextView) mainActivity.findViewById(R.id.tv_hello_world);

        assertEquals(saveValue, tv_hello_world.getText().toString());
    }

    public void testEchec() {
        assertNull(tv_hello_world.getText());
    }

}
