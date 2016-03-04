package org.shingo.shingoeventsapp.data.affiliates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.events.Events;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

/**
 * @author Dustin Homan
 *
 * This class is used to make an asynchronus call to
 * the API to fetch the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}s
 * related to the event. Extends {@link AsyncTask}
 */
public class GetAffiliatesTask extends AsyncTask<Void, Void, Boolean> {

    private OnTaskComplete mListener;

    /**
     * Constructor
     * @param listener the callback to call when task is complete
     */
    public GetAffiliatesTask(OnTaskComplete listener) {
        mListener = listener;
        System.out.println("GetAffiliatesTask created");
    }

    /**
     * This method makes the API call, parses the JSON response
     * and stores the data in {@link Affiliates#AFFILIATES} and
     * {@link Affiliates#AFFILIATE_MAP} via {@link Affiliates#addAffiliate(Affiliates.Affiliate)}
     *
     * @param params a list of parameters. Will always be null.
     * @return the success of the task
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetAffiliatesTask.doInBackground called");
        boolean success;
        try {
            URL url = new URL("https://shingo-events.herokuapp.com/api/sfevents/affiliates?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String output = sb.toString();
            success = parseJSON(output);
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

    /**
     * This method is called when {@link GetAffiliatesTask#doInBackground(Void...)} is finished.
     *
     * @param success the return value of {@link GetAffiliatesTask#doInBackground(Void...)}
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            System.out.println("Calling mListener.onTaskComplete");
            Collections.sort(Events.EVENTS);
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred...");
        }
    }

    /**
     * Parses the JSON response from the API call
     *
     * @param output the result from the API call in {@link GetAffiliatesTask#doInBackground}
     * @return the success variable stored in the output
     * @throws JSONException
     * @throws IOException
     */
    private boolean parseJSON(String output) throws JSONException, IOException{
        JSONObject response = new JSONObject(output);
        System.out.println("Affiliates " + output);
        boolean success = response.getBoolean("success");
        Events.clear();
        if (success) {
            Affiliates.clear();
            JSONArray jAffiliates = response.getJSONObject("affiliates").getJSONArray("records");
            for (int i = 0; i < jAffiliates.length(); i++) {
                JSONObject jAffiliate = jAffiliates.getJSONObject(i);
                if(!jAffiliate.getString("Logo__c").equals("null")) {
                    URL image = new URL(jAffiliate.getString("Logo__c"));
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

        return success;
    }
}
