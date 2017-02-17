package com.jogoler.jogolmapsunsri.database;

/**
 * Created by RazorX on 12/14/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jogoler.jogolmapsunsri.constant.CategoryField;
import com.jogoler.jogolmapsunsri.constant.LocationField;
import com.jogoler.jogolmapsunsri.constant.RegionField;
import com.jogoler.jogolmapsunsri.model.Location;


public class SQLiteDB extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "unsrimaps.db";
    //private static final String DATABASE_PATH = "/data/data/com.jogoler.jogolmaps/databases/";

    private static final String TEXT_TYPE = " TEXT ";
    private static final String VARCHAR_TYPE = " VARCHAR ";
    private static final String BIG_INT_TYPE = " BIGINT ";
    private static final String DOUBLE_TYPE = " DOUBLE ";
    private static final String SMALL_INT_TYPE = " SMALLINT ";
    private static final String INT_TYPE = " INTEGER ";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RegionField.TABLE_NAME + " (" +
                    RegionField.COLUMN_ID + VARCHAR_TYPE + " PRIMARY KEY," +
                    RegionField.COLUMN_NAME + VARCHAR_TYPE + " );"
            +
            "CREATE TABLE " + CategoryField.TABLE_NAME + " (" +
                    CategoryField.COLUMN_ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    CategoryField.COLUMN_NAME + VARCHAR_TYPE + COMMA_SEP +
                    CategoryField.COLUMN_IMAGE + VARCHAR_TYPE + COMMA_SEP +
                    CategoryField.COLUMN_MARKER + VARCHAR_TYPE + " );"
            +
            "CREATE TABLE " + LocationField.TABLE_NAME + " (" +
                    LocationField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LocationField.COLUMN_CATEGORY_ID + BIG_INT_TYPE + " REFERENCES " +
                        CategoryField.TABLE_NAME + " (" + CategoryField.COLUMN_ID +")" + COMMA_SEP +
                    LocationField.COLUMN_REGION_ID + VARCHAR_TYPE + " REFERENCES " +
                        RegionField.TABLE_NAME + " (" + RegionField.COLUMN_ID +
                            ") ON DELETE CASCADE ON UPDATE CASCADE" + COMMA_SEP +
                    LocationField.COLUMN_NAME + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_INTRO + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_DESCRIPTION + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_IMAGE + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_LINK + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    LocationField.COLUMN_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
                    LocationField.COLUMN_ADDRESS + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_PHONE + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_EMAIL + VARCHAR_TYPE + COMMA_SEP +
                    LocationField.COLUMN_FAVORITE + SMALL_INT_TYPE + " );";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocationField.TABLE_NAME + COMMA_SEP +
                                      CategoryField.TABLE_NAME + COMMA_SEP +
                                      RegionField.TABLE_NAME;

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void create(Location location){
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationField.COLUMN_NAME, location.getName());
        values.put(LocationField.COLUMN_INTRO, location.getThumb());


        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                LocationField.TABLE_NAME,
                null,
                values);
    }

    public Cursor retrieve(){
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LocationField.COLUMN_ID,
                LocationField.COLUMN_NAME,
                LocationField.COLUMN_INTRO };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                LocationField.COLUMN_NAME + " ASC";

        Cursor c = db.query(
                LocationField.TABLE_NAME,                    // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortOrder                                   // The sort order
        );

        return c;
    }

    public void update(Location location){
        SQLiteDatabase db = getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(LocationField.COLUMN_NAME, location.getName());
        values.put(LocationField.COLUMN_INTRO, location.getThumb());

        // Which row to update, based on the ID
        String selection = LocationField.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(location.getId()) };

        int count = db.update(
                LocationField.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void delete(int id){
        SQLiteDatabase db = getReadableDatabase();

        // Define 'where' part of query.
        String selection = LocationField.COLUMN_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
        // Issue SQL statement.
        db.delete(LocationField.TABLE_NAME, selection, selectionArgs);
    }
}