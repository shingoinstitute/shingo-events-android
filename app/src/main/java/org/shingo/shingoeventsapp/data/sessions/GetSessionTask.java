package org.shingo.shingoeventsapp.data.sessions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
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
public class GetSessionTask extends AsyncTask<Void, Void, Boolean> {

    private final String mId;
    private OnTaskComplete mListener;
    private String output;

    public GetSessionTask(String id, OnTaskComplete listener) {
        mId = id;
        mListener = listener;
        System.out.println("GetSessionTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetSessionTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(mId, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/session?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
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
            System.out.println("Session response: " + output);
            success = response.getBoolean("success");
            if (success) {
                JSONObject jSession = response.getJSONObject("session");
                List<Speakers.Speaker> speakers = new ArrayList<>();
                JSONArray jSpeakers = jSession.getJSONObject("Speakers").getJSONArray("records");
                for(int j = 0; j < jSpeakers.length(); j++){
                    JSONObject jSpeaker = jSpeakers.getJSONObject(j);
                    URL image = new URL(jSpeaker.getString("Speaker_Image__c"));
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                            jSpeaker.getString("Name"),jSpeaker.getString("Name"),jSpeaker.getString("Title"),
                            jSpeaker.getString("Organization"),"",picture));
                }
                Sessions.addSession(new Sessions.Session(jSession.getString("Id"),
                        jSession.getString("Name"),jSession.getString("Session_Abstract__c"),
                        jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"),
                        jSession.getString("Session_Time__c"), speakers, jSession.getString("Room")));
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
            System.out.println("Calling onTaskComplete");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred...");
        }
    }

    @Override
    protected void onCancelled() {

    }
}