package org.shingo.shingoeventsapp.data.speakers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dustin Homan
 *
 * This class is used to hold the
 * data pertinent to speakers
 * recieved from the API. The lists
 * are static to reduce calls to the API.
 */
public class Speakers {

    public static List<Speaker> SPEAKERS = new ArrayList<>();

    public static Map<String, Speaker> SPEAKER_MAP = new HashMap<>();

    public static Date refresh;

    public static int is_loading = 0;

    /**
     * A check to see when the data was last pulled from the API. If
     * it was longer than 30 minutes the data needs refreshed.
     *
     * @return now > refresh + 30 min
     */
    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    /**
     * Adds an {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker} to both {@link Speakers#SPEAKERS}
     * and {@link Speakers#SPEAKER_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}.
     *
     * @param speaker an {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}
     */
    public static void addSpeaker(Speaker speaker){
        if(SPEAKER_MAP.get(speaker.id) == null){
            SPEAKERS.add(speaker);
            SPEAKER_MAP.put(speaker.id, speaker);
        }
    }

    /**
     * Used to clear {@link Speakers#SPEAKERS} and
     * {@link Speakers#SPEAKER_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        SPEAKERS.clear();
        SPEAKER_MAP.clear();
        refresh = new Date();
    }

    /**
     * Parse {@link Speakers} from a JSON string
     * @param json JSON string representing {@link Speakers}
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException{
        Speakers.clear();
        JSONObject response = new JSONObject(json);
        JSONArray jSpeakers = response.getJSONObject("speakers").getJSONArray("records");
        for(int i = 0; i < jSpeakers.length(); i++){
            Speakers.addSpeaker(Speaker.fromJSON(jSpeakers.getJSONObject(i)));
        }
    }

    /**
     * Holder class for Speaker data.
     */
    public static class Speaker implements Comparable<Speaker>{
        public String id;
        public String name;
        public String displayName;
        public String title;
        public String company;
        public String bio;
        public Bitmap picture;

        /**
         *
         * @param id SalesForce ID of the speaker
         * @param name The speaker's full name
         * @param displayName Optional display name
         * @param title The speaker's title
         * @param company The speaker's company
         * @param bio The speaker's bio
         * @param picture A Bitmap of the speaker's picture
         */
        public Speaker(String id, String name, String displayName, String title, String company, String bio, Bitmap picture){
            this.id = id;
            this.name = name;
            this.displayName = displayName;
            this.title = title;
            this.company = company;
            this.bio = bio;
            this.picture = picture;
            if(displayName.isEmpty()){
                this.displayName = name;
            }
            if(title.equals("null")) this.title = "";
            if(company.equals("null")) this.company = "";
            if(bio.equals("null")) this.bio = "";
        }

        /**
         * Returns a round picture from {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker#picture}
         * @param context Context of the calling {@link android.app.Activity}
         * @return a {@link Bitmap} of a circle with radius 250px of {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker#picture}
         */
        public Bitmap getRoundPicture(Context context){
            int targetWidth = 250;
            int targetHeight = 250;
            Bitmap target = Bitmap.createBitmap(targetWidth,targetHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);
            Path path = new Path();
            path.addCircle(((float) (targetWidth - 1) / 2),
                    ((float) (targetHeight - 1) / 2),
                    (Math.min(((float) (targetWidth / 2)), ((float) (targetHeight / 2)))),
                    Path.Direction.CCW);
            canvas.clipPath(path);
            if(picture == null) {
                Bitmap sill = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_silhouette);
                canvas.drawBitmap(sill, new Rect(0,0,sill.getWidth(), sill.getHeight()),
                        new Rect(0,0, targetWidth, targetHeight), null);
            }
            else {
                canvas.drawBitmap(picture, new Rect(0, 0, picture.getWidth(), picture.getHeight()),
                        new Rect(0, 0, targetWidth, targetHeight), null);
            }
            return target;
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker} from a {@link JSONObject}
         * @param jSpeaker {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}
         * @return {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}
         * @throws JSONException
         */
        public static Speaker fromJSON(JSONObject jSpeaker) throws JSONException{
            if(!jSpeaker.getString("Speaker_Image__c").equals("null"))
                getPicture(jSpeaker.getString("Speaker_Image__c"), jSpeaker.getString("Id"));

            return new Speakers.Speaker(jSpeaker.getString("Id"),
                    jSpeaker.getString("Name"),jSpeaker.getString("Speaker_Display_Name__c"),
                    jSpeaker.getString("Title"), jSpeaker.getString("Organization"),
                    jSpeaker.getString("Rich_Biography"), null);
        }

        /**
         * Get the {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}'s Picture
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}'s picture
         * @param id the {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker#id}
         */
        public static void getPicture(final String url, final String id){
            is_loading++;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(SPEAKER_MAP.containsKey(id)) SPEAKER_MAP.get(id).picture = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Overload of {@link Object#toString()}
         * @return {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker#displayName}
         */
        @Override
        public String toString() { return displayName; }

        /**
         * Sorts speakers based on last name
         * @param another {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}
         * @return sort based on last name
         */
        @Override
        public int compareTo(@NonNull Speaker another) {
            String[] thisNameSplit = this.name.split(" ");
            String[] anotherNameSplit = another.name.split(" ");
            return thisNameSplit[thisNameSplit.length].compareTo(anotherNameSplit[anotherNameSplit.length]);
        }
    }
}
