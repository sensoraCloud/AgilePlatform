/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import agile.json.me.JSONException;
import agile.json.me.JSONObject;
import agile.session.Session;
import agile.util.Date_Util;

/**
 *
 * @author ruego
 */
public class AnagBean implements  Searchable,Storeable,Indexable,Linkable,Synchronized {

    //Fields
    private Hashtable fieldsValue;//Hashtable of field values 
    
    //Index
    private Vector listIndexFields;
    private IndexFieldValue clusteredField;
    private String defaultGroupIndex;//default field for GroupValueObjForm
    private boolean isChild;
    
    //link
    private String refLinkID;
    
    //title
    private String title;    
    
    //sinc server
    private Integer systemID=null;
    //private Long lastMod=null;
    
     public AnagBean(String type){ 
         
        fieldsValue=new Hashtable(); 
        fieldsValue.put("type", type);
        //this.setLastMod(Date_Util.getStampFromDate(new Date()));
           
     }
     
     public AnagBean(){ 
           
     }
     
     
      public String getSourceType(){
      
//           if (this.getFieldsValue().containsKey("sourceType"))
//               return ((String)this.getFieldsValue().get("sourceType"));
//        else {
//             //#debug error
//            System.out.println("Missing getSourceType()");
//            return null;
//        }

          return getFieldValueString("sourceType");
      }
     
       public Integer getSourceNumRec(){
      
//           if (this.getFieldsValue().containsKey("sourceNumRec"))
//               return ((Integer)this.getFieldsValue().get("sourceNumRec"));
//        else {
//             //#debug error
//            System.out.println("Missing getSourceNumRec()");
//            return null;
//        }

            return (Integer)getFieldValue("sourceNumRec");
      }
       
      public Integer getNumRec(){
      
//           if (this.getFieldsValue().containsKey("numRec"))
//               return ((Integer)this.getFieldsValue().get("numRec"));
//        else {
//             //#debug error
//            System.out.println("Missing getNumRec()");
//            return null;
//        }
            return (Integer)getFieldValue("numRec");
      
      }
     
          
     public void makeTitle(){
                  
        //search filed with propery "printSmallDesc"
        Vector flds=((AnagObj)Session.getListStructureObj().get(getFieldsValue().get("type"))).getListFields();
         
        Field fld=null;
        String value=null;
        
        long size = flds.size();
        
         setTitle("");
        
        for (int j = 0; j < size; j++) {

            fld = (Field) flds.elementAt(j);

            value=(getFieldValueString(fld.getId()))==null?"":getFieldValueString(fld.getId());

            if (!value.equals("")) {

                if (fld.hasProperty("printSmallDesc")) {

                    if (fld.getTypeUI().equals("DATE")) {
                        setTitle((title == null || title.equals("")) ? Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(value)), Date_Util.dmyHHmmss, false) : title + " " + Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(value)), Date_Util.dmyHHmmss, false));
                    } else {
                        setTitle((title == null || title.equals("")) ? new String(value) : title + " " + value);
                    }
                }
            }
            
        }
                    
     }
     
   //index  
    public IndexFieldValue getClusteredField() {
        if (clusteredField==null)
            clusteredField=new IndexFieldValue();
        return clusteredField;
    }

    public void setClusteredField(IndexFieldValue clusteredField) {
        this.clusteredField = clusteredField;
    }

    public void setListIndexFields(Vector listIndexFields) {
        this.listIndexFields = listIndexFields;
    }
    
    public Vector getListIndexFields() {
        
      if (listIndexFields==null)
          listIndexFields=new Vector();
      
        return listIndexFields;
    }

    public void updateIndexInfo() {
    
        //index fields
        Vector indexFields=((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).getListIndexFields();
        
        int size=indexFields.size();
        
        getListIndexFields().removeAllElements();
        IndexFieldValue indexFld=null;
        
         for (int j = 0; j < size; j++) {

             indexFld=new IndexFieldValue();
             
             indexFld.setFieldID( (String) indexFields.elementAt(j) ); //field id
             indexFld.setFieldValue(this.getFieldValue((String) indexFields.elementAt(j))); //field value
             
             getListIndexFields().addElement(indexFld);
          
        }        
        
        //clusterd field
        if ((((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).getClusteredField())!=null)
        {
            indexFld=new IndexFieldValue();
            
            indexFld.setFieldID(((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).getClusteredField());
            indexFld.setFieldValue(this.getFieldValue((String)((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).getClusteredField()));
            
            this.setClusteredField(indexFld);
        }
        
        //defaultGroupIndex field
        if ((((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).getDefaultGroupIndex())!=null)
        {
            this.setDefaultGroupIndex((String)((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).getDefaultGroupIndex());
        }
        
        //isChild
        if (((AnagObj) Session.getListStructureObj().get(this.getFieldValue("type"))).isChild()) {
            this.setIsChild(true);
        } else {
            this.setIsChild(false);}
        
        
    }
     
      public String getType(){
      
          return (String)getFieldValue("type");
      
      }
    
    
    //index end
    
    
     public String getTitle() {

         return title;

    }
     
     public void setField(String fieldID,Object value){
     
         this.getFieldsValue().put(fieldID, value);
     
     }
     
     public Object getFieldValue(String fieldID) {

        if (this.getFieldsValue().containsKey(fieldID)) {
            return (Object) this.getFieldsValue().get(fieldID);
        } else {
            return null;
        }
    }

      public String getFieldValueString(String fieldID) {

          if (this.getFieldsValue().containsKey(fieldID)) {

              //String valueStr=null;

              Object value = (Object) this.getFieldsValue().get(fieldID);

              if (value instanceof String) {
                  value = (String) value;
              } else if (value instanceof Integer) {
                  value = String.valueOf(((Integer) value).intValue());
              } else if (value instanceof Double) {
                  value = String.valueOf(((Double) value).doubleValue());
              } else if (value instanceof Long) {
                  value = String.valueOf(((Long) value).longValue());
              } else if (value instanceof Boolean) {
                  value = String.valueOf(((Boolean) value).booleanValue());
              }


//              String typeField = ((Field) ((AnagObj) Session.getListStructureObj().get((String) getFieldsValue().get("type"))).getListFieldByID().get(fieldID)).getTypeUI();
//
//              if (typeField.equals("ANY")) {
//                  valueStr = (String)value;
//              } else if (typeField.equals("NUMERIC")) {
//                   valueStr = String.valueOf(((Long)value).longValue()) ;
//              } else if (typeField.equals("PASSWORD")) {
//                   valueStr = value.toString();
//              } else if (typeField.equals("PHONENUMBER")) {
//                   valueStr = String.valueOf(((Long)value).longValue()) ;
//              } else if (typeField.equals("UNEDITABLE")) {
//                  valueStr = (String)value;
//              } else if (typeField.equals("EMAILADDR")) {
//                  valueStr = (String)value;
//              } else if (typeField.equals("DECIMAL")) {
//                    valueStr = String.valueOf(((Double)value).doubleValue()) ;
//              } .. DATE ..


              return (String)value;

          } else {
              return null;
          }
    }

    
    public boolean hasField(String key) {
        return getFieldsValue().containsKey(key);
    }
   
//     public boolean hasFieldWithProperty(String keyProperty) {         
//        
//    }
//       
     

    
    public String toString(){
        return serialize();
    }
    
   
    public Hashtable getFieldsValue() {
        
        if (fieldsValue==null)
            fieldsValue=new Hashtable();
        
        return fieldsValue;
    }

    public void setFieldsValue(Hashtable FieldsValue) {
        this.fieldsValue = FieldsValue;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultGroupIndex() {
        return defaultGroupIndex;
    }

    public void setDefaultGroupIndex(String defaultGroupIndex) {
        this.defaultGroupIndex = defaultGroupIndex;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
    }

    public String serialize() {        
        //make title
        this.makeTitle();
        
        if (getTitle() != null) {
            this.getFieldsValue().put("title", getTitle());
        }
        if (getRefLinkID() != null) {
            this.getFieldsValue().put("refLinkID", getRefLinkID());
        }
        if (getSystemID() != null) {
            this.getFieldsValue().put("sysID", getSystemID());
        }
        
        JSONObject jso = new JSONObject(this.getFieldsValue());
        
        return jso.toString();
    }

    public String serializeToServer() {
        
        JSONObject jso = null;
        
        try {

            jso = new JSONObject(this.getFieldsValue());

            jso.put("sysID", this.getSystemID() == null ? new Integer(0) : this.getSystemID());
       
            //jso.put("lastModified", this.getLastMod() == null ? new Long(0) : this.getLastMod());
       
            
        } catch (JSONException ex) {
            //#debug error
            System.out.println("Exception " + ex.getMessage()==null?"Null":ex.getMessage());
            return null;
        }
      
        return jso.toString();

    }
    
    
    public void unserialize(String JSONText) {
     
        try {
                      
            JSONObject jso=new JSONObject(JSONText);
           
             //delete all fields value
            getFieldsValue().clear();
            
            listIndexFields=null;
            clusteredField=null;
            title=null;
            
            Enumeration en=jso.keys();

            while(en.hasMoreElements()){

                String key=(String)en.nextElement();

                if (!key.equals("title"))
                    this.getFieldsValue().put(key,jso.get(key));

            }
            
            if (jso.has("title"))
                this.setTitle(jso.getString("title"));     
            
             if (jso.has("sysID"))
                this.setSystemID(new Integer(jso.getInt("sysID")));
            
            
            //System.out.println("Unserialized from JSON object:\n"+this.toString());
        } catch (JSONException ex) {         
            //#debug error
            System.out.println("Could not parse JSON string ("+JSONText+") to JSON object");
           
        }
        
    }
    
     public boolean isSync(){
     
        return (getSystemID()==null || getSystemID().intValue()==0) ?false : true;
     
     }
     

    public String getRefLinkID() {
        return refLinkID;
    }

    public void setRefLinkID(String refLinkID) {
        this.refLinkID = refLinkID;
    }

    public Integer getSystemID() {
        return systemID;
    }

    public void setSystemID(Integer systemID) {
        this.systemID = systemID;
    }

    public Long getLastMod() {

         Object value = (Object) this.getFieldsValue().get("lastModified");
         Long longValue=null;

              if (value instanceof Integer) {
                  longValue = new Long(((Integer) value).longValue());
              } else if (value instanceof Long) {
                  longValue = (Long) value;
              } else if (value instanceof String) {
                  longValue =  new Long( Long.parseLong((String) value));
              }

        return  longValue;


    }

//    public void setLastMod(Long lastMod) {
//        this.lastMod = lastMod;
//    }
    

    
    
}
