/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

//#if subMenuActive
//# import de.enough.polish.ui.UiAccess;
//#endif
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.FilteredList;
import de.enough.polish.ui.Item;
import de.enough.polish.util.Locale;
import java.util.Hashtable;
import java.util.Vector;
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
import agile.model.structure.Structure;
import agile.session.Session;
import de.enough.polish.util.TextUtil;


/**
 *
 * @author ruego
 */
public class PageListForm extends MainForm implements Viewable{

    
    //#style addressInput
    private FilteredList fl = new FilteredList(null, Choice.IMPLICIT);
  
    private int currentPage=0;
    private int numPages=0;
    private PaginatedValues pagesValue;
    private Hashtable hashOrderByFields;
    private int selectItm=-1;
    
    private static Command nextPage = new Command(Locale.get("command.nextPage"), Command.SCREEN , 0);
    private static Command beforePage =   new Command(Locale.get("command.beforePage"),Command.SCREEN, 1);
   
     //#if subMenuActive
      //# private static Command orderBy =   new Command(Locale.get("command.orderby"),Command.SCREEN, 2);
     //#endif
    
    private Hashtable hashCommand=null;
    

    public PageListForm() {

        super(null, true, true, true, null);

        setHelpFile("view_list_obj");

    }
    
    public void showView() {

        ManageViewNavigation.getDisplay().setCurrent(getFl());
     
    }

    
     public void setTicker(String mex){
     
           //#style mailTicker
          Ticker tik = new Ticker(mex);

          getFl().setTicker(tik);
     
     }
    
     
      public String getTickerMex(){
     
         Ticker tik = getFl().getTicker();
         
         return  tik!=null?tik.getString():null;
     
     }
     
     
    public void pageNext(){
      
        this.setCurrentPage(this.getCurrentPage() + 1);
    }
    public void pageBack(){
        
        this.setCurrentPage(this.getCurrentPage() - 1);
    }

    public PageListForm prepareView(PaginatedValues pagesValue, int currentPage) {

        try {

            this.deleteAll();

            getFl().deleteAll();

            getFl().removeCommand(beforePage);
            getFl().removeCommand(nextPage);


            if (pagesValue != null) {
                setPagesValue(pagesValue);
            }


            //AnagObj
            if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("AnagObj")) {

                getFl().setTitle(String.valueOf(getCurrentPage() + 1) + " / " + getPagesValue().getNumPages() + " " + getPagesValue().getTypeObj() + "." +   Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(getPagesValue().getTypeObj(),  getPagesValue().getField()) + "." + getPagesValue().getValue().toString()  );


            } else //DocObj
            if (((Structure) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getType().equals("DocObj")) {

                //fields head obj
                 getFl().setTitle(String.valueOf(getCurrentPage() + 1) + " / " + getPagesValue().getNumPages() + " " + getPagesValue().getTypeObj() + "." +   Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(((DocObj) Session.getListStructureObj().get(getPagesValue().getTypeObj())).getTypeHeadObj(),  getPagesValue().getField()) + "." + getPagesValue().getValue().toString() );

            }

            //getFl().setTitle(String.valueOf(getCurrentPage() + 1) + " / " + getPagesValue().getNumPages() + " " + getPagesValue().getTypeObj() + "." + Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(getPagesValue().getTypeObj(), getPagesValue().getField())  + "." + getPagesValue().getValue().toString());

            setNumPages(getPagesValue().getNumPages());
            setCurrentPage(currentPage);

            int size = ((Vector) ((Vector) getPagesValue().getPagesValue()).elementAt(currentPage)).size();

            ChoiceItem title = null;

            for (int i = 0; i < size; i++) {

                //#style senderOption
                title = new ChoiceItem((String) ((Vector) ((Vector) getPagesValue().getPagesValue()).elementAt(currentPage)).elementAt(i), null, Choice.IMPLICIT);

                getFl().append(title);

            }
            
            if (getSelectItm()>=0){
            
                getFl().setSelectedIndex(getSelectItm(), true);
                getFl().focus(getSelectItm());
            
            }


            if ((getNumPages() - 1) > currentPage) {
                getFl().addCommand(nextPage);
            }
            if (currentPage > 0) {
                getFl().addCommand(beforePage);            //add orderBy filter           
            }
            
            if (pagesValue != null) {

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
                if (sizeFld > 1) {

                   
                    //#if subMenuActive
                    //#  getFl().addCommand(orderBy);
                    //#endif
                    
                    Command cmd = null;

                    for (int j = 0; j < sizeFld; j++) {

                        fld = (Field) fldsOrdBy.elementAt(j);

                        //#if subMenuActive
                        //# cmd = new Command(fld.getDescription(), Command.SCREEN, j);     
                        //# UiAccess.addSubCommand(cmd, orderBy, getFl());
                        //#else
cmd = new Command(   Locale.get("command.orderby") + " " + fld.getDescription(), Command.SCREEN, j);
getFl().addCommand(cmd);
                        //#endif

                        getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.orderby) + "@" + fld.getDescription());
                                   
                        
                        getHashOrderByFields().put(fld.getDescription(), fld);

                    }

                }

            }

            getFl().setSelectCommand(ManageViewNavigation.viewCommand);
            getFl().addCommand(ManageViewNavigation.updateCommand);
            getFl().addCommand(ManageViewNavigation.backCommand);
            getFl().addCommand(ManageViewNavigation.home);
            getFl().addCommand(ManageViewNavigation.healpCommand);
            getFl().setCommandListener(this);

        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage());

        }
        
         return this;

    }
    
    
   
        
    public void commandAction(Command cmd, Displayable disp) {
 
        
        if (cmd == nextPage) {

            this.pageNext();
            this.prepareView(null, getCurrentPage());
                      

        }else if (cmd == beforePage) {

            this.pageBack();
            this.prepareView(null, getCurrentPage());
                      

        }else   if (cmd == ManageViewNavigation.viewCommand) {

            setSelectItm(getFl().getSelectedIndex());
            
            if (getSelectItm() >= 0) {

                Event e = new Event();

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

            setSelectItm(getFl().getSelectedIndex());
            
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

             Viewable backView = ManageViewNavigation.getBackView();

             //modific paginated values and list value values
            //((GroupValueObjForm) backView).getPagesGroupValue().getPagesGroupValue().setElementAt(getPagesValue().getValue().toString(), ((GroupValueObjForm) backView).getSelectItm());
            ((Vector) ((Vector) ((GroupValueObjForm) backView).getPagesGroupValue().getPagesGroupValue()).elementAt(((GroupValueObjForm) backView).getCurrentPage())).setElementAt(getPagesValue().getValue().toString(), ((GroupValueObjForm) backView).getSelectItm());

            //#style senderOption
            ChoiceItem   newtitle = new ChoiceItem(getPagesValue().getValue().toString(), null, Choice.IMPLICIT);
            ((GroupValueObjForm) backView).getFl().set( ((GroupValueObjForm) backView).getSelectItm(), newtitle);
 
            //((GroupValueObjForm) backView).getFl().repaint();

            e=new Event();            
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
            
            
        }    else if (cmd == ManageViewNavigation.home) {

          e=new Event();            
          Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        } else if (cmd == ManageViewNavigation.healpCommand) {
           
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);


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

                        Session.getMngService().doService(ManageService.SORT_PAGESVALUE_SERVICE, params);
                       
                        break;


                }
            }
            
            
        }
    }

    public void itemStateChanged(Item item) {
        
            
    }

    
     public  void commandAction(Command cmd, Item item){
     
     }
   

    public FilteredList getFl() {
        return fl;
    }

    public void setFl(FilteredList fl) {
        this.fl = fl;
    }

   
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
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

    public int getSelectItm() {
        return selectItm;
    }

    public void setSelectItm(int selectItm) {
        this.selectItm = selectItm;
    }
    
      public Hashtable getHashCommand() {
        if (hashCommand==null)
            hashCommand=new Hashtable();
        return hashCommand;
    }

    public void setHashCommand(Hashtable hashCommand) {
        this.hashCommand = hashCommand;
    }

    }
    
  
