package com.ncsavault.app;

import android.app.Application;
import android.os.Handler;

import com.flurry.android.FlurryAgent;
import com.ncsavault.controllers.AppController;
import com.ncsavault.utils.FontsOverride;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import applicationId.R;
import io.fabric.sdk.android.Fabric;


/**
 * This is main class of all the class's and Activity
 * We are initializing fonts,twitter, fabric and flurry
 */
public class AndroidApplication extends Application {

    private final Handler handler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        AppController.getInstance().setApplication(this);
        AppController.getInstance().initialize();


        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_consumer_key),
                getResources().getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig)/*,new Crashlytics()*/);

        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        Fabric.with(this, new TweetComposer());
        FlurryAgent.init(this, getResources().getString(R.string.flurry_key));

        FontsOverride.overrideFont(this);

    }


}
