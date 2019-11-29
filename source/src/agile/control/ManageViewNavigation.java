/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.control;



import de.enough.polish.ui.Alert;
import de.enough.polish.util.Locale;
import java.util.Stack;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDlet;
import agile.view.ProgressThreadForm;
import agile.view.Viewable;




/**
 *
 * @author ruego
 */

public class ManageViewNavigation {
    
    //command code
    public final static int saveandcreatedoc=1;
    public final static int finddoc=2;
    public final static int createdoc=3;
    public final static int orderby=4;
    public final static int addbeantodoc=5;
    public final static int duplicatedoc=6;
    public final static int filterBy=7;
    public final static int findlink=8;
    
    public static Command backCommand =   new Command(Locale.get("command.back"),Command.BACK, 2);
    public static Command selectCommand = new Command(Locale.get("command.select"), Command.ITEM , 1);
    public static Command cancelCommand = new Command(Locale.get("command.cancel"), Command.BACK, 9);
    public static Command confirmCommand = new Command(Locale.get("command.confirm"), Command.BACK, 8);
    public static Command pauseCommand = new Command(Locale.get("command.stop"), Command.BACK, 8);
    public static Command startCommand = new Command(Locale.get("command.start"), Command.BACK, 7);
    public static Command saveCommand =   new Command(Locale.get("command.save"), Command.BACK , 0);
    public static Command healpCommand =   new Command(Locale.get("command.help"),Command.HELP, 100);
    public static Command exit = new Command(Locale.get("command.exit"), Command.SCREEN, 10);
    public static Command home = new Command(Locale.get("command.home"), Command.SCREEN, 10);
    public static Command viewCommand = new Command(Locale.get("command.view"), Command.ITEM, 9);
    public static Command updateCommand = new Command(Locale.get("command.update"), Command.ITEM, 10);
    public static Command retryCommand =   new Command(Locale.get("command.retry"),Command.SCREEN, 7);
      
    private static MIDlet midlet;
    private static Display display;
    private static Viewable mainMenu;
    private static Stack pila=new Stack();
    public static Ticker ticker;     
    
    
    private static Viewable currentDisplayed=null;
 
    private static ProgressThreadForm prgsThread=null;
    
    private long totalByteLoading;
   
    
    /** Creates a new instance of ManageFormNav */
    public static void ManageFormNav() {

    }    
           
    
    public static Display getDisplay() {
        return display;
    }
 
    public static Viewable getBackView(){
    
        Viewable backDispl=((Viewable)pila.pop());       
        
        pila.push(backDispl);
        
        return backDispl;
    
    }
    
  
    
    public synchronized static Viewable goBack(){
        
        //tolgo dalla memeroria il displayed corrente e metto quello prima
        //Displayable currentDisp=(Displayable)ManageViewNavigation.getDisplay().getCurrent();           

        Viewable currentDisp=getCurrentDisplayed();
       
        Viewable backDispl=((Viewable)pila.pop());        
        
              
        currentDisp=null;
       
        backDispl.showView();
        
        setCurrentDisplayed(backDispl);
       
        System.gc();
        
        return backDispl;
        
    }
    
    
    public static void deletePila() {

       Displayable currentDisp=null;
        
        while (!pila.empty()) {

            currentDisp = ((Displayable) pila.pop());
             
            currentDisp=null;       

        }
       
       System.gc();


    }
    
    public synchronized  static void goHome(){    
        
             
        ManageViewNavigation.deletePila();
       
        ManageViewNavigation.getMainMenu().showView();
        
        setCurrentDisplayed(ManageViewNavigation.getMainMenu());
        
        System.gc();
        
    }
    
    
    public synchronized  static void goNext(Viewable d){
                 
        pila.push(getCurrentDisplayed());        
                
        d.showView();
         
        setCurrentDisplayed(d);        
    
        
    }


    //metto come back menu il menu principale
    public  synchronized static void goNextsetMainMenuBack(Viewable d){
        
        //delete all pila
        ManageViewNavigation.deletePila();
        
        pila.push(getMainMenu());        
                
        d.showView();
         
        setCurrentDisplayed(d);        
    
        
    }


    //non metto il menu corrente come back quindi dal next facendo back non torna in questo corrente
    public  synchronized static void goNextNoReturn(Viewable d) {

        Viewable currentDisp = getCurrentDisplayed();    
        
        currentDisp=null;
       
        d.showView();

        setCurrentDisplayed(d);

    }

     
    public  synchronized static Alert showAlert(String title,String message,Image alertImage, AlertType alertType) {
        
         //#style mailAlertSimple
        Alert alert = new Alert(title,message, alertImage, alertType);

       // Displayable currentDisp=(Displayable)ManageViewNavigation.getDisplay().getCurrent(); 
//        getDisplay().setCurrent(alert, currentDisp);
        getDisplay().setCurrent(alert);
        
//        getDisplay().setCurrent(currentDisp);
              
        return alert;
  
        
    }    
    
       
    public static javax.microedition.lcdui.Alert getAlert(String title, String message, Image alertImage, javax.microedition.lcdui.AlertType alertType,Integer timeout) {
       
        javax.microedition.lcdui.Alert alert = new javax.microedition.lcdui.Alert(title, message, alertImage, alertType);
        if (timeout!=null)
            alert.setTimeout(timeout.intValue());
        
        return alert;

    }

    public static String getTickerMessage() {
 
        if ((getCurrentDisplayed()!=null)&&(getCurrentDisplayed().getTickerMex() != null)) {
            return getCurrentDisplayed().getTickerMex();
        } else {
            return "";
        }
    }

    public static void setTickerMessage(String aTickerMessage) {
        if (getCurrentDisplayed()!=null)
          getCurrentDisplayed().setTicker(aTickerMessage);
    }

    public static MIDlet getMidlet() {
        return midlet;
    }

    public static void setMidlet(MIDlet aMidlet) {
        midlet = aMidlet;
    }

    public static Viewable getMainMenu() {
        return mainMenu;
    }

    public static void setMainMenu(Viewable aMainMenu) {
        mainMenu = aMainMenu;
    }

    public static void setDisplay(Display aDisplay) {
        display = aDisplay;
    }
      

    public static ProgressThreadForm getPrgsThread() {
         if (prgsThread==null)
            setPrgsThread(new ProgressThreadForm());
        return prgsThread;
    }


    public static void setPrgsThread(ProgressThreadForm aPrgsThread) {
        prgsThread = aPrgsThread;
    }

   
    
    public static Viewable getCurrentDisplayed() {
        return currentDisplayed;
    }

    public static void setCurrentDisplayed(Viewable aCurrentDisplayed) {
        currentDisplayed = aCurrentDisplayed;
    }

   
    
}
