package com.ncsavault.alabamavault.views;import android.app.AlertDialog;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.SharedPreferences;import android.content.pm.ApplicationInfo;import android.content.pm.PackageInfo;import android.content.pm.PackageManager;import android.content.pm.Signature;import android.graphics.Point;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.os.Handler;import android.provider.Settings;import android.util.Base64;import android.view.Display;import android.view.View;import android.view.WindowManager;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.widget.Button;import android.widget.ImageView;import android.widget.RelativeLayout;import android.widget.TextView;import com.facebook.FacebookSdk;import com.facebook.Profile;import com.ncsavault.alabamavault.BuildConfig;import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;import com.nostra13.universalimageloader.core.ImageLoader;import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;import com.nostra13.universalimageloader.utils.StorageUtils;import com.ncsavault.alabamavault.R;import com.ncsavault.alabamavault.controllers.AppController;import com.ncsavault.alabamavault.database.VaultDatabaseHelper;import com.ncsavault.alabamavault.defines.AppDefines;import com.ncsavault.alabamavault.globalconstants.GlobalConstants;import com.ncsavault.alabamavault.models.BannerDataModel;import com.ncsavault.alabamavault.models.BaseModel;import com.ncsavault.alabamavault.utils.Utils;import java.io.File;import java.security.MessageDigest;import java.security.NoSuchAlgorithmException;/** * Created by gauravkumar.singh on 11/24/2015. */public class SplashActivity extends PermissionActivity implements AbstractView {    private String videoUrlData;    private boolean isBackToSplashScreen = false;    private boolean askAgainForMustPermissions = false;    private boolean goToSettingsScreen = false;    private AlertDialog alertDialog;    private BannerDataModel bannerDataModel;    private TextView text;    @Override    protected void finalize() throws Throwable {        super.finalize();    }    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        FacebookSdk.sdkInitialize(this.getApplicationContext());        hashKey();        Bundle intent = getIntent().getExtras();        if (intent != null) {            String videoID = intent.getString("key");            AppController.getInstance().getModelFacade().getLocalModel().setVideoId(videoID);            AppController.getInstance().getModelFacade().getLocalModel().setNotificationVideoId(videoID);        }        // forceCrash();        Uri uri = getIntent().getData();        if (uri != null) {//            ugavault://domain/mypath?id=1743364023001//            http://vaultservices.cloudapp.net/SocialSharing/UGA/1808827980001.html            AppController.getInstance().getModelFacade().getLocalModel().setUriUrl(uri);            String videoUrl = String.valueOf(uri);            String[] videoParams = videoUrl.split("/");            String lastParam = videoParams[videoParams.length - 1];            if (lastParam.toLowerCase().contains("=")) {                videoUrlData = lastParam.split("=")[1];            } else {                videoUrlData = lastParam.split("\\.")[0];            }            AppController.getInstance().getModelFacade().getLocalModel().setVideoUrl(videoUrlData);        }        setContentView(R.layout.splash_activity);        text = (TextView) findViewById(R.id.text);        if(BuildConfig.BUILD_TYPE == "debug")        {            text.setVisibility(View.VISIBLE);        }else if(BuildConfig.BUILD_TYPE == "release")        {            text.setVisibility(View.GONE);        }        File cacheDir = StorageUtils.getCacheDirectory(SplashActivity.this);        ImageLoaderConfiguration config;        config = new ImageLoaderConfiguration.Builder(SplashActivity.this)                .threadPoolSize(3) // default                .denyCacheImageMultipleSizesInMemory()                .diskCache(new UnlimitedDiscCache(cacheDir))                .build();        ImageLoader.getInstance().init(config);        //Marshamallow permission        if (haveAllMustPermissions()) {            initViews();            startApp();        }    }    public void forceCrash() {        throw new RuntimeException("This is a crash");    }    private void hashKey() {        try {            PackageInfo info = getPackageManager().getPackageInfo(                    "com.ncsavault.alabamavault",                    PackageManager.GET_SIGNATURES);            for (Signature signature : info.signatures) {                MessageDigest md = MessageDigest.getInstance("SHA");                md.update(signature.toByteArray());                // Log.d("My key Hash : ", Base64.encodeToString(md.digest(), Base64.DEFAULT));                System.out.println("My key Hash : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));            }        } catch (PackageManager.NameNotFoundException e) {        } catch (NoSuchAlgorithmException e) {        }    }    @Override    protected void onPostResume() {        super.onPostResume();    }    public void initViews() {        try {            Utils.getInstance().setAppName(this);            Point size = new Point();            WindowManager w = getWindowManager();            int screenWidth;            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {                w.getDefaultDisplay().getSize(size);                screenWidth = size.y;            } else {                Display d = w.getDefaultDisplay();                screenWidth = d.getHeight();            }            int dimension = (int) (screenWidth * 0.45);            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dimension, dimension);            lp.setMargins(0, 30, 0, 0);            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {        switch (requestCode) {            case PERMISSION_REQUEST_MUST:                if (isGranted) {                    //perform action here                    initViews();                    startApp();                } else {                    if (!askAgainForMustPermissions) {                        askAgainForMustPermissions = true;                        haveAllMustPermissions();                    } else if (!goToSettingsScreen) {                        goToSettingsScreen = true;                        showPermissionsConfirmationDialog(GlobalConstants.VAULT_PERMISSION);                    } else {                        showPermissionsConfirmationDialog(GlobalConstants.VAULT_PERMISSION);                    }                }                break;        }    }    private void startApp() {        if (Utils.isInternetAvailable(AppController.getInstance().getApplicationContext())) {            loadData();        } else {            Utils.getInstance().showToastMessage(this,GlobalConstants.MSG_NO_CONNECTION,findViewById(R.id.llToast));            Handler handler = new Handler();            handler.postDelayed(new Runnable() {                @Override                public void run() {                    try {                        Profile fbProfile = Profile.getCurrentProfile();                        SharedPreferences pref = AppController.getInstance().getApplicationContext().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);                        long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);                        if (/*fbProfile != null || */userId > 0) {                            AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);                            overridePendingTransition(R.anim.slideup, R.anim.nochange);                            finish();                            //gk if (!VideoDataService.isServiceRunning)                                startService(new Intent(SplashActivity.this, TrendingFeaturedVideoService.class));                        } else {                            VaultDatabaseHelper.getInstance(SplashActivity.this).removeAllTabBannerData();                            AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);                            overridePendingTransition(R.anim.slideup, R.anim.nochange);                            finish();                        }                    } catch (Exception e) {                        e.printStackTrace();                    }                }            }, 3000);        }    }    @Override    protected void onDestroy() {        super.onDestroy();    }    @Override    protected void onResume() {        super.onResume();        // Logs 'install' and 'app activate' App Events.    }    @Override    public void onWindowFocusChanged(boolean hasFocus) {        super.onWindowFocusChanged(hasFocus);        if (hasFocus) {            if (isBackToSplashScreen) {                isBackToSplashScreen = false;                if (haveAllMustPermissions()) {                    initViews();                    startApp();                }            }        }    }    @Override    protected void onPause() {        super.onPause();        // Logs 'app deactivate' App Event.    }    public void showPermissionsConfirmationDialog(String message) {        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        alertDialogBuilder.setTitle("Permission Denied");        alertDialogBuilder                .setMessage(message);        alertDialogBuilder.setPositiveButton("Go to Settings",                new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface arg0, int arg1) {                        goToSettings();                    }                });        alertDialogBuilder.setNegativeButton("Cancel",                new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface dialog, int which) {                        showPermissionsConfirmationDialog(GlobalConstants.VAULT_PERMISSION);                    }                });        alertDialog = alertDialogBuilder.create();        alertDialog.setCancelable(false);        alertDialog.setCanceledOnTouchOutside(false);        alertDialog.show();        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        super.onActivityResult(requestCode, resultCode, data);        if (requestCode == 500) {            isBackToSplashScreen = true;        }    }    public void goToSettings() {        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        startActivityForResult(myAppSettings, 500);    }    @Override    public void update() {        System.out.println("splash update");        if (bannerDataModel.getState() == BaseModel.STATE_SUCCESS) {            try {                bannerDataModel.unRegisterView(this);                Profile fbProfile = Profile.getCurrentProfile();                SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);                if (/*fbProfile != null ||*/ userId > 0) {                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);                    overridePendingTransition(R.anim.slideup, R.anim.nochange);                    finish();                   //gk if (!VideoDataService.isServiceRunning)                        startService(new Intent(SplashActivity.this, TrendingFeaturedVideoService.class));                } else {                    VaultDatabaseHelper.getInstance(SplashActivity.this).removeAllTabBannerData();                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);                    overridePendingTransition(R.anim.slideup, R.anim.nochange);                    finish();                }            } catch (Exception e) {                e.printStackTrace();            }        }    }    /**     * load splash screen data frim server     */    private void loadData() {        if(bannerDataModel != null)        {            bannerDataModel.unRegisterView(this);        }        bannerDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getBannerDataModel();        bannerDataModel.registerView(this);        bannerDataModel.loadTabData();////        new Handler().postDelayed(new Runnable() {//            @Override//            public void run() {//                Profile fbProfile = Profile.getCurrentProfile();//                SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);//                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);////                if (fbProfile != null || userId > 0) {////                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_MAIN_SCREEN);//                   // AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);//                    overridePendingTransition(R.anim.slideup, R.anim.nochange);//                    finish();//                    if(bannerDataModel != null)//                    {//                        bannerDataModel.unRegisterView(SplashActivity.this);//                    }////                } else {////                    VaultDatabaseHelper.getInstance(SplashActivity.this).removeAllTabBannerData();//                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);//                   // AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);//                    overridePendingTransition(R.anim.slideup, R.anim.nochange);//                    finish();//                    if(bannerDataModel != null)//                    {//                        bannerDataModel.unRegisterView(SplashActivity.this);//                    }//                }//            }//        },3000);    }}