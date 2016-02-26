package org.shingo.shingoeventsapp.data.speakers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 1/8/16.
 */
public class Speakers {

    public static List<Speaker> SPEAKERS = new ArrayList<>();

    public static Map<String, Speaker> SPEAKER_MAP = new HashMap<>();

    public static Date refresh;

    public static boolean needsRefresh(){
        if(refresh == null) return true;
        Date now = new Date();
        return now.after(new Date(refresh.getTime() + (20 * 60000)));
    }

    public static void addSpeaker(Speaker speaker){
        if(SPEAKER_MAP.get(speaker.id) == null){
            SPEAKERS.add(speaker);
            SPEAKER_MAP.put(speaker.id, speaker);
        }
    }

    public static void clear(){
        SPEAKERS.clear();
        SPEAKER_MAP.clear();
        refresh = new Date();
    }

    public static class Speaker implements Comparable<Speaker>{
        public String id;
        public String name;
        public String displayName;
        public String title;
        public String company;
        public String bio;
        public Bitmap picture;

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

        public Bitmap getRoundPicture(){
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
            canvas.drawBitmap(picture, new Rect(0,0,picture.getWidth(), picture.getHeight()),
                    new Rect(0,0, targetWidth, targetHeight), null);
            return target;
        }

        @Override
        public String toString() { return displayName; }

        @Override
        public int compareTo(Speaker another) {
            return this.name.split(" ")[1].compareTo(another.name.split(" ")[1]);
        }
    }
}
