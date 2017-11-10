package com.reactiveandroid.sample.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class AppUtils {

    public static void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(View view) {
        if (view == null) return;
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Log.e("Sample", "Can't show keyboard ", e);
        }
    }

}
