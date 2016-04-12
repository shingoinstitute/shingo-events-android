package org.shingo.shingoeventsapp.data.sessions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.data.speakers.Speakers;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class Sessions {

    public static List<Session> SESSIONS = new ArrayList<>();

    public static Map<String, Session> SESSION_MAP = new HashMap<>();

    public static Date refresh;

    public static int speaker_images = 0;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    public static void addSession(Session session){
        if(SESSION_MAP.get(session.id) == null){
            SESSIONS.add(session);
            SESSION_MAP.put(session.id, session);
        }
    }

    public static void clear(){
        SESSIONS.clear();
        SESSION_MAP.clear();
        refresh = new Date();
    }

    public static void fromJSON(String json) throws JSONException{
        Sessions.clear();
        JSONObject response = new JSONObject(json);
        JSONArray jSessions = response.getJSONObject("sessions").getJSONArray("records");
        for(int i = 0; i < response.getJSONObject("sessions").getInt("size"); i++){
            Sessions.addSession(Session.fromJSON(jSessions.getJSONObject(i)));
        }
    }

    public static class Session implements Comparable<Session>{
        public String id;
        public String name;
        public String sAbstract;
        public String notes;
        public String date;
        public String time;
        public String room;
        public String startTime;
        public String endTime;
        public String format;
        public List<Speakers.Speaker> speakers;
        private SimpleDateFormat formatter;

        public Session(String id, String name, String sAbstract, String notes, String date, String format, String time, List<Speakers.Speaker> speakers, String room){
            this.id = id;
            this.name = name;
            this.sAbstract = sAbstract;
            this.notes = notes;
            this.date = date;
            this.time = time;
            this.speakers = speakers;
            this.room = room;
            this.format = format;
            this.startTime = date + " " + time.split("-")[0];
            this.endTime = date + " " + time.split("-")[1];
            this.formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            if(sAbstract.equals("null")) this.sAbstract = "";
            if(format.equals("null")) this.format = "";
        }

        public static Session fromJSON(JSONObject jSession) throws JSONException{
            List<Speakers.Speaker> speakers = new ArrayList<>();
            JSONArray jSpeakers = jSession.getJSONObject("Speakers").getJSONArray("records");
            for(int i = 0; i < jSpeakers.length(); i++){
                JSONObject jSpeaker = jSpeakers.getJSONObject(i);
                URL image;
                if(!jSpeaker.getString("Speaker_Image__c").equals("null")) {
                    speaker_images++;
                    getImage(jSpeaker.getString("Speaker_Image__c"), speakers, i);
                }
                speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                        jSpeaker.getString("Name"), jSpeaker.getString("Name"), jSpeaker.getString("Title"),
                        jSpeaker.getString("Organization"), "", null));
            }
            if(!jSession.has("Room")) jSession.put("Room", "null");
            while(speaker_images > 0) {/* wait */}
            return new Sessions.Session(jSession.getString("Id"),
                    jSession.getString("Name"),jSession.getString("Rich_Session_Abstract"),
                    jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"), jSession.getString("Session_Format__c"),
                    jSession.getString("Session_Time__c"), speakers, jSession.getString("Room"));
        }

        public static void getImage(final String url, final List<Speakers.Speaker> speakers, final int index) throws JSONException{
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        speakers.get(index).picture = picture;
                        speaker_images--;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        @Override
        public String toString() { return name; }

        @Override
        public int compareTo(Session another) {
            Date thisStart = null;
            Date anotherStart = null;
            Date thisEnd = null;
            Date anotherEnd = null;
            try {
                thisStart = formatter.parse(this.startTime);
                anotherStart = formatter.parse(another.startTime);
                thisEnd = formatter.parse(this.endTime);
                anotherEnd = formatter.parse(another.endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(thisStart.compareTo(anotherStart) == 0)
                if(thisEnd.compareTo(anotherEnd) == 0)return name.compareTo(another.name);
                else return thisEnd.compareTo(anotherEnd);

            return thisStart.compareTo(anotherStart);
        }
    }
}
