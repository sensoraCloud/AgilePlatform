/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.dao;

import agile.json.me.JSONArray;
import de.enough.polish.util.TextUtil;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import agile.json.me.JSONObject;
import agile.model.structure.Index;
import agile.model.structure.IndexFieldValue;
import agile.model.structure.Indexable;
import agile.model.structure.SystemIDsIdex;
import agile.session.Session;
import agile.util.HeapSorter;
import agile.util.IntegerComparator;
import agile.util.Search_Util;
/**
 *
 * @author ruego
 */
public class ManageIndex {

    private Hashtable hashIndex = null;
    private Hashtable hashCountRealNumObjs = null; //real number of obj
    private Hashtable hashIndexCountRealNumObjs = null; //numRec of typeObj in CountRealNumObjs rms
    private Hashtable hashCountRMSObjs = null; //number of last relativeNumRec rms obj
    private Hashtable hashIndexCountObjRMS; //numRec of typeObj in CountRMSObjs rms
    private Hashtable hashSyncIds = null;    
    //!!! se aggiungo dati sulla gestione rms occhio al metodo DELETE_ALL_OBJS_SERVICE
      
    
    private IndexFieldValue fld = null;
    private int numRecIndexRms = 0;
    private Index index = null;
    private RecordStore rms = null;
    private byte[] ser = null;
   
    private byte[] recData = null;
    private int sizeRecData = 0;
    private String str = null;
    private int dataLen = 0;  
    private int size = 0;
    private RecordStore rmsListIndex = null;
    private int numRMS=0;
    private int realRecID =0;
    private int nextRelativeID =0;
    
    
    public boolean createIndex(Indexable obj) {

        try {

            Vector indexFields = null;
            Vector indexsNumRecObj = null;
            Hashtable groupValues=null;
            
            //update obj index info
            obj.updateIndexInfo();
            
            boolean isChild=obj.isChild();

            indexFields = obj.getListIndexFields();
            size = indexFields.size();

            for (int j = 0; j < size; j++) {

                fld = (IndexFieldValue) indexFields.elementAt(j);

                if ((fld.getFieldValueString() == null) || (fld.getFieldValueString().equals(""))) {
                    continue;               
                }
                
                 //if exist "cliente_4 (cap) " (cliente_fieldID)
                if (((Hashtable) getHashIndex().get((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID())) != null) {

                    //if exist in "cliente_4" value "20020"
                    if (((Integer) ((Hashtable) getHashIndex().get((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID())).get(fld.getFieldValueString())) != null) {

                        //add obj numRec for "20020" index 

                        numRecIndexRms = ((Integer) ((Hashtable) getHashIndex().get((isChild?obj.getSourceType():obj.getType())+ "_" + fld.getFieldID())).get(fld.getFieldValueString())).intValue();
    
                         ////#debug info
                         //System.out.println("1(Create Index)-Trovato index per valore: " + fld.getFieldValue()  + " ; Apro record store: " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID());
                         
                        numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                        rms = RecordStore.openRecordStore((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID() + "_" + String.valueOf(numRMS), false);

                        realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);
                        
                        sizeRecData = rms.getRecordSize(realRecID);

                        recData = new byte[sizeRecData];

                        dataLen = rms.getRecord(realRecID, recData, 0);

                        str = (new String(recData, 0, dataLen)).trim();

                        index = new Index();

                        index.unserialize(str);                        

                        //if (!index.getIndexs().contains(obj.getNumRec())) {
                        index.getIndexs().addElement((isChild?obj.getSourceNumRec():obj.getNumRec()));
                        //}

                        index.setSorted(new Boolean(false));

                        //store index
                        ser = index.serialize().getBytes();

                        rms.setRecord(realRecID, ser, 0, ser.length);
                        
                        ////#debug info
                        //System.out.println("2(Create Index)-Aggiunto numRec:" +(isChild?obj.getSourceNumRec():obj.getNumRec())+" all'indice." + index.serialize());
                        
                        rms.closeRecordStore();
                        
                         // //#debug info
                         //System.out.println("3(Create Index)-Chiusi Record Store");


                    } else //create  value "20020" index and add to hashIndex
                    {
                        index = new Index();

                        ser = null;

                          ////#debug info
                        System.out.println("1.1(Create Index)-Non trovato index per valore: " + fld.getFieldValueString()  + " Scarico nuovo RecID ");
                        
                        nextRelativeID = Session.getMngIndex().getNextRelativeRecordID((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID());

                        numRMS = 1;

                         // //#debug info
                        //System.out.println("1.2(Create Index)- Calcolo numero RMS");
                        
                        
                        if (nextRelativeID > Session.getMaxLinesRMS()) {

                            numRMS = (int) (nextRelativeID / Session.getMaxLinesRMS()) + (nextRelativeID % Session.getMaxLinesRMS() > 0 ? 1 : 0);

                        }
                        
                        ////#debug info
                        //System.out.println("1.2(Create Index)-Non trovato index per valore: " + fld.getFieldValue()  + " ; Apro record store: " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID()+ "_" + String.valueOf(numRMS));
                      

                        rms = RecordStore.openRecordStore((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID() + "_" + String.valueOf(numRMS), true);
              
                        //set 20020
                        index.setValue((String) fld.getFieldValueString());

                        indexsNumRecObj = new Vector();

                        indexsNumRecObj.addElement((isChild?obj.getSourceNumRec():obj.getNumRec()));

                        //set numRec of obj
                        index.setIndexs(indexsNumRecObj);                       

                        index.setNumRec(new Integer(nextRelativeID));

                        //only one element
                        index.setSorted(new Boolean(true));

                        ser = index.serialize().getBytes();
                        
                        try {
                            rms.addRecord(ser, 0, ser.length);
                        } catch (RecordStoreNotOpenException ex) {
                            //#debug error
                            System.out.println(ex.getMessage());
                        } catch (RecordStoreException ex) {
                            //#debug error
                            System.out.println(ex.getMessage());
                        }
                        
                        // //#debug info
                         //System.out.println("2(Create Index)-Aggiunto indice " + index.serialize());

                        //put 20020 in hashIndex
                        ((Hashtable) getHashIndex().get((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID())).put((String) fld.getFieldValueString(), new Integer(nextRelativeID));

                        rms.closeRecordStore();
                        
                         // //#debug info
                         //System.out.println("3(Create Index)-Chiusi Record Store");
                        
                         
                    }

                } else {

                    //create cliente_cap rms and hash and 20020 index

                    //#debug info
                   // System.out.println("1(Create Index)-Non trovato rms per : " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID()  + " ; Creo record store: " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID());
                    
                    nextRelativeID = Session.getMngIndex().getNextRelativeRecordID((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID());

                    numRMS = 1;

                    if (nextRelativeID > Session.getMaxLinesRMS()) {

                        numRMS = (int) (nextRelativeID / Session.getMaxLinesRMS()) + (nextRelativeID % Session.getMaxLinesRMS() > 0 ? 1 : 0);

                    }

                    rms = RecordStore.openRecordStore((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID() + "_" + String.valueOf(numRMS), true);
                
                    //insert in rmsListIndex the rms name
                    rmsListIndex = RecordStore.openRecordStore("rmsListIndex", true);

                    ser = ((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID() + "_" + String.valueOf(numRMS)).getBytes();

                    try {
                        rmsListIndex.addRecord(ser, 0, ser.length);
                    } catch (RecordStoreNotOpenException ex) {
                        //#debug error
                        System.out.println(ex.getMessage());
                    } catch (RecordStoreException ex) {
                        //#debug error
                        System.out.println(ex.getMessage());
                    }
                    
                    //#debug info
                    //System.out.println("2(Create Index)-Aggiunto recordstore name : " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID()  + "in rmsListIndex RMS ");
          
                    
                    //create index "20020"
                    index = new Index();

                    //set 20020
                    index.setValue((String) fld.getFieldValueString());

                    indexsNumRecObj = new Vector();

                    indexsNumRecObj.addElement((isChild?obj.getSourceNumRec():obj.getNumRec()));

                    //set numRec of obj
                    index.setIndexs(indexsNumRecObj);
                  
                    index.setNumRec(new Integer(nextRelativeID));

                    //only one element
                    index.setSorted(new Boolean(true));

                    ser = index.serialize().getBytes();
                  
                    try {
                        rms.addRecord(ser, 0, ser.length);
                    } catch (RecordStoreNotOpenException ex) {
                        //#debug error
                        System.out.println(ex.getMessage());
                    } catch (RecordStoreException ex) {
                        //#debug error
                        System.out.println(ex.getMessage());
                    }
                    
                    //#debug info
                    //System.out.println("3(Create Index)-Aggiunto indice " + index.serialize());

                      //create hash for 20020
                    groupValues = new Hashtable();

                    groupValues.put((String) fld.getFieldValueString(), new Integer(nextRelativeID));

                    //put "cliente_cap" in hashIndex
                    getHashIndex().put((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID(), groupValues);
                    
                    rms.closeRecordStore();                    
                    rmsListIndex.closeRecordStore();
                    
                    //#debug info
                    //System.out.println("4(Create Index)-Chiusi Record Store");

                }
            }
            
            System.gc();
            
        } catch (Exception e) {
            //#debug error
            System.out.println("Can't create index for obj: " + obj.toString() + " Exception: " + e.getMessage());
            return false;
        } 



        return true;
    }

    public boolean deleteIndex(Indexable obj) {

        try {

            Vector indexFields = null;
            
            //update obj index info
            obj.updateIndexInfo();

            boolean isChild=obj.isChild();
                        
            indexFields = obj.getListIndexFields();
            size = indexFields.size();

            //for index field of obj
            for (int j = 0; j < size; j++) {

                fld = (IndexFieldValue) indexFields.elementAt(j);

                if ((fld.getFieldValueString() == null) || (fld.getFieldValueString().equals(""))) {
                    continue;              
                }
                
                //take rms numRec for field index value
                numRecIndexRms = ((Integer) ((Hashtable) getHashIndex().get( (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID())).get(fld.getFieldValueString())).intValue();

                //#debug info
                System.out.println("1(Delete Index)-Apro record store: " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID()+ "_" + String.valueOf(numRMS));
                
                numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                rms = RecordStore.openRecordStore((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID() + "_" + String.valueOf(numRMS), false);

                 //#debug info
                System.out.println("1.2(Delete Index)-Aperto record store: " + (isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID()+ "_" + String.valueOf(numRMS));
                
                realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);
                
                //#debug info
                System.out.println("1.3(Delete Index)-Get real ID" + realRecID);
               
                sizeRecData = rms.getRecordSize(realRecID); 

                recData = new byte[sizeRecData];

                dataLen = rms.getRecord(realRecID, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();

                //#debug info
                System.out.println("2(Delete Index)-Letto indice: " + str);
                
                index = new Index();

                index.unserialize(str);

                //delete numRec of obj
                index.getIndexs().removeElement((isChild?obj.getSourceNumRec():obj.getNumRec()));

                //if size=0 delete index from rms
                if (index.getIndexs().size() == 0) {
                    
                    //#debug info
                    System.out.println("3(Delete Index)-Cancello indice (non ha piu elementi) " + str);

                    rms.deleteRecord(realRecID);

                    ((Hashtable) getHashIndex().get((isChild?obj.getSourceType():obj.getType()) + "_" + fld.getFieldID())).remove((String) fld.getFieldValueString());

                } else {

                    //i can left old sorted value (when delete element sort its ok)
                    //index.setSorted(new Boolean(false));

                    //store new index
                    ser = index.serialize().getBytes();

                       //#debug info
                     System.out.println("4(Delete Index)-Salvo nuovo indice senza numRec " + index.serialize());
                    
                    rms.setRecord(realRecID, ser, 0, ser.length);


                }
                
                rms.closeRecordStore();
                
                //#debug info
                System.out.println("5(Delete Index)-Chiuso record Store");

            }

        } catch (Exception e) {
            //#debug error
            System.out.println("Can't delete index for obj: " + obj.toString() + " Exception: " + e.getMessage());
            return false;
        } 

        return true;
    }

    public Hashtable getHashIndex() {
        if (hashIndex == null) {
            setHashIndex(new Hashtable());
        }
        return hashIndex;
    }
    
   
    //return next relative ID obj Example: for store numrec value in the rms object (every obj stored in the rms have relative numrec for unique indentified when the obj is unserialized in the virtual memory)
    public int getNextRelativeRecordID(String typeObj) {

        int numRec = 1;

        try {

            if (getHashCountRMSObjs().get(typeObj) != null) {

                numRec = ((Integer) getHashCountRMSObjs().get(typeObj)).intValue() + 1;

                RecordStore rmsList = RecordStore.openRecordStore("rmsCountObjs", false);

                String valueCount = typeObj + "@" + String.valueOf(numRec);

                // can save modified  
                rmsList.setRecord(((Integer) getHashIndexCountObjRMS().get(typeObj)).intValue(), valueCount.getBytes(), 0, valueCount.getBytes().length);

                rmsList.closeRecordStore();
                
                //put in hash
                getHashCountRMSObjs().put(typeObj, new Integer(numRec));


            } else {
               
                
                //save in rms
                RecordStore rmsList = RecordStore.openRecordStore("rmsCountObjs", true);
                
                int numRecRmsObj=rmsList.getNextRecordID();              

                String valueCount = typeObj + "@" + String.valueOf(numRec);
                
                rmsList.addRecord(valueCount.getBytes(), 0, valueCount.getBytes().length);
                
                rmsList.closeRecordStore();
                
                getHashIndexCountObjRMS().put(typeObj, new Integer(numRecRmsObj)); 
              
                getHashCountRMSObjs().put(typeObj, new Integer(numRec));
              

            }

        } catch (Exception e) {

            //#debug error
            System.out.println("Can't take Relative RecordID! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return 0;

        }

        return numRec;

    }
    

    //for know how many type objs memorized in to the current system
    public void incrementCountRealObj(String typeObj) {

        int numObj = 1;

        try {

            if (getHashCountRealNumObjs().get(typeObj) != null) {

                numObj = ((Integer) getHashCountRealNumObjs().get(typeObj)).intValue() + 1;

                RecordStore rmsList = RecordStore.openRecordStore("realCountObjs", false);

                String valueCount = typeObj + "@" + String.valueOf(numObj);

                // can save modified  
                rmsList.setRecord(((Integer) getHashIndexCountRealNumObjs().get(typeObj)).intValue(), valueCount.getBytes(), 0, valueCount.getBytes().length);

                rmsList.closeRecordStore();
                
                //put in hash
                getHashCountRealNumObjs().put(typeObj, new Integer(numObj));


            } else {
               
                
                //save in rms
                RecordStore rmsList = RecordStore.openRecordStore("realCountObjs", true);
                
                int numRecRmsObj=rmsList.getNextRecordID();              

                String valueCount = typeObj + "@" + String.valueOf(numObj);
                
                rmsList.addRecord(valueCount.getBytes(), 0, valueCount.getBytes().length);
                
                rmsList.closeRecordStore();
                
                getHashCountRealNumObjs().put(typeObj, new Integer(numObj)); 
              
                getHashIndexCountRealNumObjs().put(typeObj, new Integer(numRecRmsObj));
              

            }

        } catch (Exception e) {

            //#debug error
            System.out.println("Can't increment count obj! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
         
        }       

    }
    
     public void decrementCountRealObj(String typeObj) {

        
        try {

            if (getHashCountRealNumObjs().get(typeObj) != null) {

                int numObj = ((Integer) getHashCountRealNumObjs().get(typeObj)).intValue() - 1;

                RecordStore rmsList = RecordStore.openRecordStore("realCountObjs", false);

                String valueCount = typeObj + "@" + String.valueOf(numObj);

                // can save modified  
                rmsList.setRecord(((Integer) getHashIndexCountRealNumObjs().get(typeObj)).intValue(), valueCount.getBytes(), 0, valueCount.getBytes().length);

                rmsList.closeRecordStore();
                
                //put in hash
                getHashCountRealNumObjs().put(typeObj, new Integer(numObj));

            } 

        } catch (Exception e) {

            //#debug error
            System.out.println("Can't decrement count obj ! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
         
        }       

    }


    
     public int getNextRelativeRecordIDnoSaveValue(String typeObj) {

         int numRec = 1;

         if (getHashCountRMSObjs().get(typeObj) != null) {

             numRec = ((Integer) getHashCountRMSObjs().get(typeObj)).intValue() + 1;

         }

        return numRec;

    }
  

    public boolean createHashIndex() {

        try {
            
            Hashtable groupValues=null;
            
            //create hashCountRMS
            RecordStore rmsList = RecordStore.openRecordStore("rmsCountObjs", true);
           
            String valueCount=null;
             
            String[] campi = null;
             
            int lastID = rmsList.getNextRecordID();
                
            //cliente@122 or cliente_citta@23
            for (int i = 1; i < lastID; ++i) {

                try {

                    sizeRecData = rmsList.getRecordSize(i);

                    recData = new byte[sizeRecData];

                    dataLen = rmsList.getRecord(i, recData, 0);

                    valueCount = (new String(recData, 0, dataLen)).trim();

                    campi = TextUtil.split(valueCount, '@');

                    //put in hash
                    getHashCountRMSObjs().put(campi[0],new Integer(Integer.parseInt(campi[1])));

                    getHashIndexCountObjRMS().put(campi[0], new Integer(i));


                } catch (InvalidRecordIDException e) {
                    continue;
                }
            }
        
            
            rmsList.closeRecordStore();
            
            
             //create hashCountRealNumObjs
            rmsList = RecordStore.openRecordStore("realCountObjs", true);
           
            valueCount=null;
             
            campi = null;
             
            lastID = rmsList.getNextRecordID();
                
            //cliente@122 or cliente_citta@23
            for (int i = 1; i < lastID; ++i) {

                try {

                    sizeRecData = rmsList.getRecordSize(i);

                    recData = new byte[sizeRecData];

                    dataLen = rmsList.getRecord(i, recData, 0);

                    valueCount = (new String(recData, 0, dataLen)).trim();

                    campi = TextUtil.split(valueCount, '@');

                    //put in hash
                    getHashCountRealNumObjs().put(campi[0],new Integer(Integer.parseInt(campi[1])));

                    getHashIndexCountRealNumObjs().put(campi[0], new Integer(i));


                } catch (InvalidRecordIDException e) {
                    continue;
                }
            }
        
            
            rmsList.closeRecordStore();
            
            
            

            RecordEnumeration recordEnumListIndex = null;

            rmsListIndex = RecordStore.openRecordStore("rmsListIndex", true);

            recordEnumListIndex = rmsListIndex.enumerateRecords(null, null, false);

            String rmsName = null;

            RecordEnumeration recordEnum = null;
            
            String genericKey=null;
            
            //for all rmsIndex create hashIndex
            while (recordEnumListIndex.hasNextElement()) {

                recData = recordEnumListIndex.nextRecord();

                rmsName = (new String(recData)).trim();
                
                campi = TextUtil.split(rmsName, '_');
                
                genericKey = campi[0] + "_" + campi[1]; 

                if (!getHashIndex().containsKey(genericKey)) {
                    
                    groupValues=new Hashtable();
                    
                    getHashIndex().put(genericKey,groupValues);                   
                    
                }else{
                    
                    groupValues=(Hashtable)getHashIndex().get(genericKey);
                
                }                 
               
                rms = RecordStore.openRecordStore(rmsName, false);

                recordEnum = rms.enumerateRecords(null, null, false);

                //for all index 
                while (recordEnum.hasNextElement()) {

                    recData = recordEnum.nextRecord();

                    str = (new String(recData)).trim();

                    index = new Index();

                    index.unserialize(str);                   

                    groupValues.put(index.getValue(), index.getNumRec());

                }
                
                rms.closeRecordStore();

            }

            rmsListIndex.closeRecordStore();     
            
            
            //create hashSystemIDs
            RecordStore rmsSysID = RecordStore.openRecordStore("systemIDs", true);
             
            lastID = rmsSysID.getNextRecordID();
            
            String serz=null;
            
            SystemIDsIdex  sysindex = null;
                
            
            for (int i = 1; i < lastID; ++i) {

                try {

                    sizeRecData = rmsSysID.getRecordSize(i);

                    recData = new byte[sizeRecData];

                    dataLen = rmsSysID.getRecord(i, recData, 0);

                    serz = (new String(recData, 0, dataLen)).trim();

                    sysindex = new SystemIDsIdex();

                    sysindex.unserialize(serz);                   

                    //put in hash
                    getHashSyncIds().put(sysindex.getTypeObj(),new Integer(i));
                  
                } catch (InvalidRecordIDException e) {
                    continue;
                }
            }
        
            
            rmsSysID.closeRecordStore();               

            System.gc();


        } catch (Exception e) {

            //#debug info
            System.out.println("Can't create HashIndex! Exception: " + e.getMessage()==null?"null":e.getMessage());
            return false;

        } 

        return true;
    }
    
    
     public int getRealRecID(int relativeRecID,int numRMS){
    
        return relativeRecID - ( Session.getMaxLinesRMS() * (numRMS-1));
    }
    
     public int getRelativeRecID(int realRecID,int numRMS){
    
        return ( (numRMS-1) * Session.getMaxLinesRMS() ) + realRecID ;
    }
    
    
    
    public int getNumRMS(int relativeRecID){
    
        return (int) (relativeRecID / Session.getMaxLinesRMS()) + (relativeRecID % Session.getMaxLinesRMS()>0?1:0);
    }
      
    
    
   
    //sync
    
    public boolean existSystemID(String typeObj,Integer systemID){
        
      boolean find=false;

        try {

            if (getHashSyncIds().get(typeObj) != null) {

                int numRec = ((Integer) getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);
                
                sizeRecData = rmsSysIds.getRecordSize(numRec); 

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRec, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();
                 
                SystemIDsIdex  sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);
                
                int idx=-1;
                
                if (sysindex.getSorted().booleanValue()){
                
                    idx=Search_Util.binaryIntegerVectorSearch(sysindex.getSystemIDs(), 0, sysindex.getSystemIDs().size()-1 , systemID.intValue());
                
                }else{
                
                    if (sysindex.getSystemIDs().contains(systemID)){
                    
                        idx=sysindex.getSystemIDs().indexOf(systemID);
                    }               
                
                }
               
                
                if (idx>=0)
                    find=true;
                else find=false;                
                
                rmsSysIds.closeRecordStore();                

            } else return false;
            
        } catch (Exception e) {

            //#debug error
            System.out.println("Can't take systemID! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return false;

        }
    
        return find;
    }
    
    public Integer getNumRecFromSystemID(String typeObj,Integer systemID){
        
     Integer numRecSys=null;

        try {

            if (getHashSyncIds().get(typeObj) != null) {

                int numRec = ((Integer) getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);
                
                sizeRecData = rmsSysIds.getRecordSize(numRec); 

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRec, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();
                 
                SystemIDsIdex  sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);
                
                int idx=-1;
                
                if (sysindex.getSorted().booleanValue()){
                
                    idx=Search_Util.binaryIntegerVectorSearch(sysindex.getSystemIDs(), 0, sysindex.getSystemIDs().size()-1 , systemID.intValue());
                
                }else{
                
                    if (sysindex.getSystemIDs().contains(systemID)){
                    
                        idx=sysindex.getSystemIDs().indexOf(systemID);
                    }               
                
                }
                 
                if (idx>=0){                    
                                  
                   numRecSys=(Integer)sysindex.getNumRecs().elementAt(idx);
                                      
                }                
                else numRecSys=null;
                
                rmsSysIds.closeRecordStore();                

            } else return null;
            
        } catch (Exception e) {

            //#debug error
            System.out.println("Can't take systemID! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return null;

        }
    
        return numRecSys;
    }
    
    public boolean addSystemID(String typeObj,Integer numRec,Integer systemID,long lastmod,boolean sortResult){
         
        try {

            if (getHashSyncIds().get(typeObj) != null) {

                int numRc = ((Integer) getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);
                
                sizeRecData = rmsSysIds.getRecordSize(numRc); 

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRc, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();
                 
                SystemIDsIdex  sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);
                
                sysindex.getSystemIDs().addElement(systemID);
                sysindex.getLastMod().addElement(new Long(lastmod));
                sysindex.getNumRecs().addElement(numRec);
                
                sysindex.setSorted(new Boolean(false));
                
                ser = sysindex.serialize().getBytes();
                    
                rmsSysIds.setRecord(numRc, ser, 0, ser.length);                
                
                rmsSysIds.closeRecordStore();    
                
                if (sortResult)
                    this.sortSystemIDs(typeObj);   

            } else {                              
                
                //save in rms
                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", true);
                
                int numRmsSysIds=rmsSysIds.getNextRecordID();              

                 SystemIDsIdex  sysindex = new SystemIDsIdex();
                 
                 sysindex.setSorted(new Boolean(true));
                 
                 sysindex.setTypeObj(typeObj);
                 
                 sysindex.setNumRec(new Integer(numRmsSysIds));
                 
                 Vector sysID=new Vector();
                 Vector lastMod=new Vector();
                 Vector numRecs=new Vector();
                 
                 sysID.addElement(systemID);
                 lastMod.addElement(new Long(lastmod));
                 numRecs.addElement(numRec);
                 
                 sysindex.setSystemIDs(sysID);
                 sysindex.setLastMod(lastMod);
                 sysindex.setNumRecs(numRecs);
                
                 String serz =sysindex.serialize();
                
                 rmsSysIds.addRecord(serz.getBytes(), 0, serz.getBytes().length);
                
                 rmsSysIds.closeRecordStore();
                         
                 getHashSyncIds().put(typeObj, new Integer(numRmsSysIds));              

            }

        } catch (Exception e) {

            //#debug error
            System.out.println("Can't add systemID! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return false;

        }
        
        return true;
    }
    
    public boolean deleteSystemID(String typeObj,Integer systemID){        
        
        boolean deleted=false;

        try {

            if (getHashSyncIds().get(typeObj) != null) {

                int numRec = ((Integer) getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);
                
                sizeRecData = rmsSysIds.getRecordSize(numRec); 

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRec, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();
                 
                SystemIDsIdex  sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);
                
                int idx=-1;
                
                if (sysindex.getSorted().booleanValue()){
                
                    idx=Search_Util.binaryIntegerVectorSearch(sysindex.getSystemIDs(), 0, sysindex.getSystemIDs().size()-1 , systemID.intValue());
                
                }else{
                
                    if (sysindex.getSystemIDs().contains(systemID)){
                    
                        idx=sysindex.getSystemIDs().indexOf(systemID);
                    }               
                
                }
                
                if (idx>=0){   
                
                   //delete                   
                   sysindex.getSystemIDs().removeElementAt(idx);
                   sysindex.getNumRecs().removeElementAt(idx);
                   sysindex.getLastMod().removeElementAt(idx);
                   
                    if (sysindex.getSystemIDs().size() == 0) {
                        
                        rmsSysIds.deleteRecord(numRec);
                        
                        getHashSyncIds().remove(typeObj);
                        
                    } else {

                        ser = sysindex.serialize().getBytes();

                        rmsSysIds.setRecord(numRec, ser, 0, ser.length);

                    }        
                    
                      
                   deleted=true;
                   
                }else deleted=false;                
                               
                rmsSysIds.closeRecordStore();                

            } else return false;
            
        } catch (Exception e) {

            //#debug error
            System.out.println("Can't delete systemID! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return false;

        }        
    
        return deleted;
    }
    
    public boolean commitLastModSystemID(String typeObj,Integer systemID,long lastmod){
    
        boolean updated=false;

        try {

            if (getHashSyncIds().get(typeObj) != null) {

                int numRec = ((Integer) getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);
                
                sizeRecData = rmsSysIds.getRecordSize(numRec); 

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRec, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();
                 
                SystemIDsIdex  sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);
                
               int idx=-1;
                
                if (sysindex.getSorted().booleanValue()){
                
                    idx=Search_Util.binaryIntegerVectorSearch(sysindex.getSystemIDs(), 0, sysindex.getSystemIDs().size()-1 , systemID.intValue());
                
                }else{
                
                    if (sysindex.getSystemIDs().contains(systemID)){
                    
                        idx=sysindex.getSystemIDs().indexOf(systemID);
                    }               
                
                }
               
                if (idx>=0){   
                
                   //update
                   sysindex.getLastMod().setElementAt(new Long(lastmod), idx);
                   
                   ser = sysindex.serialize().getBytes();
                    
                   rmsSysIds.setRecord(numRec, ser, 0, ser.length);
                      
                   updated=true;
                   
                }else updated=false;                
                               
                rmsSysIds.closeRecordStore();                

            } else return false;
            
        } catch (Exception e) {

            //#debug error
            System.out.println("Can't updated lastMod systemID! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return false;

        }        
    
        return updated;        
      
    }
    
    public boolean sortSystemIDs(String typeObj){
        
        try {

            if (Session.getMngIndex().getHashSyncIds().get(typeObj) != null) {

                int numRec = ((Integer) Session.getMngIndex().getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);

                sizeRecData = rmsSysIds.getRecordSize(numRec);

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRec, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();

                SystemIDsIdex sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);

//                int first = 0;
//                int second = 0;
//                Integer temp = null;

                Vector swapVect=new Vector(2);

                swapVect.addElement( sysindex.getLastMod());
                swapVect.addElement(  sysindex.getNumRecs());

                //sort
                HeapSorter.heapsort(sysindex.getSystemIDs(), null,new IntegerComparator());
                
                //sort
//                size = sysindex.getSystemIDs().size();
//
//                for (int i = 0; i < size; i++) {
//
//                    for (int j = i + 1; j < size; j++) {
//
//                        first = ((Integer) sysindex.getSystemIDs().elementAt(i)).intValue();
//                        second = ((Integer) sysindex.getSystemIDs().elementAt(j)).intValue();
//
//                        if (first > second) {
//
//                            //systemIDs
//                            temp = ((Integer) sysindex.getSystemIDs().elementAt(i));
//                            sysindex.getSystemIDs().setElementAt((Integer) sysindex.getSystemIDs().elementAt(j), i);
//                            sysindex.getSystemIDs().setElementAt(temp, j);
//
//                            //lastMod
//                            temp = ((Integer) sysindex.getLastMod().elementAt(i));
//                            sysindex.getLastMod().setElementAt((Integer) sysindex.getLastMod().elementAt(j), i);
//                            sysindex.getLastMod().setElementAt(temp, j);
//
//                            //numRec
//                            temp = ((Integer) sysindex.getNumRecs().elementAt(i));
//                            sysindex.getNumRecs().setElementAt((Integer) sysindex.getNumRecs().elementAt(j), i);
//                            sysindex.getNumRecs().setElementAt(temp, j);
//
//
//                        }
//
//                    }
//
//
//                }

                sysindex.setSorted(new Boolean(true));

                ser = sysindex.serialize().getBytes();

                rmsSysIds.setRecord(numRec, ser, 0, ser.length);

                rmsSysIds.closeRecordStore();

            }


        } catch (Exception ex) {

            //#debug error
            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());
            
             return false;

        }
    
        return true;
    }
    
    public String getSerializedJSONSystemIDs(String typeObj){
        
       String vetSysIDs=null;

        try {

            if (getHashSyncIds().get(typeObj) != null) {

                int numRec = ((Integer) getHashSyncIds().get(typeObj)).intValue();

                RecordStore rmsSysIds = RecordStore.openRecordStore("systemIDs", false);
                
                sizeRecData = rmsSysIds.getRecordSize(numRec); 

                recData = new byte[sizeRecData];

                dataLen = rmsSysIds.getRecord(numRec, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();
                 
                SystemIDsIdex  sysindex = new SystemIDsIdex();

                sysindex.unserialize(str);
              
                JSONObject jso=new JSONObject();
                
                jso.put("sysIDs",sysindex.getSystemIDs());
                jso.put("recentChanges",sysindex.getLastMod());
                
                vetSysIDs=jso.toString();
                                             
                rmsSysIds.closeRecordStore();                

            } else{

                JSONObject jso=new JSONObject();

                jso.put("sysIDs",new JSONArray());
                jso.put("recentChanges",new JSONArray());

                vetSysIDs=jso.toString();


            }
            
        } catch (Exception e) {

            //#debug error
            System.out.println("Can't get vet systemIDs! Exception: " + e.getMessage() == null ? "null" : e.getMessage());
            return null;

        }        
    
        return vetSysIDs;        
      
    }
    
    //end sync
    
    
   
                
    
    public Hashtable getHashCountRealNumObjs() {
         if (hashCountRealNumObjs == null) {
            hashCountRealNumObjs = new Hashtable();
        }
        return hashCountRealNumObjs;
    }

    public void setHashCountRealNumObjs(Hashtable hashCountObjs) {
        this.hashCountRealNumObjs = hashCountObjs;
    }

    public Hashtable getHashIndexCountObjRMS() {
        
         if (hashIndexCountObjRMS == null) {
            setHashIndexCountObjRMS(new Hashtable());
        }
        
        return hashIndexCountObjRMS;
    }

    public Hashtable getHashSyncIds() {
         
         if (hashSyncIds == null) {
            hashSyncIds = new Hashtable();
        }
        return hashSyncIds;
    }

    public void setHashSyncIds(Hashtable hashSyncIds) {
        this.hashSyncIds = hashSyncIds;
    }

    public Hashtable getHashCountRMSObjs() {
        
        if (hashCountRMSObjs == null) {
            hashCountRMSObjs = new Hashtable();
        }
          
        return hashCountRMSObjs;
    }

    public void setHashCountRMSObjs(Hashtable hashCountRMSObjs) {
        this.hashCountRMSObjs = hashCountRMSObjs;
    }

    public Hashtable getHashIndexCountRealNumObjs() {
         if (hashIndexCountRealNumObjs == null) {
            hashIndexCountRealNumObjs = new Hashtable();
        }
        return hashIndexCountRealNumObjs;
    }

    public void setHashIndexCountRealNumObjs(Hashtable hashIndexCountRealNumObjs) {
        this.hashIndexCountRealNumObjs = hashIndexCountRealNumObjs;
    }

    public void setHashIndex(Hashtable hashIndex) {
        this.hashIndex = hashIndex;
    }

    public void setHashIndexCountObjRMS(Hashtable hashIndexCountObjRMS) {
        this.hashIndexCountObjRMS = hashIndexCountObjRMS;
    }
}
