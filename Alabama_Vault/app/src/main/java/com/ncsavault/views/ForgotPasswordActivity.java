package com.ncsavault.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.ncsavault.R;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.LoginEmailModel;
import com.ncsavault.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gauravkumar.singh on 8/16/2016.
 */
public class ForgotPasswordActivity extends BaseActivity implements AbstractView {

    private EditText registeredEmailId, verificationCode, newPasswordEditText, confirmPasswordEditText;
    private TextView tvHeaderText, tvVerificationCode, tvResendCode, tvCancel, tvBack, tvEnterEmail, tvCancelPassword, tvEnterPasswordText;
    private AsyncTask<Void, Void, String> mChangeTask;
    private AsyncTask<Void, Void, String> mChangeConfirmPassTask;
    ProgressDialog pDialog;
    private String verificationCodeValue;
    private LinearLayout childBlockLinearLayout, verificationLinearLayout;
    private TextView tvResetPassword;

    private Button tvSaveTextView;
    private Animation animation;
    private CheckBox chkChangePassword;
    private long userId;
    private Button nextButtonTextView, tvSubmitButton;
    private AlertDialog alertDialog;

    ImageView imageViewNewPassword;
    ImageView imageViewConfirmPassword;


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
        registeredEmailId = (EditText) findViewById(R.id.ed_registered_email_id);
        if (getIntent() != null) {
//            boolean isValue = getIntent().getBooleanExtra("key", false);
            String emailId = getIntent().getStringExtra("email_id");

//            if (isValue) {
            registeredEmailId.setText(emailId);
//                isValue = false;
//            }
        }
        // tvHeaderText = (TextView) findViewById(R.id.tv_header_text);
        nextButtonTextView = (Button) findViewById(R.id.tv_next);

        verificationCode = (EditText) findViewById(R.id.ed_verification_code);
        // tvVerificationCode = (TextView) findViewById(R.id.tv_verification_text);
        tvSubmitButton = (Button) findViewById(R.id.tv_submit);
        verificationLinearLayout = (LinearLayout) findViewById(R.id.ll_password_block);

        childBlockLinearLayout = (LinearLayout) findViewById(R.id.child_block);
        // tvResetPassword = (TextView) findViewById(R.id.tv_reset_password);
        tvSaveTextView = (Button) findViewById(R.id.tv_save);

        newPasswordEditText = (EditText) findViewById(R.id.ed_new_password);
        confirmPasswordEditText = (EditText) findViewById(R.id.ed_confirm_password);

        imageViewNewPassword = (ImageView) findViewById(R.id.imageview_new_password);
        imageViewNewPassword.setTag(R.drawable.eyeon);
        imageViewNewPassword.setOnTouchListener(mPasswordVisibleTouchListener);
        imageViewConfirmPassword = (ImageView) findViewById(R.id.imageview_confirm_password);
        imageViewConfirmPassword.setTag(R.drawable.eyeon);
        imageViewConfirmPassword.setOnTouchListener(mPasswordVisibleTouchListener);

        //GK chkChangePassword = (CheckBox) findViewById(R.id.chk_show_password);
        tvResendCode = (TextView) findViewById(R.id.tv_resend);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvEnterEmail = (TextView) findViewById(R.id.tv_enter_email);
        tvCancelPassword = (TextView) findViewById(R.id.tv_cancel_password);
        tvEnterPasswordText = (TextView) findViewById(R.id.tv_enter_password);
        // tvBack = (TextView) findViewById(R.id.tv_back);

    }

    @Override
    public void initData() {

        registeredEmailId.setFocusableInTouchMode(true);
        registeredEmailId.requestFocus();

//        verificationCode.setFocusableInTouchMode(true);
//        verificationCode.requestFocus();
//
//        newPasswordEditText.setFocusableInTouchMode(true);
//        newPasswordEditText.requestFocus();
    }

    @Override
    public void initListener() {

        nextButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmailAndProceed();

            }
        });

        registeredEmailId.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    checkEmailAndProceed();
                    pDialog.dismiss();
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


//        verificationCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//
//                }
//
//                return false;
//
//
//            }
//        });

//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (keyCode == KeyEvent.KEYCODE_D) {
//
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

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

//        chkChangePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    newPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                } else {
//                    newPasswordEditText.setInputType(129);
//                    confirmPasswordEditText.setInputType(129);
//                }
//
//                newPasswordEditText.setTypeface(Typeface.DEFAULT);
//                confirmPasswordEditText.setTypeface(Typeface.DEFAULT);
//            }
//        });

        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registeredEmailId.getText().toString() != null) {

                    checkEmailAndProceed();

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

//        tvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ForgotPasswordActivity.this, LoginPasswordActivity.class);
//                // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.putExtra("key_is", true);
//                intent.putExtra("status", "vt_exists");
//                intent.putExtra("email", registeredEmailId.getText().toString());
//                startActivity(intent);
//                overridePendingTransition(R.anim.slideup, R.anim.nochange);
//                Utils.getInstance().gethideKeyboard(ForgotPasswordActivity.this);
//                finish();
//            }
//        });

    }


    private View.OnTouchListener mPasswordVisibleTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int cursor = 0;

            switch (v.getId()) {

                case R.id.imageview_new_password:

                    // change input type will reset cursor position, so we want to save it
                    cursor = newPasswordEditText.getSelectionStart();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if (((Integer) imageViewNewPassword.getTag()).intValue() == R.drawable.eyeon) {

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
                            imageViewNewPassword.setImageResource(R.drawable.eyeon);
                            imageViewNewPassword.setTag(R.drawable.eyeon);
                        }

                        newPasswordEditText.setSelection(cursor);

                    }
                    break;
                case R.id.imageview_confirm_password:

                    cursor = confirmPasswordEditText.getSelectionStart();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if (((Integer) imageViewConfirmPassword.getTag()).intValue() == R.drawable.eyeon) {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            confirmPasswordEditText.setTypeface(confirmPasswordEditText.getTypeface(), Typeface.BOLD);

                            imageViewConfirmPassword.setImageResource(R.drawable.eyeoff);
                            imageViewConfirmPassword.setTag(R.drawable.eyeoff);

                        } else {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imageViewConfirmPassword.setImageResource(R.drawable.eyeon);
                            imageViewConfirmPassword.setTag(R.drawable.eyeon);
                        }

                        confirmPasswordEditText.setTypeface(confirmPasswordEditText.getTypeface(), Typeface.BOLD);
                        confirmPasswordEditText.setSelection(cursor);

                        break;
                    }
            }
            return true;
        }
    };

    public void changePasswordCall() {
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
                                            showToastMessage("Please enter registered email id.");
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
                showToastMessage("Please enter registered email id");
                return false;
            } else
                return matcher.matches();
        }
    }

    public void changeConfirmPasswordCall() {
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

                                    showAlertDialog(GlobalConstants.YOUR_PASSWORD_HAS_BEEN_REGISTERED_SUCCESSFULLY);

                                } else {
                                    showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                                }
                            } else {
                                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                            pDialog.dismiss();
                            mChangeConfirmPassTask = null;
                        } catch (Exception e) {

                        }
                    }
                };
                mChangeConfirmPassTask.execute();
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    private Animation leftOutAnimation, leftInAnimation;
    private Animation rightInAnimation, rightOutAnimation;

    private void checkEmailIdAndProceed() {

//        if (/*tvVerificationCode.getVisibility() == View.GONE &&*/ tvSubmitButton.getVisibility() == View.GONE) {
        leftOutAnimation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.rightin);

        // tvHeaderText.setAnimation(leftOutAnimation);
        registeredEmailId.setAnimation(leftOutAnimation);
        nextButtonTextView.setAnimation(leftOutAnimation);
        tvEnterEmail.setAnimation(leftOutAnimation);
        // tvHeaderText.setVisibility(View.GONE);
        registeredEmailId.setVisibility(View.GONE);
        nextButtonTextView.setVisibility(View.GONE);
        tvEnterEmail.setVisibility(View.GONE);
        //  tvBack.setVisibility(View.GONE);

        //tvHeader.setText("Register");

        // tvVerificationCode.setAnimation(rightInAnimation);
        verificationCode.setAnimation(rightInAnimation);
        tvSubmitButton.setAnimation(rightInAnimation);
        tvEnterEmail.setAnimation(rightInAnimation);
        // tvVerificationCode.setVisibility(View.VISIBLE);
        verificationCode.setVisibility(View.VISIBLE);
        tvEnterEmail.setVisibility(View.VISIBLE);
        tvEnterEmail.setText("ENTER THE VERIFICATION CODE EMAILED TO YOU");
        // verificationCode.setFocusableInTouchMode(true);
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

        // tvVerificationCode.setAnimation(leftOutAnimation);
        verificationCode.setAnimation(leftOutAnimation);
        tvSubmitButton.setAnimation(leftOutAnimation);

        // tvVerificationCode.setVisibility(View.GONE);
        verificationCode.setVisibility(View.GONE);
        tvSubmitButton.setVisibility(View.GONE);
        verificationLinearLayout.setVisibility(View.GONE);
        tvResendCode.setVisibility(View.GONE);
        //  tvBack.setVisibility(View.GONE);

        //tvHeader.setText("Register");
        childBlockLinearLayout.setAnimation(rightInAnimation);
        //  tvResetPassword.setAnimation(rightInAnimation);
        tvSaveTextView.setAnimation(rightInAnimation);
        tvEnterPasswordText.setAnimation(rightInAnimation);
        // verificationCode.setAnimation(rightInAnimation);
        // tvSubmitButton.setAnimation(rightInAnimation);
        childBlockLinearLayout.setVisibility(View.VISIBLE);
        newPasswordEditText.requestFocus();
        //  tvResetPassword.setVisibility(View.VISIBLE);
        tvSaveTextView.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.VISIBLE);
        tvEnterPasswordText.setVisibility(View.VISIBLE);
        // verificationCode.setVisibility(View.VISIBLE);
        // tvSubmitButton.setVisibility(View.VISIBLE);


    }

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            if (pass.contains(" ")) {
                showToastMessage("Please enter vaild password");
                return false;
            }
            return true;
        }
        if (pass != null) {
            if (pass.length() == 0) {
//                    edPassword.setError("Password not entered");
                showToastMessage("Password not entered");
            } else if (pass.length() < 6) {
//                    edPassword.setError("Minimum 6 characters required!");
                showToastMessage("Password should contain minimum 6 characters!");
            }
        }
        return false;
    }

    private boolean isConfirmPasswordValid(String confirmPass) {
        return confirmPass != null && (confirmPass.equals(newPasswordEditText.getText().toString()));
    }


    public void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(ForgotPasswordActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void showAlertDialog(String mesg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(mesg);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();

                        AppController.getInstance().getModelFacade().getLocalModel().setRegisteredEmailIdForgot(true);
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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }


    public void showAlert(final long userId, final String VerficationCode) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("A verification code has been sent to your registered email id.");
        alertDialogBuilder.setTitle("Confirmation");
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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));

    }
    private LoginEmailModel loginEmailModel;
    public void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {
                Utils.getInstance().gethideKeyboard(this);

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
        System.out.println("login screen");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                        pDialog.dismiss();
                        loginEmailModel.unRegisterView(ForgotPasswordActivity.this);
                        if (Utils.isInternetAvailable(ForgotPasswordActivity.this)) {
                            if (loginEmailModel != null) {
                                if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")
                                        || loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")
                                        || loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {
                                    showToastMessage(GlobalConstants.ERROR_MESG);
                                    pDialog.dismiss();
                                }else
                                {
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

}