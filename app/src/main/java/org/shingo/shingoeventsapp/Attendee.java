package org.shingo.shingoeventsapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dustinehoman on 12/21/15.
 */
public class Attendee implements Parcelable{
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

    protected Attendee(Parcel in) {
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        _id = in.readString();
        displayName = in.readString();
        visible = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            registrationIds = new ArrayList<String>();
            in.readList(registrationIds, String.class.getClassLoader());
        } else {
            registrationIds = null;
        }
        if (in.readByte() == 0x01) {
            connections = new ArrayList<String>();
            in.readList(connections, String.class.getClassLoader());
        } else {
            connections = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(_id);
        dest.writeString(displayName);
        dest.writeByte((byte) (visible ? 0x01 : 0x00));
        if (registrationIds == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(registrationIds);
        }
        if (connections == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(connections);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Attendee> CREATOR = new Parcelable.Creator<Attendee>() {
        @Override
        public Attendee createFromParcel(Parcel in) {
            return new Attendee(in);
        }

        @Override
        public Attendee[] newArray(int size) {
            return new Attendee[size];
        }
    };
}
