/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TableItem;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.model.service.ManageService;
import agile.model.structure.AnagObj;
import agile.model.structure.ResponseService;
import agile.model.structure.Structure;
import agile.session.Session;

/**
 *
 * @author ruego
 */
public class AdminDataForm extends MainForm implements Viewable {
    
     private Command sincAllObjs = new Command(Locale.get("command.sincAllObjs"), Command.SCREEN, 0);


     public AdminDataForm() {

        super(Locale.get("AdminDataForm.title"), false,false,false,null);

         setHelpFile("AdminDataForm");
       
    }
 
  public void showView() {

      ManageViewNavigation.getDisplay().setCurrent(this);
     
    }
  
 
    public void makeTableView() throws Exception {

        //loop on sustem obj
        if (Session.getMngIndex().getHashCountRealNumObjs()!= null) {

            
            Enumeration en = Session.getListStructureObj().keys();

            String typeObj = null;
            Integer qta = null;

            //#style defaultTable
            TableItem newTab = new TableItem(Session.getListStructureObj().size(),Session.getListStructureObj().size()+1);
           
            int i = 0;
            
            Command cmd=null;

            boolean insert=false;
            
            while (en.hasMoreElements()) {

                typeObj = (String) en.nextElement();

                //AnagObj
                if (((Structure) Session.getListStructureObj().get(typeObj)).getType().equals("AnagObj")) {


                    if (((AnagObj) Session.getListStructureObj().get(typeObj)).isChild()) {
                        insert = false;
                    } else {
                        insert = true;
                    }

                } else //DocObj
                if (((Structure) Session.getListStructureObj().get(typeObj)).getType().equals("DocObj")) {

                    insert = true;
                }

                if (insert) {

                    qta = (Integer) Session.getMngIndex().getHashCountRealNumObjs().get(typeObj);

                    if (qta == null) {
                        qta = new Integer(0);                    //compile header
                    }
                    if (i == 0) {
                        //#style heading
                        newTab.set(0, 0, "Oggetto");
                        //#style heading
                        newTab.set(1, 0, "Qta");
                    }

                    //#style leftedCellnoExpand		
                    newTab.set(0, i + 1, typeObj);

                    //#style leftedCell
                    newTab.set(1, i + 1, String.valueOf(qta.intValue()));

                    //add delete command
                    //cmd = new Command(Locale.get("command.delete") + " " + typeObj, Command.ITEM, i);

                    //this.addCommand(cmd);

                    i++;


                }


            }

            if (newTab.size()>0)
                newTab.setSelectedCell(0, 0);

            newTab.setSelectionMode(TableItem.SELECTION_MODE_ROW | TableItem.SELECTION_MODE_COLUMN);

            //add delete all command
            cmd=new Command(Locale.get("command.deleteall"), Command.ITEM, i++);

            this.addCommand(cmd);
            
            this.append(newTab);


        }else{
        
            //#style mex
            StringItem mex = new StringItem(null, Locale.get("message.noobjs"));

            this.append(mex);


        }
        
       

        try {

            String avaibleMemory = "";

            ResponseService resp = Session.getMngService().doService(ManageService.GET_FREE_MEMORY_SERVICE, null);

            if (resp.getStatus()) {

                avaibleMemory = (String) resp.getResponseObj();
            }                                

            //#style mex
            StringItem mex = new StringItem(null, "Free memory: " + avaibleMemory + " Bytes");

            this.append(mex);          
          

        } catch (Exception e2) {
            //#debug error
            System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());

        }


     


    }

   
   
   public AdminDataForm prepareView(){
        
       try {
           
           this.deleteAll();
           this.removeAllCommands();
           this.removeCommand(ManageViewNavigation.backCommand);
           this.removeCommand(ManageViewNavigation.home);
           this.removeCommand(ManageViewNavigation.healpCommand);
           this.removeCommand(sincAllObjs);


           this.addCommand(ManageViewNavigation.backCommand);
           this.addCommand(ManageViewNavigation.home);
           this.addCommand(ManageViewNavigation.healpCommand);
           this.addCommand(sincAllObjs);


                      
           this.makeTableView();
           
           
       } catch (Exception e2) {
           //#debug error
           System.out.println(e2.getMessage() == null ? "Null" : e2.getMessage());

       }
       
        return this;
         
   }
    
 
    public void commandAction(Command cmd, Displayable disp) {

      if (cmd == ManageViewNavigation.healpCommand) {
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);
             
        }else if (cmd == ManageViewNavigation.backCommand) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);


        } else if (cmd == ManageViewNavigation.home) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        }else if (cmd == sincAllObjs) {

           //sincronizza tutti gli oggetti

        }else{
        
          String command=cmd.getLabel();
          
          if (command.equals(Locale.get("command.deleteall"))){
          
            Session.getMngService().doService(ManageService.DELETE_ALL_OBJS_SERVICE, null);
               
          
//          }else{
//           
//              String typeObj = TextUtil.split(command, ' ')[1];
//
//              Hashtable param = new Hashtable(1);
//              param.put("typeObj", typeObj);
//
//              Session.getMngService().doService(ManageService.DELETE_TYPEOBJ_SERVICE, param);
//
//          
          }
          
        
        } 

    }         
    
    

}
