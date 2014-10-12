/**
 * (C)opyright 2012 - UrbanPulse - All rights Reserved Released by CARDIWEB File : OneTimePopup.java
 * 
 * @date 30 mai 2012
 * @author mbaroukh
 */

package com.example.handlerexemplesolution.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.handlerexemplesolution.R;

/** Gestion des boite de dialogue. */
public class PopupsManager {

    public static Dialog createProgressPopup(final Activity activity, final String bodyText) {

        final Dialog dialog = new Dialog(activity);

        // Get the layout inflater
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_progress, null);

        ((TextView) dialogView.findViewById(R.id.textView1)).setText(bodyText);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(final DialogInterface arg0, final int keyCode, final KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });

        // Show Dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(dialogView);

        return dialog;
    }

}
