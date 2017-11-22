package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.ImageLoaderController;
import com.twitter.sdk.android.Twitter;
import applicationId.R;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.FBLoginModel;
import com.ncsavault.models.FetchingAllDataModel;
import com.ncsavault.models.LoginEmailModel;
import com.ncsavault.models.MailChimpDataModel;
import com.ncsavault.utils.Utils;
import com.ncsavault.wheeladapters.NumericWheelAdapter;
import com.ncsavault.wheelwidget.OnWheelChangedListener;
import com.ncsavault.wheelwidget.OnWheelScrollListener;
import com.ncsavault.wheelwidget.WheelView;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reginald.editspinner.EditSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegistrationActivity is used for social registration
 */

public class RegistrationActivity extends PermissionActivity implements AbstractView {

    private final String tag = "RegistrationActivity";
    private EditSpinner mEditSpinner;
    private ImageView mProfileImage;
    private EditText mUserName;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailId;
    private EditText mEmailIdFB;
    private EditSpinner mGender;
    private EditText mYOB;
    private ProgressBar pBar;
    private Button mRegistrationButton, mSignUpButton;
    private WheelView yearWheel;
    private View view;
    private TextView tvUploadPhoto, tvAlreadyRegistered, tvSignUpWithoutProfile;
    private boolean isBackToSplashScreen = false;
    private boolean askAgainForMustPermissions = false;
    private boolean goToSettingsScreen = false;
    private boolean twitter;
    private Uri selectedImageUri = null;
    private Uri outputFileUri;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    private File sdImageMainDirectory;
    private User socialUser;
    private AsyncTask<Void, Void, String> mLoginTask;
    @SuppressWarnings("deprecation")
    private ProgressDialog pDialog;
    private FBLoginModel fbLoginModel;
    private FetchingAllDataModel fetchingAllDataModel;
    private int screenWidth;
    private boolean wheelScrolled = false;
    private String[] yearArray;
    private AlertDialog alertDialog;
    private int displayHeight = 0;
    private MailChimpDataModel mMailChimpModelData;
    private boolean isBlankEmail = false;
    private final Bundle params = new Bundle();
    private FirebaseAnalytics mFirebaseAnalytics = null;
    private boolean isImageProvided = false;
    private boolean isFirstTime = true;
    private String mLogin;
    private String firstName = "";
    private String lastName = "";
    private final CharSequence[] alertListItems =  {Html.fromHtml("<b>Take from camera</b>"),
            Html.fromHtml("<b>Select from gallery<b>")};
    private Bitmap selectedBitmap;
    private LoginEmailModel loginEmailModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen_layout);
        initialiseAllData();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    /**
     * Method used for initialize the all the data and component related to registration screen.
     */
    private void initialiseAllData() {
        initViews();
        initData();
        initListener();
        setGenderAdapter();
        getDataOfSocialLogin();
        displayHeight = Utils.getScreenHeight(this);
    }

    /**
     * Method is used to get user data of social login
     */
    private void getDataOfSocialLogin() {

        twitter = AppController.getInstance().getModelFacade().getLocalModel().isTwitterLogin();
        socialUser = AppController.getInstance().getModelFacade().getLocalModel().getUser();

        if (socialUser.getFname() != null && !Objects.equals(socialUser.getFname(), "")) {
            firstName = socialUser.getFname().trim().substring(0, 1).toUpperCase() + socialUser.getFname().trim().substring(1);
        }
        if (socialUser.getFname() != null && !Objects.equals(socialUser.getLname(), "")) {
            lastName = socialUser.getLname().trim().substring(0, 1).toUpperCase() + socialUser.getLname().trim().substring(1);
        }
        String userName = socialUser.getUsername().trim().substring(0, 1).toUpperCase() + socialUser.getUsername().trim().substring(1);

        mFirstName.setText(firstName);
        mLastName.setText(lastName);
        mUserName.setText(userName);
        if (isFirstTime) {
            isFirstTime = false;
            if (!Objects.equals(socialUser.getEmailID(), "")) {

                mEmailIdFB.setText(socialUser.getEmailID().trim());
                mEmailIdFB.setVisibility(View.VISIBLE);
                mEmailId.setVisibility(View.GONE);
            } else {
                isBlankEmail = true;
                mEmailIdFB.setVisibility(View.GONE);
                mEmailId.setVisibility(View.VISIBLE);
            }
        }

        mYOB.setText(mYOB.getText().toString().trim());
        mGender.setText(mGender.getText().toString().trim());
        String profileImage = socialUser.getImageurl();

        if (profileImage != null) {

            com.android.volley.toolbox.ImageLoader volleyImageLoader =
                    ImageLoaderController.getInstance(this).getImageLoader();

            volleyImageLoader.get(profileImage,
                    com.android.volley.toolbox.ImageLoader.getImageListener(mProfileImage,
                            R.drawable.camera_background, R.drawable.camera_background));

        }
    }

    /**
     * Method to set adapter to gender edit spinner
     */
    private void setGenderAdapter() {
        @SuppressWarnings("unchecked")
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.gender_selection));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditSpinner.setAdapter(adapter);
        Utils.getInstance().getHideKeyboard(RegistrationActivity.this);
    }

    /**
     * Method is used for facebook login
     * @param socialUserData set the data of social user data.
     * @param registerUser set the string of register user.
     */
    private void facebookLogin(User socialUserData, String registerUser) {

        String email;

        if (twitter) {
            email = mEmailId.getText().toString();
        } else {
            email = mEmailIdFB.getText().toString();
        }

        //       }
        if (loginEmailModel != null) {
            loginEmailModel.unRegisterView(this);
        }
        socialUserData.setEmailID(email);
        socialUserData.setIsRegisteredUser(registerUser);
        if (isImageProvided) {
            try {
                selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, RegistrationActivity.this);
                selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, RegistrationActivity.this, sdImageMainDirectory);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
            socialUserData.setImageurl(convertedImage);
        }

        //noinspection deprecation
        pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        if (fbLoginModel != null) {
            fbLoginModel.unRegisterView(RegistrationActivity.this);
        }
        if (fetchingAllDataModel != null) {
            fetchingAllDataModel.unRegisterView(RegistrationActivity.this);
            fetchingAllDataModel = null;
        }
        socialUser = socialUserData;

        if (AppController.getInstance().getModelFacade().getLocalModel().isOverride()) {
            AppController.getInstance().getModelFacade().getLocalModel().setOverride(false);
            overrideUserData();
        } else {
            fbLoginModel = AppController.getInstance().getModelFacade().getRemoteModel().getFbLoginModel();
            fbLoginModel.registerView(RegistrationActivity.this);
            fbLoginModel.setProgressDialog(pDialog);
            fbLoginModel.fetchData(socialUser);
        }
    }

    /**
     * Method used for convert bitmap image to base64.
     * @param bitmap set the bitmap image
     * @return the value in string.
     */
    private String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }

    /**
     * Method used for check the override user data.
     */
    private void overrideUserData() {
        mLoginTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //noinspection deprecation
                pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {
                    result = AppController.getInstance().getServiceManager().getVaultService().updateUserData(socialUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Log.d("result","Result of post user data : " + result);
                    if (result.contains("true") || result.contains("success")) {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(result.trim(), classType);
                        pDialog.dismiss();
                        SharedPreferences pref = getSharedPreferences(getResources()
                                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                        pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                                            fetchInitialRecordsForAll();

                        if (AppController.getInstance().getModelFacade().getLocalModel().isGoogleLogin()) {
                            mLogin = "gm_exist";
                        } else if (AppController.getInstance().getModelFacade().getLocalModel().isFacebookLogin()) {
                            mLogin = "fb_exist";
                        } else if (AppController.getInstance().getModelFacade().getLocalModel().isTwitterLogin()) {
                            mLogin = "tw_exist";
                        }
                        params.putString(mLogin, mLogin);
                        mFirebaseAnalytics.logEvent(mLogin, params);
                    } else {
                        try {
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response.getReturnStatus() != null) {
                                if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Vault", response.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Google", response.getEmailID());

                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Twitter", response.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Facebook", response.getEmailID());
                                }
                            } else {
                                pDialog.dismiss();
                                LoginManager.getInstance().logOut();
                                Utils.getInstance().showToastMessage(RegistrationActivity.this, getResources().getString(R.string.connect_to_server), view);
                            }

                            mLoginTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mLoginTask = null;
                            Utils.getInstance().showToastMessage(RegistrationActivity.this, getResources().getString(R.string.unable_to_process), view);
                        }
                    }

                } else {
                    pDialog.dismiss();
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, getResources().getString(R.string.unable_to_process), view);
                }
            }
        };
        mLoginTask.execute();
    }

    /**
     * Method used for save fb data at server
     */
    private void getFBData() {
        Log.d(tag, "Result of post user data : " + fbLoginModel.getResultData());
        if (Utils.isInternetAvailable(RegistrationActivity.this)) {
            if (fbLoginModel.getResultData() != null) {
                if (fbLoginModel.getResultData().contains("true") /*|| fbLoginModel.getResultData().contains("success")*/) {
                    pDialog.dismiss();
                    Gson gson = new Gson();
                    Type classType = new TypeToken<APIResponse>() {
                    }.getType();
                    APIResponse response = gson.fromJson(fbLoginModel.getResultData().trim(), classType);
                    SharedPreferences pref = getSharedPreferences(getResources()
                            .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                    pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                    pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                    fetchInitialRecordsForAll();

                    if (AppController.getInstance().getModelFacade().getLocalModel().isGoogleLogin()) {
                        mLogin = "gm_exist";
                    } else if (AppController.getInstance().getModelFacade().getLocalModel().isFacebookLogin()) {
                        mLogin = "fb_exist";
                    } else if (AppController.getInstance().getModelFacade().getLocalModel().isTwitterLogin()) {
                        mLogin = "tw_exist";
                    }

                    params.putString(mLogin, mLogin);
                    mFirebaseAnalytics.logEvent(mLogin, params);

                } else {
                    try {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(fbLoginModel.getResultData().trim(), classType);
                        if (response.getReturnStatus() != null) {
                            if (response.getReturnStatus().toLowerCase().contains("vt_exists")
                                    || response.getReturnStatus().toLowerCase().contains("false")) {
                                pDialog.dismiss();
                                showAlertDialog("Vault", response.getEmailID());
                            } else if (response.getReturnStatus().toLowerCase().contains("fb_exists") /*|| response.getReturnStatus().toLowerCase().contains("" +
                                                    "")*/) {
                                pDialog.dismiss();
                                showAlertDialog("Facebook", response.getEmailID());


                            } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                pDialog.dismiss();
                                showAlertDialog("Twitter", response.getEmailID());
                            } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                pDialog.dismiss();
                                showAlertDialog("Google", response.getEmailID());
                            }
                        } else {
                            pDialog.dismiss();
                            LoginManager.getInstance().logOut();
                            // tvFacebookLogin.setText("Login with Facebook");
                            //showToastMessage(result);
                            Utils.getInstance().showToastMessage(RegistrationActivity.this, getResources().getString(R.string.connect_to_server), view);
                        }

                    } catch (Exception e) {
                        LoginManager.getInstance().logOut();
                        e.printStackTrace();
                        pDialog.dismiss();
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, getResources().getString(R.string.unable_to_process), view);
                    }
                }
            } else {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
            }
        } else {
            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
        pDialog.dismiss();
    }

    /**
     * Method to fetch all records of user from server
     */
    private void fetchInitialRecordsForAll() {

        if (Utils.isInternetAvailable(this)) {
            //noinspection deprecation
            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LoginManager.getInstance().logOut();
                }
            });
            if (fbLoginModel != null) {
                fbLoginModel.unRegisterView(RegistrationActivity.this);
                fbLoginModel = null;
            }

            if (fetchingAllDataModel != null) {
                fetchingAllDataModel.unRegisterView(this);
            }
            fetchingAllDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getFetchingAllDataModel();
            fetchingAllDataModel.registerView(this);
            fetchingAllDataModel.setProgressDialog(pDialog);
            fetchingAllDataModel.fetchData();

        } else {
            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
    }

    /**
     * Method is used to initialize views
     */
    @SuppressLint("CutPasteId")
    private void initViews() {

        Utils.getInstance().setAppName(this);
        mEditSpinner = findViewById(R.id.edit_spinner);
        mProfileImage = findViewById(R.id.imgUserProfile);
        mUserName = findViewById(R.id.username);
        mFirstName = findViewById(R.id.fname);
        mLastName = findViewById(R.id.lname);
        mEmailId = findViewById(R.id.Email);
        mEmailId.setVisibility(View.GONE);
        mEmailIdFB = findViewById(R.id.Email_FB);
        mGender = findViewById(R.id.edit_spinner);

        mRegistrationButton = findViewById(R.id.btn_sign_up);
        pBar = findViewById(R.id.register_progress_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            pBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_progress_bar_lower, null));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
        }


        yearWheel = findViewById(R.id.year_wheel);
        initWheel();
        yearWheel.setBackgroundColor(Color.parseColor("#797979"));
        mYOB = findViewById(R.id.yob);
        view = findViewById(R.id.llToast);

        mFirstName.setOnFocusChangeListener(onFocusChangeListener);
        mLastName.setOnFocusChangeListener(onFocusChangeListener);
        mYOB.setOnFocusChangeListener(onFocusChangeListener);
        mEmailId.setOnFocusChangeListener(onFocusChangeListener);
        mUserName.setOnFocusChangeListener(onFocusChangeListener);

        mUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mSignUpButton = findViewById(R.id.tv_sign_up_button);
        tvUploadPhoto = findViewById(R.id.upload_photo_text_view);

        tvAlreadyRegistered = findViewById(R.id.tv_already_registered);
        tvSignUpWithoutProfile = findViewById(R.id.tv_sign_up_without_pic);
    }

    /**
     * Set pointer to end of text in edit text when user clicks Next on KeyBoard.
     */
    private final View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                ((EditText) view).setSelection(((EditText) view).getText().length());
            }
        }
    };

    /**
     * Method is used to initialize data
     */
    private void initData() {

        try {

            //noinspection deprecation
            mProfileImage.setImageDrawable(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.camera_background));
            screenWidth = Utils.getScreenDimensions(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 3, screenWidth / 3);
            mProfileImage.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method is used to check visibility of birth year wheel view
     */
    private void checkYearWheelVisibility() {
        if (yearWheel.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.slidedown);
            yearWheel.setAnimation(anim);
            yearWheel.setVisibility(View.GONE);
            mRegistrationButton.setVisibility(View.VISIBLE);
        }

        if (mLastName.getText().toString().length() > 0) {
            String lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                    mLastName.getText().toString().trim().substring(1);
            lastName = lastName.replace(" ", "");
            mLastName.setText(lastName);
        }

        if (mFirstName.getText().toString().length() > 0) {
            String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                    mFirstName.getText().toString().trim().substring(1);
            firstName = firstName.replace(" ", "");
            mFirstName.setText(firstName);
        }
    }

    /**
     * Method is used to set birth year wheel view
     */
    private void openYearWheel() {
        mRegistrationButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (displayHeight * 0.30));
        lp.setMargins(10, 10, 10, 0);
        lp.gravity = Gravity.BOTTOM;
        yearWheel.setLayoutParams(lp);
        Animation anim = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.slideup);
        yearWheel.setAnimation(anim);
        yearWheel.setVisibility(View.VISIBLE);
        Utils.getInstance().getHideKeyboard(RegistrationActivity.this);
    }


    /**
     * Method used for handle the click event.
     */
    private void initListener() {

        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validationOfSocialLogin();

            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBlankEmail) {
                    isBlankEmail = false;
                    checkEmailAndProceed();

                } else {

                    showConfirmLoginDialog(getResources().getString(R.string.do_you_want_to_join_our_mailing_list), firstName,
                            lastName, socialUser.getEmailID());
                }
            }
        });

        tvAlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitter = false;
                finish();
                LoginManager.getInstance().logOut();
                Twitter.logOut();
                Auth.GoogleSignInApi.signOut(LoginEmailActivity.mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                //updateUI(null);
                            }
                        });
                overridePendingTransition(R.anim.leftin, R.anim.rightout);

            }
        });

        mYOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYearWheel();
                mYOB.requestFocus();
            }
        });


        yearWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYearWheelVisibility();
            }
        });

        mYOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openYearWheel();
                mYOB.requestFocus();
                return false;
            }
        });

        mFirstName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d(tag, "Deleted");
                }
                return false;
            }
        });

        mLastName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d(tag, "Deleted");
                }
                return false;
            }
        });


        mFirstName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mFirstName.requestFocus();
                return false;
            }
        });


        mFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mFirstName.getText().toString().length() == 0) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY, view);
                    }

                    if (mFirstName.getText().toString().length() > 0) {
                        String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() + mFirstName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mFirstName.setText(firstName);
                    }

                }
                return false;
            }
        });


        mLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (mLastName.getText().toString().length() == 0) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.LAST_NAME_CAN_NOT_EMPTY, view);

                    }
                    if (mLastName.getText().toString().length() > 0) {
                        String firstName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                mLastName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mLastName.setText(firstName);
                    }

                    openYearWheel();


                }
                return false;
            }
        });


        mEmailId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (mEmailId.getText().toString().length() == 0) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY, view);

                    }
                    if (mEmailId.getText().toString().length() > 0) {
                        String firstName = mEmailId.getText().toString().replace(" ", "");
                        mEmailId.setText(firstName);
                    }

                }
                return false;
            }
        });



        mUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (mUserName.getText().toString().length() == 0) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_CAN_NOT_EMPTY, view);


                    } else if (mUserName.getText().toString().length() < 3) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
                    }

                    if (mUserName.getText().toString().length() > 0) {
                        String firstName = mUserName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                mUserName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mUserName.setText(firstName);
                    }

                }
                return false;
            }
        });


        mLastName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mLastName.requestFocus();

                return false;
            }
        });

        mUserName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mUserName.requestFocus();
                return false;
            }
        });

        mEmailId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                //if (twitter) {
                mEmailId.requestFocus();
                //}
                if (mLastName.getText().toString().length() > 0) {
                    String lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mLastName.getText().toString().trim().substring(1);
                    lastName = lastName.replace(" ", "");
                    mLastName.setText(lastName);
                }

                if (mFirstName.getText().toString().length() > 0) {
                    String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mFirstName.getText().toString().trim().substring(1);
                    firstName = firstName.replace(" ", "");
                    mFirstName.setText(firstName);
                }

                return false;
            }
        });

        mEmailIdFB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                if (mLastName.getText().toString().length() > 0) {
                    String lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mLastName.getText().toString().trim().substring(1);
                    lastName = lastName.replace(" ", "");
                    mLastName.setText(lastName);
                }

                if (mFirstName.getText().toString().length() > 0) {
                    String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mFirstName.getText().toString().trim().substring(1);
                    firstName = firstName.replace(" ", "");
                    mFirstName.setText(firstName);
                }

                return false;
            }
        });


        mGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mGender.showDropDown();
                mGender.requestFocus();
                Utils.getInstance().getHideKeyboard(RegistrationActivity.this);
                return false;
            }
        });



        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.slidedown);
                    yearWheel.setAnimation(anim);
                    yearWheel.setVisibility(View.GONE);
                }

                if (Utils.isInternetAvailable(getApplicationContext()))
                    try {
                        //Marshmallow permissions for write external storage.
                        if (haveAllMustPermissions(writeExternalStorage)) {
                            if (twitter) {
                                mEmailId.setVisibility(View.GONE);
                            } else {
                                mEmailIdFB.setVisibility(View.GONE);
                            }
                            openImageIntent();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                else
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
            }
        });


    }



    /**
     * Method is used to validate social user data
     */
    private void validationOfSocialLogin() {
        if (Utils.isInternetAvailable(RegistrationActivity.this)) {

            checkYearWheelVisibility();
            if (mFirstName.getText().toString().length() == 0) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY, view);
                return;

            } else if (!mFirstName.getText().toString().matches("[a-zA-Z ]+")) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.ENTER_ONLY_ALPHABETS + " in first name", view);
                return;
            }

            if (mLastName.getText().toString().length() == 0) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.LAST_NAME_CAN_NOT_EMPTY, view);
                return;

            } else if (!mLastName.getText().toString().matches("[a-zA-Z ]+")) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.ENTER_ONLY_ALPHABETS + " in last name", view);
                return;
            }

            if (mEmailId.getText().length() == 0 && mEmailId.getVisibility() == View.VISIBLE) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY, view);
                return;
            }

            if (isValidText(mUserName.getText().toString().replace(" ", "").trim())) {
                Log.d(tag, "is valid" + isValidText(mUserName.getText().toString().replace(" ", "").trim()));
            } else if (mUserName.getText().toString().length() == 0) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_CAN_NOT_EMPTY, view);
                return;

            } else if (mUserName.getText().toString().length() < 3) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
                return;
            }
            String email = "";
            if (mEmailId.getVisibility() == View.VISIBLE) {
                email = mEmailId.getText().toString();
            } else if (mEmailIdFB.getVisibility() == View.VISIBLE) {
                email = mEmailIdFB.getText().toString();
            }

            if (isValidEmail(email)) {
                if (twitter) {
                    socialUser.setEmailID(email);
                }
                if (socialUser != null) {

                    socialUser.setFname(mFirstName.getText().toString().trim());
                    socialUser.setLname(mLastName.getText().toString().trim());
                    socialUser.setUsername(mUserName.getText().toString().trim());

                    if (isValidEmail(socialUser.getEmailID())) {

                        if (firstName != null && !Objects.equals(firstName, "")) {
                            firstName = socialUser.getFname().trim().substring(0, 1).toUpperCase() + socialUser.getFname().trim().substring(1);
                        } else {
                            firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                    mFirstName.getText().toString().trim().substring(1);
                        }
                        if (lastName != null && !Objects.equals(lastName, "")) {
                            lastName = socialUser.getLname().trim().substring(0, 1).toUpperCase() + socialUser.getLname().trim().substring(1);
                        } else {
                            lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                    mLastName.getText().toString().trim().substring(1);
                        }

                        checkEmailIdAndProceed();

                    }
                }

            } else {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, "Invalid Email Id", view);
            }
        }
    }

    /**
     * Method is used to check email on server and proceed
     */
    private void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {

            Utils.getInstance().getHideKeyboard(this);

            //noinspection deprecation
            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);


            String email;
            if (twitter) {
                email = mEmailId.getText().toString();
            } else {
                email = mEmailIdFB.getText().toString();
            }
            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
            }

            loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
            loginEmailModel.registerView(this);
            loginEmailModel.setProgressDialog(pDialog);
            loginEmailModel.loadLoginData(email);


        } else {
            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
    }

    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {
        switch (requestCode) {
            case PERMISSION_REQUEST_MUST:
                if (isGranted) {
                    //perform action here
                    //initialiseAllData();
                } else {
                    if (!askAgainForMustPermissions) {
                        askAgainForMustPermissions = true;
                        haveAllMustPermissions(writeExternalStorage);
                    } else if (!goToSettingsScreen) {
                        goToSettingsScreen = true;

                        showPermissionsConfirmationDialog(getResources().getString(R.string.vault_permission));

                    } else {
                        showPermissionsConfirmationDialog(getResources().getString(R.string.vault_permission));
                    }

                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (isBackToSplashScreen) {
                isBackToSplashScreen = false;
                if (haveAllMustPermissions(writeExternalStorage)) {
                    initialiseAllData();
                }
            }
        }
    }




    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        String title = getResources().getString(R.string.add_profile_pic);
        SpannableStringBuilder sb = new SpannableStringBuilder(title);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.app_theme_color)), 0, title.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setTitle(sb);;
        builder.setItems(alertListItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    // Pick from camera
                    choiceAvatarFromCamera();
                } else {
                    // Pick from gallery
                    // Filesystem.
                    final Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    // Chooser of filesystem options.
                    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

                    startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static final int PICK_FROM_CAMERA = 1;

    /**
     * Method is used to set image from camera
     */
    private void choiceAvatarFromCamera() {
        // Check for Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Check for Nougat devices, as Nougat doesn't support Uri.
            // We need to provide FileProvider to access file system for image cropping
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider",
                        sdImageMainDirectory);
            } else {
                // Marshmallow doesn't require FileProviders, they can use Uri to access
                // File system for image cropping
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method is used to open gallery image chooser dialog to set image from gallery
     */
    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() +
                File.separator + getResources()
                .getString(R.string.profile_pic_directory) + File.separator);
        //noinspection ResultOfMethodCallIgnored
        root.mkdirs();
        Random randomNumber = new Random();
        final String fname = getResources()
                .getString(R.string.profile_pic_directory) + "_" + randomNumber.nextInt(1000) + 1;
        sdImageMainDirectory = new File(root, fname);

        getUserChooserOptions();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case YOUR_SELECT_PICTURE_REQUEST_CODE: {
                    final boolean isCamera;
                    isCamera = data == null || MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                    Log.d(tag, "Camera" + isCamera);
                    selectedImageUri = data.getData();
                }
                break;
                case PICK_FROM_CAMERA: {
                    selectedImageUri = outputFileUri;
                }
                break;
            }

            if (selectedImageUri != null) {
                try {
                    selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, RegistrationActivity.this);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, RegistrationActivity.this, sdImageMainDirectory);
                                          AppController.getInstance().getModelFacade().getLocalModel().setSelectImageBitmap(selectedBitmap);
                    mProfileImage.setImageBitmap(selectedBitmap);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 3, screenWidth / 3);
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    mProfileImage.setLayoutParams(lp);
                    isImageProvided = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(RegistrationActivity.this,R.drawable.camera_background));
                }
            }
        }

        if (requestCode == 500) {
            isBackToSplashScreen = true;
        }
    }

    private boolean isValidText(String text) {
        return text != null && text.length() >= 3;
    }

    /**
     * Method to check email validation
     * @param email set the email address
     * @return the value true and false.
     */
    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            Utils.getInstance().showToastMessage(this, "Email Not Entered!", view);
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                Utils.getInstance().showToastMessage(this, "Invalid Email", view);
                return false;
            } else
                return matcher.matches();
        }
    }

    /**
     * Method used for show the alert dialog for override scenario.
     * @param loginType set the login type Like: Google,Facebook and twitter.
     * @param emailId set the email address.
     */
    private void showAlertDialog(String loginType, String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, " + emailId + ", with " + loginType + " login, would you like to update your profile with this new login method?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        overrideUserData();


                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        LoginManager.getInstance().logOut();
                        Utils.getInstance().getHideKeyboard(RegistrationActivity.this);
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.leftin, R.anim.rightout);
                        finish();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method used for show the alert dialog box.
     * @param loginType set the type of login
     * @param emailId set the email address.
     */
    private void showAlertBox(String loginType, String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, " + emailId + ", with " + loginType + " login, would you like to update your profile with this new login method?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        if (socialUser != null) {
                            showAlert(socialUser.getEmailID());
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        // tvFacebookLogin.setText("Login with Facebook");
                        LoginManager.getInstance().logOut();
                        Twitter.logOut();
                        Utils.getInstance().getHideKeyboard(RegistrationActivity.this);
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.leftin, R.anim.rightout);
                        finish();
                    }
                });


        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method used for show the alert dialog box for set
     * the verfication code on your registered email address.
     * @param emailId set the email address.
     */
    private void showAlert(final String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Verification code has been sent to be on " + emailId + " .");
        String title = "Confirmation";
        SpannableStringBuilder sb = new SpannableStringBuilder(title);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.app_theme_color)),
                0, title.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        alertDialogBuilder.setTitle(sb);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        if (isImageProvided) {
                            try {
                                selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, RegistrationActivity.this);
                                selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, RegistrationActivity.this, sdImageMainDirectory);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
                            socialUser.setImageurl(convertedImage);
                        }
                        AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
                        Intent intent = new Intent(RegistrationActivity.this, VerificationEmailActivity.class);
                        intent.putExtra("registration_screen", true);
                        intent.putExtra("email_id", emailId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        finish();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
    }

    /**
     * Method is used to initialize birth year wheel view
     */
    private void initWheel() {
        int startingYear = 1901;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int numberOfYears = currentYear - startingYear;
        yearArray = new String[numberOfYears + 1];
        int yearCheck = startingYear;
        for (int i = 0; i <= numberOfYears; i++) {
            yearArray[i] = String.valueOf(yearCheck);
            yearCheck++;
        }

        yearWheel.setViewAdapter(new NumericWheelAdapter(this, startingYear, currentYear));
        yearWheel.setCurrentItem(numberOfYears / 2);

        yearWheel.addChangingListener(changedListener);
        yearWheel.addScrollingListener(scrolledListener);
        yearWheel.setCyclic(false);
    }


    // Wheel scrolled listener
    private final OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }

        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            mYOB.setText(yearArray[yearWheel.getCurrentItem()]);
        }
    };

    // Wheel changed listener
    private final OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                mYOB.setText(String.valueOf(yearArray[newValue]));
            }
        }
    };



    @Override
    public void update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fetchingAllDataModel != null && fetchingAllDataModel.getState() == BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                    showAlertDialogForSuccess();
                } else if (fbLoginModel != null && fbLoginModel.getState() == BaseModel.STATE_SUCCESS_FETCH_FB_DATA) {
                    fbLoginModel.unRegisterView(RegistrationActivity.this);
                    getFBData();
                } else if (mMailChimpModelData != null && mMailChimpModelData.getState() == BaseModel.STATE_SUCCESS_MAIL_CHIMP) {
                    mMailChimpModelData.unRegisterView(RegistrationActivity.this);
                    if (!Utils.isInternetAvailable(RegistrationActivity.this) && pDialog.isShowing()) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
                    } else {
                        getAllSocialLoginData("Y");
                    }
                    pDialog.dismiss();
                } else if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                    pDialog.dismiss();
                    loginEmailModel.unRegisterView(RegistrationActivity.this);
                    if (Utils.isInternetAvailable(RegistrationActivity.this)) {
                        if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {

                            showAlertBox("Vault", mEmailId.getText().toString());

                        } else if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")) {

                            showAlertBox("Facebook", mEmailId.getText().toString());

                        } else if (loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")) {

                            showAlertBox("Twitter", mEmailId.getText().toString());

                        } else if (loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {

                            showAlertBox("Google", mEmailId.getText().toString());

                        } else {

                            showConfirmLoginDialog(getResources().getString(R.string.do_you_want_to_join_our_mailing_list), firstName,
                                    lastName, socialUser.getEmailID());
                        }

                    } else {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
                    }

                } else {
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mFirstName.getVisibility() == View.VISIBLE) {
            twitter = false;
            super.onBackPressed();
            finish();
            LoginManager.getInstance().logOut();
            Twitter.logOut();
            Auth.GoogleSignInApi.signOut(LoginEmailActivity.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            //updateUI(null);
                        }
                    });
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
        } else {
            navigateBackToRegistration();
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
        }
    }

    /**
     * Method is used to show mail chimp confirmation dialog
     * @param mailChimpMessage set the mail chimp message.
     * @param firstName set first name.
     * @param lastName set the last name.
     * @param emailId set the email address
     */
    private void showConfirmLoginDialog(String mailChimpMessage, final String firstName, final String lastName, final String emailId) {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        TextView message = new TextView(this);
        //message.setGravity(Gravity.CENTER);
        message.setPadding(75, 50, 5, 10);
        message.setTextSize(17);
        message.setText(mailChimpMessage);
        message.setTextColor(ContextCompat.getColor(this, R.color.gray));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(message);
        alertDialogBuilder.setTitle("Join our Mailing List?");
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton("No Thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(false);
                        getAllSocialLoginData("N");

                    }
                });

        alertDialogBuilder.setNegativeButton("Yes! Keep me Updated",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(RegistrationActivity.this)) {
                            AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(true);
                            loadDataFromMailChimp(emailId, firstName, lastName);

                        } else {
                            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
                        }
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(Color.GRAY);
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method used for get the all social login data from server.
     * @param registerValue set the string value.
     */
    private void getAllSocialLoginData(String registerValue) {

        facebookLogin(socialUser, registerValue);

    }

    /**
     * Method used for load mail chimp data from server
     * @param email   email
     * @param firstName first name
     * @param lastName  last name
     */
    private void loadDataFromMailChimp(String email, String firstName, String lastName) {

        //noinspection deprecation
        pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
        pDialog.setCanceledOnTouchOutside(false);

        if (mMailChimpModelData != null) {
            mMailChimpModelData.unRegisterView(this);
        }
        mMailChimpModelData = AppController.getInstance().getModelFacade().getRemoteModel().
                getMailChimpDataModel();
        mMailChimpModelData.registerView(this);
        mMailChimpModelData.setProgressDialog(pDialog);
        mMailChimpModelData.loadMailChimpData(null, email, firstName, lastName);
    }

    /**
     * Method is used to fetch data after social login and start TrendingFeaturedVideoService
     */
    private void getFetchDataResponse() {
        try {
            pDialog.dismiss();
            fetchingAllDataModel.unRegisterView(RegistrationActivity.this);
            if (Utils.isInternetAvailable(RegistrationActivity.this)) {
                if (fetchingAllDataModel.getABoolean()) {
                    twitter = false;
                    Profile fbProfile = Profile.getCurrentProfile();
                    SharedPreferences pref = AppController.getInstance().
                            getApplicationContext()
                            .getSharedPreferences(getResources()
                                    .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                    if (fbProfile != null || userId > 0) {

                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();
                        startService(new Intent(RegistrationActivity.this, TrendingFeaturedVideoService.class));
                    }
                } else {
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
                }
            } else {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
            }


        } catch (Exception e) {
            e.printStackTrace();
            stopService(new Intent(RegistrationActivity.this, TrendingFeaturedVideoService.class));
            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
            pDialog.dismiss();

        }
    }

    /**
     * Method is used to show alert dialog after successful registration
     */
    private void showAlertDialogForSuccess() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(GlobalConstants.USER_SUCCESSFULLY_REGISTERED);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(false);
                        getFetchDataResponse();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(RegistrationActivity.this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    private Animation leftOutAnimation;
    private Animation rightInAnimation;

    /**
     * Method is used to proceed to next view after registration
     */
    private void checkEmailIdAndProceed() {

        leftOutAnimation = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.rightin);

        mFirstName.setAnimation(leftOutAnimation);
        mLastName.setAnimation(leftOutAnimation);
        mYOB.setAnimation(leftOutAnimation);
        mGender.setAnimation(leftOutAnimation);
        mUserName.setAnimation(leftOutAnimation);
        mRegistrationButton.setAnimation(leftOutAnimation);
        tvAlreadyRegistered.setAnimation(leftOutAnimation);
        if (twitter) {
            mEmailId.setAnimation(leftOutAnimation);
            mEmailId.setVisibility(View.GONE);
        } else {
            mEmailIdFB.setAnimation(leftOutAnimation);
            mEmailIdFB.setVisibility(View.GONE);
        }


        mFirstName.setVisibility(View.GONE);
        mLastName.setVisibility(View.GONE);
        mYOB.setVisibility(View.GONE);
        mGender.setVisibility(View.GONE);
        mUserName.setVisibility(View.GONE);
        mRegistrationButton.setVisibility(View.GONE);
        tvAlreadyRegistered.setVisibility(View.GONE);
        //tvHeader.setText("Register");

        mProfileImage.setAnimation(rightInAnimation);
        mSignUpButton.setAnimation(rightInAnimation);
        tvUploadPhoto.setAnimation(rightInAnimation);
        tvSignUpWithoutProfile.setAnimation(rightInAnimation);

        mProfileImage.setVisibility(View.VISIBLE);
        mSignUpButton.setVisibility(View.VISIBLE);
        tvUploadPhoto.setVisibility(View.VISIBLE);
        tvSignUpWithoutProfile.setVisibility(View.VISIBLE);
        pBar.setVisibility(View.VISIBLE);
    }

    /**
     * Method is used to navigate back to registration screen
     */
    private void navigateBackToRegistration() {

        leftOutAnimation = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.leftin);
        rightInAnimation = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.rightout);

        mFirstName.setAnimation(leftOutAnimation);
        mLastName.setAnimation(leftOutAnimation);
        mYOB.setAnimation(leftOutAnimation);
        mGender.setAnimation(leftOutAnimation);
        mEmailId.setAnimation(leftOutAnimation);
        mEmailIdFB.setAnimation(leftOutAnimation);
        mUserName.setAnimation(leftOutAnimation);
        mRegistrationButton.setAnimation(leftOutAnimation);
        tvAlreadyRegistered.setAnimation(leftOutAnimation);

        if (twitter) {
            mEmailId.setAnimation(leftOutAnimation);
            mEmailId.setVisibility(View.VISIBLE);
        } else {
            mEmailIdFB.setAnimation(leftOutAnimation);
            mEmailIdFB.setVisibility(View.VISIBLE);
        }

        mFirstName.setVisibility(View.VISIBLE);
        mLastName.setVisibility(View.VISIBLE);
        mYOB.setVisibility(View.VISIBLE);
        mGender.setVisibility(View.VISIBLE);
        mUserName.setVisibility(View.VISIBLE);
        mRegistrationButton.setVisibility(View.VISIBLE);
        tvAlreadyRegistered.setVisibility(View.VISIBLE);
        //tvHeader.setText("Register");

        mProfileImage.setAnimation(rightInAnimation);
        mSignUpButton.setAnimation(rightInAnimation);
        tvUploadPhoto.setAnimation(rightInAnimation);
        tvSignUpWithoutProfile.setAnimation(rightInAnimation);

        mProfileImage.setVisibility(View.GONE);
        mSignUpButton.setVisibility(View.GONE);
        tvUploadPhoto.setVisibility(View.GONE);
        tvSignUpWithoutProfile.setVisibility(View.GONE);
        pBar.setVisibility(View.GONE);

    }

}

