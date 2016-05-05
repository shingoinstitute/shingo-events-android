package org.shingo.shingoeventsapp.testData;

import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.shingo.shingoeventsapp.data.academymembers.AcademyMembers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to test
 * {@link AcademyMembers}
 *
 * @author Dustin Homan
 */

@LargeTest
public class AcademyMembersTest extends AcademyMembers {

    public AcademyMembersTest(){
        super(AcademyMembers.class);
    }

    @Test
    public void testToString(){
        AcademyMember testMember = new AcademyMember("1test2","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        Assert.assertEquals("toString failed!", testMember.name, testMember.toString());
    }

    @Test
    public void testEquals(){
        AcademyMember testMember1 = new AcademyMember("TEST1","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        AcademyMember testMember2 = new AcademyMember("TEST1","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        AcademyMember testMember3 = new AcademyMember("TEST3","A Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        Assert.assertEquals("equals() failed!", true, testMember1.equals(testMember2));
        Assert.assertEquals("equals() failed!", false, testMember1.equals(testMember3));
    }

    @Test
    public void testAddMember(){
        AcademyMember testMember = new AcademyMember("1test2","Test Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        AcademyMembers.addAcademyMember(testMember);
        Assert.assertEquals("Academy Member was not added to ACADEMY_MEMBERS!", testMember, ACADEMY_MEMBERS.get(0));
        Assert.assertEquals("Academy Member was not added to ACADEMY_MEMBERS_MAP!", testMember, ACADEMY_MEMBERS_MAP.get(testMember.id));
    }

    @Test
    public void testFromJSON(){
        AcademyMember test1 = new AcademyMember("TEST1","Member Name 1", "Member Title 1", "Member Organization", "HTML FORMATTED");
        AcademyMember test2 = new AcademyMember("TEST2","Member Name 2", "Member Title 2", "Member Organization", "HTML FORMATTED");
        AcademyMember test3 = new AcademyMember("TEST3","Member Name 3", "Member Title 3", "Member Organization", "HTML FORMATTED");
        HashMap<Integer, AcademyMember> testMap = new HashMap<>();
        testMap.put(1, test1);
        testMap.put(2, test2);
        testMap.put(3, test3);
        String json = "{\n" +
                "   \"success\" : true,\n" +
                "   \"academy_members\" : {\n" +
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
            AcademyMembers.fromJSON(json);
            for(int i = 1; i < 4; i++){
                Assert.assertEquals("Academy Member, Test " + i + ", is not equal!", ACADEMY_MEMBERS_MAP.get("TEST" + i), testMap.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.assertEquals("Exception was thrown, " + e.getMessage(), null, e);
        }
    }

    @Test
    public void testNeedsRefreshed(){
        AcademyMembers.clear();
        Assert.assertEquals("Needs refreshed didn't work", false, AcademyMembers.needsRefresh());
    }

    @Test
    public void testCompare(){
        AcademyMembers.clear();
        List<AcademyMember> testMembers = new ArrayList<>();
        for(int i = 0;i < 6; i++){
            String name = new String(new char[]{(char)(i + 65), (char)(i+66), ' ', 'B'});
            AcademyMember testMember = new AcademyMember("TEST" + (i + 1), name, "Testing", "Test Org", "");
            testMembers.add(testMember);
        }
        for(int i = 0;i < 6; i++){
            String name = new String(new char[]{(char)(i + 65), (char)(i+66), ' ', 'C'});
            AcademyMember testMember = new AcademyMember("TEST" + (i + 12), name, "Testing", "Test Org", "");
            testMembers.add(testMember);
        }

        for(int i = testMembers.size() - 1; i >= 0; i--){
            AcademyMembers.addAcademyMember(testMembers.get(i));
        }

        Collections.sort(ACADEMY_MEMBERS);

        Assert.assertEquals("Lists aren't the same length!", testMembers.size(), ACADEMY_MEMBERS.size());
        for(int i = 0; i < testMembers.size(); i++){
            Assert.assertEquals("ACADEMY_MEMBERS not sorted correctly at " + i, testMembers.get(i), ACADEMY_MEMBERS.get(i));
        }
    }
    
    @Test
    public void testGetPicture(){
        AcademyMember testMember = new AcademyMember("PICTURE_TEST","Test Member", "Testing", "Test Org", "<a href=\"google.com\">Google</a>");
        AcademyMembers.addAcademyMember(testMember);
        AcademyMember.getPicture("http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png",testMember.id);

        // Waiting
        while (AcademyMembers.is_loading > 0) {
        }
        Assert.assertNotNull("Picture didn't load!", AcademyMembers.ACADEMY_MEMBERS_MAP.get(testMember.id));
    }

    @After
    public void testCleanUp(){
        AcademyMembers.clear();
    }
}
