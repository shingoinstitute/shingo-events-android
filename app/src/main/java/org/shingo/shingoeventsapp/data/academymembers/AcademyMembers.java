package org.shingo.shingoeventsapp.data.academymembers;

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
 * This class is used to hold the
 * data pertinent to academy members
 * received from the API. The lists
 * are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class AcademyMembers {
    /**
     * Holds {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}s
     */
    public static List<AcademyMember> ACADEMY_MEMBERS = new ArrayList<>();

    /**
     * Map to {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}s.
     * The key is {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember#id}
     */
    public static Map<String, AcademyMember> ACADEMY_MEMBERS_MAP = new HashMap<>();

    /**
     * {@link Date} when data was last pulled from API
     */
    public static Date refresh;

    /**
     * If >0 data is still loading
     */
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
        return now.after(new Date(refresh.getTime() + (30 * 60000)));
    }

    /**
     * Adds an {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember} to both {@link AcademyMembers#ACADEMY_MEMBERS}
     * and {@link AcademyMembers#ACADEMY_MEMBERS_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}.
     *
     * @param academyMember an {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}
     */
    public static void addAcademyMember(AcademyMember academyMember){
        if(ACADEMY_MEMBERS_MAP.get(academyMember.id) == null){
            ACADEMY_MEMBERS.add(academyMember);
            ACADEMY_MEMBERS_MAP.put(academyMember.id, academyMember);
        }
    }

    /**
     * Used to clear {@link AcademyMembers#ACADEMY_MEMBERS} and
     * {@link AcademyMembers#ACADEMY_MEMBERS_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        ACADEMY_MEMBERS.clear();
        ACADEMY_MEMBERS_MAP.clear();
        refresh = new Date();
    }


    /**
     * Parse {@link AcademyMembers} from a JSON string
     * @param json JSON string representing {@link AcademyMembers}
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException{
        JSONObject response = new JSONObject(json);
        clear();
        JSONArray jAcademyMembers = response.getJSONObject("academy_members").getJSONArray("records");
        for (int i = 0; i < jAcademyMembers.length(); i++)
            addAcademyMember(AcademyMember.fromJSON(jAcademyMembers.getJSONObject(i)));
    }

    /**
     * Holder class for Academy Member data.
     */
    public static class AcademyMember implements Comparable<AcademyMember>{
        /**
         * SalesForce ID
         */
        public String id;
        public String name;
        public String title;
        public String bio;
        public String org;
        public Bitmap picture;

        /**
         *
         * @param id the SF id of the Academy Member
         * @param name the member's name
         * @param title the member's title
         * @param org the member's organization
         * @param bio the member's biography
         */
        public AcademyMember(String id, String name, String title, String org, String bio){
            this.id = id;
            this.name = name;
            this.title = title;
            this.org = org;
            this.bio = bio;
            if(bio.equals("null")) this.bio = "Bio coming soon!";
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember} from a {@link JSONObject}
         * @param jAcademyMember {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}
         * @return {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}
         * @throws JSONException
         */
        public static AcademyMember fromJSON(JSONObject jAcademyMember) throws JSONException{
            if(!jAcademyMember.getString("Photograph__c").equals("null"))
                getPicture(jAcademyMember.getString("Photograph__c"), jAcademyMember.getString("Id"));

            return new AcademyMember(jAcademyMember.getString("Id"),
                    jAcademyMember.getString("Name"), jAcademyMember.getString("Title"),
                    jAcademyMember.getString("Organization"), jAcademyMember.getString("Biography__c"));
        }


        /**
         * Get the {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}'s Picture
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember}'s picture
         * @param id the {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember#id}
         */
        public static void getPicture(final String url, final String id){
            is_loading++;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(ACADEMY_MEMBERS_MAP.containsKey(id)) ACADEMY_MEMBERS_MAP.get(id).picture = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Returns a round picture from {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember#picture}
         * @param context Context of the calling {@link android.app.Activity}
         * @return a {@link Bitmap} of a circle with radius 250px of {@link org.shingo.shingoeventsapp.data.academymembers.AcademyMembers.AcademyMember#picture}
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
         * Overload of {@link Object#toString()}
         * @return {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker#displayName}
         */
        @Override
        public String toString() { return name; }

        /**
         * Sorts speakers based on last name
         * @param another {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}
         * @return sort based on last name
         */
        @Override
        public int compareTo(@NonNull AcademyMember another) {
            String[] thisNameSplit = this.name.split(" ");
            String[] anotherNameSplit = another.name.split(" ");
            return thisNameSplit[thisNameSplit.length - 1].compareTo(anotherNameSplit[anotherNameSplit.length - 1]);
        }
    }
}
