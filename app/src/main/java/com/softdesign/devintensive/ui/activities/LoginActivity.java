package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;

public class LoginActivity extends AppCompatActivity {
    private Button mBtnlogin;
    private TextInputLayout mWrapLoginEmail, mWrapLoginPassword;
    private EditText mEtLoginEmail, mEtLoginPassword;

    /**
     * bind values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBtnlogin = (Button)findViewById(R.id.login_btn);
        mWrapLoginEmail = (TextInputLayout)findViewById(R.id.wrap_login_email);
        mWrapLoginPassword = (TextInputLayout)findViewById(R.id.wrap_login_password);
        mEtLoginEmail = (EditText)findViewById(R.id.et_login_email);
        mEtLoginPassword = (EditText)findViewById(R.id.et_login_password);

        mBtnlogin.setOnClickListener(new View.OnClickListener() {
            /**
             * make action
             * @param v
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
}
