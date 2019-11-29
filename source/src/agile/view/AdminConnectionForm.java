/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.model.service.ManageService;
import agile.model.structure.ResponseService;
import agile.session.Session;

/**
 *
 * @author ruego
 */
public class AdminConnectionForm extends MainForm implements Viewable {
    
 public AdminConnectionForm() {

        super(Locale.get("AdminConnectionForm.title"), true,true,true,null);

         setHelpFile("AdminConnectionForm");
       
    }
 
  public void showView() {

      ManageViewNavigation.getDisplay().setCurrent(this);
     
    }
  
   private  TextField connectionURL =null;
  
 
   public AdminConnectionForm  prepareView(){
        
       String url="http://";
       
       ResponseService resp=Session.getMngService().doService(ManageService.GET_URL_CONNECTION_SERVICE,null) ;
      
      if (resp.getStatus()){
      
          url=(String)resp.getResponseObj();
      } 
      
       
      //#style simpleinput
      connectionURL = new TextField(Locale.get("AdminConnectionForm.connectionURL") + " :",url ,150, TextField.ANY);
      UiAccess.setInputMode(connectionURL,UiAccess.MODE_NATIVE);      
           
      this.append(connectionURL);
      this.addCommand(ManageViewNavigation.saveCommand);       
     
      return this;
         
   }
    
    public boolean write(String url){
        try{
            RecordStore.deleteRecordStore("admin_connection");
        } catch(Exception e){          
        }
        
        try {
           
            //#debug info
            System.out.println("Scrittura url");
           
            RecordStore rs = RecordStore.openRecordStore("admin_connection", true);

            try {
                
                rs.addRecord(url.getBytes(), 0, url.length());
                
            } catch (RecordStoreNotOpenException ex) {
                //#debug error
                System.out.println(ex.getMessage());
            } catch (RecordStoreException ex) {
                //#debug error
                System.out.println(ex.getMessage());
            }

            rs.closeRecordStore();

        } catch (Exception ex) {
            
            //#debug error
            System.out.println("Error write url: " + ex.getMessage());
          
            return false;
        }
        
        return true;
    }
    
   
   
    public void commandAction(Command cmd, Displayable disp) {

       if (cmd == ManageViewNavigation.saveCommand) {
            
           if ((connectionURL.getString()!=null)&&(!connectionURL.getString().equals(""))){
            
                boolean result= this.write(connectionURL.getString());
                         
                if (!result){
                
                   ManageViewNavigation.showAlert("Info.", Locale.get("message.errwrite"), null, AlertType.INFO);
                    
                }else{
                
                     e = new Event();
                     Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                   
                }
            
            }else {
            
                 ManageViewNavigation.showAlert("Info.", Locale.get("message.errallfields"), null, AlertType.INFO);

            }         
           
        }else if (cmd == ManageViewNavigation.healpCommand) {
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);
             
        }else if (cmd == ManageViewNavigation.backCommand) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);


        } else if (cmd == ManageViewNavigation.home) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        } 

    }         
     
}