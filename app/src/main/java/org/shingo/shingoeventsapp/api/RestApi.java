package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.connections.ConnectionApproveTask;
import org.shingo.shingoeventsapp.data.attendees.ConnectionRequestTask;
import org.shingo.shingoeventsapp.data.attendees.GetAttendeesTask;
import org.shingo.shingoeventsapp.data.connections.GetConnectionsTask;
import org.shingo.shingoeventsapp.data.reportabug.SendReportTask;
import org.shingo.shingoeventsapp.data.speakers.GetSpeakersTask;
import org.shingo.shingoeventsapp.data.events.RegIdTask;
import org.shingo.shingoeventsapp.data.sponsors.GetSponsorsTask;
import org.shingo.shingoeventsapp.data.venuemaps.GetVenueMapsTask;

/*********************************************
 * @author Dustin Homan                      *
 * Purpose: A central object to store needed *
 *      data and return an initialized       *
 *      AsyncTask                            *
 *********************************************/
public class RestApi {
    private final String mEmail;
    private final String mPassword;
    private final int mId;
    private OnTaskComplete mListener;

    public static final String CLIENT_ID = "b5a635526386a516edcafa2479a7a8ac";
    public static final String CLIENT_SECRET = "72988d72f5e3496e8e8a0edf5a3f87c9";
    public static String API_URL;

    /**
     * Constructor
     *
     * @param listener a OnTaskComplete interface
     * @param context the calling classes context
     */
    public RestApi(OnTaskComplete listener, Context context){
        mListener = listener;
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        mEmail = sharedPreferences.getString("email", "");
        mPassword = sharedPreferences.getString("password", "");
        mId = sharedPreferences.getInt("id", -1);
        API_URL = context.getResources().getString(R.string.api_prod_url);
    }

    public GetAsyncData getAsyncData() { return new GetAsyncData(mListener); }

    /**
     *
     * @param regId the RegOnline Id of an attendee
     * @return a new RegIdTask
     * @see RegIdTask
     */
    public RegIdTask addRegId(String regId) {
        return new RegIdTask(mEmail, mPassword, regId, mListener);
    }

    /**
     *
     * @return a new GetConnectionsTask
     * @see GetConnectionsTask
     */
    public GetConnectionsTask getConnections() {
        return new GetConnectionsTask(mId, mListener);
    }

    /**
     *
     * @param email email of the connection
     * @param approve boolean whether to approve connection
     * @return a new ConnectionApproveTask
     * @see ConnectionApproveTask
     */
    public ConnectionApproveTask approveConnection(String email, boolean approve) {
        return new ConnectionApproveTask(mEmail, mPassword, email, approve, mListener);
    }

    /**
     *
     * @return a new GetAttendeesTask
     * @see GetAttendeesTask
     */
    public GetAttendeesTask getAttendees() {
        return new GetAttendeesTask(mEmail, mListener);
    }

    /**
     *
     * @param connection email of the connection to request
     * @return a new ConnectionRequestTask
     * @see ConnectionRequestTask
     */
    public ConnectionRequestTask sendRequest(String connection) {
        return new ConnectionRequestTask(mEmail, mPassword, connection, mId, mListener);
    }

    /**
     *
     * @param id the SF event id to get speakers of
     * @return a new GetSpeakersTask
     * @see GetSpeakersTask
     */
    public GetSpeakersTask getSpeakers(String id) {
        return new GetSpeakersTask(id, mListener);
    }

    /**
     *
     * @param id the SF event id to get sponsors for
     * @return a new GetSponsorsTask
     * @see GetSponsorsTask
     */
    public GetSponsorsTask getSponsors(String id) { return new GetSponsorsTask(id, mListener); }

    /**
     *
     * @param id the SF event id to get venue maps for
     * @return a new GetVenueMapsTask
     * @see GetVenueMapsTask
     */
    public GetVenueMapsTask getVenueMaps(String id) { return new GetVenueMapsTask(id, mListener); }

    public SendReportTask sendReport() { return new SendReportTask(mListener);}
}
