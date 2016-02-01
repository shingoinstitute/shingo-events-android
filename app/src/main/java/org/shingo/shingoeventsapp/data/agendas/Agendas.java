package org.shingo.shingoeventsapp.data.agendas;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 1/14/16.
 */
public class Agendas {

    public static List<Day> AGENDAS = new ArrayList<>();

    public static Map<String, Day> AGENDA_MAP = new HashMap<>();

    public static void addAgenda(Day day){
        if(AGENDA_MAP.get(day.id) == null){
            AGENDAS.add(day);
            AGENDA_MAP.put(day.id, day);
        }
    }

    public static void clear(){
        AGENDAS.clear();
        AGENDA_MAP.clear();
    }

    public static class Day implements Comparable<Day>{
        public String id;
        public String name;
        public static List<Session> sessions;

        public Day (String id, String name, List<Session> sessions){
            this.id = id;
            this.name = name;
            this.sessions = sessions;
        }

        @Override
        public int compareTo(Day another) {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            Date thisStart = null;
            Date anotherStart = null;
            try {
                thisStart = formatter.parse(this.name);
                anotherStart = formatter.parse(another.name);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return thisStart.compareTo(anotherStart);
        }

        @Override
        public String toString(){
            return this.name;
        }

        public static class Session implements Comparable<Session>{

            public String id;
            public String name;
            public String startTime;
            public String endTime;
            private SimpleDateFormat formatter;

            public Session(String id, String name, String startTime, String endTime){
                this.id = id;
                this.name = name;
                this.startTime = startTime;
                this.endTime = endTime;
                this.formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            }

            @Override
            public int compareTo(Session another) {
                Date thisStart = null;
                Date anotherStart = null;
                try {
                    thisStart = formatter.parse(this.startTime);
                    anotherStart = formatter.parse(another.startTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return thisStart.compareTo(anotherStart);
            }

            @Override
            public String toString(){
                try {
                    DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
                    String start = format.format(formatter.parse(this.startTime).getTime());
                    String end = format.format(formatter.parse(this.endTime).getTime());
                    String shortName = (25 < name.length()) ? name.substring(0, 15) : name;
                    return start + "-" +end + "\n  " + shortName;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return this.name;
                }
            }
        }
    }
}
