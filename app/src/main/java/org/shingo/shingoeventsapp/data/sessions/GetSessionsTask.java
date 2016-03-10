package org.shingo.shingoeventsapp.data.sessions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
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
import java.util.Collections;
import java.util.List;

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
        System.out.println("GetSessionsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetEventsTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder.encode(mId, "UTF-8");
            URL url = new URL(RestApi.API_URL + "/sfevents/sessions?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
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
                for(int i = 0; i < response.getJSONObject("sessions").getInt("size"); i++){
                    JSONObject jSession = jSessions.getJSONObject(i);
                    List<Speakers.Speaker> speakers = new ArrayList<>();
                    JSONArray jSpeakers = jSession.getJSONObject("Speakers").getJSONArray("records");
                    for(int j = 0; j < jSpeakers.length(); j++){
                        JSONObject jSpeaker = jSpeakers.getJSONObject(j);
                        URL image = null;
                        if(!jSpeaker.getString("Speaker_Image__c").equals("null")) {
                            image = new URL(jSpeaker.getString("Speaker_Image__c"));
                            Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                            speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                                    jSpeaker.getString("Name"), jSpeaker.getString("Name"), jSpeaker.getString("Title"),
                                    jSpeaker.getString("Organization"), "", picture));
                        } else {
                            speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                                    jSpeaker.getString("Name"), jSpeaker.getString("Name"), jSpeaker.getString("Title"),
                                    jSpeaker.getString("Organization"), "", null));
                        }
                    }
                    Sessions.addSession(new Sessions.Session(jSession.getString("Id"),
                            jSession.getString("Name"),jSession.getString("Session_Abstract__c"),
                            jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"),
                            jSession.getString("Session_Time__c"), speakers, jSession.getString("Room")));
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
            Collections.sort(Sessions.SESSIONS);
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred...");
        }
    }

    @Override
    protected void onCancelled() {
        
    }
}
