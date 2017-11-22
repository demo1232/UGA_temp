package com.ncsavault.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ncsavault.dto.CategoriesTabDao;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.dto.VideoDTO;

import java.util.ArrayList;

/**
 * Class used for create, upgrade all the data base table.
 * We are inserting, updating  and deleting data of video table
 * Also we are create table of video dto.
 * This is base class of all the data base table which we have used in vault app.
 */
@SuppressWarnings({"deprecation", "WeakerAccess", "unused"})
public class VaultDatabaseHelper extends SQLiteOpenHelper {

    @SuppressLint("StaticFieldLeak")
    public static VaultDatabaseHelper sInstance;

    // ------ Database Version----------
    public static final int DATABASE_VERSION = 5;
    private static String DATABASE_PATH = "";

    // ----- Database Name------------
    public static final String DATABASE_NAME = "UgaVault_database";

    private static final String USER_TABLE = "user_data";
    private static final String OLD_VIDEO_TABLE = "VIDEO_TABLE";

    public final Context context;

    /**
     * Constructor
     *
     * @param ctx Reference of context.
     */
    public VaultDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        context = ctx;
    }

    /**
     * Method used for to make it class singleton.
     *
     * @param context Reference of context.
     * @return The instance of VaultDatabaseHelper.
     */
    public static synchronized VaultDatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new VaultDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    /**
     * Method used for create the table into data base.
     *
     * @param db The reference of SQLiteDatabase.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        VideoTable.onCreate(db);
        TrendingVideoTable.onCreate(db);
        TabBannerTable.onCreate(db);
        CategoriesDatabaseTable.onCreate(db);
        PlaylistDatabaseTable.onCreate(db);
    }

    /**
     * Method used for the upgrade any column and row then that will be call.
     * And called this method inside the class VaultDatabaseHelper.
     *
     * @param oldVersion The older version of database.
     * @param newVersion The newer version of database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OLD_VIDEO_TABLE);
        VideoTable.onUpgrade(db, oldVersion, newVersion);
        TrendingVideoTable.onUpgrade(db, oldVersion, newVersion);
        TabBannerTable.onUpgrade(db, oldVersion, newVersion);
        CategoriesDatabaseTable.onUpgrade(db, oldVersion, newVersion);
        PlaylistDatabaseTable.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Method used for close the data base.
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * Method used for open data base connection.
     *
     * @return The value true and false.
     */
    public boolean isOpen() {
        return this.getWritableDatabase() != null && this.getWritableDatabase().isOpen();
    }

    //DATABASE OPERATION QUERIES AND METHODS
    //All methods related to storage of videos in the table
    //All methods related to retrieval of video records from DB


    /**
     * Method to check number of videos from the VideoTable in database
     *
     * @return video count
     */
    public int getVideoCount() {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            Cursor cursor = database.rawQuery(VideoTable.selectAllVideos, null);

            if (cursor != null) {
                return cursor.getCount();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }


    /**
     * Method to check number of trending videos from the Trending VideoTable in database
     *
     * @return trending video count
     */
    public int getTrendingVideoCount() {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            Cursor cursor = database.rawQuery(TrendingVideoTable.selectAllVideos, null);

            if (cursor != null) {
                return cursor.getCount();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }


    /**
     * Method used for the get the all trending video list from data base.
     *
     * @return The list of all trending video list.
     */
    public ArrayList<VideoDTO> getAllTrendingVideoList() {
        String selectOKFQuery = "SELECT * FROM " + TrendingVideoTable.TRENDING_VIDEO_TABLE;
        try {
            return setDtoValue(selectOKFQuery, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * This method is used to check trending video is available in database or not
     *
     * @param videoId Using video id and check video available or not.
     * @return trending video available or not
     */
    public boolean isTrendingVideoAvailableInDB(long videoId) {

        int count;
        SQLiteDatabase database = this.getReadableDatabase();
        database.enableWriteAheadLogging();
        String query = "select * from " + TrendingVideoTable.TRENDING_VIDEO_TABLE + " where " + TrendingVideoTable.KEY_VIDEO_ID + " = " + videoId;
        Cursor cursor = database.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();

        return count > 0;
    }


    /**
     * Inserting trending videos to the local database
     *
     * @param listVideos insert the all trending video into data base.
     */
    public void insertTrendingVideosInDatabase(ArrayList<VideoDTO> listVideos) {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            ContentValues initialValues;
            for (VideoDTO videoDTO : listVideos) {
                //if video is not available in database, execute INSERT
                if (!isTrendingVideoAvailableInDB(videoDTO.getVideoId())) {
                    if (videoDTO.getVideoShortDescription() != null && videoDTO.getVideoName() != null) {
                        initialValues = new ContentValues();
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_ID, videoDTO.getVideoId());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_NAME, videoDTO.getVideoName());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_SHORT_DESC, videoDTO.getVideoShortDescription());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_LONG_DESC, videoDTO.getVideoLongDescription());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_SHORT_URL, videoDTO.getVideoShortUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_LONG_URL, videoDTO.getVideoLongUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_THUMB_URL, videoDTO.getVideoThumbnailUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_STILL_URL, videoDTO.getVideoStillUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_COVER_URL, videoDTO.getVideoCoverUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_WIDE_STILL_URL, videoDTO.getVideoWideStillUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_BADGE_URL, videoDTO.getVideoBadgeUrl());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_DURATION, videoDTO.getVideoDuration());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_TAGS, videoDTO.getVideoTags());
                        initialValues.put(TrendingVideoTable.KEY_PLAYLIST_ID, videoDTO.getPlaylistId());
                        initialValues.put(TrendingVideoTable.KEY_PLAYLIST_SHORT_DESC, videoDTO.getPlaylistShortDescription());
                        if (videoDTO.isVideoIsFavorite())
                            initialValues.put(TrendingVideoTable.KEY_VIDEO_IS_FAVORITE, 1);
                        else
                            initialValues.put(TrendingVideoTable.KEY_VIDEO_IS_FAVORITE, 0);
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_INDEX, videoDTO.getVideoIndex());
                        initialValues.put(TrendingVideoTable.KEY_VIDEO_SOCIAL_URL, videoDTO.getVideoSocialUrl());
                        database.insert(TrendingVideoTable.TRENDING_VIDEO_TABLE, null, initialValues);
                    }
                } else {      // Perform UPDATE query on available record
                    ContentValues updateExistingVideo = new ContentValues();
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_ID, videoDTO.getVideoId());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_NAME, videoDTO.getVideoName());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_SHORT_DESC, videoDTO.getVideoShortDescription());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_LONG_DESC, videoDTO.getVideoLongDescription());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_SHORT_URL, videoDTO.getVideoShortUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_LONG_URL, videoDTO.getVideoLongUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_THUMB_URL, videoDTO.getVideoThumbnailUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_STILL_URL, videoDTO.getVideoStillUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_COVER_URL, videoDTO.getVideoCoverUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_WIDE_STILL_URL, videoDTO.getVideoWideStillUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_BADGE_URL, videoDTO.getVideoBadgeUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_DURATION, videoDTO.getVideoDuration());
                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_TAGS, videoDTO.getVideoTags());
                    if (videoDTO.isVideoIsFavorite())
                        updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_IS_FAVORITE, 1);
                    else
                        updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_IS_FAVORITE, 0);

                    updateExistingVideo.put(TrendingVideoTable.KEY_VIDEO_SOCIAL_URL, videoDTO.getVideoSocialUrl());
                    updateExistingVideo.put(TrendingVideoTable.KEY_PLAYLIST_ID, videoDTO.getPlaylistId());
                    updateExistingVideo.put(TrendingVideoTable.KEY_PLAYLIST_SHORT_DESC, videoDTO.getPlaylistShortDescription());

                    database.update(TrendingVideoTable.TRENDING_VIDEO_TABLE, updateExistingVideo, TrendingVideoTable.KEY_VIDEO_ID + "=?", new String[]{"" + videoDTO.getVideoId()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used for get the video data using video id from data base.
     *
     * @param videoId Get video data using video id
     * @return The data of video dto.
     */
    public VideoDTO getVideoDataByVideoId(String videoId) {
        try {
            String query = "SELECT * FROM "
                    + VideoTable.VIDEO_TABLE + " WHERE " + VideoTable.KEY_VIDEO_ID
                    + " = " + videoId + " GROUP BY " + VideoTable.KEY_VIDEO_ID;

            return setDtoValue(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used for get the video data using playlist id from data base.
     *
     * @param playlist Get video data using playlist id.
     * @return The data of video dto.
     */
    public VideoDTO getVideoDtoByPlaylistId(long playlist) {
        try {

            String query = "SELECT * FROM " + VideoTable.VIDEO_TABLE
                    + " WHERE " + VideoTable.KEY_PLAYLIST_ID + " = " + playlist +
                    " GROUP BY " + VideoTable.KEY_PLAYLIST_ID + "," + VideoTable.KEY_VIDEO_ID + " ORDER BY " +
                    VideoTable.KEY_VIDEO_NAME + " COLLATE NOCASE " + " ASC ";

            return setDtoValue(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used for get all the video list data from data base.
     *
     * @return The list of all video data.
     */
    public ArrayList<VideoDTO> getAllVideoList() {
        String selectOKFQuery = "SELECT * FROM " + VideoTable.VIDEO_TABLE +
                " GROUP BY " + VideoTable.KEY_VIDEO_ID + " ORDER BY " +
                VideoTable.KEY_VIDEO_NAME + " COLLATE NOCASE " + " ASC ";
        try {

            return setDtoValue(selectOKFQuery, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * this method is used to set dto values
     *
     * @param selectOKFQuery query created for table
     * @param value          to restrict setting of value in dto object as per requirement
     * @return video dto arraylist
     */
    private ArrayList<VideoDTO> setDtoValue(String selectOKFQuery, int value) {
        ArrayList<VideoDTO> videoDTOsArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        database.enableWriteAheadLogging();
        Cursor cursor = database.rawQuery(selectOKFQuery, null);

        if (cursor.moveToFirst()) {
            do {
                VideoDTO videoDTO = new VideoDTO();
                videoDTO.setVideoId(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_ID)));
                videoDTO.setVideoName(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_NAME)));
                videoDTO.setVideoShortDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SHORT_DESC)));
                videoDTO.setVideoLongDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_LONG_DESC)));
                videoDTO.setVideoShortUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SHORT_URL)));
                videoDTO.setVideoLongUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_LONG_URL)));
                videoDTO.setVideoThumbnailUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_THUMB_URL)));
                videoDTO.setVideoStillUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_STILL_URL)));
                videoDTO.setVideoCoverUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_COVER_URL)));
                videoDTO.setVideoWideStillUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_WIDE_STILL_URL)));
                videoDTO.setVideoBadgeUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_BADGE_URL)));
                videoDTO.setVideoDuration(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_DURATION)));
                videoDTO.setVideoTags(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_TAGS)));
                if (cursor.getInt(cursor.getColumnIndex(VideoTable.KEY_VIDEO_IS_FAVORITE)) == 0)
                    videoDTO.setVideoIsFavorite(false);
                else
                    videoDTO.setVideoIsFavorite(true);
                if (value == 0) {
                    videoDTO.setPlaylistName(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_NAME)));
                    videoDTO.setPlaylistThumbnailUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_THUMB_URL)));
                    videoDTO.setPlaylistLongDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_LONG_DESC)));
                    videoDTO.setPlaylistTags(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_TAGS)));
                    videoDTO.setPlaylistReferenceId(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_REFERENCE_ID)));
                    videoDTO.setVedioList_modified(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_MODIFIED)));

                }
                videoDTO.setVideoIndex(cursor.getInt(cursor.getColumnIndex(VideoTable.KEY_VIDEO_INDEX)));
                videoDTO.setVideoSocialUrl(cursor.getString(cursor.getColumnIndex(TrendingVideoTable.KEY_VIDEO_SOCIAL_URL)));
                videoDTO.setPlaylistId(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_ID)));
                videoDTO.setPlaylistShortDescription(cursor.getString(cursor.getColumnIndex(TrendingVideoTable.KEY_PLAYLIST_SHORT_DESC)));
                videoDTOsArrayList.add(videoDTO);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return videoDTOsArrayList;
    }

    /**
     * this method is used to set dto values
     *
     * @param selectOKFQuery query for table
     * @return video dto
     */
    private VideoDTO setDtoValue(String selectOKFQuery) {
        SQLiteDatabase database = this.getReadableDatabase();
        database.enableWriteAheadLogging();
        Cursor cursor = database.rawQuery(selectOKFQuery, null);
        VideoDTO videoDTO = new VideoDTO();
        if (cursor.moveToFirst()) {
            do {

                videoDTO.setVideoId(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_ID)));
                videoDTO.setVideoName(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_NAME)));
                videoDTO.setVideoShortDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SHORT_DESC)));
                videoDTO.setVideoLongDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_LONG_DESC)));
                videoDTO.setVideoShortUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SHORT_URL)));
                videoDTO.setVideoLongUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_LONG_URL)));
                videoDTO.setVideoThumbnailUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_THUMB_URL)));
                videoDTO.setVideoStillUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_STILL_URL)));
                videoDTO.setVideoCoverUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_COVER_URL)));
                videoDTO.setVideoWideStillUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_WIDE_STILL_URL)));
                videoDTO.setVideoBadgeUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_BADGE_URL)));
                videoDTO.setVideoDuration(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_DURATION)));
                videoDTO.setVideoTags(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_TAGS)));
                if (cursor.getInt(cursor.getColumnIndex(VideoTable.KEY_VIDEO_IS_FAVORITE)) == 0)
                    videoDTO.setVideoIsFavorite(false);
                else
                    videoDTO.setVideoIsFavorite(true);

                videoDTO.setVideoIndex(cursor.getInt(cursor.getColumnIndex(VideoTable.KEY_VIDEO_INDEX)));
                videoDTO.setPlaylistName(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_NAME)));
                videoDTO.setPlaylistId(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_ID)));
                videoDTO.setPlaylistThumbnailUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_THUMB_URL)));
                videoDTO.setPlaylistShortDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_SHORT_DESC)));
                videoDTO.setPlaylistLongDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_LONG_DESC)));
                videoDTO.setPlaylistTags(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_TAGS)));
                videoDTO.setPlaylistReferenceId(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_REFERENCE_ID)));
                videoDTO.setVideoSocialUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SOCIAL_URL)));
                videoDTO.setVedioList_modified(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_MODIFIED)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return videoDTO;
    }

    /**
     * Method used for get all video data list from data base.
     *
     * @param referenceId Using the reference id and get video list from data base.
     * @return he list of all video data.
     */
    public ArrayList<VideoDTO> getVideoList(String referenceId) {
        String selectOKFQuery = "SELECT * FROM " + VideoTable.VIDEO_TABLE
                + " WHERE " + VideoTable.KEY_PLAYLIST_REFERENCE_ID + " LIKE '" + referenceId + "%'" +
                " GROUP BY " + VideoTable.KEY_VIDEO_ID + " ORDER BY " +
                VideoTable.KEY_VIDEO_NAME + " COLLATE NOCASE " + " ASC ";
        try {

            return setDtoValue(selectOKFQuery, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method used for get all video list using playlist id from data base.
     *
     * @param playlistId Using playlist id and get all video data from data base.
     * @return The list of video data.
     */
    public ArrayList<VideoDTO> getVideoDataByPlaylistId(long playlistId) {

        Log.d("getVideoData", "getVideoDataByPlaylistId" + playlistId);

        String selectOKFQuery = "SELECT * FROM " + VideoTable.VIDEO_TABLE
                + " WHERE " + VideoTable.KEY_PLAYLIST_ID + " LIKE '" + playlistId + "%'" +
                " GROUP BY " + VideoTable.KEY_PLAYLIST_ID + "," + VideoTable.KEY_VIDEO_ID + " ORDER BY " +
                VideoTable.KEY_VIDEO_NAME + " COLLATE NOCASE " + " ASC ";
        try {

            return setDtoValue(selectOKFQuery, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This method is used to get the favorites videos
     *
     * @return video list
     */
    public ArrayList<VideoDTO> getFavouriteVideosArrayList() {
        try {

            String getFavouriteVideosListQuery = "SELECT * FROM " + VideoTable.VIDEO_TABLE + " WHERE " + VideoTable.KEY_VIDEO_IS_FAVORITE + "=1 " + " GROUP BY " + VideoTable.KEY_VIDEO_ID + " ORDER BY " + VideoTable.KEY_VIDEO_NAME + " ASC";

            return setDtoValue(getFavouriteVideosListQuery, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    /**
     * This method is used to set the video favorite
     *
     * @param flag    used for make id video favorite or not
     * @param videoID Make if favorite using video id.
     */

    public void setFavoriteFlag(int flag, long videoID) {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            ContentValues contents = new ContentValues();
            contents.put(VideoTable.KEY_VIDEO_IS_FAVORITE, flag);
            database.update(VideoTable.VIDEO_TABLE, contents, VideoTable.KEY_VIDEO_ID + "=?", new String[]{"" + videoID});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to check the video is favorite or not
     *
     * @param videoId Using videoId and check video is favorite or not.
     * @return is favourite or not
     */
    public boolean isFavorite(long videoId) {
        try {
            int flag = 0;
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            String getFlagQuery = "SELECT " + VideoTable.KEY_VIDEO_IS_FAVORITE + "  FROM " + VideoTable.VIDEO_TABLE + " WHERE " + VideoTable.KEY_VIDEO_ID + " = "
                    + videoId;
            Cursor cursor = database.rawQuery(getFlagQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    flag = cursor.getInt(0);
                } while (cursor.moveToNext());

            }
            cursor.close();
            return flag == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method used for set the all video status false.
     */
    public void setAllFavoriteStatusToFalse() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.enableWriteAheadLogging();
            database.execSQL("UPDATE " + VideoTable.VIDEO_TABLE + " SET " + VideoTable.KEY_VIDEO_IS_FAVORITE + " = 0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for check video is available or not in data base.
     *
     * @param videoId Using the video id and check particular video availability.
     * @return The flag status true and false.
     */
    public boolean isVideoAvailableInDB(String videoId) {

        int count;
        SQLiteDatabase database = this.getReadableDatabase();
        database.enableWriteAheadLogging();
        String query = "select * from " + VideoTable.VIDEO_TABLE + " where " + VideoTable.KEY_VIDEO_ID + " = " + videoId;
        Cursor cursor = database.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    /**
     * Method used for check video availability into data base.
     *
     * @param videoId Using video id and check availability.
     * @return The flag status true and false.
     */
    public boolean checkVideoAvailability(long videoId) {

        int count;
        SQLiteDatabase database = this.getReadableDatabase();
        database.enableWriteAheadLogging();
        String query = "select * from " + VideoTable.VIDEO_TABLE + " where " + VideoTable.KEY_VIDEO_ID + " = " + videoId;
        Cursor cursor = database.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    /**
     * Method used for check is change in data
     *
     * @param newVideoObject Using the video dto
     * @return The value true or false
     */
    public boolean isChangeInData(VideoDTO newVideoObject) {
        SQLiteDatabase database = this.getReadableDatabase();
        database.enableWriteAheadLogging();
        try {
            //Fetch Old Video Metadata from local database
            String query = "select * from " + VideoTable.VIDEO_TABLE + " where " + VideoTable.KEY_VIDEO_ID + " = " + newVideoObject.getVideoId();
            Cursor cursor = database.rawQuery(query, null);
            int count = cursor.getCount();
            if (count > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        if (!cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_NAME)).equals(newVideoObject.getVideoName())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SHORT_DESC)).equals(newVideoObject.getVideoShortDescription())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_LONG_DESC)).equals(newVideoObject.getVideoLongDescription())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_SHORT_URL)).equals(newVideoObject.getVideoShortUrl())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_LONG_URL)).equals(newVideoObject.getVideoLongUrl())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_THUMB_URL)).equals(newVideoObject.getVideoThumbnailUrl())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_STILL_URL)).equals(newVideoObject.getVideoStillUrl())
                                || !cursor.getString(cursor.getColumnIndex(VideoTable.KEY_VIDEO_TAGS)).equals(newVideoObject.getVideoTags())
                                || cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_VIDEO_DURATION)) != newVideoObject.getVideoDuration())
                            return true;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method used for remove the special character if we are getting.
     *
     * @param videoAttribute Using the string and remove it.
     * @return The value of staring after remove special character.
     */
    public String trimAndRemoveSpecialCharacters(String videoAttribute) {
        videoAttribute = videoAttribute.replace("'", "");
        videoAttribute = videoAttribute.replace(":", "");
        videoAttribute = videoAttribute.replace(";", "");
        videoAttribute = videoAttribute.replace("-", "");
        videoAttribute = videoAttribute.replace("_", "");
        videoAttribute = videoAttribute.replace("#", "");

        return videoAttribute.trim();
    }


    /**
     * Inserting videos to the local database
     *
     * @param listVideos add the list of video into data base.
     */
    public void insertVideosInDatabase(ArrayList<VideoDTO> listVideos) {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            ContentValues initialValues;
            for (VideoDTO videoDTO : listVideos) {

                if (videoDTO.getVideoShortDescription() != null && videoDTO.getVideoName() != null) {
                    initialValues = new ContentValues();
                    initialValues.put(VideoTable.KEY_VIDEO_ID, videoDTO.getVideoId());
                    initialValues.put(VideoTable.KEY_VIDEO_NAME, videoDTO.getVideoName());
                    initialValues.put(VideoTable.KEY_VIDEO_SHORT_DESC, videoDTO.getVideoShortDescription());
                    initialValues.put(VideoTable.KEY_VIDEO_LONG_DESC, videoDTO.getVideoLongDescription());
                    initialValues.put(VideoTable.KEY_VIDEO_SHORT_URL, videoDTO.getVideoShortUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_LONG_URL, videoDTO.getVideoLongUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_THUMB_URL, videoDTO.getVideoThumbnailUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_STILL_URL, videoDTO.getVideoStillUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_COVER_URL, videoDTO.getVideoCoverUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_WIDE_STILL_URL, videoDTO.getVideoWideStillUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_BADGE_URL, videoDTO.getVideoBadgeUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_DURATION, videoDTO.getVideoDuration());
                    initialValues.put(VideoTable.KEY_VIDEO_TAGS, videoDTO.getVideoTags());
                    if (videoDTO.isVideoIsFavorite())
                        initialValues.put(VideoTable.KEY_VIDEO_IS_FAVORITE, 1);
                    else
                        initialValues.put(VideoTable.KEY_VIDEO_IS_FAVORITE, 0);
                    initialValues.put(VideoTable.KEY_VIDEO_INDEX, videoDTO.getVideoIndex());

                    initialValues.put(VideoTable.KEY_PLAYLIST_NAME, videoDTO.getPlaylistName());
                    initialValues.put(VideoTable.KEY_PLAYLIST_ID, videoDTO.getPlaylistId());
                    initialValues.put(VideoTable.KEY_PLAYLIST_THUMB_URL, videoDTO.getPlaylistThumbnailUrl());
                    initialValues.put(VideoTable.KEY_PLAYLIST_SHORT_DESC, videoDTO.getPlaylistShortDescription());
                    initialValues.put(VideoTable.KEY_PLAYLIST_LONG_DESC, videoDTO.getPlaylistLongDescription());
                    initialValues.put(VideoTable.KEY_PLAYLIST_TAGS, videoDTO.getPlaylistTags());
                    initialValues.put(VideoTable.KEY_PLAYLIST_REFERENCE_ID, videoDTO.getPlaylistReferenceId());
                    initialValues.put(VideoTable.KEY_VIDEO_SOCIAL_URL, videoDTO.getVideoSocialUrl());
                    initialValues.put(VideoTable.KEY_VIDEO_MODIFIED, videoDTO.getVedioList_modified());
                    database.insert(VideoTable.VIDEO_TABLE, null, initialValues);

                    checkVideoAvailabilityInOtherPlaylistAndUpdate(videoDTO);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used for check Video Availability In Other Playlist And Update
     *
     * @param videoDTO Using the video dto and check Availability.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void checkVideoAvailabilityInOtherPlaylistAndUpdate(VideoDTO videoDTO) {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            database.enableWriteAheadLogging();
            //Update the video metadata if exists in some other playlist
            if (checkVideoAvailability(videoDTO.getVideoId())) {
                // if (isChangeInData(videoDTO)) {
                ContentValues updateExistingVideo = new ContentValues();
                updateExistingVideo.put(VideoTable.KEY_VIDEO_ID, videoDTO.getVideoId());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_NAME, videoDTO.getVideoName());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_SHORT_DESC, videoDTO.getVideoShortDescription());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_LONG_DESC, videoDTO.getVideoLongDescription());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_SHORT_URL, videoDTO.getVideoShortUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_LONG_URL, videoDTO.getVideoLongUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_THUMB_URL, videoDTO.getVideoThumbnailUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_STILL_URL, videoDTO.getVideoStillUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_COVER_URL, videoDTO.getVideoCoverUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_WIDE_STILL_URL, videoDTO.getVideoWideStillUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_BADGE_URL, videoDTO.getVideoBadgeUrl());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_DURATION, videoDTO.getVideoDuration());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_TAGS, videoDTO.getVideoTags());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_SOCIAL_URL, videoDTO.getVideoSocialUrl());
                if (videoDTO.isVideoIsFavorite())
                    updateExistingVideo.put(VideoTable.KEY_VIDEO_IS_FAVORITE, 1);
                else
                    updateExistingVideo.put(VideoTable.KEY_VIDEO_IS_FAVORITE, 0);
//                                updateExistingVideo.put(VideoTable.KEY_VIDEO_INDEX, videoDTO.getVideoIndex());
                updateExistingVideo.put(VideoTable.KEY_VIDEO_MODIFIED, videoDTO.getVedioList_modified());
                database.update(VideoTable.VIDEO_TABLE, updateExistingVideo, VideoTable.KEY_VIDEO_ID + "=?",
                        new String[]{"" + videoDTO.getVideoId()});
                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for remove the all records from the data base.
     */
    public void removeAllRecords() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.enableWriteAheadLogging();
            db.execSQL("DELETE FROM " + VideoTable.VIDEO_TABLE);
            removeAllTabBannerData();
            removeAllPlaylistTabData();
            removeAllCategoriesTabData();
            ArrayList<VideoDTO> favoriteVideoList = new ArrayList<>();
            favoriteVideoList=  getFavouriteVideosArrayList();
            Log.d("size","favoriteVideoList size getFavoriteDataFromDataBase database clasee : " +
                    favoriteVideoList.size());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used for remove the trending video data from data base.
     */
    public void removeTrendingVideoRecords() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.enableWriteAheadLogging();
            db.execSQL("DELETE FROM " + TrendingVideoTable.TRENDING_VIDEO_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used for remove the record the tab from data base.
     *
     * @param referenceId Using the referenceId and remove the tab data.
     */
    public void removeRecordsByTab(String referenceId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.enableWriteAheadLogging();
            db.execSQL("DELETE FROM " + VideoTable.VIDEO_TABLE + " WHERE " + VideoTable.KEY_PLAYLIST_REFERENCE_ID + " LIKE '" + referenceId + "%'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for remove the video data using playlist id from data base.
     *
     * @param playlist Using playlist id and remove video data.
     */
    public void removeVideoByPlaylistId(long playlist) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.enableWriteAheadLogging();
            db.execSQL("DELETE FROM " + VideoTable.VIDEO_TABLE + " WHERE " + VideoTable.KEY_PLAYLIST_ID + " = " + playlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //***********************************************************************//
    //*******************TAB BANNER METHODS**********************************//
    //***********************************************************************//
    public void insertTabBannerData(TabBannerDTO tabBannerDTO) {
        TabBannerTable.getInstance().insertTabBannerData(tabBannerDTO, this.getWritableDatabase());
    }

    public ArrayList<TabBannerDTO> getAllLocalTabBannerData() {
        return TabBannerTable.getInstance().getAllLocalTabBannerData(this.getReadableDatabase());
    }

    public TabBannerDTO getLocalTabBannerDataByBannerId(long bannerId) {
        return TabBannerTable.getInstance().getLocalTabBannerDataByBannerId(this.getReadableDatabase(), bannerId);
    }

    public int getTabBannerCount() {
        return TabBannerTable.getInstance().getTabBannerCount(this.getWritableDatabase());
    }

    public TabBannerDTO getLocalTabBannerDataByTabId(long tabId) {
        return TabBannerTable.getInstance().getLocalTabBannerDataByTabId(this.getReadableDatabase(), tabId);
    }

    public void updateBannerData(TabBannerDTO tabBannerDTO) {
        TabBannerTable.getInstance().updateBannerData(this.getWritableDatabase(), tabBannerDTO);
    }

    public void updateTabData(TabBannerDTO tabBannerDTO) {
        TabBannerTable.getInstance().updateTabData(this.getWritableDatabase(), tabBannerDTO);
    }

    public void updateTabBannerData(TabBannerDTO tabBannerDTO) {
        TabBannerTable.getInstance().updateTabBannerData(this.getWritableDatabase(), tabBannerDTO);
    }

    public void removeAllTabBannerData() {
        TabBannerTable.getInstance().removeAllTabBannerData(this.getWritableDatabase());
    }


    //***********************************************************************//
    //*******************CATEGORIES METHODS**********************************//
    //***********************************************************************//
    public void insertCategoriesTabData(ArrayList<CategoriesTabDao> categoriesTabDaoArrayList) {
        CategoriesDatabaseTable.getInstance().insertCategoriesTabData(categoriesTabDaoArrayList, this.getWritableDatabase());
    }

    public ArrayList<CategoriesTabDao> getAllLocalCategoriesTabData() {
        return CategoriesDatabaseTable.getInstance().getAllLocalCategoriesData(this.getReadableDatabase());
    }

    public void updateCategoriesData(CategoriesTabDao categoriesTabDao) {
        CategoriesDatabaseTable.getInstance().updateCategoriesData(this.getWritableDatabase(), categoriesTabDao);
    }

    public CategoriesTabDao getLocalCategoriesDataByCategoriesId(long categoriesId) {
        return CategoriesDatabaseTable.getInstance().getLocalCategoriesDataByCategoriesId(this.getReadableDatabase(), categoriesId);
    }

    public void removeAllCategoriesTabData() {
        CategoriesDatabaseTable.getInstance().removeAllCategoriesTabData(this.getWritableDatabase());
    }


    //***********************************************************************//
    //*******************PLAYLIST METHODS**********************************//
    //***********************************************************************//
    public void insertPlaylistTabData(ArrayList<PlaylistDto> playlistDtoArrayList, long categoriesId) {
        PlaylistDatabaseTable.getInstance().insertPlaylistTabData(playlistDtoArrayList, this.getWritableDatabase(), categoriesId);
    }

    public ArrayList<PlaylistDto> getAllLocalPlaylistTabData() {
        return PlaylistDatabaseTable.getInstance().getAllLocalPlaylistData(this.getReadableDatabase());
    }

    public PlaylistDto getLocalPlaylistDataByPlaylistId(long playlistId) {
        return PlaylistDatabaseTable.getInstance().getLocalPlaylistDataByPlaylistId(this.getReadableDatabase(), playlistId);
    }

    public ArrayList<PlaylistDto> getLocalPlaylistDataByCategoriesTab(long categoriesId) {
        return PlaylistDatabaseTable.getInstance().getLocalPlaylistDataByCategoriesTab(this.getReadableDatabase(), categoriesId);
    }

    public void updatePlaylistData(PlaylistDto playlistDto, long categoriesId) {
        PlaylistDatabaseTable.getInstance().updatePlaylistData(this.getReadableDatabase(), playlistDto, categoriesId);
    }

    public void removeAllPlaylistTabData() {
        PlaylistDatabaseTable.getInstance().removeAllPlaylistTabData(this.getWritableDatabase());
    }

    public void removePlaylistTabData(long categoriesId) {
        PlaylistDatabaseTable.getInstance().removePlaylistTabData(this.getWritableDatabase(), categoriesId);
    }

}
