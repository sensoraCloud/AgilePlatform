/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

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
import agile.model.structure.AnagObj;
import agile.model.structure.DocObj;
import agile.model.structure.PaginatedGroupValues;
import agile.model.structure.Structure;
import agile.session.Session;
import de.enough.polish.util.TextUtil;
//#if subMenuActive
//# import de.enough.polish.ui.UiAccess;
//#endif

/**
 *
 * @author ruego
 */
public class GroupValueObjForm extends MainForm implements Viewable{

    
    //#style addressInput
    private FilteredList fl = new FilteredList(null, Choice.IMPLICIT);
  
    private int currentPage=0;
    private int numPages=0;
    private PaginatedGroupValues pagesGroupValue;
    
    public static Command nextPage = new Command(Locale.get("command.nextPage"), Command.SCREEN , 0);
    public static Command beforePage =   new Command(Locale.get("command.beforePage"),Command.SCREEN, 1);
    
    public static Command syncObjs = null;
   
     //#if subMenuActive
      //# public static Command filterBy =   new Command(Locale.get("command.filterby"),Command.SCREEN, 2);
     //#endif
    
    private Hashtable hashCommand=null;
    
    private Structure strc;

    private int selectItm=-1;

    public GroupValueObjForm() {

        super(null, true, true, true, null);

        setHelpFile("view_group");

    }
    
     public void showView(){
         
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

    public GroupValueObjForm prepareView(PaginatedGroupValues pagesGroupValue,int currentPage,Structure strc) {

        try {

            this.deleteAll();

            getFl().deleteAll();

            getFl().removeCommand(beforePage);
            getFl().removeCommand(nextPage);
            
            try{
            getFl().removeCommand(syncObjs);
            }catch (Exception e){}
            
    
            getFl().setSelectCommand(ManageViewNavigation.viewCommand);
            getFl().addCommand(ManageViewNavigation.backCommand);
            getFl().addCommand(ManageViewNavigation.home);
            getFl().addCommand(ManageViewNavigation.healpCommand);
            syncObjs=new Command(Locale.get("command.sync") + " " +pagesGroupValue.getTypeObj(),Command.SCREEN, 2);
            getFl().addCommand(syncObjs);
            getFl().setCommandListener(this);


            //AnagObj
            if (((Structure) Session.getListStructureObj().get(pagesGroupValue.getTypeObj())).getType().equals("AnagObj")) {

                getFl().setTitle(String.valueOf(getCurrentPage() + 1) + " / " + pagesGroupValue.getNumPages() + " " + pagesGroupValue.getTypeObj() + "." +   Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(pagesGroupValue.getTypeObj(),  pagesGroupValue.getField())  );


            } else //DocObj
            if (((Structure) Session.getListStructureObj().get(pagesGroupValue.getTypeObj())).getType().equals("DocObj")) {

                //fields head obj
                 getFl().setTitle(String.valueOf(getCurrentPage() + 1) + " / " + pagesGroupValue.getNumPages() + " " + pagesGroupValue.getTypeObj() + "." +   Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(((DocObj) Session.getListStructureObj().get(pagesGroupValue.getTypeObj())).getTypeHeadObj(),  pagesGroupValue.getField())  );

            }


           
            setPagesGroupValue(pagesGroupValue);
            setNumPages(pagesGroupValue.getNumPages());
            setCurrentPage(currentPage);


            int size =0;
            
            if ((((Vector) getPagesGroupValue().getPagesGroupValue())!=null)&&((((Vector) getPagesGroupValue().getPagesGroupValue()).size()>0))&&(((Vector) ((Vector) getPagesGroupValue().getPagesGroupValue()).elementAt(currentPage))!=null))            
              size = ((Vector) ((Vector) getPagesGroupValue().getPagesGroupValue()).elementAt(currentPage)).size();

            ChoiceItem title = null;

            for (int i = 0; i < size; i++) {

                //#style senderOption
                title = new ChoiceItem((String) ((Vector) ((Vector) getPagesGroupValue().getPagesGroupValue()).elementAt(currentPage)).elementAt(i), null, Choice.IMPLICIT);

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
                getFl().addCommand(beforePage);
            }
            
            
            if (strc != null) {

                setStrc(strc);
                //add filter command
                if ((getStrc().getListIndexFields() != null) && (getStrc().getListIndexFields().size() != 0)) {
      
                    
                //#if subMenuActive
                //# getFl().addCommand(filterBy);
                //#endif
                    
                    
                    String fldName = null;
                    Command cmd = null;

                    size = getStrc().getListIndexFields().size() + 4;

                    for (int i = 4; i < size; i++) {

                       
                        //AnagObj
                        if (((Structure) Session.getListStructureObj().get(pagesGroupValue.getTypeObj())).getType().equals("AnagObj")) {

                            fldName = Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(pagesGroupValue.getTypeObj(), (String) getStrc().getListIndexFields().elementAt(i - 4));
                           

                        } else //DocObj
                        if (((Structure) Session.getListStructureObj().get(pagesGroupValue.getTypeObj())).getType().equals("DocObj")) {

                            //fields head obj
                            fldName = Session.getMngConfig().getAnagObjFieldDescriptionByFieldID(((DocObj) Session.getListStructureObj().get(pagesGroupValue.getTypeObj())).getTypeHeadObj(), (String) getStrc().getListIndexFields().elementAt(i - 4));
                            
                        }


                        if ((fldName == null) || (fldName.equals(""))) {
                            continue;
                        }

                        //#if subMenuActive
                        //# cmd = new Command(fldName, Command.SCREEN, i);            
                        //# UiAccess.addSubCommand(cmd, filterBy, getFl());
                        //#else
cmd = new Command(   Locale.get("command.filterby") + " " + fldName, Command.SCREEN, i);
getFl().addCommand(cmd);
                        //#endif

                        getHashCommand().put(cmd, String.valueOf(ManageViewNavigation.filterBy) + "@" + fldName);
                        

                    }

                }


            }

           



        } catch (Exception e) {
            //#debug error
            System.out.println(e.getMessage()==null?"Null":e.getMessage());

        }
         return this;
    }
    
      
    public void commandAction(Command cmd, Displayable disp) {
 
        
        if (cmd == nextPage) {

            this.pageNext();
            this.prepareView(getPagesGroupValue(), getCurrentPage(),null);
                      

        }else if (cmd == beforePage) {

            this.pageBack();
            this.prepareView(getPagesGroupValue(), getCurrentPage(),null);
                      

        }else   if (cmd == ManageViewNavigation.viewCommand) {
            
            try {

                setSelectItm(getFl().getSelectedIndex());

                if (getSelectItm() >= 0) {


                    Hashtable params = new Hashtable(4);
                    params.put("typeObj", getPagesGroupValue().getTypeObj());
                    params.put("searchField", getPagesGroupValue().getField());
                    params.put("valueField", (String) ((Vector) ((Vector) getPagesGroupValue().getPagesGroupValue()).elementAt(getCurrentPage())).elementAt(getSelectItm()));

                    if (((Structure) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj())).getType().equals("AnagObj")) {

                        //  params.put("indexableObjClass", new AnagBean());
                        params.put("structureObjClass", (AnagObj) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj()));


                    } else if (((Structure) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj())).getType().equals("DocObj")) {

                        //  params.put("indexableObjClass", new DocBean());
                        params.put("structureObjClass", (DocObj) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj()));

                    }

                    Session.getMngService().doService(ManageService.SEARCH_GROUPOBJS_SERVICE, params);




                }
               

            } catch (Exception e) {
                //#debug error
                System.out.println(e.getMessage() == null ? "Null" : e.getMessage());

            }

        }else  if (cmd == ManageViewNavigation.backCommand) {

            e=new Event();            
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
            
            
        }    else if (cmd == ManageViewNavigation.home) {

          e=new Event();            
          Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        } else if (cmd == ManageViewNavigation.healpCommand) {
           
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);


        }else if (cmd == syncObjs) {
            
           
            Hashtable params = new Hashtable(1);
            params.put("typeObj", getPagesGroupValue().getTypeObj());

            Session.getMngService().doService(ManageService.SYNC_HTTP_OBJs_SERVICE, params);


        } else {

            if (getHashCommand().containsKey(cmd)) {

                String typeCmd = (String) getHashCommand().get(cmd);

                String[] campi = TextUtil.split(typeCmd, '@');

                switch (Integer.parseInt(campi[0])) {

                    case (ManageViewNavigation.filterBy):

                        setSelectItm(0);

                        this.setCurrentPage(0);

                        Hashtable params = new Hashtable(3);
                        params.put("typeObj", getPagesGroupValue().getTypeObj());

                       
                        if (((Structure) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj())).getType().equals("AnagObj")) {

                            params.put("structureObjClass", (AnagObj) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj()));

                             params.put("searchField",  Session.getMngConfig().getAnagObjFieldIDbyDescription(getPagesGroupValue().getTypeObj(), campi[1])   );


                        } else if (((Structure) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj())).getType().equals("DocObj")) {

                            params.put("structureObjClass", (DocObj) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj()));

                             params.put("searchField",  Session.getMngConfig().getAnagObjFieldIDbyDescription(((DocObj) Session.getListStructureObj().get(getPagesGroupValue().getTypeObj())).getTypeHeadObj(), campi[1])   );

                        }

                        params.put("typeSourceEvent", "Rebuild_groupValueObj");

                        Session.getMngService().doService(ManageService.SEARCH_GROUPVALUE_SERVICE, params);

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

    public PaginatedGroupValues getPagesGroupValue() {
        return pagesGroupValue;
    }

    public void setPagesGroupValue(PaginatedGroupValues pagesGroupValue) {
        this.pagesGroupValue = pagesGroupValue;
    }

    public Structure getStrc() {
        return strc;
    }

    public void setStrc(Structure strc) {
        this.strc = strc;
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
     * @return the selectItm
     */
    public int getSelectItm() {
        return selectItm;
    }

    /**
     * @param selectItm the selectItm to set
     */
    public void setSelectItm(int selectItm) {
        this.selectItm = selectItm;
    }
}
    
  
