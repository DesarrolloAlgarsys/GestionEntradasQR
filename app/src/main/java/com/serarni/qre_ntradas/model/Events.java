package com.serarni.qre_ntradas.model;

/**
 * Created by SiliconVall on 10/10/2016.
 */

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**Get the event list from the user
 */
public class Events{

    @Expose
    @SerializedName("id")
    private String mId;

    @Expose
    @SerializedName("hash")
    private String mImage;

    @Expose
    @SerializedName("event_title")
    private String mName;

    @Expose
    @SerializedName("event_description")
    private String mDescription;

    @Expose
    @SerializedName("event_start_ts")
    private String mDate;

    public String getEventID() {
        return mId;
    }
    public void setEventID(String sID) {
        mId = sID;
    }

    public String getEventImage() {
        return mImage;
    }
    public void setEventImage(String sImage) {
        mImage = sImage;
    }

    public String getEventName() {
        return mName;
    }
    public void setEventName(String sName) {
        mName = sName;
    }

    public String getEventDescription() {
        return mDescription;
    }
    public void setEventDescription(String sDescription) {
        mDescription = sDescription;
    }

    public String getEventDate() {
        return mDate;
    }
    public void setEventDate(String sDate) {
        mDate = sDate;
    }
}
