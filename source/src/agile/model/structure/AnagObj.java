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
public class AnagObj implements Structure {
    
    private String id;
    private String description;
    private String iconImg;   
    private Integer readSecurityLevel;  
    private Integer writeSecurityLevel;
    private Hashtable listProperties; //vector of Propery (key - value) 
    private Vector listFields;    
    private String type;
    
    //index
    private Vector listIndexFields;//ids or descs of indexed fields
    private Vector listTypeUIIndexFields;//type UI of IndexFieldValue vector position sinc with getListIndexFields() 
    private String clusteredField;//id or desc of clustered field
    private String defaultGroupIndex;//default field for GroupValueObjForm
    private boolean isChild;
    private Field primaryKeyField;
    
     //workflow
    private String flowField;//id or desc of targetID field
    private String flowTargetDTField;//id or desc of targetDT field

    //for optimization of trscode fieldid to description
    private Hashtable listFieldByID;

    //for optimization of trscode description to fieldid
    private Hashtable listFieldByDescription;
    
    
     public void unserialize(JSONObject jso) {
           
        try {           
           
            this.setId(jso.getString("id"));           
            this.setDescription(jso.getString("description"));
            this.setIconImg(jso.getString("iconImg"));
            this.setWriteSecurityLevel(new Integer(jso.getInt("writeSecurityLevel")));
            this.setReadSecurityLevel(new Integer(jso.getInt("readSecurityLevel")));                      
            
            this.setIsChild(false);
            
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
                        
                        if (p.getKey().equals("isChild")){
                        
                            if (((Boolean)p.getValue()).booleanValue())
                                 this.setIsChild(true);
                            else
                                 this.setIsChild(false);
                        
                        }
                        
                         if (this.getListProperties().containsKey(p.getKey())) {

                            ((Vector) this.getListProperties().get(p.getKey())).addElement(p);

                        } else {

                            v = new Vector();
                            v.addElement(p);

                            this.getListProperties().put(p.getKey(), v);

                        }
                    }

               
            }

           // If the order has a listFields associated, then unserialize it
            if (jso.has("listFields")) {
                    
                    JSONArray jsa = jso.getJSONArray("listFields");
                    JSONObject jsf = null;
                    String key=null;
                    String value=null;
                    Field f=null;
                    //loop on fields 
                    long size=jsa.length();
                    for (int j = 0; j < size; j++) {
                        
                        if (jsa.get(j)==null) continue;
                        //get field
                        f=new Field();

                        f.unserialize((JSONObject) jsa.get(j));

                        if (f.hasProperty("primaryKey")) {
                            setPrimaryKeyField(f);
                        }

                        if (f.isIsindexed()) {
                            this.getListIndexFields().addElement(f.getId());
                            this.getListTypeUIIndexFields().addElement(f.getTypeUI());
                        }
                        
                        if (f.isIsclustered()) {
                            this.setClusteredField(f.getId());
                        }                        
                        if (f.isIsdefaultGroupIndex()) {
                            this.setDefaultGroupIndex(f.getId());
                        }
                         if (f.isIsflow()) {
                            this.setFlowField(f.getId());
                        }
                         if (f.isIsflowDT()) {
                            this.setFlowTargetDTField(f.getId());
                        }
                        
                        //add in list
                        this.getListFields().addElement(f);

                        this.getListFieldByID().put(f.getId(), f);

                        this.getListFieldByDescription().put(f.getDescription(),f);
                        
                    }

               
            }
            
        } catch (JSONException ex) {
            //#debug error
            System.out.println(ex.getMessage()+ "Classes: " + this.getDescription());
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

   
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector getListFields() {
        
          if (listFields==null)
            listFields= new Vector();
        
        return listFields;
    }

    public void setListFields(Vector listFields) {
        this.listFields = listFields;
    }

    public Hashtable getListProperties() {
        
        if (listProperties==null)
            listProperties= new Hashtable();
        
        return listProperties;
    }

    public void setListProperties(Hashtable listProperties) {
        this.listProperties = listProperties;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
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

    public boolean isChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
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

    public String getFlowTargetDTField() {
        return flowTargetDTField;
    }

    public void setFlowTargetDTField(String flowTargetDTField) {
        this.flowTargetDTField = flowTargetDTField;
    }

    /**
     * @return the listFieldByID
     */
    public Hashtable getListFieldByID() {
        if (listFieldByID==null)
            listFieldByID=new Hashtable();
        return listFieldByID;
    }

    /**
     * @param listFieldByID the listFieldByID to set
     */
    public void setListFieldByID(Hashtable listFieldByID) {
        this.listFieldByID = listFieldByID;
    }

    /**
     * @return the listFieldByDescription
     */
    public Hashtable getListFieldByDescription() {
         if (listFieldByDescription==null)
            listFieldByDescription=new Hashtable();
        return listFieldByDescription;
    }

    /**
     * @param listFieldByDescription the listFieldByDescription to set
     */
    public void setListFieldByDescription(Hashtable listFieldByDescription) {
        this.listFieldByDescription = listFieldByDescription;
    }

    /**
     * @return the primaryKeyField
     */
    public Field getPrimaryKeyField() {
        return primaryKeyField;
    }

    /**
     * @param primaryKeyField the primaryKeyFieldID to set
     */
    public void setPrimaryKeyField(Field primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
    }
    
       
    
}
