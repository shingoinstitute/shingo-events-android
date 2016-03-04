package org.shingo.shingoeventsapp.data.agendas;

import android.support.annotation.NonNull;

import org.shingo.shingoeventsapp.data.sessions.Sessions;

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
 *
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
        public List<Sessions.Session> sessions;

        public Day (String id, String name, List<Sessions.Session> sessions){
            this.id = id;
            this.name = name;
            this.sessions = sessions;
        }

        @Override
        public int compareTo(@NonNull Day another) {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.getDefault());
            Date thisStart;
            Date anotherStart;
            try {
                thisStart = formatter.parse(this.name);
                anotherStart = formatter.parse(another.name);

                if(thisStart.after(anotherStart)) return 1;
                if(thisStart.before(anotherStart)) return -1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public String toString(){
            return this.name;
        }

    }
}
