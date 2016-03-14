package org.shingo.shingoeventsapp.data.agendas;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.sessions.Sessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by dustinehoman on 1/14/16.
 */
public class GetAgendasTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private static boolean isWorking;

    public GetAgendasTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetAgendasTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetAgendasTask.doInBackground called");
        synchronized (this) {
            if (isWorking) {
                try {
                    wait();
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
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String output = sb.toString();
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
