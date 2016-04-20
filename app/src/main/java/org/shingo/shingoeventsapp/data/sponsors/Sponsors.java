package org.shingo.shingoeventsapp.data.sponsors;

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
 * data pertinent to sponsors
 * received from the API. The lists
 * are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class Sponsors {

    /**
     * Holds {@link List<org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor>}s
     */
    public static Map<String, List<Sponsor>> SPONSORS = new HashMap<>();
    /**
     * Map to {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}s.
     * Key is {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor#id}
     */
    public static Map<String, Sponsor> SPONSOR_MAP = new HashMap<>();

    /**
     * {@link Date} the data was last pulled from API
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
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    /**
     * Adds an {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor} to both {@link Sponsors#SPONSORS}
     * and {@link Sponsors#SPONSOR_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}.
     *
     * @param sponsor an {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}
     */
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

    /**
     * Used to clear {@link Sponsors#SPONSORS} and
     * {@link Sponsors#SPONSOR_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        refresh = new Date();
        SPONSORS.clear();
        SPONSOR_MAP.clear();
    }

    /**
     * Parse {@link Sponsors} from a JSON string
     * @param json JSON string representing {@link Sponsors}
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException {
        clear();
        JSONObject response = new JSONObject(json);
        JSONObject jsonObject = response.getJSONObject("sponsors");
        List<JSONArray> sponsors = new ArrayList<>();
        sponsors.add(jsonObject.getJSONObject("friends").getJSONArray("records"));
        sponsors.add(jsonObject.getJSONObject("supporters").getJSONArray("records"));
        sponsors.add(jsonObject.getJSONObject("benefactors").getJSONArray("records"));
        sponsors.add(jsonObject.getJSONObject("champions").getJSONArray("records"));
        sponsors.add(jsonObject.getJSONObject("presidents").getJSONArray("records"));
        for(JSONArray jSponsors : sponsors){
            for(int i = 0; i < jSponsors.length(); i++){
                Sponsors.addSponsor(Sponsor.fromJSON(jSponsors.getJSONObject(i)));
            }
        }
    }

    /**
     * Holder class for Sponsor data.
     */
    public static class Sponsor implements Comparable<Sponsor>{
        /**
         * SalesForce ID
         */
        public String id;
        public String name;
        public Bitmap banner;
        public Bitmap logo;
        /**
         * Friend | Supporter | Benefactor | Champion | President
         */
        public String level;

        /**
         *
         * @param id SalesForce ID of sponsor
         * @param name The name of the sponsor
         * @param level The level of the sponsorship
         * @param banner The banner of the sponsor
         * @param logo The sponsor's logo
         */
        public Sponsor(String id, String name, String level, Bitmap banner, Bitmap logo){
            this.id = id;
            this.name = name;
            this.level = level;
            this.banner = banner;
            this.logo = logo;
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor} from a {@link JSONObject}
         * @param jSponsor {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}
         * @return {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}
         * @throws JSONException
         */
        public static Sponsor fromJSON(JSONObject jSponsor) throws JSONException{
            String level = jSponsor.getString("Level__c");
            if(jSponsor.has("Logo__c"))
                getLogo(jSponsor.getString("Logo__c"), jSponsor.getString("Id"));
            if(jSponsor.has("Banner__c"))
                getBanner(jSponsor.getString("Banner__c"), jSponsor.getString("Id"));
            return new Sponsors.Sponsor(jSponsor.getString("Id"), jSponsor.getString("Name"),
                        level, null, null);
        }

        /**
         * Get the {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}'s Logo
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}'s logo
         * @param id the {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor#id}
         */
        public static void getLogo(final String url, final String id){
            is_loading++;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(SPONSOR_MAP.containsKey(id)) SPONSOR_MAP.get(id).logo = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Get the {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}'s Logo
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}'s banner
         * @param id the {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor#id}
         */
        public static void getBanner(final String url, final String id){
            is_loading++;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(SPONSOR_MAP.containsKey(id)) SPONSOR_MAP.get(id).banner = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Compares this to another {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}
         * @param another {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors.Sponsor}
         * @return sort based on level
         */
        @Override
        public int compareTo(@NonNull Sponsor another) {
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
