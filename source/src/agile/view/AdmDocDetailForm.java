/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TableItem;
import de.enough.polish.util.Locale;
import java.util.Hashtable;
import java.util.Vector;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.model.service.ManageService;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.DocBean;
import agile.model.structure.Field;
import agile.model.structure.Property;
import agile.session.Session;
import agile.util.Date_Util;
import agile.workflow.ManageWorkFlow;
//#if subMenuActive
//# import de.enough.polish.ui.UiAccess;
//#endif
import de.enough.polish.util.TextUtil;
import java.util.Enumeration;

/**
 *
 * @author ruego
 */
public class AdmDocDetailForm extends MainForm implements Viewable{
  
  private DocBean oldDoc;
  private DocBean currentDoc;
  private TableItem table =null;  

  //command          
  
   //#if subMenuActive
  //# private Command head =   new Command(Locale.get("AdmDocDetailForm.command.head"), Command.SCREEN , 1);
  //# private Command row =   new Command(Locale.get("AdmDocDetailForm.command.row"), Command.ITEM , 2);
  //# private Command duplicateCommand = new Command(Locale.get("command.duplicatein"), Command.SCREEN, 7);
  //#endif
  
  private Command viewHead;
  private Command updateHead;
  private Command rowNew;
  private Command rowUpdate;        
  private Command rowDelete;
  
  
  private Command confirm =   new Command(Locale.get("AdmDocDetailForm.command.confirm"), Command.BACK , 0);
  private Command confirmAndSendToServer =   new Command(Locale.get("AdmDocDetailForm.command.confirmAndSendToServer"), Command.SCREEN , 24);
  private Command cancel =   new Command(Locale.get("AdmDocDetailForm.command.cancel"), Command.SCREEN , 25);
  private Command delete =   new Command(Locale.get("AdmDocDetailForm.command.delete"), Command.SCREEN , 26);
  private Command print =   new Command(Locale.get("command.print"), Command.SCREEN , 27);
  
  private Hashtable hashCommand=null;

  private int selectedIndex = 0;
  
  //action
  public static final int DELETE_LAST_ROW_ACTION=1;
  public static final int COMMIT_UPDATE_ROW_ACTION=2;
  public static final int COMMIT_NEW_ROW_ACTION=3;
  public static final int COMMIT_UPDATE_HEAD_ACTION=4;
  public static final int COMMIT_DELETE_ROW_ACTION=5;
  public static final int ADD_CANCEL_COMMAND_ACTION=6;
   
   public AdmDocDetailForm() {            
             
        super(null,false,true,false,null);
             
       //#if subMenuActive
           //#  viewHead =   new Command(Locale.get("AdmDocDetailForm.command.viewHead"), Command.SCREEN , 1);
           //#  updateHead =   new Command(Locale.get("AdmDocDetailForm.command.updateHead"), Command.SCREEN , 2);
           //#  rowNew =   new Command(Locale.get("AdmDocDetailForm.command.rowNew"), Command.ITEM , 1);
           //#  rowUpdate =   new Command(Locale.get("AdmDocDetailForm.command.rowUpdate"), Command.ITEM , 2);
            //#  rowDelete =   new Command(Locale.get("AdmDocDetailForm.command.rowDelete"), Command.ITEM , 3);
       //#else
viewHead =   new Command(Locale.get("AdmDocDetailForm.command.viewHead") + " " + Locale.get("AdmDocDetailForm.command.head"), Command.SCREEN , 1);
updateHead =   new Command(Locale.get("AdmDocDetailForm.command.updateHead")+ " " + Locale.get("AdmDocDetailForm.command.head"), Command.SCREEN , 2);
rowNew =   new Command(Locale.get("AdmDocDetailForm.command.rowNew") + " " + Locale.get("AdmDocDetailForm.command.row") , Command.ITEM , 3);
rowUpdate =   new Command(Locale.get("AdmDocDetailForm.command.rowUpdate")+ " " + Locale.get("AdmDocDetailForm.command.row"), Command.ITEM , 4);
rowDelete =   new Command(Locale.get("AdmDocDetailForm.command.rowDelete")+ " " + Locale.get("AdmDocDetailForm.command.row"), Command.ITEM , 5);
      //#endif 
             
             
       setHelpFile("adm_docbean");       
            
           
    }
   
    public void showView(){
     
         ManageViewNavigation.getDisplay().setCurrent(this);    
     
     }
     
     public void makeTableView() throws Exception{
               
      
        int sizeRowObjs = getCurrentDoc().getRows().size();
        
         if (sizeRowObjs > 0) {

             //fields of rowobj
             Vector listfields = ((AnagObj) Session.getListStructureObj().get(getCurrentDoc().getTypeRowObj())).getListFields();

             Field fld = null;
             int sizeField = listfields.size();


             //#style defaultTable
             TableItem newTab = new TableItem(sizeField, (sizeRowObjs + 1));
             setTable(newTab);

             //header and rows

             for (int r = 0; r < sizeRowObjs; r++) {

                 for (int f = 0; f < sizeField; f++) {

                     fld = (Field) listfields.elementAt(f);

                     //compile header
                     if (r == 0) {
                         //#style heading
                         table.set(f, r, fld.getDescription());
                     }

                     //security  control
                     if (fld.getReadSecurityLevel().intValue() > Session.getActiveUser().getSecurityLevelRead()) {

                         //#style leftedCellnoExpand		
                         table.set(f, r + 1, Locale.get("message.notautorizedtoread"));

                     } else {

                         AnagBean rw = (AnagBean) getCurrentDoc().getRow(r);
                         String value = rw.getFieldValueString((fld.getId())) == null ? "" : ((String) rw.getFieldValueString(fld.getId()));

                         if ((fld.getTypeUI().equals("DATE")) && (!value.equals(""))) {

                             value = Date_Util.getStrfromData(Date_Util.getDatefromStamp(Long.parseLong(value)), Date_Util.dmyHHmmss, false);
                         }
                         
                         //#style leftedCellnoExpand		
                         table.set(f, r + 1, value);

                     }

                 }


             }

             if (sizeRowObjs>0)
                getTable().setSelectedCell(0,getSelectedIndex());
             
             table.setSelectionMode(TableItem.SELECTION_MODE_ROW | TableItem.SELECTION_MODE_COLUMN);

             this.append(table);


         }

         
      
    }
    
    
     public AdmDocDetailForm prepareView(DocBean doc,int action,AnagBean oldAnagBean){
     
         try {

             this.deleteAll();
             //#if subMenuActive
                 //# this.removeCommand(head);
             //#else
this.removeCommand(viewHead);
this.removeCommand(updateHead);
             //#endif
            
             
             //this.removeCommand(document);  
             this.removeCommand(confirm);
             this.removeCommand(confirmAndSendToServer);
             this.removeCommand(delete);
             this.removeCommand(cancel);
             this.removeCommand(print);
             

             if (doc != null) {
                 setCurrentDoc(doc);
                 setOldDoc(new DocBean());
                 //copy doc
                 getOldDoc().unserialize(doc.serialize());
             }
             
             this.addCommand(confirm);
             this.addCommand(confirmAndSendToServer);
             this.addCommand(delete);
             this.addCommand(print);

             if (action!=0){                 
                          
                 switch (action){
                     
                     //delete last row (new row canceled)
                     case DELETE_LAST_ROW_ACTION:  getCurrentDoc().deleteRow(getCurrentDoc().getRows().size()-1);
                                                   this.addCommand(cancel);
                            break;
                      
                     case COMMIT_NEW_ROW_ACTION:   Session.getDocBeanDAO().persistNewRow(getCurrentDoc(), (getCurrentDoc().getRow(getCurrentDoc().getRows().size() - 1)));
                                                   this.addCommand(cancel);
                                                   
                            break;
                       
                     case COMMIT_UPDATE_ROW_ACTION:Session.getDocBeanDAO().commitRow(getCurrentDoc(), getCurrentDoc().getRow(getCurrentDoc().getRows().size()-1), oldAnagBean);
                                                   getCurrentDoc().makeTitle();   
                                                   this.addCommand(cancel); 
                            break;
                            
                        
                     case COMMIT_DELETE_ROW_ACTION:  int selectedRow = getTable().getSelectedRow();
                                                     if (selectedRow > 0) {
                                                         
                                                         AnagBean row=getCurrentDoc().getRow(selectedRow - 1);
                                                         getCurrentDoc().deleteRow(selectedRow - 1);                                                         
                                                         Session.getDocBeanDAO().deleteRow(getCurrentDoc(),row);
                                                                                                                  
                                                         this.addCommand(cancel);

                                                     }                                                    
                                                     break;                                                   
                            
                     
                     case COMMIT_UPDATE_HEAD_ACTION: Session.getDocBeanDAO().commitHead(getCurrentDoc(), getCurrentDoc().getHeadObj(),oldAnagBean);
                                                     this.addCommand(cancel);
                            break;
               
                     case ADD_CANCEL_COMMAND_ACTION:
                         this.addCommand(cancel);
                         break;

                 }
             
             
             }

                         
             this.setTitle(Locale.get("AdmDocDetailForm.title") + " " + getCurrentDoc().getType());
     
             //set head

             //#style titleDoc
             StringItem str = new StringItem(Locale.get("AdmDocDetailForm.head"), getCurrentDoc().getTitle());

             this.append(str);
             
             //set rows
             
             this.makeTableView();
             
             //add command
             
             //#if subMenuActive
             //#  this.addCommand(this.head);             
             //#  UiAccess.addSubCommand(viewHead, head, this);
             //#  UiAccess.addSubCommand(updateHead, head, this);
             //#else
this.addCommand(viewHead);
this.addCommand(updateHead);
             //#endif
             
                         
            
             if (doc != null) {

                 //#if subMenuActive
                 //#   this.removeCommand(row);               
                 //#   this.removeCommand(duplicateCommand);     
                 //#   this.addCommand(row);
                 //#   UiAccess.addSubCommand(rowNew, row, this);          
                 //#   UiAccess.addSubCommand(rowUpdate, row, this);
                 //#   UiAccess.addSubCommand(rowDelete, row, this);
                 //#else
this.removeCommand(rowNew);
this.removeCommand(rowUpdate);
this.removeCommand(rowDelete);
this.addCommand(rowNew);
this.addCommand(rowUpdate);
this.addCommand(rowDelete);
                 //#endif
                 
                 //remove command in hash
                 Enumeration en = getHashCommand().keys();
                 Command cmd = null;
                 while (en.hasMoreElements()) {

                     cmd = (Command) en.nextElement();
                     this.removeCommand(cmd);
                     getHashCommand().remove(cmd);

                 }
                 

                 //add linkObj command

                 Vector linkRowObjs = ((agile.model.structure.DocObj) Session.getListStructureObj().get(getCurrentDoc().getType())).getPropertyByKey("linkRowObj");

                 Property prop = null;
                 cmd = null;

                 int size = linkRowObjs.size();

                 int posCmd=0;
                 
                 //#if subMenuActive
                 //#  posCmd=0;
                 //#else
posCmd=6;
                 //#endif
                 
                 
                 for (int i = 0; i < size; i++) {
                    
                     
                     prop = (Property) linkRowObjs.elementAt(i);

                     if ((prop == null) || (prop.equals(""))) {
                         continue;
                     }

                     if ((prop.getValue() == null) || (prop.getValue().equals(""))) {
                         continue;
                     }

                     //#if subMenuActive
                     //# cmd = new Command(Locale.get("AdmDocDetailForm.command.add") + " " + prop.getValue(), Command.ITEM, (posCmd + 4));
                     //# UiAccess.addSubCommand(cmd, row, this);
                     //#else
cmd = new Command(Locale.get("AdmDocDetailForm.command.add") + " " + prop.getValue(), Command.ITEM, (posCmd));
this.addCommand(cmd);
                         //#endif
                
                     getHashCommand().put( cmd , String.valueOf(ManageViewNavigation.addbeantodoc) + "@" + prop.getValue()  );

                     posCmd++;

                 }
                 
                 //add duplicate command
                 
                 Vector linkDuplicateObj = ((agile.model.structure.DocObj) Session.getListStructureObj().get(getCurrentDoc().getType())).getPropertyByKey("linkDuplicateObj");

                 prop = null;
                 cmd = null;

                 size = linkDuplicateObj.size();
                 
                 //#if subMenuActive
                 //# if (size>0)
                 //#   this.addCommand(duplicateCommand);
                 //#  posCmd=0;
                 //#endif
                 
                 for (int i = 0; i < size; i++) {

                     prop = (Property) linkDuplicateObj.elementAt(i);

                     if ((prop == null) || (prop.equals(""))) {
                         continue;
                     }

                     if ((prop.getValue() == null) || (prop.getValue().equals(""))) {
                         continue;
                     }

                     
                      //#if subMenuActive
                     //# cmd = new Command((String)prop.getValue(), Command.ITEM, posCmd);
                     //# UiAccess.addSubCommand(cmd, duplicateCommand, this);
                     //#else
cmd = new Command(Locale.get("command.duplicatein") + " " + prop.getValue(),Command.ITEM, posCmd);
this.addCommand(cmd);
                     //#endif
                
                     getHashCommand().put( cmd , String.valueOf(ManageViewNavigation.duplicatedoc) + "@" + prop.getValue()  );
                                
                     posCmd++;

                 }
                 


             }
             
             if (getCurrentDoc().getRows().size() == 0) {

                 //#style mex
                 StringItem mex = new StringItem(null,Locale.get("AdmDocDetailForm.command.norows"));                 
                 
                 this.append(mex);                 
                 
             }
             
             
             
             
         } catch (Exception e) {
             //#debug error
             System.out.println(e.getMessage()==null?"Null":e.getMessage());
         }
     
         return this;
         
     }
    
    
    public void commandAction(Command cmd, Displayable disp) {


        if (cmd == ManageViewNavigation.healpCommand) {
            
            e = new Event();
            e.setByName("helpFile",getHelpFile());
            Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);

        } else if (cmd == viewHead) {

            e = new Event();

            e.setByName("AnagBeanObj", getCurrentDoc().getHeadObj());

            e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_DOC_VIEW_HEAD));

            Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);


        } else if (cmd == updateHead) {

            e = new Event();

            e.setByName("AnagBeanObj", getCurrentDoc().getHeadObj());

            e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_DOC_UPDATE_HEAD));          

            Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);

        }else if (cmd == rowUpdate) {

            selectedIndex = getTable().getSelectedRow();

            e = new Event();

            int selectedRow=getTable().getSelectedRow();
            
            if (selectedRow > 0) {

                e.setByName("AnagBeanObj", getCurrentDoc().getRow(selectedRow-1));

                e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_DOC_UPDATE_ROW));
               
                
                Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);

            }
           


        } else if (cmd == rowNew) {
                        
            e = new Event();

            AnagBean newRowObj=new AnagBean(getCurrentDoc().getTypeRowObj());
   
            getCurrentDoc().addRowObj(newRowObj);
            
            e.setByName("AnagBeanObj", newRowObj);

            e.setByName("typeFORM", new Integer(MainForm.TYPEFORM_DOC_NEW_ROW));
                       
            Session.getMngEvent().handleEvent(ManageEvent.VIEW_ANAGBEANFORM_EVENT, e);

            selectedIndex = getTable().getSelectedRow()+1;

        }else if (cmd == rowDelete) {            
                           
           this.prepareView(null,COMMIT_DELETE_ROW_ACTION,getCurrentDoc().getHeadObj());
           
           selectedIndex = getTable().getSelectedRow()-1;

        }else if (cmd == confirm) {      
            
              Viewable backView = ManageViewNavigation.getBackView();
            
             if (backView instanceof PageListForm) {

                    //modific values
                    ((Vector) ((Vector) ((PageListForm)backView).getPagesValue().getPagesSerializedObjList()).elementAt(((PageListForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().serialize(), ((PageListForm)backView).getSelectItm());
                    ((Vector) ((Vector) ((PageListForm)backView).getPagesValue().getPagesValue()).elementAt(((PageListForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().getTitle(), ((PageListForm)backView).getSelectItm());
         
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_PAGELISTVALUE_EVENT, e);


                }else if (backView instanceof SearchForm) {

                    //modific values
                    ((Vector) ((Vector) ((SearchForm)backView).getPagesValue().getPagesSerializedObjList()).elementAt(((SearchForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().serialize(), ((SearchForm)backView).getSelectItm());
                    ((Vector) ((Vector) ((SearchForm)backView).getPagesValue().getPagesValue()).elementAt(((SearchForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().getTitle(), ((SearchForm)backView).getSelectItm());

                     
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_SEARCHVALUE_EVENT, e);


                } else {

                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

            }
          
                  
          
        }else if (cmd == confirmAndSendToServer) {            
          
           //!!!quando riceve ok dal sever va modificato il targetID e ricommitato per sistemare index workflow
            
           //invio al server e aspetta risposta
            
            String serializeObjr=getCurrentDoc().serialize();
            
             //#debug info
            System.out.println("String send to SERVER: " + serializeObjr);
            
            
            Vector listObj=new Vector();
            
           listObj.addElement(serializeObjr);           
          
           Hashtable params=new Hashtable(3);
           params.put("activityID", new Integer(ManageWorkFlow.SEND_TO_SERVER_ACTIVITY));       
           params.put("listObj",listObj);
           params.put("typeObj",getCurrentDoc().getType());      
           
           
           Session.getMngService().doService(ManageService.DO_ACTIVITY_SERVICE, params);
 
            Viewable backView = ManageViewNavigation.getBackView();
            
            if (backView instanceof PageListForm) {

                    //modific values
                    ((Vector) ((Vector) ((PageListForm)backView).getPagesValue().getPagesSerializedObjList()).elementAt(((PageListForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().serialize(), ((PageListForm)backView).getSelectItm());
                    ((Vector) ((Vector) ((PageListForm)backView).getPagesValue().getPagesValue()).elementAt(((PageListForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().getTitle(), ((PageListForm)backView).getSelectItm());
         
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_PAGELISTVALUE_EVENT, e);


                }else if (backView instanceof SearchForm) {

                    //modific values
                    ((Vector) ((Vector) ((SearchForm)backView).getPagesValue().getPagesSerializedObjList()).elementAt(((SearchForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().serialize(), ((SearchForm)backView).getSelectItm());
                    ((Vector) ((Vector) ((SearchForm)backView).getPagesValue().getPagesValue()).elementAt(((SearchForm)backView).getCurrentPage())).setElementAt(getCurrentDoc().getTitle(), ((SearchForm)backView).getSelectItm());

                     
                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_SEARCHVALUE_EVENT, e);


                } else {

                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

            }
            
        } else if (cmd == cancel) {

            Hashtable params = new Hashtable();

            params.put("doc", getCurrentDoc());
            params.put("oldDoc", getOldDoc());

            Session.getMngService().doService(ManageService.CANCEL_UPDATE_DOCBEAN_SERVICE, params);


        } else if (cmd == delete) {


            Hashtable params = new Hashtable();

            params.put("docbean", getCurrentDoc());

            Session.getMngService().doService(ManageService.DELETE_DOC_SERVICE, params);



        } else if (cmd == print) {


            Hashtable params = new Hashtable();

            params.put("docbean", getCurrentDoc());

            Session.getMngService().doService(ManageService.PRINT_DOC_SERVICE, params);



        } else if (cmd == Alert.DISMISS_COMMAND) {
        
             Viewable backView = ManageViewNavigation.getBackView();
            
            //alert Deleted!
            if (backView instanceof PageListForm) {

                //modific values
                ((Vector) ((Vector) ((PageListForm)backView).getPagesValue().getPagesSerializedObjList()).elementAt(((PageListForm)backView).getCurrentPage())).removeElementAt(((PageListForm)backView).getSelectItm());
                ((Vector) ((Vector) ((PageListForm)backView).getPagesValue().getPagesValue()).elementAt(((PageListForm)backView).getCurrentPage())).removeElementAt(((PageListForm)backView).getSelectItm());

                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_PAGELISTVALUE_EVENT, e);


            } else if (backView instanceof SearchForm) {

                //modific values
                ((Vector) ((Vector) ((SearchForm)backView).getPagesValue().getPagesSerializedObjList()).elementAt(((SearchForm)backView).getCurrentPage())).removeElementAt(((SearchForm)backView).getSelectItm());
                ((Vector) ((Vector) ((SearchForm)backView).getPagesValue().getPagesValue()).elementAt(((SearchForm)backView).getCurrentPage())).removeElementAt(((SearchForm)backView).getSelectItm());

                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.REBUILD_GOBACK_SEARCHVALUE_EVENT, e);


            } else {

                e = new Event();
                Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

            }

        
        }else {
            
             if (getHashCommand().containsKey(cmd)) {

                    String typeCmd = (String) getHashCommand().get(cmd);

                    String[] campi = TextUtil.split(typeCmd, '@');

                    switch (Integer.parseInt(campi[0])) {

                        case (ManageViewNavigation.addbeantodoc):
                           
                            e = new Event();

                            e.setByName("doc", getCurrentDoc());

                            e.setByName("goback", new Boolean(true));

                            e.setByName("searchTypeObj", campi[1]);

                            Session.getMngEvent().handleEvent(ManageEvent.VIEW_CART, e);

                            break;

                        case (ManageViewNavigation.duplicatedoc):

                            Hashtable params = new Hashtable();

                            params.put("docbean", getCurrentDoc());
                            params.put("typeDoc", campi[1]);

                            Session.getMngService().doService(ManageService.DUPLICATE_DOC_SERVICE, params);

                            break;

                     
                    }
                }

          
        }

    }

    public DocBean getOldDoc() {
        return oldDoc;
    }

    public void setOldDoc(DocBean oldDoc) {
        this.oldDoc = oldDoc;
    }

    public DocBean getCurrentDoc() {
        return currentDoc;
    }

    public void setCurrentDoc(DocBean currentDoc) {
        this.currentDoc = currentDoc;
    }

    public TableItem getTable() {
        return table;
    }

    public void setTable(TableItem table) {
        this.table = table;
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
     * @return the selectedIndex
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

}
