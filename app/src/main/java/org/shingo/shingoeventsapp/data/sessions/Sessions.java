package org.shingo.shingoeventsapp.data.sessions;

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

    public static void addSession(Session session){
        if(SESSION_MAP.get(session.id) == null){
            SESSIONS.add(session);
            SESSION_MAP.put(session.id, session);
        }
    }

    public static void clear(){
        SESSIONS.clear();
        SESSION_MAP.clear();
    }

    public static class Session implements Comparable<Session>{
        public String id;
        public String name;
        public String sAbstract;
        public String notes;
        public String date;
        public String time;
        public String status;
        public List<sSpeaker> speakers;

        public Session(String id, String name, String sAbstract, String notes, String date, String time, String status, List<sSpeaker> speakers){
            this.id = id;
            this.name = name;
            this.sAbstract = sAbstract;
            this.notes = notes;
            this.date = date;
            this.time = time;
            this.status = status;
            this.speakers = speakers;
        }

        @Override
        public String toString() { return name; }

        @Override
        public int compareTo(Session another) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date thisStart = null;
            Date anotherStart = null;
            try {
                thisStart = formatter.parse(this.date);
                anotherStart = formatter.parse(another.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return thisStart.compareTo(anotherStart);
        }

        public static class sSpeaker implements Comparable<sSpeaker>{
            public String id;
            public String name;

            public sSpeaker(String id, String name){
                this.id = id;
                this.name = name;
            }

            @Override
            public int compareTo(sSpeaker another) {
                return this.name.compareTo(another.name);
            }
        }
    }
}
