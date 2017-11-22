package com.ncsavault.fragments.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.UserProfileModel;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.CircularImageView;
import com.ncsavault.utils.ImageLoaderController;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.AbstractView;
import com.ncsavault.views.ContactActivity;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.LoginEmailActivity;
import com.ncsavault.views.SupportActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Random;

import applicationId.R;

/**
 * Class used for showing the user profile data.
 * Show the facebook, Google and Twitter email id.
 * User profile image and email id.
 * We can push notification enable and disable
 * App logout functionality.
 * Also user support feature.
 */

@SuppressWarnings("ALL")
public class ProfileFragment extends BaseFragment implements AbstractView {

    private static Context mContext;
    private SwitchCompat mSwitchCompat;
    private CircularImageView mUserProfileImage;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmailId;
    private TextView mTwitterEmailId;
    private TextView mFacebookEmailId;
    private EditText edFirstName, edLastName;
    private Button mResetPasswordButton, mLogoutButton;
    private User responseUser = null;
    private ProgressDialog pDialog;
    private UserProfileModel mUserProfileModel;
    private Uri selectedImageUri = null;
    private Uri outputFileUri = null;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    private File sdImageMainDirectory;
    private AlertDialog alertDialog = null;
    private final CharSequence[] alertListItems = {Html.fromHtml("<b>Take from camera</b>"), Html.fromHtml("<b>Select from gallery<b>")};

    private LinearLayout edLinearLayout, tvLinearLayout;
    private TwitterLoginButton twitterLoginButton;
    private AsyncTask<Void, Void, Void> mPermissionChangeTask;
    private String refreshedToken;
    private String result;
    private SharedPreferences prefs;
    private Button loginButton;
    private TextView mContactSupportView;
    private TextView textViewSupportSkip;
    private LinearLayout resetButtonLayout;
    private static final int PERMISSION_REQUEST_MUST = 101;
    private static CallbackManager callbackManager;
    private static final int PICK_FROM_CAMERA = 1;
    private Bitmap selectedBitmap;

    /**
     * Method used for create a new instance of profile fragment.
     * @param context The reference of Context.
     * @param centerX  Set the X value
     * @param centerY Set the Y value
     * @return new instance of fragment.
     */
    public static Fragment newInstance(Context context, int centerX, int centerY) {
        mContext = context;
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        Fragment frag = new ProfileFragment();
        frag.setArguments(args);
        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.user_profile_screen_layout, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwitchCompat = view.findViewById(R.id.toggle_button);
        mUserProfileImage = view.findViewById(R.id.imgUserProfile);

        mFirstName = view.findViewById(R.id.tv_first_name);
        mLastName = view.findViewById(R.id.tv_last_name);
        mEmailId = view.findViewById(R.id.email_id);
        mTwitterEmailId = view.findViewById(R.id.twitter_email_id);
        mFacebookEmailId = view.findViewById(R.id.facebook_email_id);
        TextView mPushNotification = view.findViewById(R.id.tv_push_view);
        mContactSupportView = view.findViewById(R.id.tv_support);
        textViewSupportSkip = view.findViewById(R.id.tv_support_skip);
        resetButtonLayout = view.findViewById(R.id.linear4);
        setToolbarIcons();
        ((HomeScreen) getActivity()).textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isInternetAvailable(mContext)) {
                    if (((HomeScreen) mContext).textViewEdit.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.edit))) {
                        ((HomeScreen) mContext).textViewEdit.setText(getActivity().getResources().getString(R.string.save));

                        edLinearLayout.setVisibility(View.VISIBLE);
                        tvLinearLayout.setVisibility(View.GONE);

                    } else if (((HomeScreen) mContext).textViewEdit.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.save))) {


                        if (edFirstName.getText().toString().length() == 0) {
                            ((HomeScreen) mContext).showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);

                        } else if (edLastName.getText().toString().length() == 0) {
                            ((HomeScreen) mContext).showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);
                        } else {
                            ((HomeScreen) mContext).textViewEdit.setText(getActivity().getResources().getString(R.string.edit));
                            edLinearLayout.setVisibility(View.GONE);
                            tvLinearLayout.setVisibility(View.VISIBLE);
                            mFirstName.setText(edFirstName.getText().toString());
                            mLastName.setText(edLastName.getText().toString());
                            updateUserData();
                        }


                    }
                }else
                {
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        mResetPasswordButton = view.findViewById(R.id.tv_reset_password);
        mLogoutButton = view.findViewById(R.id.tv_logout);

        edFirstName = view.findViewById(R.id.ed_first_name);
        edLastName = view.findViewById(R.id.ed_last_name);

        ProgressBar pBar = view.findViewById(R.id.progressbar);
        pBar.setVisibility(View.VISIBLE);


        edLinearLayout = view.findViewById(R.id.edit_linear_layout);
        tvLinearLayout = view.findViewById(R.id.text_linear_layout);

        twitterLoginButton = view.findViewById(R.id.twitter_login_button);
        FrameLayout circulerFrameLayout = view.findViewById(R.id.circular_image_layout);
        ScrollView scrollView = view.findViewById(R.id.scroll_view);
        LinearLayout loginViewLayout = view.findViewById(R.id.login_view_layout);
        loginButton = view.findViewById(R.id.button_login);

        int btnSize = mSwitchCompat.getWidth();
        mSwitchCompat.setHeight(btnSize);

        prefs = context.getSharedPreferences(mContext.getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        boolean isAllowed = prefs.getBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, false);
        long userId = prefs.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        mSwitchCompat.setChecked(isAllowed);

        if (userId == GlobalConstants.DEFAULT_USER_ID) {
            loginViewLayout.setVisibility(View.VISIBLE);
            ((HomeScreen) mContext).textViewEdit.setVisibility(View.INVISIBLE);
            circulerFrameLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            mContactSupportView.setVisibility(View.VISIBLE);
        } else {
            loginViewLayout.setVisibility(View.GONE);
            circulerFrameLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            ((HomeScreen) mContext).textViewEdit.setVisibility(View.VISIBLE);
            if (Utils.isInternetAvailable(mContext)) {
                loadUserDataFromServer();
            } else {
                loadUserDataFromLocal();
            }
            initData();
            initializeFacebookUtils();
        }

        initListener();

    }

    /**
     * Method used for set the toolbar icons and text in profile screen.
     */
    private void setToolbarIcons() {
        ((HomeScreen) mContext).linearLayoutToolbarText.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).imageViewSearch.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).textViewEdit.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewEdit.setText(getActivity().getResources().getString(R.string.edit));
        ((HomeScreen) mContext).imageViewBackNavigation.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).textViewToolbarText1.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewToolbarText2.setText(getResources().getString(R.string.vault_text));
        Typeface faceNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");
        ((HomeScreen) getActivity()).textViewToolbarText2.setTypeface(faceNormal);
    }

    /**
     * Method used for initialize the component of profile screen.
     */
    private void initData() {
        Utils.getScreenDimensions(getActivity());
        Profile fbProfile = Profile.getCurrentProfile();
        if (fbProfile != null) {
            mFacebookEmailId.setText(fbProfile.getName());
        }

        TwitterSession session =
                Twitter.getSessionManager().getActiveSession();


        if (session != null) {
            mTwitterEmailId.setText(getActivity().getResources().getString(R.string.at) + session.getUserName());
        }
    }

    /**
     * Method used for handle the click event of all the views
     */
    private void initListener() {
        mUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(mContext))
                    try {
                        //Marshmallow permissions for write external storage.

                        if (ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_MUST);
                        } else {
                            Log.e("DB", "PERMISSION GRANTED");
                            openImageIntent();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                else
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
        });

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_CHANGE_PASSWORD_SCREEN);

            }
        });

        mFacebookEmailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(mContext.getApplicationContext())) {

                    boolean installedFacebookApp = checkIfAppInstalled("com.facebook.katana");
                    if (!installedFacebookApp) {

                        showConfirmSharingDialog(getActivity().getResources().getString(R.string.facebook_install_msg), getActivity().getResources().getString(R.string.facebook_url));


                    } else if (Profile.getCurrentProfile() == null) {
                        LoginManager.getInstance().logInWithReadPermissions((HomeScreen) mContext,
                                Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));
                    } else {

                        LoginManager.getInstance().logOut();
                        mFacebookEmailId.setText(getActivity().getResources().getString(R.string.link_facebook));
                    }


                } else {
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        mTwitterEmailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(mContext.getApplicationContext())) {

                    boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");
                    if (!installedTwitterApp) {

                        String twitterPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                        showConfirmSharingDialog("Twitter app is not installed would you like to install it now?", twitterPlayStoreUrl);


                    } else {

                        TwitterSession session =
                                Twitter.getSessionManager().getActiveSession();
                        if (session == null) {
                            twitterLoginButton.performClick();
                        } else {
                            Twitter.logOut();
                            mTwitterEmailId.setText(getActivity().getResources().getString(R.string.link_twitter));
                        }
                    }


                } else {
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {

                mTwitterEmailId.setText(getActivity().getResources().getString(R.string.at) + twitterSessionResult.data.getUserName());
            }

            @Override
            public void failure(TwitterException e) {

            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twitter.logOut();
                AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(false);
                mContext.stopService(new Intent(mContext, TrendingFeaturedVideoService.class));

                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences pref = mContext.getSharedPreferences(mContext.getResources()
                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, "").apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, "").apply();

                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(mContext, LoginEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ((HomeScreen) mContext).finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.stopService(new Intent(mContext, TrendingFeaturedVideoService.class));
                AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(false);
                SharedPreferences pref = mContext.getSharedPreferences(mContext.getResources()
                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, "").apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, "").apply();

                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(mContext, LoginEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ((HomeScreen) mContext).finish();
            }
        });

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                refreshedToken = FirebaseInstanceId.getInstance().getToken();
                SharedPreferences pref = AppController.getInstance().
                        getApplicationContext().getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                @SuppressLint("HardwareIds")
                final String deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                mPermissionChangeTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        Log.i("Sync Dialog", "Device Id : " + deviceId);
                        Log.d("Registration","Registration Id in Toggle Setting Dialog : " + refreshedToken);
                        if (isChecked) {
                            if (!refreshedToken.equalsIgnoreCase("")) {
                                result = AppController.getInstance().getServiceManager().getVaultService().
                                        sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                                                refreshedToken, deviceId, isChecked,userId);
                                if (result != null) {
                                    if (result.toLowerCase().contains("success")) {

                                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, true).apply();
                                    }
                                }
                            } else {
                                Utils.getInstance().registerWithGCM(context);
                            }

                        } else {
                            result = AppController.getInstance().getServiceManager().getVaultService().
                                    sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                                            refreshedToken, deviceId, isChecked,userId);
                            if (result != null) {
                                if (result.toLowerCase().contains("success")) {
                                    prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, false).apply();
                                }
                            }

                        }
                        Log.d("result","Result of Push Registration Url : " + result);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {

                        if (isChecked) {
                            ((HomeScreen) mContext).showToastMessage("Enable Push Notification");
                        } else {
                            ((HomeScreen) mContext).showToastMessage("Disable Push Notification");
                        }

                    }
                };

                mPermissionChangeTask.execute();
            }
        });

        ((HomeScreen) getActivity()).imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mContactSupportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, SupportActivity.class);
                mContext.startActivity(intent);

            }
        });

        textViewSupportSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ContactActivity.class);
                mContext.startActivity(intent);
            }
        });
    }
    /**
     * Method used for initilize the facebook data and  callback listener.
     */
    @SuppressWarnings("deprecation")
    private void initializeFacebookUtils() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d("Facebook login successful","Facebook login successful");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {

                                            Log.v("LoginActivity", response.toString());

                                            mFacebookEmailId.setText(object.getString("name"));
                                        } catch (Exception e) {
                                            LoginManager.getInstance().logOut();
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday, first_name, last_name");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert();
                    }

                    private void showAlert() {
                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);
                    }
                });

    }

    /**
     * Method used for set the image from device or camera click.
     */
    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator +
                mContext.getResources()
                        .getString(R.string.profile_pic_directory) + File.separator);
        //noinspection ResultOfMethodCallIgnored
        root.mkdirs();
        Random randomNumber = new Random();
        final String fname = mContext.getResources()
                .getString(R.string.profile_pic_directory) + "_" + randomNumber.nextInt(1000) + 1;
        sdImageMainDirectory = new File(root, fname);

        getUserChooserOptions();
    }

    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String title = mContext.getResources().getString(R.string.add_profile_pic);
        SpannableStringBuilder sb = new SpannableStringBuilder(title);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.app_theme_color)), 0, title.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setTitle(sb);
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


    /**
     * Method used for click the picture from camera.
     */
    private void choiceAvatarFromCamera() {
        // Check for Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Check for Nougat devices, as Nougat doesn't support Uri.
            // We need to provide FileProvider to access file system for image cropping
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outputFileUri = FileProvider.getUriForFile(mContext.getApplicationContext(),
                        mContext.getPackageName() + ".provider",
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
     * Method used for check app installed or not.
     * @param uri set the package name.
     * @return the value true or false.
     */
    private boolean checkIfAppInstalled(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            //Check if the Facebook app is disabled
            ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    /**
     * Method used for showing the alert dialog box.
     * If facebook and twitter app apre not installed in device.
     * @param message please install facebook or twitter app.
     * @param playStoreUrl Set the play store url.
     */
    @SuppressWarnings("deprecation")
    private void showConfirmSharingDialog(String message, final String playStoreUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder
                .setMessage(message);
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
        positiveButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method used for load the user data from server.
     * Whenever will be come in profile screen
     * And Will shaow updated data always.
     */
    private void loadUserDataFromServer() {

        try {

            loadUserDataFromLocal();
            SharedPreferences pref = mContext.getSharedPreferences(mContext.getResources()
                    .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
            final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
            final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");

            mUserProfileModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserProfileModel();
            mUserProfileModel.registerView(this);

            mUserProfileModel.loadFetchData(email, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_MUST) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageIntent();
            }
        }

    }


    /**
     * Method used for update the user profile on server
     * If user want to change first ,last name and profile picture.
     */
    @SuppressWarnings("deprecation")
    private void updateUserData() {

        pDialog = new ProgressDialog(mContext, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog((HomeScreen) mContext));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        if (responseUser != null) {

            responseUser.setFname(edFirstName.getText().toString());
            responseUser.setLname(edLastName.getText().toString());
        }

        try {
            SharedPreferences pref = mContext.getSharedPreferences(mContext.getResources()
                    .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
            final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
            final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
            if (responseUser != null) {
                if (selectedImageUri != null) {
                    try {
                        selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, (HomeScreen) mContext);
                        selectedBitmap = Utils.getInstance().
                                rotateImageDetails(selectedBitmap, selectedImageUri,
                                        (HomeScreen) mContext, sdImageMainDirectory);
                        String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
                        responseUser.setImageurl(convertedImage);

                    } catch (OutOfMemoryError e) {
                        if(pDialog != null)
                        {
                            pDialog.dismiss();
                        }
                        e.printStackTrace();

                    }
                }
            }

            if (mUserProfileModel != null) {
                mUserProfileModel.unRegisterView(this);
            }
            mUserProfileModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserProfileModel();
            mUserProfileModel.registerView(this);
            mUserProfileModel.setProgressDialog(pDialog);
            mUserProfileModel.loadUserProfileData(responseUser, email, userId);

        } catch (Exception e) {
            e.printStackTrace();
            if(pDialog != null)
            {
                pDialog.dismiss();
            }
        }

    }


    /**
     * Method used for load the user data from data base if you are not getting
     * from server and no internet coneection.
     */
    private void loadUserDataFromLocal() {
        try {
            responseUser = AppController.getInstance().getModelFacade().getLocalModel().getUserData();
            if (responseUser != null) {
                if (responseUser.getUserID() > 0) {
                    mFirstName.setText(responseUser.getFname());
                    mLastName.setText(responseUser.getLname());

                    mEmailId.setText(responseUser.getEmailID());

                    edFirstName.setText(responseUser.getFname());
                    edLastName.setText(responseUser.getLname());

                    if (responseUser.getFlagStatus().toLowerCase().equals("vt")) {
                        resetButtonLayout.setVisibility(View.VISIBLE);

                    } else {
                        resetButtonLayout.setVisibility(View.GONE);
                    }

                } else {
                    ((HomeScreen) mContext).showToastMessage("Error loading information");
                }
            } else {
                ((HomeScreen) mContext).showToastMessage("Error loading information");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {

            switch (requestCode) {
                case YOUR_SELECT_PICTURE_REQUEST_CODE: {
                    final boolean isCamera;
                    //noinspection UnusedAssignment
                    isCamera = data == null || MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
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
                    selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, (HomeScreen) mContext);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap,
                            selectedImageUri, (HomeScreen) mContext, sdImageMainDirectory);

                    ((HomeScreen) mContext).textViewEdit.setText(getActivity().getResources().getString(R.string.save));
                    mUserProfileImage.setImageBitmap(selectedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode,
                    data);
        }

    }

    /**
     * Method used for convert bitmap to base 64 format.
     * @param bitmap Pass the bitmap image
     * @return the value of string.
     */
    private String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }


    @Override
    public void update() {
        ((HomeScreen) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mUserProfileModel != null && mUserProfileModel.getState() == BaseModel.STATE_SUCCESS) {
                    mUserProfileModel.unRegisterView(ProfileFragment.this);
                    try {
                        pDialog.dismiss();
                        if (mUserProfileModel.getUserProfileResult()) {
                            ((HomeScreen) mContext).showToastMessage("Profile updated successfully");
                        } else {
                            ((HomeScreen) mContext).showToastMessage("Error updating information");
                            loadUserDataFromLocal();
                        }
                        final File root = new File(Environment.getExternalStorageDirectory() +
                                File.separator + mContext.getResources()
                                .getString(R.string.profile_pic_directory) + File.separator);
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mUserProfileModel != null && mUserProfileModel.getState() ==
                        BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                    mUserProfileModel.unRegisterView(ProfileFragment.this);

                    loadData();
                }
            }
        });
    }


    /**
     * Method used for load the user data from server.
     * Whenever will be come in profile screen
     * And Will shaow updated data always.
     */
    private void loadData() {
        try {
            if (Utils.isInternetAvailable(mContext)) {
                if (mUserProfileModel.getFetchingResult() != null) {
                    Gson gson = new Gson();
                    Type classType = new TypeToken<User>() {
                    }.getType();
                    responseUser = gson.fromJson(mUserProfileModel.getFetchingResult().trim(), classType);
                    if (responseUser != null) {
                        if (responseUser.getUserID() > 0) {

                            AppController.getInstance().getModelFacade().getLocalModel().
                                    storeUserDataInPreferences(responseUser);

                            mFirstName.setText(responseUser.getFname());
                            mLastName.setText(responseUser.getLname());

                            mEmailId.setText(responseUser.getEmailID());

                            edFirstName.setText(responseUser.getFname());
                            edLastName.setText(responseUser.getLname());

                            if (responseUser.getFlagStatus().toLowerCase().equals("vt")) {
                                resetButtonLayout.setVisibility(View.VISIBLE);

                            } else {
                                resetButtonLayout.setVisibility(View.GONE);
                            }

                            if (responseUser.getImageurl().length() > 0) {

                        try {
                            com.android.volley.toolbox.ImageLoader volleyImageLoader =
                                    ImageLoaderController.getInstance(getActivity()).getImageLoader();

                            volleyImageLoader.get(responseUser.getImageurl(),
                                    com.android.volley.toolbox.ImageLoader.getImageListener(mUserProfileImage,
                                            R.drawable.camera_background, R.drawable.camera_background));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                       }
                        } else {
                            ((HomeScreen) mContext).showToastMessage("Error loading information");
                        }
                    } else {
                        ((HomeScreen) mContext).showToastMessage("Error loading information");
                    }

                } else {
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);

                }

            } else {
                ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used for check only validation of string.
     * @param str pass the string value
     * @return the value true and false.
     */
    private boolean isValidText(String str) {
        return str != null && str.length() >= 0;
    }

    @SuppressWarnings("deprecation")
    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder
                .setMessage(mContext.getResources().getString(R.string.profile_alert_text));
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.save_data),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!isValidText(edFirstName.getText().toString())) {
                            ((HomeScreen) mContext).showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);
                        } else if (!isValidText(edLastName.getText().toString())) {
                            ((HomeScreen) mContext).showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);
                        }

                        Utils.getInstance().getHideKeyboard((HomeScreen) mContext);

                        mFirstName.setText(edFirstName.getText().toString());
                        mLastName.setText(edLastName.getText().toString());


                        edFirstName.setVisibility(View.GONE);
                        edLastName.setVisibility(View.GONE);


                        edFirstName.setBackground(null);
                        edLastName.setBackground(null);


                        mFirstName.setVisibility(View.VISIBLE);
                        mLastName.setVisibility(View.VISIBLE);

                        updateUserData();

                        alertDialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final File root = new File(Environment.getExternalStorageDirectory() +
                                File.separator + mContext.getResources()
                                .getString(R.string.profile_pic_directory) + File.separator);
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
                                    root.delete();
                            }
                        }

                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button PositiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        PositiveButton.setAllCaps(false);
        PositiveButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if ((((HomeScreen) mContext).textViewEdit.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.save)))) {
            if (edFirstName.getText().toString().length() == 0) {
                ((HomeScreen) mContext).showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);

            } else if (edLastName.getText().toString().length() == 0) {
                ((HomeScreen) mContext).showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);
            } else {
                showConfirmationDialog();
            }
        }

        if (selectedBitmap != null) {
            selectedBitmap.recycle();
            selectedBitmap = null;
        }


    }

}
