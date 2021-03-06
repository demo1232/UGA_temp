package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.dto.MailChimpData;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.FBLoginModel;
import com.ncsavault.models.FetchingAllDataModel;
import com.ncsavault.models.LoginEmailModel;
import com.ncsavault.models.LoginPasswordModel;
import com.ncsavault.models.MailChimpDataModel;
import com.ncsavault.models.UserDataModel;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import applicationId.R;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

/**
 * Class used for Login into the application
 * User can login with Google,Facebook and Twitter.
 * User can vault login.
 * Also we have implemented skip functionality on Login screen.
 */
public class LoginEmailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, AbstractView {

    private final String Tag = "LoginEmailActivity";
    private EditText edEmailBox, edPassword;
    private TextView tvSkipLogin, createNewAccount, tvForgotPassword;
    private Button tvNextLogin;
    private ImageView tvFacebookLogin;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private final User socialUser = new User();
    private AlertDialog alertDialog;
    @SuppressWarnings("deprecation")
    private
    ProgressDialog pDialog;
    private Animation animation;
    private SharedPreferences prefs;
    private ImageView imageViewGmailLogin;

    private ImageView imageViewPassword;

    private LoginEmailModel loginEmailModel;
    private FetchingAllDataModel fetchingAllDataModel;
    private FBLoginModel fbLoginModel;
    public static GoogleApiClient mGoogleApiClient;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final Bundle params = new Bundle();
    private TwitterLoginButton twitterLoginButton;
    private ImageView imgTwitterLogin;
    private AsyncTask<Void, Void, String> mLoginTask;
    private boolean isFBLogin = false;
    private AsyncTask<Void, Void, String> mOverrideUserTask;
    private static final int RC_SIGN_IN = 9001;
    private LoginPasswordModel mLoginPasswordModel;
    private MailChimpDataModel mMailChimpModelData;
    private UserDataModel mVaultUserDataModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(Tag, "push notification LoginEmailActivity");
            initThirdPartyLibrary();


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(LoginEmailActivity.this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            // firebaseAuth();
            registerFacebookCallbackManager();

            //  setContentView(R.layout.login_email_activity);
            setContentView(R.layout.login_screen_layout);

            initAllDataRequiredInEmailActivity();
            AppController.getInstance().setCurrentActivity(this);
            AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Method used for register facebook callback
     */
    private void registerFacebookCallbackManager() {

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFacebookLoginStatus(loginResult);
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        showAlert();
                    }

                    private void showAlert() {
                        showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);

                    }
                });
    }


    /**
     * Method used for initialize third party library.
     */
    private void initThirdPartyLibrary() {
        callbackManager = CallbackManager.Factory.create();

        // The Dev key cab be set here or in the manifest.xml
        AppsFlyerLib.setAppsFlyerKey(getResources().getString(R.string.flyer_key));
        AppsFlyerLib.sendTracking(getApplicationContext());

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(getResources()
                        .getString(R.string.twitter_consumer_key),
                        getResources().getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));
    }

    /**
     *  Method used for initialize the all the data for Login Email Activity.
     */
    private void initAllDataRequiredInEmailActivity() {
        initViews();
        initData();


        String videoId = AppController.getInstance().getModelFacade().getLocalModel().getVideoId();

        prefs = getSharedPreferences(getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);

        if (Utils.isInternetAvailable(this)) {
            boolean isConfirmed = prefs.getBoolean(GlobalConstants.PREF_IS_CONFIRMATION_DONE, false);
            if (!isConfirmed)
                showNotificationConfirmationDialog(LoginEmailActivity.this);
        }

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            }
        };


        String videoUrl = AppController.getInstance().getModelFacade().getLocalModel().getVideoUrl();
        if (videoUrl != null) {
            skipLogin();
        }

        if (videoId != null && !videoId.equalsIgnoreCase("0")) {
            skipLogin();
        }
        initListener();
    }

    /**
     * Method used for init Data
     */
    private void initData() {
        try {
            WindowManager w = getWindowManager();
            int screenWidth;

            Display d = w.getDefaultDisplay();
            //noinspection deprecation
            screenWidth = d.getWidth();


            int dimension = (int) (screenWidth * 0.45);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dimension, dimension);
            lp.setMargins(0, 30, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for initialize the login screen component.
     */
    private void initViews() {
        try {
            Utils.getInstance().setAppName(this);
            edEmailBox =  findViewById(R.id.ed_email);
            edPassword =  findViewById(R.id.ed_password);

            imageViewPassword =  findViewById(R.id.image_view_password);
            imageViewPassword.setTag(R.drawable.eye_on);
            imageViewPassword.setOnTouchListener(mPasswordVisibleTouchListener);
            tvFacebookLogin =  findViewById(R.id.tv_facebook_login);
            imageViewGmailLogin =  findViewById(R.id.gmail_login);
            imgTwitterLogin =  findViewById(R.id.twitter_login);
            tvSkipLogin =  findViewById(R.id.tv_skip_login);
            tvNextLogin =  findViewById(R.id.tv_next_email);

            createNewAccount =  findViewById(R.id.tv_new_account);
            tvForgotPassword =  findViewById(R.id.tv_forgot_password);

            twitterLogin();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for handle the view event.
     */
    private void initListener() {

        tvSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().getHideKeyboard(LoginEmailActivity.this);
                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    SharedPreferences prefs = getSharedPreferences(getResources()
                            .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                    prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, GlobalConstants.DEFAULT_USER_ID).apply();
                    prefs.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, true).apply();
                    AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(false);
                    fetchInitialRecordsForAll();
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

            }
        });

        tvFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edEmailBox.getText().toString().isEmpty()) {
                    edEmailBox.setText("");
                }
                if (!edPassword.getText().toString().isEmpty()) {
                    edPassword.setText("");
                }
                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    LoginManager.getInstance().logOut();
                    LoginManager.getInstance().logInWithReadPermissions(LoginEmailActivity.this, Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));

                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        imageViewGmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edEmailBox.getText().toString().isEmpty()) {
                    edEmailBox.setText("");
                }
                if (!edPassword.getText().toString().isEmpty()) {
                    edPassword.setText("");
                }
                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    signOut();
                    if (mGoogleApiClient != null) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

            }
        });

        imgTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.getInstance().getHideKeyboard(LoginEmailActivity.this);

                if (!edEmailBox.getText().toString().isEmpty()) {
                    edEmailBox.setText("");
                }
                if (!edPassword.getText().toString().isEmpty()) {
                    edPassword.setText("");
                }

                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    boolean installedTwitterApp = checkIfAppInstalled();
                    if (!installedTwitterApp) {
                        String twitterPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                        showConfirmSharingDialog(twitterPlayStoreUrl);

                    } else {
                        Twitter.logOut();
                        twitterLoginButton.performClick();
                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });


        edEmailBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    checkEmailAndProceed();
                }
                return false;
            }
        });


        tvNextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSocialUser = false;
                Utils.getInstance().getHideKeyboard(LoginEmailActivity.this);
                if (edPassword != null) {
                    String oldPass = edPassword.getText().toString().trim();
                    oldPass = oldPass.replace(" ", "");
                    edPassword.setText(oldPass);
                }
                checkEmailAndProceed();
            }
        });

        edEmailBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edEmailBox != null) {
                    edEmailBox.setText("");
                    edPassword.setText("");
                }

                AppController.getInstance().getModelFacade().getLocalModel().setEmailId("");
                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginEmailActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("email_id", edEmailBox.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);

            }
        });

    }


    private final View.OnTouchListener mPasswordVisibleTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int cursor;

            // change input type will reset cursor position, so we want to save it
            cursor = edPassword.getSelectionStart();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                if ((Integer) imageViewPassword.getTag() == R.drawable.eye_on) {

                    edPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edPassword.setTypeface(edPassword.getTypeface(), Typeface.BOLD);
                    imageViewPassword.setImageResource(R.drawable.eyeoff);
                    imageViewPassword.setTag(R.drawable.eyeoff);
                } else {
                    edPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edPassword.setTypeface(edPassword.getTypeface(), Typeface.BOLD);
                    imageViewPassword.setImageResource(R.drawable.eye_on);
                    imageViewPassword.setTag(R.drawable.eye_on);
                }

                edPassword.setSelection(cursor);
            }

            return true;
        }
    };


    /**
     * Method used for sign out the google account.
     */
    private void signOut() {
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }


    /**
     * Method used for the fetch all the  user detail and data from server.
     */
    private void fetchInitialRecordsForAll() {

        if (Utils.isInternetAvailable(this)) {
            //noinspection deprecation
            pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LoginManager.getInstance().logOut();
                }
            });

            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
                loginEmailModel = null;
            }

            if (fbLoginModel != null) {
                fbLoginModel.unRegisterView(this);
                fbLoginModel = null;
            }

            if (mVaultUserDataModel != null) {
                mVaultUserDataModel.unRegisterView(this);
                mVaultUserDataModel = null;
            }

            if (mMailChimpModelData != null) {
                mMailChimpModelData.unRegisterView(this);
                mMailChimpModelData = null;
            }

            if (mLoginPasswordModel != null) {
                mLoginPasswordModel.unRegisterView(this);
                mLoginPasswordModel = null;
            }

            if (fetchingAllDataModel != null) {
                fetchingAllDataModel.unRegisterView(this);
            }
            fetchingAllDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getFetchingAllDataModel();
            fetchingAllDataModel.registerView(this);
            fetchingAllDataModel.setProgressDialog(pDialog);
            fetchingAllDataModel.fetchData();

        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    /**
     * Method used for check the email validation on server.
     */
    private void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidEmail(edEmailBox.getText().toString().trim())) {
                Utils.getInstance().getHideKeyboard(this);

                //noinspection deprecation
                pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);

                String email = edEmailBox.getText().toString().trim();
                email = email.replace(" ", "");
                edEmailBox.setText(email);
                AppController.getInstance().getModelFacade().getLocalModel().setEmailId(email);
                AppController.getInstance().getModelFacade().getLocalModel().storeEmailId(email);

                if (loginEmailModel != null) {
                    loginEmailModel.unRegisterView(this);
                    loginEmailModel = null;
                }

                loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
                loginEmailModel.registerView(this);
                loginEmailModel.setProgressDialog(pDialog);
                loginEmailModel.loadLoginData(email);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    /**
     * Method used for check valid email.
     * @param email set the email address
     * @return the value true and false.
     */
    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            showToastMessage(GlobalConstants.ENTER_EMAIL_AND_PASSWORD);
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            email = email.replace(" ", "");
            edEmailBox.setText(email);
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                showToastMessage("Please enter valid email id");
                return false;
            } else
                return matcher.matches();
        }
    }

    /**
     * Method used for show the alert dialog box for override email on server.
     * @param loginType Google, Facebook and twitter.
     * @param emailId set the email address
     * @param existFrom That means which account we are already exit.
     */
    private void showAlertDialog(String loginType, final String emailId, final String existFrom) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, " + emailId + ", with " + loginType + " login, would you like to update your profile with this new login method?");

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        pDialog.dismiss();
                        if ((existFrom.equals("gm_exists") || existFrom.equals("tw_exists") ||
                                existFrom.equals("fb_exists") || existFrom.equals("vt_exists")) && !isSocialUser) {
                            AppController.getInstance().getModelFacade().getLocalModel().setOverride(true);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email_id", edEmailBox.getText().toString());
                            SharedPreferences pref = AppController.getInstance().getApplication().
                                    getSharedPreferences(getResources()
                                            .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                            pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false).apply();
                            String email = pref.getString(GlobalConstants.PREF_VAULT_EMAIL, "");
                            String emailBox = edEmailBox.getText().toString();
                            if (emailBox.equals(email)) {
                                pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, true).apply();
                            } else {
                                AppController.getInstance().getModelFacade().getLocalModel().setSelectImageBitmap(null);
                            }
                            AppController.getInstance().getModelFacade()
                                    .getLocalModel().setMailChimpRegisterUser(false);
                            AppController.getInstance().handleEvent(AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN, hashMap);
                            overridePendingTransition(R.anim.rightin, R.anim.leftout);

                        } else if ((existFrom.equals("gm_exists") || existFrom.equals("tw_exists") ||
                                existFrom.equals("fb_exists") || existFrom.equals("vt_exists")) && isSocialUser) {
                            AppController.getInstance().getModelFacade().getLocalModel().setOverride(true);
                            AppController.getInstance().getModelFacade().getLocalModel().setEmailId(emailId);
                            AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);
                            overridePendingTransition(R.anim.rightin, R.anim.leftout);
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        pDialog.dismiss();
                        isSocialUser = false;
                        LoginManager.getInstance().logOut();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle sign in
            handleSignInResult(result);
        }
        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileTracker != null) {
            profileTracker.stopTracking();
        }
    }

    /**
     * Method used for show the toast message.
     * @param message set the message string.
     */
    @SuppressLint("PrivateResource")
    private void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

        final TextView text = includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("PrivateResource")
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(LoginEmailActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method used for show the dialog box for notification allow or deny.
     * @param mActivity the reference of Activity.
     */
    private void showNotificationConfirmationDialog(final Activity mActivity) {

        prefs = mActivity.getSharedPreferences(getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.notification_message));

        alertDialogBuilder.setPositiveButton("Allow",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Utils.getInstance().registerWithGCM(mActivity);
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_CONFIRMATION_DONE, true).apply();
                    }
                });
        alertDialogBuilder.setNegativeButton("Deny",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        Utils.getInstance().unRegisterWithGCM(mActivity);
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, false).apply();
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_CONFIRMATION_DONE, true).apply();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method used for skip login into the application.
     */
    private void skipLogin() {
        SharedPreferences prefs = getSharedPreferences(getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, GlobalConstants.DEFAULT_USER_ID).apply();
        prefs.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, true).apply();

        fetchInitialRecordsForAll();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void update() {
        Log.d(Tag, "login screen");
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              try {
                                  if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                                      pDialog.dismiss();
                                      loginEmailModel.unRegisterView(LoginEmailActivity.this);
                                      if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                                          if (isValidPassword(edPassword.getText().toString())) {
                                              if (loginEmailModel != null) {
                                                  String emailId = AppController.getInstance().getModelFacade().getLocalModel().getEmailId();
                                                  if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")) {
                                                      showAlertDialog("Facebook", emailId, "fb_exists");
                                                  } else if (loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")) {
                                                      showAlertDialog("Twitter", emailId, "tw_exists");
                                                  } else if (loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {
                                                      showAlertDialog("Google", emailId, "gm_exists");
                                                  } else {

                                                      // if (!isSocialUser) {

                                                      @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") HashMap<String, String> stringHashMap = new HashMap<>();
                                                      stringHashMap.put("email", edEmailBox.getText().toString().trim());
                                                      stringHashMap.put("status", loginEmailModel.getLoginResult());
                                                      AppController.getInstance().getModelFacade().getLocalModel().
                                                              setRegisterEmailId(edEmailBox.getText().toString());

                                                      if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {
                                                          loginVaultUser();
                                                      } else {
                                                          showToastMessage(GlobalConstants.NOT_REGISTERED);

                                                      }
                                                      overridePendingTransition(R.anim.rightin, R.anim.leftout);


                                                  }
                                              }
                                          }


                                      } else {
                                          showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                      }
                                  } else if (fetchingAllDataModel != null && fetchingAllDataModel.getState() == BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                                      try {
                                          pDialog.dismiss();
                                          fetchingAllDataModel.unRegisterView(LoginEmailActivity.this);
                                          if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                                              if (fetchingAllDataModel.getABoolean()) {
                                                  Profile fbProfile = Profile.getCurrentProfile();
                                                  SharedPreferences pref = AppController.getInstance().getApplicationContext()
                                                          .getSharedPreferences(getResources()
                                                                  .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                                                  long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                                                  if (fbProfile != null || userId > 0) {

                                                      if (fetchingAllDataModel.getResponseUserData().getEmailID() == null) {
                                                          AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);

                                                          overridePendingTransition(R.anim.slideup, R.anim.nochange);
                                                          finish();
                                                          Intent intent = new Intent(LoginEmailActivity.this, TrendingFeaturedVideoService.class);
                                                          startService(intent);
                                                      } else {
                                                          if (fetchingAllDataModel.getResponseUserData().getIsRegisteredUser().equals("N") && !AppController.getInstance().getModelFacade()
                                                                  .getLocalModel().getMailChimpRegisterUser()) {
                                                              AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(true);
                                                              String mFirstName = fetchingAllDataModel.getResponseUserData().getFname().substring(0, 1).toUpperCase() + fetchingAllDataModel.getResponseUserData().getFname().substring(1);//AppController.getInstance().getFirstName().toString();
                                                              String mLastName = fetchingAllDataModel.getResponseUserData().getLname().substring(0, 1).toUpperCase() + fetchingAllDataModel.getResponseUserData().getLname().substring(1);
                                                              String mEmailId = fetchingAllDataModel.getResponseUserData().getEmailID();
                                                              long mUserId = fetchingAllDataModel.getResponseUserData().getUserID();

                                                              showConfirmLoginDialog(getResources().getString(R.string.do_you_want_to_join_our_mailing_list),
                                                                      mFirstName, mLastName, mEmailId, mUserId);
                                                          } else {
                                                              AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);

                                                              overridePendingTransition(R.anim.slideup, R.anim.nochange);
                                                              finish();

                                                              Intent intent = new Intent(LoginEmailActivity.this, TrendingFeaturedVideoService.class);
                                                              startService(intent);
                                                          }
                                                      }
                                                  }
                                              } else {
                                                  showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                                              }
                                          } else {
                                              showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                          }


                                      } catch (Exception e) {
                                          e.printStackTrace();
                                          stopService(new Intent(LoginEmailActivity.this, TrendingFeaturedVideoService.class));
                                          VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
                                          pDialog.dismiss();
                                      }
                                  } else if (fbLoginModel != null && fbLoginModel.getState() == BaseModel.STATE_SUCCESS_FETCH_FB_DATA) {
                                      fbLoginModel.unRegisterView(LoginEmailActivity.this);
                                      getFBData();
                                  } else if (mLoginPasswordModel != null && mLoginPasswordModel.getState() ==
                                          BaseModel.STATE_SUCCESS_EMAIL_PASSWORD_DATA) {

                                      mLoginPasswordModel.unRegisterView(LoginEmailActivity.this);
                                      loadEmailAndPasswordData();
                                  } else if (mMailChimpModelData != null && mMailChimpModelData.getState() ==
                                          BaseModel.STATE_SUCCESS_MAIL_CHIMP) {

                                      mMailChimpModelData.unRegisterView(LoginEmailActivity.this);
                                      AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);
                                      overridePendingTransition(R.anim.slideup, R.anim.nochange);
                                      finish();
                                      startService(new Intent(LoginEmailActivity.this, TrendingFeaturedVideoService.class));
                                      pDialog.dismiss();

                                  } else if (mVaultUserDataModel != null && mVaultUserDataModel.getState() ==
                                          BaseModel.STATE_SUCCESS_VAULTUSER_DATA) {
                                      mVaultUserDataModel.unRegisterView(LoginEmailActivity.this);
                                      loadVaultUserData();
                                  }

                              } catch (
                                      Exception e
                                      )

                              {
                                  e.printStackTrace();

                              }
                          }
                      }

        );
    }

    /**
     * Method used for save fb data at server
     */
    private void getFBData() {
        Log.d(Tag, "Result of post user data : " + fbLoginModel.getResultData());
        if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
            if (fbLoginModel.getResultData() != null) {
                if (fbLoginModel.getResultData().contains("success")) {
                    pDialog.dismiss();
                    SharedPreferences pref = getSharedPreferences(getResources()
                            .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                    pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, fbLoginModel.getUserId()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                    pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                    fetchInitialRecordsForAll();

                } else {
                    try {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(fbLoginModel.getResultData().trim(), classType);
                        if (response.getReturnStatus() != null) {
                            if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                pDialog.dismiss();
                                showAlertDialog("Vault", response.getEmailID(), "vt_exists");
                            } else if (response.getReturnStatus().toLowerCase().contains("fb_exists") /*|| response.getReturnStatus().toLowerCase().contains("" +
                                                    "")*/) {
                                pDialog.dismiss();
                                SharedPreferences pref = getSharedPreferences(getResources()
                                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                                pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                                params.putString("fb_exist", "fb_exist");
                                mFirebaseAnalytics.logEvent("fb_exist", params);

                                fetchInitialRecordsForAll();

                            } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                pDialog.dismiss();
                                showAlertDialog("Twitter", response.getEmailID(), "tw_exists");
                            } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                pDialog.dismiss();
                                showAlertDialog("Google", response.getEmailID(), "gm_exists");
                            }
                        } else {
                            pDialog.dismiss();
                            LoginManager.getInstance().logOut();
                            // tvFacebookLogin.setText("Login with Facebook");
                            //showToastMessage(result);
                            showToastMessage("Can not connect to server. Please try again...");
                        }

                    } catch (Exception e) {
                        LoginManager.getInstance().logOut();
                        e.printStackTrace();
                        pDialog.dismiss();
                        showToastMessage("We are unable to process your request");
                    }
                }
            } else {
                showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
        pDialog.dismiss();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Method usd for get the login status and user detail from facebook.
     * @param loginResult the value of user detail.
     */
    private void getFacebookLoginStatus(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            URL image_path = null;
                            try {
                                image_path = new URL("http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large");
                                Log.d(Tag, "Image Path : " + image_path.toString());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            Log.v("LoginActivity", response.toString());

                            socialUser.setEmailID(object.getString("email"));
                            if (image_path != null)
                                socialUser.setImageurl(image_path.toString());
                            socialUser.setUsername(object.getString("name"));
                            socialUser.setPasswd("vault_fb_" + object.getString("id"));
                            socialUser.setGender("gender");
                            socialUser.setAppID(Integer.parseInt(getResources()
                                    .getString(R.string.app_id)));
                            socialUser.setAppVersion(getResources()
                                    .getString(R.string.app_version));
                            socialUser.setDeviceType(getResources()
                                    .getString(R.string.device_type));
                            socialUser.setFname(object.getString("first_name"));
                            socialUser.setLname(object.getString("last_name"));
                            socialUser.setFlagStatus("fb");
                            socialUser.setSocialLoginToken(object.getString("id"));

                            AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
                            AppController.getInstance().getModelFacade().getLocalModel().setFacebookLogin(true);
                            AppController.getInstance().getModelFacade().getLocalModel().setGoogleLogin(false);
                            AppController.getInstance().getModelFacade().getLocalModel().setTwitterLogin(false);
                            isFBLogin = true;
                            checkExistingUserOrNot(socialUser);


                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            // tvFacebookLogin.setText("Login with Facebook");
                            e.printStackTrace();
                        }
                    }
                });


        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * After the signing we are calling this function
     * @param result the value on google login result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        String image_path = " ";
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(Tag, "Token Gmail : " + acct.getId());

            // firebaseAuthWithGoogle(acct);
            socialUser.setEmailID(acct.getEmail());

            socialUser.setUsername(acct.getDisplayName());
            socialUser.setPasswd(acct.getId());
            //socialUser.setGender("gender");
            socialUser.setAppID(Integer.parseInt(getResources()
                    .getString(R.string.app_id)));
            socialUser.setAppVersion(getResources()
                    .getString(R.string.app_version));
            socialUser.setDeviceType(getResources()
                    .getString(R.string.device_type));
            socialUser.setFname(acct.getFamilyName());
            socialUser.setLname(acct.getGivenName());
            socialUser.setFlagStatus("gm");
            socialUser.setSocialLoginToken(acct.getId());

            if (acct.getPhotoUrl() != null) {
                image_path = acct.getPhotoUrl().toString();
            }
            if (image_path != null) {
                socialUser.setImageurl(image_path);
            } else {
                @SuppressWarnings("deprecation")
                Drawable image = getResources().getDrawable(R.drawable.camera_background);
                String defaultImage = image.toString();
                socialUser.setImageurl(String.valueOf(defaultImage));

            }

            AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
            AppController.getInstance().getModelFacade().getLocalModel().setFacebookLogin(false);
            AppController.getInstance().getModelFacade().getLocalModel().setGoogleLogin(true);
            AppController.getInstance().getModelFacade().getLocalModel().setTwitterLogin(false);

            checkExistingUserOrNot(socialUser);


        } else {
            //If login fails
            Toast.makeText(this, "Google sign in was cancelled.", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Method used for return the user detail after login on twitter.
     * @param result the user detail.
     */
    private void login(Result<TwitterSession> result) {

        //Creating a twitter session with result's data
        TwitterSession session = result.data;

        Call<com.twitter.sdk.android.core.models.User> call = Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false);
        call.enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void failure(TwitterException e) {
                //If any error occurs handle it here
                Log.e("Twitter Exception","TwitterException "+e.getMessage());
            }

            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                //If it succeeds creating a User object from userResult.data
                try {
                    //If it succeeds creating a User object from userResult.data
                    String image_path;
                    com.twitter.sdk.android.core.models.User user = userResult.data;
                    String fName;
                    String lName = "";
                    String twitterImage = user.profileImageUrl;
                    String userName = user.screenName;
                    String name = user.name;
                    long id = user.id;
                    if (name.toLowerCase().contains(" ")) {
                        String[] firstAndLastName = name.split(" ");
                        fName = firstAndLastName[0];
                        lName = firstAndLastName[1];
                    } else {
                        fName = name;
                    }

                    image_path = twitterImage;
                    if (image_path != null) {
                        socialUser.setImageurl(image_path);
                    } else {
                        @SuppressWarnings("deprecation")
                        Drawable image = getResources().getDrawable(R.drawable.camera_background);
                        String defaultImage = image.toString();
                        socialUser.setImageurl(String.valueOf(defaultImage));

                    }

                    if (user.email == null) {
                        socialUser.setEmailID("");
                    }

                    socialUser.setUsername(userName);
                    socialUser.setFname(fName);
                    socialUser.setLname(lName);
                    socialUser.setPasswd(String.valueOf(id));
                    socialUser.setImageurl(twitterImage);
                    socialUser.setAppID(Integer.parseInt(getResources()
                            .getString(R.string.app_id)));
                    socialUser.setAppVersion(getResources()
                            .getString(R.string.app_version));
                    socialUser.setDeviceType(getResources()
                            .getString(R.string.device_type));
                    socialUser.setFlagStatus("tw");
                    socialUser.setSocialLoginToken(String.valueOf(id));
                    AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
                    AppController.getInstance().getModelFacade().getLocalModel().setFacebookLogin(false);
                    AppController.getInstance().getModelFacade().getLocalModel().setGoogleLogin(false);
                    AppController.getInstance().getModelFacade().getLocalModel().setTwitterLogin(true);


                    checkExistingUserOrNot(socialUser);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //This code will fetch the profile image URL
        //Getting the account service of the user logged in
    }


    /**
     * Method used for twitter login
     */
    private void twitterLogin() {
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                login(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    private boolean isSocialUser = false;

    /**
     * Method user for check the user on server already exit or not.
     * @param socialUser the value of user detail
     */
    private void checkExistingUserOrNot(final User socialUser) {
        try {
            mLoginTask = new AsyncTask<Void, Void, String>() {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //noinspection deprecation
                    pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                    pDialog.show();
                    pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.setCancelable(false);
                }

                @Override
                protected String doInBackground(Void... params) {
                    String email = "";

                    if (socialUser.getEmailID() != null) {
                        email = socialUser.getEmailID();
                    }

                    return AppController.getInstance().getServiceManager().getVaultService().socialLoginExits(socialUser.getSocialLoginToken(), email);
                }

                @Override
                protected void onPostExecute(String result) {

                    Log.d(Tag, "Result of post user data : " + result);
                    if (result != null) {
                        if (result.contains("existing_user")) {
                            pDialog.dismiss();
                            mLoginTask = null;
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response != null) {
                                SharedPreferences pref = getSharedPreferences(getResources()
                                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, response.getEmailID()).apply();
                                fetchInitialRecordsForAll();
                            }
                        } else {

                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response != null) {
                                isSocialUser = true;
                                if (response.getReturnStatus().toLowerCase().contains("new_user")) {
                                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);
                                    overridePendingTransition(R.anim.rightin, R.anim.leftout);
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    if (isFBLogin) {
                                        isFBLogin = false;
                                        overrideUserData(socialUser);
                                    } else {
                                        showAlertDialog("Facebook", socialUser.getEmailID(), "fb_exists");
                                    }
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    showAlertDialog("Twitter", socialUser.getEmailID(), "tw_exists");
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    showAlertDialog("Google", socialUser.getEmailID(), "gm_exists");
                                } else if (response.getReturnStatus().toLowerCase().contains("vt_exists")) {
                                    showAlertDialog("Vault", socialUser.getEmailID(), "vt_exists");
                                }

                            }


                        }

                    }
                }
            };
            mLoginTask.execute();
        } catch (Exception e) {
            pDialog.dismiss();
            mLoginTask = null;
            LoginManager.getInstance().logOut();
            // tvFacebookLogin.setText("Login with Facebook");
            e.printStackTrace();
        }
    }


    /**
     * Method used for check the override scenario on server.
     * @param vaultUser set the detail of user data.
     */
    private void overrideUserData(final User vaultUser) {

        mOverrideUserTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //noinspection deprecation
                pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {

                    result = AppController.getInstance().getServiceManager().getVaultService().updateUserData(vaultUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Log.d(Tag, "Result of post user data : " + result);
                    if (result.contains("true") || result.contains("success")) {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(result.trim(), classType);
                        pDialog.dismiss();
                        SharedPreferences pref = getSharedPreferences(getResources()
                                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                        pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, vaultUser.getUsername()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, vaultUser.getEmailID()).apply();
                        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                        fetchInitialRecordsForAll();
                        params.putString("fb_exist", "fb_exist");
                        mFirebaseAnalytics.logEvent("fb_exist", params);
                    } else {
                        try {
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response.getReturnStatus() != null) {
                                if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Vault", vaultUser.getEmailID(), "vt_exists");
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Google", vaultUser.getEmailID(), "gm_exists");
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Twitter", vaultUser.getEmailID(), "tw_exists");
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Facebook", vaultUser.getEmailID(), "fb_exists");
                                }
                            } else {
                                pDialog.dismiss();
                                LoginManager.getInstance().logOut();
                                // tvFacebookLogin.setText("Login with Facebook");
                                // showToastMessage(result);
                                showToastMessage("Can not connect to server. Please try again...");
                            }

                            mOverrideUserTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mOverrideUserTask = null;
                            // tvFacebookLogin.setText("Login with Facebook");
                            showToastMessage("We are unable to process your request");
                        }
                    }

                }
            }
        };
        mOverrideUserTask.execute();
    }

    /**
     * Method used for check twitter app install or not in device.
     * @return the value true and false.
     */
    private boolean checkIfAppInstalled() {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.twitter.android", PackageManager.GET_ACTIVITIES);
            //Check if the Facebook app is disabled
            ApplicationInfo ai = getPackageManager().getApplicationInfo("com.twitter.android", 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    /**
     * Method used for showing the dialog box twitter app is not install.
     * @param playStoreUrl set the twitter app play store url.
     */
    private void showConfirmSharingDialog(final String playStoreUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Twitter app is not installed would you like to install it now?");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Install",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(playStoreUrl));
                        startActivityForResult(intent, 100);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }


    /**
     * Method used for validation of password.
     * @param pass set the password string
     * @return the value of true and false.
     */
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            if (pass.contains(" ")) {
                showToastMessage("Please enter valid password");
                return false;
            }
            return true;
        }
        if (pass != null) {
            if (pass.length() == 0) {
//                    edPassword.setError("Password not entered");
                showToastMessage(GlobalConstants.ENTER_EMAIL_AND_PASSWORD);
            } else if (pass.length() < 6) {
//                    edPassword.setError("Minimum 6 characters required!");
                showToastMessage("Password should contain minimum 6 characters!");
            }
        }
        return false;
    }

    /**
     * Method used for vault login in app.
     */
    private void loginVaultUser() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidPassword(edPassword.getText().toString())) {

                Utils.getInstance().getHideKeyboard(this);
                String password;
                String email;

                //noinspection deprecation
                pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                password = edPassword.getText().toString();
                email = edEmailBox.getText().toString().trim();

                if (loginEmailModel != null) {
                    loginEmailModel.unRegisterView(this);
                    loginEmailModel = null;
                }


                if (mLoginPasswordModel != null) {
                    mLoginPasswordModel.unRegisterView(this);
                }
                mLoginPasswordModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginPasswordModel();
                mLoginPasswordModel.registerView(this);
                mLoginPasswordModel.setProgressDialog(pDialog);
                mLoginPasswordModel.loadEmailAndPassData(email, password);

            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    /**
     * Method used for check email and password validation on server.
     */
    private void loadEmailAndPasswordData() {
        if (!Utils.isInternetAvailable(LoginEmailActivity.this) && mLoginPasswordModel.getProgressDialog().isShowing()) {
            if (mLoginPasswordModel.getEmailPasswordResult() == null || mLoginPasswordModel != null && mLoginPasswordModel.getEmailPasswordResult().equals("vt_exists")) {
                mLoginPasswordModel.getProgressDialog().dismiss();
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            } else {
                mLoginPasswordModel.getProgressDialog().dismiss();
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }

        } else {
            try {
                mLoginPasswordModel.getProgressDialog().dismiss();
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                if (mLoginPasswordModel.getEmailPasswordResult() != null) {
                    APIResponse response = gson.fromJson(mLoginPasswordModel.getEmailPasswordResult().trim(), classType);
                    if (response != null) {
                        if (response.getReturnStatus().toLowerCase().equals("true")) {
                            SharedPreferences pref = getSharedPreferences(getResources()
                                    .getString(R.string.pref_package_name), MODE_PRIVATE);
                            pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, edEmailBox.getText().toString()).apply();
                            pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                            fetchInitialRecordsForAll();

                            params.putString("vt_exist", "vt_exist");
                            mFirebaseAnalytics.logEvent("vt_exist", params);
                        } else {
                            showToastMessage(GlobalConstants.ENTERED_PASSWORD_WRONG);
                        }
                    }
                } else {

                    Thread.currentThread();
                    Thread.sleep(2000);
                    if (!Utils.isInternetAvailable(LoginEmailActivity.this)) {
                        showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        mLoginPasswordModel.getProgressDialog().dismiss();

                    } else {
                        showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                        mLoginPasswordModel.getProgressDialog().dismiss();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(Tag, "On Post :Exception Occur");
                pDialog.dismiss();
            }
        }
    }

    /**
     * Method used for show the mail chimp dialog box
     * @param mailChimpMessage set the mail chimp message.
     * @param firstName set the first name.
     * @param lastName set the last name.
     * @param emailId set the email id.
     * @param userId set the user id.
     */
    private void showConfirmLoginDialog(String mailChimpMessage, final String firstName, final String lastName,
                                        final String emailId, final long userId) {
        AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(false);
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        TextView message = new TextView(this);
        //message.setGravity(Gravity.CENTER);
        message.setPadding(75, 50, 5, 10);
        message.setTextSize(17);
        message.setText(mailChimpMessage);
        message.setTextColor(ContextCompat.getColor(this,R.color.gray));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(message);
        alertDialogBuilder.setTitle("Join our Mailing List?");
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton("No Thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                            AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(true);
                            loadData(userId, "N", emailId, firstName, lastName);

                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        }

                    }
                });

        alertDialogBuilder.setNegativeButton("Yes! Keep me Updated",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                            AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(true);
                            loadData(userId, "Y", emailId, firstName, lastName);

                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
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
        negativeButton.setTextColor(ContextCompat.getColor(this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }


    /**
     * Method used for load mail chimp data from server
     *
     * @param email     email
     * @param firstName firstName
     * @param lastName  lastName
     */
    private void loadData(long userId, String registerValue, String email, String firstName, String lastName) {

        MailChimpData mailChimpData = new MailChimpData();
        mailChimpData.setIsRegisteredUser(registerValue);
        mailChimpData.setUserID(userId);
        //noinspection deprecation
        pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
        pDialog.setCanceledOnTouchOutside(false);


        if (mLoginPasswordModel != null) {
            mLoginPasswordModel.unRegisterView(this);
            mLoginPasswordModel = null;
        }

        if (mVaultUserDataModel != null) {
            mVaultUserDataModel.unRegisterView(this);
            mVaultUserDataModel = null;
        }

        if (fetchingAllDataModel != null) {
            fetchingAllDataModel.unRegisterView(this);
            fetchingAllDataModel = null;
        }

        if (mMailChimpModelData != null) {
            mMailChimpModelData.unRegisterView(this);
        }

        mMailChimpModelData = AppController.getInstance().getModelFacade().getRemoteModel().
                getMailChimpDataModel();
        mMailChimpModelData.registerView(this);
        mMailChimpModelData.setProgressDialog(pDialog);
        mMailChimpModelData.loadMailChimpData(mailChimpData, email, firstName, lastName);
    }


    /**
     * Method used for load the user data from server.
     */
    private void loadVaultUserData() {
        if (!Utils.isInternetAvailable(LoginEmailActivity.this) && pDialog.isShowing()) {
            pDialog.dismiss();
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);

        } else {
            try {
                pDialog.dismiss();
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                APIResponse response = gson.fromJson(mVaultUserDataModel.getVaultUserResult().trim(), classType);
                SharedPreferences pref = getSharedPreferences(getResources()
                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                String emailId = pref.getString(GlobalConstants.PREF_VAULT_EMAIL, "");

                if (response != null) {
                    if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("vt_exists")
                            || mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("false")) {
                        showAlertDialog("Vault", emailId, "vt_exists");
                    } else if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("fb_exists")) {
                        showAlertDialog("Facebook", emailId, "fb_exists");
                    } else if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("tw_exists")) {
                        showAlertDialog("Twitter", emailId, "tw_exists");
                    } else if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("gm_exists")) {
                        showAlertDialog("Google", emailId, "gm_exists");
                    } else {
                        if (response.getReturnStatus().toLowerCase().equals("true") || response.getReturnStatus().toLowerCase().equals("vt_exists")) {
                            pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, response.getEmailID()).apply();

                            fetchInitialRecordsForAll();

                        } else {
                            Toast.makeText(LoginEmailActivity.this, response.getReturnStatus(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                pDialog.dismiss();
            }

        }
    }

}


