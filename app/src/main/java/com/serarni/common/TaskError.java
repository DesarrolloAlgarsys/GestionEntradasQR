/*
 * Copyright (c) 2016. All rights reserved.
 */

package com.serarni.common;

public class TaskError {

    public TaskError(){
        mError = "";
    }

    public TaskError(String sError){
        mError = sError;
        if (null==sError){
            mError = "";
        }
    }

    public TaskError(Throwable t){
        mError = "";
        if (null!=t){
            mError = t.getLocalizedMessage();
        }
    }

    public TaskError(Exception e){
        mError = "";
        if (null!=e){
            mError = e.getLocalizedMessage();
        }
    }

    private String mError;

    public String getError() {
        return mError;
    }

    @Override
    public String toString() {
        return mError;
    }
}
