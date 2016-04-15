package org.shingo.shingoeventsapp.data.events;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Dustin Homan
 *
 * This class is used to hold the
 * data pertinent to the {@link Events}.
 * The lists are static to reduce calls to the API.
 */
public class Events {

    public static List<Event> EVENTS = new ArrayList<>();

    public static Map<String, Event> EVENT_MAP = new HashMap<>();

    /**
     * Used to add an {@link org.shingo.shingoeventsapp.data.events.Events.Event}
     * to {@link Events#EVENTS} and {@link Events#EVENT_MAP}
     * @param event the {@link org.shingo.shingoeventsapp.data.events.Events.Event} to add
     */
    public static void addEvent(Event event){
        if(EVENT_MAP.get(event.id) == null) {
            EVENTS.add(event);
            EVENT_MAP.put(event.id, event);
        }
    }

    /**
     * Clear {@link Events#EVENTS} and {@link Events#EVENT_MAP}
     */
    public static void clear(){
        EVENTS.clear();
        EVENT_MAP.clear();
    }

    /**
     * Used to parse {@link Events} from a JSON string
     * @param json JSON string representing a list of {@link org.shingo.shingoeventsapp.data.events.Events.Event}s
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException {
        JSONObject response = new JSONObject(json);
        JSONArray jSfevents = response.getJSONObject("events").getJSONArray("records");
        for (int i = 0; i < response.getJSONObject("events").getInt("size"); i++) {
            Events.addEvent(Event.fromJSON(jSfevents.getJSONObject(i)));
        }
    }

    /**
     * Class to hold data for a particular Event
     */
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

        /**
         *
         * @param id Event SalesForce ID
         * @param name Event name
         * @param startDate The start date of an event (yyyy-MM-dd)
         * @param endDate The end date of an event (yyyy-MM-dd)
         * @param location The city and state/country where the Event is occurring
         * @param lat Latitude of the venue
         * @param lng Longitude of the venue
         * @param registration Registration URL
         * @param venueMaps A list of {@link org.shingo.shingoeventsapp.data.events.Events.Event.VenueMaps}
         */
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

        /**
         * A method to parse an {@link org.shingo.shingoeventsapp.data.events.Events.Event} from a {@link JSONObject}
         * @param jEvent a {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.events.Events.Event}
         * @return a new {@link org.shingo.shingoeventsapp.data.events.Events.Event}
         * @throws JSONException
         */
        public static Event fromJSON(JSONObject jEvent) throws JSONException{
            String latlng = jEvent.getString("LatLng__c");
            List<Events.Event.VenueMaps> venueMaps = new ArrayList<>();
            for(int j = 0; j < jEvent.getJSONArray("Venue_Maps").length(); j++){
                JSONObject map = jEvent.getJSONArray("Venue_Maps").getJSONObject(j);
                venueMaps.add(new Events.Event.VenueMaps(map.getString("name"), map.getString("url")));
            }
            if(!latlng.equals("null")){
                return new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getString("Event_Start_Date__c"),
                        jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), jEvent.getJSONObject("LatLng__c").getDouble("latitude"),
                        jEvent.getJSONObject("LatLng__c").getDouble("longitude"), "", venueMaps);
            } else {
                return new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getString("Event_Start_Date__c"),
                        jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), 0, 0, "", venueMaps);
            }
        }

        /**
         * Overload of {@link Object#toString()}
         * @return "{@link org.shingo.shingoeventsapp.data.events.Events.Event#name} | {@link org.shingo.shingoeventsapp.data.events.Events.Event#location}"
         */
        @Override
        public String toString() { return name + " | " + location; }

        /**
         * Method to compare to another {@link org.shingo.shingoeventsapp.data.events.Events.Event}
         * @param another {@link org.shingo.shingoeventsapp.data.events.Events.Event}
         * @return The Events compared by start date
         */
        public int compareTo(@NonNull Event another) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date thisStart = null;
            Date anotherStart = null;
            try {
                thisStart = formatter.parse(this.startDate);
                anotherStart = formatter.parse(another.startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert thisStart != null;
            assert anotherStart != null;
            return thisStart.compareTo(anotherStart);
        }

        /**
         * Class to hold information about an {@link org.shingo.shingoeventsapp.data.events.Events.Event}'s Venue Maps
         */
        public static class VenueMaps {
            public String name;
            public String url;

            /**
             *
             * @param name The name displayed to the user
             * @param url URL to download image
             */
            public VenueMaps(String name, String url){
                this.name = name;
                this.url = url;
            }
        }
    }
}
