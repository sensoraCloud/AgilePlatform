/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.workflow;

import java.util.Hashtable;
import java.util.Vector;
import agile.json.me.JSONArray;
import agile.json.me.JSONException;
import agile.json.me.JSONObject;
import agile.model.structure.Property;

/**
 *
 * @author ruego
 */
public class Node {
    
    private Integer nodeID;
    private String description;
    private Hashtable listProperties;

    
     public void unserialize(JSONObject jso) {

        try {

            this.setNodeID(new Integer(jso.getInt("id")));
            this.setDescription(jso.getString("description"));
                      
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
    
    
    
    public Integer getNodeID() {
        return nodeID;
    }

    public void setNodeID(Integer nodeID) {
        this.nodeID = nodeID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Hashtable getListProperties() {
        if (listProperties==null)
            listProperties=new Hashtable();
        return listProperties;
    }

    public void setListProperties(Hashtable listProperties) {
        this.listProperties = listProperties;
    }
    

}
