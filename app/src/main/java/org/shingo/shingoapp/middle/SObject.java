package org.shingo.shingoapp.middle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the base class for all Shingo SF Objects.
 *
 * @author Dustin Homan
 */
public abstract class SObject implements Comparable<SObject> {
    protected String id;
    protected String name;

    public SObject(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object a){
        if(a instanceof SObject){
            return this.id.equals(((SObject) a).getId());
        }

        return false;
    }

    @Override
    public int compareTo(SObject a){
        return this.name.compareTo(a.getName());
    }

    public void fromJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            id = jsonObject.getString("Id");
            name = jsonObject.getString("Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
