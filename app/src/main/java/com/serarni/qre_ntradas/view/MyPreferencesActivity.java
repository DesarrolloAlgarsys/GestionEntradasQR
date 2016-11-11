package com.serarni.qre_ntradas.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.model.AppPreferences;

/** App preferences
 * Created by serarni on 10/09/2016.
 */
public class MyPreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat
    {
        private static final String TAG_LOG = MyPreferenceFragment.class.getSimpleName();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Log.d(TAG_LOG, "onCreatePreferences() rootKey="+rootKey);
            addPreferencesFromResource(R.xml.preferences);

            Context context = getContext();
            if (null!=context){
                AppPreferences appPreferences = AppPreferences.getSingleton(context);
                if (null!=appPreferences){
                    initPreferenceVibrationOnValidation(appPreferences.getVibrationOnValidation(), context);
                    initPreferenceSoundOnValidation(appPreferences.getSoundOnValidation(), context);
                }
            }
        }

        private void initPreferenceSoundOnValidation(boolean vibrationOnValidation, final Context context) {
            Preference pref = findPreference("preference_key_sound");
            if (null!=pref && pref instanceof CheckBoxPreference){
                CheckBoxPreference prefButton = (CheckBoxPreference) pref;
                prefButton.setChecked(vibrationOnValidation);
                prefButton.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue)
                    {
                        try{
                            //noinspection ConstantConditions
                            AppPreferences.getSingleton(context).setSoundOnValidation((Boolean)newValue, context);
                        }catch (NullPointerException e){
                            Log.e(TAG_LOG, "initPreferenceSoundOnValidation::onPreferenceChange() appPreferences is null");
                        }
                        return true;
                    }
                });
            }
        }

        private void initPreferenceVibrationOnValidation(boolean vibrationOnValidation, final Context context) {
            Preference pref = findPreference("preference_key_vibration");
            if (null!=pref && pref instanceof CheckBoxPreference){
                CheckBoxPreference prefButton = (CheckBoxPreference) pref;
                prefButton.setChecked(vibrationOnValidation);
                prefButton.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue)
                    {
                        try{
                            //noinspection ConstantConditions
                            AppPreferences.getSingleton(context).setVibrationOnValidation((Boolean)newValue, context);
                        }catch (NullPointerException e){
                            Log.e(TAG_LOG, "initPreferenceSoundOnValidation::onPreferenceChange() appPreferences is null");
                        }
                        return true;
                    }
                });
            }
        }
    }
}
