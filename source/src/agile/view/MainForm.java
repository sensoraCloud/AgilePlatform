/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

import de.enough.polish.ui.Command;
import javax.microedition.lcdui.Ticker;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.ItemStateListener;
import de.enough.polish.ui.CommandListener;
import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.session.Session;


/**
 *
 * @author ruego
 */
public class MainForm extends de.enough.polish.ui.Form implements CommandListener,ItemCommandListener,ItemStateListener,Viewable {
       
    public static final int TYPEFORM_VIEW=1;
    public static final int TYPEFORM_NEW=2;
    public static final int TYPEFORM_UPDATE=3; 
    
    public static final int TYPEFORM_DOC_VIEW_HEAD=4; 
    public static final int TYPEFORM_DOC_UPDATE_HEAD=5; 
    public static final int TYPEFORM_DOC_NEW_ROW=6; 
    public static final int TYPEFORM_DOC_UPDATE_ROW=7; 
    
    public static final int TYPEFORM_SEARCH_STANDARD=8; 
    public static final int TYPEFORM_SEARCH_NEWDOC=9; 
    public static final int TYPEFORM_SEARCH_CART=10; 
    public static final int TYPEFORM_SEARCH_WORKFLOW=11; 
    
    public static final int TYPEFORM_CREATENEWDOC=12; 
    public static final int TYPEFORM_NEW_ROW_CART=13; 
    
    public static final int TYPEFORM_INIT_LOGIN=14; 
    public static final int TYPEFORM_UPDATE_LOGIN=15; 
    
    
    private String helpFile=null;
    private int typeFORM;
    public Event e=null; 
    
     /**
     * Form principale
     * @param title Titolo del form
     * @param backcommand True se vuoi aggiungere il comando back
     * @param helpcommand True se vuoi aggiungere il comando help
     * @param homecommand True se vuoi aggiungere il comando home    
     */
     public MainForm(String title,boolean backcommand,boolean helpcommand,boolean homecommand){
       
         //#style mailForm
        super(title);
        
        //setto il tiker del FORM chiamante
        //#style mailTicker
        Ticker tik=new Ticker(ManageViewNavigation.getTickerMessage()); 
        
        this.setTicker(tik);
     
      
        if (backcommand)
             this.addCommand(ManageViewNavigation.backCommand);
        if (helpcommand)
            this.addCommand(ManageViewNavigation.healpCommand);
        if (homecommand)
           this.addCommand(ManageViewNavigation.home);
        
        this.setCommandListener(this);
        this.setItemStateListener(this);
       
    }
     
      /**
     * Form principale
     * @param title Titolo del form
     * @param backcommand True se vuoi aggiungere il comando back
     * @param helpcommand True se vuoi aggiungere il comando help
     * @param homecommand True se vuoi aggiungere il comando home 
     * @param tickerMessage String Messaggio da visualizzare come ticker    
     */
     public MainForm(String title,boolean backcommand,boolean healpcommand,boolean homecommand,String tickerMessage){
       
            
         //#style mailForm
        super(title);
        
        //setto il ticker
        Ticker tik;        
        if (tickerMessage!=null){
        //#style mailTicker
         tik=new Ticker(tickerMessage);
          super.setTicker(tik);
        }
        else{
         //   //#style mailTicker
        // tik=new Ticker("");
        }
       
         //super.setTicker(tik);
        
         if (backcommand)
             this.addCommand(ManageViewNavigation.backCommand);
        if (healpcommand)
            this.addCommand(ManageViewNavigation.healpCommand);
        if (homecommand)
           this.addCommand(ManageViewNavigation.home);
        
        this.setCommandListener(this);
        this.setItemStateListener(this);
        
              
    }

     //permit to view different objet than (this) Form Example: some View must viewable TabbedPanel that it can't append to form
     public void showView(){};
          
     public void commandAction(Command cmd, Item item){};
     
     public void itemStateChanged(Item item){
        //#debug info
         System.out.println("Call Item state Change of Main Form");

     } 
      public void itemStateChanged(){
        //#debug info
         System.out.println("Call Item state Change of Main Form");
     } 

     
     public void commandAction(Command cmd, Displayable disp) { 
         
        if (cmd == ManageViewNavigation.backCommand) {
            
            e=new Event();            
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
            
            
        }else if (cmd == ManageViewNavigation.home) {

           e=new Event();            
           Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);
            
        }else if (cmd == ManageViewNavigation.healpCommand) {
             e = new Event();
             e.setByName("helpFile",getHelpFile());
             Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);
        } 
         
     }
    
     
     
     
      public void setTicker(String mex){
     
          this.setTicker(mex);
              
     }
     
       public String getTickerMex() {

        return this.getTicker()!=null?this.getTicker().toString():null;

    }

    public int getTypeFORM() {
        return typeFORM;
    }

    public void setTypeFORM(int typeFORM) {
        this.typeFORM = typeFORM;
    }

    public String getHelpFile() {
        return helpFile;
    }

    public void setHelpFile(String helpFile) {
        this.helpFile = helpFile;
    }
     
             
     
       
    
}
