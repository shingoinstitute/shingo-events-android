package org.shingo.shingoeventsapp.data.affiliates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to hold the
 * data pertinent to affilitates
 * received from the API. The lists
 * are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class Affiliates {

    /**
     * Holds {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}s
     */
    public static List<Affiliate> AFFILIATES = new ArrayList<>();

    /**
     * Map to {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}s.
     * The key is {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate#id}
     */
    public static Map<String, Affiliate> AFFILIATE_MAP = new HashMap<>();

    /**
     * {@link Date} when data was last pulled from API
     */
    public static Date refresh;

    /**
     * If >0 data is still loading
     */
    public static int is_loading = 0;

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
     * Adds an {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate} to both {@link Affiliates#AFFILIATES}
     * and {@link Affiliates#AFFILIATE_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}.
     *
     * @param affiliate an {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}
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
     * Parse {@link Affiliates} from a JSON string
     * @param json JSON string representing {@link Affiliates}
     * @throws JSONException
     * @throws IOException
     */
    public static void fromJSON(String json) throws JSONException, IOException{
        JSONObject response = new JSONObject(json);
        clear();
        JSONArray jAffiliates = response.getJSONObject("affiliates").getJSONArray("records");
        for (int i = 0; i < jAffiliates.length(); i++)
            addAffiliate(Affiliate.fromJSON(jAffiliates.getJSONObject(i)));
    }

    /**
     * Holder class for Affiliate data.
     */
    public static class Affiliate implements Comparable<Affiliate>{
        /**
         * SalesForce ID
         */
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
         * Used to parse an {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate} from a {@link JSONObject}
         * @param jAffiliate {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}
         * @return {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}
         * @throws JSONException
         * @throws IOException
         */
        public static Affiliate fromJSON(JSONObject jAffiliate) throws JSONException, IOException{
            if(!jAffiliate.getString("Logo__c").equals("null"))
                getLogo(jAffiliate.getString("Logo__c"),jAffiliate.getString("Id"));

            return new Affiliates.Affiliate(jAffiliate.getString("Id"),
                    jAffiliate.getString("Name"), jAffiliate.getString("Rich_App_Abstract"),
                    null, jAffiliate.getString("Website"), jAffiliate.getString("Phone"),
                    jAffiliate.getString("Public_Contact_Email__c"));
        }

        /**
         * Get the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}'s Logo
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}'s logo
         * @param id the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate#id}
         */
        public static void getLogo(final String url, final String id){
            is_loading++;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(AFFILIATE_MAP.containsKey(id)) AFFILIATE_MAP.get(id).logo = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         *
         * @param another another {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate} to compare to
         * @return this.name.compareTo(another.name)
         */
        @Override
        public int compareTo(@NonNull Affiliate another) {
            return this.name.compareTo(another.name);
        }
    }
}
