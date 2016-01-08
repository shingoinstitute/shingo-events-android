package org.shingo.shingoeventsapp.data.attendees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 12/30/15.
 */
public class Attendees {
    public static List<Attendee> ATTENDEES = new ArrayList<>();

    public static Map<String, Attendee> ATTENDEE_MAP = new HashMap<>();

    public static void addAttendee(Attendee attendee){
        if(ATTENDEE_MAP.get(attendee.email) == null){
            ATTENDEES.add(attendee);
            ATTENDEE_MAP.put(attendee.email, attendee);
        }
    }

    public static void clear(){
        ATTENDEE_MAP.clear();
        ATTENDEES.clear();
    }

    public static class Attendee implements Comparable<Attendee>{
        public int id;
        public String email;
        public String firstName;
        public String lastName;
        public String displayName;
        public String title;
        public String company;
        public String status;

        public Attendee(int id, String email, String firstName,
                        String lastName, String displayName,
                        String title, String company, String status){
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
            this.title = title;
            this.company = company;
            this.status = status;
            if(displayName.equals("null")){
                this.displayName = firstName + " " + lastName;
            }
            if(title.equals("null")){
                this.title = "";
            }
            if(company.equals("null")){
                this.company = "";
            }
        }

        @Override
        public String toString(){
            if(displayName.isEmpty()){
                return firstName + " " + lastName;
            } else {
                return displayName;
            }
        }

        @Override
        public int compareTo(Attendee another) {
            return this.lastName.compareTo(another.lastName);
        }
    }
}
