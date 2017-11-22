package com.ncsavault.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.ncsavault.dto.CategoriesTabDao;

import java.util.ArrayList;

/**
 *  Class used to save,update, get and delete the Categories data from data base.
 */

@SuppressWarnings("WeakerAccess")
public class CategoriesDatabaseTable {

    public static final String CATEGORIES_DATA_TABLE = "categories_data_table";
    //Primary Key Column
    public static final String KEY_ID = "id";

    //Playlist Columns
    public static final String KEY_CATEGORIES_ID = "categories_id";
    public static final String KEY_CATEGORIES_NAME = "categories_name";
    public static final String KEY_CATEGORIES_INDEX_POSITION = "categories_index_position";
    public static final String KEY_CATEGORIES_URL = "categories_url";
    public static final String KEY_CATEGORIES_MODIFIED = "categories_modified";
    public static final String KEY_CATEGORIES_KEYWORD = "categories_keyword";


    public static final String CREATE_CATEGORIES = "CREATE TABLE "
            + CATEGORIES_DATA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CATEGORIES_ID + " INTEGER," + KEY_CATEGORIES_NAME
            + " TEXT," + KEY_CATEGORIES_INDEX_POSITION + " INTEGER,"
            + KEY_CATEGORIES_URL  + " TEXT," + KEY_CATEGORIES_MODIFIED + " TEXT," + KEY_CATEGORIES_KEYWORD +" TEXT )";

    /**
     * Method used for create the categories table into data base.
     * And called this method inside the class VaultDatabaseHelper.
     * @param db The reference of SQLiteDatabase.
     */
    public static void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CATEGORIES);
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
        database.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_DATA_TABLE);
        Log.d("DB Version","DB Version"+oldVersion+"/"+newVersion);
        onCreate(database);
    }

    private static CategoriesDatabaseTable sInstance;

    /**
     *
     * @return The instance of CategoriesDatabaseTable.
     */
    public static synchronized CategoriesDatabaseTable getInstance() {
        if (sInstance == null) {
            sInstance = new CategoriesDatabaseTable();
        }
        return sInstance;
    }

    /**
     *  Method used for check the count of tab in data base.
     * @param database reference of SQLiteDatabase
     * @param categoriesId set the categoriesId and get the count of tab in data base.
     * @return number of count
     */
    public boolean isTabAvailableInDB(SQLiteDatabase database,long categoriesId) {

        int count;
        database.enableWriteAheadLogging();
        String query = "select * from " + CATEGORIES_DATA_TABLE + " where " + KEY_CATEGORIES_ID + " = " + categoriesId;
//                + " and " + VideoTable.KEY_PLAYLIST_REFERENCE_ID + " = ? ";
        Cursor cursor = database.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Method used for to insert the categories tab data into database
     * @param categoriesTabDaoArrayList To insert the categories data list
     * @param database Reference of SQLiteDatabase
     */
    public void insertCategoriesTabData(ArrayList<CategoriesTabDao> categoriesTabDaoArrayList, SQLiteDatabase database){
        try {
            database.enableWriteAheadLogging();
            ContentValues categoriesListValues;

            for (CategoriesTabDao categoriesTabDao : categoriesTabDaoArrayList) {
                if(!isTabAvailableInDB(database,categoriesTabDao.getCategoriesId())) {
                    categoriesListValues = new ContentValues();
                    categoriesListValues.put(KEY_CATEGORIES_ID, categoriesTabDao.getCategoriesId());
                    categoriesListValues.put(KEY_CATEGORIES_NAME, categoriesTabDao.getCategoriesName());
                    categoriesListValues.put(KEY_CATEGORIES_INDEX_POSITION, categoriesTabDao.getIndex_position());
                    categoriesListValues.put(KEY_CATEGORIES_URL, categoriesTabDao.getCategoriesUrl());
                    categoriesListValues.put(KEY_CATEGORIES_MODIFIED, categoriesTabDao.getCategories_modified());
                    categoriesListValues.put(KEY_CATEGORIES_KEYWORD, categoriesTabDao.getCategoriesKeyword());
                    database.insert(CATEGORIES_DATA_TABLE, null, categoriesListValues);
                }else
                {
                    ContentValues updateCategoriesListValues = new ContentValues();
                    updateCategoriesListValues.put(KEY_CATEGORIES_ID, categoriesTabDao.getCategoriesId());
                    updateCategoriesListValues.put(KEY_CATEGORIES_NAME, categoriesTabDao.getCategoriesName());
                    updateCategoriesListValues.put(KEY_CATEGORIES_INDEX_POSITION, categoriesTabDao.getIndex_position());
                    updateCategoriesListValues.put(KEY_CATEGORIES_URL, categoriesTabDao.getCategoriesUrl());
                    updateCategoriesListValues.put(KEY_CATEGORIES_MODIFIED, categoriesTabDao.getCategories_modified());
                    updateCategoriesListValues.put(KEY_CATEGORIES_KEYWORD, categoriesTabDao.getCategoriesKeyword());

                    database.update(CATEGORIES_DATA_TABLE, updateCategoriesListValues, KEY_CATEGORIES_ID + "=?",
                            new String[]{"" + categoriesTabDao.getCategoriesId()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Method used to update the Categories data using categories tab dto
     * @param database The reference of SQLiteDatabase
     * @param categoriesTabDao update the value of particular categories tab dto
     */
    public void updateCategoriesData(SQLiteDatabase database, CategoriesTabDao categoriesTabDao){
        try {
            database.enableWriteAheadLogging();
            ContentValues categoriesListValues = new ContentValues();

            categoriesListValues.put(KEY_CATEGORIES_NAME, categoriesTabDao.getCategoriesName());
            categoriesListValues.put(KEY_CATEGORIES_INDEX_POSITION, categoriesTabDao.getIndex_position());
            categoriesListValues.put(KEY_CATEGORIES_URL, categoriesTabDao.getCategoriesUrl());
            categoriesListValues.put(KEY_CATEGORIES_MODIFIED, categoriesTabDao.getCategories_modified());
            categoriesListValues.put(KEY_CATEGORIES_KEYWORD, categoriesTabDao.getCategoriesKeyword());

            database.update(CATEGORIES_DATA_TABLE, categoriesListValues, KEY_CATEGORIES_ID + "=?", new String[]{"" + categoriesTabDao.getCategoriesId()});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used to get the all categories data from data base
     * @param database The reference of SQLiteDatabase
     * @return The value of Categories tab list.
     */
    public ArrayList<CategoriesTabDao> getAllLocalCategoriesData(SQLiteDatabase database){
        try {
            ArrayList<CategoriesTabDao> categoriesTabDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM "+CATEGORIES_DATA_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            CategoriesTabDao categoriesTabDTO;
            if(cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        categoriesTabDTO = new CategoriesTabDao();
                        categoriesTabDTO.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_ID)));
                        categoriesTabDTO.setCategoriesName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_NAME)));
                        categoriesTabDTO.setIndex_position(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_INDEX_POSITION)));
                        categoriesTabDTO.setCategoriesUrl(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_URL)));
                        categoriesTabDTO.setCategories_modified(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_MODIFIED)));
                        categoriesTabDTO.setCategoriesKeyword(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_KEYWORD)));

                        categoriesTabDaoArrayList.add(categoriesTabDTO);
                    }while (cursor.moveToNext());
                }

            cursor.close();
            return categoriesTabDaoArrayList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used to get the particular categories tab data from data base using categoriesId
     * @param database The reference of SQLiteDatabase
     * @param categoriesId categories tab id to using get the data from data base
     * @return the value of particular categories tab dto using categoriesId
     */
    public CategoriesTabDao getLocalCategoriesDataByCategoriesId(SQLiteDatabase database, long categoriesId){
        try {
            CategoriesTabDao categoriesTabDTO = null;
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM "+CATEGORIES_DATA_TABLE+" WHERE "+ KEY_CATEGORIES_ID +" = "+categoriesId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            if(cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        categoriesTabDTO = new CategoriesTabDao();
                        categoriesTabDTO.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_ID)));
                        categoriesTabDTO.setCategoriesName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_NAME)));
                        categoriesTabDTO.setIndex_position(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_INDEX_POSITION)));
                        categoriesTabDTO.setCategoriesUrl(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_URL)));
                        categoriesTabDTO.setCategories_modified(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_MODIFIED)));
                        categoriesTabDTO.setCategoriesKeyword(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_KEYWORD)));

                    }while (cursor.moveToNext());
                }

            cursor.close();
            return categoriesTabDTO;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used for to remove the table CATEGORIES_DATA_TABLE from data base.
     * @param database The reference of SQLiteDatabase.
     */
    public void removeAllCategoriesTabData(SQLiteDatabase database){
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + CATEGORIES_DATA_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
