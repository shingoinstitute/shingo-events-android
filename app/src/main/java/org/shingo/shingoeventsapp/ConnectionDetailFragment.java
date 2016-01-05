package org.shingo.shingoeventsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.connections.Connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * A fragment representing a single Connection detail screen.
 * This fragment is either contained in a {@link ConnectionListActivity}
 * in two-pane mode (on tablets) or a {@link ConnectionDetailActivity}
 * on handsets.
 */
public class ConnectionDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Connections.Connection mConnection;

    private SendConnectApproveTask sendConnectApproveTask = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mConnection = Connections.CONNECTION_MAP.get(getArguments().getString(ARG_ITEM_ID));


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mConnection.toString());
        }

        View rootView = inflater.inflate(R.layout.fragment_connection_detail, container, false);
        View parent = container.getRootView();
        if (mConnection != null) {
            TextView emailView = (TextView) rootView.findViewById(R.id.connection_email);
            TextView nameView = (TextView) rootView.findViewById(R.id.connection_display_name);
            TextView titleView = (TextView) rootView.findViewById(R.id.connection_title);
            TextView companyView = (TextView) rootView.findViewById(R.id.connection_company);
            emailView.setText(mConnection.email);
            nameView.setText(mConnection.toString());
            titleView.setText(mConnection.title);
            companyView.setText(mConnection.company);
            FloatingActionButton fab = (FloatingActionButton) parent.findViewById(R.id.fab);
            System.out.println("mAttendee.status " + mConnection.status);
            switch(mConnection.status.toLowerCase()){
                case "approved":
                    //TODO: send message
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.stat_notify_chat));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view, "Opening chat...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    break;
                case "pending":
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.title_approve);
                            builder.setMessage(getResources().getString(R.string.approve_message) + " " + mConnection.email);
                            builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendApprove(true);
                                }
                            }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendApprove(false);
                                }
                            }).show();
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        return rootView;
    }

    private void sendApprove(boolean approve){
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        sendConnectApproveTask = new SendConnectApproveTask(email, password, mConnection.email, approve);
        sendConnectApproveTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous Send a connection request task used to add a
     * connection to a user.
     */
    private class SendConnectApproveTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mConnectionEmail;
        private final String mPassword;
        private final boolean mApprove;
        private String output;

        SendConnectApproveTask(String email, String password, String connection, boolean approve) {
            mEmail = email;
            mPassword = password;
            mConnectionEmail = connection;
            mApprove = approve;
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
            sendConnectApproveTask = null;

            if (success) {
                System.out.println("Restarting activity");
                if(mApprove) {
                    mConnection.status = "approved";
                    Fragment currentFrag = getFragmentManager().findFragmentById(getId());
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(currentFrag);
                    fragmentTransaction.attach(currentFrag);
                    fragmentTransaction.commit();
                } else {
                    getActivity().finish();
                }
            } else {
                System.out.println("Error occurred sending request");
            }
        }

        @Override
        protected void onCancelled() {
            sendConnectApproveTask = null;
        }
    }
}
