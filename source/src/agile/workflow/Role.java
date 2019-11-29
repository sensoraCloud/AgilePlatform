/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.workflow;

import agile.json.me.JSONException;
import agile.json.me.JSONObject;

/**
 *
 * @author ruego
 */
public class Role {

    private Integer id;
    private Integer fromNodeID;
    private Integer toNodeID;
    private Integer priority;
    private Integer activity;
    
    public void unserialize(JSONObject jso) {

        try {

            this.setId(new Integer(jso.getInt("id")));
            this.setFromNodeID(new Integer(jso.getInt("fromNodeID")));
            this.setToNodeID(new Integer(jso.getInt("toNodeID")));
            this.setPriority(new Integer(jso.getInt("priority")));
            this.setActivity(new Integer(jso.getInt("activity")));
            
        } catch (JSONException ex) {
            //#debug error
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }


    }


    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromNodeID() {
        return fromNodeID;
    }

    public void setFromNodeID(Integer fromNodeID) {
        this.fromNodeID = fromNodeID;
    }

    public Integer getToNodeID() {
        return toNodeID;
    }

    public void setToNodeID(Integer toNodeID) {
        this.toNodeID = toNodeID;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }
}
