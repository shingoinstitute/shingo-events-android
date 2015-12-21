package org.shingo.shingoeventsapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dustinehoman on 12/21/15.
 */
public class Attendee {
    private String email;
    private String firstName;
    private String lastName;
    private String _id;
    private String displayName;
    private boolean visible;
    private List<String> registrationIds;
    private List<String> connections;

    public Attendee(String _id, String email){
        this.email = email;
        this._id = _id;
        this.registrationIds = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    public String getEmail(){
        return this.email;
    }

    public String get_id(){
        return this._id;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    public List<String> getRegistrationIds(){
        return this.registrationIds;
    }

    public void addRegistration(String regId){
        this.registrationIds.add(regId);
    }

    public List<String> getConnections(){
        return this.connections;
    }

    public void deleteRegistration(String regId){
        this.registrationIds.remove(regId);
    }

    public void addConnection(String connection){
        this.connections.add(connection);
    }

    public void deleteConnection(String connection){
        this.connections.remove(connection);
    }

    public boolean isVisible(){
        return this.visible;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }
}
