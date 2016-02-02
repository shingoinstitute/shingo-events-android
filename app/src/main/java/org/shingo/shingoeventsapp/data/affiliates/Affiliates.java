package org.shingo.shingoeventsapp.data.affiliates;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 2/2/16.
 */
public class Affiliates {

    public static List<Affiliate> AFFILIATES = new ArrayList<>();

    public static Map<String, Affiliate> AFFILIATE_MAP = new HashMap<>();

    public static void addAffiliate(Affiliate affiliate){
        if(AFFILIATE_MAP.get(affiliate.id) == null){
            AFFILIATES.add(affiliate);
            AFFILIATE_MAP.put(affiliate.id, affiliate);
        }
    }

    public static void clear(){
        AFFILIATES.clear();
        AFFILIATE_MAP.clear();
    }

    public static class Affiliate implements Comparable<Affiliate>{
        public String id;
        public String name;
        public String appAbstract;
        public Bitmap logo;
        public String website;
        public String phone;
        public String email;

        public Affiliate(String id, String name, String appAbstract,
                         Bitmap logo, String website, String phone,
                         String email){
            this.id = id;
            this.name = name;
            this.appAbstract = appAbstract;
            this.logo = logo;
            this.website = website;
            this.phone = phone;
            this.email = email;
        }

        @Override
        public int compareTo(Affiliate another) {
            return this.name.compareTo(another.name);
        }
    }
}
