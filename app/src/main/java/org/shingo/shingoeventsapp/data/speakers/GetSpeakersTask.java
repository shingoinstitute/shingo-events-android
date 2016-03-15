package org.shingo.shingoeventsapp.data.speakers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.events.Events;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;

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
public class GetSpeakersTask extends AsyncTask<Void, Void, Boolean> {

    private final String mId;
    private OnTaskComplete mListener;
    private static boolean isWorking;
    private static Object mutex = new Object();

    public GetSpeakersTask(String id, OnTaskComplete listener) {
        mId = id;
        mListener = listener;
        System.out.println("GetSpeakersTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetSpeakersTask.doInBackground called");
        synchronized (mutex) {
            if (isWorking) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWorking = true;
            }
        }
        boolean success;
        if(!Speakers.needsRefresh()) return true;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder.encode(mId, "UTF-8");
            URL url = new URL(RestApi.API_URL + "/sfevents/speakers?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
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
            System.out.println("Speakers response: " + output);
            success = response.getBoolean("success");
            if (success) {
                Speakers.clear();
                JSONArray jSpeakers = response.getJSONObject("speakers").getJSONArray("records");
                for(int i = 0; i < jSpeakers.length(); i++){
                    JSONObject jSpeaker = jSpeakers.getJSONObject(i);
                    URL image = new URL(jSpeaker.getString("Speaker_Image__c"));
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    Speakers.addSpeaker(new Speakers.Speaker(jSpeaker.getString("Id"),
                            jSpeaker.getString("Name"),jSpeaker.getString("Speaker_Display_Name__c"),
                            jSpeaker.getString("Title"), jSpeaker.getString("Organization"),
                            jSpeaker.getString("Biography__c"), picture));
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
            System.out.println("GetSpeakersTask completed");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred in GetSpeakersTask...");
        }
    }

    @Override
    protected void onCancelled() {

    }
}