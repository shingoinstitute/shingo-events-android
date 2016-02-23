package org.shingo.shingoeventsapp.data.sponsors;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 2/23/16.
 */
public class Sponsors {

    public static Map<String, List<Sponsor>> SPONSORS = new HashMap<>();
    public static Map<String, Sponsor> SPONSOR_MAP = new HashMap<>();

    public static Date refresh;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    public static void addSponsor(Sponsor sponsor){
        if(SPONSOR_MAP.get(sponsor.id) == null){
            if(SPONSORS.get(sponsor.level) == null){
                List<Sponsor> list = new ArrayList<>();
                list.add(sponsor);
                SPONSORS.put(sponsor.level, list);
            } else {
                SPONSORS.get(sponsor.level).add(sponsor);
            }
            SPONSOR_MAP.put(sponsor.id, sponsor);
        }
    }

    public static void clear(){
        refresh = new Date();
        SPONSORS.clear();
        SPONSOR_MAP.clear();
    }

    public static class Sponsor implements Comparable<Sponsor>{
        public String id;
        public String name;
        public Bitmap banner;
        public Bitmap logo;
        public String level;

        public Sponsor(String id, String name, String level, Bitmap banner, Bitmap logo){
            this.id = id;
            this.name = name;
            this.level = level;
            this.banner = banner;
            this.logo = logo;
        }

        @Override
        public int compareTo(Sponsor another) {
            if(level.equals(another.level)) return name.compareTo(another.name);
            if(level.equals("Friend")) return -1;
            if(level.equals("President")) return 1;
            if(another.level.equals("Friend")) return 1;
            if(another.level.equals("President")) return -1;
            if(level.equals("Supporter")) return -1;
            if(level.equals("Benefactor") && another.level.equals("Champion")) return -1;
            return 1;
        }
    }
}
