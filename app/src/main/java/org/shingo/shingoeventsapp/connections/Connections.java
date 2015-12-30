package org.shingo.shingoeventsapp.connections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 12/30/15.
 */
public class Connections {
    public static List<Connection> CONNECTIONS = new ArrayList<>();

    public static Map<String, Connection> CONNECTION_MAP = new HashMap<>();

    public static void addConnection(Connection connection){
        if(CONNECTION_MAP.get(connection.email) == null){
            CONNECTIONS.add(connection);
            CONNECTION_MAP.put(connection.email, connection);
        }
    }

    public static class Connection {
        public String email;
        public String firstName;
        public String lastName;
        public String displayName;
        public String title;
        public String company;
        public String status;

        public Connection(String email, String firstName,
                        String lastName, String displayName,
                          String title, String company, String status){
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
            this.title = title;
            this.company = company;
            this.status = status;
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
