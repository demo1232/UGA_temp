package com.ncsavault.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ncsavault.dto.PlaylistDto;

import java.util.ArrayList;

/**
 * Class used to save,update, get and delete the playlist data from data base.
 */

@SuppressWarnings("WeakerAccess")
public class PlaylistDatabaseTable {

    public static final String PLAYLIST_DATA_TABLE = "playlist_data_table";
    //Primary Key Column
    public static final String KEY_ID = "id";

    //Playlist Columns
    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    public static final String KEY_PLAYLIST_ID = "playlist_id";
    public static final String KEY_PLAYLIST_THUMB_URL = "playlist_thumbnail_url";
    public static final String KEY_PLAYLIST_SHORT_DESC = "playlist_short_desc";
    public static final String KEY_PLAYLIST_LONG_DESC = "playlist_long_desc";
    public static final String KEY_PLAYLIST_TAGS = "playlist_tags";
    public static final String KEY_PLAYLIST_REFERENCE_ID = "playlist_reference_id";
    public static final String KEY_CATEGORIES_ID = "categories_id";
    public static final String KEY_PLAYLIST_MODIFIED = "playlist_modified";

    public static final String CREATE_PLAYLIST = "CREATE TABLE "
            + PLAYLIST_DATA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_PLAYLIST_NAME + " TEXT," + KEY_PLAYLIST_ID + " INTEGER," + KEY_PLAYLIST_THUMB_URL
            + " TEXT," + KEY_PLAYLIST_SHORT_DESC + " TEXT," + KEY_PLAYLIST_LONG_DESC + " TEXT," +
            KEY_PLAYLIST_TAGS + " TEXT," + KEY_PLAYLIST_REFERENCE_ID + " TEXT," + KEY_CATEGORIES_ID + " INTEGER,"
            + KEY_PLAYLIST_MODIFIED + " INTEGER  )";

    /**
     * Method used for create the playlist table into data base.
     * And called this method inside the class VaultDatabaseHelper.
     * @param db The reference of SQLiteDatabase.
     */
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAYLIST);
    }

    /**
     * Method used for the upgrade any column and row then that will be call.
     * And called this method inside the class VaultDatabaseHelper.
     * @param database The reference of SQLiteDatabase.
     * @param oldVersion The older version of database.
     * @param newVersion The newer version of database.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_DATA_TABLE);
        Log.d("DB Version","DB Version"+oldVersion+"/"+newVersion);
        onCreate(database);
    }

    private static PlaylistDatabaseTable sInstance;

    /**
     * Method used to make it class singleton .
     * @return The reference of PlaylistDatabaseTable class.
     */
    public static synchronized PlaylistDatabaseTable getInstance() {
        if (sInstance == null) {
            sInstance = new PlaylistDatabaseTable();
        }
        return sInstance;
    }

    /**
     * Method used for check the count of playlist into data base.
     * @param database reference of SQLiteDatabase
     * @param playlistId set the playlistId and get the count of playlist in data base.
     * @return number of count
     */
    public boolean isPlaylistAvailableInDB(SQLiteDatabase database,long playlistId) {

        int count;
        database.enableWriteAheadLogging();
        String query = "select * from " + PLAYLIST_DATA_TABLE + " where " + KEY_PLAYLIST_ID + " = " + playlistId;
//                + " and " + VideoTable.KEY_PLAYLIST_REFERENCE_ID + " = ? ";
        Cursor cursor = database.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    /**
     * Method used for to insert the playlist tab data into database
     * @param playlistDtoArrayList Insert the playlist data into data base.
     * @param database Reference of SQLiteDatabase.
     * @param categoriesId Inset the one extra filed into data base.
     */
    public void insertPlaylistTabData(ArrayList<PlaylistDto> playlistDtoArrayList, SQLiteDatabase database,long categoriesId) {
        try {
            database.enableWriteAheadLogging();
            ContentValues initialValues;

            for (PlaylistDto playlistDto : playlistDtoArrayList) {
                if(!isPlaylistAvailableInDB(database,playlistDto.getPlaylistId())) {
                    initialValues = new ContentValues();
                    initialValues.put(KEY_PLAYLIST_NAME, playlistDto.getPlaylistName());
                    initialValues.put(KEY_PLAYLIST_ID, playlistDto.getPlaylistId());
                    initialValues.put(KEY_PLAYLIST_THUMB_URL, playlistDto.getPlaylistThumbnailUrl());
                    initialValues.put(KEY_PLAYLIST_SHORT_DESC, playlistDto.getPlaylistShortDescription());
                    initialValues.put(KEY_PLAYLIST_LONG_DESC, playlistDto.getPlaylistLongDescription());
                    initialValues.put(KEY_PLAYLIST_TAGS, playlistDto.getPlaylistTags());
                    initialValues.put(KEY_PLAYLIST_REFERENCE_ID, playlistDto.getPlaylistReferenceId());
                    initialValues.put(KEY_CATEGORIES_ID, categoriesId);
                    initialValues.put(KEY_PLAYLIST_MODIFIED, playlistDto.getPlaylist_modified());

                    database.insert(PLAYLIST_DATA_TABLE, null, initialValues);
                }
                else
                {
                    ContentValues updateInitialValues = new ContentValues();
                    updateInitialValues.put(KEY_PLAYLIST_NAME, playlistDto.getPlaylistName());
                    updateInitialValues.put(KEY_PLAYLIST_ID, playlistDto.getPlaylistId());
                    updateInitialValues.put(KEY_PLAYLIST_THUMB_URL, playlistDto.getPlaylistThumbnailUrl());
                    updateInitialValues.put(KEY_PLAYLIST_SHORT_DESC, playlistDto.getPlaylistShortDescription());
                    updateInitialValues.put(KEY_PLAYLIST_LONG_DESC, playlistDto.getPlaylistLongDescription());
                    updateInitialValues.put(KEY_PLAYLIST_TAGS, playlistDto.getPlaylistTags());
                    updateInitialValues.put(KEY_PLAYLIST_REFERENCE_ID, playlistDto.getPlaylistReferenceId());
                    updateInitialValues.put(KEY_CATEGORIES_ID, categoriesId);
                    updateInitialValues.put(KEY_PLAYLIST_MODIFIED, playlistDto.getPlaylist_modified());

                    database.update(PLAYLIST_DATA_TABLE, updateInitialValues, KEY_PLAYLIST_ID + "=?",
                            new String[]{"" + playlistDto.getPlaylistId()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for update playlist data into data base.
     * @param database Reference of SQLiteDatabase.
     * @param playlistDto Update the value of playlist dto.
     * @param categoriesId update the categories id.
     */
    public void updatePlaylistData(SQLiteDatabase database, PlaylistDto playlistDto,long categoriesId){
        try {
            database.enableWriteAheadLogging();
            ContentValues updateInitialValues = new ContentValues();
            updateInitialValues.put(KEY_PLAYLIST_NAME, playlistDto.getPlaylistName());
            updateInitialValues.put(KEY_PLAYLIST_ID, playlistDto.getPlaylistId());
            updateInitialValues.put(KEY_PLAYLIST_THUMB_URL, playlistDto.getPlaylistThumbnailUrl());
            updateInitialValues.put(KEY_PLAYLIST_SHORT_DESC, playlistDto.getPlaylistShortDescription());
            updateInitialValues.put(KEY_PLAYLIST_LONG_DESC, playlistDto.getPlaylistLongDescription());
            updateInitialValues.put(KEY_PLAYLIST_TAGS, playlistDto.getPlaylistTags());
            updateInitialValues.put(KEY_PLAYLIST_REFERENCE_ID, playlistDto.getPlaylistReferenceId());
            updateInitialValues.put(KEY_CATEGORIES_ID, categoriesId);
            updateInitialValues.put(KEY_PLAYLIST_MODIFIED, playlistDto.getPlaylist_modified());

            database.update(PLAYLIST_DATA_TABLE, updateInitialValues, KEY_PLAYLIST_ID + "=?",
                    new String[]{"" + playlistDto.getPlaylistId()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for get the playlist data for particular tab from database.
     * @param database The reference of SQLiteDatabase.
     * @param categoriesId Used the particular categoriesId and ge the playlist data.
     * @return The playlist data for particular tab.
     */
    public ArrayList<PlaylistDto> getLocalPlaylistDataByCategoriesTab(SQLiteDatabase database, long categoriesId) {
        try {
            ArrayList<PlaylistDto> playlistDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM " + PLAYLIST_DATA_TABLE +" WHERE "+
                    KEY_CATEGORIES_ID +" = "+categoriesId;

            Cursor cursor = database.rawQuery(selectQuery, null);
            PlaylistDto playlistDto;
            if (cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        playlistDto = new PlaylistDto();
                        playlistDto.setPlaylistName(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_NAME)));
                        playlistDto.setPlaylistId(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_ID)));
                        playlistDto.setPlaylistThumbnailUrl(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_THUMB_URL)));
                        playlistDto.setPlaylistShortDescription(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_SHORT_DESC)));
                        playlistDto.setPlaylistLongDescription(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_LONG_DESC)));
                        playlistDto.setPlaylistTags(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_TAGS)));
                        playlistDto.setPlaylistReferenceId(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_REFERENCE_ID)));
                        playlistDto.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_ID)));
                        playlistDto.setPlaylist_modified(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_MODIFIED)));

                        playlistDaoArrayList.add(playlistDto);
                    } while (cursor.moveToNext());
                }

            cursor.close();
            return playlistDaoArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method used for to get the all playlist data from data base.
     * @param database Reference of SQLiteDatabase.
     * @return The all playlist data from data base.
     */
    public ArrayList<PlaylistDto> getAllLocalPlaylistData(SQLiteDatabase database) {
        try {
            ArrayList<PlaylistDto> playlistDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM " + PLAYLIST_DATA_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            PlaylistDto playlistDto;
            if (cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        playlistDto = new PlaylistDto();
                        playlistDto.setPlaylistName(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_NAME)));
                        playlistDto.setPlaylistId(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_ID)));
                        playlistDto.setPlaylistThumbnailUrl(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_THUMB_URL)));
                        playlistDto.setPlaylistShortDescription(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_SHORT_DESC)));
                        playlistDto.setPlaylistLongDescription(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_LONG_DESC)));
                        playlistDto.setPlaylistTags(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_TAGS)));
                        playlistDto.setPlaylistReferenceId(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_REFERENCE_ID)));
                        playlistDto.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_ID)));
                        playlistDto.setPlaylist_modified(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_MODIFIED)));

                        playlistDaoArrayList.add(playlistDto);
                    } while (cursor.moveToNext());
                }

            cursor.close();
            return playlistDaoArrayList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method used for get the playlist data using playlist id.
     * @param database Reference of SQLiteDatabase
     * @param playlistId Get the particular playlist data using playlist id.
     * @return The value of playlist dto.
     */
    public PlaylistDto getLocalPlaylistDataByPlaylistId(SQLiteDatabase database, long playlistId){
        try {
            PlaylistDto playlistDto = new PlaylistDto();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM "+PLAYLIST_DATA_TABLE+" WHERE "+KEY_PLAYLIST_ID+" = "+playlistId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            if(cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        playlistDto = new PlaylistDto();
                        playlistDto.setPlaylistName(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_NAME)));
                        playlistDto.setPlaylistId(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_ID)));
                        playlistDto.setPlaylistThumbnailUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_THUMB_URL)));
                        playlistDto.setPlaylistShortDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_SHORT_DESC)));
                        playlistDto.setPlaylistLongDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_LONG_DESC)));
                        playlistDto.setPlaylistTags(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_TAGS)));
                        playlistDto.setPlaylistReferenceId(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_REFERENCE_ID)));
                        playlistDto.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_ID)));
                        playlistDto.setPlaylist_modified(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_MODIFIED)));

                    }while (cursor.moveToNext());
                }

            cursor.close();
            return playlistDto;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used for remove the data of  particular playlist tab using categories id.
     * @param database The reference of SQLiteDatabase.
     * @param categoriesId Remove the data of particular playlist tab using categories id.
     */
    public void removePlaylistTabData(SQLiteDatabase database,long categoriesId) {
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + PLAYLIST_DATA_TABLE + " WHERE " + KEY_CATEGORIES_ID + " = " + categoriesId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for remove the all playlist tab data from data base.
     * @param database The reference of SQLiteDatabase.
     */
    public void removeAllPlaylistTabData(SQLiteDatabase database) {
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + PLAYLIST_DATA_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

