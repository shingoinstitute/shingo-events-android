package org.shingo.shingoeventsapp.data.events;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;
import org.shingo.shingoeventsapp.api.OnTaskComplete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class GetEventsTask extends AsyncTask<Void, Void, Boolean> {

    private OnTaskComplete mListener;
    private String output;

    public GetEventsTask(OnTaskComplete listener) {
        mListener = listener;
        System.out.println("GetEventsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetEventsTask.doInBackground called");
        boolean success = false;
        try {
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("SFEvents " + output);
            success = response.getBoolean("success");
            Events.clear();
            if (success) {
                JSONArray jSfevents = response.getJSONObject("events").getJSONArray("records");
                for (int i = 0; i < response.getJSONObject("events").getInt("size"); i++) {
                    JSONObject jEvent = jSfevents.getJSONObject(i);
                    String latlng = jEvent.getString("LatLng__c");
                    List<Events.Event.VenueMaps> venueMaps = new ArrayList<>();
                    for(int j = 0; j < jEvent.getJSONArray("Venue_Maps").length(); j++){
                        JSONObject map = jEvent.getJSONArray("Venue_Maps").getJSONObject(j);
                        venueMaps.add(new Events.Event.VenueMaps(map.getString("name"), map.getString("url")));
                    }
                    Events.Event mEvent;
                    if(!latlng.equals("null")){
                     mEvent = new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getString("Event_Start_Date__c"),
                            jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), jEvent.getJSONObject("LatLng__c").getDouble("latitude"),
                             jEvent.getJSONObject("LatLng__c").getDouble("longitude"), "", venueMaps);
                    } else {
                        mEvent = new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getString("Event_Start_Date__c"),
                                jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), 0, 0, "", venueMaps);
                    }
                    Events.addEvent(mEvent);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            System.out.println("Setting list adapter");
            Collections.sort(Events.EVENTS);
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