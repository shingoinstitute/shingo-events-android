package org.shingo.shingoeventsapp.data.agendas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.data.speakers.Speakers;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dustinehoman on 1/19/16.
 */
public class GetDayTask extends AsyncTask<Void, Void, Boolean> {

    private String mDay;
    private OnTaskComplete mListener;
    private static boolean isWorking;
    private static Object mutex = new Object();

    public GetDayTask(String day, OnTaskComplete listener) {
        mDay = day;
        mListener = listener;
        System.out.println("GetDayTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetDayTask.doInBackground called");
        synchronized (mutex) {
            if (isWorking) {
                try {
                    mutex.wait();
                    return true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWorking = true;
            }
        }
        boolean success;
        try {
            String data = URLEncoder.encode("day_id", "UTF-8") + "="
                    + URLEncoder.encode(mDay, "UTF-8");
            URL url = new URL(RestApi.API_URL + "/sfevents/day?client_id="
                    + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("Day: " + output);
            success = response.getBoolean("success");
            if (success) {
                JSONObject jDay = response.getJSONObject("day");
                if(jDay != null){
                    JSONArray jSessions = jDay.getJSONObject("Sessions").getJSONArray("records");
                    for(int i = 0; i < jSessions.length(); i++){
                        JSONObject jSession = jSessions.getJSONObject(i);
                        List<Speakers.Speaker> speakers = new ArrayList<>();
                        JSONArray jSpeakers = jSession.getJSONObject("Speakers").getJSONArray("records");
                        for (int j = 0; j < jSpeakers.length(); j++) {
                            JSONObject jSpeaker = jSpeakers.getJSONObject(j);
                            speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                                    jSpeaker.getString("Name"), jSpeaker.getString("Name"), jSpeaker.getString("Title"),
                                    jSpeaker.getString("Organization"), "", null));
                        }
                        if(!jSession.has("Room")) jSession.put("Room", "null");
                        Agendas.AGENDA_MAP.get(jDay.getString("Id")).sessions.add((new Sessions.Session(jSession.getString("Id"),
                                jSession.getString("Name"),jSession.getString("Session_Abstract__c"),
                                jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"), jSession.getString("Session_Format__c"),
                                jSession.getString("Session_Time__c"), speakers, jSession.getString("Room"))));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        synchronized (mutex) {
            isWorking = false;
            mutex.notifyAll();
        }
        if (success) {
            System.out.println("GetDayTask completed");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred in GetDayTask...");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
