package com.example.app.pizzaapp.util;

import android.content.Context;
import android.content.DialogInterface;

import com.example.app.pizzaapp.R;

/**
 * Created by juandiegoGL on 4/11/17.
 */

public class DialogUtil {

    public static void showErrorDialog(final DialogUtilListener listener, Context context, String message) {
        new android.support.v7.app.AlertDialog.Builder(context, R.style.ActionThemeDialog)
                .setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onPositiveButtonClicked();
                    }
                })
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onNegativeButtonClicked(dialogInterface);
                    }
                })
                .show();
    }
}
