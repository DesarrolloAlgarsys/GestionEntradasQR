/*
 * Copyright (c) 2016, by Sergio Arnillas.
 */

package com.serarni.qre_ntradas.server;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.GsonBuilder;
import com.serarni.common.MyLoggingInterceptor;
import com.serarni.common.RetrofitCallback;
import com.serarni.common.TaskCallback;
import com.serarni.qre_ntradas.AppConstants;
import com.serarni.qre_ntradas.BuildConfig;
import com.serarni.qre_ntradas.model.Clients;
import com.serarni.qre_ntradas.model.Events;
import com.serarni.qre_ntradas.model.TicketValidationResult;
import com.serarni.qre_ntradas.model.User;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Manager to handle all server requests
 * Created by serarni on 23/08/2016.
 */
public class ServerManager {

    private static final String TAG = ServerManager.class.getSimpleName();

    private static ServerManager mSingleton;
    private ServerApi mServerApi;

    private ServerManager(){
        mServerApi = null;
    }

    public void initialize(Context context){
        if (null==mServerApi){
            // cookies handler
            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                    .cookieJar(cookieJar);// add your other interceptors …

            if (BuildConfig.DEBUG){
                MyLoggingInterceptor logging = new MyLoggingInterceptor();
                //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClientBuilder.addInterceptor(logging);
            }


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.SERVER_URL)
                    .client(httpClientBuilder.build())
                    .addConverterFactory(getGsonConverter())
                    .build();
            mServerApi =  retrofit.create(ServerApi.class);
        }
    }

    public static Converter.Factory getGsonConverter(){
        return GsonConverterFactory.create(
                new GsonBuilder()
                        .setLenient()
                        .create());
        //return GsonConverterFactory.create();
    }

    public static ServerManager getSingleton(){
        if (null==mSingleton){
            synchronized (ServerManager.class){
                if (null==mSingleton){
                    mSingleton = new ServerManager();
                }
            }
        }
        return mSingleton;
    }

    public void authenticate(String sLogin, String sPassword, @NonNull TaskCallback<ServerResponse<User>> callback){
        Call<ServerResponse<User>> call = mServerApi.authenticate(sLogin, sPassword);
        call.enqueue(new RetrofitCallback<>(callback, "authenticate()"));
    }

    public void validateTicket(String sUrl, String sLogin, @NonNull TaskCallback<ServerResponse<TicketValidationResult>> callback){
        Call<ServerResponse<TicketValidationResult>> call = mServerApi.validateTicket(getUrlParameter(sUrl, "id"), sLogin);
        call.enqueue(new RetrofitCallback<>(callback, "validateTicket()"));
    }

    public void reactivateTicket(String sUrl, String sLogin, @NonNull TaskCallback<ServerResponse<TicketValidationResult>> callback){
        Call<ServerResponse<TicketValidationResult>> call = mServerApi.reactivateTicket(getUrlParameter(sUrl, "id"), sLogin);
        call.enqueue(new RetrofitCallback<>(callback, "validateTicket()"));
    }

    public void validateClient(String sUrl, String sLogin, @NonNull TaskCallback<ServerResponse<TicketValidationResult>> callback){
        Call<ServerResponse<TicketValidationResult>> call = mServerApi.validateTicket(sUrl, sLogin);
        call.enqueue(new RetrofitCallback<>(callback, "validateTicket()"));
    }

    public void reactivateClient(String sUrl, String sLogin, @NonNull TaskCallback<ServerResponse<TicketValidationResult>> callback){
        Call<ServerResponse<TicketValidationResult>> call = mServerApi.reactivateTicket(sUrl, sLogin);
        call.enqueue(new RetrofitCallback<>(callback, "validateTicket()"));
    }

    public void getEvents (String sLogin, @NonNull TaskCallback<ServerResponse<ArrayList<Events>>> callback){
        Call<ServerResponse<ArrayList<Events>>> call = mServerApi.getEvents(sLogin, 1);
        call.enqueue(new RetrofitCallback<>(callback, "getEvents()"));
    }

    public void getClients (String sLogin, String sID, @NonNull TaskCallback<ServerResponse<ArrayList<Clients>>> callback){
        Call<ServerResponse<ArrayList<Clients>>> call = mServerApi.getClients(sLogin, sID, 1);
        call.enqueue(new RetrofitCallback<>(callback, "getEvents()"));
    }

    private String getUrlParameter(String url, String param){
        String result = "";
        String[] params = new String[1];
        if (url.contains("?")){
            url = url.split("\\?")[1];
        }
        if (url.contains("&")){
            params = url.split("\\&");
        }
        else {
            params[0] = url;
        }
        for (String p : params){
            if (p.contains(param+"=")){
                result = p.split("=")[1];
                break;
            }
        }
        return result;
    }
}
