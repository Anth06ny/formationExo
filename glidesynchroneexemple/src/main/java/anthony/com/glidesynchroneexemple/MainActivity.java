package anthony.com.glidesynchroneexemple;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private static final String urlImage = "http://www.bobthebuilder.com/fr-fr/Images/btb_where_to_watch_background_Bob_tcm1281-232957.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);
    }

    public void onClick(View view) {

        new AsyncTask() {

            Bitmap bitmap;

            @Override
            protected Object doInBackground(Object[] objects) {
                FutureTarget<Bitmap> futureTarget =
                        Glide.with(MainActivity.this)
                                .asBitmap()
                                .load(urlImage)
                                .submit(Integer.MIN_VALUE, Integer.MIN_VALUE);

                try {
                    bitmap = futureTarget.get();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                iv.setImageBitmap(bitmap);
            }
        }.execute();
    }

//    public static void downloadFile(Context context, String url) throws Exception {
//
//        Log.w("TAG_URL", "file: " + url);
//
//        File downloadedFile = new File(context.getCacheDir(), nameFromPath(url));
//        if (downloadedFile.exists()) {
//            downloadedFile
//        }
//
//        //Création de la requete
//        Request request = new Request.Builder().url(url).build();
//
//        //Execution de la requête
//        Response response;
//        response = new OkHttpClient().newCall(request).execute();
//
//        File downloadedFile = new File(context.getCacheDir(), nameFromPath(url));
//        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
//        sink.writeAll(response.body().source());
//        sink.close();
//    }

    public static final String nameFromPath(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }
}
