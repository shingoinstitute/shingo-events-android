package org.shingo.shingoeventsapp.data.agendas;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
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
import java.util.Collections;

/**
 * Created by dustinehoman on 1/14/16.
 */
public class GetAgendasTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private String output;

    public GetAgendasTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetEventsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetEventsTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "="
                    + URLEncoder.encode(mEvent, "UTF-8");
            URL url = new URL(RestApi.API_URL + "/sfevents/agenda?client_id="
                    + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
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
            System.out.println("Agendas: " + output);
            success = response.getBoolean("success");
            if (success) {
                Agendas.clear();
                JSONObject jAgenda = response.getJSONObject("agenda");
                if(jAgenda != null){
                    JSONArray jDays = jAgenda.getJSONObject("Days").getJSONArray("records");
                    for(int i = 0; i < jDays.length(); i++){
                        JSONObject jDay = jDays.getJSONObject(i);
                        Agendas.addAgenda(new Agendas.Day(jDay.getString("Id"), jDay.getString("Name"), new ArrayList<Sessions.Session>()));
                    }
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
            Collections.sort(Agendas.AGENDAS);
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
