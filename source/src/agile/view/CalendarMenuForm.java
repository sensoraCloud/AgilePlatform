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
import de.enough.polish.calendar.CalendarItem;

/**
 *
 * @author ruego
 */
public class CalendarMenuForm extends MainForm implements Viewable {    
    
    
    
 public CalendarMenuForm() {

        super(Locale.get("CalendarMenuForm.title"), false,true,false,null);

         setHelpFile("calendar");

      
    }
 
  public void showView() {

      ManageViewNavigation.getDisplay().setCurrent(this);

    
     // UiAccess.setCurrentListIndex( ManageViewNavigation.getDisplay(), this.menuScreen, 2 );
     
    }
  
   private  CalendarItem cal=null;
 
    public CalendarMenuForm prepareView() {

        //#style calendar
        cal = new CalendarItem();

        this.append(cal);

      
        this.addCommand(ManageViewNavigation.backCommand);
        this.addCommand(ManageViewNavigation.home);
        this.addCommand(ManageViewNavigation.healpCommand);
        
         return this;
         
    }

   
    public void commandAction(Command cmd, Displayable disp) {

      if (cmd == ManageViewNavigation.backCommand) {

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
