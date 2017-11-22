package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import applicationId.R;

import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.FetchingAllDataModel;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;

import java.lang.reflect.Type;

/**
 * Class used for  the verify the user email id on server.
 */

public class VerificationEmailActivity extends BaseActivity implements AbstractView {

    private final String tag = "VerificationEmail";
    private EditText registeredEmailId, verificationCode;
    private TextView tvResendCode, tvCancel;
    private AsyncTask<Void, Void, String> mChangeTask;
    @SuppressWarnings("deprecation")
    private
    ProgressDialog pDialog;
    private String verificationCodeValue;
    private Animation animation;
    private long userId;
    private Button tvSubmitButton;
    private AlertDialog alertDialog;
    private User vaultUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_email);

        vaultUser = AppController.getInstance().getModelFacade().getLocalModel().getUser();
        String mRegisteredEmailId = vaultUser.getEmailID();
        initViews();
        initData();
        initListener();

        verificationEmailCall(mRegisteredEmailId);
    }


    @Override
    public void initViews() {
        Utils.getInstance().setAppName(this);
        registeredEmailId = findViewById(R.id.ed_registered_email_id);
        if (getIntent() != null) {
            String emailId = getIntent().getStringExtra("email_id");
            registeredEmailId.setText(emailId);
        }

        verificationCode = findViewById(R.id.ed_verification_code);
        tvSubmitButton = findViewById(R.id.tv_submit);
        tvResendCode = findViewById(R.id.tv_resend);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSubmitButton.setVisibility(View.VISIBLE);
        tvResendCode.setVisibility(View.VISIBLE);

    }

    @Override
    public void initData() {
        registeredEmailId.setFocusableInTouchMode(true);
        registeredEmailId.requestFocus();
    }

    @Override
    public void initListener() {


        tvSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationCode.getText().toString().equals("")) {
                    showToastMessage("Please enter verification code.");
                } else if (verificationCode.getText().toString().equals(verificationCodeValue)) {
                    overrideUserData(vaultUser);
                } else {
                    showToastMessage("Entered code is either invalid or expired.");
                }

            }
        });

        verificationCode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (verificationCode.getText().toString().equals(verificationCodeValue)) {
                        overrideUserData(vaultUser);
                    } else if (verificationCode.getText().toString().equals("")) {
                        showToastMessage("Please enter verification code");
                    } else {
                        showToastMessage("Entered code is either invalid or expired.");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registeredEmailId.getText().toString() != null) {
                    showAlert(registeredEmailId.getText().toString());
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerificationEmailActivity.this, LoginEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("key", true);
                intent.putExtra("email_id", registeredEmailId.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slidedown, R.anim.nochange);
                finish();
            }
        });

    }

    /**
     * Async class used for check the email is valid on server or not.
     */
    private AsyncTask<Void, Void, String> mLoginTask;

    private void overrideUserData(final User vaultUser) {
        mLoginTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //noinspection deprecation
                pDialog = new ProgressDialog(VerificationEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(VerificationEmailActivity.this));
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
                    Log.d(tag, "Result of post user data : " + result);
                    if (result.contains("true") || result.contains("success")) {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(result.trim(), classType);
                        pDialog.dismiss();
                        SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                        pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, vaultUser.getUsername()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, vaultUser.getEmailID()).apply();
                        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                        fetchInitialRecordsForAll();

                    } else {
                        try {
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response.getReturnStatus() != null) {
                                if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Vault Account");
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Gmail");
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Twitter");
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Facebook");
                                }
                            } else {
                                pDialog.dismiss();
                                LoginManager.getInstance().logOut();
                                showToastMessage(getResources().getString(R.string.connect_to_server));
                            }

                            mLoginTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mLoginTask = null;
                            showToastMessage(getResources().getString(R.string.unable_to_process));
                        }
                    }

                }
            }
        };
        mLoginTask.execute();
    }

    /**
     * Method used for when user forgot password then send email on verified email.
     * @param registeredEmail email address
     */
    private void verificationEmailCall(final String registeredEmail) {
        if (Utils.isInternetAvailable(this)) {
            if (mChangeTask == null) {
                mChangeTask = new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //noinspection deprecation
                        pDialog = new ProgressDialog(VerificationEmailActivity.this, R.style.CustomDialogTheme);
                        pDialog.show();
                        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(VerificationEmailActivity.this));
                        pDialog.setCanceledOnTouchOutside(false);
                        pDialog.setCancelable(false);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String result = "";
                        try {
                            result = AppController.getInstance().getServiceManager().getVaultService().forgotPassword(registeredEmail, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        try {
                            if (Utils.isInternetAvailable(VerificationEmailActivity.this)) {
                                if (result != null) {
                                    Gson gson = new Gson();
                                    Type classType = new TypeToken<APIResponse>() {
                                    }.getType();
                                    APIResponse response = gson.fromJson(result.trim(), classType);
                                    userId = response.getUserID();
                                    SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                                    pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, userId).apply();
                                    if (userId > 0) {
                                        verificationCodeValue = response.getVerficationCode();
                                    }
                                } else {
                                    showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                                }
                            } else {
                                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                            mChangeTask = null;
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mChangeTask = null;
                            pDialog.dismiss();
                        }
                    }
                };
                mChangeTask.execute();
            }

        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    /**
     * Method used for show to toast message if user getting any error
     * @param message set the message
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
                animation = AnimationUtils.loadAnimation(VerificationEmailActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method used for show the alert dialog box to check the user login with
     * Google,Facebook and Twitter
     * @param msg set the message.
     */
    private void showAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(msg);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();

                        AppController.getInstance().getModelFacade().getLocalModel().setRegisteredEmailIdForgot();
                        AppController.getInstance().getModelFacade().getLocalModel().setRegisteredEmailIdForgot(registeredEmailId.getText().toString());
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();


                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(VerificationEmailActivity.this, R.color.app_theme_color));
    }


    /**
     * Method used for show the alert box to the send verification code in your email id.
     * @param emailId registered email id.
     */
    private void showAlert(final String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Verification code has been sent to be on " + emailId + " .");
        String title = "Confirmation";
        SpannableStringBuilder sb = new SpannableStringBuilder(title);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.app_theme_color)), 0, title.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        alertDialogBuilder.setTitle(sb);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        verificationEmailCall(emailId);
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(VerificationEmailActivity.this, R.color.app_theme_color));
    }

    /**
     * Method used for fetch the all data from server.
     */
    private void fetchInitialRecordsForAll() {


        //noinspection deprecation
        pDialog = new ProgressDialog(VerificationEmailActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(VerificationEmailActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        if (mFetchingAllDataModel != null) {
            mFetchingAllDataModel.unRegisterView(this);
        }
        mFetchingAllDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getFetchingAllDataModel();
        mFetchingAllDataModel.registerView(this);
        mFetchingAllDataModel.setProgressDialog(pDialog);
        mFetchingAllDataModel.fetchData();

    }

    private FetchingAllDataModel mFetchingAllDataModel;

    @Override
    public void update() {

        Log.d("upload photo","Uploaded photo update");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("result","Uploaded photo update 123");
                    if (mFetchingAllDataModel != null && mFetchingAllDataModel.getState() ==
                            BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {

                        pDialog.dismiss();
                        showAlertDialogForSuccess();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Method used for show the alert box for successfully registered in app.
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
        positiveButton.setTextColor(ContextCompat.getColor(VerificationEmailActivity.this, R.color.app_theme_color));
    }

    /**
     * Method used for get the all response from server.
     */
    private void getFetchDataResponse() {
        try {
            mFetchingAllDataModel.unRegisterView(VerificationEmailActivity.this);
            if (Utils.isInternetAvailable(VerificationEmailActivity.this)) {
                if (mFetchingAllDataModel.getABoolean()) {
                    Profile fbProfile = Profile.getCurrentProfile();
                    SharedPreferences pref = AppController.getInstance()
                            .getApplicationContext().getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                    if (fbProfile != null || userId > 0) {
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();
                        startService(new Intent(VerificationEmailActivity.this, TrendingFeaturedVideoService.class));

                    }
                }

            } else {
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            stopService(new Intent(VerificationEmailActivity.this, TrendingFeaturedVideoService.class));
            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
            pDialog.dismiss();
        }
    }

}
