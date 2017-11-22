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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.models.LoginEmailModel;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.reginald.editspinner.EditSpinner;

import applicationId.R;

import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.FetchingAllDataModel;
import com.ncsavault.models.MailChimpDataModel;
import com.ncsavault.models.UserDataModel;
import com.ncsavault.utils.Utils;
import com.ncsavault.wheeladapters.NumericWheelAdapter;
import com.ncsavault.wheelwidget.OnWheelChangedListener;
import com.ncsavault.wheelwidget.OnWheelScrollListener;
import com.ncsavault.wheelwidget.WheelView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UploadPhotoActivity is used for new user registration
 */
public class UploadPhotoActivity extends PermissionActivity implements AbstractView {

    private boolean isImageProvided = false;
    private Uri selectedImageUri = null;
    private Bitmap selectedBitmap = null;
    private Uri outputFileUri;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    private File sdImageMainDirectory;
    private User vaultUser = null;
    private int screenWidth = 0;
    @SuppressWarnings("deprecation")
    private ProgressDialog pDialog;
    private Animation animation;
    private String fName;
    private String lName;
    private String emailId;
    private boolean isBackToSplashScreen = false;
    private boolean askAgainForMustPermissions = false;
    private boolean goToSettingsScreen = false;
    private UserDataModel mVaultUserDataModel;
    private FetchingAllDataModel mFetchingAllDataModel;
    private MailChimpDataModel mMailChimpModelData;
    private EditSpinner mEditSpinner;
    private ImageView mProfileImage;
    private EditText mUserName;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailId;
    private EditText mGender;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mYOB;
    private Button mRegistrationButton, mSignUpButton;
    private WheelView yearWheel;
    private View view;
    private TextView tvUploadPhoto;
    private TextView tvAlreadyRegistered;
    private TextView tvSignUpWithoutProfile;
    private boolean wheelScrolled = false;
    private LoginEmailModel loginEmailModel;
    private String email = "";
    private ImageView imageViewPasswordVisibility;
    private ImageView imageViewConfirmPasswordVisibility;
    private int height;
    private final CharSequence[] alertListItems = {Html.fromHtml("<b>Take from camera</b>"),
            Html.fromHtml("<b>Select from gallery<b>")};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen_layout);
        initialiseAllData();

    }

    /**
     * Method to initialize all data of activity
     */
    private void initialiseAllData() {
        initViews();
        initData();
        initListener();
        setGenderAdapter();
        height = Utils.getScreenHeight(this);
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


    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null)
            pDialog.dismiss();
    }

    /**
     * Method is used to set adapter to gender edit spinner
     */
    private void setGenderAdapter() {
        @SuppressWarnings("unchecked") ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.gender_selection));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditSpinner.setAdapter(adapter);
        Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);
    }


    /**
     * Method is used to initialize views
     */
    @SuppressLint("CutPasteId")
    private void initViews() {

        Utils.getInstance().setAppName(this);
        mEditSpinner = findViewById(R.id.edit_spinner);

        mUserName = findViewById(R.id.username);
        mFirstName = findViewById(R.id.fname);
        mLastName = findViewById(R.id.lname);
        mEmailId = findViewById(R.id.Email);
        mGender = findViewById(R.id.edit_spinner);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_pass);
        imageViewPasswordVisibility = findViewById(R.id.image_view_password);
        imageViewPasswordVisibility.setTag(R.drawable.eye_on);
        imageViewPasswordVisibility.setOnTouchListener(mPasswordVisibleTouchListener);
        imageViewConfirmPasswordVisibility = findViewById(R.id.image_view_confirm_password);
        imageViewConfirmPasswordVisibility.setTag(R.drawable.eye_on);
        imageViewConfirmPasswordVisibility.setOnTouchListener(mPasswordVisibleTouchListener);
        mRegistrationButton = findViewById(R.id.btn_sign_up);
        yearWheel = findViewById(R.id.year_wheel);
        initWheel();
        yearWheel.setBackgroundColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.app_dark_grey));

        mYOB = findViewById(R.id.yob);
        view = findViewById(R.id.llToast);

        if (AppController.getInstance().getModelFacade().getLocalModel().getEmailId() != null) {
            mEmailId.setText(AppController.getInstance().getModelFacade().getLocalModel().getEmailId());
        }


        mFirstName.setOnFocusChangeListener(onFocusChangeListener);
        mLastName.setOnFocusChangeListener(onFocusChangeListener);
        mYOB.setOnFocusChangeListener(onFocusChangeListener);
        mEmailId.setOnFocusChangeListener(onFocusChangeListener);
        mUserName.setOnFocusChangeListener(onFocusChangeListener);
        mPassword.setOnFocusChangeListener(onFocusChangeListener);
        mConfirmPassword.setOnFocusChangeListener(onFocusChangeListener);

        mProfileImage = findViewById(R.id.imgUserProfile);
        mSignUpButton = findViewById(R.id.tv_sign_up_button);
        tvUploadPhoto = findViewById(R.id.tv_upload_photo);


        tvAlreadyRegistered = findViewById(R.id.tv_already_registered);
        tvSignUpWithoutProfile = findViewById(R.id.tv_sing_up_with_put);
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
     * Method is used for password validation
     * @param pass set the password value.
     * @return boolean the value true and false.
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
                showToastMessage(getResources().getString(R.string.password_not_entered));
            } else if (pass.length() < 6) {
                showToastMessage(getResources().getString(R.string.enter_more_than_6_character));
            }
        }
        return false;
    }


    /**
     * Method is used for confirm password validation
     * @param confirmPass set the confirm password.
     * @return the value true and false.
     */
    private boolean isConfirmPasswordValid(String confirmPass) {
        return confirmPass != null && (confirmPass.equals(mPassword.getText().toString()));
    }

    private String[] yearArray;

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

    /**
     * Method is used to check email validation
     * @param email set the email address
     * @return boolean the value true and false
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
     * Method is used to initialize data
     */
    @SuppressWarnings("deprecation")
    private void initData() {

        try {

            mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
            screenWidth = Utils.getScreenDimensions(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 3, screenWidth / 3);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            mProfileImage.setLayoutParams(lp);
            @SuppressWarnings("unchecked") HashMap<String, String> stringMap = (HashMap<String, String>) getIntent().getSerializableExtra("eventObject");
            if (stringMap != null) {
                emailId = stringMap.get("email_id");
                mEmailId.setText(emailId);
                getAllRegistrationDetail();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isDeleteKey = true;

    /**
     * Method is used to initialize listeners
     */
    private void initListener() {

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.slidedown);
                    yearWheel.setAnimation(anim);
                    yearWheel.setVisibility(View.GONE);
                }

                if (Utils.isInternetAvailable(getApplicationContext()))
                    try {
                        //Marshmallow permissions for write external storage.
                        if (haveAllMustPermissions(writeExternalStorage)) {
                            openImageIntent();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                else
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
        });

        mYOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYearWheel();
                mYOB.requestFocus();
                Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);
            }
        });


        yearWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYearWheelVisibility();
                Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);
            }
        });

        mYOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openYearWheel();
                mYOB.requestFocus();
                Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);

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

        mFirstName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    isDeleteKey = true;
                }
                return false;
            }
        });

        mLastName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    isDeleteKey = true;
                }
                return false;
            }
        });


        mFirstName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mFirstName.requestFocus();
                if (mLastName.getText().toString().length() > 0) {
                    String firstName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mLastName.getText().toString().trim().substring(1);
                    firstName = firstName.replace(" ", "");
                    mLastName.setText(firstName);
                }


                return false;
            }
        });


        mFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mFirstName.getText().toString().length() == 0) {
                        showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);
                        //return true ;

                    }
//                    else if (mFirstName.getText().toString().length() < 3) {
//                        showToastMessage(GlobalConstants.FIRST_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
//                    }


                    if (mFirstName.getText().toString().length() > 0) {
                        String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                mFirstName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mFirstName.setText(firstName);
                    }

                    //return true;
                }
                return false;
            }
        });

        mFirstName.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {


                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }

                        if (!isDeleteKey) {
                            showToastMessage(GlobalConstants.ENTER_ONLY_ALPHABETS);
                        } else {
                            isDeleteKey = false;
                        }

                        return "";

                    }
                }
        });


        mLastName.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {

                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }

                        if (!isDeleteKey) {
                            showToastMessage(GlobalConstants.ENTER_ONLY_ALPHABETS);
                        } else {
                            isDeleteKey = false;
                        }

                        return "";

                    }
                }
        });

        mLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (mLastName.getText().toString().length() == 0) {
                        showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);


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
                        showToastMessage(GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY);

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
                        showToastMessage(GlobalConstants.USER_NAME_CAN_NOT_EMPTY);

                    } else if (mUserName.getText().toString().length() < 3) {
                        showToastMessage(GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                    }

                    if (mUserName.getText().toString().length() > 0) {
                        String firstName = mUserName.getText().toString().trim();
                        firstName = firstName.replace(" ", "");
                        mUserName.setText(firstName);
                    }

                }
                return false;
            }
        });


        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (mPassword.getText().toString().contains(" ")) {
                        String password = mPassword.getText().toString().replace(" ", "");
                        mPassword.setText(password);
                    }
                    Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);


                }
                return false;
            }
        });

        mConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (mConfirmPassword.getText().toString().contains(" ")) {
                        String password = mConfirmPassword.getText().toString().replace(" ", "");
                        mConfirmPassword.setText(password);
                    }
                    Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);
                    if (isValidPassword(mConfirmPassword.getText().toString())) {
                        Log.d("upload", "upload" + isValidPassword(mConfirmPassword.getText().toString()));
                    }
                    if (isConfirmPasswordValid(mConfirmPassword.getText().toString())) {
                        Log.d("upload", "upload" + isConfirmPasswordValid(mConfirmPassword.getText().toString()));
                    } else {
                        showToastMessage(GlobalConstants.PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH);
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

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mPassword.requestFocus();
                if (mConfirmPassword.getText().toString().contains(" ")) {
                    String password = mConfirmPassword.getText().toString().replace(" ", "");
                    mConfirmPassword.setText(password);
                }
                return false;
            }
        });


        mConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mConfirmPassword.requestFocus();
                if (mPassword.getText().toString().contains(" ")) {
                    String password = mPassword.getText().toString().replace(" ", "");
                    mPassword.setText(password);
                }
                return false;
            }
        });

        mGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                Utils.hideSoftKeyboard(UploadPhotoActivity.this);
                mEditSpinner.showDropDown();
                return false;
            }
        });


        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setValidationOfRegistrationScreen();

            }

        });


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmailAndProceed();

            }
        });

        tvAlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                storeAllRegistrationDetail();
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
            }
        });
    }


    private final View.OnTouchListener mPasswordVisibleTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int cursor;

            switch (v.getId()) {

                case R.id.image_view_password:

                    // change input type will reset cursor position, so we want to save it
                    cursor = mPassword.getSelectionStart();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if ((Integer) imageViewPasswordVisibility.getTag() == R.drawable.eye_on) {

                            mPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            // Do stg
                            imageViewPasswordVisibility.setImageResource(R.drawable.eyeoff);
                            imageViewPasswordVisibility.setTag(R.drawable.eyeoff);
                        } else {
                            mPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imageViewPasswordVisibility.setImageResource(R.drawable.eye_on);
                            imageViewPasswordVisibility.setTag(R.drawable.eye_on);
                        }

                        mPassword.setSelection(cursor);

                    }
                    break;
                case R.id.image_view_confirm_password:

                    cursor = mConfirmPassword.getSelectionStart();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if ((Integer) imageViewConfirmPasswordVisibility.getTag() == R.drawable.eye_on) {
                            mConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            imageViewConfirmPasswordVisibility.setImageResource(R.drawable.eyeoff);
                            imageViewConfirmPasswordVisibility.setTag(R.drawable.eyeoff);

                        } else {
                            mConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imageViewConfirmPasswordVisibility.setImageResource(R.drawable.eye_on);
                            imageViewConfirmPasswordVisibility.setTag(R.drawable.eye_on);
                        }


                        mConfirmPassword.setSelection(cursor);

                        break;
                    }
            }
            return true;
        }
    };

    /**
     * Method is used to set birth year wheel view
     */
    private void openYearWheel() {

        mRegistrationButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.30));
        lp.setMargins(10, 10, 10, 0);
        lp.gravity = Gravity.BOTTOM;
        yearWheel.setLayoutParams(lp);
        Animation anim = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.slideup);
        yearWheel.setAnimation(anim);
        yearWheel.setVisibility(View.VISIBLE);
        Utils.getInstance().getHideKeyboard(UploadPhotoActivity.this);
    }

    /**
     * Method is used to check visibility of birth year wheel view
     */
    private void checkYearWheelVisibility() {
        if (yearWheel.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.slidedown);
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
     * Method to check validation of user registration data
     */
    private void setValidationOfRegistrationScreen() {
        if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
            storeAllRegistrationDetail();

            checkYearWheelVisibility();
            if (mPassword.getText().toString().contains(" ")) {
                String password = mPassword.getText().toString().replace(" ", "");
                mPassword.setText(password);
            }
            if (mConfirmPassword.getText().toString().contains(" ")) {
                String password = mConfirmPassword.getText().toString().replace(" ", "");
                mConfirmPassword.setText(password);
            }

            if (mFirstName.getText().toString().length() == 0) {
                showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);
                return;

            } else {
                fName = mFirstName.getText().toString().trim();
            }

            if (mLastName.getText().toString().length() == 0) {
                showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);
                return;

            } else {
                lName = mLastName.getText().toString().trim();
            }


            if (mEmailId.getText().length() == 0) {
                showToastMessage(GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY);
                return;

            } else {
                emailId = mEmailId.getText().toString().trim();
            }

            if (mUserName.getText().toString().length() == 0) {
                showToastMessage(GlobalConstants.USER_NAME_CAN_NOT_EMPTY);
                return;

            } else if (mUserName.getText().toString().length() < 3) {
                showToastMessage(GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                return;
            }


            if (isValidPassword(mPassword.getText().toString())) {
                if (isConfirmPasswordValid(mConfirmPassword.getText().toString())) {
                    if (isValidPassword(mPassword.getText().toString())) {
                        if (isValidEmail(emailId)) {
                            checkEmailIdAndProceed();

                        }
                    }

                } else {
                    showToastMessage(GlobalConstants.PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH);
                }
            }


        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    /**
     * Method is used to show verification code alert
     * @param emailId set the email address.
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
                        alertDialog.dismiss();
                        Intent intent = new Intent(UploadPhotoActivity.this, VerificationEmailActivity.class);
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
        positiveButton.setTextColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.app_theme_color));
    }

    @Override
    public void onBackPressed() {

        if (mFirstName != null && mFirstName.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            finish();
        } else {
            storeAllRegistrationDetail();
            navigateBackToRegistration();
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
        }
    }

    /**
     * Method is used to set all registration detail in SharedPreferences
     */
    private void storeAllRegistrationDetail() {
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(getResources()
                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_DATA, mUserName.getText().toString()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_FIRST_NAME, mFirstName.getText().toString()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_LAST_NAME, mLastName.getText().toString()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_EMAIL, mEmailId.getText().toString()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_AGE, mYOB.getText().toString()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_GENDER, mGender.getText().toString()).apply();
        selectedBitmap = AppController.getInstance().getModelFacade().getLocalModel().getSelectImageBitmap();
        if (selectedBitmap != null) {
            String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
            pref.edit().putString(GlobalConstants.PREF_VAULT_IMAGE_URL, convertedImage).apply();
            // pref.edit().putString(GlobalConstants.PREF_VAULT_URI_IMAGE, String.valueOf(selectedImageUri)).commit();

        } else {
            pref.edit().putString(GlobalConstants.PREF_VAULT_IMAGE_URL, "").apply();
        }
        pref.edit().putString(GlobalConstants.PREF_VAULT_PASSWORD, mPassword.getText().toString()).apply();
        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false).apply();
    }

    /**
     * Method used for get the all user detail from data base.
     */
    private void getAllRegistrationDetail() {
        SharedPreferences pref = AppController.getInstance().getApplication().
                getSharedPreferences(getResources()
                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        boolean isStatus = pref.getBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false);
        if (isStatus) {
            mUserName.setText(pref.getString(GlobalConstants.PREF_VAULT_USER_DATA, ""));
            mFirstName.setText(pref.getString(GlobalConstants.PREF_VAULT_FIRST_NAME, ""));
            mLastName.setText(pref.getString(GlobalConstants.PREF_VAULT_LAST_NAME, ""));
            mGender.setText(pref.getString(GlobalConstants.PREF_VAULT_GENDER, ""));
            mYOB.setText(pref.getString(GlobalConstants.PREF_VAULT_AGE, ""));
            String imgUrl = pref.getString(GlobalConstants.PREF_VAULT_IMAGE_URL, "");
            if (imgUrl != null && !imgUrl.equalsIgnoreCase("")) {
                Bitmap bitmapImg = StringToBitMap(imgUrl);
                try {

                    mProfileImage.setImageBitmap(bitmapImg);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 3, screenWidth / 3);
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    mProfileImage.setLayoutParams(lp);
                    isImageProvided = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(UploadPhotoActivity.this, R.drawable.camera_background));
                }

            }
            mPassword.setText(pref.getString(GlobalConstants.PREF_VAULT_PASSWORD, ""));
        }
    }

    /**
     * Method used for fetch all the record and data from server.
     */
    private void fetchInitialRecordsForAll() {

        if (mVaultUserDataModel != null) {
            mVaultUserDataModel.unRegisterView(this);
            mVaultUserDataModel = null;
        }
        //noinspection deprecation
        pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
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

    /**
     * Method is used to set user data to vaultUser dto
     * @param registerUserValue set the register user value
     * @return the user detail in a object.
     */
    private User setAllVaultUserData(String registerUserValue) {
        vaultUser = new User();
        try {
            if (isImageProvided) {
                selectedBitmap = AppController.getInstance().getModelFacade().getLocalModel().getSelectImageBitmap();
                String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
                vaultUser.setImageurl(convertedImage);
            } else {
                vaultUser.setImageurl("");
            }

            vaultUser.setFname(mFirstName.getText().toString().trim());
            vaultUser.setLname(mLastName.getText().toString().trim());
            vaultUser.setEmailID(emailId.trim());
            vaultUser.setUsername(mUserName.getText().toString().trim());
            vaultUser.setPasswd(mPassword.getText().toString().trim());
            vaultUser.setGender(mGender.getText().toString().trim());
            if (mYOB.getText().toString().length() > 0) {
                vaultUser.setAge(Integer.parseInt(mYOB.getText().toString()));
            }
            vaultUser.setFlagStatus("vt");
            vaultUser.setAppID(Integer.parseInt(getResources().getString(R.string.app_id)));
            vaultUser.setAppVersion(getResources().getString(R.string.app_version));
            vaultUser.setDeviceType(getResources().getString(R.string.device_type));
            vaultUser.setIsRegisteredUser(registerUserValue);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return vaultUser;
    }

    /**
     * Method is used to store user registration data on server
     * @param registerUserValue set the register user value.
     */
    private void storeDataOnServer(final String registerUserValue) {

        try {
            //noinspection deprecation
            pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            vaultUser = setAllVaultUserData(registerUserValue);

            if (mFetchingAllDataModel != null) {
                mFetchingAllDataModel.unRegisterView(this);
                mFetchingAllDataModel = null;
            }

            if (mVaultUserDataModel != null) {
                mVaultUserDataModel.unRegisterView(this);
            }

            AppController.getInstance().getModelFacade()
                    .getLocalModel().setMailChimpRegisterUser(false);
            mVaultUserDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserDataModel();
            mVaultUserDataModel.registerView(this);
            mVaultUserDataModel.setProgressDialog(pDialog);
            mVaultUserDataModel.loadVaultData(vaultUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method is used to show toast message
     * @param message set the message which we want to show.
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
                animation = AnimationUtils.loadAnimation(UploadPhotoActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method used for convert bitmap image into base64.
     * @param bitmap set bitmap image.
     * @return the string
     */
    private String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }

    /**
     * Method used for change the string to bitmap
     * @param encodedString set the string
     * @return bitmap (from given string)
     */
    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case YOUR_SELECT_PICTURE_REQUEST_CODE: {
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
                    selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, UploadPhotoActivity.this);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, UploadPhotoActivity.this, sdImageMainDirectory);
                        /*Drawable drawable = new BitmapDrawable(getResources(), selectedBitmap);
                        userProfilePic.setImageDrawable(drawable);*/
                    AppController.getInstance().getModelFacade().getLocalModel().setSelectImageBitmap(selectedBitmap);
                    mProfileImage.setImageBitmap(selectedBitmap);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 3, screenWidth / 3);
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
//                        lp.setMargins(0,30,0,0);
                    mProfileImage.setLayoutParams(lp);
                    isImageProvided = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(UploadPhotoActivity.this, R.drawable.camera_background));
                }
            }
        }

        if (requestCode == 500)

        {
            isBackToSplashScreen = true;
        }
    }




    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPhotoActivity.this);
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
                File.separator + getResources().getString(R.string.profile_pic_directory) + File.separator);
        //noinspection ResultOfMethodCallIgnored
        root.mkdirs();
        Random randomNumber = new Random();
        final String fname = getResources().getString(R.string.profile_pic_directory) + "_" + randomNumber.nextInt(1000) + 1;
        sdImageMainDirectory = new File(root, fname);


        getUserChooserOptions();
    }

    /**
     * Method is used to show mail chimp confirmation dialog
     * @param mailChimpMessage set the mail chimp message.
     * @param firstName set first name.
     * @param lastName set the last name.
     * @param emailId set the email address
     */
    private void showConfirmLoginDialog(String mailChimpMessage, final String firstName, final String lastName,
                                        final String emailId) {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        TextView message = new TextView(this);
        //message.setGravity(Gravity.CENTER);
        message.setPadding(75, 50, 5, 10);
        message.setTextSize(17);
        message.setText(mailChimpMessage);
        message.setTextColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.gray));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(message);
        alertDialogBuilder.setTitle("Join our Mailing List?");
        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("No Thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(false);
                        storeDataOnServer("N");

                    }
                });

        alertDialogBuilder.setNegativeButton("Yes! Keep me Updated",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
                            AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(true);
                            loadData(emailId, firstName, lastName);

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
        negativeButton.setTextColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }


    @Override
    public void update() {

        Log.d("upload", "Uploaded photo update");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("result","Uploaded photo update 123");
                    if (mVaultUserDataModel != null && mVaultUserDataModel.getState() ==
                            BaseModel.STATE_SUCCESS_VAULTUSER_DATA) {
                        mVaultUserDataModel.unRegisterView(UploadPhotoActivity.this);
                        loadVaultUserData();
                    } else if (mFetchingAllDataModel != null && mFetchingAllDataModel.getState() ==
                            BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {

                        showAlertDialogForSuccess();

                    } else if (mMailChimpModelData != null && mMailChimpModelData.getState() == BaseModel.STATE_SUCCESS_MAIL_CHIMP) {
                        mMailChimpModelData.unRegisterView(UploadPhotoActivity.this);
                        if (!Utils.isInternetAvailable(UploadPhotoActivity.this) && pDialog.isShowing()) {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        } else {
                            storeDataOnServer("Y");
                        }
                        pDialog.dismiss();
                    } else if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                        pDialog.dismiss();
                        loginEmailModel.unRegisterView(UploadPhotoActivity.this);
                        if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
                            if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {

                                showToastMessage(GlobalConstants.ALREADY_REGISTERED_EMAIL);

                            } else if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")) {

                                showAlertDialog("Facebook", email);

                            } else if (loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")) {

                                showAlertDialog("Twitter", email);

                            } else if (loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {

                                showAlertDialog("Google", email);

                            } else {
                                fName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase()
                                        + mFirstName.getText().toString().trim().substring(1);
                                lName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase()
                                        + mLastName.getText().toString().trim().substring(1);


                                showConfirmLoginDialog(getResources().getString(R.string.do_you_want_to_join_our_mailing_list), fName,
                                        lName, email);

                            }

                        } else {
                            Utils.getInstance().showToastMessage(UploadPhotoActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Method used for check the email validation on server.
     */
    private void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {

            Utils.getInstance().getHideKeyboard(this);

            //noinspection deprecation
            pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            email = mEmailId.getText().toString();

            if (mVaultUserDataModel != null) {
                mVaultUserDataModel.unRegisterView(this);
                mVaultUserDataModel = null;
            }

            if (mFetchingAllDataModel != null) {
                mFetchingAllDataModel.unRegisterView(this);
                mFetchingAllDataModel = null;
            }

            if (mMailChimpModelData != null) {
                mMailChimpModelData.unRegisterView(this);
                mMailChimpModelData = null;
            }

            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
            }


            loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
            loginEmailModel.registerView(this);
            loginEmailModel.setProgressDialog(pDialog);
            loginEmailModel.loadLoginData(email);


        } else {
            Utils.getInstance().showToastMessage(UploadPhotoActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
    }

    /**
     * Method used for get the all user detail and data response from server.
     */
    private void getFetchDataResponse() {
        try {
            mFetchingAllDataModel.unRegisterView(UploadPhotoActivity.this);
            if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
                if (mFetchingAllDataModel.getABoolean()) {
                    Profile fbProfile = Profile.getCurrentProfile();
                    SharedPreferences pref = AppController.getInstance()
                            .getApplicationContext().getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                    if (fbProfile != null || userId > 0) {
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();

                        startService(new Intent(UploadPhotoActivity.this, TrendingFeaturedVideoService.class));

                    }
                }

            } else {
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            stopService(new Intent(UploadPhotoActivity.this, TrendingFeaturedVideoService.class));
            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
            pDialog.dismiss();
        }
    }

    /**
     * Method used for load mail chimp data from server
     * @param email set the email address
     * @param firstName set the first name
     * @param lastName set the last name.
     */
    private void loadData(String email, String firstName, String lastName) {

        //noinspection deprecation
        pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
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
     * Method used for load the vault user data on server.
     */
    private void loadVaultUserData() {
        if (!Utils.isInternetAvailable(UploadPhotoActivity.this) && pDialog.isShowing()) {
            pDialog.dismiss();
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);

        } else {
            try {
                pDialog.dismiss();
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                APIResponse response = gson.fromJson(mVaultUserDataModel.getVaultUserResult().trim(), classType);

                if (response != null) {
                    if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("vt_exists")
                            || mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("false")) {

                        showAlertDialog("Vault", response.getEmailID());

                    } else if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("fb_exists")) {
                        showAlertDialog("Facebook", response.getEmailID());
                    } else if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("tw_exists")) {
                        showAlertDialog("Twitter", response.getEmailID());
                    } else if (mVaultUserDataModel.getVaultUserResult().toLowerCase().contains("gm_exists")) {
                        showAlertDialog("Google", response.getEmailID());
                    } else {
                        if (response.getReturnStatus().toLowerCase().equals("true") || response.getReturnStatus().toLowerCase().equals("vt_exists")) {
                            SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                            pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, vaultUser.getUsername()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, vaultUser.getEmailID()).apply();

                            fetchInitialRecordsForAll();

                            if (isImageProvided) {
                                final File root = new File(Environment.getExternalStorageDirectory()
                                        + File.separator + getResources().getString(R.string.profile_pic_directory) + File.separator);
                                if (root != null) {
                                    if (root.listFiles() != null) {
                                        for (File childFile : root.listFiles()) {
                                            if (childFile != null) {
                                                if (childFile.exists())
                                                    //noinspection ResultOfMethodCallIgnored
                                                    childFile.delete();
                                            }

                                        }
                                        if (root.exists())
                                            //noinspection ResultOfMethodCallIgnored
                                            root.delete();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(UploadPhotoActivity.this, response.getReturnStatus(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                pDialog.dismiss();
            }

        }
    }

    /**
     * Method used for show the alert dialog box.
     * @param loginType set the type of login
     * @param emailId set the email address.
     */
    private void showAlertDialog(String loginType, final String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, " + emailId + ", with " + loginType + " login, would you like to update your profile with this new login method?");

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();

                        AppController.getInstance().getModelFacade().getLocalModel().setOverride(false);
                        AppController.getInstance().getModelFacade().getLocalModel().setUser(setAllVaultUserData(""));
                        showAlert(emailId);

                        //GK  overrideUserData(vaultUser);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.rightin, R.anim.leftout);
                        finish();
                        // tvFacebookLogin.setText("Login with Facebook");
                        LoginManager.getInstance().logOut();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.app_theme_color));
        negativeButton.setAllCaps(false);
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
        positiveButton.setTextColor(ContextCompat.getColor(UploadPhotoActivity.this, R.color.app_theme_color));
    }


    private Animation leftOutAnimation;
    private Animation rightInAnimation;

    /**
     * Method is used to proceed to next view after registration
     */
    private void checkEmailIdAndProceed() {

        leftOutAnimation = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.rightin);

        mFirstName.setAnimation(leftOutAnimation);
        mLastName.setAnimation(leftOutAnimation);
        mYOB.setAnimation(leftOutAnimation);
        mGender.setAnimation(leftOutAnimation);
        mEmailId.setAnimation(leftOutAnimation);
        mUserName.setAnimation(leftOutAnimation);
        mPassword.setAnimation(leftOutAnimation);
        mConfirmPassword.setAnimation(leftOutAnimation);
        mRegistrationButton.setAnimation(leftOutAnimation);
        tvAlreadyRegistered.setAnimation(leftOutAnimation);
        imageViewPasswordVisibility.setAnimation(leftOutAnimation);
        imageViewConfirmPasswordVisibility.setAnimation(leftOutAnimation);

        mFirstName.setVisibility(View.GONE);
        mLastName.setVisibility(View.GONE);
        mYOB.setVisibility(View.GONE);
        mGender.setVisibility(View.GONE);
        mEmailId.setVisibility(View.GONE);
        mUserName.setVisibility(View.GONE);
        mPassword.setVisibility(View.GONE);
        mConfirmPassword.setVisibility(View.GONE);
        mRegistrationButton.setVisibility(View.GONE);
        tvAlreadyRegistered.setVisibility(View.GONE);
        imageViewPasswordVisibility.setVisibility(View.GONE);
        imageViewConfirmPasswordVisibility.setVisibility(View.GONE);
        //tvHeader.setText("Register");

        mProfileImage.setAnimation(rightInAnimation);
        mSignUpButton.setAnimation(rightInAnimation);
        tvUploadPhoto.setAnimation(rightInAnimation);
        tvSignUpWithoutProfile.setAnimation(rightInAnimation);

        mProfileImage.setVisibility(View.VISIBLE);
        mSignUpButton.setVisibility(View.VISIBLE);
        tvUploadPhoto.setVisibility(View.VISIBLE);
        tvSignUpWithoutProfile.setVisibility(View.VISIBLE);
    }

    /**
     * Method is used to navigate back to registration screen
     */
    private void navigateBackToRegistration() {

        leftOutAnimation = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.leftin);
        rightInAnimation = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.rightout);

        mFirstName.setAnimation(leftOutAnimation);
        mLastName.setAnimation(leftOutAnimation);
        mYOB.setAnimation(leftOutAnimation);
        mGender.setAnimation(leftOutAnimation);
        mEmailId.setAnimation(leftOutAnimation);
        mUserName.setAnimation(leftOutAnimation);
        mPassword.setAnimation(leftOutAnimation);
        mConfirmPassword.setAnimation(leftOutAnimation);
        mRegistrationButton.setAnimation(leftOutAnimation);
        tvAlreadyRegistered.setAnimation(leftOutAnimation);
        imageViewPasswordVisibility.setAnimation(leftOutAnimation);
        imageViewConfirmPasswordVisibility.setAnimation(leftOutAnimation);

        mFirstName.setVisibility(View.VISIBLE);
        mLastName.setVisibility(View.VISIBLE);
        mYOB.setVisibility(View.VISIBLE);
        mGender.setVisibility(View.VISIBLE);
        mEmailId.setVisibility(View.VISIBLE);
        mUserName.setVisibility(View.VISIBLE);
        mPassword.setVisibility(View.VISIBLE);
        mConfirmPassword.setVisibility(View.VISIBLE);
        mRegistrationButton.setVisibility(View.VISIBLE);
        tvAlreadyRegistered.setVisibility(View.VISIBLE);
        imageViewPasswordVisibility.setVisibility(View.VISIBLE);
        imageViewConfirmPasswordVisibility.setVisibility(View.VISIBLE);
        //tvHeader.setText("Register");

        mProfileImage.setAnimation(rightInAnimation);
        mSignUpButton.setAnimation(rightInAnimation);
        tvUploadPhoto.setAnimation(rightInAnimation);
        tvSignUpWithoutProfile.setAnimation(rightInAnimation);

        mProfileImage.setVisibility(View.GONE);
        mSignUpButton.setVisibility(View.GONE);
        tvUploadPhoto.setVisibility(View.GONE);
        tvSignUpWithoutProfile.setVisibility(View.GONE);

    }

}
