package anthony.com.cahors2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import anthony.com.cahors2.transverse.MyException;
import anthony.com.cahors2.transverse.OkHttpUtils;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.analyse_en_cours));
        new MonAt().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public class MonAt extends AsyncTask {

        MyException exception;

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                OkHttpUtils.ping(MainActivity.this);
            }
            catch (MyException e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

            AlertDialog.Builder buider = new AlertDialog.Builder(MainActivity.this);

            if (exception != null) {
                buider.setTitle(R.string.dialog_title_error);
                buider.setMessage(getString(exception.getCode(), getString(R.string.ipClient)));
                if (exception.getCode() == R.string.airplane) {
                    buider.setIcon(R.mipmap.ic_airplane_on);
                }
                else if (exception.getCode() == R.string.internet_faible) {
                    buider.setIcon(R.mipmap.ic_no_reseau);
                }
                else if (exception.getCode() == R.string.noreseau) {
                    buider.setIcon(R.mipmap.ic_no_reseau2);
                }
                else {
                    buider.setIcon(R.mipmap.ic_error);
                }
            }
            else {
                buider.setTitle(R.string.dialog_title_ok);
                buider.setIcon(R.mipmap.ic_ok);
                buider.setMessage(getString(R.string.internet_ok, getString(R.string.ipClient)));
            }
            buider.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            buider.show();
        }
    }
}
