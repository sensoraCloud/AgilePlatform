/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

import de.enough.polish.ui.List;
import de.enough.polish.util.Locale;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.session.Session;

/**
 *
 * @author ruego
 */
public class ConfigMenuForm extends MainForm implements Viewable {
    
    
       
 public ConfigMenuForm() {

        super(Locale.get("ConfigMenu.title"), false,true,false,null);

         setHelpFile("configmenu");

      
    }
 
  public void showView() {

      ManageViewNavigation.getDisplay().setCurrent(menuScreen);

    
     // UiAccess.setCurrentListIndex( ManageViewNavigation.getDisplay(), this.menuScreen, 2 );
     
    }
  
   private  List menuScreen=null; 
 
    public ConfigMenuForm prepareView() {

      
        //#style menuList
        this.menuScreen = new List(Locale.get("ConfigMenu.title"), List.IMPLICIT);
        
        //#style menuListItem
        this.menuScreen.append(Locale.get("command.changelogin"), null);
        //#style menuListItem
        this.menuScreen.append(Locale.get("command.adminconnection"), null);
         //#style menuListItem
        this.menuScreen.append(Locale.get("command.admindata"), null);


     
        this.menuScreen.setCommandListener(this);
        this.menuScreen.setSelectCommand(ManageViewNavigation.selectCommand);
        this.menuScreen.addCommand(ManageViewNavigation.backCommand);
        this.menuScreen.addCommand(ManageViewNavigation.home);
        this.menuScreen.addCommand(ManageViewNavigation.healpCommand);

        
         return this;
         
    }

   
    public void commandAction(Command cmd, Displayable disp) {

        if (cmd == ManageViewNavigation.selectCommand) {
            
            String command=menuScreen.getString(menuScreen.getSelectedIndex());
            
            if (command.equals(Locale.get("command.changelogin"))){
            
                 e = new Event();
                 Session.getMngEvent().handleEvent(ManageEvent.VIEW_UPDATE_LOGIN_EVENT, e);
            
            }else if (command.equals(Locale.get("command.adminconnection"))){
            
                 e = new Event();
                 Session.getMngEvent().handleEvent(ManageEvent.VIEW_ADMIN_CONNECTION_EVENT, e);
            
            }else if (command.equals(Locale.get("command.admindata"))){
            
                 e = new Event();
                 Session.getMngEvent().handleEvent(ManageEvent.VIEW_ADMIN_DATA_EVENT, e);
            
            }
                     
        } else if (cmd == ManageViewNavigation.backCommand) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);


        } else if (cmd == ManageViewNavigation.home) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        } else if (cmd == ManageViewNavigation.healpCommand) {

            e = new Event();
            e.setByName("helpFile", getHelpFile());
            Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);


        }

    }
    
    

}
