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
import agile.model.structure.Property;
import agile.model.structure.Structure;
import agile.session.Session;
import java.util.Vector;

/**
 *
 * @author ruego
 */
public class FunctionMenuForm extends MainForm implements Viewable {
    
    
      
 public FunctionMenuForm() {

        super(Locale.get("FunctionMenuForm.title"), false,true,false,null);

       
         setHelpFile("functionmenu");

       
       
    }
 
  public void showView() {


      ManageViewNavigation.getDisplay().setCurrent(menuScreen);

   

     // UiAccess.setCurrentListIndex( ManageViewNavigation.getDisplay(), this.menuScreen, 2 );
     
    }
  
   private  List menuScreen=null;

   private Structure typeObj;
 
   private String selectedObj;


    public FunctionMenuForm prepareView(Structure typeObj,String selectedObj) {

        try {

            setTypeObj(typeObj);
            setSelectedObj(selectedObj);


            //#style menuList
            this.menuScreen = new List(Locale.get("FunctionMenuForm.title")+" " + getSelectedObj() , List.IMPLICIT);

            setTypeObj(typeObj);
            setSelectedObj(selectedObj);

            Vector menuopts = typeObj.getPropertyByKey("menu");

            boolean newopt = false;
            boolean searchopt = false;
            boolean listopt = false;
            boolean workflowopt = false;

            //#debug info
            System.out.println("3-Controllo menu opts");

            if (menuopts != null) {

                Property prop = null;

                long size = menuopts.size();

                for (int j = 0; j < size; j++) {

                    if (menuopts.elementAt(j) == null) {
                        continue;
                    }

                    prop = (Property) menuopts.elementAt(j);

                    if (((String) prop.getValue()).equals("new")) {
                        newopt = true;
                    }
                    if (((String) prop.getValue()).equals("search")) {
                        searchopt = true;
                    }
                    if (((String) prop.getValue()).equals("list")) {
                        listopt = true;
                    }

                    if (((String) prop.getValue()).equals("workflow")) {
                        workflowopt = true;
                    }


                }

            }

            if (newopt) {

                //#style menuListItem
                this.menuScreen.append(Locale.get("mainmenuform.menu.newelement"),null);

            }

            if (searchopt) {

                //#style menuListItem
                this.menuScreen.append(Locale.get("mainmenuform.menu.search"),null);

            }

            if (listopt) {

                //#style menuListItem
                this.menuScreen.append(Locale.get("mainmenuform.menu.list"),null);

            }

            if (workflowopt) {

                //#style menuListItem
                this.menuScreen.append(Locale.get("mainmenuform.menu.workflow"),null);


            }


        } catch (Exception e) {
            //#debug error
            System.out.println("Exception: " + e.getMessage());
        }

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
            
            e = new Event();
            e.setByName("objSelText", getSelectedObj());
            e.setByName("comandoText", command);
            if (getTypeObj().getType() != null) {
                e.setByName("typeObjSelText", getTypeObj().getType());
            }

            Session.getMngEvent().handleEvent(ManageEvent.MAIN_MENU_SELECT, e);
  
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

    /**
     * @return the typeObj
     */
    public Structure getTypeObj() {
        return typeObj;
    }

    /**
     * @param typeObj the typeObj to set
     */
    public void setTypeObj(Structure typeObj) {
        this.typeObj = typeObj;
    }

    /**
     * @return the selectedObj
     */
    public String getSelectedObj() {
        return selectedObj;
    }

    /**
     * @param selectedObj the selectedObj to set
     */
    public void setSelectedObj(String selectedObj) {
        this.selectedObj = selectedObj;
    }
    
    

}
