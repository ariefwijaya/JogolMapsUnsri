package com.jogoler.jogolmapsunsri.model;

import android.content.Context;

/**
 * Created by RazorX on 12/21/2016.
 */

public class MapsMarker {
    private String categoryName;
    private int categoryId;
    private int categoryMarker;
    private double latitude;
    private double longitude;
    private boolean available;
    private int order;
    private String name;

    public static int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public void setOrder(int order){
        this.order = order;
    }

    public int getOrder(){
        return order;
    }

    public void setAvailable(boolean available){
        this.available = available;
    }

    public boolean getAvailable(){
        return available;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public void setCategoryMarker(int categoryMarker){
        this.categoryMarker = categoryMarker;
    }

    public int getCategoryMarker(){
        return categoryMarker;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }
}
