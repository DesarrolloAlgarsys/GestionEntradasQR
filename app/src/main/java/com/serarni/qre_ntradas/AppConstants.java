/*
 * Copyright (c) 2016, by Sergio Arnillas.
 */

package com.serarni.qre_ntradas;

import android.media.ToneGenerator;

public class AppConstants {
    //public static final String SERVER_URL = "http://www.serarni.com/qr/";
    public static final String SERVER_URL = "http://www.gestionentradas.com/gestion/";
    public static final String EMAIL_ERROR_NOTIFICATION = "juancarlos@algarsys.com";

    public static final int VIBRATION_ON_ERROR = 1000; // milliseconds
    public static final int VIBRATION_ON_SUCCESS = 150; // milliseconds
    public static final int BIP_DURATION = 1000; // milliseconds
    public static final int SOUNDS_ON_SUCCESS = ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE;
    public static final int SOUNDS_ON_ERROR = ToneGenerator.TONE_CDMA_PIP;
    public static final int SOUNDS_TIMES_ON_SUCCESS = 1;
    public static final int SOUNDS_TIMES_ON_ERROR = 1;
}
