/*
 * Copyright (c) 2016, by Sergio Arnillas
 */

package com.serarni.qre_ntradas;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/** Allow sending chrash reports
 * Created by serarni on 27/08/2016.
 */

@ReportsCrashes(
        mailTo = AppConstants.EMAIL_ERROR_NOTIFICATION,
        mode = ReportingInteractionMode.DIALOG,
        resDialogTitle = R.string.crash_error_title,
        resDialogText = R.string.crash_error_desc,
        resDialogTheme = R.style.AppTheme,
        resDialogPositiveButtonText = R.string.send,
        resDialogNegativeButtonText = R.string.cancel,
        resDialogIcon = R.mipmap.ic_launcher
)
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // Initialise ACRA
        ACRA.init(this);
    }

}
