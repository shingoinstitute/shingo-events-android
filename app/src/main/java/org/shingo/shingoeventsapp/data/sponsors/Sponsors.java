package org.shingo.shingoeventsapp.data.sponsors;

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
 * Created by dustinehoman on 2/23/16.
 */
public class Sponsors {

    public static Map<String, List<Sponsor>> SPONSORS = new HashMap<>();
    public static Map<String, Sponsor> SPONSOR_MAP = new HashMap<>();

    public static Date refresh;

    public static int is_loading = 0;

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

    public static void fromJSON(String json) throws JSONException {
        Sponsors.clear();
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

        public static Sponsor fromJSON(JSONObject jSponsor) throws JSONException{
            String level = jSponsor.getString("Level__c");
            if(jSponsor.has("Logo__c"))
                getLogo(jSponsor.getString("Logo__c"), jSponsor.getString("Id"));
            if(jSponsor.has("Banner__c"))
                getBanner(jSponsor.getString("Banner__c"), jSponsor.getString("Id"));
            return new Sponsors.Sponsor(jSponsor.getString("Id"), jSponsor.getString("Name"),
                        level, null, null);
        }

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
