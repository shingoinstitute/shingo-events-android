package org.shingo.shingoeventsapp.data.agendas;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.data.sessions.Sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dustin Homan
 *
 * This class is used to hold the
 * data pertinate to the {@link org.shingo.shingoeventsapp.data.events.Events.Event}'s
 * agenda recieved from the API. The lists
 * are static to reduce calls to the API.
 */
public class Agendas {

    public static List<Day> AGENDAS = new ArrayList<>();

    public static Map<String, Day> AGENDA_MAP = new HashMap<>();

    /**
     * Adds an {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day} to both {@link Agendas#AGENDAS}
     * and {@link Agendas#AGENDA_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day}.
     *
     * @param day a {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day}
     */
    public static void addAgenda(Day day){
        if(AGENDA_MAP.get(day.id) == null){
            AGENDAS.add(day);
            AGENDA_MAP.put(day.id, day);
        }
    }

    /**
     * Used to clear {@link Agendas#AGENDAS} and
     * {@link Agendas#AGENDA_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        AGENDAS.clear();
        AGENDA_MAP.clear();
    }

    /**
     * Used to parse {@link Agendas} from a JSON string
     * @param json JSON string representing an Agenda
     * @throws JSONException
     */
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

    /**
     * Class to hold data for a Day in the Agenda
     */
    public static class Day implements Comparable<Day>{
        public String id;
        public String name;
        public List<Sessions.Session> sessions;

        /**
         *
         * @param id Day's SalesForce ID
         * @param name Day of the week
         * @param sessions {@link List} of {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
         */
        public Day (String id, String name, List<Sessions.Session> sessions){
            this.id = id;
            this.name = name;
            this.sessions = sessions;
        }

        /**
         * Used to compare one {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day} to another
         * @param another {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day}
         * @return the day sorted by its position in the week
         */
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

        /**
         * Parse a {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day} from a {@link JSONObject}
         * @param jDay a {@link JSONObject} representing a {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day}
         * @return a new {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day}
         * @throws JSONException
         */
        public static Day fromJSON(JSONObject jDay) throws JSONException{
           return new Agendas.Day(jDay.getString("Id"), jDay.getString("Name"), new ArrayList<Sessions.Session>());
        }

        /**
         * Override the default {@link Object#toString()}
         * @return {@link org.shingo.shingoeventsapp.data.agendas.Agendas.Day#name}
         */
        @Override
        public String toString(){
            return this.name;
        }
    }
}
