package com.bltucker.utahpublicnotices.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bltucker.utahpublicnotices.R;

public final class PreferenceFetcher {


    public String getCityPreference(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.city_setting_pref_key), context.getString(R.string.city_setting_pref_default));
    }
}
