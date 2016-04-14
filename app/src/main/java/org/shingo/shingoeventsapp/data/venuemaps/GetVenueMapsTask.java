package org.shingo.shingoeventsapp.data.venuemaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.data.events.Events;

import java.net.URL;


/**
 * Created by dustinehoman on 2/10/16.
 */
public class GetVenueMapsTask extends AsyncTask<Void, Void, Boolean> {

    private String mEvent;
    private OnTaskComplete mListener;
    private static boolean isWorking;
    private static Object mutex = new Object();

    public GetVenueMapsTask(String event, OnTaskComplete listener) {
        mEvent = event;
        mListener = listener;
        System.out.println("GetVenueMapsTask created");
    }

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

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}