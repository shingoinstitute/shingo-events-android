package org.shingo.shingoeventsapp.data.venuemaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.events.Events;

import java.net.URL;


/**
 * This class is used to make an asynchronus call to
 * the API to fetch data.
 * Extends {@link AsyncTask}
 *
 * @author Dustin Homan
 */
public class GetVenueMapsTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private static boolean isWorking;
    private final static Object mutex = new Object();

    /**
     *
     * @param event the SalesForce ID to get the Venue Maps for
     * @param listener the callback to call when task is complete
     */
    public GetVenueMapsTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetVenueMapsTask created");
    }

    /**
     * This method makes the API call
     *
     * @param params not used
     * @return the success of the task
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("GetVenueMapsTask.doInBackground called");
        synchronized (mutex) {
            if (isWorking) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWorking = true;
            }
        }
        if(!VenueMaps.needsRefresh()) return true;
        boolean success = true;
        VenueMaps.clear();
        try {
            for (Events.Event.VenueMaps vm : Events.EVENT_MAP.get(mEvent).venueMaps) {
                try {
                    URL url = new URL(vm.url);
                    Bitmap map = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    VenueMaps.addMap(new VenueMaps.VMap(vm.name, map));
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                    break;
                }
            }
        } catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }

        return success;
    }

    /**
     * This method is called when {@link GetVenueMapsTask#doInBackground(Void...)} is finished.
     * Calls {@link OnTaskComplete#onTaskComplete()} if API call was successful
     *
     * @param success the return value of {@link GetVenueMapsTask#doInBackground(Void...)}
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        synchronized (mutex) {
            isWorking = false;
            mutex.notifyAll();
        }
        if (success) {
            System.out.println("GetVenueMapsTask completed");
            mListener.onTaskComplete();
        } else {
            System.out.println("An error occurred in GetVenueMapsTask...");
        }
    }
}