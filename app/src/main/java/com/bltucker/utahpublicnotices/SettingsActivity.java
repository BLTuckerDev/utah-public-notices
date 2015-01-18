package com.bltucker.utahpublicnotices;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.bltucker.utahpublicnotices.data.PublicNoticeContract;
import com.bltucker.utahpublicnotices.sync.PublicNoticeSyncAdapter;


public final class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private boolean bindingPreference = false;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.city_setting_pref_key)));
    }


    @Override
    public boolean onIsMultiPane() {
        return false;
    }


    private void bindPreferenceSummaryToValue(Preference preference) {

        bindingPreference = true;
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

        bindingPreference = false;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if ( !bindingPreference ) {
            if (preference.getKey().equals(getString(R.string.city_setting_pref_key))) {
                PublicNoticeSyncAdapter.syncImmediately(this);
            } else {
                getContentResolver().notifyChange(PublicNoticeContract.NoticeEntry.CONTENT_URI, null);
            }
        }

        preference.setSummary(stringValue);

        return true;
    }
}
