/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.qre_ntradas.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.serarni.common.TaskCallback;
import com.serarni.common.TaskError;
import com.serarni.common.ViewHelper;
import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.server.ServerManager;
import com.serarni.qre_ntradas.server.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**General controller
 * Created by serarni on 24/08/2016.
 */
public class DataManager {

    private static final String TAG = ServerManager.class.getSimpleName();

    private User mUserAuth;
    private List<TicketValidationResult> mListValidations;
    private static ArrayList<Events> mListEvents;
    private static DataManager mSingleton;

    private String mLastTicketValidationUrl;

    private DataManager(){
        mUserAuth = null;
        mLastTicketValidationUrl = null;
        mListValidations = new ArrayList<>();
        mListEvents = new ArrayList<>();
    }

    public static DataManager getSingleton(){
        if (null==mSingleton){
            synchronized (DataManager.class){
                if (null==mSingleton){
                    mSingleton = new DataManager();
                }
            }
        }
        return mSingleton;
    }

    public static List<Events> getListEvents(){
        if (null==mListEvents){
            synchronized (DataManager.class){
                if (null==mListEvents){
                    mListEvents = new ArrayList<>();
                }
            }
        }
        return mListEvents;
    }

    public void initialize(Context context) {
        ServerManager.getSingleton().initialize(context);
    }

    public boolean isUserAuthenticated(){
        return null!=mUserAuth;
    }

    public String getUserName(){
        return null!=mUserAuth?mUserAuth.getUserName():"";
    }

    public void authenticate(
            final String sLogin,
            final String sPassword,
            boolean bSilenLogin,
            final Activity context,
            @Nullable final TaskCallback<User> callback){

        final ProgressDialog pDlg;
        if (!bSilenLogin && null!=context){
            String sLoadingMsg = context.getString(R.string.please_wait);
            pDlg = ViewHelper.showLoadingProgressDialog(context, sLoadingMsg);
        }
        else {
            pDlg = null;
        }

        ServerManager.getSingleton().authenticate(
                sLogin,
                sPassword,
                new TaskCallback<ServerResponse<User>>() {
                    @Override
                    public void success(@NonNull ServerResponse<User> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            mUserAuth = response.getResult();
                            // set user name due it is received as empty by the server
                            mUserAuth.setUserName(sLogin);
                            // store data into app preferences to allow authenticate in silent mode later
                            AppPreferences appPref = AppPreferences.getSingleton(context);
                            if (null!=appPref){
                                appPref.setLastUserCredentials(sLogin, sPassword, context);
                            }
                            if (null!=callback){
                                callback.success(mUserAuth);
                            }
                        }
                        else {
                            if (null!=callback){
                                callback.error(new TaskError(response.getMessage("Server response unexpected")));
                            }
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        if (null!=callback){
                            callback.error(e);
                        }
                    }

                    @Override
                    public void completed() {
                        if (null!=pDlg){
                            pDlg.dismiss();
                        }
                        if (null!=callback){
                            callback.completed();
                        }
                    }
                }
        );
    }

    public void validateTicket(final String sUrl,final @NonNull TaskCallback<TicketValidationResult> callback){
        ServerManager.getSingleton().validateTicket(
                sUrl,
                mUserAuth.getUserName(),
                new TaskCallback<ServerResponse<TicketValidationResult>>() {
                    @Override
                    public void success(@NonNull ServerResponse<TicketValidationResult> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            TicketValidationResult validation = response.getResult();
                            mListValidations.add(validation);
                            mLastTicketValidationUrl = sUrl;
                            callback.success(validation);
                        }
                        else {
                            callback.error(new TaskError(response.getMessage("Server response unexpected")));
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        callback.error(e);
                    }

                    @Override
                    public void completed() {
                        callback.completed();
                    }
                }
        );
    }

    public void reactivateTicket(final String sUrl,final @NonNull TaskCallback<TicketValidationResult> callback){
        ServerManager.getSingleton().reactivateTicket(
                sUrl,
                mUserAuth.getUserName(),
                new TaskCallback<ServerResponse<TicketValidationResult>>() {
                    @Override
                    public void success(@NonNull ServerResponse<TicketValidationResult> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            TicketValidationResult validation = response.getResult();
                            mListValidations.add(validation);
                            mLastTicketValidationUrl = sUrl;
                            callback.success(validation);
                        }
                        else {
                            callback.error(new TaskError(response.getMessage("Server response unexpected")));
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        callback.error(e);
                    }

                    @Override
                    public void completed() {
                        callback.completed();
                    }
                }
        );
    }

    public void validateClient(final String sUrl,final @NonNull TaskCallback<TicketValidationResult> callback){
        ServerManager.getSingleton().validateClient(
                sUrl,
                mUserAuth.getUserName(),
                new TaskCallback<ServerResponse<TicketValidationResult>>() {
                    @Override
                    public void success(@NonNull ServerResponse<TicketValidationResult> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            TicketValidationResult validation = response.getResult();
                            mListValidations.add(validation);
                            mLastTicketValidationUrl = sUrl;
                            callback.success(validation);
                        }
                        else {
                            callback.error(new TaskError(response.getMessage("Server response unexpected")));
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        callback.error(e);
                    }

                    @Override
                    public void completed() {
                        callback.completed();
                    }
                }
        );
    }

    public void reactivateClient(final String sUrl,final @NonNull TaskCallback<TicketValidationResult> callback){
        ServerManager.getSingleton().reactivateClient(
                sUrl,
                mUserAuth.getUserName(),
                new TaskCallback<ServerResponse<TicketValidationResult>>() {
                    @Override
                    public void success(@NonNull ServerResponse<TicketValidationResult> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            TicketValidationResult validation = response.getResult();
                            mListValidations.add(validation);
                            mLastTicketValidationUrl = sUrl;
                            callback.success(validation);
                        }
                        else {
                            callback.error(new TaskError(response.getMessage("Server response unexpected")));
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        callback.error(e);
                    }

                    @Override
                    public void completed() {
                        callback.completed();
                    }
                }
        );
    }

    public void getEvents(final @NonNull TaskCallback<ArrayList<Events>> callback){
        ServerManager.getSingleton().getEvents(
                mUserAuth.getUserName(),
                new TaskCallback<ServerResponse<ArrayList<Events>>>() {
                    @Override
                    public void success(@NonNull ServerResponse<ArrayList<Events>> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            callback.success(response.getResult());
                        }
                        else {
                            callback.error(new TaskError(response.getMessage("Server response unexpected")));
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        callback.error(e);
                    }

                    @Override
                    public void completed() {
                        callback.completed();
                    }
                }
        );
    }

    public void getClients(final String sID, final @NonNull TaskCallback<ArrayList<Clients>> callback){
        ServerManager.getSingleton().getClients(
                mUserAuth.getUserName(),
                sID,
                new TaskCallback<ServerResponse<ArrayList<Clients>>>() {
                    @Override
                    public void success(@NonNull ServerResponse<ArrayList<Clients>> response) {
                        if (response.isSuccess() && null!= response.getResult()){
                            callback.success(response.getResult());
                        }
                        else {
                            callback.error(new TaskError(response.getMessage("Server response unexpected")));
                        }
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        callback.error(e);
                    }

                    @Override
                    public void completed() {
                        callback.completed();
                    }
                }
        );
    }

    public String getLastTicketValidationUrl(){
        return mLastTicketValidationUrl;
    }

    public void resetLastTicketValidationUrl(){
        mLastTicketValidationUrl = "";
    }
}
