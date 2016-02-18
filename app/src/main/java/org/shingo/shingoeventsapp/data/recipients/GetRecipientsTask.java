package org.shingo.shingoeventsapp.data.recipients;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.exhibitors.Exhibitors;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by dustinehoman on 2/10/16.
 */
public class GetRecipientsTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private String output;

    public GetRecipientsTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetRecipientsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetRecipientsTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "="
                    + URLEncoder.encode(mEvent, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/recipients?client_id="
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
            System.out.println("Exhibitors: " + output);
            success = response.getBoolean("success");
            if (success) {
                Recipients.clear();
                JSONArray jAwardRecipients = response.getJSONObject("award_recipients").getJSONArray("records");
                for(int i = 0; i < jAwardRecipients.length(); i++){
                    JSONObject jRecipient = jAwardRecipients.getJSONObject(i);
                    URL image = new URL(jRecipient.getString("Logo_Book_Cover__c"));
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    Recipients.addAwardRecipient(new Recipients.AwardRecipient(jRecipient.getString("Id"), jRecipient.getString("Name"),
                            jRecipient.getString("Abstract__c"),jRecipient.getString("Award__c"),picture));
                }
                JSONArray jResearchRecipients = response.getJSONObject("research_recipients").getJSONArray("records");
                for(int i = 0; i < jResearchRecipients.length(); i++){
                    JSONObject jRecipient = jResearchRecipients.getJSONObject(i);
                    URL image = new URL(jRecipient.getString("Logo_Book_Cover__c"));
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    Recipients.addResearchRecipient(new Recipients.ResearchRecipient(jRecipient.getString("Id"), jRecipient.getString("Name"),
                            jRecipient.getString("Abstract__c"), jRecipient.getString("Author_s__c"), picture));
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