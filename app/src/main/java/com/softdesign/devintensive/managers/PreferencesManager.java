package com.softdesign.devintensive.managers;

import android.content.SharedPreferences;

import com.softdesign.devintensive.utils.DevintensiveApplication;

public class PreferencesManager {
    private SharedPreferences mSharedPreferences;

    public PreferencesManager() {
        this.mSharedPreferences = DevintensiveApplication.getSharedPreferences();
    }
}
