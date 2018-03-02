package com.creativeconflux.srdp.mycard.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by srdpatel on 3/1/2018.
 */

public class MyColorObj implements Parcelable {

    HashMap<String, Integer> hashMap;

    public HashMap<String, Integer> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    public MyColorObj(HashMap<String, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    public MyColorObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.hashMap);
    }

    protected MyColorObj(Parcel in) {
        this.hashMap = (HashMap<String, Integer>) in.readSerializable();
    }

    public static final Creator<MyColorObj> CREATOR = new Creator<MyColorObj>() {
        @Override
        public MyColorObj createFromParcel(Parcel source) {
            return new MyColorObj(source);
        }

        @Override
        public MyColorObj[] newArray(int size) {
            return new MyColorObj[size];
        }
    };
}
