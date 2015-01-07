package com.formation.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by amonteiro on 07/01/2015.
 */
public class BDDUtils {

    /**
     * Copier la base de donnée de l'application dans le repertoire download
     * @param context
     * @param tableName
     */
    public static void CopySQLiteBaseToDownload(Context context, String tableName) {

        //OU se trouve la base de donnée
        File database = new File("data/data/" + context.getPackageName() + "/databases/" + tableName);
        //Ou on la copie
        File downloadDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + tableName);

        try {

            if (database.exists()) {

                if (!downloadDirectory.exists()) {
                    downloadDirectory.createNewFile();
                }

                InputStream in = new FileInputStream(database);
                OutputStream out = new FileOutputStream(downloadDirectory);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Toast.makeText(context, "Le fichier a été copié", Toast.LENGTH_LONG).show();

            }
            else {
                Toast.makeText(context, "Erreur lors de la copie", Toast.LENGTH_LONG).show();
            }

            //Permet de le voir directement dans windows
            MediaScannerConnection.scanFile(context, new String[] { downloadDirectory.getAbsolutePath() }, null, null);

        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur lors de la copie", Toast.LENGTH_LONG).show();
        }
    }
}
