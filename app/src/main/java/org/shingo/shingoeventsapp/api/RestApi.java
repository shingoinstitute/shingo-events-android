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

/**
 * Created by dustinehoman on 1/8/16.
 */
public class RestApi {
    private final String mEmail;
    private final String mPassword;
    private final int mId;
    private OnTaskComplete mListener;

    public RestApi(OnTaskComplete listener, Context context){
        mListener = listener;
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        mEmail = sharedPreferences.getString("email", "");
        mPassword = sharedPreferences.getString("password", "");
        mId = sharedPreferences.getInt("id", -1);
    }

    public GetEventsTask getEvents(){
        return new GetEventsTask(mListener);
    }

    public RegIdTask addRegId(String regId) {
        return new RegIdTask(mEmail, mPassword, regId, mListener);
    }

    public GetConnectionsTask getConnections() {
        return new GetConnectionsTask(mId, mListener);
    }

    public ConnectionApproveTask approveConnection(String email, boolean approve) {
        return new ConnectionApproveTask(mEmail, mPassword, email, approve, mListener);
    }

    public GetAttendeesTask getAttendees() {
        return new GetAttendeesTask(mEmail, mListener);
    }

    public ConnectionRequestTask sendRequest(String connection) {
        return new ConnectionRequestTask(mEmail, mPassword, connection, mId, mListener);
    }

    public GetSessionTask getSession(String id) { return new GetSessionTask(id, mListener); }

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
}
