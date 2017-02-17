package com.jogoler.jogolmapsunsri.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.jogoler.jogolmapsunsri.constant.CategoryField;
import com.jogoler.jogolmapsunsri.constant.LocationField;

import java.io.IOException;

/**
 * Created by RazorX on 12/14/2016.
 */

public class LocationQuery {
    private DataBaseHelper dbHelper;

    public LocationQuery(Context context){
        dbHelper = new DataBaseHelper(context);

        try {

            dbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }
    }

    public Cursor getLocationList() {
        //Open connection to read only
        try {
            dbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        String selectQuery =  "SELECT  _id as " +
                LocationField.COLUMN_ID + "," +
                LocationField.COLUMN_IMAGE + "," +
                LocationField.COLUMN_NAME + "," +
                LocationField.COLUMN_LATITUDE + "," +
                LocationField.COLUMN_LONGITUDE +
                " FROM " + LocationField.TABLE_NAME;

        Cursor cursor = dbHelper.getQuery(selectQuery);


        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        dbHelper.closeDatabase();
        return cursor;

    }

    public Cursor getLocationListByKeyword(String search) {
        //Open connection to read only
        try {

            dbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        // SQLiteDatabase db = dbHelper.getReadableDatabase();
        search=search.replaceAll("\"","");
        char quotes ='"';
        String selectQuery =  "SELECT  _id as " +
                LocationField.COLUMN_ID + "," +
                LocationField.COLUMN_IMAGE + "," +
                LocationField.COLUMN_NAME + "," +
                LocationField.COLUMN_LATITUDE + "," +
                LocationField.COLUMN_LONGITUDE +
                " FROM " + LocationField.TABLE_NAME +
                " WHERE " +  LocationField.COLUMN_NAME +  "  LIKE "+quotes+"%" +search + "%"+quotes+" ORDER BY "+
                LocationField.COLUMN_NAME +" ASC"
                ;

        //Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = dbHelper.getQuery(selectQuery);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        dbHelper.closeDatabase();
        return cursor;

    }

    public Cursor getLocationDetailById(int id) {
        //Open connection to read only
        try {

            dbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        // SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                LocationField.COLUMN_CATEGORY_ID + "," +
                LocationField.COLUMN_REGION_ID + "," +
                LocationField.COLUMN_NAME + "," +
                LocationField.COLUMN_INTRO + "," +
                LocationField.COLUMN_DESCRIPTION + "," +
                LocationField.COLUMN_IMAGE + "," +
                LocationField.COLUMN_LINK + "," +
                LocationField.COLUMN_LATITUDE + "," +
                LocationField.COLUMN_LONGITUDE + "," +
                LocationField.COLUMN_ADDRESS + "," +
                LocationField.COLUMN_PHONE + "," +
                LocationField.COLUMN_EMAIL + "," +
                LocationField.COLUMN_FAVORITE +
                " FROM " + LocationField.TABLE_NAME +
                " WHERE " +  LocationField.COLUMN_ID +  " = "+id
                ;

        //Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = dbHelper.getQuery(selectQuery);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        dbHelper.closeDatabase();
        return cursor;

    }

    public Cursor getSuggestionLocation(String search,String limit) {
        //Open connection to read only
        try {

            dbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        // SQLiteDatabase db = dbHelper.getReadableDatabase();
        //prevent error because special character
        search=search.replaceAll("\"","");
        char quotes ='"';
        String selectQuery =  "SELECT  _id as " +
                LocationField.COLUMN_ID + "," +
                LocationField.COLUMN_NAME +
                " FROM " + LocationField.TABLE_NAME +
                " WHERE " +  LocationField.COLUMN_NAME + "  LIKE "+quotes+"%" +search + "%"+quotes+" "+limit
                ;

        //Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = dbHelper.getQuery(selectQuery);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        dbHelper.closeDatabase();
        return cursor;
    }

    public Cursor getAllLocationMarkerByRegion(String region) {
        //Open connection to read only
        try {

            dbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        // SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                LocationField.COLUMN_LATITUDE + "," +
                LocationField.COLUMN_LONGITUDE + "," +
                CategoryField.TABLE_NAME+"."+CategoryField.COLUMN_ID + "," +
                CategoryField.TABLE_NAME+"."+CategoryField.COLUMN_NAME + "," +
                CategoryField.TABLE_NAME+"."+CategoryField.COLUMN_MARKER + "," +
                LocationField.TABLE_NAME+"."+LocationField.COLUMN_ORDER + "," +
                LocationField.TABLE_NAME+"."+LocationField.COLUMN_NAME +
                " FROM " + LocationField.TABLE_NAME +
                " INNER JOIN "+CategoryField.TABLE_NAME+
                " ON "+LocationField.TABLE_NAME+"."+LocationField.COLUMN_CATEGORY_ID+
                " = "+CategoryField.TABLE_NAME+"."+CategoryField.COLUMN_ID+
                " WHERE " +  LocationField.COLUMN_REGION_ID +" = "+"'"+region+"'"
                ;

        //Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = dbHelper.getQuery(selectQuery);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        dbHelper.closeDatabase();
        return cursor;

    }
}
