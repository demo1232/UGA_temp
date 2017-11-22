package com.ncsavault.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import applicationId.R;


/**
 * This class is tool class of the app.
 * We have made generalize method inside this class
 * This is singleton class
 */

public class Utils {


    private static Utils utilInstance;

    // Async task
    private AsyncTask<Void, Void, Void> mRegisterTask;
    private SharedPreferences prefs;
    private Animation animation;
    private String refreshedToken;


    /**
     * Method used for make it single instance.
     * @return the instance of the class
     */
    public static Utils getInstance() {

        if (utilInstance == null) {
            utilInstance = new Utils();
        }

        return utilInstance;
    }

    /**
     * Check for any type of internet connection
     *
     * @param ctx Activity context
     * @return boolean
     */
    public static boolean isInternetAvailable(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void showNoConnectionMessage(Context ctx) {
        Toast.makeText(ctx, GlobalConstants.MSG_NO_CONNECTION, Toast.LENGTH_SHORT).show();
    }

    //method to check whether the bottom navigation bar exists
    public static boolean hasNavBar(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    /**
     * Method used for get the getNavBarStatusAndHeight
     * @param context the reference of the Context
     * @return the nav status height.
     */
    public static int getNavBarStatusAndHeight(Context context) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");

        if (!hasMenuKey && !hasBackKey) {
            result = getNavigationBarHeight(context);
        } else if (id > 0 && context.getResources().getBoolean(id)) {
            result = getNavigationBarHeight(context);
        } else if ((!(hasBackKey && hasHomeKey))) {        // Condition worked for all other devices
            result = getNavigationBarHeight(context);
        }
        return result;
    }

    /**
     * Method used for get the getNavBarStatusAndHeight
     * @param context the reference of the Context
     * @return the nav status height.
     */
    private static int getNavigationBarHeight(Context context) {
        //The device has a navigation bar
        Resources resources = context.getResources();

        int orientation = context.getResources().getConfiguration().orientation;
        int resourceId;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
        } else {
            resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
        }

        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Method used for set the banner image and show the home tab and playlist screen
     * @param bannerCacheableImageView reference of the Image view
     * @param url Url of the image.
     */
    public static void addImageByCaching(Activity activity,final ImageView bannerCacheableImageView, String url) {

        try {
            bannerCacheableImageView.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(url)
                    .placeholder(R.drawable.vault)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            bannerCacheableImageView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            bannerCacheableImageView.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(bannerCacheableImageView);
        } catch (Exception error) {
            error.printStackTrace();
            Log.e("Utils", "Exception Saved " + error.getMessage());
        }

    }

    /**
     * Method used for register the token on server using GCM
     * @param mActivity Instance of the activity.
     */
    public void registerWithGCM(final Activity mActivity) {
        prefs = mActivity.getSharedPreferences(mActivity.getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);

        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.i("Utils", "Device Registration Id : = " + refreshedToken);
                @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(mActivity.getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String result = AppController.getInstance().getServiceManager().getVaultService().sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                        refreshedToken, deviceId, true,0);
                if (result != null) {
                    Log.d("Response", "Response from server after registration : "
                            + result);
                    if (result.toLowerCase().contains("success")) {

                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, true).apply();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRegisterTask = null;
            }
        };

        mRegisterTask.execute();

    }
    /**
     * Method used for unregister the token on server using GCM
     * @param mActivity Instance of the activity.
     */
    public void unRegisterWithGCM(final Activity mActivity) {
        prefs = mActivity.getSharedPreferences(mActivity.getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.i("Utils", "Device Registration Id : = " + refreshedToken);
                @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(mActivity.getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String result = AppController.getInstance().getServiceManager().getVaultService().sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                        refreshedToken, deviceId, false,0);
                if (result != null) {
                    Log.d("Response", "Response from server after registration : "
                            + result);
                    if (result.toLowerCase().contains("success")) {
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, false).apply();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRegisterTask = null;
            }
        };

        mRegisterTask.execute();

    }

    /**
     * Method used for the decode Uri and return the Bitmap.
     * @param selectedImage image url.
     * @param context the reference of the Context.
     * @return Bitmap image.
     * @throws FileNotFoundException throws exception
     */
    public Bitmap decodeUri(Uri selectedImage, Activity context) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 340;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }

    /**
     * Method used for rotate the image
     * @param bitmap set the Image bitmap.
     * @param selectedImageUri set image uri.
     * @param context the reference of the Context.
     * @param sdImageMainDirectory save the image in directory.
     * @return the bitmap image.
     */
    public Bitmap rotateImageDetails(Bitmap bitmap, Uri selectedImageUri, Activity context, File sdImageMainDirectory) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(
                    getRealPathFromURI(selectedImageUri, context));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String orientString = null;
        if (exif != null) {
            orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        }
        int orientation = orientString != null ? Integer.parseInt(orientString)
                : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationAngle = 90;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationAngle = 180;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationAngle = 270;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Log.i("Image Details",
                "Image Camera Width: " + rotatedBitmap.getWidth() + " Height: "
                        + rotatedBitmap.getHeight());
        File f = new File(sdImageMainDirectory.getPath());

        try {
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.gc();
        return rotatedBitmap;
    }

    /**
     * Method used for get the real path from URI.
     * @param contentURI set the content URI.
     * @param context the reference of Context.
     * @return string value.
     */
    private static String getRealPathFromURI(Uri contentURI, Context context) {
        String path = contentURI.getPath();
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } catch (Exception e) {
            return path;
        }
        return path;
    }

    /**
     * This method used for fetching all data for banner from Server
     */
    public static boolean loadDataFromServer(final Context context) {

        try {
            ArrayList<TabBannerDTO> arrayListBanner = new ArrayList<>();

            arrayListBanner.addAll(AppController.getInstance().getServiceManager().getVaultService().getAllTabBannerData());

            ArrayList<String> lstUrls = new ArrayList<>();
            @SuppressWarnings("UnusedAssignment") File imageFile;
            for (TabBannerDTO bDTO : arrayListBanner) {
                TabBannerDTO localBannerData = VaultDatabaseHelper.getInstance(context).getLocalTabBannerDataByTabId(bDTO.getTabId());
                if (localBannerData != null) {
                    if ((localBannerData.getBannerModified() != bDTO.getBannerModified()) || (localBannerData.getBannerCreated() != bDTO.getBannerCreated())) {
                        VaultDatabaseHelper.getInstance(context).updateBannerData(bDTO);
                    }
                    if (localBannerData.getTabDataModified() != bDTO.getTabDataModified()) {
                        VaultDatabaseHelper.getInstance(context).updateTabData(bDTO);
                        if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.FEATURED).toLowerCase())) {
                            VaultDatabaseHelper.getInstance(context).removeRecordsByTab(GlobalConstants.OKF_FEATURED);
                            lstUrls.add(GlobalConstants.FEATURED_API_URL);
                        } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.GAMES).toLowerCase())) {
                            VaultDatabaseHelper.getInstance(context).removeRecordsByTab(GlobalConstants.OKF_GAMES);
                            lstUrls.add(GlobalConstants.GAMES_API_URL);
                        } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.PLAYERS).toLowerCase())) {
                            VaultDatabaseHelper.getInstance(context).removeRecordsByTab(GlobalConstants.OKF_PLAYERS);
                            lstUrls.add(GlobalConstants.PLAYER_API_URL);
                        } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.OPPONENTS).toLowerCase())) {
                            VaultDatabaseHelper.getInstance(context).removeRecordsByTab(GlobalConstants.OKF_OPPONENT);
                            lstUrls.add(GlobalConstants.OPPONENT_API_URL);
                        } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.COACHES_ERA).toLowerCase())) {
                            VaultDatabaseHelper.getInstance(context).removeRecordsByTab(GlobalConstants.OKF_COACH);
                            lstUrls.add(GlobalConstants.COACH_API_URL);
                        }
                    }
                } else {
                    VaultDatabaseHelper.getInstance(context).insertTabBannerData(bDTO);
                }
            }
            if (lstUrls.size() == 0) {
                int count = VaultDatabaseHelper.getInstance(context).getTabBannerCount();
                if (count > 0) {
                    lstUrls.add(GlobalConstants.FEATURED_API_URL);
                    lstUrls.add(GlobalConstants.GAMES_API_URL);
                    lstUrls.add(GlobalConstants.PLAYER_API_URL);
                    lstUrls.add(GlobalConstants.OPPONENT_API_URL);
                    lstUrls.add(GlobalConstants.COACH_API_URL);
                }
            }
            AppController.getInstance().getModelFacade().getLocalModel().setAPI_URLS(lstUrls);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * Method used to get the deep linking from banner
     * @param actionUrl set the url of banner.
     * @return Hash map object.
     */
    @SuppressWarnings("unchecked")
    public HashMap getVideoInfoFromBanner(String actionUrl) {
        HashMap videoMap = new HashMap();
        actionUrl = actionUrl.substring(5);
        String[] videoParams = actionUrl.split(";");
        if (videoParams.length > 0) {
            for (String videoParam : videoParams) {

                String[] values = videoParam.split("=");
                if (values[0].toLowerCase().contains("tabid"))
                    videoMap.put("TabId", values[1]);
                else if (values[0].toLowerCase().contains("tabkeyword"))
                    videoMap.put("TabKeyword", values[1]);
                else if (values[0].toLowerCase().contains("videoid"))
                    videoMap.put("VideoId", values[1]);
                else if (values[0].toLowerCase().contains("videoname"))
                    videoMap.put("VideoName", values[1]);
                else if (values[0].toLowerCase().contains("playlistid"))
                    videoMap.put("PlaylistId", values[1]);
            }
        }
        Log.d("Video", "Video Hash Map Length : " + videoMap.size());
        return videoMap;
    }

    /**
     * Method used for hiding the key board from device.
     * @param activity reference of Activity class.
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * Method used for show the custom progress dialog
     * @param context reference of Activity class.
     * @return view of the progress bar.
     */
    public View setViewToProgressDialog(Activity context) {
        @SuppressLint("InflateParams") View view = context.getLayoutInflater().inflate(R.layout.progress_bar, null);
        ProgressBar pBar = view.findViewById(R.id.progress_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            pBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            //noinspection deprecation
            pBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.circle_progress_bar));
        }

        ImageView imgView = view.findViewById(R.id.img_circular);

        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width;


        Display d = w.getDefaultDisplay();
        //noinspection deprecation
        width = d.getWidth();


        int dimension = (int) (width * 0.10);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            dimension = (int) (width * 0.15);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dimension, dimension);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgView.setLayoutParams(lp);
        imgView.setAdjustViewBounds(true);
        imgView.setImageBitmap(setCircularBitmap(context));

        lp = new RelativeLayout.LayoutParams(dimension + 35, dimension + 35);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp = new RelativeLayout.LayoutParams(dimension + 45, dimension + 45);
        }

        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        pBar.setLayoutParams(lp);

        return view;
    }

    /**
     * Method used to make it bitmap image circular
     * @param context reference of activity class.
     * @return bitmap object.
     */
    private Bitmap setCircularBitmap(Activity context) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.vault_logo_glare);
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();
        return output;
    }

    /**
     * hiding keyboard
     */
    public void getHideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            Log.d("onResume", "onResume getHideKeyboard111");
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Method used for show the toast message
     * @param activity reference of activity class
     * @param message set the toast message
     * @param viewId set the view id.
     */
    @SuppressLint("PrivateResource")
    public void showToastMessage(final Activity activity, String message, View viewId) {
        //View includedLayout = findViewById(R.id.llToast);

        final TextView text = viewId.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(activity,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("PrivateResource")
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(activity,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method used for convert second to HH:MM:SS
     * @param millis set the mills
     * @return the string.
     */
    public static String convertSecondsToHMmSs(long millis) {


        String duration;
        int min;
        int sec;
        String hrStr;
        String mnStr;
        String secStr;
        int hr = (int) ((millis / (1000 * 60 * 60)) % 24);
        if (hr > 0) {
            min = (int) ((millis / (1000 * 60)) % 60);
            sec = (int) (millis / 1000) % 60;
            hrStr = (hr < 10 ? "0" : "") + hr;
            mnStr = (min < 10 ? "0" : "") + min;
            secStr = (sec < 10 ? "0" : "") + sec;
            duration = hrStr + ":" + mnStr + ":" + secStr;
        } else {
            min = (int) ((millis / (1000) / 60));
            sec = (int) (millis / 1000) % 60;
            mnStr = (min < 10 ? "0" : "") + min;
            secStr = (sec < 10 ? "0" : "") + sec;
            duration = mnStr + ":" + secStr;
        }
        return duration;
    }

    /**
     * Method used for set the font on particular screen.
     * @param context the reference of Activity class.
     */
    public void setAppName(Activity context) {
        TextView textViewAlabama = context.findViewById(R.id.textview_alabama);
        TextView textViewVault = context.findViewById(R.id.textview_vault);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface faceNormal = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
        textViewAlabama.setTypeface(faceNormal);
        textViewVault.setTypeface(face);
    }


    /**
     * Method used for get the device dimensions
     * @return screen Width.
     */
    public static int getScreenDimensions(Activity activity) {
        int displayWidth;
        Point size = new Point();
        Display d = activity.getWindowManager().getDefaultDisplay();
        d.getSize(size);
        displayWidth = size.x;
        Log.d("Utils", "Display Width" + displayWidth);
        return displayWidth;
    }

    /**
     * Method used for get the screen height.
     * @return screen height.
     */
    public static int getScreenHeight(Activity activity) {
        int displayHeight;
        Display d = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        displayHeight = size.y;
        return displayHeight;
    }
}
