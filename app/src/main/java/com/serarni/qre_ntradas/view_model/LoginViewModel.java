package com.serarni.qre_ntradas.view_model;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.serarni.common.TaskCallback;
import com.serarni.common.TaskError;
import com.serarni.common.ViewHelper;
import com.serarni.qre_ntradas.BR;

import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.model.DataManager;
import com.serarni.qre_ntradas.model.User;
import com.serarni.qre_ntradas.view.EventsActivity;
import com.serarni.qre_ntradas.view.MainActivity;

import org.antlr.v4.runtime.misc.NotNull;

import java.lang.ref.WeakReference;

/** Data showed into LoginActivity
 * Created by serarni on 07/09/2016.
 */
public class LoginViewModel extends BaseObservable {
    private WeakReference<Activity> mActivity;
    private String mLastUserLogin;
    private String mLastUserPassword;

    public LoginViewModel(
            @NonNull String sUserLogin,
            @NonNull String sUserPwd,
            Activity activity,
            boolean bAutoLogin){
        this.mLastUserLogin = sUserLogin;
        this.mLastUserPassword = sUserPwd;
        this.mActivity = new WeakReference<>(activity);

        // auto-login
//        if (bAutoLogin && !mLastUserLogin.isEmpty() && !mLastUserPassword.isEmpty()){
//            doLogin();
//        }
    }

    @Bindable
    public String getLastUserLogin(){
        return mLastUserLogin;
    }

    @Bindable
    public String getLastUserPassword(){
        return mLastUserPassword;
    }

    @Bindable
    public void setLastUserLogin(String sLastUserLogin) {
        if (!mLastUserLogin.equals(sLastUserLogin)){
            this.mLastUserLogin = sLastUserLogin;
            notifyPropertyChanged(BR.lastUserLogin);
        }
    }

    @Bindable
    public void setLastUserPassword(String sLastUserPassword) {
        if (!mLastUserPassword.equals(sLastUserPassword)){
            this.mLastUserPassword = sLastUserPassword;
            notifyPropertyChanged(BR.lastUserPassword);
        }
    }

    public View.OnClickListener onClickBtnLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        };
    }

    private void doLogin(){

        DataManager.getSingleton().authenticate(
                mLastUserLogin,
                mLastUserPassword,
                false,
                mActivity.get(),
                new TaskCallback<User>() {
                    @Override
                    public void success(@NotNull User user) {
                        Activity act = mActivity.get();
                        if (null!=act){
                            String sMessage = String.format(act.getString(R.string.session_success), mLastUserLogin); // TODO: put here the user name when received ok
                            Toast.makeText(act, sMessage, Toast.LENGTH_LONG).show();

                            // redirect to main activity and close this
                            Intent intent = new Intent(act, MainActivity.class);
                            intent.putExtra("Usuario",mLastUserLogin);
                            act.startActivity(intent);
                            act.finish();
                        }
                    }

                    @Override
                    public void error(TaskError e) {
                        Activity act = mActivity.get();
                        if (null!=act){
                            ViewHelper.showDialogMessage(R.string.error, e.getError(), act);
                        }
                    }

                    @Override
                    public void completed() {
                    }
                }
        );
    }
}
