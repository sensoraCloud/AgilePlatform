/*
 * Created on 30-Jan-2006 at 03:27:38.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package agile.midlet;


import de.enough.polish.ui.splash.ApplicationInitializer;
import de.enough.polish.ui.splash.InitializerSplashScreen;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import agile.control.ManageViewNavigation;
import agile.session.Session;
import agile.session.User;
import agile.view.LoginForm;
import agile.view.MainForm;
import agile.view.MainMenuForm;
import agile.view.Viewable;


//import javax.wireless.messaging.BinaryMessage;
//import javax.wireless.messaging.Message;
//import javax.wireless.messaging.MessageConnection;
//import javax.wireless.messaging.TextMessage;

/**
 * <p>Example for using the TreeItem.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AgileMidlet
        extends MIDlet
	implements ApplicationInitializer 

    
     {
    

    private InitializerSplashScreen splashScreen;
    
    /**
     * Creates a new midlet.
     */
    public AgileMidlet() {
        super();
        
        //#debug info
        System.out.println("agile midlet created");
    }

    public Display getDisplay() {
        return Display.getDisplay(this);
    }

        
    public Displayable initApp() {        
       
        //load structure from config        
        Session.getMngConfig().loadInSessionStructureFromFileConfig();
        Session.setMngConfig(null);
        
        //create hashIndex
        if (!Session.getMngIndex().createHashIndex()){
            
             // ManageViewNavigation.showAlert("Info", "Non è stato possibile creare gli indici!", null, AlertType.INFO);
        
        }
        
        
        ManageViewNavigation.setMainMenu(new MainMenuForm());
        
        User us=new User();
        
        Session.setActiveUser(us);
        
        if (!us.isset()){
            
            ManageViewNavigation.goNextNoReturn((Viewable)  (new LoginForm()).prepareView(MainForm.TYPEFORM_INIT_LOGIN));       
      
            return ((Displayable)ManageViewNavigation.getCurrentDisplayed());
            
        }else{
        
             ManageViewNavigation.goNextNoReturn( (Viewable)  ((MainMenuForm)ManageViewNavigation.getMainMenu())  );

           
             return ((Displayable)ManageViewNavigation.getDisplay().getCurrent());
        
        }
            
    
    }
    
    
    
    protected void startApp() throws MIDletStateChangeException {

        //#debug info
        System.out.println("start app");  
        
//        try{
//            sconn = (MessageConnection) Connector.open("sms://:" + smsPort);
//        } catch (IOException ex) {
//              //#debug info
//                System.out.println("Exception- open SERVER SMSo: "+ex.getMessage());
//        }
//        
//         new Thread(new SMSServer()).start();
        
        
          // Set main controller for UI
        ManageViewNavigation.setMidlet(this);
        ManageViewNavigation.setDisplay(this.getDisplay());
       
        Image splashImage = null;
        try {
            splashImage = Image.createImage("/logo.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int backgroundColor = 0xebe8e0;

        this.splashScreen = new InitializerSplashScreen(
                getDisplay(),
                splashImage,
                backgroundColor,
                null, //Locale.get("message.loadconfig") no message, so we proceed to the initial screen as soon as possible
                0, // since we have no message, there's no need to define a message color
                this);
        
        ManageViewNavigation.getDisplay().setCurrent(this.splashScreen);        

    }
    
//    private MessageConnection sconn;
//    public boolean done=false; 
//    private String smsPort="2828";
//    
//    class SMSServer implements Runnable {
//        // http://10.100.10.5:13013/cgi-bin/invia?username=smsitaly&password=smsitaly&to=%2B393477657857&from=%2B393356359515&text=ciao&udh=%06%05%04%41%18%00%00
//        public void run() {
//            
//            try {
//               
//                //#debug info
//                System.out.println("Server SMS attivato");
//                
//                while (!done) {
//                    Message msg = sconn.receive();
//                    String msgText=null;
//                    if (msg instanceof TextMessage) {
//                        TextMessage tmsg = (TextMessage) msg;
//                        msgText = tmsg.getPayloadText();
//                        
//                    } else if (msg instanceof BinaryMessage){
//                        BinaryMessage bmsg=(BinaryMessage) msg;
//                        msgText = new String(bmsg.getPayloadData());
//                    } else {
//                        msgText=new String("Not readable");
//                        throw new Exception("Received is:"+msg.getClass().getName());
//                    }
//                    
//                     //#debug info
//                System.out.println("SMS ricevuto: "+msgText);
//
//                     
//                    Alert alert = new Alert("Received", msgText,
//                            null, AlertType.INFO);
//                    alert.setTimeout(Alert.FOREVER);
//                    getDisplay().setCurrent( alert );
//                }
//                
//            } catch (Exception e) {                
//                     //#debug info
//                System.out.println("Server SMS chiuso: "+e.getMessage());
//               
//            }
//        }
//    }
//    
    
      

    /* (non-Javadoc)
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    protected void pauseApp() {
        // just keep on pausing
    }

    /* (non-Javadoc)
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        // nothing to clean up
    }
}

