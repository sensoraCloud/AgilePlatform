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
import agile.session.Session;

/**
 *
 * @author ruego
 */
public class DocObj implements Structure  {
    
    private String id;    
    private String iconImg;
    private String description;   
    private String typeHeadObj;
    private String typeRowObj;
    private Hashtable listProperties; //vector of Propery (key - value) 
    private String type;
    
    //index
    private Vector listIndexFields;//ids or descs of indexed fields
    private Vector listTypeUIIndexFields;//type UI of IndexFieldValue vector position sinc with getListIndexFields() 
    private String clusteredField;//id or desc of clustered field
    private String defaultGroupIndex;//default field for GroupValueObjForm
    
    //workflow
    private Integer flowID;
    private String flowField;//id of targetID field
    private String flowTargetDTField;//id or desc of targetDT field
  
     public void unserialize(JSONObject jso){
           
        try {           
           
            this.setId(jso.getString("id"));           
            this.setDescription(jso.getString("description"));
            this.setIconImg(jso.getString("iconImg"));
            this.setTypeHeadObj(jso.getString("typeHeadObj"));
            this.setTypeRowObj(jso.getString("typeRowObj"));            
            
            // If the order has a listProperties associated, then unserialize it
            if (jso.has("listProperties")) {
                
                    java.util.Vector v = null;
                    
                    JSONArray jsa = jso.getJSONArray("listProperties");
                                   
                    Property p=null;
                    //loop on propertys class
                    long size=jsa.length();
                    for (int j = 0; j <size; j++) {
                        
                        if (jsa.get(j)==null) continue;
                        //get property
                        p=new Property();
                        p.unserialize((JSONObject)jsa.get(j) );
                        
                        if (p.getKey().equals("flowID"))
                            setFlowID((Integer)p.getValue());
                        
                        if (this.getListProperties().containsKey(p.getKey())) {

                            ((Vector) this.getListProperties().get(p.getKey())).addElement(p);

                        } else {

                            v = new Vector();
                            v.addElement(p);

                            this.getListProperties().put(p.getKey(), v);

                        }
                    }

               
            }
         
            //index (only for head Obj) (eredita dal suo headobj (AnagObj))
         
            AnagObj headObj=(AnagObj)Session.getListStructureObj().get(this.getTypeHeadObj());            
                         
            this.setListIndexFields(headObj.getListIndexFields());
            this.setListTypeUIIndexFields(headObj.getListTypeUIIndexFields());
                        
            this.setClusteredField(headObj.getClusteredField());
            this.setDefaultGroupIndex(headObj.getDefaultGroupIndex());           
            this.setFlowField(headObj.getFlowField());
            this.setFlowTargetDTField(headObj.getFlowTargetDTField());
            
            
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
    
        
     public boolean hasProperty(String key) {     
     
         return getListProperties().containsKey(key);
     
     }
     
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

 

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
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
    
    
     public Vector getListIndexFields() {
        if (listIndexFields==null)
            listIndexFields=new Vector();
        return listIndexFields;
    }

    public void setListIndexFields(Vector listIndexFields) {
        this.listIndexFields = listIndexFields;
    }

    public String getClusteredField() {
        return clusteredField;
    }

    public void setClusteredField(String clusteredField) {
        this.clusteredField = clusteredField;
    }
    
  public String getDefaultGroupIndex() {
        return defaultGroupIndex;
    }

    public void setDefaultGroupIndex(String defaultGroupIndex) {
        this.defaultGroupIndex = defaultGroupIndex;
    }
        
     public Vector getListTypeUIIndexFields() { 
        if (listTypeUIIndexFields==null)
            listTypeUIIndexFields=new Vector();
        return listTypeUIIndexFields;
    }

    public void setListTypeUIIndexFields(Vector listTypeUIIndexFields) {
        this.listTypeUIIndexFields = listTypeUIIndexFields;
    }

    public String getTypeHeadObj() {
        return typeHeadObj;
    }

    public void setTypeHeadObj(String typeHeadObj) {
        this.typeHeadObj = typeHeadObj;
    }

    public String getTypeRowObj() {
        return typeRowObj;
    }

    public void setTypeRowObj(String typeRowObj) {
        this.typeRowObj = typeRowObj;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlowField() {
        return flowField;
    }

    public void setFlowField(String flowField) {
        this.flowField = flowField;
    }

    public //default field for GroupValueObjForm
    //workflow
    Integer getFlowID() {
        return flowID;
    }

    public void setFlowID(Integer flowID) {
        this.flowID = flowID;
    }

    public String getFlowTargetDTField() {
        return flowTargetDTField;
    }

    public void setFlowTargetDTField(String flowTargetDTField) {
        this.flowTargetDTField = flowTargetDTField;
    }

}
