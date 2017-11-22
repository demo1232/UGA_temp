package com.ncsavault.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;

import java.util.ArrayList;

import applicationId.R;

/**
 * Class used for create setter and getter if you want to used whole the app.
 */
public class LocalModel implements IModel {


    private String firstName;
    private String lastName;
    private String emailId;
    private String videoId;
    private User user;
    private boolean isOverride;
    private boolean isTwitterLogin;
    private long tabId;

    /**
     * @return Gets the value of isDefaultLogin and returns isDefaultLogin
     */
    public boolean isDefaultLogin() {
        return isDefaultLogin;
    }

    /**
     * Sets the isDefaultLogin
     * You can use getDefaultLogin() to get the value of isDefaultLogin
     */
    public void setDefaultLogin(boolean defaultLogin) {
        isDefaultLogin = defaultLogin;
    }

    private boolean isDefaultLogin;
    @SuppressWarnings("unused")
   private Bitmap selectedBitmap;
    public boolean isBannerActivated() {
        return isBannerActivated;
    }

    public void setBannerActivated(boolean bannerActivated) {
        isBannerActivated = bannerActivated;
    }

    private boolean isBannerActivated;

    @SuppressWarnings("unused")
    public long getTabId() {
        return tabId;
    }

    public void setTabId(long tabId) {
        this.tabId = tabId;
    }

    public String getNotificationVideoId() {
        return notificationVideoId;
    }

    public void setNotificationVideoId(String notificationVideoId) {
        this.notificationVideoId = notificationVideoId;
    }

    private String notificationVideoId;

    public boolean isGoogleLogin() {
        return isGoogleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        isGoogleLogin = googleLogin;
    }

    public boolean isFacebookLogin() {
        return isFacebookLogin;
    }

    public void setFacebookLogin(boolean facebookLogin) {
        isFacebookLogin = facebookLogin;
    }

    private boolean isGoogleLogin;
    private boolean isFacebookLogin;
    private Bitmap selectImageBitmap;

    public Bitmap getSelectImageBitmap() {
        return selectImageBitmap;
    }

    public void setSelectImageBitmap(Bitmap selectImageBitmap) {
        this.selectImageBitmap = selectImageBitmap;
    }


    public boolean isOverride() {
        return isOverride;
    }

    public void setOverride(boolean override) {
        isOverride = override;
    }

    public boolean isTwitterLogin() {
        return isTwitterLogin;
    }

    public void setTwitterLogin(boolean twitterLogin) {
        isTwitterLogin = twitterLogin;
    }


    @SuppressWarnings("UnusedAssignment")
    public void setRegisteredEmailIdForgot(String registeredEmailIdForgot) {
        String registeredEmailIdForgot1 = registeredEmailIdForgot;
        Log.d("registeredEmailId","registeredEmailIdForgot1"+registeredEmailIdForgot1);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressWarnings("UnusedAssignment")
    public void setRegisteredEmailIdForgot() {
        boolean isRegisteredEmailIdForgot = true;
    }


    public Uri getUriUrl() {
        return uriUrl;
    }

    public void setUriUrl(Uri uriUrl) {
        this.uriUrl = uriUrl;
    }

    private Uri uriUrl;

    private ArrayList<String> API_URLS = new ArrayList<>();

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    private String videoUrl;


    public LocalModel()
    {
    }

    @SuppressWarnings("unused")
    public String getRegisterEmailId() {
        return registerEmailId;
    }

    public void setRegisterEmailId(String registerEmailId) {
        this.registerEmailId = registerEmailId;
    }


    private String registerEmailId;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @SuppressWarnings("unused")
    public String getLastName() {
        return lastName;
    }

    @SuppressWarnings("unused")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @SuppressWarnings("unused")
    public String getFirstName() {
        return firstName;
    }

    @SuppressWarnings("unused")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    @SuppressWarnings("unused")
    public ArrayList<String> getAPI_URLS() {
        return API_URLS;
    }

    public void setAPI_URLS(ArrayList<String> API_URLS) {
        this.API_URLS = API_URLS;
    }


    /**
     * Method used for store the first name and last name on local data base.
     * @param firstName set the first name.
     * @param lastName set the last name.
     */
    @SuppressWarnings("unused")
    public void storeNameAndName(String firstName, String lastName)
    {
        SharedPreferences pref = AppController.getInstance().getApplication().
        getSharedPreferences(AppController.getInstance().getApplicationContext().getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME,firstName ).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, lastName).apply();
    }

    /**
     * Method used for store the email id on local data base.
     * @param emailId set the email id.
     */
    public void storeEmailId(String emailId)
    {
        SharedPreferences pref = AppController.getInstance().getApplication().
        getSharedPreferences(AppController.getInstance().
        getApplicationContext().getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL,emailId).apply();
    }

    /**
     * Method used to get the device id.
     * @return the value of device id in a string.
     */
    @SuppressLint("HardwareIds")
    @SuppressWarnings("unused")
    public String getDeviceId(){
        /**/
        return Settings.Secure.getString(AppController.getInstance()
                        .getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * Method used for ge user id from local data base.
     * @return the user id
     */
    public long getUserId(){
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        return pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
    }

    /**
     * Method used for get the first name from local data base.
     * @return the value of first name
     */
    public String getFName(){
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        return pref.getString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, "");
    }

    /**
     * Method used for get the last name from local data base.
     * @return the value of last name.
     */
    public String getLName(){
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        return pref.getString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, "");
    }

    /**
     * Method used for get the email address from local data base.
     * @return the value of email address.
     */
    public String getEmailAddress(){
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        return pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
    }


    /**
     * Method used for get the mail chimp register user from local data base.
     * @return the value of mail chimp register user.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getMailChimpRegisterUser() {
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        return pref.getBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP,false);
    }

    /**
     * Method used for set the mail chimp register user from local data base.
     * @param registerUserValue the registerUserValue.
     */
    public void setMailChimpRegisterUser(boolean registerUserValue) {
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        pref.edit().putBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP, registerUserValue).apply();
    }


    /**
     * Method used for store the all user detail in local data base.
     * @param userDto set the user detail
     */
    public void storeUserDataInPreferences(User userDto){
        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, userDto.getUserID()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, userDto.getEmailID()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, userDto.getUsername()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, userDto.getFname()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, userDto.getLname()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_BIO_TEXT, userDto.getBiotext()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_IMAGE_URL, userDto.getImageurl()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_GENDER, userDto.getGender()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FLAG_STATUS, userDto.getFlagStatus()).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_PASSWORD, userDto.getPasswd()).apply();
        pref.edit().putInt(GlobalConstants.PREF_VAULT_USER_AGE, userDto.getAge()).apply();
        //pref.edit().putString(GlobalConstants.PREF_JOIN_MAIL_CHIMP, userDto.getIsRegisteredUser()).commit();

    }

    /**
     * Method used for get the user detail from local data base.
     * @return user detail
     */
    public User getUserData() {
        User userDto = new User();
        SharedPreferences pref = AppController.getInstance().getApplication().
                getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        userDto.setUserID(pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0));
        userDto.setUsername(pref.getString(GlobalConstants.PREF_VAULT_USER_NAME, ""));
        userDto.setEmailID(pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, ""));
        userDto.setFname(pref.getString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, ""));
        userDto.setLname(pref.getString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, ""));
        userDto.setBiotext(pref.getString(GlobalConstants.PREF_VAULT_USER_BIO_TEXT, ""));
        userDto.setGender(pref.getString(GlobalConstants.PREF_VAULT_USER_GENDER, ""));
        userDto.setAge(pref.getInt(GlobalConstants.PREF_VAULT_USER_AGE, 0));
        userDto.setImageurl(pref.getString(GlobalConstants.PREF_VAULT_USER_IMAGE_URL, ""));
        userDto.setFlagStatus(pref.getString(GlobalConstants.PREF_VAULT_USER_FLAG_STATUS, ""));
        userDto.setPasswd(pref.getString(GlobalConstants.PREF_VAULT_USER_PASSWORD, ""));
        userDto.setAppID(Integer.parseInt(AppController.getInstance().getApplicationContext().getResources()
                .getString(R.string.app_id)));
        //userDto.setIsRegisteredUser(pref.getString(GlobalConstants.PREF_JOIN_MAIL_CHIMP, ""));
        return userDto;
    }

    /**
     * Method used for update the user detail in local data base.
     * @param Username set username
     * @param FName set first name
     * @param LName set last name
     * @param BioText set Bio Text
     * @param ImageUrl set the image url
     */
    @SuppressWarnings("unused")
    public void updateUserData(String Username, String FName, String LName, String BioText, String ImageUrl){
        // update 5 fields in the preferences Username, FName, LName, BioText, ImageUrl
        SharedPreferences pref = AppController.getInstance().getApplication().
                getSharedPreferences(AppController.getInstance().
                        getApplicationContext().getResources().getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, Username).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, FName).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, LName).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_BIO_TEXT, BioText).apply();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_IMAGE_URL, ImageUrl).apply();
    }


    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }
}
