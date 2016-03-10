package org.shingo.shingoeventsapp.data.connections;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.ui.attendees.LoginActivity;
import org.shingo.shingoeventsapp.api.OnTaskComplete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class GetConnectionsTask extends AsyncTask<Void, Void, Boolean> {

    private final int mId;
    private final OnTaskComplete mListener;
    private String output;

    public GetConnectionsTask(int id, OnTaskComplete listener) {
        mId = id;
        mListener = listener;
        System.out.println("GetConnectionsTask created");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetConnections.doInBackground called");
        boolean success  = false;
        try {
            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(mId),"UTF-8");
            URL url = new URL(RestApi.API_URL + "/attendees/connections?client_id=" + RestApi.CLIENT_ID + "&client_secret=" + RestApi.CLIENT_SECRET);
            System.out.println("Opening connection: " + url.getPath());
            URLConnection conn = url.openConnection();
            System.out.println("Got connection: " + conn.getURL());
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            System.out.println("Wrote post body");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            output = sb.toString();
            JSONObject response = new JSONObject(output);
            System.out.println("getConnections() response: " + output);
            success = response.getBoolean("success");
            if(success){
                System.out.println("Connections fetched successfully");
                Connections.clear();
                JSONArray jConnections = response.getJSONArray("connections");
                for(int i = 0; i < jConnections.length(); i++){
                    JSONObject jConnection = jConnections.getJSONObject(i);
                    if(jConnection.getString("status").equals("approved") || jConnection.getString("status").equals("pending") || jConnection.getString("status").equals("waiting")) {
                        Connections.addConnection(new Connections.Connection(jConnection.getInt("ID"),jConnection.getString("email"),
                                jConnection.getString("first_name"), jConnection.getString("last_name"), jConnection.getString("display_name"),
                                jConnection.getString("title"), jConnection.getString("company"), jConnection.getString("status")));
                    }
                }
            }
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
            System.out.println("Setting list adapter");
            Collections.sort(Connections.CONNECTIONS);
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
