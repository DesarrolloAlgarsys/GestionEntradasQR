package com.serarni.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.util.Log;

import com.serarni.qre_ntradas.AppConstants;

/** Helpers
 * Created by serarni on 10/09/2016.
 */
public class UtilsHelper {


    private static final String TAG_LOG = UtilsHelper.class.getSimpleName();

    public static void playBeep(int iTimes, int bipMillisecondsDuration, int iToneGeneratorTone) {
        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_DTMF, ToneGenerator.MAX_VOLUME);
        toneGen.startTone(iToneGeneratorTone, bipMillisecondsDuration);
        iTimes--;
        while(iTimes>0){
            try {
                Thread.sleep(100);
                toneGen.startTone(iToneGeneratorTone, AppConstants.BIP_DURATION);
            } catch (InterruptedException e) {
                Log.e(TAG_LOG, "playBeep() error sleeping thread");
            } finally {
                iTimes--;
            }
        }
    }

    public static void playVibration(long lMillisecondsDuration, Context context) {
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(lMillisecondsDuration);
    }
}
