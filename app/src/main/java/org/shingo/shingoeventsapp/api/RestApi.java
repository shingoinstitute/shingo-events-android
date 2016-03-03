package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.content.SharedPreferences;

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
import org.shingo.shingoeventsapp.data.sessions.GetSessionTask;
import org.shingo.shingoeventsapp.data.sessions.GetSessionsTask;
import org.shingo.shingoeventsapp.data.speakers.GetSpeakersTask;
import org.shingo.shingoeventsapp.data.events.RegIdTask;
import org.shingo.shingoeventsapp.data.sponsors.GetSponsorsTask;
import org.shingo.shingoeventsapp.data.venuemaps.GetVenueMapsTask;

/*********************************************
 * Created by Dustin Homan on 1/8/16.        *
 * Purpose: A central object to store needed *
 *      data and return an initialized       *
 *      AsyncTask                            *
 *********************************************/
public class RestApi {
    private final String mEmail;
    private final String mPassword;
    private final int mId;
    private OnTaskComplete mListener;

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
     *
     * @param id the SF session id to get
     * @return a new GetSessionTask
     * @see GetSessionTask
     */
    public GetSessionTask getSession(String id) { return new GetSessionTask(id, mListener); }

    /**
     *
     * @param id the SF event id to get session of
     * @return
     */
    public GetSessionsTask getSessions(String id) {
        return new GetSessionsTask(id, mListener);
    }

    public GetSpeakersTask getSpeakers(String id) {
        return new GetSpeakersTask(id, mListener);
    }

    public GetAgendasTask getAgendas(String id){
        return new GetAgendasTask(id, mListener);
    }

    public GetDayTask getDay(String id){
        return new GetDayTask(id, mListener);
    }

    public GetAffiliatesTask getAffiliates() {
        return new GetAffiliatesTask(mListener);
    }

    public GetExhibitorsTask getExhibitors(String id) { return new GetExhibitorsTask(id, mListener); }

    public GetRecipientsTask getRecipients(String id) { return new GetRecipientsTask(id, mListener); }

    public GetSponsorsTask getSponsors(String id) { return new GetSponsorsTask(id, mListener); }

    public GetVenueMapsTask getVenueMaps(String id) { return new GetVenueMapsTask(id, mListener); }
}
