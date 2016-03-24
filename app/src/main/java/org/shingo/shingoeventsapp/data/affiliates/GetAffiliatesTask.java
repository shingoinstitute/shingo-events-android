package org.shingo.shingoeventsapp.data.affiliates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Dustin Homan
 *
 * This class is used to make an asynchronus call to
 * the API to fetch the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}s
 * related to the event. Extends {@link AsyncTask}
 */
public class GetAffiliatesTask extends AsyncTask<String, Void, Boolean> {

    private OnTaskComplete mListener;
    private static boolean isWorking = false;
    private static Object mutex = new Object();
    String output;

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
    protected Boolean doInBackground(String... params) {
        System.out.println("GetAffiliatesTask.doInBackground called");
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
        if(!Affiliates.needsRefresh()) return true;
        boolean success;
        try {
            URL url = new URL( RestApi.API_URL + "/sfevents/affiliates?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            output = sb.toString();
            success = output.contains("success: true") || output.contains("success:true");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
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
        synchronized (mutex) {
            isWorking = false;
            mutex.notifyAll();
        }
        if (success) {
            System.out.println("GetAffiliatesTask completed");
            mListener.onTaskComplete(output);
        } else {
            System.out.println("An error occurred in GetAffiliatesTask...");
        }
    }
}
