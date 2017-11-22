package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import applicationId.R;

import com.facebook.login.LoginManager;
import com.ncsavault.controllers.AppController;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.LoginEmailModel;
import com.ncsavault.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ForgotPasswordActivity is used to rest password if user forgot password
 */
public class ForgotPasswordActivity extends BaseActivity implements AbstractView {

    private final String Tag = "ForgotPasswordActivity";
    private EditText registeredEmailId, verificationCode, newPasswordEditText, confirmPasswordEditText;
    private TextView tvResendCode, tvCancel, tvEnterEmail, tvCancelPassword, tvEnterPasswordText;
    private AsyncTask<Void, Void, String> mChangeTask;
    private AsyncTask<Void, Void, String> mChangeConfirmPassTask;
    @SuppressWarnings("deprecation")
    private ProgressDialog pDialog;
    private String verificationCodeValue;
    private LinearLayout childBlockLinearLayout, verificationLinearLayout;

    private Button tvSaveTextView;
    private Animation animation;
    private long userId;
    private Button nextButtonTextView, tvSubmitButton;
    private AlertDialog alertDialog;

    private ImageView imageViewNewPassword;
    private ImageView imageViewConfirmPassword;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password);

        initViews();
        initData();
        initListener();
    }

    @Override
    public void initViews() {
        Utils.getInstance().setAppName(this);
        registeredEmailId = findViewById(R.id.ed_registered_email_id);
        if (getIntent() != null) {
            String emailId = getIntent().getStringExtra("email_id");
            registeredEmailId.setText(emailId);
        }

        nextButtonTextView = findViewById(R.id.tv_next);
        verificationCode = findViewById(R.id.ed_verification_code);
        tvSubmitButton = findViewById(R.id.tv_submit);
        verificationLinearLayout = findViewById(R.id.ll_password_block);
        childBlockLinearLayout = findViewById(R.id.child_block);
        tvSaveTextView = findViewById(R.id.tv_save);

        newPasswordEditText = findViewById(R.id.ed_new_password);
        confirmPasswordEditText = findViewById(R.id.ed_confirm_password);

        imageViewNewPassword = findViewById(R.id.image_view_new_password);
        imageViewNewPassword.setTag(R.drawable.eye_on);
        imageViewNewPassword.setOnTouchListener(mPasswordVisibleTouchListener);
        imageViewConfirmPassword = findViewById(R.id.image_view_confirm_password);
        imageViewConfirmPassword.setTag(R.drawable.eye_on);
        imageViewConfirmPassword.setOnTouchListener(mPasswordVisibleTouchListener);

        tvResendCode = findViewById(R.id.tv_resend);
        tvCancel = findViewById(R.id.tv_cancel);
        tvEnterEmail = findViewById(R.id.tv_enter_email);
        tvCancelPassword = findViewById(R.id.tv_cancel_password);
        tvEnterPasswordText = findViewById(R.id.tv_enter_password);


    }

    @Override
    public void initData() {

        registeredEmailId.setFocusableInTouchMode(true);
        registeredEmailId.requestFocus();

    }

    @Override
    public void initListener() {

        nextButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!registeredEmailId.getText().toString().isEmpty())
                {
                    checkEmailAndProceed();
                }else
                {
                    showToastMessage(getResources().getString(R.string.enter_registered_email));
                }

            }
        });

        registeredEmailId.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(!registeredEmailId.getText().toString().isEmpty())
                    {
                        checkEmailAndProceed();
                        pDialog.dismiss();
                    }else
                    {
                        showToastMessage(getResources().getString(R.string.enter_registered_email));
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });


        verificationCode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (verificationCode.getText().toString().equals(verificationCodeValue)) {
                        checkConfirmPasswordAndProceed();
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

        tvSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationCode.getText().toString().equals("")) {
                    showToastMessage("Please enter verification code.");
                } else if (verificationCode.getText().toString().equals(verificationCodeValue)) {
                    checkConfirmPasswordAndProceed();
                } else {
                    showToastMessage("Entered code is either invalid or expired.");
                }

            }
        });


        tvSaveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPassword(newPasswordEditText.getText().toString())) {
                    if (isConfirmPasswordValid(confirmPasswordEditText.getText().toString())) {
                        changeConfirmPasswordCall();
                    } else {
                        showToastMessage("Password doesn't match");
                    }
                }
            }
        });


        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registeredEmailId.getText().toString() != null) {

                    if(!registeredEmailId.getText().toString().isEmpty())
                    {
                        checkEmailAndProceed();
                    }else
                    {
                        showToastMessage(getResources().getString(R.string.enter_registered_email));
                    }

                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("key", true);
                intent.putExtra("email_id", registeredEmailId.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slidedown, R.anim.nochange);
                finish();
            }
        });

        tvCancelPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("key", true);
                intent.putExtra("email_id", registeredEmailId.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slidedown, R.anim.nochange);
                finish();
            }
        });

    }


    private final View.OnTouchListener mPasswordVisibleTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int cursor;

            switch (v.getId()) {

                case R.id.image_view_new_password:

                    // change input type will reset cursor position, so we want to save it
                    cursor = newPasswordEditText.getSelectionStart();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if ((Integer) imageViewNewPassword.getTag() == R.drawable.eye_on) {

                            newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            newPasswordEditText.setTypeface(newPasswordEditText.getTypeface(), Typeface.BOLD);
                            // Do stg
                            imageViewNewPassword.setImageResource(R.drawable.eyeoff);
                            imageViewNewPassword.setTag(R.drawable.eyeoff);
                        } else {
                            newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            newPasswordEditText.setTypeface(newPasswordEditText.getTypeface(), Typeface.BOLD);
                            imageViewNewPassword.setImageResource(R.drawable.eye_on);
                            imageViewNewPassword.setTag(R.drawable.eye_on);
                        }

                        newPasswordEditText.setSelection(cursor);

                    }
                    break;
                case R.id.image_view_confirm_password:

                    cursor = confirmPasswordEditText.getSelectionStart();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if ((Integer) imageViewConfirmPassword.getTag() == R.drawable.eye_on) {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            confirmPasswordEditText.setTypeface(confirmPasswordEditText.getTypeface(), Typeface.BOLD);

                            imageViewConfirmPassword.setImageResource(R.drawable.eyeoff);
                            imageViewConfirmPassword.setTag(R.drawable.eyeoff);

                        } else {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imageViewConfirmPassword.setImageResource(R.drawable.eye_on);
                            imageViewConfirmPassword.setTag(R.drawable.eye_on);
                        }

                        confirmPasswordEditText.setTypeface(confirmPasswordEditText.getTypeface(), Typeface.BOLD);
                        confirmPasswordEditText.setSelection(cursor);

                        break;
                    }
            }
            return true;
        }
    };

    private void changePasswordCall() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidEmail(registeredEmailId.getText().toString())) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                if (mChangeTask == null) {
                    mChangeTask = new AsyncTask<Void, Void, String>() {

                        String registeredEmail = "";

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //noinspection deprecation
                            pDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.CustomDialogTheme);
                            pDialog.show();
                            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(ForgotPasswordActivity.this));
                            pDialog.setCanceledOnTouchOutside(false);
                            pDialog.setCancelable(false);
                            registeredEmail = registeredEmailId.getText().toString();
                        }


                        @Override
                        protected String doInBackground(Void... params) {
                            String result = "";
                            try {
                                result = AppController.getInstance().getServiceManager().getVaultService().forgotPassword(registeredEmail, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return result;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            super.onPostExecute(result);

                            try {
                                if (Utils.isInternetAvailable(ForgotPasswordActivity.this)) {
                                    if (result != null) {
                                        Gson gson = new Gson();
                                        Type classType = new TypeToken<APIResponse>() {
                                        }.getType();
                                        APIResponse response = gson.fromJson(result.trim(), classType);
                                        userId = response.getUserID();
                                        if (userId > 0) {
                                            showAlert(userId, response.getVerficationCode());
                                        } else {
                                            Log.d(Tag,"Please enter registered email id");
                                            showToastMessage(getResources().getString(R.string.enter_registered_email));
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
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    /**
     * Method is used for email validation
     * @param email set the email address to check the validation
     * @return the value true and false.
     */
    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            showToastMessage("Please enter registered email id");
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                showToastMessage(getResources().getString(R.string.enter_registered_email));
                return false;
            } else
                return matcher.matches();
        }
    }

    private void changeConfirmPasswordCall() {
        if (Utils.isInternetAvailable(this)) {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (mChangeConfirmPassTask == null) {
                mChangeConfirmPassTask = new AsyncTask<Void, Void, String>() {

                    String newPassword = "";

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //noinspection deprecation
                        pDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.CustomDialogTheme);
                        pDialog.show();
                        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(ForgotPasswordActivity.this));
                        pDialog.setCanceledOnTouchOutside(false);
                        pDialog.setCancelable(false);
                        newPassword = newPasswordEditText.getText().toString();
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String result = "";
                        try {
                            result = AppController.getInstance().getServiceManager().getVaultService().confirmPassword(userId, newPassword);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        try {
                            if (Utils.isInternetAvailable(ForgotPasswordActivity.this)) {
                                if (result != null) {

                                    showAlertDialog();

                                } else {
                                    showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                                }
                            } else {
                                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                            pDialog.dismiss();
                            mChangeConfirmPassTask = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mChangeConfirmPassTask.execute();
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    private Animation leftOutAnimation;
    private Animation rightInAnimation;

    private void checkEmailIdAndProceed() {


        leftOutAnimation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.rightin);

        registeredEmailId.setAnimation(leftOutAnimation);
        nextButtonTextView.setAnimation(leftOutAnimation);
        tvEnterEmail.setAnimation(leftOutAnimation);
        registeredEmailId.setVisibility(View.GONE);
        nextButtonTextView.setVisibility(View.GONE);
        tvEnterEmail.setVisibility(View.GONE);
        verificationCode.setAnimation(rightInAnimation);
        tvSubmitButton.setAnimation(rightInAnimation);
        tvEnterEmail.setAnimation(rightInAnimation);
        verificationCode.setVisibility(View.VISIBLE);
        tvEnterEmail.setVisibility(View.VISIBLE);
        tvEnterEmail.setText(getResources().getString(R.string.verification_code));
        verificationCode.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        tvSubmitButton.setVisibility(View.VISIBLE);
        tvResendCode.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.VISIBLE);
//        }

    }


    private void checkConfirmPasswordAndProceed() {


        leftOutAnimation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.rightin);
        verificationCode.setAnimation(leftOutAnimation);
        tvSubmitButton.setAnimation(leftOutAnimation);
        verificationCode.setVisibility(View.GONE);
        tvSubmitButton.setVisibility(View.GONE);
        verificationLinearLayout.setVisibility(View.GONE);
        tvResendCode.setVisibility(View.GONE);
        childBlockLinearLayout.setAnimation(rightInAnimation);
        tvSaveTextView.setAnimation(rightInAnimation);
        tvEnterPasswordText.setAnimation(rightInAnimation);
        childBlockLinearLayout.setVisibility(View.VISIBLE);
        newPasswordEditText.requestFocus();
        tvSaveTextView.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.VISIBLE);
        tvEnterPasswordText.setVisibility(View.VISIBLE);

    }

    /**
     * Method is used to check password validation
     * @param pass set the value of password
     * @return the value true and false.
     */
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            if (pass.contains(" ")) {
                showToastMessage(getResources().getString(R.string.valid_password));
                return false;
            }
            return true;
        }
        if (pass != null) {
            if (pass.length() == 0) {
                showToastMessage(getResources().getString(R.string.password_minimum_char));
            } else if (pass.length() < 6) {
                showToastMessage(getResources().getString(R.string.password_minimum_char));
            }
        }
        return false;
    }

    /**
     *  Method is used to check confirm password validation
     * @param confirmPass set the value of confirm password
     * @return the value of true and false.
     */
    private boolean isConfirmPasswordValid(String confirmPass) {
        return confirmPass != null && (confirmPass.equals(newPasswordEditText.getText().toString()));
    }

    /**
     * Method is used to show toast message
     * @param message set the message of Toast.
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
                animation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method is used to show password reset alert dialog
     */
    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(GlobalConstants.YOUR_PASSWORD_HAS_BEEN_REGISTERED_SUCCESSFULLY);

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
        //noinspection deprecation
        positiveButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        //noinspection deprecation
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method is used to show verification code sent alert
     * @param userId set the value of used id
     * @param VerficationCode set the value of VerficationCode
     */
    private void showAlert(final long userId, final String VerficationCode) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("A verification code has been sent to your registered email id.");
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
                        alertDialog.dismiss();
                        if (userId > 0) {
                            verificationCodeValue = VerficationCode;
                            checkEmailIdAndProceed();
                        } else {
                            showToastMessage("Please enter registered email id.");
                        }
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        //noinspection deprecation
        positiveButton.setTextColor(getResources().getColor(R.color.app_theme_color));

    }

    private LoginEmailModel loginEmailModel;

    private void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {
            Utils.getInstance().getHideKeyboard(this);

            //noinspection deprecation
            pDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(ForgotPasswordActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
                loginEmailModel = null;
            }

            loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
            loginEmailModel.registerView(this);
            loginEmailModel.setProgressDialog(pDialog);
            loginEmailModel.loadLoginData(registeredEmailId.getText().toString());
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
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
                        loginEmailModel.unRegisterView(ForgotPasswordActivity.this);
                        if (Utils.isInternetAvailable(ForgotPasswordActivity.this)) {
                            if (loginEmailModel != null) {

                                if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists"))
                                {
                                    showAlertDialog("Facebook", registeredEmailId.getText().toString());
                                    pDialog.dismiss();
                                }else if(loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists"))
                                {
                                    showAlertDialog("Twitter", registeredEmailId.getText().toString());
                                    pDialog.dismiss();
                                }else if(loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists"))
                                {
                                    showAlertDialog("Google", registeredEmailId.getText().toString());
                                    pDialog.dismiss();
                                } else {
                                    changePasswordCall();
                                }

                            }
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method used for show the alert dialog for override scenario.
     * @param loginType set the login type Like: Google,Facebook and twitter.
     * @param emailId set the email address.
     */
    private void showAlertDialog(String loginType, final String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("This email id is currently registered with " + loginType +
                        ", if you want to reset password please override with new login method.");
        alertDialogBuilder.setPositiveButton("Register",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        AppController.getInstance().getModelFacade().getLocalModel().setEmailId(emailId);
                        Intent intent = new Intent(ForgotPasswordActivity.this,UploadPhotoActivity.class);
                        startActivity(intent);
                        finish();
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
        positiveButton.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setAllCaps(false);
        negativeButton.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this,R.color.app_theme_color));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.getInstance().getHideKeyboard(this);
    }
}
