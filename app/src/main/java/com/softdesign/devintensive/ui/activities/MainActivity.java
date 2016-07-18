package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.manager.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = MainActivity.class.getSimpleName();
    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mCallImg, mSendEmailImg, mShowVkImg, mShowGithubImg;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private List<EditText> mUserInfoViews;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private AppBarLayout mAppBarLayout;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private ImageView mProfileImage;
    private TextInputLayout mInputLayoutPhone, mInputLayoutEmail, mInputLayoutVk, mInputLayoutGithub;
    private EditText mUserPhone, mUserMail, mUserVk, mUserGit, mUserAbout;
    private TextView mUserValueRating, mUserValueCodeLines, mUserValueProject;
    private List<TextView> mUserValueViews;

    /**
     * bind values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();
        mCallImg = (ImageView)findViewById(R.id.call_img);
        mSendEmailImg = (ImageView)findViewById(R.id.send_email_img);
        mShowVkImg = (ImageView)findViewById(R.id.show_vk);
        mShowGithubImg = (ImageView)findViewById(R.id.show_github);
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_coordinator_container);
        mNavigationDrawer = (DrawerLayout)findViewById(R.id.navigation_drawer);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mProfilePlaceholder = (RelativeLayout)findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout)findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView)findViewById(R.id.user_photo_img);

        mInputLayoutPhone = (TextInputLayout)findViewById(R.id.inputLayoutPhone);
        mInputLayoutEmail = (TextInputLayout)findViewById(R.id.inputLayoutEmail);
        mInputLayoutVk = (TextInputLayout)findViewById(R.id.inputLayoutVk);
        mInputLayoutGithub = (TextInputLayout)findViewById(R.id.inputLayiutGithub);

        mUserPhone = (EditText)findViewById(R.id.phone_et);
        mUserMail = (EditText)findViewById(R.id.email_et);
        mUserVk = (EditText)findViewById(R.id.vk_et);
        mUserGit = (EditText)findViewById(R.id.github_et);
        mUserAbout = (EditText)findViewById(R.id.about_et);

        mUserValueRating = (TextView)findViewById(R.id.user_info_rait_txt);
        mUserValueCodeLines = (TextView)findViewById(R.id.user_info_code_lines_txt);
        mUserValueProject = (TextView)findViewById(R.id.user_info_project_txt);

        mUserPhone.addTextChangedListener(new MyTextWatcher(mUserPhone));
        mUserMail.addTextChangedListener(new MyTextWatcher(mUserMail));
        mUserVk.addTextChangedListener(new MyTextWatcher(mUserVk));
        mUserGit.addTextChangedListener(new MyTextWatcher(mUserGit));
        mUserAbout.addTextChangedListener(new MyTextWatcher(mUserAbout));

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserAbout);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLines);
        mUserValueViews.add(mUserValueProject);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mCallImg.setOnClickListener(this);
        mSendEmailImg.setOnClickListener(this);
        mShowVkImg.setOnClickListener(this);
        mShowGithubImg.setOnClickListener(this);

        setupToolbar();
        setupDraver();
        initUserFields();
        initUserInfoValue();
        initPhotos();
        initFirstLastName();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg) //todo сделать placeholder + transform + crop
                .error(R.drawable.user_bg)
                .fit()
                .into(mProfileImage);


        if (savedInstanceState == null) {
//            showSnackbar("Активити запускается впервые");
        }else {
//            showSnackbar("Активити уже создавалось");
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    /**
     * start activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * start activity after pause
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * activity is visible now
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        initUserFields();
    }

    /**
     * pause with activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    /**
     * activity is stop
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * save instance state
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * show snackbar with message
     * @param message
     */
    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * setup toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams)mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * on pressed listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_img:
                if ((ContextCompat.checkSelfPermission(
                        this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
                    Intent dialIntent = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + mUserPhone.getText().toString()));
                    startActivity(dialIntent);
                }else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, ConstantManager.CALL_REQUEST_PERMISSION_CODE);
                    Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемые разрешения",
                            Snackbar.LENGTH_LONG).setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
                }
                break;
            case R.id.send_email_img:
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mUserMail.getText().toString(), null));
                try {
                    startActivity(Intent.createChooser(sendIntent, "Email message goes here"));
                }catch (ActivityNotFoundException e) {
                    showSnackbar("There is no email client installed.");
                }
                break;
            case R.id.show_vk:
                Intent showVkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www." + mUserVk.getText().toString()));
                startActivity(showVkIntent);
                break;
            case R.id.show_github:
                Intent showGithub = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www." + mUserGit.getText().toString()));
                startActivity(showGithub);
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                }else {
                    submitForm();
                }
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            default:
                break;
        }
    }

    /**
     * check forms
     */
    private void submitForm() {
        if(!validatePhone()) return;
        if(!validateEmail()) return;
        if (!validateVk()) return;
        if (!validateGithub()) return;
        saveUserFields();
        changeEditMode(0);
        mCurrentEditMode = 0;
    }

    /**
     * check phone number
     * @return
     */
    public boolean validatePhone() {
        String phone = (mUserPhone.getText().toString().trim());
        if (phone.isEmpty() || phone.length() < 11 || phone.length() > 20) {
            mInputLayoutPhone.setError(getString(R.string.enter_correct_phone_number));
            requestFocus(mUserPhone);
            showSnackbar(getString(R.string.enter_correct_phone_number));
            return false;
        }else {
            mInputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * check email
     * @return
     */
    private boolean validateEmail() {
        String email = mUserMail.getText().toString().trim();
        String[] emailArray = email.split("@");
        String[] emailArrayPostFix = emailArray[1].split("\\.");

        if (email.isEmpty() || !isValidEmail(email)
                || emailArray[0].length() < 3
                || emailArrayPostFix[0].length() < 2 || emailArrayPostFix[1].length() < 2
                )
        {
            mInputLayoutEmail.setError(getString(R.string.enter_correct_email));
            requestFocus(mUserMail);
            showSnackbar(getString(R.string.enter_correct_email));
            return false;
        }else {
            mInputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * check email with pattern
     * @param email
     * @return
     */
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * check vk field
     * @return
     */
    private boolean validateVk() {
        String vk = (mUserVk.getText().toString().trim());
        if (vk.isEmpty() || !vk.startsWith("vk.com/")) {
            mInputLayoutVk.setError(getString(R.string.enter_correct_vk_address));
            requestFocus(mUserVk);
            showSnackbar(getString(R.string.enter_correct_vk_address));
            return false;
        }else {
            mInputLayoutVk.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * check github field
     * @return
     */
    private boolean validateGithub() {
        String github = mUserGit.getText().toString().trim();
        if (github.isEmpty() || !github.startsWith("github.com/")) {
            mInputLayoutGithub.setError(getString(R.string.enter_correct_github_address));
            requestFocus(mUserGit);
            showSnackbar(getString(R.string.enter_correct_github_address));
            return false;
        }else {
            mInputLayoutGithub.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * set request focus on view
     * @param view
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * check what of menu item was selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * check back button was pressed
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    /**
     * setup navigation drawer
     */
    private void setupDraver() {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Получение результата из другой активности (фото из камеры или из галереи).
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
        }
    }

    /**
     * change edit mode
     * @param mode
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }

                mUserPhone.requestFocus();
                mUserPhone.requestFocusFromTouch();
                mUserPhone.setSelection(mUserPhone.getText().length());

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        }else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
                saveUserFields();

        }
    }

    /**
     * load data from preferences
     */
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * save data in preferences
     */
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }
    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }
    private void initPhotos() {

    }
    private void initFirstLastName() {

    }

    /**
     * load photo from gallery
     */
    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choise_message)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    /**
     * load photo from camera
     */
    private void loadPhotoFromCamera() {
        if
                (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // todo обработать ошибку
            }
            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        }else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемые разрешения",
                    Snackbar.LENGTH_LONG).setAction("Разрешить", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApplicationSettings();
                }
            }).show();
        }
    }

    /**
     * result from permissions request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackbar("Разрешение на камеру полученно");
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showSnackbar("Разрешение на сохранение полученно");
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                showSnackbar("Разрешение на звонок полученно");
            }
        }
    }

    /**
     * hide placeholder
     */
    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * show placeholder
     */
    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * lock toolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * unlock toolbar
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * create dialog
     * @param id
     * @return
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title))
                        .setItems(selectItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choiceItem) {
                                switch (choiceItem) {
                                    case 0:
                                        loadPhotoFromGallery();
                                        break;
                                    case 1:
                                        loadPhotoFromCamera();
                                        break;
                                    case 2:
                                        dialog.cancel();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        return builder.create();
                    default:
                        return null;
        }
    }

    /**
     * create image file
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    /**
     * insert photo in view and save
     * @param selectedImage
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mProfileImage);
        // todo сделать placeholder + transform + crop
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    /**
     * start with application settings
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    /**
     * private class for watching text changes
     */
    private class MyTextWatcher implements TextWatcher {
        private View view;

        /**
         * constructor
         * @param view
         */
        public MyTextWatcher(View view) {
            this.view = view;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * after text changed listener
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.phone_et:
                    validatePhone();
                    break;
                case R.id.email_et:
                    validateEmail();
                    break;
                case R.id.vk_et:
                    validateVk();
                    break;
                case R.id.github_et:
                    validateGithub();
                    break;
                default:
                    break;
            }
        }
    }
}
