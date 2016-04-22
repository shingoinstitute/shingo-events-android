package org.shingo.shingoeventsapp.api;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is used to make an asynchronous call to
 * the API to fetch data.
 * Extends {@link AsyncTask}
 *
 * @author Dustin Homan
 */
public class GetAsyncData extends AsyncTask<String, Void, Boolean> {

    private OnTaskComplete mListener;
    private static boolean isWorking = false;
    private final static Object mutex = new Object();
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
     * This method makes the API call
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
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            if(params.length > 1){
                wr.write(params[1] + "&" + RestApi.CLIENT_POST);
                wr.flush();
            } else {
                wr.write(RestApi.CLIENT_POST);
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
     * Calls {@link OnTaskComplete#onTaskComplete(String)} if API call was successful
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
