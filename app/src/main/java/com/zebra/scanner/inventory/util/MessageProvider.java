package com.zebra.scanner.inventory.util;

/**
 * Created by harshas on 2/23/17.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class MessageProvider {

    public static AlertDialog displayAlert(Context context, int title, int message, int posBtn) {
        return displayAlert(context, title, message, posBtn, null, 0, null);
    }

    public static AlertDialog displayAlert(Context context, int title, int message, int posBtn, OnClickListener posBtnListner, int negBtn, OnClickListener negBtnListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        if(posBtn != 0)builder.setPositiveButton(posBtn, posBtnListner);
        if(negBtn != 0)builder.setNegativeButton(negBtn, negBtnListner);

        AlertDialog dialog = builder.create();
        if (context != null && !((Activity)context).isFinishing()) dialog.show();
        return dialog;
    }

    public static AlertDialog displayAlert(Context context, CharSequence title, CharSequence message, CharSequence posBtn, OnClickListener posBtnListner, CharSequence negBtn, OnClickListener negBtnListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        if(posBtn != null && posBtn.length() > 0)builder.setPositiveButton(posBtn, posBtnListner);
        if(negBtn != null && negBtn.length() > 0)builder.setNegativeButton(negBtn, negBtnListner);

        AlertDialog dialog = builder.create();
        if (context != null && !((Activity)context).isFinishing()) dialog.show();
        return dialog;
    }

    public static AlertDialog displayAlert(Context context, CharSequence title, CharSequence message, CharSequence posBtn) {
        return displayAlert(context, title, message, posBtn, null, null, null);
    }

}
