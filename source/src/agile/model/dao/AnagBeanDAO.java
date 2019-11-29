/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile.model.dao;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.DateField;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ListItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import agile.json.me.JSONObject;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.Field;
import agile.session.Session;
import agile.util.Date_Util;

/**
 *
 * @author ruego
 */
public class AnagBeanDAO {

    //work with MangageIndex.java
    private RecordStore rms = null;

    public void setAnagBeanFromListItms(AnagBean obj, ListItem listItms) throws Exception {

        Item[] itms = listItms.getItems();
        int size = itms.length;
        Object itmObj = null;
        Object value = null;
        String valueStr = null;
        String typeField = null;
        String fieldID = null;

        for (int i = 0; i < size; i++) {

            itmObj = itms[i];

            if (itmObj instanceof TextField) {

                //obj.setField(  Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel()  ) , ((TextField) itmObj).getString().toUpperCase());

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((TextField) itmObj).getLabel())).getTypeUI();

                valueStr = ((TextField) itmObj).getString().toUpperCase();


            }

            if (itmObj instanceof StringItem) {

                //obj.setField(    Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel()  )     ,  ((StringItem) itmObj).getText()==null?"":((StringItem) itmObj).getText().toUpperCase());

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((StringItem) itmObj).getLabel())).getTypeUI();

                valueStr = ((StringItem) itmObj).getText() == null ? "" : ((StringItem) itmObj).getText().toUpperCase();


            }

            //occio che un DateField è anche uno StringItem
            if (itmObj instanceof DateField) {

                //obj.setField(  Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel()  ) , String.valueOf(   Date_Util.getStamp00000FromDate(((DateField) itmObj).getDate()).longValue()  ));

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((DateField) itmObj).getLabel())).getTypeUI();

                valueStr = String.valueOf(Date_Util.getStamp00000FromDate(((DateField) itmObj).getDate()).longValue());

            }

            if (itmObj instanceof ChoiceGroup) {

                //obj.setField(   Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel()  )     , ((ChoiceGroup) itmObj).getString(((ChoiceGroup) itmObj).getSelectedIndex()));

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((ChoiceGroup) itmObj).getLabel())).getTypeUI();

                valueStr = ((ChoiceGroup) itmObj).getString(((ChoiceGroup) itmObj).getSelectedIndex());


            }

            if (typeField.equals("ANY")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("NUMERIC")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = new Long(Long.parseLong(valueStr));
                } else {
                    value = null;
                }
            } else if (typeField.equals("PASSWORD")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("PHONENUMBER")) {
                 if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            }else if (typeField.equals("listProperties")) {
                 if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            }  else if (typeField.equals("UNEDITABLE")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("EMAILADDR")) {
                 if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("DECIMAL")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = new Double(Double.parseDouble(valueStr));
                } else {
                    value = null;
                }
            } else if (typeField.equals("DATE")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {

                    value = new Long(Long.parseLong(valueStr));
                } else {
                    value = null;
                }
            }

            if (value!=null)
                obj.setField(fieldID, value);

        }

    }

    public boolean setAnagBeanFromListItmsControlModIndex(AnagBean obj, ListItem listItms) throws Exception {

        Item[] itms = listItms.getItems();
        int size = itms.length;
        Object itmObj = null;
        boolean modifiedIndex = false;
        //index fields
        Vector indexFields = ((AnagObj) Session.getListStructureObj().get(obj.getFieldValue("type"))).getListIndexFields();
        String valueStr = null;
        String typeField = null;
        String fieldID = null;
        Object value = null;


        for (int i = 0; i < size; i++) {


            itmObj = itms[i];

            if (itmObj instanceof TextField) {

                //if field is index control if modified
                if (indexFields.contains(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel()))) {

                    if (!((obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel())) == null ? "" : (String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel()))).equals(((TextField) itmObj).getString().toUpperCase()))) {

                        modifiedIndex = true;

                        //#debug info
                        System.out.println("3-Modificato indice: " + ((TextField) itmObj).getLabel());

                    }
                }

                //obj.setField(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel()), ((TextField) itmObj).getString().toUpperCase());

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((TextField) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((TextField) itmObj).getLabel())).getTypeUI();

                valueStr = ((TextField) itmObj).getString().toUpperCase();


            }
            if (itmObj instanceof StringItem) {

                //if field is index control if modified
                if (indexFields.contains(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel()))) {

                    if (!((obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel())) == null ? "" : (String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel()))).equals(((StringItem) itmObj).getText() == null ? "" : ((StringItem) itmObj).getText().toUpperCase()))) {
                        modifiedIndex = true;

                        //#debug info
                        System.out.println("3-Modificato indice: " + ((StringItem) itmObj).getLabel());

                    }
                }

                //obj.setField(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel()), ((StringItem) itmObj).getText() == null ? "" : ((StringItem) itmObj).getText().toUpperCase());

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((StringItem) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((StringItem) itmObj).getLabel())).getTypeUI();

                valueStr = ((StringItem) itmObj).getText() == null ? "" : ((StringItem) itmObj).getText().toUpperCase();


            }

            //occio che un DateField è anche uno StringItem
            if (itmObj instanceof DateField) {

                //#debug info
                System.out.println(((DateField) itmObj).getDate());

                //if field is index control if modified
                if (indexFields.contains(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel()))) {

                    if ((obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel())) == null ? (long) 0 : Long.parseLong((String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel())))) != (((DateField) itmObj).getDate() == null ? (long) 0 : Date_Util.getStampFromDate(((DateField) itmObj).getDate()).longValue())) {
                        modifiedIndex = true;
                        //#debug info
                        System.out.println("3-Modificato indice: " + ((DateField) itmObj).getLabel());
                    }
                }

                //obj.setField(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel()), String.valueOf(Date_Util.getStamp00000FromDate(((DateField) itmObj).getDate()).longValue()));

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((DateField) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((DateField) itmObj).getLabel())).getTypeUI();

                valueStr = String.valueOf(Date_Util.getStamp00000FromDate(((DateField) itmObj).getDate()).longValue());



            }

            if (itmObj instanceof ChoiceGroup) {

                //if field is index control if modified
                if (indexFields.contains(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel()))) {

                    if (!(obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel())) == null ? "" : (String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel()))).equals(((ChoiceGroup) itmObj).getString(((ChoiceGroup) itmObj).getSelectedIndex()))) {
                        modifiedIndex = true;

                        //#debug info
                        System.out.println("3-Modificato indice: " + ((ChoiceGroup) itmObj).getLabel());
                    }
                }

                //obj.setField(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel()), ((ChoiceGroup) itmObj).getString(((ChoiceGroup) itmObj).getSelectedIndex()));

                fieldID = Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(), ((ChoiceGroup) itmObj).getLabel());

                typeField = ((Field) ((AnagObj) Session.getListStructureObj().get(obj.getType())).getListFieldByDescription().get(((ChoiceGroup) itmObj).getLabel())).getTypeUI();

                valueStr = ((ChoiceGroup) itmObj).getString(((ChoiceGroup) itmObj).getSelectedIndex());


            }


            if (typeField.equals("ANY")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("NUMERIC")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = new Long(Long.parseLong(valueStr));
                } else {
                    value = null;
                }
            } else if (typeField.equals("PASSWORD")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("PHONENUMBER")) {
                 if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            }else if (typeField.equals("listProperties")) {
                 if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            }  else if (typeField.equals("UNEDITABLE")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("EMAILADDR")) {
                 if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = (String) valueStr;
                } else {
                    value = null;
                }
            } else if (typeField.equals("DECIMAL")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = new Double(Double.parseDouble(valueStr));
                } else {
                    value = null;
                }
            } else if (typeField.equals("DATE")) {
                if ((valueStr != null) && (!valueStr.equals(""))) {
                    value = new Long(Long.parseLong(valueStr));
                } else {
                    value = null;
                }
            }

            if (value!=null)
                obj.setField(fieldID, value);



        }

        //#debug info
        System.out.println("3b-Oggetto modificato");

        return modifiedIndex;

    }

    public boolean persist(AnagBean obj, ListItem listItms) {

        try {

            //use DAO 
            //use Index Class

            //set new value from form
            this.setAnagBeanFromListItms(obj, listItms);

            obj.setField("lastModified",Date_Util.getStampFromDate(new Date()));
           
            byte[] ser = null;

            int nextRelativeID = Session.getMngIndex().getNextRelativeRecordID((String) obj.getFieldValue("type"));

            int numRMS = 1;

            if (nextRelativeID > Session.getMaxLinesRMS()) {

                numRMS = (int) (nextRelativeID / Session.getMaxLinesRMS()) + (nextRelativeID % Session.getMaxLinesRMS() > 0 ? 1 : 0);

            }

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), true);

            obj.setField("numRec", new Integer(nextRelativeID));

            ser = obj.serialize().getBytes();

            //if can index obj save
            if (Session.getMngIndex().createIndex(obj)) {

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


            } else {
                rms.closeRecordStore();
                return false;
            }



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

    public boolean persist(AnagBean obj) {

        try {

            //use DAO 
            //use Index Class               

            if (!obj.hasField("lastModified"))
                obj.setField("lastModified", Date_Util.getStampFromDate(new Date()));

            byte[] ser = null;

            int nextRelativeID = Session.getMngIndex().getNextRelativeRecordID((String) obj.getFieldValue("type"));

            int numRMS = 1;

            if (nextRelativeID > Session.getMaxLinesRMS()) {

                numRMS = (int) (nextRelativeID / Session.getMaxLinesRMS()) + (nextRelativeID % Session.getMaxLinesRMS() > 0 ? 1 : 0);

            }

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), true);

            obj.setField("numRec", new Integer(nextRelativeID));

            ser = obj.serialize().getBytes();

            //if can index obj save
            if (Session.getMngIndex().createIndex(obj)) {

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


            } else {
                rms.closeRecordStore();
                return false;
            }

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

    public void setAnagBeanFromJsonObj(AnagBean obj, JSONObject jsonObj) {

        try {

            Enumeration en = jsonObj.keys();
            String key = null;

            while (en.hasMoreElements()) {

                key = (String) en.nextElement();

                if (key.equals("sysID")) {
                    obj.setSystemID(new Integer(jsonObj.getInt(key)));
                } else {
                    obj.setField(key, jsonObj.get(key));
                }
//                else if (key.equals("lastModified")) {
//                    obj.setLastMod(new Long(jsonObj.getLong(key)));
//                }
            }

        } catch (Exception e) {

            //#debug error
            System.out.println(e.getMessage());

        }

    }

    public boolean setAnagBeanFromJsonObjControlIndexModified(AnagBean obj, JSONObject jsonObj) {

        boolean modifiedIndex = false;

        try {

            Enumeration en = jsonObj.keys();
            String key = null;

            //index fields
            Vector indexFields = ((AnagObj) Session.getListStructureObj().get(obj.getFieldValue("type"))).getListIndexFields();

            Vector controlledIndexFields = new Vector();

            while (en.hasMoreElements()) {

                key = (String) en.nextElement();

                if (key.equals("sysID")) {
                    obj.setSystemID(new Integer(jsonObj.getInt(key)));
                }  else {

                    //if field is index control if modified
                    if (indexFields.contains(key)) {

                        controlledIndexFields.addElement(key);


                        if (!((obj.getFieldValue(key) == null ? "" : (String) obj.getFieldValueString(key)).equalsIgnoreCase(jsonObj.getString(key)))) {
                            modifiedIndex = true;
                        }
                    }

                    obj.setField(key, jsonObj.get(key));
                }
//                else if (key.equals("lastModified")) {
//                    obj.setLastMod(new Long(jsonObj.getLong(key)));
//                }

            }




        } catch (Exception e) {

            //#debug error
            System.out.println(e.getMessage());

        }

        return modifiedIndex;

    }

    public boolean persist(AnagBean obj, JSONObject jsonObj, boolean sortResultSystemID) {

        try {

            this.setAnagBeanFromJsonObj(obj, jsonObj);

            byte[] ser = null;

            int nextRelativeID = Session.getMngIndex().getNextRelativeRecordID((String) obj.getFieldValue("type"));

            int numRMS = 1;

            if (nextRelativeID > Session.getMaxLinesRMS()) {

                numRMS = (int) (nextRelativeID / Session.getMaxLinesRMS()) + (nextRelativeID % Session.getMaxLinesRMS() > 0 ? 1 : 0);

            }

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), true);

            obj.setField("numRec", new Integer(nextRelativeID));

            ser = obj.serialize().getBytes();

            //if can index obj save
            if (Session.getMngIndex().createIndex(obj)) {

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
                    Session.getMngIndex().addSystemID(obj.getType(), obj.getNumRec(), obj.getSystemID(), obj.getLastMod().longValue(), sortResultSystemID);
                }


                //increment count obj
                Session.getMngIndex().incrementCountRealObj(obj.getType());

            } else {
                rms.closeRecordStore();
                return false;
            }

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

    public boolean commitChange(AnagBean obj, ListItem listItms) {

        try {
            //use DAO 
            //use Index Class
            AnagBean oldAnagBean = null;

            obj.setField("lastModified", Date_Util.getStampFromDate(new Date()));

            //copy old obj
            oldAnagBean = new AnagBean();
            oldAnagBean.unserialize(obj.serialize());

            //#debug info
            System.out.println("1b-Carico modifiche ");

            boolean modified = false;

            modified = setAnagBeanFromListItmsControlModIndex(obj, listItms);


            //if modified title resort
            if (!modified) {

                //remake title
                obj.unserialize(obj.serialize());

                if (!obj.getTitle().equals(oldAnagBean.getTitle())) {
                    modified = true;
                }
            }

            //save modified and control if modificate index obj,if so modified obj index
            if (modified) {

                //#debug info
                System.out.println("5-Lancio cancellazione indici");

                //delete old index
                if (!Session.getMngIndex().deleteIndex(oldAnagBean)) {

                    return false;
                }


                //#debug info
                System.out.println("6-Lancio creazione indici");

                //create new index
                if (!Session.getMngIndex().createIndex(obj)) {

                    return false;
                }

            }


            byte[] ser = null;

            //#debug info
            System.out.println("7-Apro recordordStore: " + (String) obj.getFieldValue("type"));


            int numRMS = Session.getMngIndex().getNumRMS(obj.getNumRec().intValue());

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), false);

            int realRecID = Session.getMngIndex().getRealRecID(obj.getNumRec().intValue(), numRMS);

            //#debug info
            System.out.println("8-Salvo in recordorsotre oggetto modificato: ");

            ser = obj.serialize().getBytes();

            //#debug info
            System.out.println("9-Oggetto serializzato: " + obj.serialize());

            // can save modified obj 
            rms.setRecord(realRecID, ser, 0, ser.length);

            //#debug info
            System.out.println("10-Chiudi rs");

            rms.closeRecordStore();

            //#debug info
            System.out.println("11-rsChiuso .Salvato");


            //if isSync save sync info
            if (obj.isSync()) {
                Session.getMngIndex().commitLastModSystemID(obj.getType(), obj.getSystemID(), obj.getLastMod().longValue());
            }



        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
            try {
                rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());
                return false;
            }
            return false;
        }

        return true;

    }

    public boolean commitChange(AnagBean obj, JSONObject jsonObj) {

        try {
            //use DAO 
            //use Index Class
            AnagBean oldAnagBean = null;           

            //copy old obj
            oldAnagBean = new AnagBean();
            oldAnagBean.unserialize(obj.serialize());

            boolean modified = false;

            modified = setAnagBeanFromJsonObjControlIndexModified(obj, jsonObj);

            if (!obj.hasField("lastModified"))
                obj.setField("lastModified", Date_Util.getStampFromDate(new Date()));


            //if modified title resort
            if (!modified) {

                //remake title
                obj.unserialize(obj.serialize());

                if (!obj.getTitle().equals(oldAnagBean.getTitle())) {
                    modified = true;
                }
            }

            //save modified and control if modificate index obj,if so modified obj index
            if (modified) {

                //#debug info
                System.out.println("5-Lancio cancellazione indici");

                //delete old index
                if (!Session.getMngIndex().deleteIndex(oldAnagBean)) {

                    return false;
                }


                //#debug info
                System.out.println("6-Lancio creazione indici");

                //create new index
                if (!Session.getMngIndex().createIndex(obj)) {

                    return false;
                }

            }


            byte[] ser = null;

            //#debug info
            System.out.println("7-Apro recordordStore: " + (String) obj.getFieldValue("type"));


            int numRMS = Session.getMngIndex().getNumRMS(obj.getNumRec().intValue());

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), false);

            int realRecID = Session.getMngIndex().getRealRecID(obj.getNumRec().intValue(), numRMS);

            //#debug info
            System.out.println("8-Salvo in recordorsotre oggetto modificato: ");

            ser = obj.serialize().getBytes();

            //#debug info
            System.out.println("9-Oggetto serializzato: " + obj.serialize());

            // can save modified obj 
            rms.setRecord(realRecID, ser, 0, ser.length);

            //#debug info
            System.out.println("10-Chiudi rs");

            rms.closeRecordStore();

            //#debug info
            System.out.println("11-rsChiuso .Salvato");


            //if isSync save sync info
            if (obj.isSync()) {
                Session.getMngIndex().commitLastModSystemID(obj.getType(), obj.getSystemID(), obj.getLastMod().longValue());
            }


        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
            try {
                rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());
                return false;
            }
            return false;
        }

        return true;

    }

    public boolean commitChange(AnagBean obj, AnagBean oldobj) {

        try {

            
            obj.setField("lastModified", Date_Util.getStampFromDate(new Date()));
            

            //#debug info
            System.out.println("5-Lancio cancellazione indici");

            //delete old index
            if (!Session.getMngIndex().deleteIndex(oldobj)) {

                return false;
            }


            //#debug info
            System.out.println("6-Lancio creazione indici");

            //create new index
            if (!Session.getMngIndex().createIndex(obj)) {

                return false;
            }


            byte[] ser = null;

            //#debug info
            System.out.println("7-Apro recordordStore: " + (String) obj.getFieldValue("type"));


            int numRMS = Session.getMngIndex().getNumRMS(obj.getNumRec().intValue());

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), false);

            int realRecID = Session.getMngIndex().getRealRecID(obj.getNumRec().intValue(), numRMS);

            //#debug info
            System.out.println("8-Salvo in recordorsotre oggetto modificato: ");

            ser = obj.serialize().getBytes();

            //#debug info
            System.out.println("9-Oggetto serializzato: " + obj.serialize());

            // can save modified obj 
            rms.setRecord(realRecID, ser, 0, ser.length);

            //#debug info
            System.out.println("10-Chiudi rs");

            rms.closeRecordStore();

            //#debug info
            System.out.println("11-rsChiuso .Salvato");

            //if isSync save sync info
            if (obj.isSync()) {
                Session.getMngIndex().commitLastModSystemID(obj.getType(), obj.getSystemID(), obj.getLastMod().longValue());
            }



        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
            try {
                rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());
                return false;
            }
            return false;
        }

        return true;

    }

    public boolean delete(AnagBean obj) {

        try {

            //#debug info
            System.out.println("1-Lancio cancellazione indici");

            //delete old index
            if (!Session.getMngIndex().deleteIndex(obj)) {
                return false;
            }

            //#debug info
            System.out.println("2-Apro recordordStore: " + (String) obj.getFieldValue("type"));

            int numRMS = (int) (obj.getNumRec().intValue() / Session.getMaxLinesRMS()) + (obj.getNumRec().intValue() % Session.getMaxLinesRMS() > 0 ? 1 : 0);

            rms = RecordStore.openRecordStore((String) obj.getFieldValue("type") + "_" + String.valueOf(numRMS), false);

            int realRecID = obj.getNumRec().intValue() - (Session.getMaxLinesRMS() * (numRMS - 1));

            //#debug info
            System.out.println("3-Cancello obj");

            // can save modified obj 
            rms.deleteRecord(realRecID);

            //#debug info
            System.out.println("4-Chiudi rs");

            rms.closeRecordStore();

            //#debug info
            System.out.println("5-Cancellato!");

            //if isSync delete sync info with new lastmod
            if (obj.isSync()) {
                Session.getMngIndex().deleteSystemID(obj.getType(), obj.getSystemID());
            }

            //decrement count obj
            Session.getMngIndex().decrementCountRealObj(obj.getType());

        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
            try {
                rms.closeRecordStore();
            } catch (Exception e2) {
                //#debug error
                System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());
                return false;
            }
            return false;
        }

        return true;

    }

    public AnagBean getAnagBeanFromNumRec(String type, int numRec) {

        AnagBean obj = new AnagBean();

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

            obj.unserialize(str);

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

        return obj;
    }

    public String getSerializedAnagBeanFromNumRec(String type, int numRec) {

        String str = null;

        try {

            //#debug info
            System.out.println("2-Apro recordordStore: " + type);

            int numRMS = (int) (numRec / Session.getMaxLinesRMS()) + (numRec % Session.getMaxLinesRMS() > 0 ? 1 : 0);

            rms = RecordStore.openRecordStore(type + "_" + String.valueOf(numRMS), false);

            int realRecID = numRec - (Session.getMaxLinesRMS() * (numRMS - 1));

            int sizeRecData = rms.getRecordSize(realRecID);

            byte[] recData = new byte[sizeRecData];

            int dataLen = rms.getRecord(realRecID, recData, 0);

            str = (new String(recData, 0, dataLen)).trim();

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

        return str;
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

            if (jso.has("title")) {
                title = jso.getString("title");
            } else {
                //#debug error
                System.out.println("No title for obj: " + str);
            }

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
}
