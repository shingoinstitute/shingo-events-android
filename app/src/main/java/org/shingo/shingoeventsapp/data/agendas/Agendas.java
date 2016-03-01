package org.shingo.shingoeventsapp.data.agendas;

import org.shingo.shingoeventsapp.data.sessions.Sessions;

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

    public static Date refresh;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }


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

    public static void setRefresh(){
        refresh = new Date();
    }

    public static class Day implements Comparable<Day>{
        public String id;
        public String name;
        public static List<Sessions.Session> sessions;

        public Day (String id, String name, List<Sessions.Session> sessions){
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
            if(thisStart.getDay() > anotherStart.getDay()) return 1;
            if(thisStart.getDay() < anotherStart.getDay()) return -1;
            return 0;
        }

        @Override
        public String toString(){
            return this.name;
        }

    }
}
