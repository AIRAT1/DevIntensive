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
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private Button mSignIn;
    private TextView mRememberPassword;
    private TextInputLayout mWrapLoginEmail, mWrapLoginPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private DataManager mDataManager;

    /**
     * bind values
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDataManager = DataManager.getInstance();
        mSignIn = (Button) findViewById(R.id.login_btn);
        mWrapLoginEmail = (TextInputLayout) findViewById(R.id.wrap_login_email);
        mWrapLoginPassword = (TextInputLayout) findViewById(R.id.wrap_login_password);
        mLogin = (EditText) findViewById(R.id.et_login_email);
        mPassword = (EditText) findViewById(R.id.et_login_password);
        mRememberPassword = (TextView) findViewById(R.id.remember_txt);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_Coordinator_container);

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

    private void loginSuccess(UserModelRes userModel) {
        showSnackbar(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        saveUserValues(userModel);
        saveUserPhotos(userModel);
        saveUserFields(userModel);
        saveUserFirstLastName(userModel);

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginRec(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Не верный логин или пароль");
                    } else {
                        showSnackbar("Всё пропало шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    // todo обработать ошибки ретрофита
                }
            });
        }else {
            showSnackbar("Сеть на данный момент не доступна, попробуйте позже");
        }
    }
    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }
    private void saveUserPhotos(UserModelRes userModel) {
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
    }
    private void saveUserFields(UserModelRes userModel) {
        List<String> userFields = new ArrayList<>();
        userFields.add(userModel.getData().getUser().getContacts().getPhone());
        userFields.add(userModel.getData().getUser().getContacts().getEmail());
        String vk = userModel.getData().getUser().getContacts().getVk();
        if (vk.startsWith("https://")) vk = vk.substring(8, vk.length());
        userFields.add(vk);
        String git = userModel.getData().getUser().getRepositories().getRepo().get(0).getGit();
        if (git.startsWith("https://")) git = git.substring(8, git.length());
        userFields.add(git);
        userFields.add(userModel.getData().getUser().getPublicInfo().getBio());
        mDataManager.getPreferencesManager().saveUserProfileData(userFields);
    }
    private void saveUserFirstLastName(UserModelRes userModel) {
        mDataManager.getPreferencesManager().saveFirstLastName(userModel.getData().getUser().getFirstName(),
                userModel.getData().getUser().getSecondName());
    }
}
