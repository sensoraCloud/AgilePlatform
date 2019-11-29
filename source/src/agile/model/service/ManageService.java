/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile.model.service;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.DateField;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ListItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import agile.control.Event;
import agile.json.me.JSONObject;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.DocBean;
import agile.model.structure.Field;
import agile.model.structure.Index;
import agile.model.structure.Property;
import agile.model.structure.ResponseService;
import agile.model.structure.Structure;
import agile.model.thread.ManageThread;
import agile.print.LayoutCompiler;
import agile.session.Session;
import agile.util.Date_Util;
import agile.util.Search_Util;
import agile.util.String_Util;
import agile.workflow.ManageWorkFlow;

/**
 *
 * @author ruego
 */
public class ManageService {

    //list ServiceID  
    public static final int UPDATE_ANAGBEANOBJ_SERVICE = 1;
    public static final int SAVE_NEW_ANAGBEANOBJ_SERVICE = 2;
    public static final int THREAD_FORCELOADDATA_SERVICE = 3;
    public static final int SEARCH_GROUPVALUE_SERVICE = 4;
    public static final int SEARCH_GROUPOBJS_SERVICE = 5;
    public static final int DO_ONCHANGEITEM_METOD_SERVICE = 6;
    public static final int SORT_PAGESVALUE_SERVICE = 7;
    public static final int SORT_SEARCHRESULTS_SERVICE = 8;
    public static final int SEARCH_OBJ_SERVICE = 9;
    public static final int ADD_NEW_ROWDOC_FROM_ANAGBEAN = 11;
    public static final int DUPLICATE_DOC_SERVICE = 12;
    public static final int DELETE_DOC_SERVICE = 13;
    public static final int ADD_EDIT_NEWROWDOCBEAN_SERVICE = 14;
    public static final int CANCEL_UPDATE_DOCBEAN_SERVICE = 15;
    public static final int DO_ACTIVITY_SERVICE = 16;
    public static final int SEND_HTTP_OBJ_SERVICE = 17;
    public static final int SYNC_HTTP_OBJs_SERVICE = 18;
    public static final int GET_URL_CONNECTION_SERVICE = 19;
    public static final int GET_FREE_MEMORY_SERVICE = 20;
    public static final int DELETE_ALL_OBJS_SERVICE = 21;
    public static final int PRINT_DOC_SERVICE = 22;
    public static final int GET_ANAGOBJS_BYQUERY_INDEXEDFIELDS = 23;
    public static final int GET_TYPEUI_OF_INDEXFIELD_BYTYPEOBJ = 24;
    private ResponseService responce = null;
    private Event e = null;

    //work also with ManageThread
    public ResponseService doService(int serviceID, Hashtable params) {

        Hashtable prms = null;
        AnagBean anagBeanobj = null;
        ListItem listItms = null;
        responce = new ResponseService();
        boolean response = false;

        switch (serviceID) {

            case SAVE_NEW_ANAGBEANOBJ_SERVICE:

                //add new THREAD_PERSIST_ANAGBEAN thread
                Session.getMngThread().addThread(ManageThread.THREAD_PERSIST_ANAGBEAN);

                //start thread
                response = Session.getMngThread().startThread(ManageThread.THREAD_PERSIST_ANAGBEAN, params, "gaugeItemPercentade");

                responce.setStatus(response);

                break;

            case UPDATE_ANAGBEANOBJ_SERVICE:

                //add new THREAD_COMMIT_ANAGBEAN thread
                Session.getMngThread().addThread(ManageThread.THREAD_COMMIT_ANAGBEAN);

                //start thread
                response = Session.getMngThread().startThread(ManageThread.THREAD_COMMIT_ANAGBEAN, params, "gaugeItemPercentade");

                responce.setStatus(response);

                break;


            case THREAD_FORCELOADDATA_SERVICE:

                //add new THREAD_FORCELOADDATA thread
                Session.getMngThread().addThread(ManageThread.THREAD_FORCELOADDATA);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_FORCELOADDATA, params, "gaugeItemIndefinite");


                break;


            case SEARCH_GROUPOBJS_SERVICE:

                //add new THREAD_FORCELOADDATA thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_GETPAGINATEGROUP_OBJS);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_GETPAGINATEGROUP_OBJS, params, "gaugeItemPercentade");


                break;


            case SEARCH_GROUPVALUE_SERVICE:

                //add new THREAD_FORCELOADDATA thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_GETPAGINATELIST_VALUE);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_GETPAGINATELIST_VALUE, params, "gaugeItemPercentade");


                break;

            case DO_ONCHANGEITEM_METOD_SERVICE:

                //"BuildPrice(Qta,Prezzo,Sconto,TotPrezzo)"
                String metod = (String) params.get("command");
                Hashtable fields = (Hashtable) params.get("fields");
                AnagBean obj = (AnagBean) params.get("obj");
                String typeObj = (String) params.get("typeObj");

                int index = metod.indexOf("(");
                String nameMetod = metod.substring(0, index);
                String parameters = TextUtil.replace(metod.substring(index + 1), ")", "");
                String[] vetPrms = TextUtil.split(parameters, ',');

                if (nameMetod.equals("BuildPrice")) {

                    String qta = null;
                    String price = null;
                    String sconto = null;

                    if (fields != null) {

                        qta = this.getFieldValueFromItem((Item) fields.get( Session.getMngConfig().getAnagObjFieldIDbyDescription(typeObj,vetPrms[0]) ));
                        price = this.getFieldValueFromItem((Item) fields.get(  Session.getMngConfig().getAnagObjFieldIDbyDescription(typeObj,vetPrms[1])   )  );
                        sconto = this.getFieldValueFromItem((Item) fields.get(Session.getMngConfig().getAnagObjFieldIDbyDescription(typeObj,vetPrms[2])));

                    } else if (obj != null) {

                        qta = (String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(),vetPrms[0]));
                        price = (String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(),vetPrms[1]));
                        sconto = (String) obj.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(),vetPrms[2]));

                    }

                    double prezzoScontato = 0;

                    if ((qta != null) && (!qta.equals("")) && (price != null) && (!price.equals(""))) {

                        qta = TextUtil.replace(qta, ",", ".");
                        price = TextUtil.replace(price, ",", ".");

                        double totPrezzo = 0;

                        totPrezzo = Double.parseDouble(qta) * Double.parseDouble(price);

                        if ((sconto != null) && (!sconto.equals(""))) {
                            double scont = (totPrezzo * Double.parseDouble(sconto)) / 100;
                            prezzoScontato = totPrezzo - scont;
                        } else {
                            prezzoScontato = totPrezzo;
                        }


                    }
                    //save result

                    if (fields != null) {
                        this.setItemValue((Item) fields.get(  Session.getMngConfig().getAnagObjFieldIDbyDescription(typeObj,vetPrms[3])  ), String_Util.getStrDouble2Dec(prezzoScontato));
                    }

                    if (obj != null) {
                        obj.setField(Session.getMngConfig().getAnagObjFieldIDbyDescription(obj.getType(),vetPrms[3]), new Double( Double.parseDouble(String_Util.getStrDouble2Dec(prezzoScontato).replace(',', '.')))   );
                    }

                }


                break;

            case SORT_PAGESVALUE_SERVICE:

                params.put("callForm", "PageListValue");

                //add new THREAD_SORT_PAGESVALUE thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_SORT_PAGESVALUE);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_SORT_PAGESVALUE, params, "gaugeItemPercentade");


                break;

            case SORT_SEARCHRESULTS_SERVICE:


                params.put("callForm", "SearchResults");

                //add new THREAD_SORT_PAGESVALUE thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_SORT_PAGESVALUE);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_SORT_PAGESVALUE, params, "gaugeItemPercentade");


                break;


            case SEARCH_OBJ_SERVICE:

                //add new THREAD_SEARCH_OBJ thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_SEARCH_OBJ);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_SEARCH_OBJ, params, null);


                break;



            case ADD_NEW_ROWDOC_FROM_ANAGBEAN:

                //add new THREAD_ADD_NEW_ROWDOC_FROM_ANAGBEAN thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_ADD_NEW_ROWDOC_FROM_ANAGBEAN);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_ADD_NEW_ROWDOC_FROM_ANAGBEAN, params, "gaugeItemPercentade");


                break;

            case DUPLICATE_DOC_SERVICE:

                //add new THREAD_DUPLICATE_DOC thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_DUPLICATE_DOC);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_DUPLICATE_DOC, params, "gaugeItemPercentade");


                break;

            case DELETE_DOC_SERVICE:

                //add new THREAD_DELETE_DOC thread
                response = Session.getMngThread().addThread(ManageThread.THREAD_DELETE_DOC);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_DELETE_DOC, params, "gaugeItemPercentade");


                break;

            case ADD_EDIT_NEWROWDOCBEAN_SERVICE:

                //add new THREAD_ADD_EDIT_NEWROWDOCBEAN thread
                Session.getMngThread().addThread(ManageThread.THREAD_ADD_EDIT_NEWROWDOCBEAN);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_ADD_EDIT_NEWROWDOCBEAN, params, "gaugeItemPercentade");


                break;

            case CANCEL_UPDATE_DOCBEAN_SERVICE:

                //add new THREAD_CANCEL_UPDATE_DOCBEAN thread
                Session.getMngThread().addThread(ManageThread.THREAD_CANCEL_UPDATE_DOCBEAN);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_CANCEL_UPDATE_DOCBEAN, params, "gaugeItemPercentade");


                break;

            case DO_ACTIVITY_SERVICE:

                Integer activityID = (Integer) params.get("activityID");
                // Vector listObj=(Vector) params.get("listObj") ;

                switch (activityID.intValue()) {

                    case ManageWorkFlow.SEND_TO_SERVER_ACTIVITY:


                        this.doService(ManageService.SEND_HTTP_OBJ_SERVICE, params);


                        break;
                }

                break;

            case SEND_HTTP_OBJ_SERVICE:

                //add new THREAD_SEND_HTTP_DOC thread
                Session.getMngThread().addThread(ManageThread.THREAD_SEND_HTTP_DOC);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_SEND_HTTP_DOC, params, "gaugeItemIndefinite");


                break;

            case SYNC_HTTP_OBJs_SERVICE:

                //add new THREAD_SYNC_OBJS_HTTP thread
                Session.getMngThread().addThread(ManageThread.THREAD_SYNC_OBJS_HTTP);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_SYNC_OBJS_HTTP, params, "gaugeItemIndefinite");


                break;

            case GET_URL_CONNECTION_SERVICE:

                try {

                    RecordStore rs = RecordStore.openRecordStore("admin_connection", false);

                    byte[] urlByte = rs.getRecord(1);

                    rs.closeRecordStore();

                    responce.setStatus(true);
                    responce.setResponseObj(new String(urlByte));


                } catch (RecordStoreException ex) {
                    //#debug info
                    System.out.println("url not exist!");
                    responce.setStatus(false);
                    responce.setResponseObj(null);
                }

                break;

            case GET_FREE_MEMORY_SERVICE:

                try {

                    RecordStore rs = RecordStore.openRecordStore("free_memory", true);

                    String freeMem = String.valueOf(rs.getSizeAvailable());

                    rs.closeRecordStore();

                    RecordStore.deleteRecordStore("free_memory");

                    responce.setStatus(true);
                    responce.setResponseObj(freeMem);


                } catch (RecordStoreException ex) {
                    //#debug info
                    System.out.println("Exception " + ex.getMessage());
                    responce.setStatus(false);
                    responce.setResponseObj(null);
                }

                break;


            case DELETE_ALL_OBJS_SERVICE:

                //add new THREAD_DELETE_ALL_OBJS thread
                Session.getMngThread().addThread(ManageThread.THREAD_DELETE_ALL_OBJS);

                //start thread
                Session.getMngThread().startThread(ManageThread.THREAD_DELETE_ALL_OBJS, params, "gaugeItemPercentade");


                break;

            case PRINT_DOC_SERVICE:

                DocBean doc = (DocBean) params.get("docbean");

                LayoutCompiler ROWS = new LayoutCompiler("/rowLayout.txt");
                StringBuffer rowsContainer = new StringBuffer();

                int sizeRow = doc.getRows().size();

                AnagBean row = null;
                Enumeration en = null;
                String key = null;
                Object value = null;

                for (int h = 0; h < sizeRow; h++) {

                    row = doc.getRow(h);

                    //compile row
                    en = row.getFieldsValue().keys();

                    while (en.hasMoreElements()) {

                        key = (String) en.nextElement();

                        value = row.getFieldsValue().get(key);


                        if (value instanceof Integer) {
                            value = String.valueOf(((Integer) value).intValue());
                        } else if (value instanceof Double) {
                            value = String.valueOf(((Double) value).doubleValue());
                        } else if (value instanceof Long) {
                            value = String.valueOf(((Long) value).longValue());
                        } else if (value instanceof String) {
                            value = (String) value;
                        } else if (value instanceof Boolean) {
                            value = String.valueOf(((Boolean) value).booleanValue());
                        }

                        ROWS.add(key, ((String) value).trim(), 30);

                    }

                    try {
                        rowsContainer.append(ROWS.parse());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    ROWS.reset();

                }

                LayoutCompiler LC = new LayoutCompiler("/orderLayout.txt");

                LC.add("ORDER_ROWS", rowsContainer.toString());
                LC.add("ORDER_NUMBER", (doc.getSystemID() == null ? "0" : String.valueOf(doc.getSystemID().intValue())));
                LC.add("TYPE_OBJ", doc.getType());

                AnagBean head = doc.getHeadObj();

                //compile row
                en = head.getFieldsValue().keys();

                while (en.hasMoreElements()) {

                    key = (String) en.nextElement();

                    value = head.getFieldsValue().get(key);

                    if (value instanceof Integer) {
                        value = String.valueOf(((Integer) value).intValue());
                    } else if (value instanceof Double) {
                        value = String.valueOf(((Double) value).doubleValue());
                    } else if (value instanceof Long) {
                        value = String.valueOf(((Long) value).longValue());
                    } else if (value instanceof String) {
                        value = (String) value;
                    } else if (value instanceof Boolean) {
                        value = String.valueOf(((Boolean) value).booleanValue());
                    }

                    LC.add(key, ((String) value).trim(), 30);
                }


                try {

                    String toPrintStr = LC.parse();

                } catch (Exception e) {
                }

                break;


            case GET_ANAGOBJS_BYQUERY_INDEXEDFIELDS:

                String typeAnagObj = (String) params.get("typeAnagObj");
                String indxFld1 = (String) params.get("indxFld1");
                String indxFld1Value = (String) params.get("indxFld1Value");
                String indxFld2 = (String) params.get("indxFld2");
                String indxFld2Value = (String) params.get("indxFld2Value");

                responce.setStatus(true);
                responce.setResponseObj(this.getAnagObjsFromIndexedFields(typeAnagObj, indxFld1, indxFld1Value, indxFld2, indxFld2Value));

                break;

            case GET_TYPEUI_OF_INDEXFIELD_BYTYPEOBJ:

                String typeObj2 = (String) params.get("typeObj");
                String indexFld = (String) params.get("indexFld");

                String typeUIindexFld = (String) ((Structure) Session.getListStructureObj().get(typeObj2)).getListTypeUIIndexFields().elementAt(((Structure) Session.getListStructureObj().get(typeObj2)).getListIndexFields().indexOf(indexFld));

                if (typeUIindexFld != null) {

                    responce.setStatus(true);
                    responce.setResponseObj(typeUIindexFld);

                } else {

                    responce.setStatus(false);
                    responce.setResponseObj(null);

                }

                break;




        }

        return responce;
    }

    public Vector getAnagObjsFromIndexedFields(String typeAnagObj, String indxFld1, String indxFld1Value, String indxFld2, String indxFld2Value) {

        Vector objs = new Vector();

        try {

            if ((indxFld1 == null) || (indxFld1Value == null)) {
                return null;
            }

            Vector listNumRecsFld1 = null;
            Vector listNumRecsFld2 = null;

            Vector listTrueNumRecs = null;

//            Hashtable params = new Hashtable(2);
//
//            params.put("typeObj", typeAnagObj);
//            params.put("indexFld", indxFld1);
//
//            ResponseService resp = this.doService(ManageService.GET_TYPEUI_OF_INDEXFIELD_BYTYPEOBJ, params);
//
//            String typeUIFld1 = (String) resp.getResponseObj();
//
//            String typeUIFld2 = null;
//
//            if (indxFld2 != null) {
//
//                params.put("typeObj", typeAnagObj);
//                params.put("indexFld", indxFld2);
//
//                resp = this.doService(ManageService.GET_TYPEUI_OF_INDEXFIELD_BYTYPEOBJ, params);
//
//                typeUIFld2 = (String) resp.getResponseObj();
//
//            }

            //calcolo numRecs che soddisfano il primo campo

            //scarico l'hashtable degli indici
            Hashtable hashIndexObj = ((Hashtable) Session.getMngIndex().getHashIndex().get(typeAnagObj + "_" + indxFld1));

            //se contiene il valore cercato scarico l'indice altrimenti torno null
            if ((hashIndexObj != null) && (hashIndexObj.containsKey(indxFld1Value))) {

                //take rms numRec for field index value
                int numRecIndexRms = ((Integer) hashIndexObj.get(indxFld1Value)).intValue();

                int numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                RecordStore rms = RecordStore.openRecordStore(typeAnagObj + "_" + indxFld1 + "_" + String.valueOf(numRMS), false);

                int realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);

                int sizeRecData = rms.getRecordSize(realRecID);

                byte[] recData = new byte[sizeRecData];

                int dataLen = rms.getRecord(realRecID, recData, 0);

                String str = (new String(recData, 0, dataLen)).trim();

                rms.closeRecordStore();

                Index idx = new Index();

                idx.unserialize(str);

                listNumRecsFld1 = idx.getIndexs();

            } else {

                return null;
            }

            if (listNumRecsFld1.size() == 0) {
                return null;
            }

            listTrueNumRecs = listNumRecsFld1;

            //se ha trovato piu di un record ed ha settato anche un secondo field faccio matching altrimenti ritorno solo valori trovati
            if ((listNumRecsFld1.size() > 0) && (indxFld2 != null)) {

                //calcolo numRecs che soddisfano il secondo campo

                //scarico l'hashtable degli indici
                hashIndexObj = ((Hashtable) Session.getMngIndex().getHashIndex().get(typeAnagObj + "_" + indxFld2));

                //se contiene il valore cercato scarico l'indice altrimenti torno null
                if ((hashIndexObj != null) && (hashIndexObj.containsKey(indxFld2Value))) {

                    //take rms numRec for field index value
                    int numRecIndexRms = ((Integer) hashIndexObj.get(indxFld2Value)).intValue();

                    int numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                    RecordStore rms = RecordStore.openRecordStore(typeAnagObj + "_" + indxFld2 + "_" + String.valueOf(numRMS), false);

                    int realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);

                    int sizeRecData = rms.getRecordSize(realRecID);

                    byte[] recData = new byte[sizeRecData];

                    int dataLen = rms.getRecord(realRecID, recData, 0);

                    String str = (new String(recData, 0, dataLen)).trim();

                    rms.closeRecordStore();

                    Index idx = new Index();

                    idx.unserialize(str);

                    listNumRecsFld2 = idx.getIndexs();

                } else {

                    return null;
                }

                if (listNumRecsFld2.size() == 0) {
                    return null;
                } //se ha trovato dei valori faccio il matching dei numRecs trovati per il campo 1
                else {

                    listTrueNumRecs = Search_Util.getMatchingIntegerVector(listTrueNumRecs, listNumRecsFld2);

                }

            }

            int sizeIndexs = listTrueNumRecs.size();

            Hashtable openedRMS = new Hashtable();
            JSONObject jso = null;
            RecordStore rmsObj = null;
            int numRecObj = 0;
            int numRMS = 0;
            int realRecID = 0;
            int sizeRecData = 0;
            byte[] recData = null;
            int dataLen = 0;
            String str = null;
            AnagBean obj = null;

            //carico gli oggetti corrispondenti

            objs = new Vector(sizeIndexs);

            //get all obj by index
            for (int d = 0; d < sizeIndexs; d++) {


                numRecObj = ((Integer) listTrueNumRecs.elementAt(d)).intValue();

                numRMS = Session.getMngIndex().getNumRMS(numRecObj);

                if (!openedRMS.containsKey(new Integer(numRMS))) {

                    rmsObj = RecordStore.openRecordStore(typeAnagObj + "_" + String.valueOf(numRMS), false);
                    openedRMS.put(new Integer(numRMS), rmsObj);

                }

                realRecID = Session.getMngIndex().getRealRecID(numRecObj, numRMS);

                sizeRecData = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecordSize(realRecID);

                recData = new byte[sizeRecData];

                dataLen = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecord(realRecID, recData, 0);

                str = (new String(recData, 0, dataLen)).trim();

                jso = new JSONObject(str);

                obj = new AnagBean(typeAnagObj);

                obj.unserialize(str);

                objs.addElement(obj);

            }


        } catch (Exception e2) {

            //#debug error
            System.out.println(e2.getMessage() == null ? "null" : e2.getMessage());

            return null;
        }

        return objs;
    }

    public Vector getListValueOfIndexedField(String typeAnagObj, String indxFld) {

        Vector values = new Vector();

        try {

            if ((indxFld == null) || (typeAnagObj == null)) {
                return null;
            }

            //scarico l'hashtable degli indici
            Hashtable hashIndexObj = ((Hashtable) Session.getMngIndex().getHashIndex().get(typeAnagObj + "_" + indxFld));

            if (hashIndexObj != null) {

                Enumeration en = hashIndexObj.keys();

                while (en.hasMoreElements()) {

                    values.addElement((String) en.nextElement());
                }

            }


        } catch (Exception e2) {

            //#debug error
            System.out.println(e2.getMessage() == null ? "null" : e2.getMessage());

            return null;
        }

        return values;
    }

    public String getFieldValueFromItem( Object itmObj) {

        if (itmObj instanceof TextField) {

            return ((TextField) itmObj).getString().toUpperCase();

        }

        if (itmObj instanceof StringItem) {

            return ((StringItem) itmObj).getText() == null ? "" : ((StringItem) itmObj).getText().toUpperCase();

        }

        if (itmObj instanceof DateField) {

            return String.valueOf(Date_Util.getStampFromDate(((DateField) itmObj).getDate()).longValue());

        }

        if (itmObj instanceof ChoiceGroup) {

            return ((ChoiceGroup) itmObj).getString(((ChoiceGroup) itmObj).getSelectedIndex());

        }

        return null;

    }

    public void setItemValue(Object itmObj, String value) {

        if (itmObj instanceof TextField) {

            ((TextField) itmObj).setString(value.toUpperCase());

        }

        if (itmObj instanceof StringItem) {

            ((StringItem) itmObj).setText(value.toUpperCase());

        }

        if (itmObj instanceof DateField) {

            ((DateField) itmObj).setDate(Date_Util.getDatefromStamp(Long.parseLong(value)));
        }

        if (itmObj instanceof ChoiceGroup) {

            ((ChoiceGroup) itmObj).setSelectedIndex(Integer.parseInt(value), true);

        }

    }

    //serve per creare dinamicamente il nome giusto del campo da usare per scaricare il valore desiderato
    public static String getNameField(String parameter, AnagBean compileAnagBean, DocBean currentDocBean) {

        String nameField = "";
        String[] opzs = null;
        int index = 0;
        String nameMetod = null;
        String param = null;
        String[] vals = null;

        //Prezzo_+Val(Ordine.Listino) or Sconto

        //find + operation
        opzs = TextUtil.split(parameter, '+');

        nameField = "";

        for (int c = 0; c < opzs.length; c++) {

            index = opzs[c].indexOf("(");

            //if have metod
            if (index > 0) {

                nameMetod = opzs[c].substring(0, index);
                param =
                        TextUtil.replace(opzs[c].substring(index + 1), ")", "");

                if (nameMetod.equals("Val")) {

                    //Ordine.Listino or Prodotto.Prezzo or Prezzo
                    vals = TextUtil.split(param, '.');

                    if (vals.length > 1) {

                        if (vals[0].equals(compileAnagBean.getType())) {
                            //Prodotto.Prezzo
                            nameField = nameField + compileAnagBean.getFieldValueString( Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),vals[1])  );

                        //} else if ((currentDocBean != null) && (vals[0].equals(currentDocBean.getType()))) {
                        } else if ((currentDocBean != null) && (vals[0].equals("Testata"))) {

                            //Ordine.Listino modificato in Testata.Listino (piu generico)
                            nameField = nameField + currentDocBean.getHeadObj().getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(currentDocBean.getHeadObj().getType(),vals[1]));

                        }

                    } else {

                        //Prezzo
                        nameField = nameField + compileAnagBean.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),vals[0]));

                    }

                }

            } else {

                nameField = nameField + opzs[c];

            }

        }

        return nameField;

    }

    public AnagBean getNewAnaBeanFromMatchingSourceAnagBean(String typeObj, AnagBean compileAnagBean) {

        AnagBean newObj = new AnagBean(typeObj);

        try {

            //loop on obj fields
            Vector listfields = ((AnagObj) Session.getListStructureObj().get(typeObj)).getListFields();

            Field fld = null;
            long size = listfields.size();

            for (int j = 0; j < size; j++) {

                fld = (Field) listfields.elementAt(j);

                //security  control
                if (fld.getWriteSecurityLevel().intValue() > Session.getActiveUser().getSecurityLevelWrite()) {

                    newObj.setField(fld.getId(), Locale.get("message.notautorizedtowrite"));


                } else {

                    if (compileAnagBean.getFieldValue(fld.getId()) != null) {
                        newObj.setField(fld.getId(), compileAnagBean.getFieldValue(fld.getId()));
                    }

                }

            }


        } catch (Exception e2) {
            //#debug error
            System.out.println(e2.getMessage() == null ? "null" : e2.getMessage());

            return null;
        }

        return newObj;

    }

    public AnagBean getNewAnagBeanFromSourceAnagBean(String typeObj, AnagBean compileAnagBean, DocBean currentDocBean) {

        Hashtable onChangeMetodFields = new Hashtable();
        AnagBean newObj = new AnagBean(typeObj);


        try {

            //loop on obj fields
            Vector listfields = ((AnagObj) Session.getListStructureObj().get(typeObj)).getListFields();

            Field fld = null;
            long size = listfields.size();
            Vector props = null;

            Hashtable params = null;

            String value = null;

            for (int j = 0; j < size; j++) {

                fld = (Field) listfields.elementAt(j);

                //put field for onChange metods..
                if (fld.hasProperty("onChange")) {
                    onChangeMetodFields.put(fld.getId(), fld.getPropertyByKey("onChange"));
                }


                //security  control
                if (fld.getWriteSecurityLevel().intValue() > Session.getActiveUser().getSecurityLevelWrite()) {

                    newObj.setField(fld.getId(), Locale.get("message.notautorizedtowrite"));


                } else {

                  
                    value = this.getFieldValueFromLinkedToExternalFieldorSetDefault(fld, compileAnagBean, currentDocBean);

                    if (value != null) {                    

                        if ((fld.getTypeUI().equals("ANY")) || (fld.getTypeUI().equals("UNEDITABLE")) || (fld.getTypeUI().equals("EMAILADDR")) || (fld.getTypeUI().equals("listProperties"))|| (fld.getTypeUI().equals("PHONENUMBER"))|| (fld.getTypeUI().equals("PASSWORD"))) {
                            newObj.setField(fld.getId(), value);
                        }

                        if ((fld.getTypeUI().equals("NUMERIC"))  ||(fld.getTypeUI().equals("DATE"))) {
                            newObj.setField(fld.getId(), new Long(Long.parseLong(value)));
                        }

                        if ((fld.getTypeUI().equals("DECIMAL"))) {
                            newObj.setField(fld.getId(), new Double(Double.parseDouble(value)));
                        }

                       
                    } else {

                        if (fld.getTypeUI().equals("DATE")) {
                            newObj.setField(fld.getId(), Date_Util.getStampFromDate(new Date()));
                        }

                        if ((fld.getTypeUI().equals("ANY")) || (fld.getTypeUI().equals("UNEDITABLE")) || (fld.getTypeUI().equals("EMAILADDR")) || (fld.getTypeUI().equals("listProperties"))|| (fld.getTypeUI().equals("PASSWORD")) || (fld.getTypeUI().equals("PHONENUMBER"))) {
                            newObj.setField(fld.getId(), "");
                        }

                        if ((fld.getTypeUI().equals("NUMERIC")) ) {
                            newObj.setField(fld.getId(), new Long(0));
                        }

                        if ((fld.getTypeUI().equals("DECIMAL"))) {
                            newObj.setField(fld.getId(), new Double(0));
                        }


                    }


                }

            }

            //do onchangeMetod per risettare in modo corretto dei campi legati (esempio:ricalcolo prezzo totale riga..)
            Enumeration en = onChangeMetodFields.keys();
            params = null;

            while (en.hasMoreElements()) {

                String key = (String) en.nextElement();
                params = new Hashtable(2);
                props = (Vector) onChangeMetodFields.get(key);
                params.put("command", ((Property) props.elementAt(0)).getValue());
                params.put("obj", newObj);

                this.doService(ManageService.DO_ONCHANGEITEM_METOD_SERVICE, params);

            }

        } catch (Exception e2) {
            //#debug error
            System.out.println(e2.getMessage() == null ? "null" : e2.getMessage());

        }

        return newObj;

    }

    public String getFieldValueFromLinkedToExternalFieldorSetDefault(Field fld, AnagBean compileAnagBean, DocBean compileDocBean) {

        String value = null;

        try {


            Vector props = null;
            Property pr = null;
            int sizePrps = 0;

            String nameField = null;
            int index = 0;
            String firstPart = null;
            String secondPart = null;

            boolean setted = false;

            Hashtable params = null;

            //precompilo...
            if (compileAnagBean != null) {

                props = fld.getPropertyByKey("linkedTOField");

                sizePrps = props == null ? 0 : props.size();

                for (int p = 0; p < sizePrps; p++) {

                    pr = (Property) props.elementAt(p);

                    //Fornitore.Denominazione (i due oggetti son anagrafici) or Prodotto.Prezzo_+Val(Ordine.Listino) (deve creare il nome del campo corretto) or Ordine.Sconto (un oggetto è anagrafico e l'altro documento) or search:Prezzo.Listino.CodList=Ordine.Listino;Listino.CodProd=Prodotto.Codice (deve ricercare il valore corretto)

                    index = ((String) pr.getValue()).indexOf(".");
                    //fino al primo punto
                    firstPart = ((String) pr.getValue()).substring(0, index);
                    //dal secondo punto in poi
                    secondPart = ((String) pr.getValue()).substring(index + 1);

                    //search:Prezzo
                    if ((TextUtil.split(firstPart, ':').length > 0) && (TextUtil.split(firstPart, ':')[0].equals("search"))) {

                        //lancio ricerca valore
                        params = new Hashtable(4);

                        //Listino.CodList=Ordine.Listino;Listino.CodProd=Prodotto.Codice

                        //Listino.CodList=Ordine.Listino modificato in--> Testata.Listino (piu generico)
                        String firstSearch = TextUtil.split(secondPart, ';')[0];

                        //Listino.CodProd=Prodotto.Codice
                        String secondSearch = (TextUtil.split(secondPart, ';').length > 1) ? TextUtil.split(secondPart, ';')[1] : null;

                        //Listino
                        params.put("typeAnagObj", TextUtil.split(TextUtil.split(firstSearch, '=')[0], '.')[0] );
                        //CodList
                        params.put("indxFld1",  Session.getMngConfig().getAnagObjFieldIDbyDescription(TextUtil.split(TextUtil.split(firstSearch, '=')[0], '.')[0],TextUtil.split(TextUtil.split(firstSearch, '=')[0], '.')[1] )  );
                        //Value Ordine.Listino modificato in Testata.Listino (piu generico)
                        //ci son due casi o prendo un campo del documento o un campo della riga ( Prodotto. or Ordine.  modificato in Testata )
                        if (TextUtil.split(TextUtil.split(firstSearch, '=')[1], '.')[0].equals(compileAnagBean.getType())) {
                                                   
                            //calcolo in nome del campo in caso ci fosse qualche metodo tipo Prodotto.Prezzo_+Val(Testata.Listino)
                            nameField = getNameField(TextUtil.split(TextUtil.split(firstSearch, '=')[1], '.')[1], compileAnagBean, null);

                            if (compileAnagBean.hasField(  Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),nameField)  )) {

                                params.put("indxFld1Value", (String) compileAnagBean.getFieldValueString( Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),nameField)    ));

                            }

                        }//Ordine.Sconto Testata.Sconto
                        //else if ((compileDocBean != null) && (TextUtil.split(TextUtil.split(firstSearch, '=')[1], '.')[0].equals(compileDocBean.getType()))) {
                        else if ((compileDocBean != null) && (TextUtil.split(TextUtil.split(firstSearch, '=')[1], '.')[0].equals("Testata"))) {

                            nameField = getNameField(TextUtil.split(TextUtil.split(firstSearch, '=')[1], '.')[1], compileAnagBean, compileDocBean);

                            if (compileDocBean.getHeadObj().hasField(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileDocBean.getHeadObj().getType(),nameField))) {

                                params.put("indxFld1Value", (String) compileDocBean.getHeadObj().getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileDocBean.getHeadObj().getType(),nameField)));

                            }

                        }

                        //CodProd
                        //params.put("indxFld2", TextUtil.split(TextUtil.split(secondSearch, '=')[0], '.')[1]);
                        params.put("indxFld2",  Session.getMngConfig().getAnagObjFieldIDbyDescription(TextUtil.split(TextUtil.split(secondSearch, '=')[0], '.')[0],TextUtil.split(TextUtil.split(secondSearch, '=')[0], '.')[1] )  );
                   
                        //Value of Prodotto.Codice
                        if (TextUtil.split(TextUtil.split(secondSearch, '=')[1], '.')[0].equals(compileAnagBean.getType())) {

                            //calcolo in nome del campo in caso ci fosse qualche metodo tipo Prodotto.Prezzo_+Val(Ordine.Listino)
                            nameField = getNameField(TextUtil.split(TextUtil.split(secondSearch, '=')[1], '.')[1], compileAnagBean, null);

                            if (compileAnagBean.hasField(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),nameField))) {

                                params.put("indxFld2Value", (String) compileAnagBean.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),nameField)));

                            }

                        }//Ordine.Sconto Testata.Sconto
                        //else if ((compileDocBean != null) && (TextUtil.split(TextUtil.split(secondSearch, '=')[1], '.')[0].equals(compileDocBean.getType()))) {
                        else if ((compileDocBean != null) && (TextUtil.split(TextUtil.split(secondSearch, '=')[1], '.')[0].equals("Testata"))) {

                            nameField = getNameField(TextUtil.split(TextUtil.split(secondSearch, '=')[1], '.')[1], compileAnagBean, compileDocBean);

                            if (compileDocBean.getHeadObj().hasField(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileDocBean.getHeadObj().getType(),nameField))) {

                                params.put("indxFld2Value", (String) compileDocBean.getHeadObj().getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileDocBean.getHeadObj().getType(),nameField)));

                            }

                        }

                        //lancio la query
                        ResponseService resp = this.doService(this.GET_ANAGOBJS_BYQUERY_INDEXEDFIELDS, params);

                        //dovrebbe ritornare un unico oggetto in ogni caso prendo il primo
                        Vector objs = (Vector) resp.getResponseObj();

                        if ((objs != null) && (objs.size() > 0)) {

                            //scarico il valore del campo search:Prezzo dell'oggetto trovato
                            //newObj.setField(fld.getDescription(), ((AnagBean) objs.elementAt(0)).getFieldValue(TextUtil.split(firstPart, ':')[1]));
                            value = (String) ((AnagBean) objs.elementAt(0)).getFieldValueString( Session.getMngConfig().getAnagObjFieldIDbyDescription(((AnagBean) objs.elementAt(0)).getType(),TextUtil.split(firstPart, ':')[1])   );
                            setted = true;
                        }


                    }//Fornitore.Denominazione
                    else if (firstPart.equals(compileAnagBean.getType())) {

                        nameField = getNameField(secondPart, compileAnagBean, null);

                        if (compileAnagBean.hasField(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),nameField))) {

                            //newObj.setField(fld.getDescription(), (String) compileAnagBean.getFieldValue(nameField));
                            value = (String) compileAnagBean.getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileAnagBean.getType(),nameField));
                            setted = true;
                        }

                    }//Ordine.Sconto Testata.Sconto
                    //else if ((compileDocBean != null) && (firstPart.equals(compileDocBean.getType()))) {
                    else if ((compileDocBean != null) && (firstPart.equals("Testata"))) {
                        
                        nameField = getNameField(secondPart, compileAnagBean, compileDocBean);

                        if (compileDocBean.getHeadObj().hasField(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileDocBean.getHeadObj().getType(),nameField))) {

                            //newObj.setField(fld.getDescription(), (String) compileDocBean.getHeadObj().getFieldValue(nameField));
                            value = (String) compileDocBean.getHeadObj().getFieldValueString(Session.getMngConfig().getAnagObjFieldIDbyDescription(compileDocBean.getHeadObj().getType(),nameField));
                            setted = true;
                        }

                    }

                }

            }


            //control setDefault
            if (!setted) {

                props = fld.getPropertyByKey("setDefault");
                sizePrps =    props == null ? 0 : props.size();

                if (sizePrps > 0) {

                    //newObj.setField(fld.getDescription(), (String) ((Property) props.elementAt(0)).getValue());
                    value = String.valueOf(((Integer) ((Property) props.elementAt(0)).getValue()).intValue());
                }

            }

        } catch (Exception e) {
            //#debug error
            System.out.println("Exception: " + e.getMessage());
        }

        return value;

    }
}
