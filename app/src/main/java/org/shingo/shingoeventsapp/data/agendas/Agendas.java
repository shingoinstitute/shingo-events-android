package org.shingo.shingoeventsapp.data.agendas;

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

    public static List<Agenda> AGENDAS = new ArrayList<>();

    public static Map<String, Agenda> AGENDA_MAP = new HashMap<>();

    public static void addAgenda(Agenda agenda){
        if(AGENDA_MAP.get(agenda.id) == null){
            AGENDAS.add(agenda);
            AGENDA_MAP.put(agenda.id, agenda);
        }
    }

    public static void clear(){
        AGENDAS.clear();
        AGENDA_MAP.clear();
    }

    public static class Agenda implements Comparable<Agenda> {
        public String id;
        public String day;
        public String date;
        public List<String> sessions;

        public Agenda(String id, String day, String date, List<String> sessions){
            this.id = id;
            this.day = day;
            this.date = date;
            this.sessions = sessions;
        }

        @Override
        public int compareTo(Agenda another) {
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
    }
}
