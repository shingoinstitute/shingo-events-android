package org.shingo.shingoeventsapp.data.events;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 12/29/15.
 */
public class Events {

    public static List<Event> EVENTS = new ArrayList<>();

    public static Map<String, Event> EVENT_MAP = new HashMap<>();

    public static void addEvent(Event event){
        if(EVENT_MAP.get(event.id) == null) {
            EVENTS.add(event);
            EVENT_MAP.put(event.id, event);
        }
    }

    public static void clear(){
        EVENTS.clear();
        EVENT_MAP.clear();
    }

    public static void fromJSON(String json) throws JSONException {
        JSONObject response = new JSONObject(json);
        JSONArray jSfevents = response.getJSONObject("events").getJSONArray("records");
        for (int i = 0; i < response.getJSONObject("events").getInt("size"); i++) {
            Events.addEvent(Event.fromJSON(jSfevents.getJSONObject(i)));
        }
    }

    public static class Event implements Comparable<Event>{
        public String id;
        public String name;
        public String startDate;
        public String endDate;
        public String location;
        public double lat;
        public double lng;
        public String registration;
        public List<VenueMaps> venueMaps;

        public Event(String id, String name, String startDate,
                     String endDate, String location, double lat,
                     double lng, String registration, List<VenueMaps> venueMaps){
            this.id = id;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.location = location;
            this.lat = lat;
            this.lng = lng;
            this.registration = registration;
            this.venueMaps = venueMaps;
        }

        public static Event fromJSON(JSONObject jEvent) throws JSONException{
            String latlng = jEvent.getString("LatLng__c");
            List<Events.Event.VenueMaps> venueMaps = new ArrayList<>();
            for(int j = 0; j < jEvent.getJSONArray("Venue_Maps").length(); j++){
                JSONObject map = jEvent.getJSONArray("Venue_Maps").getJSONObject(j);
                venueMaps.add(new Events.Event.VenueMaps(map.getString("name"), map.getString("url")));
            }
            Events.Event mEvent;
            if(!latlng.equals("null")){
                return new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getString("Event_Start_Date__c"),
                        jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), jEvent.getJSONObject("LatLng__c").getDouble("latitude"),
                        jEvent.getJSONObject("LatLng__c").getDouble("longitude"), "", venueMaps);
            } else {
                return new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getString("Event_Start_Date__c"),
                        jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), 0, 0, "", venueMaps);
            }
        }

        @Override
        public String toString() { return name + " | " + location; }

        public int compareTo(Event another) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date thisStart = null;
            Date anotherStart = null;
            try {
                thisStart = formatter.parse(this.startDate);
                anotherStart = formatter.parse(another.startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return thisStart.compareTo(anotherStart);
        }

        public static class VenueMaps {
            public String name;
            public String url;

            public VenueMaps(String name, String url){
                this.name = name;
                this.url = url;
            }
        }
    }
}
