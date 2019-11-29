/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import agile.json.me.JSONObject;

/**
 *
 * @author ruego
 */
public class Property {

    public Property() {
    }
    
     public Property(String Key,Object Value) {
         key=Key;
         value=Value;
    }
    
         
    
    private String key;
    private Object value;
    
    public void unserialize(JSONObject jso) {

        try {

            this.setKey((String) jso.get("key"));
            this.setValue((Object) jso.get("value"));

        } catch (Exception ex) {
            //#debug error
            System.out.println(ex.getMessage());
            ex.printStackTrace();

        }


    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
