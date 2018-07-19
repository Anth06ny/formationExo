package com.example.sendsms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.mms.ContentType;
import com.google.android.mms.InvalidHeaderValueException;
import com.google.android.mms.pdu_alt.CharacterSets;
import com.google.android.mms.pdu_alt.EncodedStringValue;
import com.google.android.mms.pdu_alt.GenericPdu;
import com.google.android.mms.pdu_alt.PduBody;
import com.google.android.mms.pdu_alt.PduComposer;
import com.google.android.mms.pdu_alt.PduHeaders;
import com.google.android.mms.pdu_alt.PduParser;
import com.google.android.mms.pdu_alt.PduPart;
import com.google.android.mms.pdu_alt.RetrieveConf;
import com.google.android.mms.pdu_alt.SendConf;
import com.google.android.mms.pdu_alt.SendReq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MmsMessagingDemo extends Activity {
    private static final String TAG = "MmsMessagingDemo";

    public static final String EXTRA_NOTIFICATION_URL = "notification_url";

    private static final String ACTION_MMS_SENT = "com.example.android.apis.os.MMS_SENT_ACTION";
    private static final String ACTION_MMS_RECEIVED =
            "com.example.android.apis.os.MMS_RECEIVED_ACTION";

    private EditText mRecipientsInput;
    private EditText mSubjectInput;
    private EditText mTextInput;
    private TextView mSendStatusView;
    private Button mSendButton;
    private File mSendFile;
    private File mDownloadFile;
    private Random mRandom = new Random();

    private BroadcastReceiver mSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleSentResult(getResultCode(), intent);
        }
    };
    private IntentFilter mSentFilter = new IntentFilter(ACTION_MMS_SENT);

    private BroadcastReceiver mReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                handleReceivedResult(context, getResultCode(), intent);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private IntentFilter mReceivedFilter = new IntentFilter(ACTION_MMS_RECEIVED);

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final String notificationIndUrl = intent.getStringExtra(EXTRA_NOTIFICATION_URL);
        if (!TextUtils.isEmpty(notificationIndUrl)) {
            downloadMessage(notificationIndUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mms_demo);

        // Enable or disable the broadcast receiver depending on the checked
        // state of the checkbox.
        final CheckBox enableCheckBox = (CheckBox) findViewById(R.id.mms_enable_receiver);
        final PackageManager pm = this.getPackageManager();
        final ComponentName componentName = new ComponentName("com.example.android.apis",
                "com.example.android.apis.os.MmsWapPushReceiver");
        //enableCheckBox.setChecked(pm.getComponentEnabledSetting(componentName) ==
        //PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        //        enableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //                Log.d(TAG, (isChecked ? "Enabling" : "Disabling") + " MMS receiver");
        //                pm.setComponentEnabledSetting(componentName,
        //                        isChecked ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        //                                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        //                        PackageManager.DONT_KILL_APP);
        //            }
        //        });

        mRecipientsInput = (EditText) findViewById(R.id.mms_recipients_input);
        mSubjectInput = (EditText) findViewById(R.id.mms_subject_input);
        mTextInput = (EditText) findViewById(R.id.mms_text_input);
        mSendStatusView = (TextView) findViewById(R.id.mms_send_status);
        mSendButton = (Button) findViewById(R.id.mms_send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(
                        mRecipientsInput.getText().toString(),
                        mSubjectInput.getText().toString(),
                        mTextInput.getText().toString(), MainActivity.imageUri);
            }
        });
        registerReceiver(mSentReceiver, mSentFilter);
        registerReceiver(mReceivedReceiver, mReceivedFilter);
        final Intent intent = getIntent();
        final String notificationIndUrl = intent.getStringExtra(EXTRA_NOTIFICATION_URL);
        if (!TextUtils.isEmpty(notificationIndUrl)) {
            downloadMessage(notificationIndUrl);
        }
    }

    private void sendMessage(final String recipients, final String subject, final String text, Uri pictureUri) {
        Log.d(TAG, "Sending");
        mSendStatusView.setText("Envoi en cours...");
        mSendButton.setEnabled(false);
        final String fileName = "send." + String.valueOf(Math.abs(mRandom.nextLong())) + ".dat";
        mSendFile = new File(getCacheDir(), fileName);

        //mSendFile = new File(getPath(this, MainActivity.imageUri));

        // Making RPC call in non-UI thread
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                final byte[] pdu;
                try {
                    pdu = buildPdu(MmsMessagingDemo.this, recipients, subject, text);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                Uri writerUri = (new Uri.Builder())
                        .authority("com.example.android.apis.os.MmsFileProvider")
                        .path(fileName)
                        .scheme(ContentResolver.SCHEME_CONTENT)
                        .build();
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        MmsMessagingDemo.this, 0, new Intent(ACTION_MMS_SENT), 0);
                FileOutputStream writer = null;
                Uri contentUri = null;
                try {
                    writer = new FileOutputStream(mSendFile);
                    writer.write(pdu);
                    contentUri = writerUri;
                }
                catch (final IOException e) {
                    Log.e(TAG, "Error writing send file", e);
                }
                finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        }
                        catch (IOException e) {
                        }
                    }
                }

                if (contentUri != null) {
                    SmsManager.getDefault().sendMultimediaMessage(getApplicationContext(),
                            contentUri, null/*locationUrl*/, null/*configOverrides*/,
                            pendingIntent);
                }
                else {
                    Log.e(TAG, "Error writing sending Mms");
                    try {
                        pendingIntent.send(SmsManager.MMS_ERROR_IO_ERROR);
                    }
                    catch (PendingIntent.CanceledException ex) {
                        Log.e(TAG, "Mms pending intent cancelled?", ex);
                    }
                }
            }
        });
    }

    private void downloadMessage(final String locationUrl) {
        Log.d(TAG, "Downloading " + locationUrl);
        mSendStatusView.setText("Downloading");
        mSendButton.setEnabled(false);
        mRecipientsInput.setText("");
        mSubjectInput.setText("");
        mTextInput.setText("");
        final String fileName = "download." + String.valueOf(Math.abs(mRandom.nextLong())) + ".dat";
        mDownloadFile = new File(getCacheDir(), fileName);
        // Making RPC call in non-UI thread
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Uri contentUri = (new Uri.Builder())
                        .authority("com.example.android.apis.os.MmsFileProvider")
                        .path(fileName)
                        .scheme(ContentResolver.SCHEME_CONTENT)
                        .build();
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        MmsMessagingDemo.this, 0, new Intent(ACTION_MMS_RECEIVED), 0);
                SmsManager.getDefault().downloadMultimediaMessage(getApplicationContext(),
                        locationUrl, contentUri, null/*configOverrides*/, pendingIntent);
            }
        });
    }

    private void handleSentResult(int code, Intent intent) {
        mSendFile.delete();
        String status = "failed";
        if (code == Activity.RESULT_OK) {
            final byte[] response = intent.getByteArrayExtra(SmsManager.EXTRA_MMS_DATA);
            if (response != null) {
                final GenericPdu pdu = new PduParser(
                        response, shouldParseContentDisposition()).parse();
                if (pdu instanceof SendConf) {
                    final SendConf sendConf = (SendConf) pdu;
                    if (sendConf.getResponseStatus() == PduHeaders.RESPONSE_STATUS_OK) {
                        status = "Envoyé";
                    }
                    else {
                        Log.e(TAG, "MMS sent, error=" + sendConf.getResponseStatus());
                    }
                }
                else {
                    Log.e(TAG, "MMS sent, invalid response");
                }
            }
            else {
                Log.e(TAG, "MMS sent, empty response");
            }
        }
        else {
            Log.e(TAG, "MMS not sent, error=" + code);
        }

        mSendFile = null;
        mSendStatusView.setText(status);
        mSendButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSentReceiver != null) {
            unregisterReceiver(mSentReceiver);
        }
        if (mReceivedReceiver != null) {
            unregisterReceiver(mReceivedReceiver);
        }
    }

    private void handleReceivedResult(Context context, int code, Intent intent) throws Exception {
        String status = "echec";
        if (code == Activity.RESULT_OK) {
            try {
                final int nBytes = (int) mDownloadFile.length();
                FileInputStream reader = new FileInputStream(mDownloadFile);
                final byte[] response = new byte[nBytes];
                final int read = reader.read(response, 0, nBytes);
                if (read == nBytes) {
                    final GenericPdu pdu = new PduParser(
                            response, shouldParseContentDisposition()).parse();
                    if (pdu instanceof RetrieveConf) {
                        final RetrieveConf retrieveConf = (RetrieveConf) pdu;
                        mRecipientsInput.setText(getRecipients(context, retrieveConf));
                        mSubjectInput.setText(getSubject(retrieveConf));
                        mTextInput.setText(getMessageText(retrieveConf));
                        status = "Downloaded";
                    }
                    else {
                        Log.e(TAG, "MMS received, invalid response");
                    }
                }
                else {
                    Log.e(TAG, "MMS received, empty response");
                }
            }
            catch (FileNotFoundException e) {
                Log.e(TAG, "MMS received, file not found exception", e);
            }
            catch (IOException e) {
                Log.e(TAG, "MMS received, io exception", e);
            }
            finally {
                mDownloadFile.delete();
            }
        }
        else {
            Log.e(TAG, "MMS not received, error=" + code);
        }
        mDownloadFile = null;
        mSendStatusView.setText(status);
        mSendButton.setEnabled(true);
    }

    public static final long DEFAULT_EXPIRY_TIME = 7 * 24 * 60 * 60;
    public static final int DEFAULT_PRIORITY = PduHeaders.PRIORITY_NORMAL;

    private static final String TEXT_PART_FILENAME = "text_0.txt";
    private static final String sSmilText =
            "<smil>" +
                    "<head>" +
                    "<layout>" +
                    "<root-layout/>" +
                    "<region height=\"100%%\" id=\"Text\" left=\"0%%\" top=\"0%%\" width=\"100%%\"/>" +
                    "</layout>" +
                    "</head>" +
                    "<body>" +
                    "<par dur=\"8000ms\">" +
                    "<text src=\"%s\" region=\"Text\"/>" +
                    "</par>" +
                    "</body>" +
                    "</smil>";

    public static byte[] buildPdu(Context context, String recipients, String subject,
                                  String text) throws Exception {
        final SendReq req = new SendReq();
        // From, per spec
        final String lineNumber = getSimNumber(context);
        if (!TextUtils.isEmpty(lineNumber)) {
            req.setFrom(new EncodedStringValue(lineNumber));
        }
        // To
        EncodedStringValue[] encodedNumbers =
                EncodedStringValue.encodeStrings(recipients.split(" "));
        if (encodedNumbers != null) {
            req.setTo(encodedNumbers);
        }
        // Subject
        if (!TextUtils.isEmpty(subject)) {
            req.setSubject(new EncodedStringValue(subject));
        }
        // Date
        req.setDate(System.currentTimeMillis() / 1000);
        // Body
        PduBody body = new PduBody();
        // Add text part. Always add a smil part for compatibility, without it there
        // may be issues on some carriers/client apps
        final int size = addTextPart(body, text, true/* add text smil */);
        req.setBody(body);
        // Message size
        req.setMessageSize(size);
        // Message class
        req.setMessageClass(PduHeaders.MESSAGE_CLASS_PERSONAL_STR.getBytes());
        // Expiry
        req.setExpiry(DEFAULT_EXPIRY_TIME);
        try {
            // Priority
            req.setPriority(DEFAULT_PRIORITY);
            // Delivery report
            req.setDeliveryReport(PduHeaders.VALUE_NO);
            // Read report
            req.setReadReport(PduHeaders.VALUE_NO);
        }
        catch (InvalidHeaderValueException e) {
        }

        return new PduComposer(context, req).make();
    }

    private static int addTextPart(PduBody pb, String message, boolean addTextSmil) {
        final PduPart part = new PduPart();
        // Set Charset if it's a text media.
        part.setCharset(CharacterSets.UTF_8);
        // Set Content-Type.
        part.setContentType(ContentType.TEXT_PLAIN.getBytes());
        // Set Content-Location.
        part.setContentLocation(TEXT_PART_FILENAME.getBytes());
        int index = TEXT_PART_FILENAME.lastIndexOf(".");
        String contentId = (index == -1) ? TEXT_PART_FILENAME
                : TEXT_PART_FILENAME.substring(0, index);
        part.setContentId(contentId.getBytes());
        part.setData(message.getBytes());
        pb.addPart(part);
        if (addTextSmil) {
            final String smil = String.format(sSmilText, TEXT_PART_FILENAME);
            addSmilPart(pb, smil);
        }
        return part.getData().length;
    }

    private static void addSmilPart(PduBody pb, String smil) {
        final PduPart smilPart = new PduPart();
        smilPart.setContentId("smil".getBytes());
        smilPart.setContentLocation("smil.xml".getBytes());
        smilPart.setContentType(ContentType.APP_SMIL.getBytes());
        smilPart.setData(smil.getBytes());
        pb.addPart(0, smilPart);
    }

    private static String getRecipients(Context context, RetrieveConf retrieveConf) throws Exception {
        final String self = getSimNumber(context);
        final StringBuilder sb = new StringBuilder();
        if (retrieveConf.getFrom() != null) {
            sb.append(retrieveConf.getFrom().getString());
        }
        if (retrieveConf.getTo() != null) {
            for (EncodedStringValue to : retrieveConf.getTo()) {
                final String number = to.getString();
                if (!PhoneNumberUtils.compare(number, self)) {
                    sb.append(" ").append(to.getString());
                }
            }
        }
        if (retrieveConf.getCc() != null) {
            for (EncodedStringValue cc : retrieveConf.getCc()) {
                final String number = cc.getString();
                if (!PhoneNumberUtils.compare(number, self)) {
                    sb.append(" ").append(cc.getString());
                }
            }
        }
        return sb.toString();
    }

    private static String getSubject(RetrieveConf retrieveConf) {
        final EncodedStringValue subject = retrieveConf.getSubject();
        return subject != null ? subject.getString() : "";
    }

    private static String getMessageText(RetrieveConf retrieveConf) {
        final StringBuilder sb = new StringBuilder();
        final PduBody body = retrieveConf.getBody();
        if (body != null) {
            for (int i = 0; i < body.getPartsNum(); i++) {
                final PduPart part = body.getPart(i);
                if (part != null
                        && part.getContentType() != null
                        && ContentType.isTextType(new String(part.getContentType()))) {
                    sb.append(new String(part.getData()));
                }
            }
        }
        return sb.toString();
    }

    private static String getSimNumber(Context context) throws Exception {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("Permission manquante");
        }
        return telephonyManager.getLine1Number();
    }

    public static boolean shouldParseContentDisposition() {
        return SmsManager
                .getDefault()
                .getCarrierConfigValues()
                .getBoolean(SmsManager.MMS_CONFIG_SUPPORT_MMS_CONTENT_DISPOSITION, true);
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
