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
public class SystemIDsIdex {
    
    private Integer numRec;//numRec valueGroup RMS Index
    private String typeObj; 
    private Vector systemIDs;
    private Vector lastMod;
    private Vector numRecs;
    private Boolean sorted;//if indexs is sorted by value     
    
    public String serialize() {
        
        JSONObject jso = new JSONObject();
        
        try {            

            jso.put("numRec", this.getNumRec());

            jso.put("typeObj", this.getTypeObj());

            jso.put("systemIDs", this.getSystemIDs());
            
            jso.put("lastModified", this.getLastMod());
            
            jso.put("numRecs", this.getNumRecs());
            
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
            
            this.setTypeObj(jso.getString("typeObj"));
                               
            this.setSorted(new Boolean(jso.getBoolean("sorted")));
            
            JSONArray jsa = jso.getJSONArray("systemIDs");
            
            Vector systemID=new Vector();
                        
            //loop on propertys class
            long size = jsa.length();

            for (int j = 0; j < size; j++) {

                if (jsa.get(j) == null) {
                    continue;                    
                }
                
                systemID.addElement(jsa.get(j));
            }
            
            this.setSystemIDs(systemID);
            
            jsa = jso.getJSONArray("lastModified");
            
            Vector lastMods=new Vector();
                        
            //loop on propertys class
            size = jsa.length();
            for (int j = 0; j < size; j++) {

                if (jsa.get(j) == null) {
                    continue;                    
                }
                
                lastMods.addElement(jsa.get(j));
            }
            
            this.setLastMod(lastMods);           
            
            
            jsa = jso.getJSONArray("numRecs");
            
            Vector numRcs=new Vector();
                        
            //loop on propertys class
            size = jsa.length();
            for (int j = 0; j < size; j++) {

                if (jsa.get(j) == null) {
                    continue;                    
                }
                
                numRcs.addElement(jsa.get(j));
            }
            
            this.setNumRecs(numRcs); 
            
          
            
            //System.out.println("Unserialized from JSON object:\n"+this.toString());
        } catch (JSONException ex) {         
            //#debug error
            System.out.println("Could not parse JSON string ("+JSONText+") to JSON object");
            throw new Exception("Could not retrieve Customer");
        }
        
    }

    public Integer getNumRec() {
        return numRec;
    }

    public void setNumRec(Integer numRec) {
        this.numRec = numRec;
    }

    public String getTypeObj() {
        return typeObj;
    }

    public void setTypeObj(String typeObj) {
        this.typeObj = typeObj;
    }

    public Vector getSystemIDs() {
        return systemIDs;
    }

    public void setSystemIDs(Vector systemIDs) {
        this.systemIDs = systemIDs;
    }

    public Vector getLastMod() {
        return lastMod;
    }

    public void setLastMod(Vector lastMod) {
        this.lastMod = lastMod;
    }

    public Boolean getSorted() {
        return sorted;
    }

    public void setSorted(Boolean sorted) {
        this.sorted = sorted;
    }

    public Vector getNumRecs() {
        return numRecs;
    }

    public void setNumRecs(Vector numRecs) {
        this.numRecs = numRecs;
    }
}