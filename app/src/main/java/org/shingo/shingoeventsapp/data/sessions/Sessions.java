package org.shingo.shingoeventsapp.data.sessions;

import org.shingo.shingoeventsapp.data.speakers.Speakers;

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

        }

        public Session(String id, String name, String format, String startTime, String endTime){
            this.id = id;
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
            this.format = format;
            this.formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
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
