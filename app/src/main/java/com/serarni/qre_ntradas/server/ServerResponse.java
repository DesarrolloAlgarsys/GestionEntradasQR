/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.qre_ntradas.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/** Wraps the response received from server
 * Created by serarni on 24/08/2016.
 */
public class ServerResponse<T> {

    @Expose
    @SerializedName("success")
    private boolean mSuccess;

    @Expose
    @SerializedName("results")
    private T mResult;

    @Expose
    @SerializedName("msj")
    private String mMessage;

    public String getMessage(String sDefaultMsg) {
        return null!=mMessage && !mMessage.isEmpty()?mMessage:sDefaultMsg;
    }

    public T getResult() {
        return mResult;
    }

    public boolean isSuccess() {
        return mSuccess;
    }
}
