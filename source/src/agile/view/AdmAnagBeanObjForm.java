package agile.view;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.DateField;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ListItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TableItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.json.me.JSONArray;
import agile.model.service.ManageService;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.DocBean;
import agile.model.structure.DocObj;
import agile.model.structure.Field;
import agile.model.structure.Property;
import agile.model.structure.ResponseService;
import agile.model.thread.ManageThread;
import agile.session.Session;
import agile.util.Date_Util;
import de.enough.polish.util.TextUtil;
import javax.microedition.lcdui.Ticker;

/**
 * <p>Form for manage read-write generic item detail</p>
 *
 * <p>Copyright agile</p>
 * @author ruego
 */
public class AdmAnagBeanObjForm   extends MainForm implements Viewable {

    //#if subMenuActive
    //# private Command saveAndCreateDocCommand = new Command(Locale.get("command.saveandcreatedoc"), Command.SCREEN, 3);
    //# private Command createNewDocCommand = new Command(Locale.get("command.createNewDocCommand"), Command.SCREEN, 3);
    //# private Command finddocument = new Command(Locale.get("command.finddocument"), Command.SCREEN, 4);
    //# private Command findlink = new Command(Locale.get("command.findlink"), Command.SCREEN, 2);
    //#endif
    private Command createDocCommand = null;
    private Command add = new Command(Locale.get("command.add"), Command.BACK, 0);
    private Command cancelCommand = new Command(Locale.get("command.cancel"), Command.SCREEN, 0);
    private Command refreshdata = new Command(Locale.get("command.refreshdata"), Command.SCREEN, 4);
    private Hashtable onChangeMetodFields;
    private Hashtable hashFields;
    private String currentTypeDoc = null;
    private DocBean currentDocBean = null;
    private String createDoc = null;
    private AnagBean currentAnagBean = null;
    private TableItem table = null;
    private int selectedIndex = 0;
    //#style mailForm
    private ListItem listITMS = new ListItem(null);
    private Field primaryKey = null;
    private AnagBean sourceObj = null;
    private Hashtable hashCommand = null;

    public AdmAnagBeanObjForm() {

        super(Locale.get("AdmAnagBeanObjForm.title"), true, true, true, null);


    }

    public void showView() {

        ManageViewNavigation.getDisplay().setCurrent(this);

    }


    public void setTicker(String mex) {


        if (mex != null) {

            //#style mailTicker
            Ticker tik = new Ticker(mex);

            this.setTicker(tik);

        }


    }


      public String getTickerMex(){

         Ticker tik = this.getTicker();

         return  tik!=null?tik.getString():null;

     }

    public void makeListField(String typeObj, AnagBean compileAnagBean) throws Exception {

        try {


            //se è presente il compileAnagBean la procedura copia i valori nei campi del nuovo oggetto
            // con property "linkedTOField" "Cliente.Codice" corrispondente all'oggetto e al campo compileAnagBean


            //            DATE		      DateField
            //            NUMERIC             TextField	NUMERIC
            //            ANY                 TextField     ANY
            //            PASSWORD            TextField	PASSWORD
            //            PHONENUMBER         TextField	PHONENUMBER
            //            UNEDITABLE          TextField	UNEDITABLE
            //            EMAILADDR           TextField	EMAILADDR
            //            DECIMAL		  TextField	DECIMAL
            //            listProperties      ChoiceGroup	POPUP



            //loop on obj fields
            Vector listfields = ((AnagObj) Session.getListStructureObj().get(typeObj)).getListFields();

            Field fld = null;
            long size = listfields.size();
            Vector props = null;       

            boolean setted = false;

            String value = null;

            for (int j = 0; j < size; j++) {

                fld = (Field) listfields.elementAt(j);

                //put field for onChange metods..
                if (fld.hasProperty("onChange")) {
                    getOnChangeMetodFields().put(fld.getId(), fld.getPropertyByKey("onChange"));
                }

                //put primaryKey
                if (fld.hasProperty("primaryKey")) {
                    primaryKey = fld;

                }

                //security  control
                if (fld.getWriteSecurityLevel().intValue() > Session.getActiveUser().getSecurityLevelWrite()) {


                    StringItem str = null;

                    if ((getTypeFORM() == MainForm.TYPEFORM_NEW) || (getTypeFORM() == MainForm.TYPEFORM_CREATENEWDOC) || (getTypeFORM() == MainForm.TYPEFORM_DOC_NEW_ROW) || (getTypeFORM() == MainForm.TYPEFORM_NEW_ROW_CART)) {
                        //#style simpleinput
                        str = new StringItem(fld.getDescription(), Locale.get("message.notautorizedtowrite"));


                    } else if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW)) {


                        if (fld.getReadSecurityLevel().intValue() > Session.getActiveUser().getSecurityLevelRead()) {

                            //#style simpleinput
                            str = new StringItem(fld.getDescription(), Locale.get("message.notautorizedtowrite"));

                        } else {
                          
                            //#style simpleinput
                            str = new StringItem(fld.getDescription(), ((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())));

                        }


                    }

                    getListITMS().append(str);

                    getHashFields().put(  fld.getId() , str);


                } else {


                    //noupdate control
                    if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW)) {

                        if (fld.hasProperty("noUpdate")) {

                            value = null;

                            if (fld.getTypeUI().equals("DATE")) {

                                if (((((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()))) != null) && (!this.getCurrentAnagBean().getFieldValueString(fld.getId()).equals(""))) {

                                    value = Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()))), Date_Util.dmyHHmmss, false);

                                }

                            } else {

                                value = ((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()));

                            }


                            //#style simpleinput
                            StringItem str = new StringItem(fld.getDescription(), value);

                            getListITMS().append(str);

                            getHashFields().put(fld.getId(), str);


                            continue;
                        }

                    }

                    //systemInfo
                    if (fld.hasProperty("systemInfo")) {

                        value = null;

                        if (fld.getTypeUI().equals("DATE")) {

                            if (((((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()))) != null) && (!this.getCurrentAnagBean().getFieldValueString(fld.getId()).equals(""))) {

                                value = Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()))), Date_Util.dmyHHmmss, false);

                            }

                        } else {

                            value = ((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()));

                        }


                        //#style simpleinput
                        StringItem str = new StringItem(fld.getDescription(), value==null?"":value);

                        getListITMS().append(str);

                        getHashFields().put(fld.getId(), str);


                        continue;
                    }



                    if (fld.getTypeUI().equals("DATE")) {

                        DateField date = null;

                        if ((getTypeFORM() == MainForm.TYPEFORM_NEW) || (getTypeFORM() == MainForm.TYPEFORM_CREATENEWDOC) || (getTypeFORM() == MainForm.TYPEFORM_DOC_NEW_ROW) || (getTypeFORM() == MainForm.TYPEFORM_NEW_ROW_CART)) {

                            //#style simpleinput
                            date = new DateField(fld.getDescription(), DateField.DATE);
                            date.setDate(new Date());

                            //controllo linkedTOField
                            value = Session.getMngService().getFieldValueFromLinkedToExternalFieldorSetDefault(fld, compileAnagBean, getCurrentDocBean());

                            if (value != null) {
                                date.setDate(Date_Util.getDatefromStamp(Long.parseLong(value)));
                            }


//                        if (compileAnagBean != null) {
//
//                            props = fld.getPropertyByKey("linkedTOField");
//
//                            sizePrps = props == null ? 0 : props.size();
//
//                            for (int p = 0; p < sizePrps; p++) {
//
//                                pr = (Property) props.elementAt(p);
//
//                                //Fornitore.Denominazione or Prodotto.Prezzo_+Val(Ordine.Listino) or Ordine.Sconto
//
//                                index = ((String) pr.getValue()).indexOf(".");
//                                firstPart = ((String) pr.getValue()).substring(0, index);
//                                secondPart = ((String) pr.getValue()).substring(index + 1);
//
//                                if (firstPart.equals(compileAnagBean.getType())) {
//
//                                    nameField =ManageService.getNameField(secondPart, compileAnagBean,getCurrentDocBean());
//
//                                    if (compileAnagBean.hasField(nameField)) {
//
//                                        date.setDate(Date_Util.getDatefromStamp(Long.parseLong((String) compileAnagBean.getFieldValue(nameField))));
//
//                                    }
//
//
//                                } else if ((getCurrentDocBean() != null) && (firstPart.equals(getCurrentDocBean().getType()))) {
//
//                                    nameField = ManageService.getNameField(secondPart, compileAnagBean,getCurrentDocBean());
//
//                                  if (getCurrentDocBean().getHeadObj().hasField(nameField)) {
//
//                                        date.setDate(Date_Util.getDatefromStamp(Long.parseLong((String) getCurrentDocBean().getHeadObj().getFieldValue(nameField))));
//
//                                    }
//
//
//                                }
//
//                            }
//
//                        }


                        }

                        if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW)) {
                            //#style simpleinput
                            date = new DateField(fld.getDescription(), DateField.DATE);

                            if (((((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()))) != null) && (!this.getCurrentAnagBean().getFieldValueString(fld.getId()).equals(""))) {
                                date.setDate(Date_Util.getDatefromStamp(Long.parseLong((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()))));
                            }

                        }

                        getListITMS().append(date);

                        getHashFields().put(fld.getId(), date);


                    }

                    if ((fld.getTypeUI().equals("ANY")) || (fld.getTypeUI().equals("NUMERIC")) || (fld.getTypeUI().equals("PASSWORD")) || (fld.getTypeUI().equals("PHONENUMBER")) || (fld.getTypeUI().equals("UNEDITABLE")) || (fld.getTypeUI().equals("EMAILADDR")) || (fld.getTypeUI().equals("DECIMAL"))) {

                        TextField text = null;

                        int typeUI = 0;

                        if (fld.getTypeUI().equals("ANY")) {
                            typeUI = TextField.ANY;
                        } else if (fld.getTypeUI().equals("NUMERIC")) {
                            typeUI = TextField.NUMERIC;
                        } else if (fld.getTypeUI().equals("PASSWORD")) {
                            typeUI = TextField.PASSWORD;
                        } else if (fld.getTypeUI().equals("PHONENUMBER")) {
                            typeUI = TextField.PHONENUMBER;
                        } else if (fld.getTypeUI().equals("UNEDITABLE")) {
                            typeUI = TextField.UNEDITABLE;
                        } else if (fld.getTypeUI().equals("EMAILADDR")) {
                            typeUI = TextField.EMAILADDR;
                        } else if (fld.getTypeUI().equals("DECIMAL")) {
                            typeUI = TextField.DECIMAL;
                        }


                        if ((getTypeFORM() == MainForm.TYPEFORM_NEW) || (getTypeFORM() == MainForm.TYPEFORM_CREATENEWDOC) || (getTypeFORM() == MainForm.TYPEFORM_DOC_NEW_ROW) || (getTypeFORM() == MainForm.TYPEFORM_NEW_ROW_CART)) {

                            //#style simpleinput
                            text = new TextField(fld.getDescription(), null, fld.getMaxLenght().intValue(), typeUI);

                            value = Session.getMngService().getFieldValueFromLinkedToExternalFieldorSetDefault(fld, compileAnagBean, getCurrentDocBean());

                            if (value != null) {
                                text.setText(value);

                            }

//                        setted=false;
//
//                        if (compileAnagBean != null) {
//
//                            props = fld.getPropertyByKey("linkedTOField");
//
//                            sizePrps = props == null ? 0 : props.size();
//
//                            for (int p = 0; p < sizePrps; p++) {
//
//                                pr = (Property) props.elementAt(p);
//
//                                //Fornitore.Denominazione
//                                index = ((String) pr.getValue()).indexOf(".");
//                                firstPart = ((String) pr.getValue()).substring(0, index);
//                                secondPart = ((String) pr.getValue()).substring(index + 1);
//
//
//                                if (firstPart.equals(compileAnagBean.getType())) {
//
//                                    nameField = ManageService.getNameField(secondPart, compileAnagBean,getCurrentDocBean());
//
//                                    if (compileAnagBean.hasField(nameField)) {
//
//                                        text.setText((String) compileAnagBean.getFieldValue(nameField));
//                                        setted=true;
//                                    }
//
//
//                                } else if ((getCurrentDocBean() != null) && (firstPart.equals(getCurrentDocBean().getType()))) {
//
//                                    nameField = ManageService.getNameField(secondPart, compileAnagBean,getCurrentDocBean());
//
//                                    if (getCurrentDocBean().getHeadObj().hasField(nameField)) {
//
//                                        text.setText((String) getCurrentDocBean().getHeadObj().getFieldValue(nameField));
//                                        setted=true;
//                                    }
//
//
//                                }
//
//
//                            }
//
//                        }
//
//                        //control setDefault
//                        if (!setted){
//
//                            props = fld.getPropertyByKey("setDefault");
//                            sizePrps = props == null ? 0 : props.size();
//
//                            if (sizePrps>0){
//
//                                  text.setText((String)((Property) props.elementAt(0)).getValue());
//                            }
//
//                        }



                        }

                        if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW)) {
                            //#style simpleinput
                            text = new TextField(fld.getDescription(), ((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())), fld.getMaxLenght().intValue(), typeUI);

                        }

                        UiAccess.setInputMode(text, UiAccess.MODE_NATIVE);

                        getListITMS().append(text);

                        getHashFields().put(fld.getId(), text);


                    }

                    if (fld.getTypeUI().equals("listProperties")) {


                        //find value or  (key: values_Listino value: list og values) or (key: values_search value: query  )

                        int selectedIdx = 0;
                        Vector propValues = fld.getPropertyByKey("values_" + fld.getDescription());
                        String[] values = null;

                        if (propValues != null) {

                            int sizeV = propValues.size();

                            //values_Listino
                            if (sizeV > 0) {

                                for (int v = 0; v < sizeV; v++) {

                                    Property prop = (Property) propValues.elementAt(v);
                                    JSONArray listValue = (JSONArray) prop.getValue();
                                    int sizeVls = listValue.length();
                                    values = new String[sizeVls];

                                    for (int vls = 0; vls < sizeVls; vls++) {

                                        values[vls] = (String) listValue.get(vls);

                                        //selected index
                                        if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW) && (((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())) != null)) {

                                            if (((String) listValue.get(vls)).equals(((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())))) {
                                                selectedIdx = vls;
                                            }
                                        }


                                    }

                                }

                            }

                        } else {

                            propValues = fld.getPropertyByKey("values_search");
                            if (propValues != null) {

                                int sizeV = propValues.size();

                                //list:Listino.CodList
                                if (sizeV > 0) {

                                    Property prop = (Property) propValues.elementAt(0);
                                    String query = (String) prop.getValue();

                                    //list
                                    if (TextUtil.split(query, ':')[0].equals("list")) {

                                        //lancio query (Mi aspetto che il field sia indicizzato..)
                                        Vector list = Session.getMngService().getListValueOfIndexedField(TextUtil.split(TextUtil.split(query, ':')[1], '.')[0], TextUtil.split(TextUtil.split(query, ':')[1], '.')[1]);

                                        if (list != null) {

                                            int sizeValues = list.size();

                                            values = new String[sizeValues];

                                            for (int vls = 0; vls < sizeValues; vls++) {

                                                values[vls] = (String) list.elementAt(vls);

                                                //selected index
                                                if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW) && (((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())) != null)) {

                                                    if (((String) list.elementAt(vls)).equals(((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())))) {
                                                        selectedIdx = vls;
                                                    }
                                                }

                                            }

                                        }


                                    }

                                }

                            }


                        }


                        if ((values == null) || (values.length == 0)) {

                            selectedIdx = 0;
                            values = new String[1];
                            values[0] = Locale.get("AdmAnagBeanObjForm.noValue");


                        }

                        //#style simpleChoice
                        ChoiceGroup choiceGroup = new ChoiceGroup(fld.getDescription(), Choice.POPUP, values, null);

                        if ((getTypeFORM() == MainForm.TYPEFORM_UPDATE) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) || (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW)) {

                            choiceGroup.setSelectedIndex(selectedIdx, true);

                        }

                        if ((getTypeFORM() == MainForm.TYPEFORM_NEW) || (getTypeFORM() == MainForm.TYPEFORM_CREATENEWDOC) || (getTypeFORM() == MainForm.TYPEFORM_DOC_NEW_ROW) || (getTypeFORM() == MainForm.TYPEFORM_NEW_ROW_CART)) {

                            value = Session.getMngService().getFieldValueFromLinkedToExternalFieldorSetDefault(fld, compileAnagBean, getCurrentDocBean());

                            if (value != null) {

                                //find index
                                int sizeValues = values.length;

                                for (int v = 0; v < sizeValues; v++) {

                                    if (values[v].equals(value)) {
                                        choiceGroup.setSelectedIndex(v, true);
                                        setted = true;
                                    }
                                }

                            }


//                        setted=false;
//
//                        if (compileAnagBean != null) {
//
//                            props = fld.getPropertyByKey("linkedTOField");
//
//                            sizePrps = props == null ? 0 : props.size();
//
//                            for (int p = 0; p < sizePrps; p++) {
//
//                                pr = (Property) props.elementAt(p);
//
//                                //Fornitore.Denominazione
//                                index = ((String) pr.getValue()).indexOf(".");
//                                firstPart = ((String) pr.getValue()).substring(0, index);
//                                secondPart = ((String) pr.getValue()).substring(index + 1);
//
//
//                                if (firstPart.equals(compileAnagBean.getType())) {
//
//                                    nameField = ManageService.getNameField(secondPart, compileAnagBean,getCurrentDocBean());
//
//                                    if (compileAnagBean.hasField(nameField)) {
//
//                                        //find index
//                                        int sizeValues = values.length;
//
//                                        for (int v = 0; v < sizeValues; v++) {
//
//                                            if (values[v].equals((String) compileAnagBean.getFieldValue(nameField))) {
//                                                choiceGroup.setSelectedIndex(v, true);
//                                                setted=true;
//                                            }
//                                        }
//
//
//                                    }
//
//
//                                } else if ((getCurrentDocBean() != null) && (firstPart.equals(getCurrentDocBean().getType()))) {
//
//                                    nameField =ManageService.getNameField(secondPart, compileAnagBean,getCurrentDocBean());
//
//                                   if (getCurrentDocBean().getHeadObj().hasField(nameField)) {
//
//                                        //find index
//                                        int sizeValues = values.length;
//
//                                        for (int v = 0; v < sizeValues; v++) {
//
//                                            if (values[v].equals((String) getCurrentDocBean().getHeadObj().getFieldValue(nameField))) {
//                                                choiceGroup.setSelectedIndex(v, true);
//                                                setted=true;
//                                            }
//                                        }
//
//                                    }
//
//
//                                }
//
//
//
//
//                            }
//
//                        }
//
//                        //control setDefault
//                        if (!setted){
//
//                            props = fld.getPropertyByKey("setDefault");
//                            sizePrps = props == null ? 0 : props.size();
//
//                            if (sizePrps>0){
//
//                                //find index
//                                int sizeValues = values.length;
//
//                                for (int v = 0; v < sizeValues; v++) {
//
//                                    if (values[v].equals(  (String)((Property) props.elementAt(0)).getValue() ) ) {
//                                        choiceGroup.setSelectedIndex(v, true);
//                                    }
//                                }
//
//                            }
//
//                        }


                        }

                        getListITMS().append(choiceGroup);

                        getHashFields().put(fld.getId(), choiceGroup);


                    }

                }


            }

            //do onchangeMetod
            Enumeration en = getOnChangeMetodFields().keys();
            Hashtable params = null;

            while (en.hasMoreElements()) {

                String key = (String) en.nextElement();
                params = new Hashtable(2);
                props = (Vector) onChangeMetodFields.get(key);
                params.put("command", ((Property) props.elementAt(0)).getValue()  );
                params.put("fields", getHashFields());
                params.put("typeObj", getCurrentAnagBean().getType());

                Session.getMngService().doService(ManageService.DO_ONCHANGEITEM_METOD_SERVICE, params);

            }



        } catch (Exception e) {
            //#debug error
            System.out.println("Exception: " + e.getMessage());
        }

    }

    public void makeTableView(String typeObj) throws Exception {

        //loop on obj fields
        Vector listfields = ((AnagObj) Session.getListStructureObj().get(typeObj)).getListFields();

        Field fld = null;
        int size = listfields.size();

        //#style defaultTable
        TableItem tableNew = new TableItem(2, size);

        setTable(tableNew);

        getTable().setItemStateListener(null);
        getTable().setItemCommandListener(this);

        for (int j = 0; j < size; j++) {

            fld = (Field) listfields.elementAt(j);

            //put primaryKey
            if (fld.hasProperty("primaryKey")) {
                primaryKey = fld;
            }

            //security  control
            if (fld.getReadSecurityLevel().intValue() > Session.getActiveUser().getSecurityLevelRead()) {

                //#style heading
                getTable().set(0, j, fld.getDescription());
                //#style leftedCell		
                getTable().set(1, j, Locale.get("message.notautorizedtoread"));

            } else {

                //#style heading
                getTable().set(0, j, fld.getDescription());
                String value = ((String) this.getCurrentAnagBean().getFieldValueString(fld.getId())) == null ? "" : ((String) this.getCurrentAnagBean().getFieldValueString(fld.getId()));

                if ((fld.getTypeUI().equals("DATE")) && (!value.equals(""))) {

                    value = Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(value)), Date_Util.dmyHHmmss, false);
                }

                //#style leftedCell
                getTable().set(1, j, value);


            }


        }

        if (size > 0) {
            getTable().setSelectedCell(0, selectedIndex);
        }

        getTable().setSelectionMode(TableItem.SELECTION_MODE_ROW | TableItem.SELECTION_MODE_COLUMN);

        this.append(getTable());

    }

    public void loadFindDocCommand(String typeObj) throws Exception {

        if (((AnagObj) Session.getListStructureObj().get(typeObj)).hasProperty("linkDocObj")) {

            Vector props = ((AnagObj) Session.getListStructureObj().get(typeObj)).getPropertyByKey("linkDocObj");

            Property prop = (Property) props.elementAt(0);
            JSONArray listValue = (JSONArray) prop.getValue();

            //#if subMenuActive
            //#  boolean findone = false;
            //#endif

            Command cmd = null;

            int numSubCommandForm = getHashCommand().size();

            int sizeVls = listValue == null ? 0 : listValue.length();

            String value = null;

            for (int p = 0 + numSubCommandForm; p < sizeVls + numSubCommandForm; p++) {

                value = listValue.getString(p - numSubCommandForm);

                //#if subMenuActive
//#                if (!findone) {
//#
//#                   this.addCommand(finddocument);
//#
//#                   findone = true;
//#                }
                //#endif


                //#if subMenuActive
                //#  cmd = new Command( value, Command.SCREEN, p);
                //#  UiAccess.addSubCommand(cmd, finddocument, this);
                //#else
                cmd = new Command(Locale.get("command.finddocument") + " " + value, Command.SCREEN, p);
                this.addCommand(cmd);
                //#endif

                getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.finddoc) + "@" + value);


            }

        }

    }

    public void loadLinkCommand(String typeObj) throws Exception {

        if (((AnagObj) Session.getListStructureObj().get(typeObj)).hasProperty("hyperlink")) {

            Vector props = ((AnagObj) Session.getListStructureObj().get(typeObj)).getPropertyByKey("hyperlink");

            Property prop = (Property) props.elementAt(0);
            JSONArray listValue = (JSONArray) prop.getValue();

            //#if subMenuActive
            //#  boolean findone = false;
            //#endif

            Command cmd = null;

            int numSubCommandForm = getHashCommand().size();

            int sizeVls = listValue == null ? 0 : listValue.length();

            String value = null;


            String[] campi;

            String objlink;

            for (int p = 0 + numSubCommandForm; p < sizeVls + numSubCommandForm; p++) {

                //objlink:Listino;queryLink:Listino.CodProd=Prodotto.Codice
                value = listValue.getString(p - numSubCommandForm);

                campi = TextUtil.split(value, ';');

                objlink = TextUtil.split(campi[0], ':')[1];

                //#if subMenuActive
//#                if (!findone) {
//#
//#                   this.addCommand(findlink);
//#
//#                   findone = true;
//#                }
                //#endif


                //#if subMenuActive
                //#  cmd = new Command( objlink, Command.SCREEN, p);
                //#  UiAccess.addSubCommand(cmd, findlink, this);
                //#else
                cmd = new Command(Locale.get("command.findlink") + " " + objlink, Command.SCREEN, p);
                this.addCommand(cmd);
                //#endif

                getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.findlink) + "@" + value);


            }

        }

    }

    public void loadCreateDocCommand(String typeObj) throws Exception {

        if (((AnagObj) Session.getListStructureObj().get(typeObj)).hasProperty("linkDocObj")) {

            Vector props = ((AnagObj) Session.getListStructureObj().get(typeObj)).getPropertyByKey("linkDocObj");

            Property prop = (Property) props.elementAt(0);
            JSONArray listValue = (JSONArray) prop.getValue();

            //#if subMenuActive
            //#  boolean findone = false;
            //#endif


            Command cmd = null;

            int sizeVls = listValue == null ? 0 : listValue.length();

            String value = null;

            for (int p = 0; p < sizeVls; p++) {

                value = listValue.getString(p);

                //#if subMenuActive
                //#                if (!findone) {
                //#
                //#                    if (getTypeFORM() == MainForm.TYPEFORM_VIEW) {
                //#                        this.addCommand(createNewDocCommand);
                //#                   } else {
                //#                       this.addCommand(saveAndCreateDocCommand);}
                //#
                //#                    findone = true;
                //#                }
                //#endif




                if (getTypeFORM() == MainForm.TYPEFORM_VIEW) {


                    //#if subMenuActive
                    //# cmd = new Command(  value, Command.SCREEN, p);
                    //# UiAccess.addSubCommand(cmd, createNewDocCommand, this);
                    //#else
                    cmd = new Command(Locale.get("command.createNewDocCommand") + " " + value, Command.SCREEN, p);
                    this.addCommand(cmd);
                    //#endif

                    getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.createdoc) + "@" + value);


                } else {


                    //#if subMenuActive
                    //# cmd = new Command( value, Command.SCREEN, p);
                    //# UiAccess.addSubCommand(cmd, saveAndCreateDocCommand, this);
                    //#else
                    cmd = new Command(Locale.get("command.saveandcreatedoc") + " " + value, Command.SCREEN, p);
                    this.addCommand(cmd);
                    //#endif

                    getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.saveandcreatedoc) + "@" + value);


                }




            }

        }

    }

    public AdmAnagBeanObjForm prepareView(int typeFORM, AnagBean obj, String typeDoc, DocBean docBean) {

        try {

            //#if subMenuActive
            //# this.removeCommand(finddocument);
            //# this.removeCommand(saveAndCreateDocCommand);
            //# this.removeCommand(createNewDocCommand);
            //# this.removeCommand(findlink);
            //#endif

            //remove command in hash
            Enumeration en = getHashCommand().keys();
            Command cmd = null;
            while (en.hasMoreElements()) {

                cmd = (Command) en.nextElement();
                this.removeCommand(cmd);
                getHashCommand().remove(cmd);

            }


            if (createDocCommand != null) {
                this.removeCommand(createDocCommand);
            }

            this.removeCommand(cancelCommand);
            this.removeCommand(ManageViewNavigation.saveCommand);
            this.removeCommand(ManageViewNavigation.updateCommand);
            this.removeCommand(refreshdata);

            this.deleteAll();

            getListITMS().clear();

            this.setTypeFORM(typeFORM);

            this.setSourceObj(obj);

            this.setTitle(Locale.get("AdmAnagBeanObjForm.title") + " " + obj.getType());


            switch (typeFORM) {

                case MainForm.TYPEFORM_NEW:

                    setHelpFile("adm_anagbean_new");

                    this.setCurrentAnagBean(obj);

                    this.makeListField(obj.getType(), null);

                    this.loadCreateDocCommand(obj.getType());

                    this.addCommand(ManageViewNavigation.saveCommand);

                    this.append(getListITMS());


                    break;

                case MainForm.TYPEFORM_UPDATE:

                    setHelpFile("adm_anagbean_new");

                    this.setCurrentAnagBean(obj);

                    this.makeListField(obj.getType(), null);

                    this.loadCreateDocCommand(obj.getType());

                    this.addCommand(ManageViewNavigation.saveCommand);

                    this.append(getListITMS());

                    getListITMS().focusChild(selectedIndex);

                    break;

                case MainForm.TYPEFORM_VIEW:

                    setHelpFile("adm_anagbean_view");

                    this.setCurrentAnagBean(obj);

                    this.makeTableView(obj.getType());

                    this.loadCreateDocCommand(obj.getType());

                    this.loadFindDocCommand(obj.getType());

                    this.loadLinkCommand(obj.getType());

                    this.addCommand(ManageViewNavigation.updateCommand);

                    this.addCommand(refreshdata);

//                    getTable().setSelectedCell(0, selectedIndex);  
//                    getTable().focus(selectedIndex);


                    break;

                case MainForm.TYPEFORM_DOC_NEW_ROW:

                    setHelpFile("adm_anagbean_new");

                    this.setCurrentAnagBean(obj);
                    this.makeListField(obj.getType(), null);
                    this.addCommand(ManageViewNavigation.confirmCommand);
                    this.addCommand(ManageViewNavigation.cancelCommand);
                    this.removeCommand(ManageViewNavigation.backCommand);
                    this.removeCommand(ManageViewNavigation.home);
                    this.append(getListITMS());

                    break;

                case MainForm.TYPEFORM_NEW_ROW_CART:

                    setHelpFile("adm_anagbean_new");

                    setCurrentDocBean(docBean);

                    //search type row obj
                    String typeRow = ((DocObj) Session.getListStructureObj().get(getCurrentDocBean().getType())).getTypeRowObj();

                    this.setTitle(Locale.get("AdmAnagBeanObjForm.title") + " " + typeRow);

                    //create row obj
                    AnagBean rowObj = new AnagBean(typeRow);
                    this.setCurrentAnagBean(rowObj);

                    //compile rowObj from source obj
                    this.makeListField(typeRow, obj);

                    this.addCommand(add);
                    this.addCommand(cancelCommand);

                    this.removeCommand(ManageViewNavigation.backCommand);
                    this.removeCommand(ManageViewNavigation.home);
                    this.append(getListITMS());

                    break;

                case MainForm.TYPEFORM_DOC_VIEW_HEAD:

                    setHelpFile("adm_anagbean_view");

                    this.setCurrentAnagBean(obj);
                    this.makeTableView(obj.getType());
                    this.removeCommand(ManageViewNavigation.home);
                    break;

                case MainForm.TYPEFORM_DOC_UPDATE_HEAD:

                    setHelpFile("adm_anagbean_new");

                    this.setCurrentAnagBean(obj);
                    this.makeListField(obj.getType(), null);
                    this.addCommand(ManageViewNavigation.confirmCommand);
                    this.addCommand(ManageViewNavigation.cancelCommand);
                    this.removeCommand(ManageViewNavigation.backCommand);
                    this.removeCommand(ManageViewNavigation.home);
                    this.append(getListITMS());

                    break;

                case MainForm.TYPEFORM_DOC_UPDATE_ROW:

                    setHelpFile("adm_anagbean_new");

                    this.setCurrentAnagBean(obj);
                    this.makeListField(obj.getType(), null);
                    this.addCommand(ManageViewNavigation.confirmCommand);
                    this.addCommand(ManageViewNavigation.cancelCommand);
                    this.removeCommand(ManageViewNavigation.backCommand);
                    this.removeCommand(ManageViewNavigation.home);
                    this.append(getListITMS());

                    break;

                case MainForm.TYPEFORM_CREATENEWDOC:

                    

                    setHelpFile("adm_anagbean_newDoc");

                    setCurrentTypeDoc(typeDoc);

                    //search type head obj
                    String typeHead = ((DocObj) Session.getListStructureObj().get(typeDoc)).getTypeHeadObj();

                    this.setTitle(Locale.get("AdmAnagBeanObjForm.title") + " " + typeHead);

                    //create head obj
                    AnagBean headObj = new AnagBean(typeHead);
                    this.setCurrentAnagBean(headObj);

                    this.makeListField(typeHead, obj);

                    //set workflow info

                    //add info to flowfield 
                    ((StringItem) getHashFields().get(((AnagObj) Session.getListStructureObj().get(this.getCurrentAnagBean().getType())).getFlowField())).setText(Session.getMngWrkFlw().getDescStartNodeID(getCurrentTypeDoc()));

                    createDocCommand = new Command(Locale.get("command.create") + " " + typeDoc, Command.BACK, 0);

                    this.addCommand(createDocCommand);
                    this.addCommand(cancelCommand);


                    this.removeCommand(ManageViewNavigation.backCommand);
                    this.removeCommand(ManageViewNavigation.home);

                    this.append(getListITMS());

                    break;


            }


        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
        }

        return this;

    }

    private void createNewDocForm() {

        //type view   
        this.prepareView(MainForm.TYPEFORM_VIEW, this.getCurrentAnagBean(), null, null);

        //return form
        ManageViewNavigation.getDisplay().setCurrent((Displayable) ManageViewNavigation.getCurrentDisplayed());

        if (getCreateDoc() != null) {

            e = new Event();

            e.setByName("AnagBeanObj", this.getCurrentAnagBean());

            e.setByName("typeObj", this.getCurrentAnagBean().getFieldValue("type"));

            e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_CREATENEWDOC));

            e.setByName("typeDoc", getCreateDoc());


            setCreateDoc(null);

            Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);


        }



    }

    public void commandAction(Command cmd, Displayable disp) {

        try {



            if (cmd == ManageViewNavigation.backCommand) {

                Viewable backView = ManageViewNavigation.getBackView();

                if (backView instanceof PageListForm) {

                    //modific values PageListForm
                    ((Vector) ((Vector) ((PageListForm) backView).getPagesValue().getPagesSerializedObjList()).elementAt(((PageListForm) backView).getCurrentPage())).setElementAt(this.getCurrentAnagBean().serialize(), ((PageListForm) backView).getSelectItm());
                    ((Vector) ((Vector) ((PageListForm) backView).getPagesValue().getPagesValue()).elementAt(((PageListForm) backView).getCurrentPage())).setElementAt(this.getCurrentAnagBean().getTitle(), ((PageListForm) backView).getSelectItm());
                    ((PageListForm) backView).getPagesValue().setValue( this.getCurrentAnagBean().getFieldValueString( ((PageListForm) backView).getPagesValue().getField()))  ;

                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_PAGELISTVALUE_EVENT, e);


                } else if (backView instanceof SearchForm) {

                    //modific values
                    ((Vector) ((Vector) ((SearchForm) backView).getPagesValue().getPagesSerializedObjList()).elementAt(((SearchForm) backView).getCurrentPage())).setElementAt(this.getCurrentAnagBean().serialize(), ((SearchForm) backView).getSelectItm());
                    ((Vector) ((Vector) ((SearchForm) backView).getPagesValue().getPagesValue()).elementAt(((SearchForm) backView).getCurrentPage())).setElementAt(this.getCurrentAnagBean().getTitle(), ((SearchForm) backView).getSelectItm());


                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_SEARCHVALUE_EVENT, e);


                } else {

                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                }


            } else if (cmd == ManageViewNavigation.home) {

                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

            } else if (cmd == ManageViewNavigation.healpCommand) {

                e = new Event();
                e.setByName("helpFile", getHelpFile());
                Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);

            } else if (cmd == ManageViewNavigation.saveCommand) {

                Hashtable params = new Hashtable();
                params.put("AnagBeanObj", this.getCurrentAnagBean());
                params.put("FormObj", getListITMS());
                params.put("typeForm", new Integer(getTypeFORM()));

                if (this.getTypeFORM() == MainForm.TYPEFORM_UPDATE) {

                    Session.getMngService().doService(ManageService.UPDATE_ANAGBEANOBJ_SERVICE, params);


                } else if (this.getTypeFORM() == MainForm.TYPEFORM_NEW) {

                    Session.getMngService().doService(ManageService.SAVE_NEW_ANAGBEANOBJ_SERVICE, params);

                }

                setCreateDoc(null);


            } else if (cmd == ManageViewNavigation.confirmCommand) {

                e = new Event();

                if ((getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD)) {

                    //save old obj for delete index
                    AnagBean oldObj = new AnagBean();
                    oldObj.unserialize(this.getCurrentAnagBean().serialize());
                    e.setByName("oldObj", oldObj);

                    //save only memory modified
                    Session.getAnagBeanDAO().setAnagBeanFromListItms(this.getCurrentAnagBean(), getListITMS());
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_DOC_UPDATE_HEAD_EVENT, e);

                } else if (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW) {

                    //save old obj for delete index
                    AnagBean oldObj = new AnagBean();
                    oldObj.unserialize(this.getCurrentAnagBean().serialize());
                    e.setByName("oldObj", oldObj);

                    //save only memory modified
                    Session.getAnagBeanDAO().setAnagBeanFromListItms(this.getCurrentAnagBean(), getListITMS());
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_DOC_UPDATE_ROW_EVENT, e);

                } else if (this.getTypeFORM() == MainForm.TYPEFORM_DOC_NEW_ROW) {

                    //save only memory modified
                    Session.getAnagBeanDAO().setAnagBeanFromListItms(this.getCurrentAnagBean(), getListITMS());
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_DOC_NEW_ROW_EVENT, e);

                }

            } else if (cmd == add) {

                Hashtable params = new Hashtable();
                params.put("AnagBeanObj", this.getCurrentAnagBean());
                params.put("FormObj", getListITMS());
                params.put("doc", getCurrentDocBean());

                Session.getMngService().doService(ManageService.ADD_EDIT_NEWROWDOCBEAN_SERVICE, params);


            } else if (cmd == Alert.DISMISS_COMMAND) {

                if (getTypeFORM() == MainForm.TYPEFORM_NEW_ROW_CART) {

                    //back
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

                    //set view cart mex
                    ((SearchForm) ManageViewNavigation.getCurrentDisplayed()).setTikerDocumentRow((((SearchForm) ManageViewNavigation.getCurrentDisplayed()).getTikerDocumentRow() == null ? "" : ((SearchForm) ManageViewNavigation.getCurrentDisplayed()).getTikerDocumentRow()) + " -" + getSourceObj().getTitle() + "\n");


                //oggi ManageViewNavigation.getSrcForm().setTikerDocumentRow( (ManageViewNavigation.getSrcForm().getTikerDocumentRow()==null?"":ManageViewNavigation.getSrcForm().getTikerDocumentRow()) + " -" + getSourceObj().getTitle() + "\n" );


                } else if ((getTypeFORM() == MainForm.TYPEFORM_NEW) || (getTypeFORM() == MainForm.TYPEFORM_UPDATE)) {

                    this.createNewDocForm();

                }


            } else if (cmd == ManageViewNavigation.cancelCommand) {

                //only back
                if (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_HEAD) {
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                } else if (getTypeFORM() == MainForm.TYPEFORM_DOC_UPDATE_ROW) {
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                } else if (this.getTypeFORM() == MainForm.TYPEFORM_DOC_NEW_ROW) {
                    //delete row document and back 
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_DOC_DELETE_ROW_EVENT, e);

                }

            } else if (cmd == ManageViewNavigation.updateCommand) {

                selectedIndex = getTable().getSelectedRow();

                this.prepareView(MainForm.TYPEFORM_UPDATE, this.getCurrentAnagBean(), null, null);


            } else if (cmd == createDocCommand) {

                //destroy search thread (if it is working)
                Session.getMngThread().destroyThread(ManageThread.THREAD_SEARCH_OBJ);

                //set source info
                this.getCurrentAnagBean().setField("sourceNumRec", new Integer(Session.getMngIndex().getNextRelativeRecordIDnoSaveValue(getCurrentTypeDoc())));
                this.getCurrentAnagBean().setField("sourceType", getCurrentTypeDoc());


                Hashtable params = new Hashtable();
                params.put("AnagBeanObj", this.getCurrentAnagBean());
                params.put("FormObj", getListITMS());
                params.put("typeForm", new Integer(getTypeFORM()));

                //save head obj
                Session.getMngService().doService(ManageService.SAVE_NEW_ANAGBEANOBJ_SERVICE, params);

                //create docBean
                DocBean doc = new DocBean(getCurrentTypeDoc(), this.getCurrentAnagBean());

                //save document
                Session.getDocBeanDAO().persist(doc);


                e = new Event();

                e.setByName("doc", doc);

                Session.getMngEvent().handleEvent(ManageEvent.VIEW_CART, e);


            } else if (cmd == cancelCommand) {

                //back
                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);


            } else if (cmd == refreshdata) {

                //deve sincronizzare solo questo oggetto e gli oggetti in Link con il server
                Hashtable params = new Hashtable(1);
                params.put("typeObj", this.getCurrentAnagBean().getType());

                Session.getMngService().doService(ManageService.SYNC_HTTP_OBJs_SERVICE, params);


            } else {


                if (getHashCommand().containsKey(cmd)) {

                    String typeCmd = (String) getHashCommand().get(cmd);

                    String[] campi = TextUtil.split(typeCmd, '@');

                    switch (Integer.parseInt(campi[0])) {

                        case (ManageViewNavigation.finddoc):

                            e = new Event();

                            e.setByName("typeObj", campi[1]);
                             //confronto primary key of two objs , suppos that the primay key filed of anag is the same primary key of doc head obj ( Cliente.Denominazione = Testata.Denominazione  ) siccome pero i filedID posso essere differenti devo scaricare l'id della primary key della testata
                            e.setByName("searchField",  ((AnagObj) Session.getListStructureObj().get(((DocObj) Session.getListStructureObj().get(campi[1])).getTypeHeadObj())).getPrimaryKeyField().getId()   != null ? ((AnagObj) Session.getListStructureObj().get(((DocObj) Session.getListStructureObj().get(campi[1])).getTypeHeadObj())).getPrimaryKeyField().getId() : null);
                            e.setByName("searchFieldValue", primaryKey != null ? this.getCurrentAnagBean().getFieldValueString(primaryKey.getId()) : null);

                            Session.getMngEvent().handleEvent(ManageEvent.VIEW_FINDDOC_EVENT, e);

                            break;

                        case (ManageViewNavigation.saveandcreatedoc):

                            //save event
                            Hashtable params = new Hashtable();
                            params.put("AnagBeanObj", this.getCurrentAnagBean());
                            params.put("FormObj", getListITMS());
                            params.put("typeForm", new Integer(getTypeFORM()));

                            ResponseService resp = null;

                            if (this.getTypeFORM() == MainForm.TYPEFORM_UPDATE) {

                                resp = Session.getMngService().doService(ManageService.UPDATE_ANAGBEANOBJ_SERVICE, params);


                            } else if (this.getTypeFORM() == MainForm.TYPEFORM_NEW) {

                                resp = Session.getMngService().doService(ManageService.SAVE_NEW_ANAGBEANOBJ_SERVICE, params);

                            }


                            if (resp.getStatus()) {

                                setCreateDoc(campi[1]);

                                Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.saved"), null, AlertType.INFO);

                                alert.setCommandListener(this);


                            } else {

                                setCreateDoc(null);

                                Alert alert = ManageViewNavigation.showAlert(Locale.get("alert.infosave"), Locale.get("message.nosaved"), null, AlertType.INFO);

                                alert.setCommandListener(this);
                            }



                            break;

                        case (ManageViewNavigation.createdoc):

                            setCreateDoc(campi[1]);

                            this.createNewDocForm();

                            break;

                        case (ManageViewNavigation.findlink):

                            //objlink:Listino;queryLink:Listino.CodProd=Prodotto.Codice
                            e = new Event();

                            String searchField = TextUtil.split(TextUtil.split(TextUtil.split(TextUtil.split(campi[1], ';')[1], ':')[1], '=')[0], '.')[1];
                            String searchFieldVal = TextUtil.split(TextUtil.split(TextUtil.split(TextUtil.split(campi[1], ';')[1], ':')[1], '=')[1], '.')[1];

                            e.setByName("typeObj", TextUtil.split(TextUtil.split(campi[1], ';')[0], ':')[1]);
                            e.setByName("searchField", Session.getMngConfig().getAnagObjFieldIDbyDescription((String)e.getByName("typeObj"),searchField));
                            e.setByName("searchFieldValue", this.getCurrentAnagBean().getFieldValueString( Session.getMngConfig().getAnagObjFieldIDbyDescription(this.getCurrentAnagBean().getType(),searchFieldVal)   ));

                            Session.getMngEvent().handleEvent(ManageEvent.VIEW_FINDDOC_EVENT, e);

                            break;



                    }
                }



            }



        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
        }

    }

    public void itemStateChanged(Item item) {


        if ((item.getLabel() != null) && (getOnChangeMetodFields().containsKey( Session.getMngConfig().getAnagObjFieldIDbyDescription(this.getCurrentAnagBean().getType(), item.getLabel())  ))) {

            Hashtable params = new Hashtable(2);
            Vector props = (Vector) getOnChangeMetodFields().get(Session.getMngConfig().getAnagObjFieldIDbyDescription(this.getCurrentAnagBean().getType(), item.getLabel()));
            params.put("command", ((Property) props.elementAt(0)).getValue());
            params.put("fields", getHashFields());
            params.put("typeObj", getCurrentAnagBean().getType());

            Session.getMngService().doService(ManageService.DO_ONCHANGEITEM_METOD_SERVICE, params);

        }

    }

    public void commandAction(Command cmd, Item item) {
    }

    public ListItem getListITMS() {
        return listITMS;
    }

    public void setListITMS(ListItem listITMS) {
        this.listITMS = listITMS;
    }

    public Hashtable getOnChangeMetodFields() {
        if (onChangeMetodFields == null) {
            onChangeMetodFields = new Hashtable();
        }
        return onChangeMetodFields;
    }

    public void setOnChangeMetodFields(Hashtable onChangeMetodFields) {
        this.onChangeMetodFields = onChangeMetodFields;
    }

    public Hashtable getHashFields() {
        if (hashFields == null) {
            hashFields = new Hashtable();
        }
        return hashFields;
    }

    public void setHashFields(Hashtable hashFields) {
        this.hashFields = hashFields;
    }

    public String getCurrentTypeDoc() {
        return currentTypeDoc;
    }

    public void setCurrentTypeDoc(String currentTypeDoc) {
        this.currentTypeDoc = currentTypeDoc;
    }

    public DocBean getCurrentDocBean() {
        return currentDocBean;
    }

    public void setCurrentDocBean(DocBean currentDocBean) {
        this.currentDocBean = currentDocBean;
    }

    public String getCreateDoc() {
        return createDoc;
    }

    public void setCreateDoc(String createDoc) {
        this.createDoc = createDoc;
    }

    public TableItem getTable() {
        return table;
    }

    public void setTable(TableItem table) {
        this.table = table;
    }

    public AnagBean getSourceObj() {
        return sourceObj;
    }

    public void setSourceObj(AnagBean sourceObj) {
        this.sourceObj = sourceObj;
    }

    public AnagBean getCurrentAnagBean() {
        return currentAnagBean;
    }

    public void setCurrentAnagBean(AnagBean currentAnagBean) {
        this.currentAnagBean = currentAnagBean;
    }

    public Hashtable getHashCommand() {
        if (hashCommand == null) {
            hashCommand = new Hashtable();
        }
        return hashCommand;
    }

    public void setHashCommand(Hashtable hashCommand) {
        this.hashCommand = hashCommand;
    }
}