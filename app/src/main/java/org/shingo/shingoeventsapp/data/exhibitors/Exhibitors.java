package org.shingo.shingoeventsapp.data.exhibitors;

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
 * Created by dustinehoman on 2/10/16.
 */
public class Exhibitors {

    public static List<Exhibitor> EXHIBITORS = new ArrayList<>();

    public static Map<String, Exhibitor> EXHIBITOR_MAP = new HashMap<>();

    public static Date refresh;

    public static int is_ready = 0;
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

    public static void fromJSON(String json) throws JSONException{
        Exhibitors.clear();
        JSONObject response = new JSONObject(json);
        JSONArray jExhibitors = response.getJSONObject("exhibitors").getJSONArray("records");
        for(int i = 0; i < jExhibitors.length(); i++){
            Exhibitors.addExhibitor(Exhibitor.fromJSON(jExhibitors.getJSONObject(i)));
        }
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

        public static void getLogo(String url, String id) throws IOException{
            is_ready++;
            final String logoURL = url;
            final String affID = id;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(logoURL);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(EXHIBITOR_MAP.containsKey(affID)) EXHIBITOR_MAP.get(affID).logo = picture;
                        is_ready--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        @Override
        public int compareTo(Exhibitor another) {
            return this.name.compareTo(another.name);
        }
    }
}
