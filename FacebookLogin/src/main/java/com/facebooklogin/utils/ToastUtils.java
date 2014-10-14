/**
 * (C)opyright 2012 - UrbanPulse - All rights Reserved Released by CARDIWEB File : ToastUtils.java
 *
 * @date 13 juil. 2012
 * @author afouques
 */

package com.facebooklogin.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/** @author afouques */
public class ToastUtils {

    public static Toast toast;
    public static int lastToastTaskId;

    public static void showToastOnUIThread(final Context context, final String message, final int length) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final int newLastToastTaskId = ((Activity) context).getTaskId();

                    //on est sur la même activity
                    if (toast != null && lastToastTaskId == newLastToastTaskId) {
                        toast.setText(message);
                        toast.show();
                    }
                    else {
                        toast = Toast.makeText(context, message, length);
                        lastToastTaskId = newLastToastTaskId;
                        toast.show();
                    }

                }
            });
        }
    }

    public static void showToastOnUIThread(final Context context, final int messageId) {
        showToastOnUIThread(context, context.getResources().getString(messageId), Toast.LENGTH_LONG);
    }

    public static void showToastOnUIThread(final Context context, final String message) {
        showToastOnUIThread(context, message, Toast.LENGTH_LONG);
    }

    public static void showToastOnUIThread(final Context context, final int messageId, final int length) {
        showToastOnUIThread(context, context.getResources().getString(messageId), length);
    }

}
