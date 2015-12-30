package org.shingo.shingoeventsapp.attendees;

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

    public static class Attendee {
        public String email;
        public String firstName;
        public String lastName;
        public String displayName;

        public Attendee(String email, String firstName,
                        String lastName, String displayName){
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
        }

        @Override
        public String toString(){
            if(displayName.isEmpty()){
                return firstName + " " + lastName;
            } else {
                return displayName;
            }
        }
    }
}
