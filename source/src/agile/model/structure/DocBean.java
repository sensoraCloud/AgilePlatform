/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import de.enough.polish.util.TextUtil;
import java.util.Date;
import java.util.Vector;
import agile.json.me.JSONArray;
import agile.json.me.JSONException;
import agile.json.me.JSONObject;
import agile.session.Session;
import agile.util.Date_Util;
import agile.util.String_Util;


/**
 *
 * @author ruego
 */
public class DocBean implements Searchable,Storeable,Indexable,Synchronized,Linkable{
    
    private String id;//document id
    private String type;
    private Integer numRec;
    private String title;
    //private String listPriceID;
        

    private String typeHeadObj;
    private String typeRowObj;
    private AnagBean headObj;//object anagBean associated to document(custumer,supplyer)
    private Vector rows=new Vector();//vector of  rowObj anagBean 
    private Long lastMod;//date of the last modified
    
    //index
    private Vector listIndexFields;
    private IndexFieldValue clusteredField;
    private String defaultGroupIndex;//default field for GroupValueObjForm
    
     //link
    private String refLinkID;
    
     //sinc server
    private Integer systemID=null;
    
    public DocBean(String type,AnagBean headObj)  {
      
        setType(type);         
        setLastMod(Date_Util.getStampFromDate(new Date()));
        setHeadObj(headObj);
        setTypeHeadObj(((DocObj) Session.getListStructureObj().get(this.getType())).getTypeHeadObj());
        setTypeRowObj(((DocObj) Session.getListStructureObj().get(this.getType())).getTypeRowObj());   
    
           
    }
    
    public DocBean(){
    
    }
    
     
    public String getSourceType() {

//           if (this.getFieldsValue().containsKey("sourceType"))
//               return ((String)this.getFieldsValue().get("sourceType"));
//        else {
//             //#debug error
//            System.out.println("Missing getSourceType()");
//            return null;
//        }
        return null;
    }

    public Integer getSourceNumRec() {

//           if (this.getFieldsValue().containsKey("sourceNumRec"))
//               return ((Integer)this.getFieldsValue().get("sourceNumRec"));
//        else {
//             //#debug error
//            System.out.println("Missing getSourceNumRec()");
//            return null;
        // }
        return null;
    }

    public boolean isChild() {

        return false;

    }    
    
      public Integer getNumRec(){     
         
         return numRec;      
      }
     
    
    public void makeTitle() {

        //search filed with propery "printSmallDesc"
        Vector flds = ((AnagObj) Session.getListStructureObj().get(getHeadObj().getType())).getListFields();

        Field fld = null;
        String value=null;
        
        setTitle("");
        
        long size = flds.size();
        for (int j = 0; j < size; j++) {

            fld = (Field) flds.elementAt(j);
            
            if (fld.hasProperty("printSmallDesc")) {
        
                value=((String)getHeadObj().getFieldValueString(fld.getId()))==null?"":(String)getHeadObj().getFieldValueString(fld.getId());
                
                if (!value.equals("")) {

                    if (fld.getTypeUI().equals("DATE")) {
                        setTitle((title == null || title.equals("")) ? Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(value)), Date_Util.dmyHHmmss, false) : title + " " + Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(value)), Date_Util.dmyHHmmss, false));
                    } else {
                        setTitle((title == null || title.equals("")) ? value : title + " " + value);
                    }

                }

               
            }
        }

    }
    
    public void doInfoOperation() {

        //get "infoOperation" properties
        Vector infoProp = ((DocObj) Session.getListStructureObj().get(this.getType())).getPropertyByKey("infoOperation");

        if (infoProp != null) {


            Property pr = null;
            int index = 0;
            String valorizedField = null;
            String valueFields = null;
            String nameMetod = null;
            String parameters = null;
                      
            long size = infoProp.size();

            for (int j = 0; j < size; j++) {

                pr = (Property) infoProp.elementAt(j);

                //Totale=Sum(TotPrezzo)
                index = ((String) pr.getValue()).indexOf("=");
                //Totale
                valorizedField = ((String) pr.getValue()).substring(0, index);
                //Sum(TotPrezzo)
                valueFields = ((String) pr.getValue()).substring(index + 1);
                index = valueFields.indexOf("(");
                //Sum
                nameMetod = valueFields.substring(0, index);
                //TotPrezzo
                parameters = TextUtil.replace(valueFields.substring(index + 1), ")", "");

                //one head sum fields is the sum of all row field
                if (nameMetod.equals("Sum")) {

                    double sum = 0;
                    int sizeRows = this.getRows().size();
                    String strValue = null;
                    double value = 0;
                    for (int r = 0; r < sizeRows; r++) {

                        strValue = (String) ((AnagBean) this.getRow(r)).getFieldValueString( Session.getMngConfig().getAnagObjFieldIDbyDescription(((AnagBean) this.getRow(r)).getType(),parameters)   );

                        if ((strValue != null) && (!strValue.equals(""))) {

                            value = Double.parseDouble(TextUtil.replace(strValue, ",", "."));
                            sum = sum + value;

                        }

                    }

                    this.getHeadObj().setField( Session.getMngConfig().getAnagObjFieldIDbyDescription(this.getHeadObj().getType(),valorizedField) , new Double(Double.parseDouble(String_Util.getStrDouble2Dec(sum).replace(',', '.')))  );

                }

            }

        }

    }
     
     
     
     public String getTitle() {

         return title;

    }
    
      
       //index  
    public IndexFieldValue getClusteredField() {
        
        return clusteredField;
    }

    public void setClusteredField(IndexFieldValue clusteredField) {
        if (clusteredField==null)
            clusteredField=new IndexFieldValue();
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
    
        //si scarica i type del head e del row
        //(del row per ora non metteremo nessun index,
        //quando lo metteremo si fara il ciclo su tutte le sue righe
        //e si aggiungerenno tutti i field (duplicati) coi loro valori)
        
        
        //index fields
        Vector indexFields=((DocObj) Session.getListStructureObj().get(this.getType())).getListIndexFields();
        
        int size=indexFields.size();
        
        getListIndexFields().removeAllElements();
        IndexFieldValue indexFld=null;
        
         for (int j = 0; j < size; j++) {

             indexFld=new IndexFieldValue();
             
             indexFld.setFieldID( (String) indexFields.elementAt(j) ); //field id
             indexFld.setFieldValue( this.getHeadObj().getFieldValue((String) indexFields.elementAt(j)) ); //field value
             
             getListIndexFields().addElement(indexFld);
          
        }        
        
        //clusterd field
        if ((((DocObj) Session.getListStructureObj().get(this.getType())).getClusteredField())!=null)
        {
            indexFld=new IndexFieldValue();
            
            indexFld.setFieldID(((DocObj) Session.getListStructureObj().get(this.getType())).getClusteredField());
            indexFld.setFieldValue(this.getHeadObj().getFieldValue(((DocObj) Session.getListStructureObj().get(this.getType())).getClusteredField()));
            
            this.setClusteredField(indexFld);
        }
        
        
         //defaultGroupIndex field
        if ((((DocObj) Session.getListStructureObj().get(getType())).getDefaultGroupIndex())!=null)
        {
            this.setDefaultGroupIndex((String)((DocObj) Session.getListStructureObj().get(getType())).getDefaultGroupIndex());
        }
        
        
//        //isChild
//        if (((AnagObj) Session.getListAnagObj().get(this.getFieldValue("type"))).isChild()) {
//            this.setIsChild(true);
//        } else {
//            this.setIsChild(false);}
        
    }
     
    
    //index end
      
    
    public void addRowObj(AnagBean rowObj){
        
        this.getRows().addElement(rowObj);
              
    }
  
    public void deleteRowObj(AnagBean rowObj){
        
        this.getRows().removeElement(rowObj);
                
    }
    
    public void deleteRow(int rowNum){
        
        this.getRows().removeElementAt(rowNum);
                
    }
    
     public AnagBean getRow(int index){
        return (AnagBean)getRows().elementAt(index);
    }
    
      public int rowSize(){
        
        return getRows().size();
        
    }
    
     
     public String serialize(){
         
        try {
            
            JSONObject jso=new JSONObject();
           
            jso.put("id", this.getId() );            
              
            jso.put("type", this.getType() );
         
            jso.put("typeHeadObj", this.getTypeHeadObj() );
            
            jso.put("typeRowObj", this.getTypeRowObj() );
            
            jso.put("numRec", this.getNumRec());
            
            jso.put("numRecHeadObj", this.getHeadObj().getNumRec() );//hashtable of head fields
            
            jso.put("numRecRowsObj", this.getNumRecRowObj()); //Vector of hashtable rows fields
            
            jso.put("lastModified",getLastMod()); // Serialize as a LONG
                    
           // jso.put("targetID",this.getTargetID());
            
            if (getRefLinkID()!=null)
             jso.put("refLinkID",this.getRefLinkID());
            
            if (getSystemID()!=null)
                jso.put("sysID", getSystemID());
            
            //do infoOperation
            this.doInfoOperation();
            
             //make title
            this.makeTitle();
            
            if (getTitle()!=null)
                jso.put("title",this.getTitle());
            
            return jso.toString();
            
        } catch (JSONException ex) {
             //#debug error
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
            
        }
    }
     
    public String serializeToServer() {
    
        //add header info and all information of headObj and rows Objs
        JSONObject jso=new JSONObject();
           
        try{
          
            jso.put("sysID", this.getSystemID()==null?new Integer(0):this.getSystemID());
            
            jso.put("lastModified", this.getLastMod());

            JSONObject jsoHead=new JSONObject(this.getHeadObj().serializeToServer());

            jso.put("head", jsoHead);

            //jso.put("head", this.getHeadObj().serializeToServer());

            //vector row
            JSONArray serializedRows = new  JSONArray();

            int size = getRows().size();

            AnagBean rowObj = null;

            JSONObject jsoRow;

            for (int i = 0; i < size; i++) {

                rowObj = (AnagBean) getRows().elementAt(i);

                jsoRow=new JSONObject(rowObj.serializeToServer());

                serializedRows.put(jsoRow);
                
                //serializedRows.addElement(rowObj.serializeToServer());

            }
           
           jso.put("rows", serializedRows );
         
        
         } catch (JSONException ex) {
             //#debug error
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
            
        }
        
          return jso.toString();
          
    }
     
     
     public Vector getNumRecRowObj(){
     
         Vector numRecs=new Vector();
         
         int size=getRows().size();
         
         AnagBean rowObj=null;
         
         for (int i=0;i<size;i++){
         
             rowObj=(AnagBean)getRows().elementAt(i);
         
             numRecs.addElement(rowObj.getNumRec());
             
         }
          
         return numRecs;
     
     }
     
       public void unserialize(String JSONstring){
           
        try {
            
            listIndexFields=null;
            clusteredField=null;
            title=null;
            
            JSONObject jso=new JSONObject(JSONstring);
             
             // Unserialize the order id
            this.setId(jso.getString("id"));
            
             // Unserialize the order type
            this.setType(jso.getString("type"));
            
            // Unserialize the order last modified          
            this.lastMod=new Long(jso.getLong("lastModified"));
            
            // Unserialize the order state
            //this.setTargetID(new Integer(jso.getInt("targetID")));
            
            this.setNumRec(new Integer(jso.getInt("numRec")));
            
            this.setTypeHeadObj(jso.getString("typeHeadObj"));
            
            this.setTypeRowObj(jso.getString("typeRowObj"));            
             
            //insert headObj
            setHeadObj(null);
                  
            try {

                int numRecHeadObj = ((Integer) jso.get("numRecHeadObj")).intValue();

                this.setHeadObj(Session.getAnagBeanDAO().getAnagBeanFromNumRec(this.getTypeHeadObj(), numRecHeadObj));

             

            } catch (JSONException ex) {
                //#debug error
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                //#debug error
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

       
            this.getRows().removeAllElements();            
            
           //Inser row Objs
            JSONArray jsa=jso.getJSONArray("numRecRowsObj");
            
            long size=jsa.length();
            int numRecRowObj=0;
            
            for(int j=0;j<size;j++){
                
                numRecRowObj=((Integer)jsa.get(j)).intValue();                  
                
                this.getRows().addElement(Session.getAnagBeanDAO().getAnagBeanFromNumRec(this.getTypeRowObj(), numRecRowObj));
            }
                     
            if (jso.has("refLinkID"))
                setRefLinkID(jso.getString("refLinkID"));
            
            
            if (jso.has("title"))
                this.setTitle(jso.getString("title"));
            
             if (jso.has("sysID"))
                this.setSystemID(new Integer(jso.getInt("sysID")));
            
           
        } catch (JSONException ex) {
            
            //#debug error
            System.out.println(ex.getMessage());
            
            ex.printStackTrace();
        }
        
        
    }
    
     
    public boolean isSync(){
     
        return (getSystemID()==null || getSystemID().intValue()==0) ?false : true;
     
     }
   
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
  

    public Vector getRows() {
        return rows;
    }

    public void setRows(Vector rows) {
        this.rows = rows;
    }

    
    public Long getLastMod() {
        return lastMod;
    }

    public void setLastMod(Long lastMod) {
        this.lastMod = lastMod;
    }

//    public Integer getTargetID() {
//       return (Integer) this.getHeadObj().getFieldValue("targetID");        
//    }
//
//    public void setTargetID(Integer targetID) {
//        this.getHeadObj().setField("targetID", targetID);
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

  
    public void setNumRec(Integer numRec) {
        this.numRec = numRec;
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


    public AnagBean getHeadObj() {
        return headObj;
    }

    public void setHeadObj(AnagBean headObj) {
        this.headObj = headObj;
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


    
}
