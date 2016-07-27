package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.manager.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginRec;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private Button mSignIn;
    private TextView mRememberPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    /**
     * bind values
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        mSignIn = (Button) findViewById(R.id.login_btn);
        mLogin = (EditText) findViewById(R.id.login_email_et);
        mPassword = (EditText) findViewById(R.id.login_password_et);
        mRememberPassword = (TextView) findViewById(R.id.remember_txt);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);

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
        saveUserInDb();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, UserListActivity.class);
                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);


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
    private void saveUserInDb() {
        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {
                        List<Repository> allRepositories = new ArrayList<>();
                        List<User> allUserrs = new ArrayList<>();

                        for (UserListRes.UserData userRes : response.body().getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUserrs.add(new User(userRes));

                            mRepositoryDao.insertOrReplaceInTx(allRepositories);
                            mUserDao.insertOrReplaceInTx(allUserrs);
                        }
                    }else {
                        showSnackbar("Список пользователей не может быть получен");
                        Log.e(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showSnackbar("Что то пошло не так");
                }

            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                // todo обработать ошибки
            }
        });
    }
    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();
        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }
}
