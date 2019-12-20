/*Copyright (C) 2015 The ResurrectionRemix Project
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/
package com.android.settings.rr;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.rr.utils.RRUtils;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.settings.rr.Preferences.SystemSettingSwitchPreference;
import com.android.settings.rr.Preferences.CustomSeekBarPreference;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {


    private static final String SMS_BREATH = "sms_breath";
    private static final String MISSED_CALL_BREATH = "missed_call_breath";
    private static final String VOICEMAIL_BREATH = "voicemail_breath";

    private SwitchPreference mSmsBreath;
    private SwitchPreference mMissedCallBreath;
    private SwitchPreference mVoicemailBreath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rr_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        
        // Breathing Notifications
           mSmsBreath = (SwitchPreference) findPreference(SMS_BREATH);
           mMissedCallBreath = (SwitchPreference) findPreference(MISSED_CALL_BREATH);
           mVoicemailBreath = (SwitchPreference) findPreference(VOICEMAIL_BREATH);

           ConnectivityManager cm = (ConnectivityManager)
                   getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

           if (cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)) {
               mSmsBreath.setChecked(Settings.Global.getInt(resolver,
                       Settings.Global.KEY_SMS_BREATH, 0) == 1);
               mSmsBreath.setOnPreferenceChangeListener(this);

               mMissedCallBreath.setChecked(Settings.Global.getInt(resolver,
                       Settings.Global.KEY_MISSED_CALL_BREATH, 0) == 1);
               mMissedCallBreath.setOnPreferenceChangeListener(this);

               mVoicemailBreath.setChecked(Settings.System.getInt(resolver,
                       Settings.System.KEY_VOICEMAIL_BREATH, 0) == 1);
               mVoicemailBreath.setOnPreferenceChangeListener(this);
           } else {
               prefSet.removePreference(mSmsBreath);
               prefSet.removePreference(mMissedCallBreath);
               prefSet.removePreference(mVoicemailBreath);
           }

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mSmsBreath) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(), SMS_BREATH, value ? 1 : 0);
            return true;
        } else if (preference == mMissedCallBreath) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(), MISSED_CALL_BREATH, value ? 1 : 0);
            return true;
        } else if (preference == mVoicemailBreath) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), VOICEMAIL_BREATH, value ? 1 : 0);
            return true;
        }
	return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.RESURRECTED;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        RRUtils.addSearchIndexProvider(R.xml.rr_statusbar);
}
