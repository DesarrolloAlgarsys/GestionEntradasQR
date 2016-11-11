/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.qre_ntradas.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.serarni.qre_ntradas.server.ServerManager;

/** Preferences of the app stored persistently
 * Created by serarni on 24/08/2016.
 */
public class AppPreferences{

    private static final String TAG = ServerManager.class.getSimpleName();
    private static final String SHARED_PREF_LAST_LOGIN = "lastLogin";
    private static final String SHARED_PREF_LAST_PASSWORD = "lastPwd";
    private static final String SHARED_PREF_VIBRATION_ON_VALIDATION = "vibrationOnValidation";
    private static final String SHARED_PREF_SOUND_ON_VALIDATION = "soundOnValidation";

    private static AppPreferences mSingleton = null;
    private String mLastUserLogin;
    private String mLastUserPassword;
    private boolean mVibrationOnValidation;
    private boolean mSoundOnValidation;

    private AppPreferences(){
        throw new RuntimeException("Default constructor of AppPreferences is not supported");
    }

    private AppPreferences(@NonNull Context context){
        loadPreferences(context);
    }

    public static @Nullable AppPreferences getSingleton(@Nullable Context context){
        if (null==mSingleton && null!=context){
            synchronized (AppPreferences.class){
                if (null==mSingleton){
                    mSingleton = new AppPreferences(context);
                }
            }
        }
        return mSingleton;
    }

    private void savePreferences(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_PREF_LAST_LOGIN, mLastUserLogin);
        editor.putString(SHARED_PREF_LAST_PASSWORD, mLastUserPassword);// TODO: save password encrypted
        editor.putBoolean(SHARED_PREF_SOUND_ON_VALIDATION, mSoundOnValidation);
        editor.putBoolean(SHARED_PREF_VIBRATION_ON_VALIDATION, mVibrationOnValidation);
        editor.apply();
    }

    private void loadPreferences(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        mLastUserLogin = prefs.getString(SHARED_PREF_LAST_LOGIN, "");
        mLastUserPassword = prefs.getString(SHARED_PREF_LAST_PASSWORD, ""); // TODO: decrypt password
        mVibrationOnValidation = prefs.getBoolean(SHARED_PREF_VIBRATION_ON_VALIDATION, true);
        mSoundOnValidation = prefs.getBoolean(SHARED_PREF_SOUND_ON_VALIDATION, true);
    }

    public @NonNull String getLastUserLogin() {
        return null!=mLastUserLogin?mLastUserLogin:"";
    }

    public @NonNull String getLastUserPassword() {
        return null!=mLastUserPassword? mLastUserPassword:"";
    }

    public void setLastUserCredentials(String sUserLogin, String sUserPassword, @NonNull Context context) {
        mLastUserLogin = sUserLogin;
        mLastUserPassword = sUserPassword;
        savePreferences(context);
    }

    public void setSoundOnValidation(boolean bSoundOnValidation, @NonNull Context context) {
        mSoundOnValidation = bSoundOnValidation;
        savePreferences(context);
    }

    public boolean getSoundOnValidation() {
        return mSoundOnValidation;
    }

    public void setVibrationOnValidation(boolean bVibrationOnValidation, @NonNull Context context) {
        mVibrationOnValidation = bVibrationOnValidation;
        savePreferences(context);
    }

    public boolean getVibrationOnValidation() {
        return mVibrationOnValidation;
    }
}
