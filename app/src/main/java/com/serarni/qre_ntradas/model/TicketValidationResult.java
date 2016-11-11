/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.qre_ntradas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**Response after validating a QR ticket
 * Created by serarni on 26/08/2016.
 */
public class TicketValidationResult {

    @Expose
    @SerializedName("status")
    private String mStatus;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("passport")
    @Expose
    private String mPassport;


    public String getName() {
        return mName;
    }

    public String getPassport() {
        return mPassport;
    }

    public String gemStatus() {
        return mStatus;
    }

    @Override
    public String toString() {
        String result = "\n"+mStatus;
        if (null!=mName && mName.length()>0){
            result += "\n\nNombre: " + mName;
        }
        if (null!=mPassport && mPassport.length()>0){
            result += "\nDNI: " + mPassport;
        }
        result += "\n";
        return result;
    }
}
