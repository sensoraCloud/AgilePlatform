/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;


import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.DateField;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.ScreenStateListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.Locale;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Ticker;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.model.service.ManageService;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.DocBean;
import agile.model.structure.DocObj;
import agile.model.structure.Field;
import agile.model.structure.PaginatedValues;
import agile.model.structure.Property;
import agile.model.structure.ResponseService;
import agile.model.structure.Structure;
import agile.model.thread.ManageThread;
import agile.session.Session;
import agile.util.Date_Util;
import de.enough.polish.util.TextUtil;

/**
 *
 * @author ruego
 */
public class SearchForm extends MainForm implements Viewable,ScreenStateListener{

    public static Command getDoactionCommand() {
        return doactionCommand;
    }
   
    private TabbedForm form = null;
    private ChoiceGroup results=null;
    private int currentPage=0;
    private PaginatedValues pagesValue;
    private Hashtable hashOrderByFields;
    private DocBean currentDoc;
    private String currentTypeDoc;
    private String currentTypeObj;
    private Field currentSearchField;
    private Hashtable searchFields;
    private int selectItm=-1;   
    
    private StringItem searchTypeObjStr =null;
    
    private de.enough.polish.ui.ChoiceGroup   searchTypeObj = null;
   
    private de.enough.polish.ui.ChoiceGroup searchFld = null;
   
    private TextField searchText = null;
    
    private DateField searchDateFrom = null;
    
    private DateField searchDateTo = null;
    
    private String tikerDocumentRow=null;
    
   
    private static Command search =   new Command(Locale.get("command.search"),Command.BACK, 0);
    public static Command nextPage = new Command(Locale.get("command.nextPage"), Command.SCREEN , 0);
    private static Command beforePage =   new Command(Locale.get("command.beforePage"),Command.SCREEN, 1);
    public static Command viewCommand = new Command(Locale.get("command.view"), Command.OK, 2);
    public static Command createDocCommand =  null;
    public static Command add = new Command(Locale.get("command.add"), Command.BACK, 0);
    public static Command edit = new Command(Locale.get("command.edit"), Command.ITEM, 0);
    public static Command end = new Command(Locale.get("command.end"), Command.SCREEN, 10);
    public static Command interupt = new Command(Locale.get("command.interupt"), Command.BACK, 0);
    
    private static Command viewCart =   new Command(Locale.get("command.viewcart"),Command.SCREEN, 15);
    
    public static Command doactionCommand =  null;
    
    private String typeCart=null;
    
    private String currentTargetID=null;//descNode
    private Integer currentActivityID=null;
    private Hashtable listNodes=null;
    private String descFlowField=null;
    private String idFlowField=null;
    
    private de.enough.polish.ui.ChoiceGroup nodeChoice = null;
   
  
   //#if subMenuActive
      //# private static Command orderBy =   new Command(Locale.get("command.orderby"),Command.SCREEN, 2);
     //#endif
    
    private Hashtable hashCommand=null;
    
    public SearchForm() {

        super(null, false, false, false, null);   
        
    }
    
    public void showView() {

        ManageViewNavigation.getDisplay().setCurrent(getForm());
     
    }

    
     public void setTicker(String mex) {


        if (mex != null) {

            //#style mailTicker
            Ticker tik = new Ticker(mex);

            this.getForm().setTicker(tik);

        } 


    }
    
     
      public String getTickerMex(){
     
         Ticker tik = getForm().getTicker();
         
         return  tik!=null?tik.getString():null;
     
     }
     
     
    public void pageNext(){
      
        this.setCurrentPage(this.getCurrentPage() + 1);
    }
    
    public void pageBack(){
        
        this.setCurrentPage(this.getCurrentPage() - 1);
    }

    
    public SearchForm prepareView(int typeFORM, String typeObj, String searchField,Object searchFieldValue,  String typeDocObj, DocBean docBean,String descNode, int currentPage) {

        try {

            if (typeFORM!=0)
                setTypeFORM(typeFORM);           
            
            if (typeFORM == MainForm.TYPEFORM_SEARCH_STANDARD) {   
                
                    this.deleteAll();
                    setForm(null);
                    setResults(null);
                    setCurrentDoc(null);
                    setCurrentTypeDoc(null);
                    setCurrentTypeObj(null);
                    setHashOrderByFields(null);
                    setPagesValue(null); 
                    setCurrentSearchField(null);
                   
                                
                    String[] headings = new String[]{Locale.get("SearchForm.filter")};

                    //#style tabbedForm
                    form=new TabbedForm(Locale.get("SearchForm.standard.title") + " " + typeObj, headings, null);

                                 
                    getForm().setCommandListener(this);
                    getForm().setItemStateListener(this);
                    getForm().setScreenStateListener(this);
                    
                    this.setHelpFile("search");


                    setCurrentTypeObj(typeObj);

                    //#style simpleinput
                    searchTypeObjStr=new StringItem(Locale.get("SearchForm.searchTypeObj"), getCurrentTypeObj());

                    getForm().append(0,getSearchTypeObjStr());


                    //load searchTypeField free,search,cluster,index
                  
                    //AnagObj
                    if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("AnagObj")) {

                         this.modifiedSearchField(getCurrentTypeObj(),searchField);

                    } else //DocObj
                    if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("DocObj")) {

                         this.modifiedSearchField(((DocObj) Session.getListStructureObj().get(getCurrentTypeObj())).getTypeHeadObj(),searchField);

                    }                   

                    this.modifiedSearchText((String)searchFieldValue);               
                    
                    getForm().addCommand(search);
                    getForm().addCommand(ManageViewNavigation.backCommand);
                    getForm().addCommand(ManageViewNavigation.healpCommand);
              
                      if (getSearchText()!=null)                    
                        getForm().focus(getSearchText());
                    else if (getSearchDateFrom()!=null) 
                         getForm().focus(getSearchDateFrom());
                

            } else if (typeFORM == MainForm.TYPEFORM_SEARCH_NEWDOC) {
                
                    this.deleteAll();
                    setForm(null);
                    setResults(null);
                    setCurrentDoc(null);
                    setCurrentTypeDoc(null);
                    setCurrentTypeObj(null);
                    setHashOrderByFields(null);
                    setPagesValue(null); 
                    setCurrentSearchField(null);
                    
                   

                String[] headings = new String[]{Locale.get("SearchForm.filter")};

                if (typeDocObj != null) {
                    setCurrentTypeDoc(typeDocObj);
                }                
                
                //#style tabbedForm
                form = new TabbedForm(Locale.get("SearchForm.newdoc.title") + " " + getCurrentTypeDoc(), headings, null);

                
                getForm().setCommandListener(this);
                getForm().setItemStateListener(this);
                getForm().setScreenStateListener(this);

                this.setHelpFile("search_newdoc");

                setTicker("Seleziona un oggetto per precompilare la testata del documento! ");

                //search linkable headobj
                Vector props = ((DocObj) Session.getListStructureObj().get(getCurrentTypeDoc())).getPropertyByKey("linkHeadObj");
                Property pr = null;
                
                String[] values = new String[props.size()];
                
                long size = props.size();

                for (int j = 0; j < size; j++) {

                    pr = (Property) props.elementAt(j);
                    values[j]=(String)pr.getValue();
                    
                }
                
                //#style simpleChoice
                searchTypeObj = new ChoiceGroup(Locale.get("SearchForm.searchTypeObj") , Choice.POPUP, values, null);
              
                searchTypeObj.setSelectedIndex(0, true);
               
                
                getForm().append(0, getSearchTypeObj());                

                //load searchTypeField free,search,cluster,index

                this.setCurrentTypeObj((String) getSearchTypeObj().getString(getSearchTypeObj().getSelectedIndex()));
                
                this.modifiedSearchField(getCurrentTypeObj(),searchField);

                this.modifiedSearchText((String)searchFieldValue);

                getForm().addCommand(search);
                getForm().addCommand(ManageViewNavigation.backCommand);
                getForm().addCommand(ManageViewNavigation.healpCommand);
                
                  if (getSearchText()!=null)                    
                        getForm().focus(getSearchText());
                    else if (getSearchDateFrom()!=null) 
                         getForm().focus(getSearchDateFrom());


            } else if (typeFORM == MainForm.TYPEFORM_SEARCH_CART) {
               
                this.deleteAll();
                setForm(null);
                setResults(null);
                setCurrentDoc(null);
                setCurrentTypeDoc(null);
                setCurrentTypeObj(null);
                setHashOrderByFields(null);
                setPagesValue(null);
                setCurrentSearchField(null);
               

                if (docBean != null) {

                    setCurrentDoc(docBean);
                    setCurrentTypeDoc(docBean.getType());
                }
                     
               
                String[] headings = new String[]{Locale.get("SearchForm.filter")};

                         
                
                //#style tabbedForm
                form = new TabbedForm(Locale.get("SearchForm.cart.title") + " " + getCurrentDoc().getType(), headings, null);

                setTicker("Aggiungi gli oggetti al tuo documento ed infine premi su Finito! ");

                getForm().setCommandListener(this);
                getForm().setItemStateListener(this);
                getForm().setScreenStateListener(this);

                this.setHelpFile("cart");  

                if (typeObj != null) {
                    
                    setTypeCart("Cart_UpdateDoc");
                    
                    setCurrentTypeObj(typeObj);

                    //#style simpleinput
                    searchTypeObjStr=new StringItem(Locale.get("SearchForm.searchTypeObj"), getCurrentTypeObj());

                    getForm().append(0,getSearchTypeObjStr());

                } else {

                    setTypeCart("Cart_newDoc");

                    //search linkable rowobj
                    Vector props = ((DocObj) Session.getListStructureObj().get(getCurrentTypeDoc())).getPropertyByKey("linkRowObj");
                    Property pr = null;

                    String[] values = new String[props.size()];

                    long size = props.size();

                    for (int j = 0; j < size; j++) {

                        pr = (Property) props.elementAt(j);
                        values[j] = (String) pr.getValue();

                    }

                    if (size == 1) {

                        setCurrentTypeObj(values[0]);

                        //#style simpleinput
                        searchTypeObjStr = new StringItem(Locale.get("SearchForm.searchTypeObj") , getCurrentTypeObj());

                        getForm().append(0, getSearchTypeObjStr());


                    } else {

                        //#style simpleChoice
                        searchTypeObj = new ChoiceGroup(Locale.get("SearchForm.searchTypeObj")  , Choice.POPUP, values, null);

                        searchTypeObj.setSelectedIndex(0, true);

                        getForm().append(0, getSearchTypeObj());

                        //load searchTypeField free,search,cluster,index
                        this.setCurrentTypeObj((String) getSearchTypeObj().getString(getSearchTypeObj().getSelectedIndex()));


                    }



                }
                
 
                this.modifiedSearchField(getCurrentTypeObj(),searchField);

                this.modifiedSearchText((String)searchFieldValue);

                getForm().addCommand(search);
               // getForm().addCommand(backCommand);
                getForm().addCommand(end);
                getForm().addCommand(ManageViewNavigation.healpCommand);       
                
                getForm().addCommand(viewCart);
                
                if (getSearchText()!=null)                    
                        getForm().focus(getSearchText());
                    else if (getSearchDateFrom()!=null) 
                         getForm().focus(getSearchDateFrom());

            }else  if (typeFORM == MainForm.TYPEFORM_SEARCH_WORKFLOW) {   
                
                    this.deleteAll();
                    setForm(null);
                    setResults(null);
                    setCurrentDoc(null);
                    setCurrentTypeDoc(null);
                    setCurrentTypeObj(null);
                    setHashOrderByFields(null);
                    setPagesValue(null); 
                    setCurrentSearchField(null);
                    setCurrentTargetID(null);
                    setCurrentActivityID(null);
                    
                                
                    String[] headings = new String[]{Locale.get("SearchForm.filter")};

                    //#style tabbedForm
                    form=new TabbedForm(Locale.get("SearchForm.workflow.title") + " " + typeObj, headings, null);

                                 
                    getForm().setCommandListener(this);
                    getForm().setItemStateListener(this);
                    getForm().setScreenStateListener(this);
                    
                    this.setHelpFile("search_workflow");

                    setCurrentTypeObj(typeObj);

                    //#style simpleinput
                    searchTypeObjStr=new StringItem(Locale.get("SearchForm.searchTypeObj"), getCurrentTypeObj());

                    getForm().append(0,getSearchTypeObjStr());

                    //load workflow nodes
                    this.modifiedFlowNode(descNode);                     

                    //load searchTypeField free,search,cluster,index
                  
                    //AnagObj
                    if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("AnagObj")) {

                         this.modifiedSearchField(getCurrentTypeObj(),searchField);

                    } else //DocObj
                    if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("DocObj")) {

                         this.modifiedSearchField(((DocObj) Session.getListStructureObj().get(getCurrentTypeObj())).getTypeHeadObj(),searchField);

                    }                   

                    this.modifiedSearchText((String)searchFieldValue);               
                    
                    getForm().addCommand(search);
                    getForm().addCommand(ManageViewNavigation.backCommand);
                    getForm().addCommand(ManageViewNavigation.healpCommand);
              
                    if (getSearchText()!=null)                    
                        getForm().focus(getSearchText());
                    else if (getSearchDateFrom()!=null) 
                         getForm().focus(getSearchDateFrom());
                    else if (getNodeChoice()!=null) 
                        getForm().focus(getNodeChoice());
                    
                

            } 

            if (getResults() != null) {

                getResults().removeCommand(beforePage);
                getResults().removeCommand(nextPage);

                setCurrentPage(currentPage);

                if ((getNumPages() - 1) > currentPage) {
                    getResults().addCommand(nextPage);
                }
                if (currentPage > 0) {
                    getResults().addCommand(beforePage);
                }


            }
            
            //recall for paginated values   
            if (typeFORM == 0) {

                int size = ((Vector) ((Vector) getPagesValue().getPagesValue()).elementAt(currentPage)).size();

                ChoiceItem title = null;

                getResults().deleteAll();

                for (int i = 0; i < size; i++) {

                    if (getTypeFORM() == MainForm.TYPEFORM_SEARCH_WORKFLOW) {

                        //#style listOption
                        title = new ChoiceItem((String) ((Vector) ((Vector) getPagesValue().getPagesValue()).elementAt(currentPage)).elementAt(i), null, Choice.MULTIPLE);

                    } else {
                        //#style listOption
                        title = new ChoiceItem((String) ((Vector) ((Vector) getPagesValue().getPagesValue()).elementAt(currentPage)).elementAt(i), null, Choice.IMPLICIT);


                    }

                    getResults().append(title);

                }

                getForm().setText(1, Locale.get("SearchForm.results") + String.valueOf(getCurrentPage() + 1) + "/" + getNumPages());

                if (getSelectItm() >= 0) {

                    if (getTypeFORM() != MainForm.TYPEFORM_SEARCH_WORKFLOW)
                         getResults().setSelectedIndex(getSelectItm(), true);
                         // getResults().focus(getSelectItm());
                          getResults().focusChild(getSelectItm());

                }
                
            }

          

        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage() == null ? "Null" : e.getMessage());
        }
        
         return this;

    }

   
   public void loadOrderByCommand(){
   
          if (getPagesValue() != null) {

                Vector flds = null;
                Vector fldsOrdBy = new Vector();

                //AnagObj
                if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("AnagObj")) {

                    flds = ((AnagObj) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getListFields();


                } else //DocObj
                if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("DocObj")) {

                    //fields head obj    
                    flds = ((AnagObj) Session.getListStructureObj().get(((DocObj) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getTypeHeadObj())).getListFields();

                }

                Field fld = null;
                int sizeFld = flds.size();

                for (int j = 0; j < sizeFld; j++) {

                    fld = (Field) flds.elementAt(j);

                    if (fld.hasProperty("printSmallDesc")) {

                        fldsOrdBy.addElement(fld);

                    }
                }

                sizeFld = fldsOrdBy.size();


                //if fields title > 1 set orderby else the title is already sorted
                if (sizeFld > 0) {

                   
                    
                      //#if subMenuActive
                //#   getForm().addCommand(orderBy);
                //#endif
                    
                    
                    Command cmd = null;

                    for (int j = 0; j < sizeFld; j++) {

                        fld = (Field) fldsOrdBy.elementAt(j);
                        
                        
                        //#if subMenuActive
                        //# cmd = new Command(fld.getDescription(), Command.SCREEN, j);    
                        //# UiAccess.addSubCommand(cmd, orderBy, getForm());
                        //#else
cmd = new Command(   Locale.get("command.orderby") + " " + fld.getDescription(), Command.SCREEN, j);
getForm().addCommand(cmd);
                        //#endif

                        getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.orderby) + "@" + fld.getDescription());
                        
                        
                        getHashOrderByFields().put(fld.getDescription(), fld);

                    }

                }

            }   
   
   
   }
   
     public void screenStateChanged(Screen screen) {
            
        if (screen == this.getForm()) {            
            
            int tabIndex = this.getForm().getActiveTab();
            
            if (tabIndex == 1) {

                this.getForm().removeCommand(search);              
               

            } else {

                this.getForm().addCommand(search);
                
                if (getSearchText() != null) {
                    getForm().focus(getSearchText());
                } else if (getSearchDateFrom() != null) {
                    getForm().focus(getSearchDateFrom());
                }

                //this.repaint();

            }
            

        }
    }
     
    private void modifiedFlowNode(String setFlowNode) {


        setCurrentTargetID(setFlowNode);
        setCurrentActivityID(null);

        Hashtable nodes = Session.getMngWrkFlw().getListNodesFlowID(Session.getMngWrkFlw().getFlowID(getCurrentTypeObj()));

        setListNodes(nodes);

        //set choicegroup value

        String[] values = new String[getListNodes().size()];

        int i = 0;

        if (getCurrentTargetID() != null) {
            values[0] = getCurrentTargetID();
            i = 1;
        }


        Enumeration en = getListNodes().keys();

        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            if ((getCurrentTargetID() != null) && (getCurrentTargetID().equals(key))) {
                continue;
            } else if (i == 0) {
                setCurrentTargetID(key);
            }
            values[i] = key;
            i++;
        }

        String descFlowField = null;

        //AnagObj
        if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("AnagObj")) {

            descFlowField =  Session.getMngConfig().getAnagObjFieldDescriptionByFieldID( getCurrentTypeObj(),((AnagObj) Session.getListStructureObj().get(getCurrentTypeObj())).getFlowField());

            setIdFlowField(((AnagObj) Session.getListStructureObj().get(getCurrentTypeObj())).getFlowField());

        } else //DocObj
        if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("DocObj")) {

            //fields head obj
            descFlowField =  Session.getMngConfig().getAnagObjFieldDescriptionByFieldID( ((DocObj) Session.getListStructureObj().get(getCurrentTypeObj())).getTypeHeadObj(),((DocObj) Session.getListStructureObj().get(getCurrentTypeObj())).getFlowField());

            setIdFlowField( ((DocObj) Session.getListStructureObj().get(getCurrentTypeObj())).getFlowField() );
        }

        setDescFlowField(descFlowField);

        //#style simpleChoice
        nodeChoice = new ChoiceGroup(descFlowField, Choice.POPUP, values, null);

        getNodeChoice().setSelectedIndex(0, true);

        //set activity
        setCurrentActivityID(Session.getMngWrkFlw().getActivityIDNextHighPriority(getCurrentTypeObj(), getCurrentTargetID()));


        getForm().append(0, getNodeChoice());

    }
     
      
      private void modifiedSearchField(String typeObj,String setSearchField) {


        Vector flds = null;

        setSearchFields(null);
        setSearchFld(null);
        
        //fields head obj    
        flds = ((AnagObj) Session.getListStructureObj().get(typeObj)).getListFields();

        Field fld = null;
        int sizeFld = flds.size();

        for (int j = 0; j < sizeFld; j++) {

            fld = (Field) flds.elementAt(j);

            if ((fld.hasProperty("search")) || (fld.hasProperty("cluster")) || (fld.hasProperty("index"))) {

                getSearchFields().put(fld.getId(), fld);

                if ((setSearchField!=null)&&(setSearchField.equals(fld.getId())))
                     setCurrentSearchField(fld);
                
                if (getCurrentSearchField() == null) {

                    if (fld.hasProperty("primaryKey")) {
                        setCurrentSearchField(fld);
                    }
                    else
                    if (fld.hasProperty("defaultGroupIndex")) {
                        setCurrentSearchField(fld);
                    }

                }
               
                
            }
        }
      
        //if setSearchField==null and WorkFlow mode set nothing field
        if ((setSearchField==null) && (getTypeFORM()==MainForm.TYPEFORM_SEARCH_WORKFLOW)){
        
              setCurrentSearchField(null);
        
        }
        
        
        //set choicegroup value

        String[] values = new String[getSearchFields().size() + 1];

        int i = 0;

        if (getCurrentSearchField() != null) {
            
            values[0] = getCurrentSearchField().getDescription();
            i = 1;
            
        }else if (getTypeFORM()==MainForm.TYPEFORM_SEARCH_WORKFLOW){
           
            values[0]=Locale.get("SearchForm.nothing");
            i = 1;
        
        }
   
       
        Enumeration en = getSearchFields().keys();

        while (en.hasMoreElements()) {

            String key = (String) en.nextElement();
            
            if ((getCurrentSearchField() != null) && (getCurrentSearchField().getId().equals(key))) {
                continue;
            } else if (i == 0) {
                setCurrentSearchField((Field) getSearchFields().get(key));
            }
            
            values[i] = ((Field) getSearchFields().get(key) ).getDescription() ;
            i++;
            
        }


        //#style simpleChoice
        searchFld = new ChoiceGroup(Locale.get("SearchForm.searchField") , Choice.POPUP, values, null);

        getSearchFld().setSelectedIndex(0,true);
        
        getForm().append(0, getSearchFld());

    }
    
    private void modifiedSearchText(String searchFieldValue) {

        if (getCurrentSearchField() != null) {

            if (getCurrentSearchField().getTypeUI().equals("DATE")) {

                //#style simpleinput
                searchDateFrom = new DateField(Locale.get("SearchForm.from"), DateField.DATE);

                //#style simpleinput
                searchDateTo = new DateField(Locale.get("SearchForm.to"), DateField.DATE);

                if (searchFieldValue != null) {
                    
                    getSearchDateFrom().setDate(Date_Util.getDatefromStamp( Long.parseLong(searchFieldValue)));

                    getSearchDateTo().setDate(Date_Util.getDatefromStamp( Long.parseLong(searchFieldValue)));
                    
                    
                } else {
                  
                    //30 days before now
                    long now = Date_Util.getStampFromDate(new Date()).longValue();
                    long days30before = now - (long) 2592000;
                    getSearchDateFrom().setDate(Date_Util.getDatefromStamp(days30before));

                    getSearchDateTo().setDate(new Date());

                }
               

                getForm().append(0, getSearchDateFrom());
                getForm().append(0, getSearchDateTo());

            } else if (getCurrentSearchField().getTypeUI().equals("NUMERIC")) {

                //#style simpleinput
                searchText = new TextField(Locale.get("SearchForm.search"), searchFieldValue != null ? searchFieldValue : null, getCurrentSearchField().getMaxLenght().intValue(), TextField.NUMERIC);

                UiAccess.setInputMode(getSearchText(), UiAccess.MODE_NATIVE);
                
                getForm().append(0, getSearchText());

            } else if (getCurrentSearchField().getTypeUI().equals("DECIMAL")) {

                //#style simpleinput
                searchText = new TextField(Locale.get("SearchForm.search"), searchFieldValue != null ? searchFieldValue : null, getCurrentSearchField().getMaxLenght().intValue(), TextField.DECIMAL);

                UiAccess.setInputMode(getSearchText(), UiAccess.MODE_NATIVE);


                getForm().append(0, getSearchText());

            } else {

                //#style simpleinput
                searchText = new TextField(Locale.get("SearchForm.search"), searchFieldValue != null ? searchFieldValue : null, getCurrentSearchField().getMaxLenght().intValue(), TextField.ANY);

                UiAccess.setInputMode(getSearchText(), UiAccess.MODE_NATIVE);


                getForm().append(0, getSearchText());

            }



        }


    }
    
    
    public void doSearch() {

        this.setCurrentPage(0);

        this.getForm().removeCommand(search);
        //#if subMenuActive
        //#   this.getForm().removeCommand(orderBy);
        //#endif
       
        Hashtable params = new Hashtable(3);
        params.put("searchForm", this);
        Session.getMngService().doService(ManageService.SEARCH_OBJ_SERVICE, params);

    }
        
    public void commandAction(Command cmd, Displayable disp) {
       
       if (cmd == search) {
          
           doSearch();                  

        }else  if (cmd == ManageViewNavigation.backCommand) {

            e=new Event();            
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
            
            
        }  else if (cmd == ManageViewNavigation.home) {

          e=new Event();            
          Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        } else if (cmd == ManageViewNavigation.healpCommand) {
           
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);


        } else if (cmd == end) {
           
           e = new Event();
           e.setByName("DocBeanObj", getCurrentDoc());
           e.setByName("callForm", getTypeCart());

           Session.getMngEvent().handleEvent(ManageEvent.VIEW_DOCBEANFORM_EVENT, e);


        }else if (cmd == viewCart) {
           
           ManageViewNavigation.showAlert(Locale.get("SearchForm.cart.title"),getTikerDocumentRow()==null?"":getTikerDocumentRow(), null, AlertType.INFO);

        }else if (cmd == interupt) {
           try {
               Session.getMngThread().destroyThread(ManageThread.THREAD_SEARCH_OBJ);
           } catch (Exception e) {
           }
        }else//order by
        {   
            if (getHashCommand().containsKey(cmd)) {

                String typeCmd = (String) getHashCommand().get(cmd);

                String[] campi = TextUtil.split(typeCmd, '@');

                switch (Integer.parseInt(campi[0])) {

                    case (ManageViewNavigation.orderby):

                         setSelectItm(0);

                        this.setCurrentPage(0);

                        Hashtable params = new Hashtable(3);
                        params.put("values", getPagesValue());
                        params.put("sortField", getHashOrderByFields().get(campi[1]));


                        Session.getMngService().doService(ManageService.SORT_SEARCHRESULTS_SERVICE, params);

                         getForm().setActiveTab(1);

                        break;


                }
            }
                
           
            

        }
    }
      
  
     public  void commandAction(Command cmd, Item item){
      
         
       if (cmd == nextPage) {

            this.pageNext();
            this.prepareView(0, null, null,null, null, null,null,  getCurrentPage());
                      

        }else if (cmd == beforePage) {

             this.pageBack();
             this.prepareView(0, null, null,null, null, null, null, getCurrentPage());
                      

        }else  if (cmd == viewCommand) {

            setSelectItm(getResults().getFocusedIndex());
            
            Event e = new Event();            
           
           if (getSelectItm() >= 0) {

               //if AnagObj
               if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("AnagObj")) {

                   AnagBean anagBeanObj = new AnagBean();

                   anagBeanObj.unserialize((String) ((Vector) ((Vector) getPagesValue().getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getSelectItm()));

                   e.setByName("AnagBeanObj", anagBeanObj);

                   e.setByName("typeObj", anagBeanObj.getFieldValue("type"));

                   e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_VIEW));

                   Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);

               } else if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("DocObj")) {

                   DocBean docBeanObj = new DocBean();

                   docBeanObj.unserialize((String) ((Vector) ((Vector) getPagesValue().getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getSelectItm()));

                   e.setByName("DocBeanObj", docBeanObj);

                   Session.getMngEvent().handleEvent(ManageEvent.VIEW_DOCBEANFORM_EVENT, e);

               }

           }
            
            

        }else if (cmd == ManageViewNavigation.updateCommand) {

            setSelectItm(getResults().getFocusedIndex());
            
           if (getSelectItm() >= 0) {

               Event e = new Event();

               //if AnagObj
               if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("AnagObj")) {

                   AnagBean anagBeanObj = new AnagBean();

                   anagBeanObj.unserialize((String) ((Vector) ((Vector) pagesValue.getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getSelectItm()));

                   e.setByName("AnagBeanObj", anagBeanObj);

                   e.setByName("typeObj", anagBeanObj.getFieldValue("type"));

                   e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_UPDATE));

                   Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);

               } else if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("DocObj")) {

                   DocBean docBeanObj = new DocBean();

                   docBeanObj.unserialize((String) ((Vector) ((Vector) getPagesValue().getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getSelectItm()));

                   e.setByName("DocBeanObj", docBeanObj);

                   Session.getMngEvent().handleEvent(ManageEvent.VIEW_DOCBEANFORM_EVENT, e);

               }
           }
            
        }else  if (cmd == ManageViewNavigation.backCommand) {

            e=new Event();            
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
            
            
        }  else if (cmd == ManageViewNavigation.home) {

          e=new Event();            
          Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        } else if (cmd == ManageViewNavigation.healpCommand) {
           
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);


        }else  if (cmd == createDocCommand) {

           e = new Event();
           
           AnagBean anagBeanObj = new AnagBean();

           anagBeanObj.unserialize((String) ((Vector) ((Vector) pagesValue.getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getResults().getFocusedIndex()));

           e.setByName("AnagBeanObj", anagBeanObj);

           e.setByName("typeObj", anagBeanObj.getFieldValue("type"));

           e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_CREATENEWDOC));

           e.setByName("typeDoc", getCurrentTypeDoc());
           
           Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);
            
            
        } else  if (cmd == edit) {

           e = new Event();
           
           AnagBean anagBeanObj = new AnagBean();

           anagBeanObj.unserialize((String) ((Vector) ((Vector) pagesValue.getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getResults().getFocusedIndex()));

           e.setByName("AnagBeanObj", anagBeanObj);

           e.setByName("typeObj", anagBeanObj.getFieldValue("type"));

           e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_NEW_ROW_CART));

           e.setByName("typeDoc", getCurrentDoc().getType());
           
           e.setByName("doc", getCurrentDoc());
           
           Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_CART_EVENT, e);
            
           
            
        } else  if (cmd == add) {

           AnagBean anagBeanObj = new AnagBean();

           anagBeanObj.unserialize((String) ((Vector) ((Vector) pagesValue.getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(getResults().getFocusedIndex()));
        
           Hashtable params = new Hashtable(3);
           params.put("typeObj", getCurrentDoc().getTypeRowObj());
           params.put("anagbean",anagBeanObj); 
           params.put("docbean", getCurrentDoc());

           Session.getMngService().doService(ManageService.ADD_NEW_ROWDOC_FROM_ANAGBEAN, params);  setTikerDocumentRow( (getTikerDocumentRow()==null?"":getTikerDocumentRow()) + " -" + anagBeanObj.getTitle() + "\n" );
                 
          
           
        } else if (cmd == viewCart) {
           
           ManageViewNavigation.showAlert(Locale.get("SearchForm.cart.title"),getTikerDocumentRow()==null?"":getTikerDocumentRow(), null, AlertType.INFO);

        }else if (cmd == end) {
           
           e = new Event();
           e.setByName("DocBeanObj", getCurrentDoc());
           e.setByName("callForm", getTypeCart());

           Session.getMngEvent().handleEvent(ManageEvent.VIEW_DOCBEANFORM_EVENT, e);


        } else if (cmd == interupt) {
           try {
               Session.getMngThread().destroyThread(ManageThread.THREAD_SEARCH_OBJ);
           } catch (Exception e) {
           }
        } else if (cmd == doactionCommand) {
           
           Hashtable params = new Hashtable(3);
           params.put("activityID", getCurrentActivityID());           

           int maxObjsPages = ((Integer) ((Hashtable) Session.getListConfig().get("View")).get("maxItemsForPage")).intValue();

           boolean[] selected=new boolean[maxObjsPages];
      
           getResults().getSelectedFlags(selected);
           
           Vector listObj=new Vector();
           
           int size=selected.length;
           
           if (size > 0) {

               for (int i = 0; i < size; i++) {

                   if (selected[i]) {
                       listObj.addElement((String) ((Vector) ((Vector) pagesValue.getPagesSerializedObjList()).elementAt(getCurrentPage())).elementAt(i));
                   }
               } 
               
               if (listObj.size() > 0) {

                   params.put("listObj", listObj);
                   params.put("typeObj", getCurrentTypeObj());

                   ResponseService resp = Session.getMngService().doService(ManageService.DO_ACTIVITY_SERVICE, params);


               } else {

                   ManageViewNavigation.showAlert("Info", Locale.get("message.oneelement"), null, AlertType.INFO);

               }

              
           }else{
           
                 ManageViewNavigation.showAlert("Info",Locale.get("message.oneelement"), null, AlertType.INFO);

           
           }
          

        }
        else//order by
        {  
            
           if (getHashCommand().containsKey(cmd)) {

                String typeCmd = (String) getHashCommand().get(cmd);

                String[] campi = TextUtil.split(typeCmd, '@');

                switch (Integer.parseInt(campi[0])) {

                    case (ManageViewNavigation.orderby):

                         setSelectItm(0);
                         
                        this.setCurrentPage(0);

                        Hashtable params = new Hashtable(3);
                        params.put("values", getPagesValue());
                        params.put("sortField", getHashOrderByFields().get(campi[1]));
                       

                        Session.getMngService().doService(ManageService.SORT_SEARCHRESULTS_SERVICE, params);
                       
                         getForm().setActiveTab(1);
                        
                        break;


                }
            }
                    
           
        }
         
     
     }
      
   
       public void itemStateChanged(Item item) {
        
        if (item == getSearchTypeObj()) {

            try {

                getForm().removeTab(1);

            } catch (Exception e) {
            }

            //if thread search go , must stop
            Session.getMngThread().destroyThread(ManageThread.THREAD_SEARCH_OBJ);
            setResults(null);
            setPagesValue(null);

            setCurrentTypeObj(getSearchTypeObj().getString(getSearchTypeObj().getSelectedIndex()));

            
            try {

                getForm().delete(0, getSearchFld());

            } catch (Exception e) {
            }
            
            this.modifiedSearchField(getCurrentTypeObj(),null);

            try {

                getForm().delete(0, getSearchDateFrom());

            } catch (Exception e) {
            }

            try {
                getForm().delete(0, getSearchDateTo());
            } catch (Exception e) {
            }

            try {
                getForm().delete(0, getSearchText());
            } catch (Exception e) {
            }

           
            this.modifiedSearchText(null);


        } else if (item == getNodeChoice()) {

            if (getNodeChoice().getSelectedIndex() >= 0) {

                
                try {

                    getForm().removeTab(1);

                } catch (Exception e) {
                }

                //if thread search go , must stop
                Session.getMngThread().destroyThread(ManageThread.THREAD_SEARCH_OBJ);
                setResults(null);
                setPagesValue(null);
                
                setCurrentTargetID(getNodeChoice().getString(getNodeChoice().getSelectedIndex()));

                //set activity
                setCurrentActivityID(Session.getMngWrkFlw().getActivityIDNextHighPriority(getCurrentTypeObj(), getCurrentTargetID()));

            }
            
        }else if (item == getSearchFld()) {

             //=0 è il campo Nessuno.. solo per workflow
            boolean camponessuno = false;
            if (this.getTypeFORM() == MainForm.TYPEFORM_SEARCH_WORKFLOW) {
                camponessuno = true;
            }

            
            if (getSearchFld().getSelectedIndex() >= 0) {

                if (!((camponessuno)&&(getSearchFld().getSelectedIndex() == 0))) {


                    //AnagObj
                    if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("AnagObj")) {

                        setCurrentSearchField((Field) getSearchFields().get(Session.getMngConfig().getAnagObjFieldIDbyDescription(getCurrentTypeObj(), getSearchFld().getString(getSearchFld().getSelectedIndex()))));


                    } else //DocObj
                    if (((Structure) Session.getListStructureObj().get(getCurrentTypeObj())).getType().equals("DocObj")) {


                        //se è un documento dovro cercare tra i field dell anagbean di testata
                        setCurrentSearchField((Field) getSearchFields().get(Session.getMngConfig().getAnagObjFieldIDbyDescription(((DocObj) Session.getListStructureObj().get(getCurrentTypeObj())).getTypeHeadObj(), getSearchFld().getString(getSearchFld().getSelectedIndex()))));

                    }


                }else setCurrentSearchField(null);


                try {

                    getForm().delete(0, getSearchDateFrom());

                } catch (Exception e) {
                }

                try {
                    getForm().delete(0, getSearchDateTo());
                } catch (Exception e) {
                }

                try {
                    getForm().delete(0, getSearchText());
                } catch (Exception e) {
                }

               
                this.modifiedSearchText(null);


            }



        }

        
            
    }

    
     
     
     
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumPages() {
        if (getPagesValue()==null)
            return 0;
        else return getPagesValue().getNumPages();        
    }

   
    public PaginatedValues getPagesValue() {
        return pagesValue;
    }

    public void setPagesValue(PaginatedValues pagesValue) {
        this.pagesValue = pagesValue;
    }

    public Hashtable getHashOrderByFields() {
        if (hashOrderByFields==null)
            hashOrderByFields=new Hashtable();
        return hashOrderByFields;
    }

    public void setHashOrderByFields(Hashtable hashOrderByFields) {
        this.hashOrderByFields = hashOrderByFields;
    }

    public DocBean getCurrentDoc() {
        return currentDoc;
    }

    public void setCurrentDoc(DocBean currentDoc) {
        this.currentDoc = currentDoc;
    }

    public String getCurrentTypeDoc() {
        return currentTypeDoc;
    }

    public void setCurrentTypeDoc(String currentTypeDoc) {
        this.currentTypeDoc = currentTypeDoc;
    }
 

    public String getCurrentTypeObj() {
        return currentTypeObj;
    }

    public void setCurrentTypeObj(String currentTypeObj) {
        this.currentTypeObj = currentTypeObj;
    }

    public Field getCurrentSearchField() {
        return currentSearchField;
    }

    public void setCurrentSearchField(Field currentSearchField) {
        this.currentSearchField = currentSearchField;
    }

    public Hashtable getSearchFields() {
        
        if (searchFields==null)
            searchFields=new Hashtable();
        
        return searchFields;
    }

    public void setSearchFields(Hashtable searchFields) {
        this.searchFields = searchFields;
    }

    public TabbedForm getForm() {
        return form;
    }

    public void setForm(TabbedForm form) {
        this.form = form;
    }

    public ChoiceGroup getResults() {
        return results;
    }

    public void setResults(ChoiceGroup results) {
        this.results = results;
    }

    public StringItem getSearchTypeObjStr() {
        return searchTypeObjStr;
    }

    public void setSearchTypeObjStr(StringItem searchTypeObjStr) {
        this.searchTypeObjStr = searchTypeObjStr;
    }

    public de.enough.polish.ui.ChoiceGroup getSearchTypeObj() {
        return searchTypeObj;
    }

    public void setSearchTypeObj(de.enough.polish.ui.ChoiceGroup searchTypeObj) {
        this.searchTypeObj = searchTypeObj;
    }

    public de.enough.polish.ui.ChoiceGroup getSearchFld() {
        return searchFld;
    }

    public void setSearchFld(de.enough.polish.ui.ChoiceGroup searchFld) {
        this.searchFld = searchFld;
    }

    public TextField getSearchText() {
        return searchText;
    }

    public void setSearchText(TextField searchText) {
        this.searchText = searchText;
    }

    public DateField getSearchDateFrom() {
        return searchDateFrom;
    }

    public void setSearchDateFrom(DateField searchDateFrom) {
        this.searchDateFrom = searchDateFrom;
    }

    public DateField getSearchDateTo() {
        return searchDateTo;
    }

    public void setSearchDateTo(DateField searchDateTo) {
        this.searchDateTo = searchDateTo;
    }
  

    public int getSelectItm() {
        return selectItm;
    }

    public void setSelectItm(int selectItm) {
        this.selectItm = selectItm;
    }

    public String getTypeCart() {
        return typeCart;
    }

    public void setTypeCart(String typeCart) {
        this.typeCart = typeCart;
    }

    public String getTikerDocumentRow() {
        return tikerDocumentRow;
    }

    public void setTikerDocumentRow(String tikerDocumentRow) {
        this.tikerDocumentRow = tikerDocumentRow;
    }

    public Integer getCurrentActivityID() {
        return currentActivityID;
    }

    public void setCurrentActivityID(Integer currentActivityID) {
        this.currentActivityID = currentActivityID;
    }

    public String getCurrentTargetID() {
        return currentTargetID;
    }

    public void setCurrentTargetID(String currentTargetID) {
        this.currentTargetID = currentTargetID;
    }

    public Hashtable getListNodes() {
        if (listNodes==null)
            listNodes=new Hashtable();
        return listNodes;
    }

    public void setListNodes(Hashtable listNodes) {
        this.listNodes = listNodes;
    }

    public de.enough.polish.ui.ChoiceGroup getNodeChoice() {
        return nodeChoice;
    }

    public String getDescFlowField() {
        return descFlowField;
    }

    public void setDescFlowField(String descFlowField) {
        this.descFlowField = descFlowField;
    }

    public Hashtable getHashCommand() {
        if (hashCommand==null)
            hashCommand=new Hashtable();
        return hashCommand;
    }

    public void setHashCommand(Hashtable hashCommand) {
        this.hashCommand = hashCommand;
    }

    /**
     * @return the idFlowField
     */
    public String getIdFlowField() {
        return idFlowField;
    }

    /**
     * @param idFlowField the idFlowField to set
     */
    public void setIdFlowField(String idFlowField) {
        this.idFlowField = idFlowField;
    }

    
    }
    
  
