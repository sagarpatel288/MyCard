package com.creativeconflux.srdp.mycard.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by srdpatel on 3/2/2018.
 */

public class KeyboardUtils {

    private static InputMethodManager inputMethodManager;

    public static InputMethodManager getInputMethodManager(Context context){
        if (inputMethodManager == null){
            inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        }

        return inputMethodManager;
    }

    public static void hideKeyboard(Activity activity) {
//        getInputMethodManager(context).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        View view = activity.getCurrentFocus();
        if (view != null){
            getInputMethodManager(activity).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard (Context context){
        getInputMethodManager(context).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

}
