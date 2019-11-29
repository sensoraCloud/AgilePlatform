/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile.model.dao;

import java.util.Date;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import agile.json.me.JSONObject;
import agile.model.structure.AnagBean;
import agile.model.structure.DocBean;
import agile.session.Session;
import agile.util.Date_Util;

/**
 *
 * @author ruego
 */
public class DocBeanDAO {

    
      private RecordStore rms=null;   
      
      
     public boolean persist(DocBean obj)  {
         
         try {
             
            obj.setLastMod(Date_Util.getStampFromDate(new Date()) );
         
            byte[] ser = null;

            int nextRelativeID=Session.getMngIndex().getNextRelativeRecordID((String) obj.getType());
            
            int numRMS=1;
            
            if (nextRelativeID>Session.getMaxLinesRMS()){
            
                numRMS= (int) (nextRelativeID / Session.getMaxLinesRMS()) + (nextRelativeID % Session.getMaxLinesRMS()>0?1:0);
                           
            }
            
            rms = RecordStore.openRecordStore((String) obj.getType()+"_"+String.valueOf(numRMS), true);

            obj.setNumRec( new Integer(nextRelativeID));
            
            obj.setId(String.valueOf(nextRelativeID));
            
            ser = obj.serialize().getBytes();
            
             try {
                 rms.addRecord(ser, 0, ser.length);
             } catch (RecordStoreNotOpenException ex) {
                 //#debug error
                 System.out.println(ex.getMessage());
             } catch (RecordStoreException ex) {
                 //#debug error
                 System.out.println(ex.getMessage());
             }
            
            rms.closeRecordStore();

             //if isSync save sync info
             if (obj.isSync()) {
                 Session.getMngIndex().addSystemID(obj.getType(), obj.getNumRec(), obj.getSystemID(), obj.getLastMod().longValue(), true);
             }
            
             //increment count obj
             Session.getMngIndex().incrementCountRealObj(obj.getType());
            
        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage());
            try {
                rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage());
            }
            return false;

        }

        return true;
        
        
    }
     
     
 
     public boolean commitChange(DocBean obj) {

        try {
          
            obj.setLastMod(Date_Util.getStampFromDate(new Date()) );    
            
            byte[] ser = null;
      
            int  numRMS=  Session.getMngIndex().getNumRMS(obj.getNumRec().intValue());
            
            //#debug info
            System.out.println("1-Apro recordordStore: " + (String) obj.getType()+"_"+String.valueOf(numRMS) );
            
            rms = RecordStore.openRecordStore((String) obj.getType()+"_"+String.valueOf(numRMS), false);
    
            int realRecID=Session.getMngIndex().getRealRecID(obj.getNumRec().intValue(), numRMS);
            
             //#debug info
            System.out.println("2-Salvo in recordorsotre oggetto modificato: ");
            
            ser = obj.serialize().getBytes();
      
              //#debug info
            System.out.println("3-Oggetto serializzato: " +  obj.serialize() );
            
            // can save modified obj 
            rms.setRecord(realRecID, ser, 0, ser.length);

               //#debug info
            System.out.println("4-Chiudi rs" );
            
            rms.closeRecordStore();

            //if isSync save sync info
            if (obj.isSync()) {
                Session.getMngIndex().commitLastModSystemID(obj.getType(), obj.getSystemID(), obj.getLastMod().longValue());
            }
            
             //#debug info
            System.out.println("5-rsChiuso .Salvato" );
          
            
        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage()==null?"Null":e.getMessage());
            try {
               rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage()==null?"Null":e2.getMessage());
                return false;
            }
            return false;
        }

        return true;

    }

 public DocBean getDocBeanFromNumRec(String type,int numRec) {
        
        DocBean obj = new DocBean();
        
        try{
            
             //#debug info
            System.out.println("2-Apro recordordStore: " + type );
              
            int  numRMS= (int) (numRec / Session.getMaxLinesRMS()) + (numRec % Session.getMaxLinesRMS()>0?1:0);
            
            rms = RecordStore.openRecordStore(type+"_"+String.valueOf(numRMS), false);
    
            int realRecID=numRec -( Session.getMaxLinesRMS() * (numRMS-1));
            
            int sizeRecData = rms.getRecordSize(realRecID);

            byte[] recData = new byte[sizeRecData];

            int dataLen = rms.getRecord(realRecID, recData, 0);

            String str = (new String(recData, 0, dataLen)).trim();

            obj.unserialize(str);
            
            rms.closeRecordStore();        
            
            
         } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage()==null?"Null":e.getMessage());
            try {
               rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage()==null?"Null":e2.getMessage());
                return null;
            }
            return null;
        }        

        return obj;
    }
     
    public String getTitleFromNumRec(String type, int numRec) {

        String title = null;

        try {

            //#debug info
            System.out.println("2-Apro recordordStore: " + type);

            int numRMS = (int) (numRec / Session.getMaxLinesRMS()) + (numRec % Session.getMaxLinesRMS() > 0 ? 1 : 0);

            rms = RecordStore.openRecordStore(type + "_" + String.valueOf(numRMS), false);

            int realRecID = numRec - (Session.getMaxLinesRMS() * (numRMS - 1));

            int sizeRecData = rms.getRecordSize(realRecID);

            byte[] recData = new byte[sizeRecData];

            int dataLen = rms.getRecord(realRecID, recData, 0);

            String str = (new String(recData, 0, dataLen)).trim();

            JSONObject jso = new JSONObject(str);

            title = jso.getString("title");

            rms.closeRecordStore();


        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
            try {
                rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());
                return null;
            }
            return null;
        }

        return title;
    }
     
   
      public boolean delete(DocBean obj) {

        try {                       
           
            //#debug info
            System.out.println("1-Lancio cancellazione headObj");

            Session.getAnagBeanDAO().delete(obj.getHeadObj());
            
            //#debug info
            System.out.println("2-Lancio cancellazione rowObjs");

            int size=obj.getRows().size();
            
            for (int i = 0; i < size; i++) {

                AnagBean rowObj = (AnagBean) obj.getRows().elementAt(i);
                Session.getAnagBeanDAO().delete(rowObj);

            }
            
            //#debug info
            System.out.println("3-Apro recordordStore: " + (String) obj.getType() );
              
            int  numRMS= (int) (obj.getNumRec().intValue() / Session.getMaxLinesRMS()) + (obj.getNumRec().intValue() % Session.getMaxLinesRMS()>0?1:0);
            
            rms = RecordStore.openRecordStore((String) obj.getType()+"_"+String.valueOf(numRMS), false);
    
            int realRecID=obj.getNumRec().intValue()-( Session.getMaxLinesRMS() * (numRMS-1));
            
            //#debug info
            System.out.println("4-Cancello obj" );
                    
            // can save modified obj 
            rms.deleteRecord(realRecID);

               //#debug info
            System.out.println("5-Chiudi rs" );
            
            rms.closeRecordStore();
            
             //#debug info
            System.out.println("6-Cancellato!" );

             //if isSync delete sync info with new lastmod
            if (obj.isSync()) {
                Session.getMngIndex().deleteSystemID(obj.getType(), obj.getSystemID());
            }
            
             //decrement count obj
             Session.getMngIndex().decrementCountRealObj(obj.getType());
          
            
        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage()==null?"Null":e.getMessage());
            try {
               rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage()==null?"Null":e2.getMessage());
                return false;
            }
            return false;
        }

        return true;

    }
    
      
       public String getSerializedDocBeanFromNumRec(String type,int numRec) {
        
         String str = null;
         
        try{ 
            
             //#debug info
            System.out.println("2-Apro recordordStore: " + type );
              
            int  numRMS= (int) (numRec / Session.getMaxLinesRMS()) + (numRec % Session.getMaxLinesRMS()>0?1:0);
            
            rms = RecordStore.openRecordStore(type+"_"+String.valueOf(numRMS), false);
    
            int realRecID=numRec -( Session.getMaxLinesRMS() * (numRMS-1));
            
            int sizeRecData = rms.getRecordSize(realRecID);

            byte[] recData = new byte[sizeRecData];

            int dataLen = rms.getRecord(realRecID, recData, 0);

            str = (new String(recData, 0, dataLen)).trim();
  
            rms.closeRecordStore();        
            
            
         } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage()==null?"Null":e.getMessage());
            try {
               rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage()==null?"Null":e2.getMessage());
                return null;
            }
            return null;
        }        

        return str;
    }
      
       
      public void persistNewRow(DocBean doc, AnagBean row) {

        //set source info
        row.setField("sourceNumRec", doc.getNumRec());
        row.setField("sourceType", doc.getType());
        //save row
        Session.getAnagBeanDAO().persist(row);
        //save head obj before infoOperation Modified (if info of (index)field that change with infoOperation must remake index)
        AnagBean oldHeadObj = new AnagBean();
        oldHeadObj.unserialize(doc.getHeadObj().serialize());
        //save doc with new row and make info operation
        this.commitChange(doc);
        //save head with new infoOperation
        Session.getAnagBeanDAO().commitChange(doc.getHeadObj(), oldHeadObj);

    }
      
     public void commitRow(DocBean doc, AnagBean row,AnagBean oldRow) {

        Session.getAnagBeanDAO().commitChange(row, oldRow);
        AnagBean oldHeadObj = new AnagBean();
        oldHeadObj.unserialize(doc.getHeadObj().serialize());
        //do infoOperation
        doc.doInfoOperation();
        //save head with new infoOperation
        Session.getAnagBeanDAO().commitChange(doc.getHeadObj(), oldHeadObj);
        
    }
     
     public void deleteRow(DocBean doc, AnagBean row) {

        Session.getAnagBeanDAO().delete(row);
        AnagBean oldHeadObj = new AnagBean();                                                       
        oldHeadObj.unserialize(doc.getHeadObj().serialize());
        //save doc with new row and make info operation
        this.commitChange(doc);
        //save head with new infoOperation
        Session.getAnagBeanDAO().commitChange(doc.getHeadObj(), oldHeadObj);
        
    }
     
    public void commitHead(DocBean doc, AnagBean head, AnagBean oldHead) {

       Session.getAnagBeanDAO().commitChange(head,oldHead);    
       
       this.commitChange(doc);
       
    }
      
    
}
