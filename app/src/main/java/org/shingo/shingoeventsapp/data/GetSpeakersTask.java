package org.shingo.shingoeventsapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.events.Events;
import org.shingo.shingoeventsapp.data.speakers.Speakers;
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
public class GetSpeakersTask extends AsyncTask<Void, Void, Boolean> {

    private final String mId;
    private OnTaskComplete mListener;
    private String output;

    public GetSpeakersTask(String id, OnTaskComplete listener) {
        mId = id;
        mListener = listener;
        System.out.println("GetSpeakersTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetSpeakersTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder.encode(mId, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/speakers?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
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
            System.out.println("Speakers response: " + output);
            success = response.getBoolean("success");
            if (success) {
                Speakers.clear();
                JSONArray jSpeakers = response.getJSONObject("speakers").getJSONArray("records");
                for(int i = 0; i < jSpeakers.length(); i++){
                    JSONObject jSpeaker = jSpeakers.getJSONObject(i);
                    JSONObject jContact = jSpeaker.getJSONObject("Speaker_Contact__r");
                    JSONObject jAccount = jContact.getJSONObject("Account");
                    URL image = new URL(jSpeaker.getString("Speaker_Image__c"));
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    Speakers.addSpeaker(new Speakers.Speaker(jSpeaker.getString("Id"),
                            jContact.getString("Name"),jSpeaker.getString("Speaker_Display_Name__c"),
                            jContact.getString("Title"), jAccount.getString("Name"),
                            jSpeaker.getString("Biography__c"), picture));
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