package org.shingo.shingoeventsapp.data.attendees;

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
public class ConnectionRequestTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private final String mConnection;
    private final int mId;
    private final OnTaskComplete mListener;
    private String output;

    public ConnectionRequestTask(String email, String password, String connection, int id, OnTaskComplete listener) {
        mEmail = email;
        mPassword = password;
        mConnection = connection;
        mId = id;
        mListener = listener;
        System.out.println("ConnectionRequestTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("ConnectionRequestTask.doInBackground called");
        boolean success  = false;
        try {
            String data = URLEncoder.encode("connection", "UTF-8") + "=" + URLEncoder.encode(mConnection,"UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(mEmail, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(mPassword, "UTF-8");
            data += "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(mId), "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/attendees/addconnection?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
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
            System.out.println("SendReqeust response: " + output);
            success = response.getBoolean("success");
        } catch(UnsupportedEncodingException e){
            return false;
        } catch(IOException e ){
            return false;
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
