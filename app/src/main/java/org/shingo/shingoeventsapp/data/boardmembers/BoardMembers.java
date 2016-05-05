package org.shingo.shingoeventsapp.data.boardmembers;

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
 * data pertinent to board members
 * received from the API. The lists
 * are static to reduce calls to the API.
 *
 * @author Dustin Homan
 */
public class BoardMembers {
    /**
     * Holds {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}s
     */
    public static List<BoardMember> BOARD_MEMBERS = new ArrayList<>();

    /**
     * Map to {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}s.
     * The key is {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember#id}
     */
    public static Map<String, BoardMember> BOARD_MEMBERS_MAP = new HashMap<>();

    /**
     * {@link Date} when data was last pulled from API
     */
    public static Date refresh;

    /**
     * If >0 data is still loading
     */
    public static int is_loading = 0;

    public BoardMembers(Class<BoardMembers> boardMembersClass) {
        
    }

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
     * Adds an {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember} to both {@link BoardMembers#BOARD_MEMBERS}
     * and {@link BoardMembers#BOARD_MEMBERS_MAP} if the map does not contain
     * the passed in {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}.
     *
     * @param boardMember an {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}
     */
    public static void addBoardMember(BoardMember boardMember){
        if(BOARD_MEMBERS_MAP.get(boardMember.id) == null){
            BOARD_MEMBERS.add(boardMember);
            BOARD_MEMBERS_MAP.put(boardMember.id, boardMember);
        }
    }

    /**
     * Used to clear {@link BoardMembers#BOARD_MEMBERS} and
     * {@link BoardMembers#BOARD_MEMBERS_MAP} in prep for an
     * API call.
     */
    public static void clear(){
        BOARD_MEMBERS.clear();
        BOARD_MEMBERS_MAP.clear();
        refresh = new Date();
    }


    /**
     * Parse {@link BoardMembers} from a JSON string
     * @param json JSON string representing {@link BoardMembers}
     * @throws JSONException
     */
    public static void fromJSON(String json) throws JSONException{
        JSONObject response = new JSONObject(json);
        clear();
        JSONArray jBoardMembers = response.getJSONObject("board_members").getJSONArray("records");
        for (int i = 0; i < jBoardMembers.length(); i++)
            addBoardMember(BoardMember.fromJSON(jBoardMembers.getJSONObject(i)));
    }

    /**
     * Holder class for Board Member data.
     */
    public static class BoardMember implements Comparable<BoardMember>{
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
         * @param id the SF id of the Board Members
         * @param name the member's name
         * @param title the member's title
         * @param org the member's organization
         * @param bio the member's biography
         */
        public BoardMember(String id, String name, String title, String org, String bio){
            this.id = id;
            this.name = name;
            this.title = title;
            this.org = org;
            this.bio = bio;
            if(bio.equals("null")) this.bio = "Bio coming soon!";
        }

        /**
         * Used to parse an {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember} from a {@link JSONObject}
         * @param jBoardMember {@link JSONObject} representing an {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}
         * @return {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}
         * @throws JSONException
         */
        public static BoardMember fromJSON(JSONObject jBoardMember) throws JSONException{
            if(!jBoardMember.getString("Photograph__c").equals("null"))
                getPicture(jBoardMember.getString("Photograph__c"), jBoardMember.getString("Id"));

            return new BoardMember(jBoardMember.getString("Id"),
                    jBoardMember.getString("Name"), jBoardMember.getString("Title"),
                    jBoardMember.getString("Organization"), jBoardMember.getString("Biography__c"));
        }


        /**
         * Get the {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}'s Picture
         * @param url a url({@link String}) to the {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember}'s picture
         * @param id the {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember#id}
         */
        public static void getPicture(final String url, final String id){
            is_loading++;
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL image = new URL(url);
                        Bitmap picture = BitmapFactory.decodeStream(image.openConnection().getInputStream());
                        if(BOARD_MEMBERS_MAP.containsKey(id)) BOARD_MEMBERS_MAP.get(id).picture = picture;
                        is_loading--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        /**
         * Returns a round picture from {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember#picture}
         * @param context Context of the calling {@link android.app.Activity}
         * @return a {@link Bitmap} of a circle with radius 250px of {@link org.shingo.shingoeventsapp.data.boardmembers.BoardMembers.BoardMember#picture}
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
        public int compareTo(@NonNull BoardMember another) {
            String[] thisNameSplit = this.name.split(" ");
            String[] anotherNameSplit = another.name.split(" ");
            // Compare last names
            //int lastNameCompare = anotherNameSplit[anotherNameSplit.length - 1].compareTo(thisNameSplit[thisNameSplit.length - 1]);
            int lastNameCompare = thisNameSplit[thisNameSplit.length - 1].compareTo(anotherNameSplit[anotherNameSplit.length - 1]);
            if(lastNameCompare != 0){
                return lastNameCompare;
            } else {
                // Compare first names
                return thisNameSplit[0].compareTo(anotherNameSplit[0]);
            }
        }

        @Override
        public boolean equals(Object another){
            if(another instanceof BoardMember){
                return ((BoardMember) another).id.equals(this.id) && ((BoardMember) another).name.equals(this.name)
                        && ((BoardMember) another).title.equals(this.title) && ((BoardMember) another).org.equals(this.org)
                        && ((BoardMember) another).bio.equals(this.bio);
            }
            return false;
        }
    }
}
