package org.shingo.shingoeventsapp.testData;

import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.shingo.shingoeventsapp.data.boardmembers.BoardMembers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to test
 * {@link BoardMembers}
 *
 * @author Dustin Homan
 */

@LargeTest
public class BoardMembersTest extends BoardMembers {

    public BoardMembersTest(){
        super(BoardMembers.class);
    }

    @Test
    public void testToString(){
        BoardMember testMember = new BoardMember("1test2","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        Assert.assertEquals("toString failed!", testMember.name, testMember.toString());
    }

    @Test
    public void testEquals(){
        BoardMember testMember1 = new BoardMember("TEST1","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        BoardMember testMember2 = new BoardMember("TEST1","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        BoardMember testMember3 = new BoardMember("TEST3","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        Assert.assertEquals("equals() failed!", true, testMember1.equals(testMember2));
        Assert.assertEquals("equals() failed!", false, testMember1.equals(testMember3));
    }

    @Test
    public void testAddMember(){
        BoardMember testMember = new BoardMember("1test2","Test Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        BoardMembers.addBoardMember(testMember);
        Assert.assertEquals("Academy Member was not added to BOARD_MEMBERS!", testMember, BOARD_MEMBERS.get(0));
        Assert.assertEquals("Academy Member was not added to BOARD_MEMBERS_MAP!", testMember, BOARD_MEMBERS_MAP.get(testMember.id));
    }

    @Test
    public void testFromJSON(){
        BoardMember test1 = new BoardMember("TEST1","Member Name 1", "Member Title 1", "Member Organization", "HTML FORMATTED");
        BoardMember test2 = new BoardMember("TEST2","Member Name 2", "Member Title 2", "Member Organization", "HTML FORMATTED");
        BoardMember test3 = new BoardMember("TEST3","Member Name 3", "Member Title 3", "Member Organization", "HTML FORMATTED");
        HashMap<Integer, BoardMember> testMap = new HashMap<>();
        testMap.put(1, test1);
        testMap.put(2, test2);
        testMap.put(3, test3);
        String json = "{\n" +
                "   \"success\" : true,\n" +
                "   \"board_members\" : {\n" +
                "      \"size\" : Integer,\n" +
                "      \"records\" : [\n" +
                "         {\n" +
                "            \"Photograph__c\" : \"http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png\",\n" +
                "            \"Title\" : \"Member Title 1\",\n" +
                "            \"Biography__c\" : \"HTML FORMATTED\",\n" +
                "            \"Name\" : \"Member Name 1\",\n" +
                "            \"Id\" : \"TEST1\",\n" +
                "            \"Organization\" : \"Member Organization\"\n" +
                "         },\n" +
                "         {\n" +
                "            \"Photograph__c\" : \"http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png\",\n" +
                "            \"Title\" : \"Member Title 2\",\n" +
                "            \"Biography__c\" : \"HTML FORMATTED\",\n" +
                "            \"Name\" : \"Member Name 2\",\n" +
                "            \"Id\" : \"TEST2\",\n" +
                "            \"Organization\" : \"Member Organization\"\n" +
                "         },\n" +
                "         {\n" +
                "            \"Photograph__c\" : \"http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png\",\n" +
                "            \"Title\" : \"Member Title 3\",\n" +
                "            \"Biography__c\" : \"HTML FORMATTED\",\n" +
                "            \"Name\" : \"Member Name 3\",\n" +
                "            \"Id\" : \"TEST3\",\n" +
                "            \"Organization\" : \"Member Organization\"\n" +
                "         }\n" +
                "      ]\n" +
                "   }\n" +
                "}";

        try {
            BoardMembers.fromJSON(json);
            for(int i = 1; i < 4; i++){
                Assert.assertEquals("Academy Member, Test " + i + ", is not equal!", BOARD_MEMBERS_MAP.get("TEST" + i), testMap.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.assertEquals("Exception was thrown, " + e.getMessage(), null, e);
        }
    }

    @Test
    public void testNeedsRefreshed(){
        BoardMembers.clear();
        Assert.assertEquals("Needs refreshed didn't work", false, BoardMembers.needsRefresh());
    }

    @Test
    public void testCompare(){
        BoardMembers.clear();
        List<BoardMember> testMembers = new ArrayList<>();
        for(int i = 0;i < 6; i++){
            String name = new String(new char[]{(char)(i + 65), (char)(i+66), ' ', 'B'});
            BoardMember testMember = new BoardMember("TEST" + (i + 1), name, "Testing", "Test Org", "");
            testMembers.add(testMember);
        }
        for(int i = 0;i < 6; i++){
            String name = new String(new char[]{(char)(i + 65), (char)(i+66), ' ', 'C'});
            BoardMember testMember = new BoardMember("TEST" + (i + 12), name, "Testing", "Test Org", "");
            testMembers.add(testMember);
        }

        for(int i = testMembers.size() - 1; i >= 0; i--){
            BoardMembers.addBoardMember(testMembers.get(i));
        }

        Collections.sort(BOARD_MEMBERS);

        Assert.assertEquals("Lists aren't the same length!", testMembers.size(), BOARD_MEMBERS.size());
        for(int i = 0; i < testMembers.size(); i++){
            Assert.assertEquals("BOARD_MEMBERS not sorted correctly at " + i, testMembers.get(i), BOARD_MEMBERS.get(i));
        }
    }
    
    @Test
    public void testGetPicture(){
        BoardMember testMember = new BoardMember("PICTURE_TEST","Test Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        BoardMembers.addBoardMember(testMember);
        BoardMember.getPicture("http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png",testMember.id);

        // Waiting
        while (BoardMembers.is_loading > 0) {
        }
        Assert.assertNotNull("Picture didn't load!", BoardMembers.BOARD_MEMBERS_MAP.get(testMember.id));
    }

    @After
    public void testCleanUp(){
        BoardMembers.clear();
    }
}
