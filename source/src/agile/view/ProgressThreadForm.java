/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;


//#if subMenuActive
//# import de.enough.polish.ui.UiAccess;
//#endif

 //#ifdef polish.debugEnabled
//# import de.enough.polish.util.Debug;
    //#endif

import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.UiAccess;
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
public class ProgressThreadForm extends MainForm implements Viewable {

    
    
    //#ifdef polish.debugEnabled
//#     private Command logCmd = new Command("Show log", Command.SCREEN, 10);
    //#endif
    
    
    
    private Gauge gauge = null;
    private int threadID=0;
    private String styleGauge=null;
    private  StringItem  strItm =null;
    private boolean viewed;
    
    
    //#if subMenuActive
    //# public static Command controlCommand =   new Command(Locale.get("command.control"),Command.SCREEN, 0);
    //#endif
    
     
    public ProgressThreadForm() {

        super(null, true, true, true, null);

        setHelpFile("progress_it");

        //#debug info
        System.out.println("CREATE PROGRESS!!");

        setViewed(false);


    }
    
    public void showView() {

        ManageViewNavigation.getDisplay().setCurrent(this);
        setViewed(true);
     
    }
    
    
   
    
    public void setTextMex(String mex){
    
        if (getStrItm()!=null)
         getStrItm().setText(mex);
      
    }
    
    //this prepareview metod is void because acces him with static memory in ManageViewNavigation
    public  void prepareView(int threadID,String styleGauge) {
        
        try {
        
            if ((getGauge() == null)&&(styleGauge!=null)) {                
               
                setStyleGauge(styleGauge);

                if (styleGauge.equals("gaugeItemPercentade")) {

                    //#style gaugeItemPercentade
                    this.gauge = new Gauge(null, true, 100, 0);
                    gauge.setLayout(Gauge.LAYOUT_CENTER | Gauge.LAYOUT_VCENTER);
                    this.append(this.gauge);

                }else
                    if (styleGauge.equals("gaugeItemIndefinite")) {

                    //#style gaugeItemIndefinite
                    this.gauge = new Gauge( null, true, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING );
                    gauge.setLayout(Gauge.LAYOUT_CENTER | Gauge.LAYOUT_VCENTER);
                    this.append(this.gauge);

                }
              
                 //#style mexGauge
                 strItm= new StringItem(null, null) ;                
                 this.append(strItm);

                 this.setTextMex(null);
                 
            //#ifdef polish.debugEnabled
            //#         this.addCommand( this.logCmd );         
            //#endif
             
                 
            }
            
            
            this.setTitle(Session.getMngThread().getTitle(threadID));
            this.setThreadID(threadID);
            
            this.removeCommand(ManageViewNavigation.pauseCommand);
            this.removeCommand(ManageViewNavigation.cancelCommand);
            this.removeCommand(ManageViewNavigation.startCommand);
            this.removeCommand(ManageViewNavigation.backCommand);
            this.removeCommand(ManageViewNavigation.healpCommand);
            this.removeCommand(ManageViewNavigation.home);
            
             //#ifdef polish.debugEnabled
            //#         this.removeCommand( this.logCmd );         
            //#endif
            
             UiAccess.removeAllCommands(this);
            
             //if (failed) -1; if (cancelled) 4; if (paused) return 2; 
            //if (percentageSnapshot >= 100) return 5; if (thread == null) return 0; 
            //else  return 1;  
                        
            //if progress
            if (Session.getMngThread().getStatus(threadID) == 1) {

                 
                //#if subMenuActive
                //# this.addCommand(controlCommand);     
                //# UiAccess.addSubCommand(ManageViewNavigation.pauseCommand, controlCommand, this);
                //# UiAccess.addSubCommand(ManageViewNavigation.cancelCommand, controlCommand, this);        
                //#else
this.addCommand(ManageViewNavigation.pauseCommand);
this.addCommand(ManageViewNavigation.cancelCommand);
                //#endif

                
                this.addCommand(ManageViewNavigation.healpCommand);
 
                if (getStyleGauge().equals("gaugeItemPercentade"))
                    getGauge().setValue(Session.getMngThread().getPercentadeThread(threadID));
                getGauge().setLabel(Session.getMngThread().getPercentageText(threadID));


            } else //if paused
            if (Session.getMngThread().getStatus(threadID) == 2) {
                
             
                 //#if subMenuActive
                //# this.addCommand(controlCommand);     
                //#  UiAccess.addSubCommand(ManageViewNavigation.startCommand, controlCommand, this);
                //# UiAccess.addSubCommand(ManageViewNavigation.cancelCommand, controlCommand, this);
                //#else
this.addCommand(ManageViewNavigation.startCommand);
this.addCommand(ManageViewNavigation.cancelCommand);
                //#endif
   

                this.addCommand(ManageViewNavigation.healpCommand);

                if (getStyleGauge().equals("gaugeItemPercentade"))
                    getGauge().setValue(Session.getMngThread().getPercentadeThread(threadID));
                getGauge().setLabel(Session.getMngThread().getPercentageText(threadID));


            } else //if completed,failed,null,cancelled
            if ((Session.getMngThread().getStatus(threadID) == 5) || (Session.getMngThread().getStatus(threadID) == 0) || (Session.getMngThread().getStatus(threadID) == -1)||(Session.getMngThread().getStatus(threadID) == 4)) {

                  
                this.addCommand(ManageViewNavigation.backCommand);
                this.addCommand(ManageViewNavigation.home);
                this.addCommand(ManageViewNavigation.healpCommand);

                if (getStyleGauge().equals("gaugeItemPercentade"))
                    getGauge().setValue(Session.getMngThread().getPercentadeThread(threadID));
                getGauge().setLabel(Session.getMngThread().getPercentageText(threadID));


            }

             //if cancelled or failed
            if ((Session.getMngThread().getStatus(threadID) == 4)||(Session.getMngThread().getStatus(threadID) == -1))
                  this.addCommand(ManageViewNavigation.retryCommand); 
             
            //#ifdef polish.debugEnabled
            //#         this.addCommand( this.logCmd );         
            //#endif
         
             
       
        } catch (Exception e2) {
            //#debug error
            System.out.println(e2.getMessage() == null ? "null" : e2.getMessage());
        }

         //return this;
    }

   
    public void commandAction(Command cmd, Displayable disp) {

        if (cmd == ManageViewNavigation.backCommand) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
            
            Session.getMngThread().destroyThread(getThreadID());


        }  else if (cmd == ManageViewNavigation.cancelCommand) {

            Session.getMngThread().cancelThread(getThreadID());
            this.prepareView(getThreadID(),null);

        } else if (cmd == ManageViewNavigation.pauseCommand) {

            Session.getMngThread().pauseThread(getThreadID());
            this.prepareView(getThreadID(),null);

        }else if (cmd == ManageViewNavigation.startCommand) {

            Session.getMngThread().restartThread(getThreadID());
            this.prepareView(getThreadID(),null);          
            
        }else if (cmd == ManageViewNavigation.retryCommand) {

            //start again thread
            Session.getMngThread().startAgainThread(getThreadID());
  
            
        }else if (cmd == ManageViewNavigation.home) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);
            
            Session.getMngThread().destroyThread(getThreadID());

        } else if (cmd == ManageViewNavigation.healpCommand) {

            e = new Event();
            e.setByName("helpFile",getHelpFile());
            Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);


        }
        //#ifdef polish.debugEnabled         
        //# else if (cmd == this.logCmd) {
        //#  Debug.showLog(ManageViewNavigation.getDisplay());             
        //# }
        //#endif
        
    }
    
    
    public void itemStateChanged(Item item) {
        
            
    }

    
     public  void commandAction(Command cmd, Item item){
     
     }

    public int getThreadID() {
        return threadID;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    public Gauge getGauge() {
        return gauge;
    }

    public String getStyleGauge() {
        return styleGauge;
    }

    public void setStyleGauge(String styleGauge) {
        this.styleGauge = styleGauge;
    }

    public StringItem getStrItm() {
        return strItm;
    }

    public void setStrItm(StringItem strItm) {
        this.strItm = strItm;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    

   

}

   
    
  
