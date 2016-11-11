/*
 * Copyright (c) 2016, by Sergio Arnillas.
 */

package com.serarni.qre_ntradas.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/** User authenticated into server
 * Created by serarni on 24/08/2016.
 */
public class User{
    @Expose
    @SerializedName("username")
    private String mUsername;

    @Expose
    @SerializedName("password")
    private String mPassword;

    public String getUserName() {
        return mUsername;
    }

    public void setUserName(String sUserName) {
        mUsername = sUserName;
    }
}
