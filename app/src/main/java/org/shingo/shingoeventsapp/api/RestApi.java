package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.reportabug.SendReportTask;
import org.shingo.shingoeventsapp.data.venuemaps.GetVenueMapsTask;

/**
 * A central object to store needed
 * data and return an initialized AsyncTask
 *
 * @author Dustin Homan
*/
public class RestApi {
//    private final String mEmail;
//    private final String mPassword;
//    private final int mId;
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
//        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
//        mEmail = sharedPreferences.getString("email", "");
//        mPassword = sharedPreferences.getString("password", "");
//        mId = sharedPreferences.getInt("id", -1);
        API_URL = context.getResources().getString(R.string.api_prod_url);
    }

    /**
     * Used to get data from the API
     * @return a new {@link GetAsyncData} Task
     * @see GetAsyncData
     */
    public GetAsyncData getAsyncData() { return new GetAsyncData(mListener); }

    /**
     * Used to get the Venue Maps stored in {@link org.shingo.shingoeventsapp.data.events.Events.Event}
     * @param id the SF event id to get venue maps for
     * @return a new {@link GetVenueMapsTask}
     * @see GetVenueMapsTask
     */
    public GetVenueMapsTask getVenueMaps(String id) { return new GetVenueMapsTask(id, mListener); }

    /**
     * Used to send a report to the API
     * @return a new {@link SendReportTask}
     * @see SendReportTask
     */
    public SendReportTask sendReport() { return new SendReportTask(mListener);}
}
