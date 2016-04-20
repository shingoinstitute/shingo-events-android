package org.shingo.shingoeventsapp.data.exhibitors;

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
 * data pertinent to the {@link Exhibitors}.
 * The lists are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class Exhibitors {

    /**
     * Holds {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}s
     */
    public static List<Exhibitor> EXHIBITORS = new ArrayList<>();

    /**
     * Map to {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}s
     * Key is {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor#id}
     */
    public static Map<String, Exhibitor> EXHIBITOR_MAP = new HashMap<>();

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
     * Used to add an {@link org.shingo.shingoeventsapp.data.events.Events.Event}
     * to {@link Exhibitors#EXHIBITORS} and {@link Exhibitors#EXHIBITOR_MAP}
     * @param exhibitor the {@link org.shingo.shingoeventsapp.data.events.Events.Event} to add
     */
    public static void addExhibitor(Exhibitor exhibitor){
        if(EXHIBITOR_MAP.get(exhibitor.id) == null){
            EXHIBITORS.add(exhibitor);
            EXHIBITOR_MAP.put(exhibitor.id, exhibitor);
        }
    }

    /**
     * Clear {@link Exhibitors#EXHIBITORS} and {@link Exhibitors#EXHIBITOR_MAP}
     */
    public static void clear(){
        EXHIBITORS.clear();
        EXHIBITOR_MAP.clear();
        refresh = new Date();
    }

    /**
     * Used to parse {@link Exhibitors} from a JSON string
     * @param json JSON string representing a list of {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}s
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException{
        clear();
        JSONObject response = new JSONObject(json);
        JSONArray jExhibitors = response.getJSONObject("exhibitors").getJSONArray("records");
        for(int i = 0; i < jExhibitors.length(); i++){
            Exhibitors.addExhibitor(Exhibitor.fromJSON(jExhibitors.getJSONObject(i)));
        }
    }

    /**
     * Class to hold data for a particular Exhibitor
     */
    public static class Exhibitor implements Comparable<Exhibitor>{
        /**
         * SalesForce ID
         */
        public String id;
        public String name;
        public String description;
        public String phone;
        public String email;
        public String website;
        public Bitmap logo;

        /**
         *
         * @param id SalesForce ID of the Exhibitor
         * @param name Name of the Exhibitor
         * @param description Description of the Exhibitor
         * @param phone Public contact for the Exhibitor
         * @param email Public contact for the Exhibitor
         * @param website URL to the Exhibitor's site
         * @param logo URL to the Exhibitor's logo
         */
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

        /**
         * A method to parse an {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor} from a {@link JSONObject}
         * @param jExhibitor a {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}
         * @return a new {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}
         * @throws JSONException
         */
        public static Exhibitor fromJSON(JSONObject jExhibitor) throws JSONException{
            try {
                getLogo(jExhibitor.getString("Logo__c"), jExhibitor.getString("Id"));
            } catch (Exception e){
                e.printStackTrace();
            }

            return new Exhibitors.Exhibitor(jExhibitor.getString("Id"), jExhibitor.getString("Name"),
                    jExhibitor.getString("Rich_Description"), jExhibitor.getString("Phone__c"), jExhibitor.getString("Email__c"),
                    jExhibitor.getString("Website__c"), null);
        }

        /**
         * Get the {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}'s Logo
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}'s logo
         * @param id the {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}'s SalesForce ID
         */
        public static void getLogo(String url, String id) throws IOException{
            is_loading++;
            final String logoURL = url;
            final String affID = id;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(logoURL);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(EXHIBITOR_MAP.containsKey(affID)) EXHIBITOR_MAP.get(affID).logo = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Method to compare to another {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}
         * @param another {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}
         * @return {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor#name#compareTo(Exhibitor)}
         */
        @Override
        public int compareTo(@NonNull Exhibitor another) {
            return this.name.compareTo(another.name);
        }
    }
}
