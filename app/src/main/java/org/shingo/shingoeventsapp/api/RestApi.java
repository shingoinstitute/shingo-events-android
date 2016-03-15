package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.affiliates.GetAffiliatesTask;
import org.shingo.shingoeventsapp.data.connections.ConnectionApproveTask;
import org.shingo.shingoeventsapp.data.attendees.ConnectionRequestTask;
import org.shingo.shingoeventsapp.data.agendas.GetAgendasTask;
import org.shingo.shingoeventsapp.data.agendas.GetDayTask;
import org.shingo.shingoeventsapp.data.events.GetEventsTask;
import org.shingo.shingoeventsapp.data.attendees.GetAttendeesTask;
import org.shingo.shingoeventsapp.data.connections.GetConnectionsTask;
import org.shingo.shingoeventsapp.data.exhibitors.GetExhibitorsTask;
import org.shingo.shingoeventsapp.data.recipients.GetRecipientsTask;
import org.shingo.shingoeventsapp.data.reportabug.SendReportTask;
import org.shingo.shingoeventsapp.data.sessions.GetSessionTask;
import org.shingo.shingoeventsapp.data.sessions.GetSessionsTask;
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

    /**
     *
     * @return a new GetEventsTask
     * @see GetEventsTask
     */
    public GetEventsTask getEvents(){
        return new GetEventsTask(mListener);
    }

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
     *when accounts
     * are enabled.
     * @param id the SF session id to get
     * @return a new GetSessionTask
     * @see GetSessionTask
     */
    public GetSessionTask getSession(String id) { return new GetSessionTask(id, mListener); }

    /**
     *
     * @param id the SF event id to get sessions of
     * @return a new GetSessionsTask
     * @see GetSessionsTask
     */
    public GetSessionsTask getSessions(String id) {
        return new GetSessionsTask(id, mListener);
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
     * @param id the SF event id to get the agenda of
     * @return a new GetAgendasTask
     * @see GetAgendasTask
     */
    public GetAgendasTask getAgendas(String id){
        return new GetAgendasTask(id, mListener);
    }

    /**
     *
     * @param id the SF day id to get
     * @return a new GetDayTask
     * @see GetDayTask
     */
    public GetDayTask getDay(String id){
        return new GetDayTask(id, mListener);
    }

    /**
     *
     * @return a new GetAffiliatesTask
     * @see GetAffiliatesTask
     */
    public GetAffiliatesTask getAffiliates() {
        return new GetAffiliatesTask(mListener);
    }

    /**
     *
     * @param id the SF event id to get exhibitors for
     * @return a new GetExhibitorsTask
     * @see GetExhibitorsTask
     */
    public GetExhibitorsTask getExhibitors(String id) { return new GetExhibitorsTask(id, mListener); }

    /**
     *
     * @param id the SF event id to get exhibitors for
     * @return a new GetRecipientsTask
     * @see GetRecipientsTask
     */
    public GetRecipientsTask getRecipients(String id) { return new GetRecipientsTask(id, mListener); }

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
