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
public class RegIdTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private final String mRegId;
    private final OnTaskComplete mListener;
    private String output;

    public RegIdTask(String email, String password, String regId, OnTaskComplete listener) {
        mEmail = email;
        mPassword = password;
        mRegId = regId;
        mListener = listener;
        System.out.println("AddRegIdTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        System.out.println("AddRegIdTask.doInBackground called");
        boolean success  = false;
        try {
            String data = URLEncoder.encode("reg_id", "UTF-8") + "=" + URLEncoder.encode(mRegId,"UTF-8");
            data += "&" + URLEncoder.encode("email","UTF-8") + "=" + URLEncoder.encode(mEmail, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +URLEncoder.encode(mPassword, "UTF-8");
            URL url = new URL("https://shingo-events.herokuapp.com/api/attendees/addregid/?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
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
            System.out.println("AddRegId response: " + output);
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
            mListener.onTaskComplete();
        } else {
            System.out.println("Error occurred adding regid");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}