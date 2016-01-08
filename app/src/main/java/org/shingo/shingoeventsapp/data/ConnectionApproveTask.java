package org.shingo.shingoeventsapp.data;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.ui.LoginActivity;
import org.shingo.shingoeventsapp.api.OnTaskComplete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class ConnectionApproveTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mConnectionEmail;
    private final String mPassword;
    private final boolean mApprove;
    private final OnTaskComplete mListener;
    private String output;

    public ConnectionApproveTask(String email, String password, String connection, boolean approve, OnTaskComplete listener) {
        mEmail = email;
        mPassword = password;
        mConnectionEmail = connection;
        mApprove = approve;
        mListener = listener;
        System.out.println("SendConnectRequestTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("SendConnectRequestTask.doInBackground called");
        boolean success  = false;
        try {
            String data = URLEncoder.encode("connection", "UTF-8") + "=" + URLEncoder.encode(mConnectionEmail,"UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(mEmail, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(mPassword, "UTF-8");
            URL url = null;
            if(mApprove) url = new URL("https://shingo-events.herokuapp.com/api/attendees/approveconnection/?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            else url = new URL("https://shingo-events.herokuapp.com/api.attendees/rejectconnection/?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            System.out.println("Writing data: " + data);
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("SendApprove response: " + output);
            success = response.getBoolean("success");
        } catch(UnsupportedEncodingException e){
            return success;
        } catch(IOException e ){
            return success;
        } catch(JSONException e){
            return false;
        }

        return success ;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            System.out.println("Restarting activity");
            mListener.onTaskComplete();
        } else {
            System.out.println("Error occurred sending request");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
