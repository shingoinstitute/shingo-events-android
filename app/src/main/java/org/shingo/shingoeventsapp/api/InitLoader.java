package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;
import org.shingo.shingoeventsapp.data.affiliates.Affiliates;
import org.shingo.shingoeventsapp.data.exhibitors.Exhibitors;
import org.shingo.shingoeventsapp.data.recipients.Recipients;
import org.shingo.shingoeventsapp.data.sessions.Sessions;

import java.net.URLEncoder;
import java.util.concurrent.Executor;

/**
 * @author Dustin Homan
 * This class is used to load all of the data
 * for {@link org.shingo.shingoeventsapp.data.agendas.Agendas}, {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates},
 * {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors}, {@link org.shingo.shingoeventsapp.data.recipients.Recipients}
 * {@link org.shingo.shingoeventsapp.data.sessions.Sessions}, {@link org.shingo.shingoeventsapp.data.speakers.Speakers},
 * and {@link org.shingo.shingoeventsapp.data.sponsors.Sponsors}.
 */
public class InitLoader implements OnTaskComplete {
    private String mEvent;
    private Context mContext;
    private int count;

    public InitLoader(String event, Context context){
        mEvent = event;
        mContext = context;
    }

    /**
     * Use the {@link RestApi} to load data for the events.
     */
    public void load(){
        RestApi api = new RestApi(this, mContext);
        try{
            String eventIDParam = URLEncoder.encode("event_id", "UTF-8") + "="
                    + URLEncoder.encode(mEvent, "UTF-8");
            String[] exhibitorParams = {"/sfevents/exhibitors", eventIDParam};
            String[] recipientsParams = {"/sfevents/recipients", eventIDParam};
            String[] sessionsParams = {"/sfevents/sessions", eventIDParam};
            api.getAsyncData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/sfevents/affiliates");
            api.getAsyncData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, exhibitorParams);
            api.getAsyncData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, recipientsParams);
            api.getAsyncData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sessionsParams);
            api.getSpeakers(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            api.getSponsors(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            api.getVenueMaps(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Callback to log that an API call completed
     */
    @Override
    public void onTaskComplete() {
        count++;
        System.out.println("API call complete " + count + "/8");
    }

    @Override
    public void onTaskComplete(String response) {
        count++;
        System.out.println("API call complete " + count + "/8");
        try {
            JSONObject res = new JSONObject(response);
            if (res.has("affiliates")) {
                if (Affiliates.needsRefresh())
                    Affiliates.fromJSON(response);
            } else if(res.has("exhibitors")){
                if(Exhibitors.needsRefresh())
                    Exhibitors.fromJSON(response);
            } else if(res.has("recipients")){
                if(Recipients.needsRefresh())
                    Recipients.fromJSON(response);
            } else if (res.has("sessions")) {
                if(Sessions.needsRefresh())
                    Sessions.fromJSON(response);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
