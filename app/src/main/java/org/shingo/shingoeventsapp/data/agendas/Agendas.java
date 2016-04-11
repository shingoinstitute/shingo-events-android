package org.shingo.shingoeventsapp.data.agendas;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    public static void fromJSON(String json) throws JSONException{
        JSONObject response = new JSONObject(json);
        JSONObject jAgenda = response.getJSONObject("agenda");
        if(jAgenda != null){
            JSONArray jDays = jAgenda.getJSONObject("Days").getJSONArray("records");
            for(int i = 0; i < jDays.length(); i++){
                Agendas.addAgenda(Day.fromJSON(jDays.getJSONObject(i)));
            }
        }
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
            if(this.name.equals(another.name)) return 0;
            if(this.name.equals("Monday")) return -1;
            if(this.name.equals("Friday")) return 1;
            if(another.name.equals("Monday")) return 1;
            if(another.name.equals("Friday")) return -1;
            if(this.name.equals("Tuesday")) return -1;
            if(another.name.equals("Tuesday")) return 1;
            if(this.name.equals("Wednesday")) return -1;
            if(another.name.equals("Wednesday")) return 1;
            if(this.name.equals("Thursday")) return  -1;
            return 1;
        }

        public static Day fromJSON(JSONObject jDay) throws JSONException{
           return new Agendas.Day(jDay.getString("Id"), jDay.getString("Name"), new ArrayList<Sessions.Session>());
        }

        @Override
        public String toString(){
            return this.name;
        }

    }
}
