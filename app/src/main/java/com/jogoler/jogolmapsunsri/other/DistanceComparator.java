package com.jogoler.jogolmapsunsri.other;

import com.jogoler.jogolmapsunsri.model.Location;

import java.util.Comparator;

/**
 * Created by RazorX on 12/15/2016.
 */

public class DistanceComparator implements Comparator<Location> {

    public String optionOrder;
    public final static String ASC = "ASC";
    public final static String DESC = "DESC";

    public DistanceComparator(String optionOrder)
    {
        this.optionOrder = optionOrder;
    }
    @Override
    public int compare(Location o1, Location o2) {
        if(optionOrder==ASC)
        {
            //ascending
            return o1.getDistance().compareTo(o2.getDistance());
        }
        else if(optionOrder==DESC)
        {
            //descending
             return o2.getDistance().compareTo(o1.getDistance());
        }
        else {
            System.exit(1);
            return 0;
        }
    }
}