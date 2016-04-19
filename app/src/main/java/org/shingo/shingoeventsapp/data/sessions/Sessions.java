package org.shingo.shingoeventsapp.data.sessions;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.data.speakers.Speakers;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used to hold the
 * data pertinent to sessions
 * received from the API. The lists
 * are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class Sessions {

    public static List<Session> SESSIONS = new ArrayList<>();

    public static Map<String, Session> SESSION_MAP = new HashMap<>();

    public static Date refresh;

    public static int speaker_images = 0;

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
     * Adds an {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session} to both {@link Sessions#SESSIONS}
     * and {@link Sessions#SESSION_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}.
     *
     * @param session an {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
     */
    public static void addSession(Session session){
        if(SESSION_MAP.get(session.id) == null){
            SESSIONS.add(session);
            SESSION_MAP.put(session.id, session);
        }
    }

    /**
     * Used to clear {@link Sessions#SESSIONS} and
     * {@link Sessions#SESSION_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        SESSIONS.clear();
        SESSION_MAP.clear();
        refresh = new Date();
    }

    /**
     * Parse {@link Sessions} from a JSON string
     * @param json JSON string representing {@link Sessions}
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException{
        clear();
        JSONObject response = new JSONObject(json);
        JSONArray jSessions = response.getJSONObject("sessions").getJSONArray("records");
        for(int i = 0; i < response.getJSONObject("sessions").getInt("size"); i++){
            Sessions.addSession(Session.fromJSON(jSessions.getJSONObject(i)));
        }
    }

    /**
     * Holder class for Session data.
     */
    public static class Session implements Comparable<Session>{
        /**
         * SalesForce ID of the session
         */
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

        /**
         *
         * @param id SalesForce ID of the session
         * @param name The name of the session
         * @param sAbstract The abstract of the session
         * @param notes The notes of the session
         * @param date The date of the session ('yyyy-MM-dd')
         * @param format The format of the session
         * @param time The time of the session ('hh:mm [AM|PM]')
         * @param speakers a list of {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}s
         * @param room The room the session is being held in
         */
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
            this.formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            if(sAbstract.equals("null")) this.sAbstract = "";
            if(format.equals("null")) this.format = "";
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session} from a {@link JSONObject}
         * @param jSession {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
         * @return {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
         * @throws JSONException
         */
        public static Session fromJSON(JSONObject jSession) throws JSONException{
            List<Speakers.Speaker> speakers = new ArrayList<>();
            JSONArray jSpeakers = jSession.getJSONObject("Speakers").getJSONArray("records");
            for(int i = 0; i < jSpeakers.length(); i++){
                JSONObject jSpeaker = jSpeakers.getJSONObject(i);
                if(!jSpeaker.getString("Speaker_Image__c").equals("null")) {
                    speaker_images++;
                    getImage(jSpeaker.getString("Speaker_Image__c"), speakers, i);
                }
                speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                        jSpeaker.getString("Name"), jSpeaker.getString("Name"), jSpeaker.getString("Title"),
                        jSpeaker.getString("Organization"), "", null));
            }
            if(!jSession.has("Room")) jSession.put("Room", "null");

            while(speaker_images > 0) {
                /* wait */
            }
            return new Sessions.Session(jSession.getString("Id"),
                    jSession.getString("Name"),jSession.getString("Rich_Session_Abstract"),
                    jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"), jSession.getString("Session_Format__c"),
                    jSession.getString("Session_Time__c"), speakers, jSession.getString("Room"));
        }

        /**
         * Gets the speakers' pictures
         * @param url URL to image
         * @param speakers {@link List<org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker>}
         * @param index index into speakers
         * @throws JSONException
         */
        public static void getImage(final String url, final List<Speakers.Speaker> speakers, final int index) throws JSONException{
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        speakers.get(index).picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        speaker_images--;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Overload of {@link Object#toString()}
         * @return {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session#name}
         */
        @Override
        public String toString() { return name; }

        /**
         * Compares this to another {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
         * @param another {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
         * @return this start date compareTo(another start date)
         */
        @Override
        public int compareTo(@NonNull Session another) {
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
            assert thisStart != null;
            assert anotherStart != null;
            if(thisStart.compareTo(anotherStart) == 0) {
                assert thisEnd != null;
                assert anotherEnd != null;
                if(thisEnd.compareTo(anotherEnd) == 0)return name.compareTo(another.name);
                else return thisEnd.compareTo(anotherEnd);
            }

            return thisStart.compareTo(anotherStart);
        }
    }
}
