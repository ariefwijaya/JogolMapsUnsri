package com.jogoler.jogolmapsunsri.other;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jogoler.jogolmapsunsri.Service.GPSTracker;
import com.jogoler.jogolmapsunsri.model.Location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by RazorX on 12/14/2016.
 */

public class HaversineDistance {

    private  Double currentLatitude;
    private  Double currentLongitude;
    private  Context mContext;

    public HaversineDistance(Context context) {
        this.mContext = context;
    }

    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2){
        Double distance;
        final int R = 6371; // Radious of the earth
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        distance = R * c;
        return  distance;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public String formatDistance(Double distance){
        String formatted;
        //convert to meter if possible
        if(distance < 1 && distance >0)
        {
            formatted = new DecimalFormat("0.##").format(distance*1000) + " m";
        }
        else if(distance>1)
        {
            formatted = new DecimalFormat("0.##").format(distance) + " km";
        }
        else{
            formatted = "Turn on your location";
        }

        return formatted;
    }


    public List<Location> sortEveryLocationByDistance(Cursor cursor){

        List<Location> locationList= new ArrayList<>();
        Location location;

        if (cursor.moveToFirst()) {
            do {
                location = new Location();
                location.setId(cursor.getInt(0));
                location.setThumb(cursor.getString(1));
                location.setName(cursor.getString(2));

                //calculate distance for near place
                Double refLatitude = cursor.getDouble(3);
                Double refLongitude = cursor.getDouble(4);
                Double distance = calculateDistance(currentLatitude,currentLongitude,refLatitude,refLongitude);
                if(currentLatitude==0.0 && currentLongitude == 0.0) {distance=0.0;}
                location.setDistance(distance);
                //end calculate distance
                locationList.add(location);
            }while (cursor.moveToNext());
        }
        //sort data by distance Ascending
        Collections.sort(locationList, new DistanceComparator(DistanceComparator.ASC));
        return locationList;
    }

    public void setCurrentLatitude(Double currentLatitude){
        this.currentLatitude = currentLatitude;
    }

    public void setCurrentLongitude(Double currentLongitude){
        this.currentLongitude = currentLongitude;
    }

}
