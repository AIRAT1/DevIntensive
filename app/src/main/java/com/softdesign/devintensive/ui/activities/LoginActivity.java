package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private Button mSignIn;
    private TextView mRememberPassword;
    private TextInputLayout mWrapLoginEmail, mWrapLoginPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;

    /**
     * bind values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignIn = (Button)findViewById(R.id.login_btn);
        mWrapLoginEmail = (TextInputLayout)findViewById(R.id.wrap_login_email);
        mWrapLoginPassword = (TextInputLayout)findViewById(R.id.wrap_login_password);
        mLogin = (EditText)findViewById(R.id.et_login_email);
        mPassword = (EditText)findViewById(R.id.et_login_password);
        mRememberPassword = (TextView)findViewById(R.id.remember_txt);
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_Coordinator_container);

        mSignIn.setOnClickListener(this);
        mRememberPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                showSnackbar("Вход");
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
            default:
                break;
        }
    }
    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }
    private void loginSuccess() {

    }
}
