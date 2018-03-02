package com.creativeconflux.srdp.mycard.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.creativeconflux.srdp.mycard.R;

/**
 * Created by srdpatel on 2/25/2018.
 */

public class SharedPref {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public static SharedPreferences getSharedPref(Context mContext){
        if (mSharedPreferences == null){
            mSharedPreferences = mContext.getSharedPreferences(mContext.getResources().getString(R.string.sharedPref),
                    Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static SharedPreferences.Editor getEditor(Context mContext){
        if (mEditor == null){
            mEditor = getSharedPref(mContext).edit();
        }
        return mEditor;
    }
}
