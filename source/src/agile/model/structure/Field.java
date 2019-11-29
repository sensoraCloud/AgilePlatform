/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import java.util.Hashtable;
import java.util.Vector;
import agile.json.me.JSONArray;
import agile.json.me.JSONException;
import agile.json.me.JSONObject;

/**
 *
 * @author ruego
 */
public class Field {

    private String id;
    private Integer sequence;
    private String description;
    private String typeUI;
    private Integer maxLenght;
    private Integer readSecurityLevel;  
    private Integer writeSecurityLevel;
    private Hashtable listProperties;     
    
    private boolean isindexed;
    private boolean isclustered;
    private boolean isdefaultGroupIndex;
    
    //workflow
    private boolean isflow;
    private boolean isflowDT;
    
    public void unserialize(JSONObject jso) {

        try {

            this.setId(jso.getString("id"));
            this.setDescription(jso.getString("description"));
            this.setSequence(new Integer(jso.getInt("sequence")));
            this.setTypeUI(jso.getString("typeUI"));
            this.setWriteSecurityLevel(new Integer(jso.getInt("writeSecurityLevel")));
            this.setReadSecurityLevel(new Integer(jso.getInt("readSecurityLevel")));
            this.setMaxLenght(new Integer(jso.getInt("maxLenght")));
                 
            this.setIsindexed(false);
            this.setIsclustered(false);
            
            // If the order has a listProperties associated, then unserialize it
              if (jso.has("listProperties")) {
                
                   java.util.Vector v = null;
                  
                try {
                    
                    JSONArray jsa = jso.getJSONArray("listProperties");
                                   
                    Property p=null;
                    //loop on propertys class
                    long size=jsa.length();
                    for (int j = 0; j < size; j++) {
                        
                        if (jsa.get(j)==null) continue;
                        //get property
                        p=new Property();
                        p.unserialize((JSONObject)jsa.get(j) );
                        
                        if (p.getKey().equals("index"))
                           this.setIsindexed(true);
                        
                         if (p.getKey().equals("cluster"))
                           this.setIsclustered(true);
                        
                        if (p.getKey().equals("defaultGroupIndex"))
                           this.setIsdefaultGroupIndex(true);
                        
                         if (p.getKey().equals("target"))
                           this.setIsflow(true);
                        
                          if (p.getKey().equals("targetDT"))
                           this.setIsflowDT(true);
                        
                        if (this.getListProperties().containsKey(p.getKey())) {

                            ((Vector) this.getListProperties().get(p.getKey())).addElement(p);

                        } else {

                            v = new Vector();
                            v.addElement(p);

                            this.getListProperties().put(p.getKey(), v);

                        }
                                              
                    }

                } catch (JSONException ex) {
                    //#debug error
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    //#debug error
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
                        
            
        } catch (JSONException ex) {
            //#debug error
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }


    }
    
    
     /**
     * Ritorna il valore di una specifica propietà del field
     * @param key Il nome della propietà ricercata   
     * @return Vector vettore della propietà richieste
     */
    public Vector getPropertyByKey(String key) {
        
        return (Vector)getListProperties().get(key);
    }
    
     /**
     * Notifica l'esistenza di una data propietà nel field
     * @param key Il nome della propietà da verificare   
     * @return true se la propietà esiste
     */
     public boolean hasProperty(String key) {     
     
         return getListProperties().containsKey(key);
     
     }
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeUI() {
        return typeUI;
    }

    public void setTypeUI(String typeUI) {
        this.typeUI = typeUI;
    }

    public Integer getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght(Integer maxLenght) {
        this.maxLenght = maxLenght;
    }

    public Integer getReadSecurityLevel() {
        return readSecurityLevel;
    }

    public void setReadSecurityLevel(Integer readSecurityLevel) {
        this.readSecurityLevel = readSecurityLevel;
    }

    public Integer getWriteSecurityLevel() {
        return writeSecurityLevel;
    }

    public void setWriteSecurityLevel(Integer writeSecurityLevel) {
        this.writeSecurityLevel = writeSecurityLevel;
    }

    public Hashtable getListProperties() {
        
        if (listProperties==null)
            listProperties=new Hashtable();
        
        return listProperties;
    }

    public void setListProperties(Hashtable listProperties) {
        this.listProperties = listProperties;
    }

    public boolean isIsindexed() {
        return isindexed;
    }

    public void setIsindexed(boolean isindexed) {
        this.isindexed = isindexed;
    }

    public boolean isIsclustered() {
        return isclustered;
    }

    public void setIsclustered(boolean isclustered) {
        this.isclustered = isclustered;
    }

    public boolean isIsdefaultGroupIndex() {
        return isdefaultGroupIndex;
    }

    public void setIsdefaultGroupIndex(boolean isdefaultGroupIndex) {
        this.isdefaultGroupIndex = isdefaultGroupIndex;
    }

    public boolean isIsflow() {
        return isflow;
    }

    public void setIsflow(boolean isflow) {
        this.isflow = isflow;
    }

    public boolean isIsflowDT() {
        return isflowDT;
    }

    public void setIsflowDT(boolean isflowDT) {
        this.isflowDT = isflowDT;
    }
    
   
    
    
}
