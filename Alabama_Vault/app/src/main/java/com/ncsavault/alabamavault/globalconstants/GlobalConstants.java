package com.ncsavault.alabamavault.globalconstants;

import android.support.v4.app.Fragment;


public class GlobalConstants {

    /*******************IMPORTANT CONSTANTS THAT CHANGES IN DIFFERENT VAULT APPS********************
     ***********************************************************************************************/

    //ALABAMA Vault Twitter App Keys
    public static final String TWITTER_CONSUMER_KEY = "9ZX1ASEKbD2za841161DgNqCy";
    public static final String TWITTER_CONSUMER_SECRET = "eFK2cuCMuODM9eq5g4dfojHLonsqH6uOb5ABOZFybgbBTH9phz";

    public static final int APP_ID = 4;
    public static final String APP_NAME = "alabamavault";
    public static final String APP_VERSION = "1.0.1";
    public static final String DEVICE_TYPE = "Android";
    public static final String APP_FULL_NAME = "Alabama Vault";
    public static final String APP_SCHOOL_NAME = "Alabama";

    // --------Banner image url-----------
    public static final String URL_FEATUREDBANNER = "http://www.ncsavault.com/banner/uga/gfeatured.png";

    //Asana API call needed parameters
    public static final String ASANA_TASK_API_URL = "https://app.asana.com/api/1.0/tasks";
    public static final String ASANA_TAG_API_URL = "https://app.asana.com/api/1.0/tasks/";

    //Asana API key for NCSA workspace
    public static final String ASANA_WORKSPACE_API_KEY_OLD = "be6Qonxo.Fq4lCk3Kh2uxhp11TD83Rfz";
    public static final String ASANA_WORKSPACE_API_KEY = "bOYidKTG.o2d02z31v2uEZbHOYOnDyeO";
    public static final String ASANA_WORKSPACE_ACCESS_TOKEN = "0/27361a0a4ba92d4a206531c9b9a75feb";


    //WorkspaceId for NCSA Workspace on Asana
    public static final String WORKSPACE_ID = "36102017421462";
    //ProjectId for ALABAMAVault Project on Asana
    public static final String PROJECT_ID = "248026183943339";

    public static final String FEEDBACK_ASSIGNEE_ID = "36102034856106";
    public static final String CLIP_REQUEST_ASSIGNEE_ID = "36102034856106";
    public static final String SUPPORT_ASSIGNEE_ID = "35423163489342";

    public static final String FEEDBACK_ASSIGNEE_NAME = "Reid Shapiro";
    public static final String CLIP_REQUEST_ASSIGNEE_NAME = "Reid Shapiro";
    public static final String SUPPORT_ASSIGNEE_NAME = "Jody Smith";

    public static final String ASSIGNEE_STATUS = "upcoming";

    public static final String FEEDBACK_TAG_ID = "39878640085870";
    public static final String CLIP_REQUEST_TAG_ID = "39878640085877";
    public static final String SUPPORT_TAG_ID = "39878640085865";
    public static final String NO_LOGIN_TAG_ID = "41885771291051";

    public static final String ANDROID_TAG_ID = "40006879442737";
    // -------flurry key-----------
    public static final String FLURRY_KEY = "KKXZQKS53C6RWD9WMD89";  //Flurry Key For ALABAMAVaultAndroid app on Flurry Dashboard

    public static final String PREF_PACKAGE_NAME = "com.ncsavault.alabamavault";

    // Google project id
    public static final String GOOGLE_SENDER_ID = "859622651979";  // Place here your Google project id

    public static final String PROFILE_PIC_DIRECTORY = "ALABAMAVaultProfilePic";

    public static final String HOCKEY_APP_ID = "52ba664e14460294504388e7395d8631";

    public static final String MAIL_CHIMP_LIST_ID = "f12a62645e";

    public static final String MAIL_CHIMP_API_KEY = "e9bbd18dd1436459e53920c2e186fa39-us5";

    /***********************************************************************************************
     ***********************************************************************************************/

    // ------- Key's-------
    public static final String KEY_VIDEONAME = "name";
    public static final String KEY_CATEGORY = "category";
    public static final String RELATED_VIDEO_CATEGORY = "Related Videos";

    //Production Url
    // public static final String BASE_URL = "http://vaultservices.cloudapp.net/api";

    //new Production Url
    //public static final String BASE_URL = "http://ncsavault.westus.cloudapp.azure.com/api";

    //local url
    // public static final String BASE_URL = "http://10.10.10.147:9097/api";

    //Staging Url
    // public static final String BASE_URL = "http://0b78b111a9d0410784caa8a634aa3b90.cloudapp.net/api";

    //new Staging url
   // public static String BASE_URL = "http://syncroapi-staging.ncsavault.com/api";

    //new Production Url
    public static final String BASE_URL = "http://syncroapi.ncsavault.com/api";

    public static final String PUSH_REGISTER_URL = BASE_URL + "/MobileUsers/PostPushData";

    //local url
    //public static final String PUSH_REGISTER_URL = "http://10.10.10.65:8088/api/MobileUsers/PostPushData";

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
    public static final String POST_IMAGE_DATA_URL = BASE_URL + "/MobileUsers/getImageBase64";
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

    public static String SEARCH_VIEW_QUERY = "";

    public static boolean IS_SHARING_ON_FACEBOOK = false;


    // --------Messages------------
    public static final String MSG_SERVER_FAIL = "Couldn't connect to server";
    public static final String MSG_CONNECTION_TIMEOUT = "Connection Timeout. Please try again later";
    public static final String MSG_NO_INFO_AVAILABLE = "Video information is currently unavailable";
    public static final String MSG_NO_CONNECTION = "No connection available";
    public static final String LOGIN_MESSAGE = "Please login to save your favorite clips";
    public static final String SHARE_MESSAGE = "Please login to share this clip";
    public static final String FACEBOOK_LOGIN_CANCEL = "Facebook login was cancelled";
    public static final String TWITTER_LOGIN_CANCEL = "Twitter login was cancelled";
    public static final String FACEBOOK_SHARING_CANCEL = "Facebook post was cancelled";
    public static final String TWITTER_SHARING_CANCEL = "Twitter post was cancelled";
    public static final String FACEBOOK_POST_SUCCESS_MESSAGE = "Successfully posted to Facebook";
    public static final String TWITTER_POST_SUCCESS_MESSAGE = "Successfully posted to Twitter";
    public static final String EMAIL_SUCCESS_MESSAGE = "We have received your request";
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

    //Sub menu items name
    public static final String SUPPORT_TEXT = "Support";
    public static final String CLIP_REQUEST_TEXT = "Clip Request";
    public static final String FEEDBACK_TEXT = "Feedback";

    // ------ flags-----------
    public static final int FAVOURITE = 1;
    public static final int NOTFAVOURITE = 0;

    // ------- Fragment names --------------
    public static final String[] tabsList = new String[]{GlobalConstants.FEATURED, GlobalConstants.GAMES,
            GlobalConstants.PLAYERS, GlobalConstants.COACHES_ERA, GlobalConstants.OPPONENTS,
            GlobalConstants.FAVORITES};
    public static final String[] tabsDbIdentifierList = new String[]{GlobalConstants.OKF_FEATURED,
            GlobalConstants.OKF_PLAYERS, GlobalConstants.OKF_COACH, GlobalConstants.OKF_OPPONENT,
            GlobalConstants.FAVORITES};
    public static final String[] tabType = new String[]{"EdgeToEdge", "Wide", "EdgeToEdge", "Wide", "Wide"};

    // ------- fonts path--------
    public static final String TTF_ROBOTOBOLD = "fonts/RobotoCondensed-Bold.ttf";
    public static final String TTF_ROBOTOLIGHT = "fonts/RobotoCondensed-Light.ttf";

    // ---------Identifiers----------
    public static final String OPPONENTS = "Opponents";
    public static final String FEATURED = "Featured";
    public static final String PLAYERS = "Players";
    public static final String COACHES_ERA = "Coaches Era";
    public static final String FAVORITES = "Favorites";
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

    public static boolean IS_GRID = false;

    public static int CURRENT_TAB = 0;

    public static final long DEFAULT_USER_ID = 1110;

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "Florida Vault GCM";

    public static final String DISPLAY_MESSAGE_ACTION =
            "Display Message";

    public static final String DO_YOU_WANT_TO_JOIN_OUR_MAILING_LIST = "We won't spam you, or sell your email. We simply want to keep informed about what's going on at ALABAMA VAULT.\n";

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    public static final int AUTO_REFRESH_INTERVAL = MINUTE*30;//HOUR *2;

    public static final String VAULT_PERMISSION = "Allow ALABAMA Vault to access photos,media,files and location on your device. Please go to app settings and allow permissions.";
    public static final String VAULT_RWAD_PHONE_STATE_PERMISSION = "Allow ALABAMA Vault to access read phone state on your device. Please go to app settings and allow permission.";
    public static final String VAULT_WRITE_PERMISSION = "Allow ALABAMA Vault to access write external storage permission on your device. Please go to app settings and allow permission.";

    public static final String FIRST_NAME_CAN_NOT_EMPTY = "Please enter the first name into the field!";
    public static final String FIRST_NAME_SHOULD_CONTAIN_THREE_CHARACTER = "First name should contain minimum 3 characters!";
    public static final String LAST_NAME_CAN_NOT_EMPTY = "Please enter the last name into the field!";
    public static final String LAST_NAME_SHOULD_CONTAIN_THREE_CHARACTER = "Last name should contain minimum 3 characters!";
    public static final String EMAIL_ID_CAN_NOT_EMPTY = "Please enter the email id into the field!";
    public static final String USER_NAME_CAN_NOT_EMPTY = "Please enter the user name into the field!";
    public static final String USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER = "User name should contain minimum 3 characters!";
    public static final String PASSWORD_AND_CONFIREM_PASSWORD_DOES_NOT_MATCH = "Entered confirm password is not matching with password!";
    public static final String YOB_SHOULD_BE_MUST_FOUR_CHARACTER = "Year of birth should contain 4 characters!";
    public static final String ENTER_ONLY_ALPHABETS = "Enter only alphabets!";



    // public static final String JW_PLAYER_CSS_FILE_URL =  "file:///android_asset/UgaPlayer.css";
    public static final String JW_PLAYER_CSS_FILE_URL =  "http://syncroapi.ncsavault.com/JWPlayerCss/Alabama/AlabamaPlayer.css";
   // public static final String JW_PLAYER_CSS_FILE_URL =  "http://13.93.160.190/JWPlayerCss/Alabama/AlabamaPlayer.css";

    public static final String PREF_VAULT_USER_DATA = "u_name";
    public static final String PREF_VAULT_EMAIL = "email";
    public static final String PREF_VAULT_FIRST_NAME = "f_name";
    public static final String PREF_VAULT_LAST_NAME = "l_name";
    public static final String PREF_VAULT_GENDER = "gender_";
    public static final String PREF_VAULT_AGE = "age_";
    public static final String PREF_VAULT_IMAGE_URL = "image_url_";
    public static final String PREF_VAULT_FLAG_STATUS = "flag_status_";
    public static final String PREF_VAULT_PASSWORD = "passwd_";
    public static final String PREF_VAULT_CONFIRM_PASSWORD = "confirm_passwd_";
    public static final String PREF_VAULT_URI_IMAGE = "uri_image";
    public static final String USER_SUCCESSFULLY_REGISTERED = "User successfully registered to the app!";

    public static final String YOUR_PASSWORD_HAS_BEEN_REGISTERED_SUCCESSFULLY = "Your password has been reset successfully";
    public static final String YOUR_EMAIL_HAS_BEEN_VERIFIED_SUCCESSFULLY = "Your email has been verified successfully";
    public static final String NO_RECORDS_FOUND = "NO FAVORITE VIDEO FOUND";
    public static final String NO_VIDEO_FOUND = "NO VIDEO AVAILABLE";

    public static final String NO_VIDEOS_FOUND = "NO RECORD FOUND";

    public static final String[] FACEBOOK_PERMISSION = {"public_profile","email","user_friends"};
    public static final String NOTIFICATION_RECEIVE = "notificationReceive";
    public static final String NOTIFICATION_FOREGROUND = "notificationForeground";
    public static final String NOTIFICATION_OPEN = "notificationOpen";
    public static final String NOT_REGISTERED = "Your email is not register. Please register your email.";
    public static final String ENTER_EMAIL_AND_PASSWORD = "Please Enter Email and Password!";

    public static final String ENTERRED_PASSWORD_WRONG = "entered password is wrong";

    public static String CATEGORYNAME = "";

    public static final String ADMOB_APP_ID = "ca-app-pub-8848124662104437~3841802707";
    public static final String AD_UNIT_FOR_FEATURED[] = {"ca-app-pub-8848124662104437/3755741718",
           "ca-app-pub-8848124662104437/1840024816"};
    public static final String AD_UNIT_FOR_GAMES[] = {"ca-app-pub-8848124662104437/9171364702",
            "ca-app-pub-8848124662104437/4861773973"};
    public static final String AD_UNIT_FOR_PLAYER[] = {"ca-app-pub-8848124662104437/2769607883",
            "ca-app-pub-8848124662104437/5750994446"};
    public static final String AD_UNIT_FOR_OPPONENT[] = {"ca-app-pub-8848124662104437/8923952697",
            "ca-app-pub-8848124662104437/5531502590"};
    public static final String AD_UNIT_FOR_COACHS[] = {"ca-app-pub-8848124662104437/6430663059",
            "ca-app-pub-8848124662104437/5281232913"};


    public static final String ALREADY_REGISTERED_EMAIL = "Entered Email id Already Exists on Vault App!";
    public static final String ERROR_MESG = "Error while login in vault!";


    public static final String SUPPORT_MAIL_ID = "appsupport@alabamavault.com";
    public static final String SUPPORT_SUBJECT = "Alabama support | Android";
    public static final String SUPPORT_MAIL_THROUGH_BROWSER =  "https://mail.google.com/mail/?view=cm&fs=1&to="
                                                              +SUPPORT_MAIL_ID+"&su="+SUPPORT_SUBJECT+"&body="+""+"&bcc="+"";

    public static final String TEMP_AD_UNIT_FOR_FEATURED[] = {"ca-app-pub-3120536913205473/5975042732",
            "ca-app-pub-8848124662104437/5318535900"};
}

