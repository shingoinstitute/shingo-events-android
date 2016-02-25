package org.shingo.shingoeventsapp.data.venuemaps;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dustinehoman on 2/24/16.
 */
public class VenueMaps {

    public static List<VMap> MAPS = new ArrayList<>();
    public static Date refresh;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (60 * 60000)));
    }

    public static void clear(){
        MAPS.clear();
        refresh = new Date();
    }

    public static void addMap(VMap vMap) {
        if(!MAPS.contains(vMap))
            MAPS.add(vMap);
    }

    public static class VMap implements Comparable<VMap> {
        public String name;
        public Bitmap map;

        public VMap(String name, Bitmap map){
            this.name = name;
            this.map = map;
        }

        @Override
        public int compareTo(VMap another) {
            return this.name.compareTo(another.name);
        }
    }

}
