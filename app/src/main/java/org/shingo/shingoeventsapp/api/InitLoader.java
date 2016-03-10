package org.shingo.shingoeventsapp.api;

import android.content.Context;

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

    public InitLoader(String event, Context context){
        mEvent = event;
        mContext = context;
    }

    /**
     * Use the {@link RestApi} to load data for the events.
     */
    public void load(){
        RestApi api = new RestApi(this, mContext);
        api.getAgendas(mEvent).execute((Void) null);
        api.getAffiliates().execute((Void) null);
        api.getExhibitors(mEvent).execute((Void) null);
        api.getRecipients(mEvent).execute((Void) null);
        api.getSessions(mEvent).execute((Void) null);
        api.getSpeakers(mEvent).execute((Void) null);
        api.getSponsors(mEvent).execute((Void) null);
    }

    /**
     * Callback to log that an API call completed
     */
    @Override
    public void onTaskComplete() {
        System.out.println("API call complete");
    }
}
