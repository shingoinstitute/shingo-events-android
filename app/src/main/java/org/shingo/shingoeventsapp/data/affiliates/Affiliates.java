package org.shingo.shingoeventsapp.data.affiliates;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dustin Homan
 *
 * This class is used to hold the
 * data pertinate to affilitates
 * recieved from the API. The lists
 * are static to reduce calls to the API.
 */
public class Affiliates {

    public static List<Affiliate> AFFILIATES = new ArrayList<>();

    public static Map<String, Affiliate> AFFILIATE_MAP = new HashMap<>();

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
        return now.after(new Date(refresh.getTime() + (30 * 60000)));
    }

    /**
     * Adds an affiliate object to both {@link Affiliates#AFFILIATES}
     * and {@link Affiliates#AFFILIATE_MAP} if the map does not contain
     * the passed in Affiliate.
     *
     * @param affiliate an Affiliate object
     */
    public static void addAffiliate(Affiliate affiliate){
        if(AFFILIATE_MAP.get(affiliate.id) == null){
            AFFILIATES.add(affiliate);
            AFFILIATE_MAP.put(affiliate.id, affiliate);
        }
    }

    /**
     * Used to clear {@link Affiliates#AFFILIATES} and
     * {@link Affiliates#AFFILIATE_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        AFFILIATES.clear();
        AFFILIATE_MAP.clear();
        refresh = new Date();
    }

    /**
     * Holder class for Affiliate data.
     */
    public static class Affiliate implements Comparable<Affiliate>{
        public String id;
        public String name;
        public String appAbstract;
        public Bitmap logo;
        public String website;
        public String phone;
        public String email;

        /**
         *
         * @param id the SF id of the Affiliate
         * @param name the Affiliate's name
         * @param appAbstract the abstract of the Affiliate (null)
         * @param logo a Bitmap of the Affiliate's logo (null)
         * @param website the URL to the Affiliate's website (null)
         * @param phone the public contact of the Affiliate (null)
         * @param email the public contact email of the Affiliate (null)
         */
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
            if(appAbstract.equals("null")) this.appAbstract = "Abstract coming soon!";
        }

        /**
         *
         * @param another another Affiliate to compare to
         * @return this.name.compareTo(another.name)
         */
        @Override
        public int compareTo(@NonNull Affiliate another) {
            return this.name.compareTo(another.name);
        }
    }
}
