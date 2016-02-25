package org.shingo.shingoeventsapp.data.exhibitors;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 2/10/16.
 */
public class Exhibitors {

    public static List<Exhibitor> EXHIBITORS = new ArrayList<>();

    public static Map<String, Exhibitor> EXHIBITOR_MAP = new HashMap<>();

    public static Date refresh;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    public static void addExhibitor(Exhibitor exhibitor){
        if(EXHIBITOR_MAP.get(exhibitor.id) == null){
            EXHIBITORS.add(exhibitor);
            EXHIBITOR_MAP.put(exhibitor.id, exhibitor);
        }
    }

    public static void clear(){
        EXHIBITORS.clear();
        EXHIBITOR_MAP.clear();
        refresh = new Date();
    }

    public static class Exhibitor implements Comparable<Exhibitor>{
        public String id;
        public String name;
        public String description;
        public String phone;
        public String email;
        public String website;
        public Bitmap logo;

        public Exhibitor(String id, String name, String description, String phone,
                         String email, String website, Bitmap logo){
            this.id = id;
            this.name = name;
            this.description = description;
            this.phone = phone;
            this.email = email;
            this.website = website;
            this.logo = logo;
        }

        @Override
        public int compareTo(Exhibitor another) {
            return this.name.compareTo(another.name);
        }
    }
}
