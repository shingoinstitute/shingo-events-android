package org.shingo.shingoeventsapp.data.recipients;

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
 * data pertinent to the {@link Recipients}.
 * The lists are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class Recipients {

    /**
     * Holds {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}s
     */
    public static List<AwardRecipient> AWARD_RECIPIENTS = new ArrayList<>();
    /**
     * Map to {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}s.
     * Key is {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient#id}
     */
    public static Map<String, AwardRecipient> AWARD_RECIPIENT_MAP = new HashMap<>();

    /**
     * Holds {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}s
     */
    public static List<ResearchRecipient> RESEARCH_RECIPIENTS = new ArrayList<>();
    /**
     * Map to {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}s.
     * Key is {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient#id}
     */
    public static Map<String,ResearchRecipient> RESEARCH_RECIPIENT_MAP = new HashMap<>();

    /**
     * {@link Date} the data was last pulled from API
     */
    public static Date refresh;

    /**
     * If >0 data is still loading
     */
    public static int awards_is_loading = 0;
    /**
     * If >0 data is still loading
     */
    public static int research_is_loading = 0;

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
     * Adds an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient} to both {@link Recipients#AWARD_RECIPIENTS}
     * and {@link Recipients#AWARD_RECIPIENT_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}.
     *
     * @param awardRecipient an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}
     */
    public static void addAwardRecipient(AwardRecipient awardRecipient){
        if(AWARD_RECIPIENT_MAP.get(awardRecipient.id) == null){
            AWARD_RECIPIENTS.add(awardRecipient);
            AWARD_RECIPIENT_MAP.put(awardRecipient.id, awardRecipient);
        }
    }

    /**
     * Adds an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient} to both {@link Recipients#RESEARCH_RECIPIENTS}
     * and {@link Recipients#RESEARCH_RECIPIENT_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}.
     *
     * @param researchRecipient an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}
     */
    public static void addResearchRecipient(ResearchRecipient researchRecipient){
        if(RESEARCH_RECIPIENT_MAP.get(researchRecipient.id) == null){
            RESEARCH_RECIPIENTS.add(researchRecipient);
            RESEARCH_RECIPIENT_MAP.put(researchRecipient.id, researchRecipient);
        }
    }

    /**
     * Used to clear {@link Recipients#AWARD_RECIPIENTS},
     * {@link Recipients#AWARD_RECIPIENT_MAP},
     * {@link Recipients#RESEARCH_RECIPIENTS}, and
     * {@link Recipients#RESEARCH_RECIPIENT_MAP}in prep for an
     * API call.
     */
    public static void clear(){
        AWARD_RECIPIENT_MAP.clear();
        AWARD_RECIPIENTS.clear();
        RESEARCH_RECIPIENT_MAP.clear();
        RESEARCH_RECIPIENTS.clear();
        refresh = new Date();
    }

    /**
     * Parse {@link Recipients} from a JSON string
     * @param json JSON string representing {@link Recipients}
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException{
        clear();
        JSONObject response = new JSONObject(json);
        JSONArray jAwardRecipients = response.getJSONObject("award_recipients").getJSONArray("records");
        for(int i = 0; i < jAwardRecipients.length(); i++){
            Recipients.addAwardRecipient(AwardRecipient.fromJSON(jAwardRecipients.getJSONObject(i)));
        }

        JSONArray jResearchRecipients = response.getJSONObject("research_recipients").getJSONArray("records");
        for(int i = 0; i < jResearchRecipients.length(); i++){
            Recipients.addResearchRecipient(ResearchRecipient.fromJSON(jResearchRecipients.getJSONObject(i)));
        }
    }

    /**
     * Get the Logo of the recipient asynchronously
     * @param url The URL of the logo
     * @param id The SF id of the recipient
     * @param type {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}==0 and {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}==1
     * @throws IOException
     */
    public static void getLogo(final String url, final String id, final int type) throws IOException{
        if(type == 0)
            awards_is_loading++;
        else
            research_is_loading++;
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    URL image = new URL(url);
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    if(type == 0) {
                        if (AWARD_RECIPIENT_MAP.containsKey(id))
                            AWARD_RECIPIENT_MAP.get(id).logo = picture;
                        awards_is_loading--;
                    } else {
                        if (RESEARCH_RECIPIENT_MAP.containsKey(id))
                            RESEARCH_RECIPIENT_MAP.get(id).cover = picture;
                        research_is_loading--;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /**
     * Holder class for AwardRecipients
     */
    public static class AwardRecipient implements Comparable<AwardRecipient> {
        /**
         * SalesForce ID
         */
        public String id;
        public String name;
        public String Abstract;
        public String award;
        public Bitmap logo;

        /**
         *
         * @param id SalesForce ID of the Recipient
         * @param name Name of the Recipient
         * @param Abstract An abstract of the Recipient
         * @param award The award level of the Recipient
         * @param logo Bitmap of the Recipient's Logo
         */
        public AwardRecipient(String id, String name, String Abstract, String award, Bitmap logo)
        {
            this.id = id;
            this.name = name;
            this.Abstract = Abstract;
            this.award = award;
            this.logo = logo;
            if(Abstract.equals("null")) this.Abstract = "Abstract coming soon!";
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient} from a {@link JSONObject}
         * @param jRecipient {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}
         * @return {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}
         * @throws JSONException
         */
        public static AwardRecipient fromJSON(JSONObject jRecipient) throws JSONException{
            try{
                getLogo(jRecipient.getString("Logo_Book_Cover__c"), jRecipient.getString("Id"), 0);
            } catch (IOException e){
                e.printStackTrace();
            }
            return new Recipients.AwardRecipient(jRecipient.getString("Id"), jRecipient.getString("Name"),
                    jRecipient.getString("Rich_Abstract"), jRecipient.getString("Award__c"), null);
        }

        /**
         * Compares this to another {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}
         * @param another {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient}
         * @return value based on sorting by {@link org.shingo.shingoeventsapp.data.recipients.Recipients.AwardRecipient#award}
         */
        @Override
        public int compareTo(@NonNull AwardRecipient another) {
            if(this.award.equals(another.award)) return this.name.compareTo(another.name);
            if(this.award.equals("Shingo Prize")) return 1;
            if(this.award.equals("Silver Medallion") && !another.award.equals("Shingo Prize")) return 1;

            return -1;
        }
    }

    /**
     * Holder class for ResearchRecipient
     */
    public static class ResearchRecipient implements Comparable<ResearchRecipient> {
        /**
         * SalesForce ID
         */
        public String id;
        public String book;
        public String Abstract;
        public String author;
        public Bitmap cover;

        /**
         *
         * @param id SalesForce ID of the Recipient
         * @param book Name of the winning book
         * @param Abstract An abstract of the winning book
         * @param author The author of the winning book
         * @param cover Bitmap of the Recipient's Book Cover
         */
        public ResearchRecipient(String id, String book, String Abstract, String author, Bitmap cover){
            this.id = id;
            this.book = book;
            this.Abstract = Abstract;
            this.author = author;
            this.cover = cover;
            if(Abstract.equals("null")) this.Abstract = "Abstract coming soon!";
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient} from a {@link JSONObject}
         * @param jRecipient {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}
         * @return {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}
         * @throws JSONException
         */
        public static ResearchRecipient fromJSON(JSONObject jRecipient) throws JSONException{
            try{
                getLogo(jRecipient.getString("Logo_Book_Cover__c"), jRecipient.getString("Id"), 1);
            } catch (IOException e){
                e.printStackTrace();
            }
            return new Recipients.ResearchRecipient(jRecipient.getString("Id"), jRecipient.getString("Name"),
                    jRecipient.getString("Rich_Abstract"), jRecipient.getString("Author_s__c"), null);
        }

        /**
         * Compares this to another {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}
         * @param another {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient}
         * @return {@link org.shingo.shingoeventsapp.data.recipients.Recipients.ResearchRecipient#book}.compareTo(another.book)
         */
        @Override
        public int compareTo(@NonNull ResearchRecipient another) {
            return this.book.compareTo(another.book);
        }
    }
}
