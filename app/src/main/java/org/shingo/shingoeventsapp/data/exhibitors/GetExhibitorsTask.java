package org.shingo.shingoeventsapp.data.exhibitors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
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
public class GetExhibitorsTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private String output;

    public GetExhibitorsTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetExhibitorsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetExhibitorsTask.doInBackground called");
        boolean success = false;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "="
                    + URLEncoder.encode(mEvent, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/exhibitors?client_id="
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
                Exhibitors.clear();
                JSONArray jExhibitors = response.getJSONObject("exhibitors").getJSONArray("records");
                for(int i = 0; i < jExhibitors.length(); i++){
                    JSONObject jExhibitor = jExhibitors.getJSONObject(i);
                    URL image = new URL(jExhibitor.getString("Logo__c"));
                    Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    Exhibitors.addExhibitor(new Exhibitors.Exhibitor(jExhibitor.getString("Id"), jExhibitor.getString("Name"),
                            jExhibitor.getString("Description__c"), jExhibitor.getString("Phone__c"), jExhibitor.getString("Email__c"),
                            jExhibitor.getString("Website__c"), picture));
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