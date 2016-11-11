package com.serarni.qre_ntradas.model;

/**
 * Created by SiliconVall on 24/10/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**Get the event list from the user
 */
public class Clients{

    @Expose
    @SerializedName("customer_name")
    private String mCName;

    @Expose
    @SerializedName("customer_dni")
    private String mDNI;

    @Expose
    @SerializedName("customer_phone")
    private String mPhone;

    @Expose
    @SerializedName("booking_status")
    private String mBStatus;

    @Expose
    @SerializedName("event_title")
    private String mEName;

    @Expose
    @SerializedName("ticket")
    private String mTCategory;

    @Expose
    @SerializedName("checked")
    private String mChecked;

    @Expose
    @SerializedName("id")
    private String mQR;

    public String getClientName() {
        return mCName;
    }
    public void setClientName(String sCName) {
        mCName = sCName;
    }

    public String getClientDNI() {
        return mDNI;
    }
    public void setClientDNI(String sDNI) {
        mDNI = sDNI;
    }

    public String getClientPhone() {
        return mPhone;
    }
    public void setClientPhone(String sPhone) {
        mPhone = sPhone;
    }

    public String getBStatus() {
        return mBStatus;
    }
    public void setBStatus(String sBStatus) {
        mBStatus = sBStatus;
    }

    public String getEventName() {
        return mEName;
    }
    public void setEventName(String sEName) {
        mEName = sEName;
    }

    public String getTCategory() {
        return mTCategory;
    }
    public void setTCategory(String sTCategory) {
        mTCategory = sTCategory;
    }

    public String getClientCheck() {
        return mChecked;
    }
    public void setClientCheck(String sChecked) {
        mChecked = sChecked;
    }

    public String getClientQR() {
        return mQR;
    }
    public void setClientQR(String sQR) {
        mQR = sQR;
    }
}