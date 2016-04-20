package org.shingo.shingoeventsapp.data.venuemaps;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used to hold the
 * data pertinent to Venue Maps
 * received from the API. The lists
 * are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class VenueMaps {

    /**
     * Holds {@link org.shingo.shingoeventsapp.data.venuemaps.VenueMaps.VMap}s
     */
    public static List<VMap> MAPS = new ArrayList<>();

    /**
     * {@link Date} the data was last pulled from the API
     */
    public static Date refresh;

    /**
     * A check to see when the data was last pulled from the API. If
     * it was longer than 30 minutes the data needs refreshed.
     *
     * @return now > refresh + 30 min
     */
    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (60 * 60000)));
    }

    public static void addMap(VMap vMap) {
        if(!MAPS.contains(vMap))
            MAPS.add(vMap);
    }

    /**
     * Used to clear {@link VenueMaps#MAPS} and
     * in prep for an API call.
     */
    public static void clear(){
        MAPS.clear();
        refresh = new Date();
    }

    /**
     * Holder class for VMap data.
     */
    public static class VMap implements Comparable<VMap> {
        public String name;
        public Bitmap map;

        /**
         *
         * @param name Name of the map
         * @param map {@link Bitmap} of the map
         */
        public VMap(String name, Bitmap map){
            this.name = name;
            this.map = map;
        }

        /**
         * Compares this to another {@link org.shingo.shingoeventsapp.data.venuemaps.VenueMaps.VMap}
         * @param another {@link org.shingo.shingoeventsapp.data.venuemaps.VenueMaps.VMap}
         * @return {@link org.shingo.shingoeventsapp.data.venuemaps.VenueMaps.VMap#name}.compareTo(another.name);
         */
        @Override
        public int compareTo(@NonNull VMap another) {
            return this.name.compareTo(another.name);
        }
    }

}
