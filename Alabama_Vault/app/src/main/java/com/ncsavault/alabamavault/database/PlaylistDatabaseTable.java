package com.ncsavault.alabamavault.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.dto.PlaylistDto;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */

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
    public static final String KEY_CATEGEROIES_ID = "cateroies_id";
    public static final String KEY_PLAYLIST_MODIFIED = "playlist_modified";

    public static final String CREATE_PLAYLIST = "CREATE TABLE "
            + PLAYLIST_DATA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_PLAYLIST_NAME + " TEXT," + KEY_PLAYLIST_ID + " INTEGER," + KEY_PLAYLIST_THUMB_URL
            + " TEXT," + KEY_PLAYLIST_SHORT_DESC + " TEXT," + KEY_PLAYLIST_LONG_DESC + " TEXT," +
            KEY_PLAYLIST_TAGS + " TEXT," + KEY_PLAYLIST_REFERENCE_ID + " TEXT," + KEY_CATEGEROIES_ID + " INTEGER,"
            + KEY_PLAYLIST_MODIFIED + " INTEGER  )";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAYLIST);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_DATA_TABLE);
        onCreate(database);
    }

    private static PlaylistDatabaseTable sInstance;

    public static synchronized PlaylistDatabaseTable getInstance() {
        if (sInstance == null) {
            sInstance = new PlaylistDatabaseTable();
        }
        return sInstance;
    }

    public boolean isPlaylistAvailableInDB(SQLiteDatabase database,long playlistId) {
        // TODO Auto-generated method stub
        int count = 0;
        database.enableWriteAheadLogging();
        String query = "select * from " + PLAYLIST_DATA_TABLE + " where " + KEY_PLAYLIST_ID + " = " + playlistId;
//                + " and " + VideoTable.KEY_PLAYLIST_REFERENCE_ID + " = ? ";
        Cursor cursor = database.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        }
        return false;
    }

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
                    initialValues.put(KEY_CATEGEROIES_ID, categoriesId);
                    initialValues.put(KEY_PLAYLIST_MODIFIED, playlistDto.getPlaylist_modified());

                    database.insert(PLAYLIST_DATA_TABLE, null, initialValues);
                }else
                {
                    ContentValues updateInitialValues = new ContentValues();
                    updateInitialValues.put(KEY_PLAYLIST_NAME, playlistDto.getPlaylistName());
                    updateInitialValues.put(KEY_PLAYLIST_ID, playlistDto.getPlaylistId());
                    updateInitialValues.put(KEY_PLAYLIST_THUMB_URL, playlistDto.getPlaylistThumbnailUrl());
                    updateInitialValues.put(KEY_PLAYLIST_SHORT_DESC, playlistDto.getPlaylistShortDescription());
                    updateInitialValues.put(KEY_PLAYLIST_LONG_DESC, playlistDto.getPlaylistLongDescription());
                    updateInitialValues.put(KEY_PLAYLIST_TAGS, playlistDto.getPlaylistTags());
                    updateInitialValues.put(KEY_PLAYLIST_REFERENCE_ID, playlistDto.getPlaylistReferenceId());
                    updateInitialValues.put(KEY_CATEGEROIES_ID, categoriesId);
                    updateInitialValues.put(KEY_PLAYLIST_MODIFIED, playlistDto.getPlaylist_modified());

                    database.update(PLAYLIST_DATA_TABLE, updateInitialValues, KEY_PLAYLIST_ID + "=?",
                            new String[]{"" + playlistDto.getPlaylistId()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            updateInitialValues.put(KEY_CATEGEROIES_ID, categoriesId);
            updateInitialValues.put(KEY_PLAYLIST_MODIFIED, playlistDto.getPlaylist_modified());

            database.update(PLAYLIST_DATA_TABLE, updateInitialValues, KEY_PLAYLIST_ID + "=?",
                    new String[]{"" + playlistDto.getPlaylistId()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PlaylistDto> getLocalPlaylistDataByCategorieTab(SQLiteDatabase database,long categoriesId) {
        try {
            ArrayList<PlaylistDto> playlistDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM " + PLAYLIST_DATA_TABLE +" WHERE "+
                    KEY_CATEGEROIES_ID+" = "+categoriesId;
            //+ " ORDER BY " + KEY_PLAYLIST_NAME + " COLLATE NOCASE " + " ASC ";
            Cursor cursor = database.rawQuery(selectQuery, null);
            PlaylistDto playlistDto = null;
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
                        playlistDto.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGEROIES_ID)));
                        playlistDto.setPlaylist_modified(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_MODIFIED)));

                        playlistDaoArrayList.add(playlistDto);
                    } while (cursor.moveToNext());
                }

            cursor.close();
            return playlistDaoArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<PlaylistDto>();
        }
    }

    public ArrayList<PlaylistDto> getAllLocalPlaylistData(SQLiteDatabase database) {
        try {
            ArrayList<PlaylistDto> playlistDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM " + PLAYLIST_DATA_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            PlaylistDto playlistDto = null;
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
                        playlistDto.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGEROIES_ID)));
                        playlistDto.setPlaylist_modified(cursor.getLong(cursor.getColumnIndex(KEY_PLAYLIST_MODIFIED)));

                        playlistDaoArrayList.add(playlistDto);
                    } while (cursor.moveToNext());
                }

            cursor.close();
            return playlistDaoArrayList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ArrayList<PlaylistDto>();
        }
    }

    public PlaylistDto getLocalPlaylistDataByPlaylistId(SQLiteDatabase database, long playlistId){
        try {
            PlaylistDto playlistDto = null;
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
                        playlistDto.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGEROIES_ID)));
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

    public void removePlaylistTabData(SQLiteDatabase database,long categoriesId) {
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + PLAYLIST_DATA_TABLE + " WHERE " + KEY_CATEGEROIES_ID  + " = " + categoriesId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAllPlaylistTabData(SQLiteDatabase database) {
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + PLAYLIST_DATA_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to check the playlist is available in database or not
     * not----------
     *
     * @param playlistId
     * @param sqLiteDatabase
     * @return
     */
    public boolean isPlayListAvailableInDB(long playlistId, SQLiteDatabase sqLiteDatabase) {
        // TODO Auto-generated method stub
        int count = 0;
        sqLiteDatabase.enableWriteAheadLogging();
        String query = "select * from " + PlaylistDatabaseTable.PLAYLIST_DATA_TABLE + " where " + PlaylistDatabaseTable.KEY_PLAYLIST_ID + " = " + playlistId;
//                + " and " + VideoTable.KEY_PLAYLIST_REFERENCE_ID + " = ? ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        }
        return false;
    }
}

