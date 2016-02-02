package org.shingo.shingoeventsapp.data.affiliates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.events.Events;
import org.shingo.shingoeventsapp.ui.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

/**
 * Created by dustinehoman on 2/2/16.
 */
public class GetAffiliatesTask extends AsyncTask<Void, Void, Boolean> {

    private OnTaskComplete mListener;
    private String output;

    public GetAffiliatesTask(OnTaskComplete listener) {
        mListener = listener;
        System.out.println("GetAffiliatesTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("GetAffiliatesTask.doInBackground called");
        boolean success = false;
        try {
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/affiliates?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("Affiliates " + output);
            success = response.getBoolean("success");
            Events.clear();
            if (success) {
                Affiliates.clear();
                JSONArray jAffiliates = response.getJSONObject("affiliates").getJSONArray("records");
                for (int i = 0; i < jAffiliates.length(); i++) {
                    JSONObject jAffiliate = jAffiliates.getJSONObject(i);
                    URL image = null;
                    if(!jAffiliate.getString("Logo__c").equals("null")) {
                        image = new URL(jAffiliate.getString("Logo__c"));
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        Affiliates.addAffiliate(new Affiliates.Affiliate(jAffiliate.getString("Id"),
                                jAffiliate.getString("Name"), jAffiliate.getString("App_Abstract__c"),
                                picture, jAffiliate.getString("Website"), jAffiliate.getString("Phone"),
                                jAffiliate.getString("Public_Contact_Email__c")));
                    } else {
                        Affiliates.addAffiliate(new Affiliates.Affiliate(jAffiliate.getString("Id"),
                                jAffiliate.getString("Name"), jAffiliate.getString("App_Abstract__c"),
                                null, jAffiliate.getString("Website"), jAffiliate.getString("Phone"),
                                jAffiliate.getString("Public_Contact_Email__c")));
                    }
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
            Collections.sort(Events.EVENTS);
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
