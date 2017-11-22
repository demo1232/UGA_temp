package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.events.listeners.AdvertisingEvents;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.jwplayer.KeepScreenOnHandler;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import applicationId.R;


/**
 * VideoInfoActivity is used for playing video
 */
public class VideoInfoActivity extends BaseActivity implements VideoPlayerEvents.OnFullscreenListener {

    //Declare fields required
    private final String tag = "VideoInfoActivity";
    private VideoDTO videoObject;
    private String videoCategory;
    private boolean isFavoriteChecked;
    private AsyncTask<Void, Void, Void> mPostTask;
    private String postResult;
    private Activity context;
    private int displayHeight = 0;
    private int displayWidth = 0;

    //Declare UI elements
    private LinearLayout ll_header;
    private RelativeLayout rlVideoNameStrip;
    private FrameLayout rlVideoLayout;
    private JWPlayerView videoView;
    private TextView tvVideoName;
    private ImageView imgToggleButton;
    private ImageView imgVideoClose;
    private ImageView imgVideoStillUrl;

    //UI Elements and fields for Social Sharing
    private static CallbackManager callbackManager;
    private AlertDialog alertDialog = null;
    private TwitterLoginButton twitterLoginButton;
    private Animation animation;
    private RelativeLayout viewPagerRelativeView;
    private LinearLayout shareVideoLayout;
    private static long videoCurrentPosition = 0;
    private boolean isFirstTimeEntry = false;
    private AdView mAdView;
    private static Uri imageUri;
    private View linearLayout;
    private ImageView facebookShareView;
    private ImageView twitterShareView;
    private ImageView flatButtonFacebook;
    private ImageView flatButtonTwitter;
    private String longDescription;
    private String videoName;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final Bundle params = new Bundle();
    private TextView mVideoDescription;
    private LinearLayout llVideoLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.video_info_layout);
        context = VideoInfoActivity.this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        getIntentData();
        try {
            initializeAllVideoInfoActivityData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show ad mob banner advertising
     */
    private void adMobBannerAdvertising() {
        int navBarHeight = Utils.getNavBarStatusAndHeight(this);

        mAdView = findViewById(R.id.adView);
        int ht = 32;
        int wt = 320;
        if (Utils.getScreenHeight(this) <= 400) {
            ht = 32;
        } else if (Utils.getScreenHeight(this) > 400 ||Utils.getScreenHeight(this) <=720){
            ht = 50;
        }else if(Utils.getScreenHeight(this) > 720 ){
            ht = 90;
        }
        if(Utils.getScreenDimensions(this) <= 720)
        {
            wt = 340;
        }else
        {
            wt = 395;
        }
        Log.i("Width ","111Width : "+wt +"111Height :"+ht);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(wt, ht);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if (Utils.hasNavBar(this)) {
            layoutParams.bottomMargin = navBarHeight;

        }
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getResources().getString(R.string.test_device_id))
                .build();
        mAdView.loadAd(adRequest);
    }

    /**
     * Method is used to initialize VideoInfoActivity data
     */
    private void initializeAllVideoInfoActivityData() {
        try {
            displayWidth = Utils.getScreenDimensions(this);
            displayHeight = Utils.getScreenHeight(this);
            initializeFacebookUtil();
            initViews();
            setDimensions();
            adMobBannerAdvertising();
            //The reason to put this thread, to make screen aware of what orientation it is using

            initData();
            initListener();



            Thread.sleep(500);

            if (getScreenOrientation() == 1) {
                performAnimations();
            } else {
                moveToFullscreen();
            }

            AppController.getInstance().getModelFacade().getLocalModel().setUriUrl(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  
    /**
     * Method is used to get orientation of screen
     * @return int orientation value
     */
    private int getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation;
        Point outSize = new Point();
        getOrient.getSize(outSize);

        if (outSize.x == outSize.y) {
            orientation = Configuration.ORIENTATION_UNDEFINED;
        } else {
            if (outSize.x < outSize.y) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                linearLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            boolean installedFbApp = checkIfAppInstalled("com.facebook.katana");
            boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");

            if (facebookShareView != null && facebookShareView.getVisibility() == View.VISIBLE && installedFbApp) {
                facebookShareView.setVisibility(View.GONE);
                if (flatButtonFacebook != null && flatButtonFacebook.getVisibility() == View.GONE) {
                    flatButtonFacebook.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        makeShareDialog();
                    }

                }
            }
            if (facebookShareView != null && facebookShareView.getVisibility() == View.VISIBLE && !installedFbApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }

            }
            if (twitterShareView != null && twitterShareView.getVisibility() == View.VISIBLE && !installedTwitterApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }
            }

            if (twitterShareView != null && twitterShareView.getVisibility() == View.VISIBLE && installedTwitterApp) {
                twitterShareView.setVisibility(View.GONE);
                if (flatButtonTwitter != null && flatButtonTwitter.getVisibility() == View.GONE) {
                    flatButtonTwitter.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        makeShareDialog();
                    }
                }
            }

            if (flatButtonFacebook != null && flatButtonFacebook.getVisibility() == View.VISIBLE && !installedFbApp) {
                flatButtonFacebook.setVisibility(View.GONE);
                if (facebookShareView != null && facebookShareView.getVisibility() == View.GONE) {
                    facebookShareView.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        makeShareDialog();
                    }
                }
            }

            if (flatButtonTwitter != null && flatButtonTwitter.getVisibility() == View.VISIBLE && !installedTwitterApp) {
                flatButtonTwitter.setVisibility(View.GONE);
                if (twitterShareView != null && twitterShareView.getVisibility() == View.GONE) {
                    twitterShareView.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                            makeShareDialog();
                        }
                    }
                }
            }

            if (flatButtonFacebook != null && flatButtonFacebook.getVisibility() == View.VISIBLE && installedFbApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }
            }
            if (flatButtonTwitter != null && flatButtonTwitter.getVisibility() == View.VISIBLE && installedTwitterApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        params.putString(FirebaseAnalytics.Param.ITEM_ID, videoObject.getVideoName());
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoObject.getVideoName());
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_info");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoCategory != null) {
            // -----stopping the flurry event of video-----------
            try {
                FlurryAgent.endTimedEvent(videoCategory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode,
                    data);
        }


        if (requestCode == 100) {
            boolean installedFbApp = checkIfAppInstalled("com.facebook.katana");
            boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");

            if (facebookShareView != null && facebookShareView.getVisibility() == View.VISIBLE && installedFbApp) {
                facebookShareView.setVisibility(View.GONE);
            }
            if (twitterShareView != null && twitterShareView.getVisibility() == View.VISIBLE && installedTwitterApp) {
                twitterShareView.setVisibility(View.GONE);
            }

            if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                linearLayout.setVisibility(View.GONE);
                makeShareDialog();
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        displayWidth = Utils.getScreenDimensions(this);
        displayHeight = Utils.getScreenHeight(this);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            if (ll_header != null && rlVideoNameStrip != null) {
                ll_header.setVisibility(View.GONE);
                rlVideoNameStrip.setVisibility(View.GONE);
            }
            if (mAdView != null) {
                mAdView.setVisibility(View.GONE);
            }
            if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                linearLayout.setVisibility(View.GONE);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            if (imgVideoStillUrl != null && imgVideoStillUrl.isShown()) {
                Animation anim = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.fadein);
                imgVideoStillUrl.setAnimation(anim);
                imgVideoStillUrl.setVisibility(View.GONE);
            }

            if (llVideoLoader != null && llVideoLoader.isShown())
            llVideoLoader.setVisibility(View.GONE);

            if(rlVideoLayout != null) {
                Log.d(tag, "displayWidth land : " + displayWidth + " " + displayHeight);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
                rlVideoLayout.setLayoutParams(lp);
            }
        } else {

            ll_header.setVisibility(View.VISIBLE);
            rlVideoNameStrip.setVisibility(View.VISIBLE);
            if (mAdView != null) {
                mAdView.setVisibility(View.VISIBLE);
            }

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            int aspectHeight = (displayWidth * 9) / 16;

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(displayWidth, aspectHeight);
            lp.addRule(RelativeLayout.BELOW, R.id.view_line);
            if (rlVideoLayout != null) {
                rlVideoLayout.setLayoutParams(lp);
            }

            if (imgVideoStillUrl.isShown()) {
                Animation anim = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.fadein);
                imgVideoStillUrl.setAnimation(anim);
                imgVideoStillUrl.setVisibility(View.GONE);
            }


            if (llVideoLoader.isShown()) {
                llVideoLoader.setVisibility(View.GONE);
            }

        }
    }

    /**
     * Method to make video view in full screen mode
     */
    private void moveToFullscreen() {

        if (ll_header != null && rlVideoNameStrip != null) {
            ll_header.setVisibility(View.GONE);
            rlVideoNameStrip.setVisibility(View.GONE);

        }
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
        if (rlVideoLayout != null) {
            rlVideoLayout.setLayoutParams(lp1);
        }

        if (mAdView != null) {
            mAdView.setVisibility(View.GONE);
        }

    }

    /**
     * Method is used to perform animation
     */
    private void performAnimations() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.slidedown_header);
                if (rlVideoNameStrip != null && animation != null) {
                    rlVideoNameStrip.setAnimation(animation);
                    rlVideoNameStrip.setVisibility(View.VISIBLE);
                }
            }
        }, 300);

    }

    /**
     * Method to initialize views
     */
    @Override
    public void initViews() {

        ll_header = findViewById(R.id.ll_header);
        rlVideoNameStrip =  findViewById(R.id.rl_header);
        rlVideoLayout =  findViewById(R.id.rl_video_layout);
        videoView =  findViewById(R.id.jw_player);
        tvVideoName =  findViewById(R.id.tv_video_name);
        imgToggleButton =  findViewById(R.id.imgToggleButton);
        viewPagerRelativeView =  findViewById(R.id.relative_view_pager);
        mVideoDescription =  findViewById(R.id.tv_video_description);
        llVideoLoader = findViewById(R.id.ll_video_loader);
        LinearLayout bufferLinearLayout =  findViewById(R.id.buffer_layout);
        bufferLinearLayout.setVisibility(View.GONE);
        shareVideoLayout = findViewById(R.id.share_video_layout);
        imgVideoClose =  findViewById(R.id.img_video_close);
        imgVideoStillUrl =  findViewById(R.id.image_video_still);


    }

    /**
     * Method is used to initialize activity components
     */
    @Override
    public void initData() {

        if (videoObject != null) {
            mVideoDescription.setText(videoObject.getVideoLongDescription());
        }
        MusicIntentReceiver myReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);

        if (videoObject != null) {
            if (VaultDatabaseHelper.getInstance(VideoInfoActivity.this).isFavorite(videoObject.getVideoId()))
                imgToggleButton.setImageResource(R.drawable.saved_video_img);
            else
                imgToggleButton.setImageResource(R.drawable.video_save);
            if (imgVideoStillUrl != null)
                Utils.addImageByCaching(this,imgVideoStillUrl, videoObject.getVideoStillUrl());
            tvVideoName.setText(videoObject.getVideoName());
        }

        llVideoLoader.addView(Utils.getInstance().setViewToProgressDialog(this));

        // -------- starting the flurry event of video------
        Map<String, String> articleParams = new HashMap<>();
        articleParams.put(GlobalConstants.KEY_VIDEONAME, videoObject.getVideoName());
        FlurryAgent.logEvent(videoCategory, articleParams, true);

        //Set Video to video view
        if (Utils.isInternetAvailable(this)) {
            String encodedVideoUrl = videoObject.getVideoLongUrl();
            llVideoLoader.setVisibility(View.VISIBLE);
            encodedVideoUrl = encodedVideoUrl.replace("(format=m3u8-aapl)", "(format=m3u8-aapl-v3)");

            Log.d("URL","Media Url : " + encodedVideoUrl);
            videoView.setKeepScreenOn(true);
            videoView.addOnFullscreenListener(this);

            // Keep the screen on during playback
            new KeepScreenOnHandler(videoView, getWindow());

            videoView.setSkin(getResources().getString(R.string.jw_player_css_file_url));

            PlayerConfig playerConfig = new PlayerConfig.Builder()
                    .autostart(false)
                    .captionsEdgeStyle("ec_seek")
                    .skinName("glow")
                    .stretching(PlayerConfig.STRETCHING_EXACT_FIT) //"exact fit"
                    .build();

            videoView.setup(playerConfig);

            PlaylistItem pi = new PlaylistItem.Builder()
                    .file(videoObject.getVideoLongUrl())
                    .image(videoObject.getVideoStillUrl())
                    .build();
            videoView.load(pi);
            Log.d(tag, "Video Length : " + videoView.getDuration());
        } else {
            Utils.showNoConnectionMessage(this);
            finish();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imgVideoStillUrl.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.fadein);
                    imgVideoStillUrl.setAnimation(anim);
                    imgVideoStillUrl.setVisibility(View.GONE);
                }


                if (llVideoLoader.isShown()) {
                    llVideoLoader.setVisibility(View.GONE);
                }

            }
        }, 2000);

    }

    /**
     * Method is used to initialize listeners
     */
    @Override
    public void initListener() {


        //noinspection deprecation
        videoView.addOnErrorListener(new VideoPlayerEvents.OnErrorListener() {
            @Override
            public void onError(String s) {
                if (videoView != null) {
                    videoView.stop();
                }
                if (!Utils.isInternetAvailable(VideoInfoActivity.this)) {
                    showToastMessageForBanner();
                }
                if (videoView != null) {
                    videoCurrentPosition = videoView.getPosition();
                }
                isFirstTimeEntry = true;
            }
        });


        videoView.addOnSetupErrorListener(new VideoPlayerEvents.OnSetupErrorListener() {
            @Override
            public void onSetupError(String s) {

                if (videoView != null) {
                    videoView.stop();

                    videoView.clearFocus();
                    videoView.setSkin(getResources().getString(R.string.jw_player_css_file_url));
                    PlayerConfig playerConfig = new PlayerConfig.Builder()
                            .autostart(false)
                            .stretching(PlayerConfig.STRETCHING_EXACT_FIT)
                            .skinName("glow")
                            .build();

                    videoView.setup(playerConfig);
                    // Load a media source
                    PlaylistItem pi = new PlaylistItem.Builder()
                            .file(videoObject.getVideoLongUrl())
                            .image(videoObject.getVideoStillUrl())
                            .build();

                    videoView.load(pi);
                }
                if (!Utils.isInternetAvailable(VideoInfoActivity.this)) {
                    showToastMessageForBanner();
                }


            }


        });


        videoView.addOnBeforePlayListener(new AdvertisingEvents.OnBeforePlayListener() {
            @Override
            public void onBeforePlay() {

                if (isFirstTimeEntry) {

                    Log.d(tag, "videoCurrentPosition");
                    if (videoView != null) {
                        videoView.seek(videoCurrentPosition);
                    }
                    isFirstTimeEntry = false;
                }


            }
        });

        videoView.addOnCompleteListener(new VideoPlayerEvents.OnCompleteListener() {
            @Override
            public void onComplete() {

                new KeepScreenOnHandler(videoView, getWindow());
                videoView.setSkin(getResources().getString(R.string.jw_player_css_file_url));
                // Load a media source
                String encodedVideoUrl = videoObject.getVideoLongUrl();
                encodedVideoUrl = encodedVideoUrl.replace("(format=m3u8-aapl)", "(format=m3u8-aapl-v3)");
                PlaylistItem pi = new PlaylistItem.Builder()
                        .file(encodedVideoUrl)
                        .image(videoObject.getVideoStillUrl())
                        .build();

                videoView.load(pi);

            }

        });


        imgVideoClose.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (!llVideoLoader.isShown()) {

                        if (videoView != null) {
                            videoView.pause();
                            videoView.stop();

                            videoView = null;
                        }

                        params.putString(FirebaseAnalytics.Param.ITEM_ID, videoObject.getVideoName());
                        params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoObject.getVideoName());
                        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_info");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        shareVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeShareDialog();

            }
        });

        imgToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // firebase analytics favorite video
                params.putString(FirebaseAnalytics.Param.ITEM_ID, videoObject.getVideoName());
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoObject.getVideoName());
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                }
                if (Utils.isInternetAvailable(context)) {
                    if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
                        imgToggleButton.setImageResource(R.drawable.video_save);
                        showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
                    } else {
                        if (VaultDatabaseHelper.getInstance(VideoInfoActivity.this).isFavorite(videoObject.getVideoId())) {
                            isFavoriteChecked = false;
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(0, videoObject.getVideoId());
                            videoObject.setVideoIsFavorite(false);
                            imgToggleButton.setImageResource(R.drawable.video_save);
                        } else {
                            isFavoriteChecked = true;
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(1, videoObject.getVideoId());
                            videoObject.setVideoIsFavorite(true);
                            imgToggleButton.setImageResource(R.drawable.saved_video_img);
                        }

                        mPostTask = new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    postResult = AppController.getInstance().getServiceManager()
                                            .getVaultService().postFavoriteStatus(AppController.getInstance()
                                                    .getModelFacade().getLocalModel().
                                                            getUserId(), videoObject.getVideoId(), videoObject.getPlaylistId(), isFavoriteChecked);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                Log.d("result","Result of POST request : " + postResult);
                                if (isFavoriteChecked)
                                    VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(1, videoObject.getVideoId());
                                else
                                    VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(0, videoObject.getVideoId());
                            }
                        };

                        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                    imgToggleButton.setImageResource(R.drawable.video_save);
                }
            }
        });

        viewPagerRelativeView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                                                             linearLayout.setVisibility(View.GONE);
                                                         }
                                                     }
                                                 }

        );
    }

    /**
     * Method is used to show toast message
     * @param message set the message string
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
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method is used to show toast message for banner
     */
    @SuppressLint("PrivateResource")
    private void showToastMessageForBanner() {
        View includedLayout = findViewById(R.id.llToast);
        final TextView text = includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(GlobalConstants.MSG_NO_CONNECTION);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            animation = AnimationUtils.loadAnimation(this,
                    R.anim.abc_fade_in);

            text.setAnimation(animation);
            text.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    text.setVisibility(View.VISIBLE);
                }
            }, 50);
        }


        new Handler().postDelayed(new Runnable() {
            @SuppressLint("PrivateResource")
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method used for set the screen dimension.
     */
    private void setDimensions() {

      // int measuredHeight = Utils.getScreenHeight(this);
        int aspectHeight = (displayWidth * 9) / 16;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                aspectHeight);
        lp.addRule(RelativeLayout.BELOW, R.id.view_line);
        if (rlVideoLayout != null) {
            rlVideoLayout.setLayoutParams(lp);
        }


    }

    /**
     * Method is used to get data from intent
     */
    private void getIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                videoObject = (VideoDTO) intent
                        .getSerializableExtra(GlobalConstants.VIDEO_OBJ);
                videoCategory = intent
                        .getStringExtra(GlobalConstants.KEY_CATEGORY);

                Uri videoUri = AppController.getInstance().getModelFacade().getLocalModel().getUriUrl();
                String pushNotification = AppController.getInstance().getModelFacade().getLocalModel().getNotificationVideoId();
                if (pushNotification != null) {
                    params.putString(GlobalConstants.NOTIFICATION_OPEN, GlobalConstants.NOTIFICATION_OPEN);
                    mFirebaseAnalytics.logEvent(GlobalConstants.NOTIFICATION_OPEN, params);
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                }
                if (videoObject != null && videoUri == null) {
                    new ShareTwitter().execute();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method is used to show share dialog
     */
    private void makeShareDialog() {

        boolean installedFbApp = checkIfAppInstalled("com.facebook.katana");
        boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");
        View view = findViewById(R.id.sharing_layout);

        if (videoObject.getVideoShortDescription() != null && videoObject.getVideoName() != null) {
            longDescription = videoObject.getVideoShortDescription();
            videoName = videoObject.getVideoName();
            try {
                if (longDescription.length() > 40) {
                    longDescription = longDescription.substring(0, 40);
                }

                if (videoName.length() > 60) {
                    longDescription = longDescription.substring(0, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        linearLayout = view.findViewById(R.id.social_sharing_linear_layout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.sliding_up_dialog);
                linearLayout.setAnimation(animation);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }, 500);


        flatButtonFacebook = view.findViewById(R.id.facebookShare);
        flatButtonTwitter = view.findViewById(R.id.twitterShare);
        facebookShareView = view.findViewById(R.id.facebookShareView);
        twitterShareView = view.findViewById(R.id.twitterShareView);

        twitterLoginButton = view.findViewById(R.id.twitter_login_button_share);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                try {

                    if (videoObject.getVideoName() != null && videoObject.getVideoShortDescription() != null) {

                        Intent intent = new TweetComposer.Builder(VideoInfoActivity.this)
                                .text(videoName + "\n" + longDescription + "\n\n")
                                .url(new URL(videoObject.getVideoSocialUrl()))
                                .image(imageUri)
                                .createIntent();
                        startActivityForResult(intent, 100);
                    } else if (videoObject.getVideoName() != null) {

                        Intent intent = new TweetComposer.Builder(VideoInfoActivity.this)
                                .text(videoName + "\n" + longDescription + "\n\n")
                                .url(new URL(videoObject.getVideoSocialUrl()))
                                .image(imageUri)
                                .createIntent();

                        startActivityForResult(intent, 100);

                    }


                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void failure(TwitterException e) {
                showToastMessage(GlobalConstants.TWITTER_LOGIN_CANCEL);
            }

        });

        if (!installedFbApp) {

            facebookShareView.setVisibility(View.VISIBLE);
            flatButtonFacebook.setVisibility(View.GONE);
            facebookShareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView != null) {
                        videoView.pause();
                    }
                    showConfirmSharingDialog(getResources().getString(R.string.facebook_install_msg), getResources().getString(R.string.facebook_url));
                }
            });


        } else {
            facebookShareView.setVisibility(View.GONE);
            flatButtonFacebook.setVisibility(View.VISIBLE);
            flatButtonFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                    }
                    if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
                        showConfirmLoginDialog(GlobalConstants.SHARE_MESSAGE);
                    } else if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
                        if (videoObject.getVideoSocialUrl() != null) {
                            if (videoObject.getVideoSocialUrl().length() == 0) {
                                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                            } else {
                                shareVideoUrlFacebook(videoObject.getVideoSocialUrl(), context);
                            }
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                        }
                    } else {
                        showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                    }
                }
            });
        }
        if (!installedTwitterApp) {

            twitterShareView.setVisibility(View.VISIBLE);
            flatButtonTwitter.setVisibility(View.GONE);
            twitterShareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView != null) {
                        videoView.pause();
                    }

                    showConfirmSharingDialog(getResources().getString(R.string.twitter_install_msg), getResources().getString(R.string.twitter_url));
                }
            });


        } else {

            twitterShareView.setVisibility(View.GONE);
            flatButtonTwitter.setVisibility(View.VISIBLE);
            flatButtonTwitter.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    twitterSharingData();
                }
            });
        }

    }

    /**
     * Method is used to share on twitter
     */
    private void twitterSharingData() {
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }

        Log.d("twitter","twitter sharing");
        if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
            showConfirmLoginDialog(GlobalConstants.SHARE_MESSAGE);
        } else if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
            if (videoObject.getVideoSocialUrl() != null) {
                if (videoObject.getVideoSocialUrl().length() == 0) {
                    showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                } else {

                    TwitterSession session = Twitter.getSessionManager().getActiveSession();

                    if (session == null) {
                        twitterLoginButton.performClick();
                    } else {
                        try {

                            if (videoObject.getVideoName() != null && videoObject.getVideoShortDescription() != null) {
                                try {

                                    TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                            .text(videoName + "\n" + longDescription + "\n\n")
                                            .url(new URL(videoObject.getVideoSocialUrl()))
                                            .image(imageUri);

                                    builder.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (videoObject.getVideoName() != null) {

                                try {

                                    TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                            .text(videoName + "\n" + longDescription + "\n\n")
                                            .url(new URL(videoObject.getVideoSocialUrl()))
                                            .image(imageUri);

                                    builder.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }


    /**
     * Method to show login dialog
     * @param message set the message.
     */
    private void showConfirmLoginDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            stopService(new Intent(VideoInfoActivity.this, TrendingFeaturedVideoService.class));

                            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();

                            SharedPreferences prefs = context.getSharedPreferences(
                                    getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                            prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();

                            Intent intent = new Intent(context, LoginEmailActivity.class);
                            context.startActivity(intent);
                            context.finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        positiveButton.setTextColor(ContextCompat.getColor(VideoInfoActivity.this, R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(VideoInfoActivity.this, R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method is used to show confirmation share dialog
     * @param message set the message
     * @param playStoreUrl set the play store url.
     */
    private void showConfirmSharingDialog(String message, final String playStoreUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Install",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                            linearLayout.setVisibility(View.GONE);
                        }

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
        positiveButton.setTextColor(ContextCompat.getColor(VideoInfoActivity.this, R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(VideoInfoActivity.this, R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Method is used to share video url on facebook
     * @param videoUrl videoUrl of particular video
     * @param context reference of the Activity
     */
    private void shareVideoUrlFacebook(String videoUrl, final Activity context) {
        try {
            final FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {

                    GlobalConstants.IS_SHARING_ON_FACEBOOK = false;
                }

                @Override
                public void onError(FacebookException error) {
                    showToastMessage(GlobalConstants.FACEBOOK_SHARING_CANCEL);
                    GlobalConstants.IS_SHARING_ON_FACEBOOK = false;
                }

                @Override
                public void onSuccess(Sharer.Result result) {
                    boolean installed = checkIfFacebookAppInstalled("com.facebook.android");
                    if (!installed)
                        installed = checkIfFacebookAppInstalled("com.facebook.katana");
                    if (!installed)
                        showToastMessage(GlobalConstants.FACEBOOK_POST_SUCCESS_MESSAGE);
                    GlobalConstants.IS_SHARING_ON_FACEBOOK = false;

                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                    }

                }
            };

            GlobalConstants.IS_SHARING_ON_FACEBOOK = true;

            callbackManager = CallbackManager.Factory.create();

            ShareDialog shareDialog = new ShareDialog(context);
            shareDialog.registerCallback(
                    callbackManager,
                    shareCallback);

            boolean canPresentShareDialog = ShareDialog.canShow(
                    ShareLinkContent.class);

            Profile profile = Profile.getCurrentProfile();

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(videoUrl))
                    .build();

            if (profile != null) {
                if (canPresentShareDialog) {
                    shareDialog.show(linkContent);
                } else if (profile != null && hasPublishPermission()) {
                    ShareApi.share(linkContent, shareCallback);
                }
            } else {
                loginWithFacebook();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFullscreen(boolean fullscreen) {
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (fullscreen) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }


    private static boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    /**
     * Method is used for facebook login
     */
    private void loginWithFacebook() {

        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));
    }

    /**
     * Method is used to check facebook app is installed or not
     * @param uri set the string url
     * @return the boolean value true or false.
     */
    private boolean checkIfFacebookAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            ApplicationInfo ai = getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    /**
     * Method is used to check facebook or twitter app is installed on device or not
     * @param uri set the string url.
     * @return the boolean value true or false.
     */
    private boolean checkIfAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            ApplicationInfo ai = getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    /**
     * Method is used for facebook login
     */
    private void initializeFacebookUtil() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d("videoInfo", "Facebook login successful");
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert();
                    }

                    private void showAlert() {
                        showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);

                    }
                });

        @SuppressWarnings("UnusedAssignment") ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null && GlobalConstants.IS_SHARING_ON_FACEBOOK) {
                    shareVideoUrlFacebook(videoObject.getVideoSocialUrl(), context);
                }
            }
        };
    }


    private Bitmap bitmap;

    /**
     * Async task is used for share functionality on twitter
     */
    private class ShareTwitter extends AsyncTask<Void, Void, Uri> {

        protected Uri doInBackground(Void... arg0) {
            try {
                if (videoObject.getVideoStillUrl() != null) {
                    InputStream is = new URL(videoObject.getVideoStillUrl().trim()).openStream();
                    try {
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                    File externalFilesDir = getExternalFilesDir(null);
                    if (externalFilesDir != null) {
                        String STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/abc/";
                        File storeDirectory = new File(STORE_DIRECTORY);
                        Log.e("Storage Dir", "" + storeDirectory);
                        if (!storeDirectory.exists()) {
                            boolean success = storeDirectory.mkdirs();
                            if (!success) {
                                Log.e(tag, "failed to create file storage directory.");
                                return null;
                            }
                        }
                        try {
                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);
                            String fname = "Image-" + n + ".jpg";
                            File file = new File(storeDirectory, fname);
                            FileOutputStream out = new FileOutputStream(file);
                            try {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                            }
                            out.flush();
                            out.close();
                            imageUri = Uri.parse(file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(tag, "failed to create file storage directory, getExternalFilesDir is null.");

                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return imageUri;
        }

        protected void onPostExecute(Uri result) {
            try {
                imageUri = result;
                @SuppressWarnings("deprecation") ProgressDialog pDialog = new ProgressDialog(VideoInfoActivity.this);
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                    sharingImageOnTwitter();
                }
                Log.d("result","imageUri value ");
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                    Log.d(tag,"bitmap value null");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Method to share image on twitter
     */
    private void sharingImageOnTwitter() {
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
        if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
            if (videoView != null) {
                videoView.pause();
            }
            showConfirmLoginDialog(GlobalConstants.SHARE_MESSAGE);

        } else if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
            if (videoObject.getVideoSocialUrl() != null) {
                if (videoObject.getVideoSocialUrl().length() == 0) {
                    showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                } else {
                    //gk videoView.pause();
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    if (session == null) {
                        twitterLoginButton.performClick();
                    } else {
                        try {

                            if (videoObject.getVideoName() != null && videoObject.getVideoShortDescription() != null) {
                                try {

                                    TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                            .text(videoName + "\n" + longDescription + "\n\n")
                                            .url(new URL(videoObject.getVideoSocialUrl()))
                                            .image(imageUri);

                                    builder.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (videoObject.getVideoName() != null) {

                                try {

                                    TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                            .text(videoName + "\n" + longDescription + "\n\n")
                                            .url(new URL(videoObject.getVideoSocialUrl()))
                                            .image(imageUri);

                                    builder.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }


    /**
     * Broadcast Receiver is used to check headset is plugged or not
     */
    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(tag, "Headset is unplugged");
                        videoView.pause(true);
                        break;
                    case 1:
                        Log.d(tag, "Headset is plugged");

                        break;
                    default:
                        Log.d(tag, "I have no idea what the headset state is");
                }
            }
        }
    }
}