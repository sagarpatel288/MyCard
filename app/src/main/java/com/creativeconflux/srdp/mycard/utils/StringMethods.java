package com.creativeconflux.srdp.mycard.utils;

import android.support.design.widget.TextInputEditText;

/**
 * Created by srdpatel on 3/2/2018.
 */

public class StringMethods {

    public static String getNonNullString (TextInputEditText textInputEditText, String defaultString){
        return  textInputEditText.getText().toString().isEmpty() ? defaultString : textInputEditText.getText().toString();
    }

    public static String getNonNullString (String stringValue, String defaultString){
        return  stringValue == null || stringValue.isEmpty() ? "" : stringValue;
    }
}
