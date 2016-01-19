package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.shingo.shingoeventsapp.data.ConnectionApproveTask;
import org.shingo.shingoeventsapp.data.ConnectionRequestTask;
import org.shingo.shingoeventsapp.data.GetAgendasTask;
import org.shingo.shingoeventsapp.data.GetDayTask;
import org.shingo.shingoeventsapp.data.GetEventsTask;
import org.shingo.shingoeventsapp.data.GetAttendeesTask;
import org.shingo.shingoeventsapp.data.GetConnectionsTask;
import org.shingo.shingoeventsapp.data.GetSessionsTask;
import org.shingo.shingoeventsapp.data.GetSpeakersTask;
import org.shingo.shingoeventsapp.data.RegIdTask;

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
}
