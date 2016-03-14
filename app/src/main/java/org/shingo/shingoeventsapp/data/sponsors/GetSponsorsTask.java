package org.shingo.shingoeventsapp.data.sponsors;

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
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dustinehoman on 2/10/16.
 */
public class GetSponsorsTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private static boolean isWorking;

    public GetSponsorsTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetSponsorsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetSponsorsTask.doInBackground called");
        synchronized (this){
            if(isWorking){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWorking = true;
            }
        }
        if(!Sponsors.needsRefresh()) return true;
        boolean success;
        try {
            String data = URLEncoder.encode("event_id", "UTF-8") + "="
                    + URLEncoder.encode(mEvent, "UTF-8");
            URL url = new URL(RestApi.API_URL + "/sfevents/sponsors?client_id="
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
            System.out.println("Sponsors: " + output);
            success = response.getBoolean("success");
            if (success && Sponsors.needsRefresh()) {
                Sponsors.clear();
                JSONObject jsonObject = response.getJSONObject("sponsors");
                List<JSONArray> sponsors = new ArrayList<>();
                sponsors.add(jsonObject.getJSONObject("friends").getJSONArray("records"));
                sponsors.add(jsonObject.getJSONObject("supporters").getJSONArray("records"));
                sponsors.add(jsonObject.getJSONObject("benefactors").getJSONArray("records"));
                sponsors.add(jsonObject.getJSONObject("champions").getJSONArray("records"));
                sponsors.add(jsonObject.getJSONObject("presidents").getJSONArray("records"));
                for(JSONArray jSponsors : sponsors){
                    for(int i = 0; i < jSponsors.length(); i++){
                        JSONObject jSponsor = jSponsors.getJSONObject(i);
                        String level = jSponsor.getString("Level__c");
                        if(level.equals("Friend")){
                            Bitmap logo = null;
                            try{
                                URL image = new URL(jSponsor.getString("Logo__c"));
                                logo = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Sponsors.addSponsor(new Sponsors.Sponsor(jSponsor.getString("Id"), jSponsor.getString("Name"),
                                    level, null,logo));
                        } else {
                            Bitmap logo = null;
                            Bitmap banner = null;
                            try{
                                URL logo_image = new URL(jSponsor.getString("Logo__c"));
                                URL banner_image = new URL(jSponsor.getString("Banner__c"));
                                logo = BitmapFactory.decodeStream(logo_image.openConnection().getInputStream());
                                banner = BitmapFactory.decodeStream(banner_image.openConnection().getInputStream());
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Sponsors.addSponsor(new Sponsors.Sponsor(jSponsor.getString("Id"), jSponsor.getString("Name"),
                                    level, banner,logo));
                        }
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