package org.shingo.shingoeventsapp.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.events.Events;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.ui.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class GetSessionsTask extends AsyncTask<Void, Void, Boolean> {

    private final String mId;
    private OnTaskComplete mListener;
    private String output;

    public GetSessionsTask(String id, OnTaskComplete listener) {
        mId = id;
        mListener = listener;
        System.out.println("GetEventsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetEventsTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder.encode(mId, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/sessions?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("Sessions response: " + output);
            success = response.getBoolean("success");
            if (success) {
                Sessions.clear();
                JSONArray jSessions = response.getJSONObject("sessions").getJSONArray("records");
                for(int i = 0; i < jSessions.length(); i++){
                    JSONObject jSession = jSessions.getJSONObject(i);
                    Sessions.addSession(new Sessions.Session(jSession.getString("Id"),
                            jSession.getString("Name"),jSession.getString("Session_Abstract__c"), jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"), jSession.getString("Session_Time__c"), jSession.getString("Session_Status__c")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            return success;
        } catch (IOException e) {
            return success;
        } catch (JSONException e) {
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

    }
}
