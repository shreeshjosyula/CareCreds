package com.example.carecreds.Utils;

import android.view.View;

import com.example.carecreds.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtils {

    public static void showCustomSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(view.getContext().getResources().getColor(R.color.dark_theme));
        snackbar.setTextColor(view.getContext().getResources().getColor(android.R.color.black));

        snackbar.show();
    }
}
