package com.softdesign.devintensive.data.manager;

import android.content.Context;

import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginRec;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.DevintensiveApplication;

import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevintensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }
    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }
    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }
    public Context getContext() {
        return mContext;
    }
    // region =========== Network ==============

    public Call<UserModelRes> loginUser(UserLoginRec userLoginRec) {
        return mRestService.loginUser(userLoginRec);
    }

    // endregion

    // region =========== Database ==============
}
