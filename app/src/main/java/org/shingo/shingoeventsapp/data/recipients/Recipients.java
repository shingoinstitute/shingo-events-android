package org.shingo.shingoeventsapp.data.recipients;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
 * Created by dustinehoman on 2/18/16.
 */
public class Recipients {

    public static List<AwardRecipient> AWARD_RECIPIENTS = new ArrayList<>();
    public static Map<String, AwardRecipient> AWARD_RECIPIENT_MAP = new HashMap<>();

    public static List<ResearchRecipient> RESEARCH_RECIPIENTS = new ArrayList<>();
    public static Map<String,ResearchRecipient> RESEARCH_RECIPIENT_MAP = new HashMap<>();

    public static Date refresh;

    public static int awards_is_ready = 0;
    public static int research_is_ready = 0;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    public static void addAwardRecipient(AwardRecipient awardRecipient){
        if(AWARD_RECIPIENT_MAP.get(awardRecipient.id) == null){
            AWARD_RECIPIENTS.add(awardRecipient);
            AWARD_RECIPIENT_MAP.put(awardRecipient.id, awardRecipient);
        }
    }

    public static void addResearchRecipient(ResearchRecipient researchRecipient){
        if(RESEARCH_RECIPIENT_MAP.get(researchRecipient.id) == null){
            RESEARCH_RECIPIENTS.add(researchRecipient);
            RESEARCH_RECIPIENT_MAP.put(researchRecipient.id, researchRecipient);
        }
    }

    public static void clear(){
        AWARD_RECIPIENT_MAP.clear();
        AWARD_RECIPIENTS.clear();
        RESEARCH_RECIPIENT_MAP.clear();
        RESEARCH_RECIPIENTS.clear();
        refresh = new Date();
    }

    public static void fromJSON(String json) throws JSONException{
        Recipients.clear();
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

    public static void getLogo(final String url, final String id, final int type) throws IOException{
        if(type == 0)
            awards_is_ready++;
        else
            research_is_ready++;
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    URL image = new URL(url);
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    if(type == 0) {
                        if (AWARD_RECIPIENT_MAP.containsKey(id))
                            AWARD_RECIPIENT_MAP.get(id).logo = picture;
                        awards_is_ready--;
                    } else {
                        if (RESEARCH_RECIPIENT_MAP.containsKey(id))
                            RESEARCH_RECIPIENT_MAP.get(id).cover = picture;
                        research_is_ready--;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static class AwardRecipient implements Comparable<AwardRecipient> {
        public String id;
        public String name;
        public String Abstract;
        public String award;
        public Bitmap logo;

        public AwardRecipient(String id, String name, String Abstract, String award, Bitmap logo)
        {
            this.id = id;
            this.name = name;
            this.Abstract = Abstract;
            this.award = award;
            this.logo = logo;
            if(Abstract.equals("null")) this.Abstract = "Abstract coming soon!";
        }

        public static AwardRecipient fromJSON(JSONObject jRecipient) throws JSONException{
            try{
                getLogo(jRecipient.getString("Logo_Book_Cover__c"), jRecipient.getString("Id"), 0);
            } catch (IOException e){
                e.printStackTrace();
            }
            return new Recipients.AwardRecipient(jRecipient.getString("Id"), jRecipient.getString("Name"),
                    jRecipient.getString("Rich_Abstract"), jRecipient.getString("Award__c"), null);
        }

        @Override
        public int compareTo(AwardRecipient another) {
            if(this.award.equals(another.award)) return this.name.compareTo(another.name);
            if(this.award.equals("Shingo Prize")) return 1;
            if(this.award.equals("Silver Medallion") && !another.award.equals("Shingo Prize")) return 1;

            return -1;
        }
    }

    public static class ResearchRecipient implements Comparable<ResearchRecipient> {
        public String id;
        public String book;
        public String Abstract;
        public String author;
        public Bitmap cover;

        public ResearchRecipient(String id, String book, String Abstract, String author, Bitmap cover){
            this.id = id;
            this.book = book;
            this.Abstract = Abstract;
            this.author = author;
            this.cover = cover;
            if(Abstract.equals("null")) this.Abstract = "Abstract coming soon!";
        }

        public static ResearchRecipient fromJSON(JSONObject jRecipient) throws JSONException{
            try{
                getLogo(jRecipient.getString("Logo_Book_Cover__c"), jRecipient.getString("Id"), 1);
            } catch (IOException e){
                e.printStackTrace();
            }
            return new Recipients.ResearchRecipient(jRecipient.getString("Id"), jRecipient.getString("Name"),
                    jRecipient.getString("Rich_Abstract"), jRecipient.getString("Author_s__c"), null);
        }

        @Override
        public int compareTo(ResearchRecipient another) {
            return this.book.compareTo(another.book);
        }
    }
}
