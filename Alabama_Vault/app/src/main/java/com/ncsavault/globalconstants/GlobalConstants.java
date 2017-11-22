package com.ncsavault.globalconstants;

import android.support.v4.app.Fragment;

/**
 * Class used for set the constant value here.
 */
public class GlobalConstants {


    public static final String FEEDBACK_ASSIGNEE_NAME = "Reid Shapiro";
    public static final String CLIP_REQUEST_ASSIGNEE_NAME = "Reid Shapiro";
    public static final String SUPPORT_ASSIGNEE_NAME = "The Vault App";
    public static final String ASSIGNEE_STATUS = "upcoming";
    // ------- Key's-------
    public static final String KEY_VIDEONAME = "name";
    public static final String KEY_CATEGORY = "category";

    //Staging Url
     private static final String BASE_URL = "http://syncroapi-staging.ncsavault.com/api";

    //new Production Url
    //public static final String BASE_URL = "http://syncroapi.ncsavault.com/api";

    //new Temp Url
   // public static final String BASE_URL = "http://13.93.160.190/api";

    public static final String PUSH_REGISTER_URL = BASE_URL + "/MobileUsers/PostPushData";

    //Asana API call needed parameters
    public static final String ASANA_TASK_API_URL = "https://app.asana.com/api/1.0/tasks";
    public static final String ASANA_TAG_API_URL = "https://app.asana.com/api/1.0/tasks/";

    //Staging API Urls
    public static final String FEATURED_API_URL = BASE_URL + "/playlist/GetFeaturedPlaylist?";
    public static final String GAMES_API_URL = BASE_URL + "/playlist/GetGames?";
    public static final String PLAYER_API_URL = BASE_URL + "/playlist/GetPlayer?";
    public static final String OPPONENT_API_URL = BASE_URL + "/playlist/GetOpponent?";
    public static final String COACH_API_URL = BASE_URL + "/playlist/GetCoach?";
    public static final String FAVORITE_API_URL = BASE_URL + "/FavoriteTab/GetFavorites?";
    public static final String FAVORITE_POST_STATUS_URL = BASE_URL + "/FavoriteTab/PostFavoriteData";
    public static final String GET_ALL_TAB_BANNER_DATA_URL = BASE_URL + "/NavigationTab/ListTabsInfo";
    public static final String GET_TAB_BANNER_DATA_URL = BASE_URL + "/NavigationTab/ListTabsInfo";
    public static final String GET_VIDEO_DATA_FROM_BANNER = BASE_URL + "/NavigationTab/ListVideoPlaylistInfo";
    public static final String SOCIAL_SHARING_INFO = BASE_URL + "/FavoriteTab/PostSocialSharingInfo";
    public static final String GET_VIDEO_DATA = BASE_URL + "/Playlist/GetVideo";

    //User specific API calls
    public static final String VALIDATE_EMAIL_URL = BASE_URL + "/MobileUsers/validateEmail?emailID=";
    public static final String VALIDATE_USERNAME_URL = BASE_URL + "/MobileUsers/IsUserAvailable?UserName=";
    public static final String POST_USER_DATA_URL = BASE_URL + "/MobileUsers/PostMobileUserData";
    public static final String POST_UPDATED_USER_DATA_URL = BASE_URL + "/MobileUsers/PostProfileUpdate";
    public static final String VALIDATE_USER_CREDENTIALS_URL = BASE_URL + "/MobileUsers/CheckCredentials";
    public static final String GET_USER_DATA_URL = BASE_URL + "/MobileUsers/getUserProfileData";
    public static final String VALIDATE_SOCIAL_LOGIN_URL = BASE_URL + "/MobileUsers/ValidateEmailAndStatus";
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "/MobileUsers/PostChangePass";
    public static final String POST_MAIL_CHIMP_DATA = BASE_URL + "/MobileUsers/PostMailChimpData";
    public static final String FORGOT_PASSWORD_URL = BASE_URL + "/MobileUsers/SendForgotPasswordEmail";
    public static final String CONFIRM_PASSWORD_URL = BASE_URL + "/MobileUsers/ResetPassword";
    public static final String SOCIAL_LOGIN_EXIST_URL = BASE_URL + "/MobileUsers/IsSocialLoginExists";
    public static final String CATEGORIES_TAB_URL = BASE_URL + "/playlist/GetCategoriesData?";
    public static final String CATEGORIES_PLAYLIST_URL = BASE_URL + "/playlist/GetPlayListInfo?";
    public static final String PLAYLIST_VIDEO_URL = BASE_URL + "/playlist/GetVideoInfo?";
    public static final String VIDEO_LIST_BY_CATEGORIES_ID_URL = BASE_URL + "/playlist/GetVideoListByCategory?";
    public static final String GET_TRENDING_PLAYLIST_URL = BASE_URL + "/playlist/GetTrendingPlayListInfo?";

    public static boolean IS_RETURNED_FROM_PLAYER = false;
    public static boolean IS_SHARING_ON_FACEBOOK = false;


    // --------Messages------------

    public static final String MSG_CONNECTION_TIMEOUT = "Connection Timeout. Please try again later";
    public static final String MSG_NO_INFO_AVAILABLE = "Video information is currently unavailable";
    public static final String MSG_NO_CONNECTION = "No connection available";
    public static final String LOGIN_MESSAGE = "Please login to save your favorite clips";
    public static final String SHARE_MESSAGE = "Please login to share this clip";
    public static final String FACEBOOK_LOGIN_CANCEL = "Facebook login was cancelled";
    public static final String TWITTER_LOGIN_CANCEL = "Twitter login was cancelled";
    public static final String FACEBOOK_SHARING_CANCEL = "Facebook post was cancelled";
    public static final String FACEBOOK_POST_SUCCESS_MESSAGE = "Successfully posted to Facebook";
    public static final String EMAIL_FAILURE_MESSAGE = "Request failed";

    //--------Preferences-------------
    public static final String PREF_IS_CONFIRMATION_DONE = "is_confirmation_done";
    public static final String PREF_IS_NOTIFICATION_ALLOW = "notification_allow";
    public static final String PREF_IS_DEVICE_REGISTERED = "device_registered";

    public static final String PREF_VAULT_USER_ID_LONG = "user_id";
    public static final String PREF_VAULT_USER_NAME = "user_name";
    public static final String PREF_VAULT_USER_EMAIL = "user_email";
    public static final String PREF_VAULT_USER_FIRST_NAME = "first_name";
    public static final String PREF_VAULT_USER_LAST_NAME = "last_name";
    public static final String PREF_VAULT_USER_BIO_TEXT = "bio_text";
    public static final String PREF_VAULT_USER_GENDER = "gender";
    public static final String PREF_VAULT_USER_AGE = "age";
    public static final String PREF_VAULT_USER_IMAGE_URL = "image_url";
    public static final String PREF_VAULT_USER_FLAG_STATUS = "flag_status";
    public static final String PREF_VAULT_USER_PASSWORD = "passwd";

    public static final String PREF_VAULT_SKIP_LOGIN = "is_skip_login";
    public static final String PREF_PULL_OPTION_HEADER = "is_option_header_shown";
    public static final String PREF_JOIN_MAIL_CHIMP = "is_option_join_mail_chimp";


    // ---------Identifiers----------
    public static final String OPPONENTS = "Opponents";
    public static final String FEATURED = "Featured";
    public static final String PLAYERS = "Players";
    public static final String COACHES_ERA = "Coaches Era";
    public static final String GAMES = "Games";

    // ---------Identifiers----------
    public static final String OKF_OPPONENT = "OKFOpponent";
    public static final String OKF_FEATURED = "OKFFeatured";
    public static final String OKF_PLAYERS = "OKFPlayer";
    public static final String OKF_COACH = "OKFCoach";
    public static final String OKF_GAMES = "OKFGames";

    public static String PLAYLIST_REF_ID = "referenceId";
    public static String VIDEO_OBJ = "video_dto";

    public static Fragment LIST_FRAGMENT = null;
    public static int LIST_ITEM_POSITION = 0;

    public static final long DEFAULT_USER_ID = 1110;

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    public static final int AUTO_REFRESH_INTERVAL = MINUTE * 30;


    public static final String FIRST_NAME_CAN_NOT_EMPTY = "Please enter the first name into the field!";
    public static final String LAST_NAME_CAN_NOT_EMPTY = "Please enter the last name into the field!";
    public static final String EMAIL_ID_CAN_NOT_EMPTY = "Please enter the email id into the field!";
    public static final String USER_NAME_CAN_NOT_EMPTY = "Please enter the user name into the field!";
    public static final String USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER = "User name should contain minimum 3 characters!";
    public static final String PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH = "Entered confirm password is not matching with password!";
    public static final String ENTER_ONLY_ALPHABETS = "Enter only alphabets!";


    public static final String PREF_VAULT_USER_DATA = "u_name";
    public static final String PREF_VAULT_EMAIL = "email";
    public static final String PREF_VAULT_FIRST_NAME = "f_name";
    public static final String PREF_VAULT_LAST_NAME = "l_name";
    public static final String PREF_VAULT_GENDER = "gender_";
    public static final String PREF_VAULT_AGE = "age_";
    public static final String PREF_VAULT_IMAGE_URL = "image_url_";
    public static final String PREF_VAULT_FLAG_STATUS = "flag_status_";
    public static final String PREF_VAULT_PASSWORD = "passwd_";
    public static final String USER_SUCCESSFULLY_REGISTERED = "User successfully registered to the app!";

    public static final String YOUR_PASSWORD_HAS_BEEN_REGISTERED_SUCCESSFULLY = "Your password has been reset successfully";
    public static final String NO_RECORDS_FOUND = "NO FAVORITE VIDEO FOUND";
    public static final String NO_VIDEO_FOUND = "NO VIDEO AVAILABLE";

    public static final String NO_VIDEOS_FOUND = "NO RECORD FOUND";

    public static final String[] FACEBOOK_PERMISSION = {"public_profile", "email", "user_friends"};
    public static final String NOTIFICATION_RECEIVE = "notificationReceive";
    public static final String NOTIFICATION_FOREGROUND = "notificationForeground";
    public static final String NOTIFICATION_OPEN = "notificationOpen";
    public static final String NOT_REGISTERED = "Your email is not register. Please register your email.";
    public static final String ENTER_EMAIL_AND_PASSWORD = "Please Enter Email and Password!";

    public static final String ENTERED_PASSWORD_WRONG = "Entered password is wrong";

    public static final String ADMOB_APP_ID = "ca-app-pub-8848124662104437~3841802707";

    public static final String ALREADY_REGISTERED_EMAIL = "Entered Email id Already Exists on Vault App!";
    public static final String ERROR_MSG = "Error while login in vault!";
    public static final String TEMP_AD_UNIT_FOR_FEATURED[] = {"ca-app-pub-3120536913205473/5975042732",
            "ca-app-pub-8848124662104437/5318535900"};
}

