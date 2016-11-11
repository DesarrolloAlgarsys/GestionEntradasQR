/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.serarni.qre_ntradas.R;

/**
 * Created by serarni on 1/10/2015.
 */
public class ViewHelper {
    // CONSTANTS
    private static final String TAG_LOG = ViewHelper.class.getSimpleName();
    // ATTRIBUTES

    // CONSTRUCTOR

    // PRIVATE METHODS

    // PUBLIC METHODS
    static public void showDialogMessage(final int iIdTitle, final int iIdMessage, Context context){
        if (null!=context){
            showDialogMessage(iIdTitle, context.getString(iIdMessage), context);
        }
    }

    static public void showDialogMessage(final int iIdTitle, final String sMessage, Context context){
        if (null==context) return;


        Dialog dlg = new AlertDialog.Builder(context)
                .setTitle(context.getString(iIdTitle))
                .setMessage(sMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.accept, null)
                .create();
        dlg.show();
    }

    static public ProgressDialog showLoadingProgressDialog(Context context, int iIdMessage){
        ProgressDialog result =
                ProgressDialog.show(context,
                        context.getString(R.string.connecting),
                        context.getString(iIdMessage),
                        true);
                result.setCancelable(false);
        return result;
    }

    static public ProgressDialog showLoadingProgressDialog(Context context, String sMessage){
        ProgressDialog result =
                ProgressDialog.show(context,
                        context.getString(R.string.connecting),
                        sMessage,
                        true);
        result.setCancelable(false);
        return result;
    }

}
