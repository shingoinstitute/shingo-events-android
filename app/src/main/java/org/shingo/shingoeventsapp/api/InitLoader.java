package org.shingo.shingoeventsapp.api;

import android.content.Context;
import android.os.AsyncTask;

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
        api.getAgendas(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void) null);
        api.getAsyncData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/sfevents/affiliates");
        api.getExhibitors(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        api.getRecipients(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        api.getSessions(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        api.getSpeakers(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        api.getSponsors(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        api.getVenueMaps(mEvent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
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

    }
}
