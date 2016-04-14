package org.shingo.shingoeventsapp.data.reportabug;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.venuemaps.VenueMaps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by dustinehoman on 3/15/16.
 */
public class SendReportTask  extends AsyncTask<String, Void, Boolean> {

    private OnTaskComplete mListener;
    private static boolean isWorking;
    private static Object mutex = new Object();

    public SendReportTask(OnTaskComplete listener) {
        mListener = listener;
        System.out.println("SendReportTask created");
    }

    @Override
    protected Boolean doInBackground(String... params) {
        System.out.println("SendReportTask.doInBackground called");
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
        VenueMaps.clear();
        try {
            String data = URLEncoder.encode("system_information", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8");
            data += "&" + URLEncoder.encode("report", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
            data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");
            URL url = new URL(RestApi.API_URL + "/reportabug/?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            System.out.println("Writing reportabug data: " + data);
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            String output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("SendReport response: " + output);
            success = response.getBoolean("success");
        } catch(Exception e){
            e.printStackTrace();
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
            System.out.println("SendReportTask completed");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred in SendReportTask...");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
