/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import java.util.Vector;
import agile.json.me.JSONArray;
import agile.json.me.JSONException;
import agile.json.me.JSONObject;

/**
 *
 * @author ruego
 */
public class Index implements Storeable {
    
    private Integer numRec;//numRec valueGroup RMS Index
    private String value; //valueGroup RMS Index   
    private Vector indexs;//numsRec RMS obj in a Groupvalue
    private Boolean sorted;//if indexs is sorted by title (for Searchable obj)
    
    
    public String serialize() {
        
        JSONObject jso = new JSONObject();
        
        try {            

            jso.put("numRec", this.getNumRec());

            jso.put("value", this.getValue());

            jso.put("indexs", this.getIndexs());
            
            jso.put("sorted", this.getSorted());
            
        } catch (JSONException ex) {
             //#debug error
            System.out.println(ex.getMessage());          
            return null;

        }
        return jso.toString();
    }
    
      public String serializeToServer() {
      
          return this.serialize();
          
      }

    public void unserialize(String JSONText) throws Exception {
        // System.out.println("Unserializing customer "+JSONText);
        try {
                                  
            JSONObject jso=new JSONObject(JSONText);           
         
            this.setNumRec(new Integer(jso.getInt("numRec")));            
            
            this.setValue(jso.getString("value"));
                      
            JSONArray jsa = jso.getJSONArray("indexs");
            
            Vector indxs=new Vector();
                        
            //loop on propertys class
            long size = jsa.length();
            for (int j = 0; j < size; j++) {

                if (jsa.get(j) == null) {
                    continue;                    
                }
                
                indxs.addElement(jsa.get(j));
            }
            
            this.setIndexs(indxs);
            
            this.setSorted(new Boolean(jso.getBoolean("sorted")));
            
            //System.out.println("Unserialized from JSON object:\n"+this.toString());
        } catch (JSONException ex) {         
            //#debug error
            System.out.println("Could not parse JSON string ("+JSONText+") to JSON object");
            throw new Exception("Could not retrieve Customer");
        }
        
    }
    
    public String toString(){
        return serialize();
    }
       
   
    public Vector getIndexs() {
        return indexs;
    }

    public void setIndexs(Vector indexs) {
        this.indexs = indexs;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getNumRec() {
        return numRec;
    }

    public void setNumRec(Integer numRec) {
        this.numRec = numRec;
    }

    public Boolean getSorted() {
        return sorted;
    }

    public void setSorted(Boolean sorted) {
        this.sorted = sorted;
    }
    
    
    
}
