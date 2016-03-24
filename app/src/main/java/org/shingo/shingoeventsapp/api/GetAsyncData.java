package org.shingo.shingoeventsapp.api;

import android.os.AsyncTask;

import org.json.JSONStringer;
import org.shingo.shingoeventsapp.data.affiliates.Affiliates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Dustin Homan
 *
 * This class is used to make an asynchronus call to
 * the API to fetch the {@link Affiliates.Affiliate}s
 * related to the event. Extends {@link AsyncTask}
 */
public class GetAsyncData extends AsyncTask<String, Void, Boolean> {

    private OnTaskComplete mListener;
    private static boolean isWorking = false;
    private static Object mutex = new Object();
    String output;

    /**
     * Constructor
     * @param listener the callback to call when task is complete
     */
    public GetAsyncData(OnTaskComplete listener) {
        mListener = listener;
        System.out.println("GetAsyncData created");
    }

    /**
     * This method makes the API call, parses the JSON response
     * and stores the data in {@link Affiliates#AFFILIATES} and
     * {@link Affiliates#AFFILIATE_MAP} via {@link Affiliates#addAffiliate(Affiliates.Affiliate)}
     *
     * @param params a list of parameters. params[0] = api path. params[1] = data for POST
     * @return the success of the task
     */
    @Override
    protected Boolean doInBackground(String... params) {
        System.out.println("GetAsyncData.doInBackground called");
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

        try {
            URL url = new URL( RestApi.API_URL + params[0] + "?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
            URLConnection conn = url.openConnection();

            if(params.length > 1){
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(params[1]);
                wr.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            output = sb.toString();
            System.out.println("GetAsyncData for " + url.toExternalForm() + ": " + output);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This method is called when {@link GetAsyncData#doInBackground(String...)} is finished.
     *
     * @param success the return value of {@link GetAsyncData#doInBackground(String...)}
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        synchronized (mutex) {
            isWorking = false;
            mutex.notifyAll();
        }
        if (success) {
            System.out.println("GetAsyncData completed");
            mListener.onTaskComplete(output);
        } else {
            System.out.println("An error occurred in GetAsyncData...");
        }
    }
}
