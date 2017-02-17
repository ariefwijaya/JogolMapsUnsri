package com.jogoler.jogolmapsunsri.model;

/**
 * Created by RazorX on 12/13/2016.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.Comparator;

public class Location implements SearchSuggestion {

    private int id;
    private String name;
    private String thumb;
    private Double latitude;
    private Double longitude;
    private Double distance;

    private boolean mIsHistory = false;

    public Location() {
    }

    protected Location(Parcel in) {
        id = in.readInt();
        name = in.readString();
        thumb = in.readString();
        latitude = in.readDouble();
        latitude = in.readDouble();
        distance = in.readDouble();
        mIsHistory = in.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }


    @Override
    public String getBody() {
        return name;
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }
        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(thumb);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(distance);
        dest.writeInt(mIsHistory ? 1 : 0);
    }

}




