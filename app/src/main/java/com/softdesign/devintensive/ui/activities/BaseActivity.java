package com.softdesign.devintensive.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    static final String TAG = BaseActivity.class.getSimpleName();

    /**
     * show error message as toast
     * @param message
     * @param error
     */
    public void showError(String message, Exception error) {
        showToast(message);
        Log.e(TAG, String.valueOf(error));
    }

    /**
     * show toast
     * @param message
     */
    public void showToast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
