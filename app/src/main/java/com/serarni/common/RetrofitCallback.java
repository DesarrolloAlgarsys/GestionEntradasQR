/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.common;

import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by serarni on 26/08/2016.
 */
public class RetrofitCallback<T> implements Callback<T> {
    private static final String TAG = RetrofitCallback.class.getSimpleName();

    private String mLogFunctionName;
    private TaskCallback<T> mCallback;

    public RetrofitCallback(@NonNull TaskCallback<T> callback, String sFunctionName){
        mLogFunctionName = sFunctionName;
        mCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Log.d(TAG, mLogFunctionName + " success");
        if (response.isSuccessful() && null!=response.body()){
            mCallback.success(response.body());
        }
        else {
            Log.w(TAG, mLogFunctionName + " null result");
            mCallback.error(new TaskError(response.message()));
        }
        mCallback.completed();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        TaskError error = new TaskError(t);
        Log.e(TAG, mLogFunctionName + " error: " + error);
        mCallback.error(error);
        mCallback.completed();
    }
}
