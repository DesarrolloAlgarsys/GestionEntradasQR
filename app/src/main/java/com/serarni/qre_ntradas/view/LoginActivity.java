package com.serarni.qre_ntradas.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.databinding.ActivityLoginBinding;
import com.serarni.qre_ntradas.model.AppPreferences;
import com.serarni.qre_ntradas.model.DataManager;
import com.serarni.qre_ntradas.view_model.LoginViewModel;

/**Login activity
 * Created by serarni on 07/09/2016.
 */
public class LoginActivity  extends AppCompatActivity {

    public static final String EXTRA_AUTO_LOGIN = "autoLogin";

    public static final String STATE_LOGIN = "loginInstanceState";
    public static final String STATE_PWD = "pwdInstanceState";

    private LoginViewModel mLoginVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init data manager allows to init serverManager and any thing than requires context
        DataManager.getSingleton().initialize(this);

        // data binding
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        if (savedInstanceState == null) {
            // init data due to activity
            AppPreferences appPrefs = AppPreferences.getSingleton(this);
            if (null==appPrefs){
                throw new RuntimeException("AppPreferences is null");
            }
            mLoginVM = new LoginViewModel(
                    appPrefs.getLastUserLogin(),
                    appPrefs.getLastUserPassword(),
                    this,
                    getIntent().getBooleanExtra(EXTRA_AUTO_LOGIN, false));
        } else {
            mLoginVM = new LoginViewModel(
                    savedInstanceState.getString(STATE_LOGIN, ""),
                    savedInstanceState.getString(STATE_PWD, ""),
                    this,
                    false);
        }

        binding.setViewModelLogin(mLoginVM);

        // toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //setTitle(R.string.login);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_LOGIN, mLoginVM.getLastUserLogin());
        outState.putString(STATE_PWD, mLoginVM.getLastUserPassword());
    }
}
