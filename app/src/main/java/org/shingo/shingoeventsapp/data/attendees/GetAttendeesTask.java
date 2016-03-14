package org.shingo.shingoeventsapp.data.attendees;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.connections.Connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class GetAttendeesTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final OnTaskComplete mListener;
    private static boolean isWorking;

    public GetAttendeesTask(String email, OnTaskComplete listener) {
        mEmail = email;
        mListener = listener;
        System.out.println("GetAttendeesTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetAttendees.doInBackground called");
        synchronized (this){
            if(isWorking){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWorking = true;
            }
        }
        boolean success;
        try {
            URL url = new URL(RestApi.API_URL + "/attendees?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
            System.out.println("Opening connection: " + url.getPath());
            URLConnection conn = url.openConnection();
            System.out.println("Got connection: " + conn.getURL());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            String output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("getAttendees() response: " + output);
            success = response.getBoolean("success");
            if(success){
                System.out.println("Attendees fetched successfully");
                Attendees.clear();
                JSONArray jAttendees = response.getJSONArray("attendees");
                for(int i = 0; i < jAttendees.length(); i++){
                    JSONObject jAttendee = jAttendees.getJSONObject(i);
                    if(!jAttendee.getString("email").equals(mEmail)) {
                        Connections.Connection c = Connections.CONNECTION_MAP.get(jAttendee.getString("email"));
                        if (c != null) {
                            Attendees.addAttendee(new Attendees.Attendee(jAttendee.getInt("ID"), jAttendee.getString("email"),
                                    jAttendee.getString("first_name"), jAttendee.getString("last_name"), jAttendee.getString("display_name"),
                                    jAttendee.getString("title"), jAttendee.getString("company"),c.status));
                        }
                        Attendees.addAttendee(new Attendees.Attendee(jAttendee.getInt("ID"), jAttendee.getString("email"),
                                jAttendee.getString("first_name"), jAttendee.getString("last_name"), jAttendee.getString("display_name"),
                                jAttendee.getString("title"), jAttendee.getString("company"),""));
                    }
                }
            }
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return false;
        } catch(IOException e ){
            e.printStackTrace();
            return false;
        } catch(JSONException e){
            e.printStackTrace();
            return false;
        }

        return success ;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        synchronized (this){
            isWorking = false;
            notifyAll();
        }
        if (success) {
            System.out.println("Setting list adapter");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred...");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
