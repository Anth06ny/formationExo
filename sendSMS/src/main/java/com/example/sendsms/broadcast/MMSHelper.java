package com.example.sendsms.broadcast;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MMSHelper {

    public class MMSPart {
        public String Name = "";
        public String MimeType = "image/png";
        public byte[] Data;
    }

    public class APN {
        public String MMSCenterUrl = "";
        public String MMSPort = "";
        public String MMSProxy = "";

        public APN(String MMSCenterUrl, String MMSPort, String MMSProxy)
        {
            this.MMSCenterUrl = MMSCenterUrl;
            this.MMSPort = MMSPort;
            this.MMSProxy = MMSProxy;
        }

        public APN()
        {

        }
    }

    public class APNHelper {

        public APNHelper(final Context context) {
            this.context = context;
        }

        @SuppressWarnings("unchecked")
        public List<APN> getMMSApns() {

            final Cursor apnCursor = this.context.getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current"), null, null, null, null);
            if (apnCursor == null) {
                return Collections.EMPTY_LIST;
            }
            else {
                final List<APN> results = new ArrayList<APN>();
                if (apnCursor.moveToFirst()) {
                    do {
                        final String type = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.TYPE));
                        if (!TextUtils.isEmpty(type) && (type.equalsIgnoreCase("*") || type.equalsIgnoreCase("mms"))) {
                            final String mmsc = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSC));
                            final String mmsProxy = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSPROXY));
                            final String port = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSPORT));
                            final APN apn = new APN();
                            apn.MMSCenterUrl = mmsc;
                            apn.MMSProxy = mmsProxy;
                            apn.MMSPort = port;
                            results.add(apn);

                            Toast.makeText(context, mmsc + " " + mmsProxy + " " + port, Toast.LENGTH_LONG).show();
                        }
                    }
                    while (apnCursor.moveToNext());
                }
                apnCursor.close();
                return results;
            }
        }

        private Context context;
    }
}
