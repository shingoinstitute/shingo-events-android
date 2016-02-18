package org.shingo.shingoeventsapp.data.recipients;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustinehoman on 2/18/16.
 */
public class Recipients {

    public static List<AwardRecipient> AWARD_RECIPIENTS = new ArrayList<>();
    public static Map<String, AwardRecipient> AWARD_RECIPIENT_MAP = new HashMap<>();

    public static List<ResearchRecipient> RESEARCH_RECIPIENTS = new ArrayList<>();
    public static Map<String,ResearchRecipient> RESEARCH_RECIPIENT_MAP = new HashMap<>();

    public static void addAwardRecipient(AwardRecipient awardRecipient){
        if(AWARD_RECIPIENT_MAP.get(awardRecipient.id) == null){
            AWARD_RECIPIENTS.add(awardRecipient);
            AWARD_RECIPIENT_MAP.put(awardRecipient.id, awardRecipient);
        }
    }

    public static void addResearchRecipient(ResearchRecipient researchRecipient){
        if(RESEARCH_RECIPIENT_MAP.get(researchRecipient.id) == null){
            RESEARCH_RECIPIENTS.add(researchRecipient);
            RESEARCH_RECIPIENT_MAP.put(researchRecipient.id, researchRecipient);
        }
    }

    public static void clear(){
        AWARD_RECIPIENT_MAP.clear();
        AWARD_RECIPIENTS.clear();
        RESEARCH_RECIPIENT_MAP.clear();
        RESEARCH_RECIPIENTS.clear();
    }

    public static class AwardRecipient implements Comparable<AwardRecipient> {
        public String id;
        public String name;
        public String Abstract;
        public String award;
        public Bitmap logo;

        public AwardRecipient(String id, String name, String Abstract, String award, Bitmap logo)
        {
            this.id = id;
            this.name = name;
            this.Abstract = Abstract;
            this.award = award;
            this.logo = logo;
        }

        @Override
        public int compareTo(AwardRecipient another) {
            if(this.award.equals(another.award)) return this.name.compareTo(another.name);
            if(this.award.equals("Shingo Prize")) return 1;
            if(this.award.equals("Silver Medallion") && another.equals("Bronze Medallion")) return 1;

            return -1;
        }
    }

    public static class ResearchRecipient implements Comparable<ResearchRecipient> {
        public String id;
        public String book;
        public String Abstract;
        public String author;
        public Bitmap cover;

        public ResearchRecipient(String id, String book, String Abstract, String author, Bitmap cover){
            this.id = id;
            this.book = book;
            this.Abstract = Abstract;
            this.author = author;
            this.cover = cover;
        }

        @Override
        public int compareTo(ResearchRecipient another) {
            return this.book.compareTo(another.book);
        }
    }
}
