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
import com.softdesign.devintensive.data.manager.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginRec;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener{
    private Button mSignIn;
    private TextView mRememberPassword;
    private TextInputLayout mWrapLoginEmail, mWrapLoginPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private DataManager mDataManager;

    /**
     * bind values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDataManager = DataManager.getInstance();
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
                signIn();
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
    private void loginSuccess(Response<UserModelRes> response) {
        showSnackbar(response.body().getData().getToken());
    }
    private void signIn() {
        Call<UserModelRes> call = mDataManager.loginUser(new UserLoginRec("email", "password"));
        call.enqueue(new Callback<UserModelRes>() {
            @Override
            public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                if (response.code() == 200) {
                    loginSuccess(response);
                }else if (response.code() == 404) {
                    showSnackbar("Не верный логин или пароль");
                }else {
                    showSnackbar("Всё пропало шеф!!!");
                }
            }

            @Override
            public void onFailure(Call<UserModelRes> call, Throwable t) {
                // todo обработать ошибки ретрофита
            }
        });
    }
}
