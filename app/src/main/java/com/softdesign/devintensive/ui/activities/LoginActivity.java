package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;

public class LoginActivity extends AppCompatActivity {
    private Button mBtnlogin;
    private FloatingActionButton mFab;
    private TextInputLayout mInputLayoutLoginEmail, mInputLayoutLoginPassword;
    private EditText mLoginEmailEt, mLoginPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBtnlogin = (Button)findViewById(R.id.btn_login);
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mInputLayoutLoginEmail = (TextInputLayout)findViewById(R.id.input_layout_login_email);
        mInputLayoutLoginPassword = (TextInputLayout)findViewById(R.id.input_layout_login_password);
        mLoginEmailEt = (EditText)findViewById(R.id.login_email_et);
        mLoginPasswordEt = (EditText)findViewById(R.id.login_password_et);

        mBtnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
}
