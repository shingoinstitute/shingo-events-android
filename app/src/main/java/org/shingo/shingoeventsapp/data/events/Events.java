package org.shingo.shingoeventsapp.data.events;

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

    public static class Event implements Comparable<Event>{
        public String id;
        public String name;
        public String url;
        public String startDate;
        public String endDate;
        public String location;
        public double lat;
        public double lng;
        public String registration;

        public Event(String id, String name, String url, String startDate,
                     String endDate, String location, double lat, double lng, String registration){
            this.id = id;
            this.name = name;
            this.url = url;
            this.startDate = startDate;
            this.endDate = endDate;
            this.location = location;
            this.lat = lat;
            this.lng = lng;
            this.registration = registration;
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
    }
}
