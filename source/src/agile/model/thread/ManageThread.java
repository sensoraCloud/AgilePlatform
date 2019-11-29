/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile.model.thread;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ListItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.zip.GZipInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.json.me.JSONArray;
import agile.json.me.JSONObject;
import agile.model.service.ManageService;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.DocBean;
import agile.model.structure.DocObj;
import agile.model.structure.Field;
import agile.model.structure.Index;
import agile.model.structure.PaginatedGroupValues;
import agile.model.structure.PaginatedValues;
import agile.model.structure.ResponseService;
import agile.model.structure.Structure;
import agile.session.Session;
import agile.util.Date_Util;
import agile.util.DoubleComparable;
import agile.util.HeapSorter;
import agile.util.LongComparable;
import agile.util.Search_Util;
import agile.util.StringComparable;
import agile.view.AdmAnagBeanObjForm;
import agile.view.AdmDocDetailForm;
import agile.view.MainForm;
import agile.view.SearchForm;
import agile.webservice.RequestParameters;

/**
 *
 * @author ruego
 */
public class ManageThread {

    private Hashtable listThread;    //list ThreadID
    //list threadID
    public static final int THREAD_FORCELOADDATA = 1;
    public static final int THREAD_GETPAGINATEGROUP_OBJS = 2;
    public static final int THREAD_GETPAGINATELIST_VALUE = 3;
    public static final int THREAD_SORT_PAGESVALUE = 4;
    public static final int THREAD_SEARCH_OBJ = 5;
    public static final int THREAD_DUPLICATE_DOC = 6;
    public static final int THREAD_ADD_NEW_ROWDOC_FROM_ANAGBEAN = 7;
    public static final int THREAD_DELETE_DOC = 8;
    public static final int THREAD_COMMIT_ANAGBEAN = 9;
    public static final int THREAD_PERSIST_ANAGBEAN = 10;
    public static final int THREAD_ADD_EDIT_NEWROWDOCBEAN = 11;
    public static final int THREAD_CANCEL_UPDATE_DOCBEAN = 12;
    public static final int THREAD_SEND_HTTP_DOC = 13;
    public static final int THREAD_SYNC_OBJS_HTTP = 14;
    public static final int THREAD_DELETE_ALL_OBJS = 15;
    private Event e = null;

    //vedi ThreadMain 
    public boolean addThread(int threadID) {

        boolean response = true;

        //if thread already exist cancel
        if (getMonitorableThread(threadID) != null) {

            destroyThread(threadID);

        }

        switch (threadID) {

            case THREAD_FORCELOADDATA:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.loadData")) {

                    private DocBean currentDocBean = null;

                    public void addObj(StringBuffer buf, String typeObj, int l) throws Exception {

                        AnagBean obj = new AnagBean(typeObj);

                        String[] campi = null;

                        campi = TextUtil.split(TextUtil.replace(buf.toString().trim().toUpperCase(), "\r", ""), '\t');

                        obj.getFieldsValue().clear();

                        obj.setField("type", typeObj);

                        if (typeObj.equals("Cliente")) {

                            obj.setField("a3", new Long(Long.parseLong( campi[0])));//codice
                            obj.setField("a4", campi[1]);//Denominazione
                            obj.setField("a5", campi[2]);//Indirizzo
                            obj.setField("a7", campi[3]);//Citta
                            obj.setField("a6", new Long(Long.parseLong( campi[4])));//CAP
                            obj.setField("a9", campi[5]);//Tel.
                            obj.setField("a28", campi[6]);//Fax
                            obj.setField("a12", campi[7] + " " + campi[8]);//Rif. Persona
                            obj.setField("a26", campi[9]);//Nazione
                            obj.setField("a11", campi[10]);//Listino
                            obj.setSystemID(new Integer(1000 + l));

                            Session.getAnagBeanDAO().persist(obj);//                     

                            // System.out.println("Seliarialize line " + l + ":" + buf.toString() + " to: " + obj.serialize());

                            System.out.println(obj.serializeToServer());

                            ManageViewNavigation.getPrgsThread().setTextMex("Insert " + l + " " + typeObj);

                        } else if (typeObj.equals("Prodotto")) {

                            obj.setField("a87", campi[0]);//Descrizione
                            obj.setField("a86", new Long(Long.parseLong( campi[1])));//Codice
                            //obj.setField("Prezzo_LISTINO1", campi[2]);
                            //obj.setField("Prezzo_LISTINO2", campi[3]);
                            //obj.setField("Prezzo_LISTINO3", campi[4]);
                            obj.setField("a89", campi[5]);//Categoria
                            obj.setField("a90", campi[6]);//Sottocategoria
                            obj.setSystemID(new Integer(2000 + l));

                            Session.getAnagBeanDAO().persist(obj);//                     

                            // System.out.println("Seliarialize line " + l + ":" + buf.toString() + " to: " + obj.serialize());

                            System.out.println(obj.serializeToServer());

                            ManageViewNavigation.getPrgsThread().setTextMex("Insert " + l + " " + typeObj);


                        } else if (typeObj.equals("Listino")) {

                            obj.setField("a1", campi[0]);//CodList
                            obj.setField("a2", new Long(Long.parseLong( campi[1])));//CodProd
                            obj.setField("a3", new Double(Double.parseDouble( campi[2].replace(',', '.') )));//Prezzo
                            obj.setField("a4", new Long(Long.parseLong( campi[3])));//Sconto
                            obj.setSystemID(new Integer(3000 + l));

                            Session.getAnagBeanDAO().persist(obj);//

                            // System.out.println("Seliarialize line " + l + ":" + buf.toString() + " to: " + obj.serialize());

                            System.out.println(obj.serializeToServer());

                            ManageViewNavigation.getPrgsThread().setTextMex("Insert " + l + " " + typeObj);


                        } else if (typeObj.equals("Ordine Cli.")) {

                            String typeRec = campi[0];

                            //testata
                            if (typeRec.equals("1")) {

                                AnagBean headObj = new AnagBean("Testata");

                                headObj.setField("a28", new Long(Long.parseLong( campi[1])));//CodiceCliente
                                headObj.setField("a30", campi[2]);//Denominazione
                                headObj.setField("a31", campi[3]);//Indirizzo
                                headObj.setField("a33", campi[4]);//Citta
                                headObj.setField("a32", new Long(Long.parseLong( campi[5])));//CAP
                                headObj.setField("a38",  campi[6]);//Tel.
                                headObj.setField("a40",  campi[7]);//Fax
                                headObj.setField("a42", campi[8] + " " + campi[9]);//Rif. Persona
                                headObj.setField("a35", campi[10]);//Nazione
                                headObj.setField("a75", campi[11]);//Listino
                                headObj.setField("a48", Date_Util.getStamp00000FromDate(Date_Util.getDatefromStamp(Long.parseLong(campi[12]))));//DataDocumento
                                headObj.setField("a73", campi[13]);//Stato

                                headObj.setField("sourceNumRec", new Integer(Session.getMngIndex().getNextRelativeRecordIDnoSaveValue(typeObj)));
                                headObj.setField("sourceType", typeObj);

                                Session.getAnagBeanDAO().persist(headObj);//                     

                                System.out.println("Seliarialize line " + l + ":" + buf.toString() + " to: " + headObj.serialize());

                                ManageViewNavigation.getPrgsThread().setTextMex("Insert " + l + " " + typeObj);

                                currentDocBean = new DocBean(typeObj, headObj);

                                if (campi[13].equals("INVIATO")) {
                                    currentDocBean.setSystemID(new Integer(5000 + l));
                                }

                                Session.getDocBeanDAO().persist(currentDocBean);




                            } else {//riga

                                AnagBean rowObj = new AnagBean("Riga");

                                rowObj.setField("a74", new Long(Long.parseLong( campi[1])));//Codice
                                rowObj.setField("a75", campi[2]);//Descrizione
                                rowObj.setField("a76", new Long(Long.parseLong( campi[3].replace(',', '.'))));//Qta
                                rowObj.setField("a81", new Double(Double.parseDouble( campi[4].replace(',', '.'))));//Prezzo
                                rowObj.setField("a80", new Long(Long.parseLong( campi[5])));//Sconto
                                rowObj.setField("a84", new Double(Double.parseDouble( campi[6].replace(',', '.'))));//TotPrezzo
                                rowObj.setField("a83", new Long(Long.parseLong( campi[7])));//Iva

                                rowObj.setField("sourceNumRec", currentDocBean.getNumRec());
                                rowObj.setField("sourceType", currentDocBean.getType());

                                Session.getAnagBeanDAO().persist(rowObj);//                     

                                System.out.println("Seliarialize line " + l + ":" + buf.toString() + " to: " + rowObj.serialize());

                                ManageViewNavigation.getPrgsThread().setTextMex("Insert " + l + " " + typeObj);

                                currentDocBean.addRowObj(rowObj);

                                AnagBean oldObj = new AnagBean();
                                oldObj.unserialize(currentDocBean.getHeadObj().serialize());

                                //modified head obj with infoOperation
                                Session.getDocBeanDAO().commitChange(currentDocBean);

                                //resave head for infoOperation
                                Session.getAnagBeanDAO().commitChange(currentDocBean.getHeadObj(), oldObj);

                            }


                        }



                    }

                    public void run() {


                        try {


                            String typeObj = (String) getParams().get("typeObj");

                            InputStream inputStreamTxt = null;
                            inputStreamTxt = this.getClass().getResourceAsStream((String) getParams().get("nomeFile"));

                            inputStreamTxt = new GZipInputStream(inputStreamTxt, GZipInputStream.TYPE_GZIP, false);

                            StringBuffer buf = new StringBuffer();



                            //for view in progress
                            setPercentage(50);

                            int c;
                            int l = 0;
                            //int recID=0;

                            while ((c = inputStreamTxt.read()) != -1) {
                                char ch = (char) c;
                                if (ch == '\n') {

                                    if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
                                        break;
                                    }

                                    if (this.isPaused()) {
                                        waitUntilAliveIs();
                                    }
                                    try {

                                        l++;

                                        this.addObj(buf, typeObj, l);

                                    } catch (Exception ex) {
                                        //#debug error
                                        System.out.println(ex + "Riga: " + buf.toString());
                                        ManageViewNavigation.getPrgsThread().setTextMex("Exception: " + ex.getMessage() == null ? "Null" : ex.getMessage());
                                    }

                                    buf.delete(0, buf.length());
                                } else {
                                    buf.append(ch);
                                }
                            }


                            //save last
                            try {

                                l++;
                                this.addObj(buf, typeObj, l);

                            } catch (Exception ex) {
                                //#debug error
                                System.out.println(ex + "Riga: " + buf.toString());
                                ManageViewNavigation.getPrgsThread().setTextMex("Exception: " + ex.getMessage() == null ? "Null" : ex.getMessage());
                            }

                            inputStreamTxt.close();

                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());

                        }

                        if (!this.isFailed() && !this.isCancelled()) {
                            setPercentage(100);
                        }

                        setTerminate(true);

                        this.onComplete();

                        //task is complete or has been paused/cancelled, so thread's execution will discontinue
                        //release reference to the thread 

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {


                            //if completed
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                                e = new Event();

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

                            } else {

                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.completed"), null, AlertType.INFO);
                            }

                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }



                    }
                });


                break;

            case THREAD_GETPAGINATEGROUP_OBJS://digitex,mario...


                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.search")) {

                    private PaginatedValues pgs = null;

                    public void run() {

                        String typeObj = (String) getParams().get("typeObj");
                        String searchField = (String) getParams().get("searchField");
                        Object valueField = (Object) getParams().get("valueField");
                        //Indexable indexableObjClass = (Indexable) getParams().get("indexableObjClass");
                        Structure structObj = (Structure) getParams().get("structureObjClass");

                        Hashtable params = new Hashtable(2);

                        params.put("typeObj", typeObj);
                        params.put("indexFld", searchField);

                        ResponseService resp = Session.getMngService().doService(ManageService.GET_TYPEUI_OF_INDEXFIELD_BYTYPEOBJ, params);

                        String typeUIsearchField = (String) resp.getResponseObj();

                        boolean numericField = false;
                        boolean dateField = false;
                        boolean decimalField = false;

                        if (typeUIsearchField.equals("NUMERIC")) {
                            numericField = true;
                        } else if (typeUIsearchField.equals("DATE")) {
                            dateField = true;
                        } else if (typeUIsearchField.equals("DECIMAL")) {
                            decimalField = true;
                        }


                        RecordStore rmsObj = null;

                        try {

                            //search index of group obj

                            //#debug info
                            System.out.println("1-Recupero index da hashindex");

                            //take rms numRec for field index value
                            int numRecIndexRms = ((Integer) ((Hashtable) Session.getMngIndex().getHashIndex().get(typeObj + "_" + searchField)).get(dateField ? Date_Util.getStampFromStr((String) valueField, Date_Util.dmyHHmmss).toString() : valueField)).intValue();

                            //#debug info
                            System.out.println("2-Apro recordstore: " + typeObj + "_" + searchField);


                            int numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                            RecordStore rms = RecordStore.openRecordStore(typeObj + "_" + searchField + "_" + String.valueOf(numRMS), false);

                            int realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);

                            //#debug info
                            System.out.println("3-Aperto recordstore: " + typeObj + "_" + searchField);

                            int sizeRecData = rms.getRecordSize(realRecID);

                            byte[] recData = new byte[sizeRecData];

                            int dataLen = rms.getRecord(realRecID, recData, 0);

                            String str = (new String(recData, 0, dataLen)).trim();

                            rms.closeRecordStore();


                            Index index = new Index();

                            index.unserialize(str);

                            //#debug info
                            System.out.println("4-Letto indice: " + str);

                            int sizeIndexs = index.getIndexs().size();

                            //title vector
                            Vector titleSort = new Vector(sizeIndexs);

                            Vector sortVet = new Vector(sizeIndexs);

                            //obj serialized string vector
                            Vector ObjSort = new Vector(sizeIndexs);

                            int numRecObj = 0;

                            setPercentage(15);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            int o = 0;

                            //#debug info
                            System.out.println("5-Lettura oggetti indicizzati: ");

                            numRMS = 0;
                            //  int oldNumRMS = 0;
                            realRecID = 0;

                            Hashtable openedRMS = new Hashtable();
                            JSONObject jso = null;

                            //get all obj by index
                            for (int d = 0; d < sizeIndexs; d++) {


                                if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
                                    break;
                                }

                                if (this.isPaused()) {
                                    waitUntilAliveIs();
                                }

                                numRecObj = ((Integer) index.getIndexs().elementAt(d)).intValue();

                                numRMS = Session.getMngIndex().getNumRMS(numRecObj);

                                if (!openedRMS.containsKey(new Integer(numRMS))) {

                                    //rmsObj=new RecordStore();
                                    rmsObj = RecordStore.openRecordStore(typeObj + "_" + String.valueOf(numRMS), false);
                                    openedRMS.put(new Integer(numRMS), rmsObj);

                                }

//                                if (numRMS != oldNumRMS) {
//
//                                    if (oldNumRMS != 0) {
//                                        rmsObj.closeRecordStore();
//                                    }
//                                    rmsObj = RecordStore.openRecordStore(typeObj + "_" + String.valueOf(numRMS), false);
//
//                                    oldNumRMS = numRMS;
//
//                                }

                                realRecID = Session.getMngIndex().getRealRecID(numRecObj, numRMS);

                                sizeRecData = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecordSize(realRecID);

                                recData = new byte[sizeRecData];

                                dataLen = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecord(realRecID, recData, 0);

                                str = (new String(recData, 0, dataLen)).trim();

                                jso = new JSONObject(str);

                                //indexableObjClass.unserialize(str);
                                if (jso.has("title")) {

                                    titleSort.addElement(jso.getString("title"));

//                                    if (numericField) {
//
//                                        sortVet.addElement(new LongComparable(new Long(Long.parseLong(jso.getString("title")))));
//
//
//                                    } else if (decimalField) {
//
//                                        sortVet.addElement(new DoubleComparable(new Double(Double.parseDouble(TextUtil.replace(jso.getString("title"), ",", ".")))));
//
//
//                                    } else {
//
//                                        sortVet.addElement(new StringComparable(jso.getString("title")));
//
//                                    }

                                     sortVet.addElement(new StringComparable(jso.getString("title")));
                                    

                                } else {

                                    //#debug error
                                    System.out.println("No title for obj: " + str);

                                }

                                ObjSort.addElement(str);

                                o++;

                                ManageViewNavigation.getPrgsThread().setTextMex("Read.. " + o + " of " + sizeIndexs);


                            }

                            Enumeration en = openedRMS.keys();

                            while (en.hasMoreElements()) {

                                ((RecordStore) openedRMS.get(en.nextElement())).closeRecordStore();

                            }


                            if ((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100)) {

                                setPercentage(30);

                                ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());



                                if (this.isPaused()) {
                                    waitUntilAliveIs();
                                }


                                //if not sorted 
                                if (!index.getSorted().booleanValue()) {

                                    ManageViewNavigation.getPrgsThread().setTextMex("Sorting.. ");

                                    Vector swapVect = new Vector(3);

                                    swapVect.addElement(titleSort);
                                    swapVect.addElement(ObjSort);
                                    swapVect.addElement(index.getIndexs());

                                    HeapSorter.heapsort(sortVet, swapVect, null);

//                                    int size = titleSort.size();
//
//                                    //Sorting
//                                    for (int i = 0; i < size; i++) {
//
//                                        if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
//                                            break;
//                                        }
//
//                                        if (this.isPaused()) {
//                                            waitUntilAliveIs();                                    //if not sorted
//                                        }
//
//                                        ManageViewNavigation.getPrgsThread().setTextMex("Sorting.. " + i + " of " + size);
//
//
//                                        for (int j = i + 1; j < size; j++) {
//
//                                            if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
//                                                break;
//                                            }
//
//                                            if (this.isPaused()) {
//                                                waitUntilAliveIs();                                    //if not sorted
//                                            }
//
//                                            boolean swap = false;
//
//                                            if (numericField) {
//
//                                                long first = Long.parseLong((String) titleSort.elementAt(i));
//                                                long second = Long.parseLong((String) titleSort.elementAt(j));
//
//                                                if (first > second) {
//                                                    swap = true;
//                                                }
//
//                                            } else if (decimalField) {
//
//                                                double first = Double.parseDouble(TextUtil.replace((String) titleSort.elementAt(i), ",", "."));
//                                                double second = Double.parseDouble(TextUtil.replace((String) titleSort.elementAt(j), ",", "."));
//
//                                                if (first > second) {
//                                                    swap = true;
//                                                }
//
//
//                                            } else {
//
//                                                int result = (((String) titleSort.elementAt(i)).compareTo((String) titleSort.elementAt(j)));
//                                                if (result > 0) {
//                                                    swap = true;
//                                                }
//                                            }
//
//
//
//                                            //se la prima stringa è maggiore della seconda la sposto
//                                            if (swap) {
//
//                                                //scambio sia i title che gli oggetti serializzati che l'indice
//                                                String titleTemp = (String) titleSort.elementAt(i);
//                                                String serializedOBJTemp = (String) ObjSort.elementAt(i);
//                                                Integer indexTemp = (Integer) index.getIndexs().elementAt(i);
//
//                                                titleSort.setElementAt((String) titleSort.elementAt(j), i);
//                                                titleSort.setElementAt(titleTemp, j);
//
//                                                ObjSort.setElementAt((String) ObjSort.elementAt(j), i);
//                                                ObjSort.setElementAt(serializedOBJTemp, j);
//
//                                                index.getIndexs().setElementAt((Integer) index.getIndexs().elementAt(j), i);
//                                                index.getIndexs().setElementAt(indexTemp, j);
//
//                                            }
//
//                                        }
//                                    }

                                    //save sort index
                                    index.setSorted(new Boolean(true));

                                    numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                                    rms = RecordStore.openRecordStore(typeObj + "_" + searchField + "_" + String.valueOf(numRMS), false);

                                    realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);

                                    byte[] ser = index.serialize().getBytes();

                                    rms.setRecord(realRecID, ser, 0, ser.length);

                                    rms.closeRecordStore();

                                }


                                if ((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100)) {

                                    setPercentage(60);

                                    ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                    ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());


                                    //paginate results
                                    int maxObjsPages = ((Integer) ((Hashtable) Session.getListConfig().get("View")).get("maxItemsForPage")).intValue();

                                    int numPages = 0;

                                    if ((ObjSort.size() % maxObjsPages) > 0) {
                                        numPages = ObjSort.size() / maxObjsPages + 1;
                                    } else {
                                        numPages = ObjSort.size() / maxObjsPages;
                                    }

                                    Vector paginatesTitles = new Vector(numPages);
                                    Vector paginatesSerialized = new Vector(numPages);

                                    int size = ObjSort.size();

                                    ManageViewNavigation.getPrgsThread().setTextMex("Paginate.. ");

                                    for (int p = 0; p < numPages; p++) {

                                        if (this.isPaused()) {
                                            waitUntilAliveIs();
                                        }

                                        paginatesTitles.addElement(new Vector(maxObjsPages));
                                        paginatesSerialized.addElement(new Vector(maxObjsPages));

                                        for (int e = 0; ((e < maxObjsPages) && ((e + (maxObjsPages * p)) < size)); e++) {

                                            if ((ObjSort.elementAt(e + (maxObjsPages * p))) == null) {
                                                continue;
                                            }
                                            if ((titleSort.elementAt(e + (maxObjsPages * p))) == null) {
                                                continue;
                                            }

                                            ((Vector) paginatesTitles.elementAt(p)).addElement(titleSort.elementAt(e + (maxObjsPages * p)));
                                            ((Vector) paginatesSerialized.elementAt(p)).addElement(ObjSort.elementAt(e + (maxObjsPages * p)));

                                        }

                                    }

                                    setPercentage(90);

                                    ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                    ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                                    if (this.isPaused()) {
                                        waitUntilAliveIs();                                    //if not sorted 
                                    }


                                    pgs = new PaginatedValues(numPages, paginatesTitles, paginatesSerialized, typeObj, searchField, valueField);



                                }



                            }


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }



                        if (!this.isFailed() && !this.isCancelled()) {

                            setPercentage(100);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete();


                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }





                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {


                            boolean returnForm = true;

                            //if show progress no returnForm for back command
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {
                                returnForm = false;
                            }

                            e = new Event();
                            e.setByName("PaginatedValues", pgs);
                            e.setByName("returnForm", new Boolean(returnForm));

                            Session.getMngEvent().handleEvent(ManageEvent.VIEW_PAGELISTOBJS_RESULT_EVENT, e);


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }



                    }
                });


                break;

            case THREAD_GETPAGINATELIST_VALUE://vari CAP,CITTA

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.search")) {

                    private PaginatedGroupValues pgs = null;

                    public void run() {

                        String typeObj = (String) getParams().get("typeObj");
                        String searchField = (String) getParams().get("searchField");
                        Structure structObj = (Structure) getParams().get("structureObjClass");

                        Hashtable params = new Hashtable(2);

                        params.put("typeObj", typeObj);
                        params.put("indexFld", searchField);

                        ResponseService resp = Session.getMngService().doService(ManageService.GET_TYPEUI_OF_INDEXFIELD_BYTYPEOBJ, params);

                        String typeUIsearchField = (String) resp.getResponseObj();


                        boolean numericField = false;
                        boolean dateField = false;
                        boolean decimalField = false;

                        if (typeUIsearchField.equals("NUMERIC")) {
                            numericField = true;
                        } else if (typeUIsearchField.equals("DATE")) {
                            dateField = true;
                        } else if (typeUIsearchField.equals("DECIMAL")) {
                            decimalField = true;
                        }

                        try {

                            if (((Hashtable) Session.getMngIndex().getHashIndex().get(typeObj + "_" + searchField)) == null) {

                                setPercentage(100);

                                Vector paginatesValues = new Vector(1);
                                paginatesValues.addElement(new Vector(0));

                                pgs = new PaginatedGroupValues(1, paginatesValues, typeObj, searchField);

                            }


                            if ((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100)) {


                                int sizeRec = ((Hashtable) Session.getMngIndex().getHashIndex().get(typeObj + "_" + searchField)).size();

                                //Object[] sortValueStr =null;
                                //long[] sortValueLong =null;
                                //double[] sortValueDouble =null;

                                Vector sortVet = new Vector(sizeRec);

                                setPercentage(15);

                                ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                                Enumeration enumerate = ((Hashtable) Session.getMngIndex().getHashIndex().get(typeObj + "_" + searchField)).keys();

                                int o = 0;

                                //for all rmsIndex create hashIndex
                                while (enumerate.hasMoreElements()) {

                                    if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
                                        break;
                                    }

                                    if (this.isPaused()) {
                                        waitUntilAliveIs();                                    //if not sorted 
                                    }

                                    if ((numericField) || (dateField)) {

                                        //sortValueLong = new long[sizeRec];

                                        try {
                                            sortVet.addElement(new LongComparable(new Long(Long.parseLong((String) enumerate.nextElement()))));
                                            //sortValueLong[o] = Long.parseLong((String) enumerate.nextElement());
                                        } catch (Exception ex) {
                                            sortVet.addElement(new LongComparable(new Long(0)));
                                            //sortValueLong[o] = 0;
                                        }

                                    } else if (decimalField) {

                                        //sortValueDouble = new double[sizeRec];

                                        try {
                                            sortVet.addElement(new DoubleComparable(new Double(Double.parseDouble(TextUtil.replace((String) enumerate.nextElement(), ",", ".")))));
                                            //sortValueDouble[o] = Double.parseDouble(TextUtil.replace((String) enumerate.nextElement(), ",", "."));
                                        } catch (Exception ex) {
                                            sortVet.addElement(new DoubleComparable(new Double(0)));
                                            //sortValueDouble[o] = 0;
                                        }

                                    } else {

                                        //sortValueStr = new Object[sizeRec];
                                        sortVet.addElement(new StringComparable((String) enumerate.nextElement()));
                                        //sortValueStr[o] = (String) enumerate.nextElement();
                                    }

                                    o++;

                                    ManageViewNavigation.getPrgsThread().setTextMex("Read: " + o + " of " + sizeRec);


                                }



                                if ((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100)) {

                                    setPercentage(30);

                                    ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                    ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                                    if (this.isPaused()) {
                                        waitUntilAliveIs();
                                    }

                                    ManageViewNavigation.getPrgsThread().setTextMex("Sorting.. ");

                                    HeapSorter.heapsort(sortVet, null, null);

//                                    if ((numericField) || (dateField)) {
//
//
//                                        for (int i = 0; i < sizeRec; i++) {
//
//                                            for (int j = i + 1; j < sizeRec; j++) {
//
//                                                if (sortValueLong[i] > sortValueLong[j]) {
//
//                                                    long titleTemp = sortValueLong[i];
//                                                    sortValueLong[i] = sortValueLong[j];
//                                                    sortValueLong[j] = titleTemp;
//
//                                                }
//
//                                            }
//                                        }
//
//
//                                    } else if (decimalField) {
//
//                                        for (int i = 0; i < sizeRec; i++) {
//
//                                            for (int j = i + 1; j < sizeRec; j++) {
//
//                                                if (sortValueDouble[i] > sortValueDouble[j]) {
//
//                                                    double titleTemp = sortValueDouble[i];
//                                                    sortValueDouble[i] = sortValueDouble[j];
//                                                    sortValueDouble[j] = titleTemp;
//
//                                                }
//
//                                            }
//                                        }
//
//
//                                    } else {
//
//                                        Arrays.sort(sortValueStr);
//
//                                    }


                                    if ((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100)) {

                                        setPercentage(60);

                                        ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                        ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());


                                        //paginate results
                                        int maxObjsPages = ((Integer) ((Hashtable) Session.getListConfig().get("View")).get("maxItemsForPage")).intValue();

                                        int numPages = 0;

                                        if ((sizeRec % maxObjsPages) > 0) {
                                            numPages = sizeRec / maxObjsPages + 1;
                                        } else {
                                            numPages = sizeRec / maxObjsPages;
                                        }

                                        Vector paginatesValues = new Vector(numPages);

                                        ManageViewNavigation.getPrgsThread().setTextMex("Paginate.. ");


                                        for (int p = 0; p < numPages; p++) {

                                            if (this.isPaused()) {
                                                waitUntilAliveIs();
                                            }

                                            paginatesValues.addElement(new Vector(maxObjsPages));

                                            for (int e = 0; ((e < maxObjsPages) && ((e + (maxObjsPages * p)) < sizeRec)); e++) {


                                                if (numericField) {

                                                    ((Vector) paginatesValues.elementAt(p)).addElement(String.valueOf(((LongComparable) sortVet.elementAt(e + (maxObjsPages * p))).getComp().longValue()));


                                                } else if (decimalField) {

                                                    ((Vector) paginatesValues.elementAt(p)).addElement(TextUtil.replace(String.valueOf(((DoubleComparable) sortVet.elementAt(e + (maxObjsPages * p))).getComp().doubleValue()), ".", ","));

                                                } else if (dateField) {

                                                    ((Vector) paginatesValues.elementAt(p)).addElement(Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(String.valueOf(((DoubleComparable) sortVet.elementAt(e + (maxObjsPages * p))).getComp().doubleValue()))), Date_Util.dmyHHmmss, false));

                                                } else {

                                                    if (((StringComparable) sortVet.elementAt(e + (maxObjsPages * p))).getComp() == null) {
                                                        continue;
                                                    }

                                                    ((Vector) paginatesValues.elementAt(p)).addElement(((StringComparable) sortVet.elementAt(e + (maxObjsPages * p))).getComp());


                                                }


                                            }

                                        }

                                        setPercentage(90);

                                        ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                        ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                                        if (this.isPaused()) {
                                            waitUntilAliveIs();                                    //if not sorted 
                                        }


                                        pgs = new PaginatedGroupValues(numPages, paginatesValues, typeObj, searchField);


                                    }



                                }


                            }

                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }



                        if (!this.isFailed() && !this.isCancelled()) {

                            setPercentage(100);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete();


                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }


                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {


                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                e = new Event();

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }


                            boolean returnForm = true;

                            //if show progress no returnForm for back command
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {
                                returnForm = false;
                            }
                            e = new Event();
                            e.setByName("PaginatedGroupValues", pgs);

                            if (((String) getParams().get("typeSourceEvent")).equals("View_new_groupValueObj")) {

                                e.setByName("returnForm", new Boolean(returnForm));

                                Session.getMngEvent().handleEvent(ManageEvent.VIEW_GROUPVALUEOBJ_RESULT_EVENT, e);


                            } else if (((String) getParams().get("typeSourceEvent")).equals("Rebuild_groupValueObj")) {

                                //rebuild groupvalueobj
                                Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GROUPVALUEOBJ_RESULT_EVENT, e);


                            }



                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }



                    }
                });


                break;

            case THREAD_SORT_PAGESVALUE:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.sortingvalue")) {

                    public void run() {

                        try {

                            PaginatedValues values = (PaginatedValues) getParams().get("values");
                            Field sortField = (Field) getParams().get("sortField");

                           

                            boolean numericField = false;
                            boolean dateField = false;
                            boolean decimalField = false;

                            if (sortField.getTypeUI().equals("NUMERIC")) {
                                numericField = true;
                            } else if (sortField.getTypeUI().equals("DATE")) {
                                dateField = true;
                            } else if (sortField.getTypeUI().equals("DECIMAL")) {
                                decimalField = true;
                            }

                            boolean docobjs = false;

                            if (((Structure) Session.getListStructureObj().get(values.getTypeObj())).getType().equals("DocObj")) {
                                docobjs = true;
                            }


                            int sizeRow = values.getNumPages();
                            int sizeCol = ((Vector) values.getPagesValue().elementAt(0)).size();
                            int sizeColsuc = 0;

                            String serializedObj = null;
                            JSONObject jso = null;
                            String temp = null;

                            double percentageDouble = 0;
                            long numelements = sizeRow * sizeCol;
                            double coefficienteIncremento = (double) 100 / (double) numelements;

                            Vector pagesTemp = new Vector(sizeRow);

                            for (int r = 0; r < sizeRow; r++) {

                                if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
                                    break;
                                }

                                if (this.isPaused()) {
                                    waitUntilAliveIs();
                                }

                                //size Col can change only last row
                                if (r + 1 == sizeCol) {
                                    sizeCol = ((Vector) values.getPagesValue().elementAt(r)).size();
                                }

                                for (int c = 0; c < sizeCol; c++) {

                                    percentageDouble += coefficienteIncremento;
                                    setPercentage((int) percentageDouble);
                                    ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                    ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                                    //parto dalla riga dove sono arrivato
                                    for (int rs = r; rs < sizeRow; rs++) {

                                        sizeColsuc = ((Vector) values.getPagesValue().elementAt(rs)).size();

                                        //se siamo sulla prima riga prendo la colonna dove siam arrivati...
                                        for (int cs = (rs == r ? c + 1 : 0); cs < sizeColsuc; cs++) {

                                            boolean swap = false;

                                            Object first = null;
                                            Object second = null;

                                            if ((numericField) || (dateField)) {

                                                //se non esiste la riga la metto
                                                if (pagesTemp.size() < (r + 1)) {
                                                    pagesTemp.addElement(new Vector(sizeColsuc));
                                                }

                                                

                                                //se esiste la colonna prelevo il valore altriemnti calcolo e lo inserisco
                                                if (((Vector) pagesTemp.elementAt(r)).size() > (c + 1)) {

                                                    first = new Long(Long.parseLong((String) ((Vector) pagesTemp.elementAt(r)).elementAt(c)));

                                                } else {
                                                      

                                                    serializedObj = (String) ((Vector) values.getPagesSerializedObjList().elementAt(r)).elementAt(c);

                                                    if (docobjs) {
                                                        jso = new JSONObject(serializedObj);
                                                        jso = new JSONObject(Session.getAnagBeanDAO().getSerializedAnagBeanFromNumRec((String) jso.get("typeHeadObj"), jso.getInt("numRecHeadObj")));

                                                    } else {
                                                        jso = new JSONObject(serializedObj);
                                                    }

                                                    if (jso.get(sortField.getId()) == null) {
                                                        first = new Long(0);
                                                    } else {
                                                        first = new Long(jso.getLong(sortField.getId()));
                                                    }

                                                     
//                                                    else {
//                                                        first = new Long(Long.parseLong((String) first));
//                                                    }

                                                    ((Vector) pagesTemp.elementAt(r)).addElement(String.valueOf(((Long) first).longValue()));

                                                     
                                                }

                                                //se non esiste la riga la metto
                                                if (pagesTemp.size() < (rs + 1)) {
                                                    pagesTemp.addElement(new Vector(sizeColsuc));
                                                }

                                                //se esiste la colonna prelevo il valore altriemnti calcolo e lo inserisco
                                                if (((Vector) pagesTemp.elementAt(rs)).size() > (cs + 1)) {

                                                    second = new Long(Long.parseLong((String) ((Vector) pagesTemp.elementAt(rs)).elementAt(cs)));

                                                } else {

                                                    serializedObj = (String) ((Vector) values.getPagesSerializedObjList().elementAt(rs)).elementAt(cs);

                                                    if (docobjs) {
                                                        jso = new JSONObject(serializedObj);
                                                        jso = new JSONObject(Session.getAnagBeanDAO().getSerializedAnagBeanFromNumRec((String) jso.get("typeHeadObj"), jso.getInt("numRecHeadObj")));

                                                    } else {
                                                        jso = new JSONObject(serializedObj);
                                                    }

                                                    if (jso.get(sortField.getId()) == null) {
                                                        second = new Long(0);
                                                    } else {
                                                        second = new Long(jso.getLong(sortField.getId()));
                                                    }

                                                   

                                                    ((Vector) pagesTemp.elementAt(rs)).addElement(String.valueOf(((Long) second).longValue()));

                                                }


                                                if (((Long) first).longValue() > ((Long) second).longValue()) {
                                                    swap = true;
                                                }

                                            } else if (decimalField) {

                                                //se non esiste la riga la metto
                                                if (pagesTemp.size() < (r + 1)) {
                                                    pagesTemp.addElement(new Vector(sizeColsuc));
                                                }

                                                //se esiste la colonna prelevo il valore altriemnti calcolo e lo inserisco
                                                if (((Vector) pagesTemp.elementAt(r)).size() > (c + 1)) {

                                                    first = new Double(Double.parseDouble((String) ((Vector) pagesTemp.elementAt(r)).elementAt(c)));

                                                } else {

                                                    serializedObj = (String) ((Vector) values.getPagesSerializedObjList().elementAt(r)).elementAt(c);

                                                    if (docobjs) {
                                                        jso = new JSONObject(serializedObj);
                                                        jso = new JSONObject(Session.getAnagBeanDAO().getSerializedAnagBeanFromNumRec((String) jso.get("typeHeadObj"), jso.getInt("numRecHeadObj")));

                                                    } else {
                                                        jso = new JSONObject(serializedObj);
                                                    }

                                                    //first = TextUtil.replace((String) jso.get(sortField.getId()), ",", ".");
                                                    if (jso.get(sortField.getId()) == null) {
                                                        first = new Double(0);
                                                    } else {
                                                        first = new Double(jso.getDouble(sortField.getId()));
                                                    }


                                                    ((Vector) pagesTemp.elementAt(r)).addElement(String.valueOf(((Double) first).doubleValue()));

                                                }


                                                //se non esiste la riga la metto
                                                if (pagesTemp.size() < (rs + 1)) {
                                                    pagesTemp.addElement(new Vector(sizeColsuc));
                                                }

                                                //se esiste la colonna prelevo il valore altriemnti calcolo e lo inserisco
                                                if (((Vector) pagesTemp.elementAt(rs)).size() > (cs + 1)) {

                                                    second = new Double(Double.parseDouble((String) ((Vector) pagesTemp.elementAt(rs)).elementAt(cs)));

                                                } else {

                                                    serializedObj = (String) ((Vector) values.getPagesSerializedObjList().elementAt(rs)).elementAt(cs);

                                                    if (docobjs) {
                                                        jso = new JSONObject(serializedObj);
                                                        jso = new JSONObject(Session.getAnagBeanDAO().getSerializedAnagBeanFromNumRec((String) jso.get("typeHeadObj"), jso.getInt("numRecHeadObj")));

                                                    } else {
                                                        jso = new JSONObject(serializedObj);
                                                    }


                                                    if (jso.get(sortField.getId()) == null) {
                                                        second = new Double(0);
                                                    } else {
                                                        second = new Double(jso.getDouble(sortField.getId()));
                                                    }
                                                    

                                                    ((Vector) pagesTemp.elementAt(rs)).addElement(String.valueOf(((Double) second).doubleValue()));

                                                }


                                                if (((Double) first).doubleValue() > ((Double) second).doubleValue()) {
                                                    swap = true;
                                                }
                                            } else {


                                                //se non esiste la riga la metto
                                                if (pagesTemp.size() < (r + 1)) {
                                                    pagesTemp.addElement(new Vector(sizeColsuc));
                                                }

                                                //se esiste la colonna prelevo il valore altriemnti calcolo e lo inserisco
                                                if (((Vector) pagesTemp.elementAt(r)).size() > (c + 1)) {

                                                    first = (String) ((Vector) pagesTemp.elementAt(r)).elementAt(c);

                                                } else {

                                                    serializedObj = (String) ((Vector) values.getPagesSerializedObjList().elementAt(r)).elementAt(c);

                                                    if (docobjs) {
                                                        jso = new JSONObject(serializedObj);
                                                        jso = new JSONObject(Session.getAnagBeanDAO().getSerializedAnagBeanFromNumRec((String) jso.get("typeHeadObj"), jso.getInt("numRecHeadObj")));

                                                    } else {
                                                        jso = new JSONObject(serializedObj);
                                                    }

                                                    first = (String) jso.get(sortField.getId());


                                                    ((Vector) pagesTemp.elementAt(r)).addElement(first);

                                                }


                                                //se non esiste la riga la metto
                                                if (pagesTemp.size() < (rs + 1)) {
                                                    pagesTemp.addElement(new Vector(sizeColsuc));
                                                }

                                                //se esiste la colonna prelevo il valore altriemnti calcolo e lo inserisco
                                                if (((Vector) pagesTemp.elementAt(rs)).size() > (cs + 1)) {

                                                    second = (String) ((Vector) pagesTemp.elementAt(rs)).elementAt(cs);

                                                } else {

                                                    serializedObj = (String) ((Vector) values.getPagesSerializedObjList().elementAt(rs)).elementAt(cs);
                                                    if (docobjs) {
                                                        jso = new JSONObject(serializedObj);
                                                        jso = new JSONObject(Session.getAnagBeanDAO().getSerializedAnagBeanFromNumRec((String) jso.get("typeHeadObj"), jso.getInt("numRecHeadObj")));

                                                    } else {
                                                        jso = new JSONObject(serializedObj);
                                                    }
                                                    second = (String) jso.get(sortField.getId());

                                                    ((Vector) pagesTemp.elementAt(rs)).addElement(second);

                                                }

                                                int result = ((String) first).compareTo((String) second);
                                                if (result > 0) {
                                                    swap = true;
                                                }

                                            }

                                            if (swap) {

                                                //title
                                                temp = new String((String) ((Vector) values.getPagesValue().elementAt(r)).elementAt(c));
                                                ((Vector) values.getPagesValue().elementAt(r)).setElementAt(new String((String) ((Vector) values.getPagesValue().elementAt(rs)).elementAt(cs)), c);
                                                ((Vector) values.getPagesValue().elementAt(rs)).setElementAt(temp, cs);
                                                //serialized obj
                                                temp = new String((String) ((Vector) values.getPagesSerializedObjList().elementAt(r)).elementAt(c));
                                                ((Vector) values.getPagesSerializedObjList().elementAt(r)).setElementAt(new String((String) ((Vector) values.getPagesSerializedObjList().elementAt(rs)).elementAt(cs)), c);
                                                ((Vector) values.getPagesSerializedObjList().elementAt(rs)).setElementAt(temp, cs);
                                                //pagesTemp
                                                temp = new String((String) ((Vector) pagesTemp.elementAt(r)).elementAt(c));
                                                ((Vector) pagesTemp.elementAt(r)).setElementAt(new String((String) ((Vector) pagesTemp.elementAt(rs)).elementAt(cs)), c);
                                                ((Vector) pagesTemp.elementAt(rs)).setElementAt(temp, cs);





                                            }


                                        }

                                    }

                                }

                            }


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setPercentage(100);

                        setTerminate(true);


                        this.onComplete();


                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }




                    }

                    public void onComplete() {


                        //#debug info
                        System.out.println("STATUS!!:" + this.getStatus());

                        if (this.getStatus() == 5) {

                            e = new Event();

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

                            }


                            if (((String) getParams().get("callForm")).equals("PageListValue")) {
                                Session.getMngEvent().handleEvent(ManageEvent.REBUILD_PAGELISTOBJS_RESULT_EVENT, e);
                            } else if (((String) getParams().get("callForm")).equals("SearchResults")) {
                                Session.getMngEvent().handleEvent(ManageEvent.REBUILD_SEARCH_RESULT_EVENT, e);
                            }



                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_SEARCH_OBJ:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.search")) {

                    private PaginatedValues pgs = null;

                    public void run() {

                        try {

                            SearchForm searchForm = (SearchForm) getParams().get("searchForm");
                            Field searchField = searchForm.getCurrentSearchField();
                            String nodeDesc = searchForm.getCurrentTargetID();

                            String searchText = null;
                            long dateFrom = 0;
                            long dateTo = 0;
                            boolean dateField = false;

                            boolean searchTextnull = false;
                            boolean controlMatching = false;

                            //se è una ricerca da workflow devo fare corrispondenze su due campi
                            if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_WORKFLOW) {
                                controlMatching = true;
                            }


                            if (searchField != null) {

                                if (searchField.getTypeUI().equals("DATE")) {

                                    dateField = true;
                                    dateFrom = Date_Util.getStampFromDate(searchForm.getSearchDateFrom().getDate()).longValue();
                                    dateTo = Date_Util.getStampFromDate(searchForm.getSearchDateTo().getDate()).longValue();

                                } else {

                                    searchText = searchForm.getSearchText().getText();

                                    if ((searchText == null) || (searchText.equals(""))) {
                                        searchTextnull = true;
                                    }
                                }


                            }

                            try {

                                searchForm.setPagesValue(null);
                                searchForm.setResults(null);
                                if (searchForm.getForm().getTabCount() > 1) {
                                    searchForm.getForm().removeTab(1);
                                }

                            } catch (Exception e) {
                            }


                            //if workflow load index targeted objs

                            Vector listTargetIndex = null;

                            if (controlMatching) {


                                Hashtable hashIndexObj = ((Hashtable) Session.getMngIndex().getHashIndex().get(searchForm.getCurrentTypeObj() + "_" + searchForm.getIdFlowField()));

                                if ((hashIndexObj != null) && (hashIndexObj.containsKey(nodeDesc))) {

                                    //take rms numRec for field index value
                                    int numRecIndexRms = ((Integer) hashIndexObj.get(nodeDesc)).intValue();

                                    int numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                                    RecordStore rms = RecordStore.openRecordStore(searchForm.getCurrentTypeObj() + "_" + searchForm.getIdFlowField() + "_" + String.valueOf(numRMS), false);

                                    int realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);

                                    int sizeRecData = rms.getRecordSize(realRecID);

                                    byte[] recData = new byte[sizeRecData];

                                    int dataLen = rms.getRecord(realRecID, recData, 0);

                                    String str = (new String(recData, 0, dataLen)).trim();

                                    rms.closeRecordStore();

                                    Index idx = new Index();

                                    idx.unserialize(str);

                                    listTargetIndex = idx.getIndexs();

                                }

                            }

                            boolean loadOnlyListTargetIndex = false;

                            StringItem mex = null;
                            Gauge gauge = null;

                            //if workflow and no search field load all target objs
                            if ((controlMatching) && (searchField == null)) {

                                if ((listTargetIndex != null) && (listTargetIndex.size() > 0)) {
                                    loadOnlyListTargetIndex = true;
                                }

                            } else {

                                boolean novalue = false;

                                if ((controlMatching) && ((listTargetIndex == null) || (listTargetIndex.size() == 0))) {
                                    novalue = true;
                                }

                                if ((!searchTextnull) && (!novalue)) {

                                    boolean index = false;
                                    boolean cluster = false;
                                    boolean search = false;

                                    if (searchField.hasProperty("index")) {
                                        index = true;
                                    } else if (searchField.hasProperty("cluster")) {
                                        cluster = true;
                                    } else if (searchField.hasProperty("search")) {
                                        search = true;
                                    }


                                    //create tab Results 
                                    searchForm.getForm().addNewTab(Locale.get("SearchForm.results"), null);

                                    //add interrupt command
                                    searchForm.getForm().addCommand(SearchForm.interupt);

                                    //#style mex
                                    mex = new StringItem(null, Locale.get("message.searching"));
                                    searchForm.getForm().append(1, mex);

                                    searchForm.screenStateChanged(searchForm.getForm());

                                    //create gauge
                                    //#style gaugeItemIndefinite
                                    gauge = new Gauge(null, true, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
                                    gauge.setLayout(Gauge.LAYOUT_CENTER | Gauge.LAYOUT_VCENTER);
                                    searchForm.getForm().append(1, gauge);

                                    searchForm.getForm().setActiveTab(1);
                                    searchForm.screenStateChanged(searchForm.getForm());

                                    //create paginatevalues
                                    Vector paginatesTitles = new Vector();
                                    Vector paginatesSerialized = new Vector();

                                    //paginate results                          
                                    int maxObjsPages = ((Integer) ((Hashtable) Session.getListConfig().get("View")).get("maxItemsForPage")).intValue();

                                    //metto la prima pagina
                                    paginatesTitles.addElement(new Vector(maxObjsPages));
                                    paginatesSerialized.addElement(new Vector(maxObjsPages));

                                    pgs = new PaginatedValues(1, paginatesTitles, paginatesSerialized, searchForm.getCurrentTypeObj(), searchField.getId(), null);

                                    searchForm.setPagesValue(pgs);

                                    int pages = 0;

                                    ChoiceItem title = null;

                                    int numResults = 0;

                                    if (index) {

                                        Hashtable hashIndexObj = ((Hashtable) Session.getMngIndex().getHashIndex().get(searchForm.getCurrentTypeObj() + "_" + searchField.getId()));

                                        if (hashIndexObj != null) {


                                            Enumeration en = hashIndexObj.keys();

                                            boolean aggiungiValori = false;

                                            while (en.hasMoreElements()) {

                                                if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                    break;
                                                }

                                                String key = (String) en.nextElement();

                                                if (dateField) {

                                                    long keyVal = Long.parseLong(key);

                                                    if ((keyVal >= dateFrom) && (keyVal <= dateTo)) {

                                                        aggiungiValori = true;

                                                    }

                                                } else {

                                                    if (key.toUpperCase().indexOf(searchText.toUpperCase()) > -1) {

                                                        aggiungiValori = true;

                                                    }
                                                }


                                                if (aggiungiValori) {

                                                    aggiungiValori = false;

                                                    RecordStore rmsObj = null;

                                                    //take rms numRec for field index value
                                                    int numRecIndexRms = ((Integer) hashIndexObj.get(key)).intValue();

                                                    int numRMS = Session.getMngIndex().getNumRMS(numRecIndexRms);

                                                    RecordStore rms = RecordStore.openRecordStore(searchForm.getCurrentTypeObj() + "_" + searchField.getId() + "_" + String.valueOf(numRMS), false);

                                                    int realRecID = Session.getMngIndex().getRealRecID(numRecIndexRms, numRMS);

                                                    int sizeRecData = rms.getRecordSize(realRecID);

                                                    byte[] recData = new byte[sizeRecData];

                                                    int dataLen = rms.getRecord(realRecID, recData, 0);

                                                    String str = (new String(recData, 0, dataLen)).trim();

                                                    rms.closeRecordStore();

                                                    Index idx = new Index();

                                                    idx.unserialize(str);


                                                    int numRecObj = 0;

                                                    int o = 0;

                                                    numRMS = 0;
                                                    //  int oldNumRMS = 0;
                                                    realRecID = 0;

                                                    Hashtable openedRMS = new Hashtable();
                                                    JSONObject jso = null;

                                                    Vector listIndex = null;

                                                    if (controlMatching) {
                                                        listIndex = Search_Util.getMatchingIntegerVector(listTargetIndex, idx.getIndexs());

                                                    } else {

                                                        listIndex = idx.getIndexs();
                                                    }

                                                    int sizeIndexs = listIndex.size();

                                                    //get all obj by index
                                                    for (int d = 0; d < sizeIndexs; d++) {

                                                        if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                            break;
                                                        }

                                                        numRecObj = ((Integer) listIndex.elementAt(d)).intValue();

                                                        numRMS = Session.getMngIndex().getNumRMS(numRecObj);

                                                        if (!openedRMS.containsKey(new Integer(numRMS))) {

                                                            rmsObj = RecordStore.openRecordStore(searchForm.getCurrentTypeObj() + "_" + String.valueOf(numRMS), false);
                                                            openedRMS.put(new Integer(numRMS), rmsObj);

                                                        }

                                                        realRecID = Session.getMngIndex().getRealRecID(numRecObj, numRMS);

                                                        sizeRecData = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecordSize(realRecID);

                                                        recData = new byte[sizeRecData];

                                                        dataLen = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecord(realRecID, recData, 0);

                                                        str = (new String(recData, 0, dataLen)).trim();

                                                        jso = new JSONObject(str);

                                                        if ((searchForm.getPagesValue() == null)) {
                                                            break;                                                    //indexableObjClass.unserialize(str);
                                                        }
                                                        if ((jso.has("title"))) {

                                                            numResults++;

                                                            if (numResults == 1) {

                                                                de.enough.polish.ui.ChoiceGroup results = null;

                                                                //create results choiceGroup                                                                 
                                                                if (controlMatching) {
                                                                    //#style list
                                                                    results = new de.enough.polish.ui.ChoiceGroup(null, ChoiceGroup.MULTIPLE);

                                                                } else {

                                                                    //#style list
                                                                    results = new de.enough.polish.ui.ChoiceGroup(null, ChoiceGroup.EXCLUSIVE);

                                                                }

                                                                searchForm.setResults(results);
                                                                searchForm.getResults().setItemCommandListener(searchForm);
                                                                searchForm.getForm().append(1, searchForm.getResults());
                                                                searchForm.getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(searchForm.getCurrentPage() + 1) + "/" + (pages + 1));

                                                                if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_STANDARD) {

                                                                    searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                    searchForm.getResults().addCommand(ManageViewNavigation.updateCommand);


                                                                } else if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_NEWDOC) {

                                                                    searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                    SearchForm.createDocCommand = new Command(Locale.get("command.create") + " " + searchForm.getCurrentTypeDoc(), Command.BACK, 0);
                                                                    searchForm.getResults().addCommand(SearchForm.createDocCommand);

                                                                } else if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_CART) {

                                                                    searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                    searchForm.getResults().addCommand(SearchForm.add);
                                                                    searchForm.getResults().addCommand(SearchForm.edit);

                                                                } else if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_WORKFLOW) {

                                                                    searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                    Integer activityID = Session.getMngWrkFlw().getActivityIDNextHighPriority(searchForm.getCurrentTypeObj(), nodeDesc);

                                                                    if (activityID != null) {

                                                                        SearchForm.doactionCommand = new Command(Session.getMngWrkFlw().getActivityIDDesc(activityID.intValue()), Command.BACK, 0);
                                                                        searchForm.getResults().addCommand(SearchForm.doactionCommand);
                                                                    }

                                                                }


                                                            }

                                                            //controllo che non abbia superato il max results for page altr aggiungo la pagina
                                                            if (((Vector) pgs.getPagesValue().elementAt(pages)).size() == maxObjsPages) {

                                                                if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                                    break;
                                                                }

                                                                pgs.getPagesValue().addElement(new Vector(maxObjsPages));
                                                                pgs.getPagesSerializedObjList().addElement(new Vector(maxObjsPages));
                                                                pages++;

                                                                if (pages + 1 == 2) {
                                                                    searchForm.getResults().addCommand(SearchForm.nextPage);
                                                                }

                                                                pgs.setNumPages(pages + 1);
                                                                searchForm.getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(searchForm.getCurrentPage() + 1) + "/" + (pages + 1));

                                                            }

                                                            ((Vector) pgs.getPagesValue().elementAt(pages)).addElement(jso.getString("title"));

                                                            ((Vector) pgs.getPagesSerializedObjList().elementAt(pages)).addElement(str);


                                                            //add into first pages     
                                                            if (pages == 0) {

                                                                if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                                    break;
                                                                }

                                                                if (controlMatching) {
                                                                    //#style listOption
                                                                    title = new ChoiceItem(jso.getString("title"), null, Choice.MULTIPLE);
                                                                } else {
                                                                    //#style listOption
                                                                    title = new ChoiceItem(jso.getString("title"), null, Choice.IMPLICIT);

                                                                }

                                                                searchForm.getResults().append(title);

                                                                if (numResults == 1) {
                                                                    if (!controlMatching) {
                                                                        searchForm.getResults().setSelectedIndex(0, true);
                                                                    }
                                                                    searchForm.getResults().focusChild(0);

                                                                }

                                                            }


                                                        } else {
                                                            //#debug error
                                                            System.out.println("No title for obj: " + str);
                                                        }

                                                        o++;

                                                    }

                                                    Enumeration en2 = openedRMS.keys();

                                                    while (en2.hasMoreElements()) {

                                                        ((RecordStore) openedRMS.get(en2.nextElement())).closeRecordStore();

                                                    }


                                                }


                                            }





                                        }




                                    } else if ((cluster) || (search)) {


                                        int lastID = 0;
                                        int sizeRecData = 0;
                                        RecordStore rms = null;
                                        byte[] recData = null;
                                        int dataLen = 0;
                                        String str = null;
                                        JSONObject jso = null;
                                        int relativeRecID = 0;

                                        boolean addresult = false;
                                        String keyValStr = null;
                                        long keyVal = 0;


                                        boolean docobjs = false;
                                        String typeObj = null;

                                        if (((Structure) Session.getListStructureObj().get(pgs.getTypeObj())).getType().equals("DocObj")) {
                                            docobjs = true;
                                            typeObj = ((DocObj) Session.getListStructureObj().get(pgs.getTypeObj())).getTypeHeadObj();
                                        } else {
                                            typeObj = pgs.getTypeObj();                                        //count rms typeOby
                                        }
                                        int numObjs = Session.getMngIndex().getHashCountRMSObjs().get(typeObj) == null ? 0 : ((Integer) Session.getMngIndex().getHashCountRMSObjs().get(typeObj)).intValue();

                                        if (numObjs > 0) {


                                            int rest = (int) numObjs % (int) Session.getMaxLinesRMS();
                                            int numRms = (int) numObjs / (int) Session.getMaxLinesRMS();
                                            if (rest > 0) {
                                                numRms++;                                            //loop on all rms                                
                                            }
                                            for (int i = 0; i < numRms; i++) {

                                                if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                    break;
                                                }

                                                rms = RecordStore.openRecordStore(typeObj + "_" + (i + 1), false);

                                                lastID = rms.getNextRecordID();

                                                for (int r = 1; r < lastID; ++r) {

                                                    try {

                                                        sizeRecData = rms.getRecordSize(r);

                                                        recData = new byte[sizeRecData];

                                                        dataLen = rms.getRecord(r, recData, 0);

                                                        str = (new String(recData, 0, dataLen)).trim();

                                                        jso = new JSONObject(str);

                                                        addresult = false;

                                                        if (dateField) {

                                                            if (jso.has(searchField.getId())) {

                                                                if ((( jso.get(searchField.getId())) != null) ) {

                                                                    if (((jso.get(searchField.getId())) != null) ) {
                                                                        keyVal = jso.getLong(searchField.getId());
                                                                    } else {
                                                                        keyVal = 0;
                                                                    }

                                                                    if ((keyVal >= dateFrom) && (keyVal <= dateTo)) {

                                                                        if (controlMatching) {

                                                                            relativeRecID = Session.getMngIndex().getRelativeRecID(r, i + 1);

                                                                            if (listTargetIndex.contains(new Integer(relativeRecID))) {
                                                                                addresult = true;
                                                                            } else {
                                                                                addresult = false;
                                                                            }

                                                                        } else {
                                                                            addresult = true;
                                                                        }

                                                                    } else {
                                                                        addresult = false;
                                                                    }
                                                                } else {
                                                                    addresult = false;
                                                                }

                                                            }



                                                        } else {

                                                            if (jso.has(searchField.getId())) {

                                                                keyValStr = jso.getString(searchField.getId());

                                                                if (keyValStr.toUpperCase().indexOf(searchText.toUpperCase()) > -1) {

                                                                    if (controlMatching) {

                                                                        relativeRecID = Session.getMngIndex().getRelativeRecID(r, i + 1);

                                                                        if (listTargetIndex.contains(new Integer(relativeRecID))) {
                                                                            addresult = true;
                                                                        } else {
                                                                            addresult = false;
                                                                        }

                                                                    } else {
                                                                        addresult = true;
                                                                    }


                                                                } else {
                                                                    addresult = false;
                                                                }
                                                            }

                                                        }

                                                        if (addresult) {

                                                            if ((searchForm.getPagesValue() == null)) {
                                                                break;                                                    //indexableObjClass.unserialize(str);
                                                            }

                                                            //if doc must read sourceNumRec obj
                                                            if (docobjs) {

                                                                str = Session.getDocBeanDAO().getSerializedDocBeanFromNumRec(pgs.getTypeObj(), jso.getInt("sourceNumRec"));
                                                                jso = new JSONObject(str);


                                                            }


                                                            if ((jso.has("title"))) {

                                                                numResults++;

                                                                if (numResults == 1) {

                                                                    de.enough.polish.ui.ChoiceGroup results = null;

                                                                    //create results choiceGroup                                                                 
                                                                    if (controlMatching) {
                                                                        //#style list
                                                                        results = new de.enough.polish.ui.ChoiceGroup(null, ChoiceGroup.MULTIPLE);

                                                                    } else {

                                                                        //#style list
                                                                        results = new de.enough.polish.ui.ChoiceGroup(null, ChoiceGroup.EXCLUSIVE);

                                                                    }


                                                                    searchForm.setResults(results);
                                                                    searchForm.getResults().setItemCommandListener(searchForm);
                                                                    searchForm.getForm().append(1, searchForm.getResults());
                                                                    searchForm.getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(searchForm.getCurrentPage() + 1) + "/" + (pages + 1));

                                                                    if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_STANDARD) {


                                                                        searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                        searchForm.getResults().addCommand(ManageViewNavigation.updateCommand);


                                                                    } else if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_NEWDOC) {

                                                                        searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                        SearchForm.createDocCommand = new Command(Locale.get("command.create") + " " + searchForm.getCurrentTypeDoc(), Command.BACK, 0);
                                                                        searchForm.getResults().addCommand(SearchForm.createDocCommand);

                                                                    } else if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_CART) {

                                                                        searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                        searchForm.getResults().addCommand(SearchForm.add);
                                                                        searchForm.getResults().addCommand(SearchForm.edit);

                                                                    } else if (searchForm.getTypeFORM() == MainForm.TYPEFORM_SEARCH_WORKFLOW) {

                                                                        searchForm.getResults().addCommand(SearchForm.viewCommand);
                                                                        Integer activityID = Session.getMngWrkFlw().getActivityIDNextHighPriority(searchForm.getCurrentTypeObj(), nodeDesc);

                                                                        if (activityID != null) {

                                                                            SearchForm.doactionCommand = new Command(Session.getMngWrkFlw().getActivityIDDesc(activityID.intValue()), Command.BACK, 0);
                                                                            searchForm.getResults().addCommand(SearchForm.doactionCommand);
                                                                        }

                                                                    }

                                                                }

                                                                //controllo che non abbia superato il max results for page altr aggiungo la pagina
                                                                if (((Vector) pgs.getPagesValue().elementAt(pages)).size() == maxObjsPages) {

                                                                    if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                                        break;
                                                                    }

                                                                    pgs.getPagesValue().addElement(new Vector(maxObjsPages));
                                                                    pgs.getPagesSerializedObjList().addElement(new Vector(maxObjsPages));
                                                                    pages++;

                                                                    if (pages + 1 == 2) {
                                                                        searchForm.getResults().addCommand(SearchForm.nextPage);
                                                                    }
                                                                    pgs.setNumPages(pages + 1);
                                                                    searchForm.getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(searchForm.getCurrentPage() + 1) + "/" + (pages + 1));

                                                                }

                                                                ((Vector) pgs.getPagesValue().elementAt(pages)).addElement(jso.getString("title"));

                                                                ((Vector) pgs.getPagesSerializedObjList().elementAt(pages)).addElement(str);


                                                                //add into first pages     
                                                                if (pages == 0) {

                                                                    if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                                        break;
                                                                    }

                                                                    if (controlMatching) {
                                                                        //#style listOption
                                                                        title = new ChoiceItem(jso.getString("title"), null, Choice.MULTIPLE);
                                                                    } else {
                                                                        //#style listOption
                                                                        title = new ChoiceItem(jso.getString("title"), null, Choice.IMPLICIT);

                                                                    }


                                                                    searchForm.getResults().append(title);

                                                                    if (numResults == 1) {
                                                                        if (!controlMatching) {
                                                                            searchForm.getResults().setSelectedIndex(0, true);
                                                                        }
                                                                        searchForm.getResults().focusChild(0);


                                                                    }

                                                                }


                                                            } else {
                                                                //#debug error
                                                                System.out.println("No title for obj: " + str);
                                                            }


                                                        }

                                                    } catch (InvalidRecordIDException e) {
                                                        continue;
                                                    }
                                                }


                                                rms.closeRecordStore();


                                            }




                                        }




                                    }


                                } else {

                                    if ((controlMatching) && (listTargetIndex != null) && (listTargetIndex.size() > 0)) {
                                        loadOnlyListTargetIndex = true;
                                    }

                                }


                            }


                            if (loadOnlyListTargetIndex) {

                                //create tab Results 
                                searchForm.getForm().addNewTab(Locale.get("SearchForm.results"), null);

                                //add interrupt command
                                searchForm.getForm().addCommand(SearchForm.interupt);

                                //#style mex
                                mex = new StringItem(null, Locale.get("message.searching"));
                                searchForm.getForm().append(1, mex);

                                searchForm.screenStateChanged(searchForm.getForm());

                                //create gauge
                                //#style gaugeItemIndefinite
                                gauge = new Gauge(null, true, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
                                gauge.setLayout(Gauge.LAYOUT_CENTER | Gauge.LAYOUT_VCENTER);
                                searchForm.getForm().append(1, gauge);

                                searchForm.getForm().setActiveTab(1);
                                searchForm.screenStateChanged(searchForm.getForm());

                                //create paginatevalues
                                Vector paginatesTitles = new Vector();
                                Vector paginatesSerialized = new Vector();

                                //paginate results                          
                                int maxObjsPages = ((Integer) ((Hashtable) Session.getListConfig().get("View")).get("maxItemsForPage")).intValue();

                                //metto la prima pagina
                                paginatesTitles.addElement(new Vector(maxObjsPages));
                                paginatesSerialized.addElement(new Vector(maxObjsPages));

                                pgs = new PaginatedValues(1, paginatesTitles, paginatesSerialized, searchForm.getCurrentTypeObj(), searchField != null ? searchField.getId() : "", null);

                                searchForm.setPagesValue(pgs);

                                int pages = 0;

                                ChoiceItem title = null;

                                int numResults = 0;

                                RecordStore rmsObj = null;

                                int sizeIndexs = listTargetIndex.size();

                                int numRecObj = 0;

                                int o = 0;

                                int numRMS = 0;
                                //  int oldNumRMS = 0;
                                int realRecID = 0;

                                Hashtable openedRMS = new Hashtable();
                                JSONObject jso = null;

                                //get all obj by index
                                for (int d = 0; d < sizeIndexs; d++) {

                                    if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                        break;
                                    }
                                    numRecObj = ((Integer) listTargetIndex.elementAt(d)).intValue();

                                    numRMS = Session.getMngIndex().getNumRMS(numRecObj);

                                    if (!openedRMS.containsKey(new Integer(numRMS))) {

                                        rmsObj = RecordStore.openRecordStore(searchForm.getCurrentTypeObj() + "_" + String.valueOf(numRMS), false);
                                        openedRMS.put(new Integer(numRMS), rmsObj);

                                    }

                                    realRecID = Session.getMngIndex().getRealRecID(numRecObj, numRMS);

                                    int sizeRecData = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecordSize(realRecID);

                                    byte[] recData = new byte[sizeRecData];

                                    int dataLen = ((RecordStore) openedRMS.get(new Integer(numRMS))).getRecord(realRecID, recData, 0);

                                    String str = (new String(recData, 0, dataLen)).trim();

                                    jso = new JSONObject(str);

                                    if ((searchForm.getPagesValue() == null)) {
                                        break;                                                    //indexableObjClass.unserialize(str);
                                    }
                                    if ((jso.has("title"))) {

                                        numResults++;

                                        if (numResults == 1) {

                                            //create results choiceGroup
                                            //#style list
                                            de.enough.polish.ui.ChoiceGroup results = new de.enough.polish.ui.ChoiceGroup(null, ChoiceGroup.MULTIPLE);

                                            searchForm.setResults(results);
                                            searchForm.getResults().setItemCommandListener(searchForm);
                                            searchForm.getForm().append(1, searchForm.getResults());
                                            searchForm.getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(searchForm.getCurrentPage() + 1) + "/" + (pages + 1));


                                            searchForm.getResults().addCommand(SearchForm.viewCommand);
                                            Integer activityID = Session.getMngWrkFlw().getActivityIDNextHighPriority(searchForm.getCurrentTypeObj(), nodeDesc);

                                            if (activityID != null) {

                                                SearchForm.doactionCommand = new Command(Session.getMngWrkFlw().getActivityIDDesc(activityID.intValue()), Command.BACK, 0);
                                                searchForm.getResults().addCommand(SearchForm.doactionCommand);
                                            }


                                        }

                                        //controllo che non abbia superato il max results for page altr aggiungo la pagina
                                        if (((Vector) pgs.getPagesValue().elementAt(pages)).size() == maxObjsPages) {

                                            if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                break;
                                            }

                                            pgs.getPagesValue().addElement(new Vector(maxObjsPages));
                                            pgs.getPagesSerializedObjList().addElement(new Vector(maxObjsPages));
                                            pages++;

                                            if (pages + 1 == 2) {
                                                searchForm.getResults().addCommand(SearchForm.nextPage);
                                            }

                                            pgs.setNumPages(pages + 1);
                                            searchForm.getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(searchForm.getCurrentPage() + 1) + "/" + (pages + 1));

                                        }

                                        ((Vector) pgs.getPagesValue().elementAt(pages)).addElement(jso.getString("title"));

                                        ((Vector) pgs.getPagesSerializedObjList().elementAt(pages)).addElement(str);


                                        //add into first pages     
                                        if (pages == 0) {

                                            if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100) || (searchForm.getPagesValue() == null)) {
                                                break;
                                            }

                                            if (controlMatching) {
                                                //#style listOption
                                                title = new ChoiceItem(jso.getString("title"), null, Choice.MULTIPLE);
                                            } else {
                                                //#style listOption
                                                title = new ChoiceItem(jso.getString("title"), null, Choice.IMPLICIT);

                                            }

                                            searchForm.getResults().append(title);

                                            if (numResults == 1) {
                                                if (!controlMatching) {
                                                    searchForm.getResults().setSelectedIndex(0, true);
                                                }
                                                searchForm.getResults().focusChild(0);

                                            }

                                        }


                                    } else {
                                        //#debug error
                                        System.out.println("No title for obj: " + str);
                                    }

                                    o++;

                                }

                                Enumeration en2 = openedRMS.keys();

                                while (en2.hasMoreElements()) {

                                    ((RecordStore) openedRMS.get(en2.nextElement())).closeRecordStore();

                                }


                            }



                            if ((!this.isFailed()) && (!this.isCancelled()) && (this.getPercentage() < 100)) {

                                //if no results
                                if ((searchTextnull) || (pgs == null) || (pgs.getPagesValue() == null) || (((Vector) (pgs.getPagesValue().elementAt(0))).size() == 0)) {

                                    searchForm.getForm().setActiveTab(0);
                                    searchForm.setResults(null);
                                    searchForm.setPagesValue(null);

                                    if (searchForm.getForm().getTabCount() > 1) {
                                        searchForm.getForm().removeTab(1);
                                    }

                                    Alert alert = ManageViewNavigation.showAlert("Info.", Locale.get("message.noresults"), null, AlertType.INFO);

                                    //alert.setCommandListener(searchForm);


                                    searchForm.getForm().setActiveTab(0);

                                    //searchForm.getForm().repaint();
                                   

                                    searchForm.screenStateChanged(searchForm.getForm());

                                } else {

                                    searchForm.loadOrderByCommand();

                                }


                            }

                            //remove interrupt command
                            searchForm.getForm().removeCommand(SearchForm.interupt);

                            if (searchForm.getForm().getTabCount() > 1) {

                                try {

                                    searchForm.getForm().delete(1, mex);
                                    //delete gauge
                                    searchForm.getForm().delete(1, gauge);

                                    //if results set focus first
                                    if (((Vector) (pgs.getPagesValue().elementAt(0))).size() > 0) {

                                        searchForm.setSelectItm(0);
                                        if (!controlMatching) {
                                            searchForm.getResults().setSelectedIndex(searchForm.getSelectItm(), true);
                                        }
                                        searchForm.getResults().focusChild(searchForm.getSelectItm());

                                    }

                                } catch (Exception e) {
                                }

                            }



                        } catch (Exception ex) {

                            setFailed(true);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());

                            ManageViewNavigation.showAlert(Locale.get("message.error"), " Exception: " + ex.getMessage() == null ? "null" : ex.getMessage(), null, AlertType.INFO);


                        }


                        setTerminate(true);

                        Session.getMngThread().destroyThread(getThreadID().intValue());


                    }
                });


                break;


            case THREAD_DUPLICATE_DOC:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.duplicatedoc")) {

                    private DocBean newdoc = null;

                    public void run() {


                        try {


                            DocBean docSource = (DocBean) getParams().get("docbean");
                            String typeNewDoc = (String) getParams().get("typeDoc");

                            //create new head
                            AnagBean newHeadObj = Session.getMngService().getNewAnaBeanFromMatchingSourceAnagBean(((DocObj) Session.getListStructureObj().get(typeNewDoc)).getTypeHeadObj(), docSource.getHeadObj());

                            //set source info
                            newHeadObj.setField("sourceNumRec", new Integer(Session.getMngIndex().getNextRelativeRecordIDnoSaveValue(typeNewDoc)));
                            newHeadObj.setField("sourceType", typeNewDoc);


                            //workflow
                            String flowField = ((AnagObj) Session.getListStructureObj().get(newHeadObj.getType())).getFlowField();
                            String flowtargetDTField = ((AnagObj) Session.getListStructureObj().get(newHeadObj.getType())).getFlowTargetDTField();

                            //SET START NODE
                            newHeadObj.setField(flowField, Session.getMngWrkFlw().getDescStartNodeID(typeNewDoc));
                            //delete targetDT
                            newHeadObj.setField(flowtargetDTField, null);

                            //persist
                            Session.getAnagBeanDAO().persist(newHeadObj);

                            //create docBean
                            newdoc = new DocBean(typeNewDoc, newHeadObj);

                            //RefLinkID
                            newdoc.setRefLinkID(docSource.getId());

                            //save document
                            Session.getDocBeanDAO().persist(newdoc);

                            setPercentage(5);
                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            //loop on sourcedoc rows
                            int sizeRows = docSource.getRows().size();

                            AnagBean newRw = null;
                            String typeNewRow = ((DocObj) Session.getListStructureObj().get(typeNewDoc)).getTypeRowObj();

                            double percentageDouble = 0;
                            double coefficienteIncremento = (double) 100 / (double) sizeRows;

                            for (int r = 0; r < sizeRows; r++) {

                                if (!((!this.isFailed() && !this.isCancelled() && this.getPercentage() < 100))) {
                                    break;
                                }

                                if (this.isPaused()) {
                                    waitUntilAliveIs();
                                }

                                newRw = Session.getMngService().getNewAnaBeanFromMatchingSourceAnagBean(typeNewRow, docSource.getRow(r));

                                //set source info
                                newRw.setField("sourceNumRec", newdoc.getNumRec());
                                newRw.setField("sourceType", typeNewDoc);

                                newdoc.addRowObj(newRw);

                                Session.getDocBeanDAO().persistNewRow(newdoc, newRw);

                                percentageDouble += coefficienteIncremento;
                                setPercentage((int) percentageDouble);
                                ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());


                            }


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setPercentage(100);

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {


                        //#debug info
                        System.out.println("STATUS!!:" + this.getStatus());

                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }




                            e = new Event();
                            e.setByName("DocBeanObj", newdoc);
                            e.setByName("callForm", "DuplicateDoc");

                            Session.getMngEvent().handleEvent(ManageEvent.VIEW_DOCBEANFORM_EVENT, e);


                            //alert duplicate!
                            ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.duplicate"), null, AlertType.CONFIRMATION);


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_ADD_NEW_ROWDOC_FROM_ANAGBEAN:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.duplicatedoc")) {

                    public void run() {


                        try {


                            DocBean doc = (DocBean) getParams().get("docbean");

                            setPercentage(30);
                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());


                            AnagBean newRow = Session.getMngService().getNewAnagBeanFromSourceAnagBean((String) getParams().get("typeObj"), (AnagBean) getParams().get("anagbean"), doc);

                            //add row to docBean
                            doc.addRowObj(newRow);

                            setPercentage(60);
                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            //save doc with new row
                            Session.getDocBeanDAO().persistNewRow(doc, newRow);

                            setPercentage(90);

                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setPercentage(100);

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {

                        //#debug info
                        System.out.println("STATUS!!:" + this.getStatus());

                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }

                            //alert add!
                            ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.add"), null, AlertType.CONFIRMATION);


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_DELETE_DOC:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.deletedoc")) {

                    public void run() {


                        try {

                            DocBean doc = (DocBean) getParams().get("docbean");


                            setPercentage(30);
                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            //delete current doc obj
                            Session.getDocBeanDAO().delete(doc);


                            setPercentage(100);


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }

                            //alert deleted!
                            Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infodelete"), Locale.get("message.deleted"), null, AlertType.CONFIRMATION);

                            alert.setCommandListener((AdmDocDetailForm) ManageViewNavigation.getCurrentDisplayed());


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_PERSIST_ANAGBEAN:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("alert.infosave")) {

                    public void run() {

                        boolean response = false;

                        try {

                            AnagBean anagBean = (AnagBean) getParams().get("AnagBeanObj");
                            ListItem listItms = (ListItem) getParams().get("FormObj");


                            setPercentage(30);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            response = Session.getAnagBeanDAO().persist(anagBean, listItms);

                            setPercentage(100);


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete(response);

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete(boolean response) {


                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }


                            if (response) {

                                Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.saved"), null, AlertType.INFO);

                                alert.setCommandListener((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed());


                            } else {

                                Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.nosaved"), null, AlertType.INFO);

                                alert.setCommandListener((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed());
                            }


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_COMMIT_ANAGBEAN:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("alert.infosave")) {

                    public void run() {

                        boolean response = false;

                        try {

                            AnagBean anagBean = (AnagBean) getParams().get("AnagBeanObj");
                            ListItem listItms = (ListItem) getParams().get("FormObj");


                            setPercentage(30);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            response = Session.getAnagBeanDAO().commitChange(anagBean, listItms);


                            setPercentage(100);



                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete(response);

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete(boolean response) {


                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }


                            if (response) {

                                Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.saved"), null, AlertType.INFO);

                                alert.setCommandListener((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed());


                            } else {

                                Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.nosaved"), null, AlertType.INFO);

                                alert.setCommandListener((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed());
                            }


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_ADD_EDIT_NEWROWDOCBEAN:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("alert.infosave")) {

                    public void run() {


                        try {

                            DocBean doc = (DocBean) getParams().get("doc");
                            AnagBean anagBean = (AnagBean) getParams().get("AnagBeanObj");
                            ListItem listItms = (ListItem) getParams().get("FormObj");

                            setPercentage(30);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            //save modified row
                            Session.getAnagBeanDAO().setAnagBeanFromListItms(anagBean, listItms);

                            //add row to docBean
                            doc.addRowObj(anagBean);

                            setPercentage(30);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            //save doc with new row
                            Session.getDocBeanDAO().persistNewRow(doc, anagBean);


                            setPercentage(100);


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }

                            //alert add!
                            Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.add"), null, AlertType.CONFIRMATION);

                            alert.setCommandListener((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed());



                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_CANCEL_UPDATE_DOCBEAN:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("alert.infosave")) {

                    private DocBean oldDoc = null;

                    public void run() {


                        try {

                            DocBean doc = (DocBean) getParams().get("doc");
                            oldDoc = (DocBean) getParams().get("oldDoc");


                            //delete current doc obj
                            Session.getDocBeanDAO().delete(doc);


                            setPercentage(30);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());



                            //save new source numRec
                            oldDoc.getHeadObj().setField("sourceNumRec", new Integer(Session.getMngIndex().getNextRelativeRecordIDnoSaveValue(oldDoc.getType())));
                            oldDoc.getHeadObj().setField("sourceType", oldDoc.getType());

                            //save old head
                            Session.getAnagBeanDAO().persist(oldDoc.getHeadObj());


                            setPercentage(50);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            Thread.sleep(2000);

                            //save old rows
                            int size = oldDoc.getRows().size();

                            for (int i = 0; i < size; i++) {

                                //modified with new doc numRec               
                                oldDoc.getRow(i).setField("sourceNumRec", new Integer(Session.getMngIndex().getNextRelativeRecordIDnoSaveValue(oldDoc.getType())));
                                oldDoc.getRow(i).setField("sourceType", oldDoc.getType());

                                Session.getAnagBeanDAO().persist(oldDoc.getRow(i));

                            }


                            setPercentage(80);

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                            //save old doc
                            Session.getDocBeanDAO().persist(oldDoc);

                            setPercentage(100);


                        } catch (Exception ex) {

                            setFailed(true);

                            ManageViewNavigation.showAlert("Error", "Exception: " + ex.getMessage() == null ? "Null" : ex.getMessage(), null, AlertType.INFO);


                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }


                            ((AdmDocDetailForm) ManageViewNavigation.getCurrentDisplayed()).prepareView(oldDoc, 0, null);

                            //alert reload!
                            ManageViewNavigation.showAlert(Locale.get("alert.cancel"), Locale.get("message.canceled"), null, AlertType.INFO);



                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;

            case THREAD_SEND_HTTP_DOC:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("alert.infosave")) {
                    // Convert special characters

                    private String URLencode(String s) {

                        if (s != null) {

                            s = s.trim();

                            return s;

//                            StringBuffer tmp = new StringBuffer();
//                            int i = 0;
//                            try {
//                                while (true) {
//                                    int b = (int) s.charAt(i++);
//                                    if ((b >= 0x30 && b <= 0x39) || (b >= 0x41 && b <= 0x5A) || (b >= 0x61 && b <= 0x7A)) {
//                                        tmp.append((char) b);
//                                    } else {
//                                        tmp.append("%");
//                                        if (b <= 0xf) {
//                                            tmp.append("0");
//                                        }
//                                        tmp.append(Integer.toHexString(b));
//                                    }
//                                }
//                            } catch (Exception e) {
//                            }
//                            return tmp.toString();



                        }
                        return null;
                    }
                    public String typeObj = null;
                    public String nextNode = null;
                    public String flowField = null;
                    public String flowTargetDTField = null;

                    public void run() {

//                        HttpConnection connection = null;
//                        InputStream inputstream = null;
//                        OutputStream outputStream = null;

                        String response = null;
                        Vector sendObjs = new Vector();

                        //try {

                        typeObj = (String) this.getParams().get("typeObj");
                        Vector listObj = (Vector) this.getParams().get("listObj");

                        ManageViewNavigation.getPrgsThread().setTextMex("Formatting data..");

                         try {

                        //transfor in serializeToSever
                        int sizeList = listObj.size();

                        JSONArray serializeServerObjs = new JSONArray();

                        DocBean docBeanObj = null;

                        JSONObject jsoDoc;

                        for (int i = 0; i < sizeList; i++) {

                            docBeanObj = new DocBean();
                                                        
                            docBeanObj.unserialize((String) listObj.elementAt(i));

                            sendObjs.addElement(docBeanObj);

                            jsoDoc=new JSONObject(docBeanObj.serializeToServer());

                            serializeServerObjs.put(jsoDoc);

                            //serializeServerObjs.addElement(docBeanObj.serializeToServer());

                            ManageViewNavigation.getPrgsThread().setTextMex("Formatting data.. " + i + "/" + sizeList);


                        }

                       

                            ManageViewNavigation.getPrgsThread().setTextMex("Connecting to server..");

                            //set url
//                                String url = "http://localhost:8084/ServerMobile/LoginServlet";
//
//                                ResponseService resp = Session.getMngService().doService(ManageService.GET_URL_CONNECTION_SERVICE, null);
//
//                                if (resp.getStatus()) {
//
//                                    url = (String) resp.getResponseObj();
//                                }

                            //set parameter
                            RequestParameters req = new RequestParameters(Session.getActiveUser().getLicenseNumber(), Session.getActiveUser().getPassword(),((DocObj) Session.getListStructureObj().get(typeObj)).getId(),"sendObjs", Session.getActiveUser().getUsername());
//                            req.setUser(Session.getActiveUser().getUsername());
//                            req.setPassword(Session.getActiveUser().getPassword());
//                            req.setLicenseNumber(Session.getActiveUser().getLicenseNumber());
//                            req.setTypeObj(((DocObj) Session.getListStructureObj().get(typeObj)).getId());
//
//                            req.setTypeReq("sendObjs");


//                                String postParameter = "ut=" + Session.getActiveUser().getUsername() + "&pw=" + Session.getActiveUser().getPassword() + "&licNum=" + Session.getActiveUser().getLicenseNumber();
//
//                                postParameter = postParameter + "&typeRequest=sendObjs&typeObj=" + typeObj;

                           // JSONArray jso = new JSONArray(serializeServerObjs);

                            //#debug info
                            System.out.println("Send to Server:" + serializeServerObjs.toString());

                            response = Session.getWebService().sendObj(req, serializeServerObjs.toString());

                            //#debug info
                            System.out.println("RESPONSE SERVER: " + response);


//                                postParameter = postParameter + "&objs=" + jso.toString();
//
//                                postParameter = URLencode(postParameter);

                            // int totalBytes = 0;


//                                String typeConnection = url.substring(0, url.indexOf(":"));
//
//                                if (typeConnection.equals("http")) {
//
//                                    connection = (HttpConnection) Connector.open(url);
//
//                                } else if (typeConnection.equals("https")) {
//
//                                    connection = (HttpsConnection) Connector.open(url);
//
//                                }
//
//
//                                //HTTP Request
//                                connection.setRequestMethod(HttpConnection.POST);
//
//                                connection.setRequestProperty("Accept-Encoding", "gzip");
//                                connection.setRequestProperty("Content-Length", "" + postParameter.getBytes().length);
//                                connection.setRequestProperty("User-Agent", "Profile/MIDP2.0 Configuration/CLDC-1.0");
//                                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                                connection.setRequestProperty("Connection", "close");
//
//                                outputStream = connection.openOutputStream();
//
//                                outputStream.write(postParameter.getBytes());
//
//
//                                // HTTP Response
//                                //#debug info
//                                System.out.println("Status Line Code: " + connection.getResponseCode());
//                                //#debug info
//                                System.out.println("Status Line Message: " + connection.getResponseMessage());
//
//                                if (connection.getResponseCode() == HttpConnection.HTTP_OK) {
//
//                                    ManageViewNavigation.getPrgsThread().setTextMex("Connection ready!");
//
//                                    //#debug info
//                                    System.out.println(
//                                            connection.getHeaderField(0) + " " + connection.getHeaderFieldKey(0));
//                                    //#debug info
//                                    System.out.println(
//                                            "Header Field Date: " + connection.getHeaderField("date"));
//
//
//                                    inputstream = connection.openInputStream();
//
//
//                                    String Header = connection.getHeaderField("Content-Encoding");
//
//                                    if (Header != null && Header.trim().equals("gzip")) {
//                                        inputstream = new GZipInputStream(inputstream, GZipInputStream.TYPE_GZIP, false);
//                                        //#debug info
//                                        System.out.println("httpClient: Compressed HttpConnection");
//                                    }
//
//
//                                    //  int length = (int) connection.getLength();
//
////                                    if (length != -1) {
////
////                                        byte incomingData[] = new byte[length];
////                                        inputstream.read(incomingData);
////                                        str = new String(incomingData);
////
////                                        totalBytes=length;
////
////                                        ManageViewNavigation.getPrgsThread().setTextMex("Read " + totalBytes + "Bytes");
////
////
////                                    } else {
//
//                                    ByteArrayOutputStream bytestream =
//                                            new ByteArrayOutputStream();
//                                    int ch;
//
//                                    while ((ch = inputstream.read()) != -1) {
//
//                                        if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100)) {
//                                            break;
//                                        }
//
//                                        bytestream.write(ch);
//                                        totalBytes = bytestream.size();
//
//                                        ManageViewNavigation.getPrgsThread().setTextMex("Read " + totalBytes + "Bytes");
//
//                                    }
//
//                                    str = new String(bytestream.toByteArray());
//
//                                    ManageViewNavigation.getPrgsThread().setTextMex("Read " + totalBytes + "Bytes");
//
//                                    bytestream.close();
//                                }
//
//                            } catch (Exception e) {
//
//                                setFailed(true);
//
//                                ManageViewNavigation.showAlert("Error", ("Exception: " + e.getMessage() == null ? "Null" : e.getMessage()), null, AlertType.INFO);
//
//                                //#debug error
//                                System.out.println("Caught Exception: " + e.toString());
//
//
//                            } finally {
//
//                                if (inputstream != null) {
//                                    try {
//                                        inputstream.close();
//                                    } catch (Exception error) {
//                                        //#debug error
//                                        System.out.println("Caught Exception: " + error.toString());
//                                    }
//                                }
//                                if (connection != null) {
//                                    try {
//                                        connection.close();
//                                    } catch (Exception error) {
//                                        //#debug error
//                                        System.out.println("Caught Exception: " + error.toString());
//                                    }
//                                }
//                            }


                            if ((!this.isFailed()) && (!this.isCancelled())) {

                                ManageViewNavigation.getPrgsThread().setTextMex("Connection close!");

                                

                                //if status true assign systemId to obj!! ...
                                JSONObject serverResponse = new JSONObject(response);

                                response = null;

                                if (serverResponse.getBoolean("status")) {

                                    JSONArray sysIDs = serverResponse.getJSONArray("sysIDs");

                                    int siz = sysIDs.length();

                                    Integer systemID = null;

                                    DocBean doc = null;

                                    ManageViewNavigation.getPrgsThread().setTextMex("Insert sync info: ");

                                    AnagBean oldHead = null;

                                    for (int i = 0; i < siz; i++) {

                                        if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100)) {
                                            break;
                                        }

                                        systemID = (Integer)sysIDs.get(i);

                                        ManageViewNavigation.getPrgsThread().setTextMex("Modific systemID info: " + i + "/" + siz);

                                        doc = (DocBean) sendObjs.elementAt(i);

                                        Session.getMngIndex().addSystemID(typeObj, doc.getNumRec(), systemID, doc.getLastMod().longValue(), false);

                                        //save system ID in obj

                                        doc.setSystemID(systemID);

                                        Session.getDocBeanDAO().commitChange(doc);

                                        //workflow
                                        flowField = ((AnagObj) Session.getListStructureObj().get(doc.getTypeHeadObj())).getFlowField();
                                        flowTargetDTField = ((AnagObj) Session.getListStructureObj().get(doc.getTypeHeadObj())).getFlowTargetDTField();

                                        //copy old head
                                        oldHead = new AnagBean(doc.getTypeHeadObj());
                                        oldHead.unserialize(doc.getHeadObj().serialize());

                                        //modific new head

                                        //targetID
                                        nextNode = Session.getMngWrkFlw().getNextHighPriorityDescNode(doc.getType(), (String) doc.getHeadObj().getFieldValueString(flowField));

                                        doc.getHeadObj().setField(flowField, nextNode);
                                        //targetDT
                                        doc.getHeadObj().setField(flowTargetDTField,Date_Util.getStamp00000FromDate(new Date()));

                                        //commit new head (adjust index info)
                                        Session.getDocBeanDAO().commitHead(doc, doc.getHeadObj(), oldHead);


                                    }

                                    if (siz > 0) {
                                        Session.getMngIndex().sortSystemIDs(typeObj);
                                    }
                                } else {

                                    ManageViewNavigation.showAlert("Server Mex", serverResponse.getString("message"), null, AlertType.INFO);

                                    setFailed(true);

                                    ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                                }




                            }





                        } catch (Exception ex) {

                            setFailed(true);

                            ManageViewNavigation.showAlert("Error", "Exception: " + (ex.getMessage() == null ? "Null" : ex.getMessage()), null, AlertType.INFO);

                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }



                        if (!this.isFailed() && !this.isCancelled()) {
                            setPercentage(100);
                        }

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }


                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {


                            if ((nextNode != null) && (!nextNode.equals(""))) {

                                Event e = new Event();

                                e.setByName("typeObj", typeObj);
                                e.setByName("searchField", flowTargetDTField);
                                e.setByName("searchFieldValue", String.valueOf(Date_Util.getStamp00000FromDate(new Date())));
                                e.setByName("descNode", nextNode);

                                Session.getMngEvent().handleEvent(ManageEvent.VIEW_SENT_OBJS_EVENT, e);


                            }


                            //alert done!
                            ManageViewNavigation.showAlert("Info", "Sent!", null, AlertType.INFO);


                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

                            }


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });
                ;


                break;

            case THREAD_SYNC_OBJS_HTTP:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("alert.infosave")) {
                    // Convert special characters

                    private String URLencode(String s) {

                        if (s != null) {

                            s = s.trim();

                            return s;

//                            StringBuffer tmp = new StringBuffer();
//                            int i = 0;
//                            try {
//                                while (true) {
//                                    int b = (int) s.charAt(i++);
//                                    if ((b >= 0x30 && b <= 0x39) || (b >= 0x41 && b <= 0x5A) || (b >= 0x61 && b <= 0x7A)) {
//                                        tmp.append((char) b);
//                                    } else {
//                                        tmp.append("%");
//                                        if (b <= 0xf) {
//                                            tmp.append("0");
//                                        }
//                                        tmp.append(Integer.toHexString(b));
//                                    }
//                                }
//                            } catch (Exception e) {
//                            }
//                            return tmp.toString();



                        }
                        return null;
                    }
                    public String typeObj = null;
                    //!!cancellare TEST
                    public AnagBean currentAnagBean;

                    public void run() {


//                        HttpConnection connection = null;
//                        InputStream inputstream = null;
//                        OutputStream outputStream = null;
                        String response = null;
                        // int totalBytes = 0;


                        //try {

                        typeObj = (String) this.getParams().get("typeObj");

                        String avaibleMemory = "";

                        ResponseService resp = Session.getMngService().doService(ManageService.GET_FREE_MEMORY_SERVICE, null);

                        if (resp.getStatus()) {

                            avaibleMemory = (String) resp.getResponseObj();
                        }

                        try {

                            ManageViewNavigation.getPrgsThread().setTextMex("Connecting to server..");

                            RequestParameters req = new RequestParameters(Session.getActiveUser().getLicenseNumber(), Session.getActiveUser().getPassword(),typeObj,"sync", Session.getActiveUser().getUsername());
//                            RequestParameters req = new RequestParameters();
//                            req.setUser(Session.getActiveUser().getUsername());
//                            req.setPassword(Session.getActiveUser().getPassword());
//                            req.setLicenseNumber(Session.getActiveUser().getLicenseNumber());
//                            req.setTypeObj(typeObj);
//                            req.setTypeReq("sync");

                             //#debug info
                                System.out.println("Send to server: " + Session.getMngIndex().getSerializedJSONSystemIDs(typeObj));

                            response = Session.getWebService().syncTypeObj(req, Session.getMngIndex().getSerializedJSONSystemIDs(typeObj));

                            //#debug info
                            System.out.println("RESPONSE SERVER: " + response);


//                                String url = "http://localhost:8084/ServerMobile/LoginServlet";
//
//                                resp = Session.getMngService().doService(ManageService.GET_URL_CONNECTION_SERVICE, null);
//
//                                if (resp.getStatus()) {
//
//                                    url = (String) resp.getResponseObj();
//                                }

                            //!! da togliere solo TEST
                            //url = "http://localhost:8084/ServerMobile/LoginServlet";
//                                url=  "http://62.149.172.12:9080/ServerMobile/LoginServlet";
//
//                                String postParameter = "ut=" + Session.getActiveUser().getUsername() + "&pw=" + Session.getActiveUser().getPassword() + "&licNum=" + Session.getActiveUser().getLicenseNumber();
//
//                                postParameter = postParameter + "&typeRequest=sync&typeObj=" + typeObj;
//
//                                postParameter = postParameter + "&avaibleMemory=" + avaibleMemory;
//
//                                postParameter = postParameter + "&sysIDs=" + Session.getMngIndex().getSerializedJSONSystemIDs(typeObj);
//
//                                postParameter = URLencode(postParameter);
//
//                                String typeConnection = url.substring(0, url.indexOf(":"));
//
//                                url=url+"?"+postParameter;

                            //  url= "http://62.149.172.12:9080/ServerMobile/LoginServlet";

//                                if (typeConnection.equals("http")) {
//                                    //connection = (HttpConnection) Connector.open(url);
//                                    connection = (HttpConnection) Connector.open(url, Connector.READ_WRITE,true);
//
//                                } else if (typeConnection.equals("https")) {
//
//                                    //connection = (HttpsConnection) Connector.open(url);
//                                    connection = (HttpsConnection) Connector.open(url, Connector.READ_WRITE,true);
//
//                                }



                            //HTTP Request
                            //connection.setRequestMethod(HttpConnection.POST);
//                                connection.setRequestMethod(HttpConnection.GET);
//
//                                connection.setRequestProperty("Accept-Encoding", "gzip");
//                                connection.setRequestProperty("Content-Length", "" + postParameter.getBytes().length);
//                                connection.setRequestProperty("User-Agent", System.getProperty("microedition.platform"));
//                                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                                connection.setRequestProperty("Connection", "close");
//
//                                outputStream = connection.openOutputStream();

                            //outputStream.write(postParameter.getBytes());


                            // HTTP Response
//                                //#debug info
//                                System.out.println("Status Line Code: " + connection.getResponseCode());
//                                //#debug info
//                                System.out.println("Status Line Message: " + connection.getResponseMessage());
//
//                                if (connection.getResponseCode() == HttpConnection.HTTP_OK) {
//
//                                    ManageViewNavigation.getPrgsThread().setTextMex("Connection ready!");
//
//                                    //#debug info
//                                    System.out.println(connection.getHeaderField(0) + " " + connection.getHeaderFieldKey(0));
//                                    //#debug info
//                                    System.out.println("Header Field Date: " + connection.getHeaderField("date"));
//
//
//                                    inputstream = connection.openInputStream();
//
//                                    String Header = connection.getHeaderField("Content-Encoding");
//
//                                    if (Header != null && Header.trim().equals("gzip")) {
//                                        inputstream = new GZipInputStream(inputstream, GZipInputStream.TYPE_GZIP, false);
//                                        //#debug info
//                                        System.out.println("httpClient: Compressed HttpConnection");
//                                    }
//
//                                    //int length = (int) connection.getLength();
//
////                                    if (length != -1) {
////
////                                        byte incomingData[] = new byte[length];
////                                        inputstream.read(incomingData);
////                                        str = new String(incomingData);
////                                        totalBytes=length;
////
////                                        ManageViewNavigation.getPrgsThread().setTextMex("Read " + totalBytes + "Bytes");
////
////
////                                    } else  {
//
//                                    ByteArrayOutputStream bytestream =
//                                            new ByteArrayOutputStream();
//                                    int ch;
//
//                                    while ((ch = inputstream.read()) != -1) {
//
//                                        if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100)) {
//                                            break;
//                                        }
//
//                                        bytestream.write(ch);
//
//                                        totalBytes = bytestream.size();
//
//                                        ManageViewNavigation.getPrgsThread().setTextMex("Read " + totalBytes + "Bytes");
//
//                                    }
//
//                                    str = new String(bytestream.toByteArray());
//
//                                    ManageViewNavigation.getPrgsThread().setTextMex("Read " + totalBytes + "Bytes");
//
//                                    bytestream.close();
//
//                                // }
//
//
//                                }
//
//                            } catch (Exception e) {
//
//                                setFailed(true);
//
//                                ManageViewNavigation.showAlert("Error", ("Exception: " + e.getMessage() == null ? "Null" : e.getMessage()), null, AlertType.INFO);
//
//
//                                //#debug error
//                                System.out.println("Caught Exception: " + e.toString());
//
//
//                            } finally {
//                                if (inputstream != null) {
//                                    try {
//                                        inputstream.close();
//                                    } catch (Exception error) {
//                                        //#debug error
//                                        System.out.println("Caught Exception: " + error.toString());
//                                    }
//                                }
//                                if (connection != null) {
//                                    try {
//                                        connection.close();
//                                    } catch (Exception error) {
//                                        //#debug error
//                                        System.out.println("Caught Exception: " + error.toString());
//                                    }
//                                }
//                            }


                            // ManageViewNavigation.getPrgsThread().setTextMex("Connection close! download: " + totalBytes + "Bytes /n Import data..");


                            if ((!this.isFailed()) && (!this.isCancelled())) {

                                //if status true assign systemId to obj!! ...
                                JSONObject serverResponse = new JSONObject(response);

                                //response = null;

                                if (serverResponse.getBoolean("status")) {

                                    if (serverResponse.has("add")) {


                                        //addobjs
                                        JSONArray addobjs = serverResponse.getJSONArray("add");

                                        int siz = addobjs.length();

                                        ManageViewNavigation.getPrgsThread().setTextMex("Importing data.. - Add new " + siz);

                                        AnagBean anagBean = null;
                                        JSONObject jsonAnagBean = null;

                                        Hashtable updatedTypeObjs = new Hashtable();

                                        for (int i = 0; i < siz; i++) {

                                            if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100)) {
                                                break;
                                            }

                                            if (addobjs.get(i) == null) {
                                                continue;
                                            }

                                            jsonAnagBean = addobjs.getJSONObject(i);

                                            ManageViewNavigation.getPrgsThread().setTextMex("Insert " + (String) jsonAnagBean.get("type") + " : " + i + "/" + String.valueOf(siz + 1));

                                            updatedTypeObjs.put((String) jsonAnagBean.get("type"), "");

                                            //if type AnagBean...

                                            anagBean = new AnagBean();

                                            Session.getAnagBeanDAO().persist(anagBean, jsonAnagBean, false);

                                        }

                                        //riordino i i systemID
                                        if (siz > 0) {

                                            ManageViewNavigation.getPrgsThread().setTextMex("Importing data.. - Sorting new objs");

                                            Enumeration en = updatedTypeObjs.keys();

                                            while (en.hasMoreElements()) {

                                                Session.getMngIndex().sortSystemIDs((String) en.nextElement());
                                            }

                                        }


                                        addobjs = null;
                                        anagBean = null;

                                        System.gc();


                                    }


                                    if (serverResponse.has("update")) {

                                        //updobjs
                                        JSONArray updobjs = serverResponse.getJSONArray("update");

                                        int siz = updobjs.length();

                                        //ManageViewNavigation.getPrgsThread().setTextMex("Importing data..  Update " + siz + " " + typeObj);

                                        AnagBean anagBean = null;
                                        JSONObject jsonAnagBean = null;

                                        Integer systemID = null;
                                        Integer objNumRec = null;

                                        for (int i = 0; i < siz; i++) {

                                            if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100)) {
                                                break;
                                            }

                                            jsonAnagBean = updobjs.getJSONObject(i);

                                            ManageViewNavigation.getPrgsThread().setTextMex("Update " + (String) jsonAnagBean.get("type") + " : " + i + "/" + String.valueOf(siz + 1));

                                            systemID = new Integer(jsonAnagBean.getInt("sysID"));

                                            //find obj numRec
                                            objNumRec = Session.getMngIndex().getNumRecFromSystemID((String) jsonAnagBean.get("type"), systemID);

                                            anagBean = Session.getAnagBeanDAO().getAnagBeanFromNumRec((String) jsonAnagBean.get("type"), objNumRec.intValue());

                                            Session.getAnagBeanDAO().commitChange(anagBean, jsonAnagBean);

                                            //Cancellare TEST
                                            currentAnagBean = anagBean;

                                        }

                                        updobjs = null;
                                        anagBean = null;
                                        jsonAnagBean = null;
                                        systemID = null;
                                        objNumRec = null;

                                        System.gc();



                                    }

                                    if (serverResponse.has("delete")) {

                                        //delobjs
                                        JSONArray delobjs = serverResponse.getJSONArray("delete");

                                        int siz = delobjs.length();

                                        ManageViewNavigation.getPrgsThread().setTextMex("Importing data..  Delete " + siz + " " + typeObj);

                                        AnagBean anagBean = null;
                                        JSONObject jsonAnagBean = null;

                                        Integer systemID = null;
                                        Integer objNumRec = null;

                                        for (int i = 0; i < siz; i++) {

                                            if (this.isFailed() || (this.isCancelled()) || (this.getPercentage() >= 100)) {
                                                break;
                                            }

                                            systemID = new Integer(delobjs.getInt(i));

                                            ManageViewNavigation.getPrgsThread().setTextMex("Delete " + typeObj + " : " + i + "/" + siz);

                                            //find obj numRec
                                            objNumRec = Session.getMngIndex().getNumRecFromSystemID(typeObj, systemID);

                                            anagBean = Session.getAnagBeanDAO().getAnagBeanFromNumRec(typeObj, objNumRec.intValue());

                                            Session.getAnagBeanDAO().delete(anagBean);

                                        }

                                        delobjs = null;
                                        anagBean = null;
                                        jsonAnagBean = null;
                                        systemID = null;
                                        objNumRec = null;

                                        System.gc();

                                    }




                                } else {

                                    ManageViewNavigation.showAlert("Server Mex", serverResponse.getString("message"), null, AlertType.INFO);

                                    setFailed(true);

                                    ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                                }


                              

                              


                            }





                        } catch (Exception ex) {

                            setFailed(true);

                            ManageViewNavigation.showAlert("Error", ("Exception: " + ex.getMessage() == null ? "Null" : ex.getMessage()), null, AlertType.INFO);


                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);


                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }
//                            finally {
//                            if (inputstream != null) {
//                                try {
//                                    inputstream.close();
//                                } catch (Exception error) {
//                                    //#debug error
//                                    System.out.println("Caught Exception: " + error.toString());
//                                }
//                            }
//                            if (connection != null) {
//                                try {
//                                    connection.close();
//                                } catch (Exception error) {
//                                    //#debug error
//
//                                    System.out.println("Caught Exception: " + error.toString());
//                                }
//                            }
//                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            setPercentage(100);
                        }

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }


                    }

                    public void onComplete() {


                        if (this.getStatus() == 5) {


                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_THREAD_EVENT, e);

                            }


                            //ricarica la lista
                            Hashtable params = new Hashtable(3);

                            params.put("typeObj", typeObj);
                            params.put("searchField", (String) ((AnagObj) Session.getListStructureObj().get(typeObj)).getDefaultGroupIndex());
                            params.put("structureObjClass", (AnagObj) Session.getListStructureObj().get(typeObj));
                            params.put("typeSourceEvent", "Rebuild_groupValueObj");

                            Session.getMngService().doService(ManageService.SEARCH_GROUPVALUE_SERVICE, params);



                            //alert done!
                            ManageViewNavigation.showAlert("Info", "Done!", null, AlertType.INFO);


                            //!! TEST ricarico oggetto visualizzato
//                            ((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed()).setCurrentAnagBean(currentAnagBean);
//                            ((AdmAnagBeanObjForm) ManageViewNavigation.getCurrentDisplayed()).prepareView(MainForm.TYPEFORM_VIEW, currentAnagBean, null, null);




                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });
                ;


                break;


            case THREAD_DELETE_ALL_OBJS:

                this.getListThread().put(new Integer(threadID), new MonitorableThread(new Integer(threadID), Locale.get("message.deleteallobjs")) {

                    private DocBean newdoc = null;

                    public void run() {


                        try {


                            //delete all record store
                            String[] listRecordStore = RecordStore.listRecordStores();

                            String rmsName = null;

                            int size = listRecordStore.length;

                            double percentageDouble = 0;


                            double coefficienteIncremento = (double) 100 / (double) size;


                            for (int i = 0; i < size; i++) {

                                percentageDouble += coefficienteIncremento;
                                setPercentage((int) percentageDouble);
                                ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                                ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());


                                rmsName = listRecordStore[i];
                                if (!rmsName.equals("login")) {
                                    RecordStore.deleteRecordStore(rmsName);
                                }
                            }

                            //delete hash mem
                            Session.getMngIndex().setHashCountRMSObjs(null);
                            Session.getMngIndex().setHashIndexCountObjRMS(null);
                            Session.getMngIndex().setHashCountRealNumObjs(null);
                            Session.getMngIndex().setHashIndexCountRealNumObjs(null);
                            Session.getMngIndex().setHashSyncIds(null);
                            Session.getMngIndex().setHashIndex(null);


                        } catch (Exception ex) {

                            setFailed(true);
                            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(), null);

                            //#debug error
                            System.out.println(ex.getMessage() == null ? "Null" : ex.getMessage());


                        }

                        if (!this.isFailed() && !this.isCancelled()) {

                            ManageViewNavigation.getPrgsThread().getGauge().setValue(getPercentage());
                            ManageViewNavigation.getPrgsThread().getGauge().setLabel(getPercentageText());

                        }

                        setPercentage(100);

                        setTerminate(true);

                        this.onComplete();

                        if (!((this.isFailed() || this.isCancelled()) && (((ManageViewNavigation.getPrgsThread().isViewed()) && (ManageViewNavigation.getPrgsThread().isShown()))))) {

                            Session.getMngThread().destroyThread(getThreadID().intValue());

                            if ((!ManageViewNavigation.getPrgsThread().isViewed()) && (!ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("NULL PROGRESS!!");
                                ManageViewNavigation.setPrgsThread(null);

                            }
                        }



                    }

                    public void onComplete() {


                        //#debug info
                        System.out.println("STATUS!!:" + this.getStatus());

                        if (this.getStatus() == 5) {

                            //if show progress back to page
                            if ((ManageViewNavigation.getPrgsThread().isViewed()) || (ManageViewNavigation.getPrgsThread().isShown())) {

                                //#debug info
                                System.out.println("EVENT BACK!!");

                                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                            }


                            Session.getMngEvent().handleEvent(ManageEvent.REBUILD_ADMDATA_EVENT, e);

                            //oggi ManageViewNavigation.getAdmDataForm().prepareView();


                            //alert duplicate!
                            ManageViewNavigation.showAlert(Locale.get("message.deleteallobjs"), Locale.get("message.completed"), null, AlertType.CONFIRMATION);


                        } else if (this.getStatus() == -1) {

                            if (!ManageViewNavigation.getPrgsThread().isViewed()) {
                                ManageViewNavigation.showAlert(this.getTitle(), Locale.get("message.failed"), null, AlertType.INFO);
                            }
                        }

                    }
                });


                break;


        }

        return response;
    }

    ;

    public MonitorableThread getMonitorableThread(int threadID) {

        return (MonitorableThread) getListThread().get(new Integer(threadID));

    }

    public boolean startThread(int threadID, Hashtable params, String gaugeStyle) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            //#debug info
            System.out.println("NULL PROGRESS!!");
            //null progress
            ManageViewNavigation.setPrgsThread(null);

            ((MonitorableThread) getListThread().get(new Integer(threadID))).start(params, gaugeStyle);


            if (gaugeStyle != null) {

                boolean caricaProgress = true;

                for (int i = 0; i < 3; i++) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //se ci mette meno di 2 secondi visualizzo direttamente completed
                    if ((!getListThread().containsKey(new Integer(threadID))) || (((MonitorableThread) getListThread().get(new Integer(threadID))).isTerminate())) {

                        //#debug info
                        System.out.println("Progress NON visualizzata!");

                        caricaProgress = false;

                        break;

                    }
                }

                if (caricaProgress) {

                    //if thread already alive                    
                    if ((getListThread().containsKey(new Integer(threadID))) && (!((MonitorableThread) getListThread().get(new Integer(threadID))).isTerminate())) {

                        ManageViewNavigation.getPrgsThread().setViewed(true);

                        //#debug info
                        System.out.println("EVENTO VIEW PROGRESS!!!");

                        e = new Event();
                        Session.getMngEvent().handleEvent(ManageEvent.VIEW_PROGRESS_EVENT, e);

                    } else {

                        //#debug info
                        System.out.println("NULL PROGRESS!!");
                        //null progress
                        ManageViewNavigation.setPrgsThread(null);


                    }

                }
            }

            return true;

        } else {
            return false;
        }
    }

    public boolean startAgainThread(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            ((MonitorableThread) getListThread().get(new Integer(threadID))).startAgain();

            return true;

        } else {
            return false;
        }
    }

    public boolean cancelThread(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            ((MonitorableThread) getListThread().get(new Integer(threadID))).cancel();

            return true;

        } else {

            return false;
        }
    }

    public boolean destroyThread(int threadID) {

        try {

            if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

                ((MonitorableThread) getListThread().get(new Integer(threadID))).destroyThread();

                while ((getListThread().get(new Integer(threadID)) != null) && (!((MonitorableThread) getListThread().get(new Integer(threadID))).isTerminate())) {

                    try {
                        wait(200);
                    } catch (Exception e) {
                    }

                }

                try {
                    getListThread().remove(new Integer(threadID));
                } catch (Exception e) {
                }

                return true;

            } else {

                return false;
            }

        } catch (Exception e) {

            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());

            return false;
        }
    }

    public boolean destroyAllThread() {

        for (Enumeration e = getListThread().keys(); e.hasMoreElements();) {
            Integer id = (Integer) e.nextElement();
            ((MonitorableThread) getListThread().get(id)).destroyThread();
            getListThread().remove(id);

        }

        return true;

    }

    public boolean pauseThread(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            ((MonitorableThread) getListThread().get(new Integer(threadID))).pause();

            return true;

        } else {
            return false;
        }
    }

    public boolean restartThread(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            ((MonitorableThread) getListThread().get(new Integer(threadID))).reStart();

            return true;

        } else {
            return false;
        }
    }

    public synchronized String getPercentageText(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            return ((MonitorableThread) getListThread().get(new Integer(threadID))).getPercentageText();


        } else {
            return null;
        }
    }
    //if (failed) -1; if (cancelled) 4; if (paused) return 2; 
    //if (percentageSnapshot >= 100) return 5; if (thread == null) return 0; 
    //else  return 1;        

    public int getStatus(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            return ((MonitorableThread) getListThread().get(new Integer(threadID))).getStatus();


        } else {
            return 0;
        }
    }

    public boolean isFailed(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            return ((MonitorableThread) getListThread().get(new Integer(threadID))).isFailed();


        } else {
            return false;
        }
    }

    ;

    public int getPercentadeThread(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            return ((MonitorableThread) getListThread().get(new Integer(threadID))).getPercentage();


        } else {
            return 0;
        }
    }

    public Hashtable getParameterThread(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            return ((MonitorableThread) getListThread().get(new Integer(threadID))).getParams();


        } else {
            return null;
        }
    }

    public String getTitle(int threadID) {

        if (((MonitorableThread) getListThread().get(new Integer(threadID))) != null) {

            return ((MonitorableThread) getListThread().get(new Integer(threadID))).getTitle();


        } else {
            return null;
        }
    }

    public Hashtable getListThread() {
        if (listThread == null) {
            listThread = new Hashtable();
        }
        return listThread;
    }

    public void setListThread(Hashtable listThread) {
        this.listThread = listThread;
    }
}
