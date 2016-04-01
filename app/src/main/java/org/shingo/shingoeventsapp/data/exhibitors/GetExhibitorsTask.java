package org.shingo.shingoeventsapp.data.exhibitors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
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
    private static boolean isWorking;
    private static Object mutex = new Object();

    public GetExhibitorsTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetExhibitorsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetExhibitorsTask.doInBackground called");
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
        if(!Exhibitors.needsRefresh()) return true;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "="
                    + URLEncoder.encode(mEvent, "UTF-8");
            URL url = new URL(RestApi.API_URL + "/sfevents/exhibitors?client_id="
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
            System.out.println("Exhibitors: " + output);
            success = response.getBoolean("success");
            if (success) {
                Exhibitors.clear();
                JSONArray jExhibitors = response.getJSONObject("exhibitors").getJSONArray("records");
                for(int i = 0; i < jExhibitors.length(); i++){
                    JSONObject jExhibitor = jExhibitors.getJSONObject(i);
                    URL image;
                    Bitmap picture = null;
                    try {
                        image = new URL(jExhibitor.getString("Logo__c"));
                        picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    Exhibitors.addExhibitor(new Exhibitors.Exhibitor(jExhibitor.getString("Id"), jExhibitor.getString("Name"),
                            jExhibitor.getString("Rich_Description"), jExhibitor.getString("Phone__c"), jExhibitor.getString("Email__c"),
                            jExhibitor.getString("Website__c"), picture));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
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
            System.out.println("GetExhibitorsTask completed");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred in GetExhibitorsTask...");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}