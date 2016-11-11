package com.serarni.qre_ntradas.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.Result;
import com.serarni.common.TaskCallback;
import com.serarni.common.TaskError;
import com.serarni.common.UtilsHelper;
import com.serarni.common.ViewHelper;
import com.serarni.qre_ntradas.AppConstants;
import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.model.AppPreferences;
import com.serarni.qre_ntradas.model.DataManager;
import com.serarni.qre_ntradas.model.TicketValidationResult;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ReactivateTicketActivity
        extends AppCompatActivity
        implements ZXingScannerView.ResultHandler{

    private static final String TAG_LOG = ReactivateTicketActivity.class.getSimpleName();

    private ZXingScannerView mScannerView;
    private Snackbar mSnackBarResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactivate_ticket);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        //setContentView(mScannerView);                // Set the scanner view as the content view

        ViewGroup mainContainer = (ViewGroup) findViewById(R.id.layout_contentMain);
        mainContainer.addView(mScannerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.eventExitTitle);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init data manager allows to init serverManager and any thing than requires context
        DataManager.getSingleton().initialize(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        if (DataManager.getSingleton().isUserAuthenticated()){
            mScannerView.startCamera();         // Start camera on resume
        }
        else {
            // redirect to Login activity and close this one
            redirectToLoginActivity(true);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                this.finish();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void redirectToLoginActivity(boolean bAutoLogin) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_AUTO_LOGIN, bAutoLogin);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d(TAG_LOG, rawResult.getText()); // Prints scan results
        Log.d(TAG_LOG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        onTicketQrReceived(rawResult.getText());
    }

    private void onTicketQrReceived(String sUrlTicket) {
        // check if ticket is not the same than last ticket
        if (null!=sUrlTicket && sUrlTicket.equals(DataManager.getSingleton().getLastTicketValidationUrl())){
            // do nothing
            prepareCameraToScanAgain();
            return;
        }

        final Dialog pDlg = ViewHelper.showLoadingProgressDialog(this, R.string.validating_ticket);
        DataManager.getSingleton().reactivateTicket(
                sUrlTicket,
                new TaskCallback<TicketValidationResult>() {
                    @Override
                    public void success(@NonNull TicketValidationResult result) {
                        mSnackBarResult = Snackbar.make(mScannerView, result.toString(), Snackbar.LENGTH_LONG);
                        mSnackBarResult.setAction(R.string.accept, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSnackBarResult.dismiss();
                            }
                        });
                        mSnackBarResult.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                DataManager.getSingleton().resetLastTicketValidationUrl();
                            }
                        });

                        mSnackBarResult.setDuration(Snackbar.LENGTH_INDEFINITE);
                        View snackBarView = mSnackBarResult.getView();
                        snackBarView.setBackgroundColor(ContextCompat.getColor(ReactivateTicketActivity.this, R.color.reactivation_ok));
                        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextSize(getResources().getDimension(R.dimen.snackBarTextSize));
                        mSnackBarResult.setActionTextColor(ContextCompat.getColor(ReactivateTicketActivity.this, R.color.validation_ok_action));
                        mSnackBarResult.show();

                        playSoundVibrationEffect(true);
                        prepareCameraToScanAgain();
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        if (null!=mSnackBarResult){
                            mSnackBarResult.dismiss();
                        }

                        Dialog dlg = new AlertDialog.Builder(ReactivateTicketActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.validation_incorrect)
                                .setMessage(e.getError())
                                .setCancelable(false)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        prepareCameraToScanAgain();
                                    }
                                })
                                .create();
                        dlg.show();

                        playSoundVibrationEffect(false);
                    }

                    @Override
                    public void completed() {
                        pDlg.dismiss();
                    }
                }
        );
    }

    private void playSoundVibrationEffect(boolean bSuccess) {
        AppPreferences appPrefs = AppPreferences.getSingleton(this);
        boolean bVibration = true;
        boolean bSound = true;
        if (null!=appPrefs){
            bVibration = appPrefs.getVibrationOnValidation();
            bSound = appPrefs.getSoundOnValidation();
        }
        if (!bVibration && !bSound){
            return;
        }

        if (bVibration){
            UtilsHelper.playVibration(bSuccess?AppConstants.VIBRATION_ON_SUCCESS:AppConstants.VIBRATION_ON_ERROR, this);
        }
        if (bSound){
            if (bSuccess){
                UtilsHelper.playBeep(
                        AppConstants.SOUNDS_TIMES_ON_SUCCESS,
                        AppConstants.BIP_DURATION,
                        AppConstants.SOUNDS_ON_SUCCESS);
            }
            else {
                UtilsHelper.playBeep(
                        AppConstants.SOUNDS_TIMES_ON_ERROR,
                        AppConstants.BIP_DURATION,
                        AppConstants.SOUNDS_ON_ERROR);
            }
        }
    }

    private void prepareCameraToScanAgain(){
        mScannerView.resumeCameraPreview(ReactivateTicketActivity.this);
    }
}
