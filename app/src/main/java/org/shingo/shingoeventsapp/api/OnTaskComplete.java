package org.shingo.shingoeventsapp.api;

/**
 * This interface is used as callback for
 * {@link android.os.AsyncTask}.
 */
public interface OnTaskComplete {
    /**
     * Callback used when nothing is expected back
     */
    void onTaskComplete();

    /**
     * Callback used when a JSON string is expected back
     * @param response A JSON string to parse
     */
    void onTaskComplete(String response);
}
