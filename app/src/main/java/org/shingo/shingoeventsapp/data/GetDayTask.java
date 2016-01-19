package org.shingo.shingoeventsapp.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.agendas.Agendas;
import org.shingo.shingoeventsapp.ui.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 1/19/16.
 */
public class GetDayTask extends AsyncTask<Void, Void, Boolean> {

    private String mDay;
    private OnTaskComplete mListener;
    private String output;

    public GetDayTask(String day, OnTaskComplete listener) {
        mDay = day;
        mListener = listener;
        System.out.println("GetDayTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetDayTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("day_id", "UTF-8") + "="
                    + URLEncoder.encode(mDay, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/day?client_id="
                    + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
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
            System.out.println("Day: " + output);
            success = response.getBoolean("success");
            if (success) {
                JSONObject jDay = response.getJSONObject("day").getJSONArray("records").getJSONObject(0);
                if(jDay != null){
                    JSONArray jSessions = jDay.getJSONObject("Sessions__r").getJSONArray("records");
                    List<Agendas.Day.Session> sessions = new ArrayList<>();
                    for(int i = 0; i < jSessions.length(); i++){
                        JSONObject jSession = jSessions.getJSONObject(i);
                        String date = jSession.getString("Session_Date__c");
                        String start = jSession.getString("Session_Time__c").split("-")[0];
                        String end = jSession.getString("Session_Time__c").split("-")[1];
                        sessions.add(new Agendas.Day.Session(jSession.getString("Id"), jSession.getString("Name"), date + " " + start, date + " " + end));
                    }
                    Collections.sort(sessions);
                    Agendas.AGENDA_MAP.get(jDay.getString("Id")).sessions = sessions;
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
