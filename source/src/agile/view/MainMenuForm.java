/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile.view;

//#ifdef polish.debugEnabled
//# import de.enough.polish.util.Debug;
//#endif
import de.enough.polish.util.Locale;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.PushRegistry;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;

import javax.microedition.lcdui.Image;
//import javax.wireless.messaging.BinaryMessage;
//import javax.wireless.messaging.MessageConnection;
import agile.session.Session;

import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.model.service.ManageService;
import agile.model.structure.Structure;
import agile.model.thread.ManageThread;
import de.enough.polish.ui.List;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.ScreenStateListener;
import de.enough.polish.ui.TabListener;
import de.enough.polish.ui.TabbedFormListener;
import de.enough.polish.ui.TabbedPane;
import javax.microedition.lcdui.Ticker;

/**
 *
 * @author ruego
 */
public class MainMenuForm extends MainForm implements Viewable, TabListener, TabbedFormListener,ScreenStateListener {

    private static final Command collapseAll = new Command(Locale.get("command.collapseAll"), Command.BACK, 0);
    private static final Command loadData = new Command("Carica Dati", Command.SCREEN, 1);
    private static final Command forceLoadCli = new Command("Carica Clienti", Command.SCREEN, 7);
    private static final Command forceLoadProd = new Command("Carica Prodotti", Command.SCREEN, 8);
    private static final Command forceLoadDoc = new Command("Carica Ordini", Command.SCREEN, 9);
    private static final Command forceLoadList = new Command("Carica Listini", Command.SCREEN, 10);
    private static final Command tryConnection = new Command("Prova Connessione", Command.SCREEN, 18);
    private static final Command sendMex = new Command("Invia Messaggio", Command.SCREEN, 20);
    private static final Command register = new Command("Registra", Command.SCREEN, 20);
  

    private TabbedPane tabbedPane;
    private List menuAnag;
    private List menuDoc;
    private List menuSet;

    private boolean startup=true;

    int sizeList[]=new int[3];

    private  boolean touchScreen=false;

    private boolean activate=false;

    private int oldposition=0;

    public MainMenuForm() {

        super(Locale.get("mainmenuform.title"), false, true, false, Locale.get("mainmenuform.ticker"));

        setHelpFile("main_menu");

        this.prepareView();

        startup=false;

        
    }

    public void showView() {

        ManageViewNavigation.getDisplay().setCurrent(getTabbedPane());

//        getTabbedPane().setFocus(0);
//        getTabbedPane().setCurrentTab(0);


    }


     public void setTicker(String mex){

           //#style mailTicker
          Ticker tik = new Ticker(mex);

          getTabbedPane().setTicker(tik);

     }


      public String getTickerMex(){

         Ticker tik = getTabbedPane().getTicker();

         return  tik!=null?tik.getString():null;

     }

    private MainMenuForm prepareView() {

        try {


            //#if touchScreenDevice
            //#  touchScreen=true;
            //#else
            //touchScreen=false;
            //#endif

            //#style tabbedPane
            this.tabbedPane=new TabbedPane(null);
            this.getTabbedPane().addTabListener(this);
            this.getTabbedPane().setTabbedFormListener(this);
            //this.getTabbedPane().setCommandListener(this);

            //init size menu
            sizeList[0] = 0;
            sizeList[1] = 0;
            sizeList[2] = 0;

            
            //create menu anagobj and docobj

            //create List AnagObj

            //#style mainScreen
            //this.menuAnag = new List(null, List.IMPLICIT);
            this.menuAnag = new List(null, List.IMPLICIT);//Locale.get("mainmenuform.menu.anagrafic")

            getMenuAnag().setCommandListener(this);

            getMenuAnag().setScreenStateListener(this);

          
            //#style tabIcon
            getTabbedPane().addTab((de.enough.polish.ui.Displayable)menuAnag, Image.createImage("/anagrafic1.png"), Locale.get("mainmenuform.menu.anagrafic"));


             //create List DocObj

            //#style mainScreen
            //this.menuDoc = new List(null, List.IMPLICIT);
            this.menuDoc = new List(null, List.IMPLICIT);//Locale.get("mainmenuform.menu.document")

            getMenuDoc().setCommandListener(this);

            getMenuDoc().setScreenStateListener(this);

            //#style tabIcon
            getTabbedPane().addTab((de.enough.polish.ui.Displayable)menuDoc, Image.createImage("/document1.png"), Locale.get("mainmenuform.menu.document"));


            //Insert menu object

            Enumeration enumeration = Session.getListStructureObj().keys();


            //#debug info
            System.out.println("1-Carico Structure");

            while (enumeration.hasMoreElements()) {


                String key = (String) enumeration.nextElement();

                Structure element = (Structure) Session.getListStructureObj().get(key);

                //load anagrafic menu
                if (element.getType().equals("AnagObj")) {

                    //if child not load
                    if (((agile.model.structure.AnagObj) Session.getListStructureObj().get(key)).isChild()) {
                        continue;
                    }

                    String description = ((agile.model.structure.AnagObj) Session.getListStructureObj().get(key)).getDescription();
                    String ico = ((agile.model.structure.AnagObj) Session.getListStructureObj().get(key)).getIconImg();

                    //#debug info                
                    System.out.println("2-Caricato:" + description);

                    Image icoImg = null;

                    try {
                        if (ico != null) {
                            icoImg = Image.createImage(ico.toString());
                        }
                    } catch (Exception e) {
                    }

                    //#style mainCommand
                    this.getMenuAnag().append(description, icoImg);

                    sizeList[0]=sizeList[0]+1;


                } else //load documental menu
                if (element.getType().equals("DocObj")) {

                    String description = (String) ((agile.model.structure.DocObj) Session.getListStructureObj().get(key)).getDescription();
                    String ico = ((agile.model.structure.DocObj) Session.getListStructureObj().get(key)).getIconImg();
                    Image icoImg = null;

                    try {
                        if (ico != null) {
                            icoImg = Image.createImage(ico.toString());
                        }
                    } catch (Exception e) {
                    }

                   //#style mainCommand
                   this.getMenuDoc().append(description, icoImg);

                    sizeList[1]=sizeList[1]+1;

                }

            }

            //strument menu

            //#style mainScreen
            //this.menuSet = new List(null, List.IMPLICIT);
            this.menuSet = new List(null, List.IMPLICIT);//Locale.get("mainmenuform.menu.strument")

            getMenuSet().setCommandListener(this);
           
            getMenuSet().setScreenStateListener(this);

           
            //#style tabIcon
            getTabbedPane().addTab((de.enough.polish.ui.Displayable)menuSet, Image.createImage("/setting1.png"), Locale.get("mainmenuform.menu.strument"));

            //#style mainCommand
            this.getMenuSet().append(Locale.get("mainmenuform.menu.setting"), Image.createImage("/Settings.png"));

            //#style mainCommand
            this.getMenuSet().append(Locale.get("mainmenuform.menu.calc"), Image.createImage("/Calculator1.png"));

            //#style mainCommand
            this.getMenuSet().append(Locale.get("mainmenuform.menu.calendar"), Image.createImage("/Calendar1.png"));

              //#style mainCommand
            this.getMenuSet().append("Carica Clienti", Image.createImage("/load1.png"));

              //#style mainCommand
            this.getMenuSet().append("Carica Prodotti", Image.createImage("/load1.png"));

              //#style mainCommand
            this.getMenuSet().append("Carica Ordini", Image.createImage("/load1.png"));

              //#style mainCommand
            this.getMenuSet().append("Carica Listini", Image.createImage("/load1.png"));


            //#ifdef polish.debugEnabled
             //#style mainCommand
             //# this.getMenuSet().append("Show log", Image.createImage("/log.png"));
             //# sizeList[2]=9;
            //#else
             sizeList[2]=8;
            //#endif


            //#style mainCommand
            this.getMenuSet().append(Locale.get("command.exit"), Image.createImage("/exit2.png"));           


//            getMenuAnag().addCommand(ManageViewNavigation.exit);
//            getMenuDoc().addCommand(ManageViewNavigation.exit);
//            getMenuSet().addCommand(ManageViewNavigation.exit);
//
//
//            getMenuAnag().addCommand(ManageViewNavigation.healpCommand);
//            getMenuDoc().addCommand(ManageViewNavigation.healpCommand);
//            getMenuSet().addCommand(ManageViewNavigation.healpCommand);
//
//
//            getMenuSet().addCommand(loadData);
//
//            getMenuSet().addSubCommand(forceLoadCli, loadData);
//            getMenuSet().addSubCommand(forceLoadProd, loadData);
//            getMenuSet().addSubCommand(forceLoadDoc, loadData);
//            getMenuSet().addSubCommand(forceLoadList, loadData);


//            getTabbedPane().addCommand(ManageViewNavigation.exit);
//            getTabbedPane().addCommand(ManageViewNavigation.healpCommand);
//            getTabbedPane().addCommand(loadData);
//            getTabbedPane().addSubCommand(forceLoadCli, loadData);
//            getTabbedPane().addSubCommand(forceLoadProd, loadData);
//            getTabbedPane().addSubCommand(forceLoadDoc, loadData);
//            getTabbedPane().addSubCommand(forceLoadList, loadData);
//
//            UiAccess.setAccessible(getTabbedPane(), ManageViewNavigation.exit, true);
//             UiAccess.setAccessible(getTabbedPane(), ManageViewNavigation.healpCommand, true);
//             UiAccess.setAccessible(getTabbedPane(), loadData, true);

            
            //size list - 1 for identify last postion list index
            sizeList[0]=sizeList[0]-1;
            sizeList[1]=sizeList[1]-1;
            sizeList[2]=sizeList[2]-1;

            getTabbedPane().setFocus(0);
            getTabbedPane().setCurrentTab(0);
           
            
            //getTabbedPane().addCommand(tryConnection);
            //getTabbedPane().addCommand(sendMex);
            //getTabbedPane().addCommand(register);

 //            UiAccess.addSubCommand(forceLoadCli, loadData, this);
//            UiAccess.addSubCommand(forceLoadProd, loadData, this);
//            UiAccess.addSubCommand(forceLoadDoc, loadData, this);
//            UiAccess.addSubCommand(forceLoadList, loadData, this);

            //this.setCommandListener(this);

        } catch (Exception e) {
            //#debug error
            System.out.println("Exception: " + e.getMessage());
        }

        return this;

    }

    public void commandAction(Command cmd, Displayable disp) {

        try {

              //#debug info
            System.out.println("Main command pressed !!");


            if (disp == this.getMenuAnag()) {

                String selectedobj = getMenuAnag().getString(getMenuAnag().getSelectedIndex());

                ManageViewNavigation.goNext((Viewable) (new FunctionMenuForm()).prepareView((Structure) Session.getListStructureObj().get(selectedobj), selectedobj));
               


            } else if (disp == this.getMenuDoc()) {

                String selectedobj = getMenuDoc().getString(getMenuDoc().getSelectedIndex());

                ManageViewNavigation.goNext((Viewable) (new FunctionMenuForm()).prepareView((Structure) Session.getListStructureObj().get(selectedobj), selectedobj));


            } else if (disp == this.getMenuSet()) {

                String command = getMenuSet().getString(getMenuSet().getSelectedIndex());




                if (command.equals("Carica Clienti")) {

                    Hashtable params = new Hashtable(2);
                    params.put("typeObj", "Cliente");
                    params.put("nomeFile", "/Aziende500.txt.gz");

                    Session.getMngService().doService(ManageService.THREAD_FORCELOADDATA_SERVICE, params);


                }else if (command.equals("Carica Prodotti")) {

                    Hashtable params = new Hashtable(2);

                    params.put("typeObj", "Prodotto");
                    params.put("nomeFile", "/Articoli500.txt.gz");

                    Session.getMngService().doService(ManageService.THREAD_FORCELOADDATA_SERVICE, params);


                }else if (command.equals("Carica Ordini")){

                    Hashtable params = new Hashtable(2);

                    params.put("typeObj", "Ordine Cli.");
                    params.put("nomeFile", "/Documenti10.txt.gz");

                    Session.getMngService().doService(ManageService.THREAD_FORCELOADDATA_SERVICE, params);


                 }else if (command.equals("Carica Listini")) {

                    Hashtable params = new Hashtable(2);

                    params.put("typeObj", "Listino");
                    params.put("nomeFile", "/Listini.txt.gz");

                    Session.getMngService().doService(ManageService.THREAD_FORCELOADDATA_SERVICE, params);


               }else if (command.equals("Prova connesione")) {

                    //add new THREAD_COMMIT_ANAGBEAN thread
                    Session.getMngThread().addThread(ManageThread.THREAD_SEND_HTTP_DOC);

                    //start thread
                    Session.getMngThread().startThread(ManageThread.THREAD_SEND_HTTP_DOC, null, "gaugeItemPercentade");


               }else if (command.equals("Invia sms")) {
                    //   this.inviaSMS();
               }else if (command.equals("Carica Listini")) {

                    try {

                        String url = "sms://:2828";
                        String filtro = "*";

                        PushRegistry.registerConnection(url, "agile.midlet.agileMidlet", filtro);

                        //#debug info
                        System.out.println("Connessione registrata con successo");

                    } catch (IllegalArgumentException e) {
                        //#debug info
                        System.out.println("Gli argomenti passati al metodo registerConnection() non sono validi");

                    } catch (ConnectionNotFoundException e) {
                        //#debug info
                        System.out.println("Il sistema non support ail push per questo tipo  di connessione       ");
                    } catch (IOException e) {
                        //#debug info
                        System.out.println("Connessione già registrata o risorse non disponibili per la sua creazione");

                    } catch (ClassNotFoundException e) {
                        //#debug info
                        System.out.println("La classe specificata non è stata trovata");
                    } catch (SecurityException e) {
                        //#debug info
                        System.out.println("La MIDlet non può registrare questa  connessione");

                    }


              }else if (command.equals(Locale.get("command.exit"))) {

                    exitMIDlet();

              }else if (command.equals(Locale.get("command.help"))) {
                    e = new Event();
                    e.setByName("helpFile", getHelpFile());
                    Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);

                } else if (command.equals(Locale.get("mainmenuform.menu.setting"))) {

                   ManageViewNavigation.goNext((Viewable) (new ConfigMenuForm()).prepareView() );
                   
                } else if (command.equals(Locale.get("mainmenuform.menu.calendar"))) {

                      ManageViewNavigation.goNext((Viewable) (new CalendarMenuForm()).prepareView() );
                    
                }

                //#ifdef polish.debugEnabled
                //# else if (command.equals("Show log")) {
                //#  Debug.showLog(ManageViewNavigation.getDisplay());
                //# }
                //#endif


            } 

              
           

        } catch (Exception e) {
            //#debug error
            System.out.println("Exception: " + e.getMessage());
        }


    }

//    public void inviaSMS() {
//
//         try {
//
//            String num = "sms://+393452166845:2828";
//            
//            MessageConnection connessione = (MessageConnection) Connector.open(num);
//            BinaryMessage messaggio = (BinaryMessage) connessione.newMessage(MessageConnection.BINARY_MESSAGE);
//            messaggio.setPayloadData("Ciao".getBytes());
//            int quanti = connessione.numberOfSegments(messaggio);
//
//            connessione.send(messaggio);
//            
//        } catch (Exception e) {
//            //#debug info
//            System.out.println("Invio SMS Exception: " + e.getMessage());
//        }
//
//    }
    public void itemStateChanged(de.enough.polish.ui.Item item) {
    }

    public void commandAction(Command cmd, de.enough.polish.ui.Item item) {
    }

    public void tabChangeEvent(Screen tab) {
        System.out.println("tabChangeEvent: " + tab);
    }

    public void notifyTabChangeCompleted(int oldTabIndex, int newTabIndex) {
        System.out.println("notifyTabChangeCompleted( old=" + oldTabIndex + ", new=" + newTabIndex + ")");

        //set to 0 position new visualized list
         if (!startup)
            ((List) getTabbedPane().getDisplayable(newTabIndex)).setSelectedIndex(0, true);

         oldposition=0;
         
    }

    public boolean notifyTabChangeRequested(int oldTabIndex, int newTabIndex) {
       //se tocuh screen non va gestito il sinistra destra key
            if (!touchScreen) {

                 //quando la pagina non è ancora caricata permetti tutto
                if (startup) {
                    return true;
                }

                try {

                      int pos = ((List) tabbedPane.getDisplayable(oldTabIndex)).getSelectedIndex();
                      System.out.println("!!!POS: " + pos);


                    System.out.println("notifyTabChangeRequested( old=" + oldTabIndex + ", new=" + newTabIndex + ")");

                   
                    //destra
                    if (newTabIndex > oldTabIndex) {


                        //se siamo nell'ultimo elemento e questo è in posizione dispari allora è corretto (in quanto spostandosi a destra da questo elemento la lista di selezione rimane ferma sull'elememento mentre se fosse pari tornerebbe all'elemento 0)
                        if (pos == sizeList[oldTabIndex]) {

                            if (!ispari(pos)) {

                                //ritorna l'ultima posizione in 3 casi: -da penultima ad ultima -da terzultima a ultima -da ultima a ultima(in caso di ultima pos dispari)
                                //ho notato che quando passa dalla posizione terzultima o penultima all 'ultima viene attivato screen state listener dal quale viene attivato la var activate che non mi rende valido lo spostamento di tab
                                if (activate) {

                                    activate = false;
                                    return false;

                                } else {

                                    activate = false;
                                    return true;

                                }


                            } else {

                                activate = false;
                                return true;
                            }

                        } else {

//                            //caso in cui dall'ultimo elemento della lista si pigia destra (ritorna alla posizione zero)
//                            if (pos == 0) {
//                                return false;
//                            }

                            if (ispari(pos)) {
                                 activate=false;
                                return true;
                            } else {
                                 activate=false;
                                return false;
                            }

                        }



                    } //sinistra
                    else if (newTabIndex < oldTabIndex) {

                        //caso in cui dall'ultimo elemento della lista si pigia sinistra (ritorna alla posizione zero)
                        if (pos == sizeList[oldTabIndex]) {
                             activate=false;
                            return true;
                        }

                        if (!ispari(pos)) {
                             activate=false;
                            return true;
                        } else {
                             activate=false;
                            return false;
                        }

                    } else //estremo
                    {
                         activate=false;
                        return false;
                    }

                } catch (Exception e2) {

                     //#debug error
                      System.out.println("Exception: " + e2.getMessage());

                     activate=false;
                    return true;

                }


            }else {

                 activate=false;
                 return true;
            }


	}

        public boolean ispari(int pos){
            if (pos%2>0)
                return false;
            else return true;
        }

        public void screenStateChanged(Screen arg0) {

            if (!touchScreen) {

                try {

                    int pos = ((List) tabbedPane.getDisplayable(tabbedPane.getSelectedIndex())).getSelectedIndex();

                    //#debug info
                    System.out.println("Screen Change oldPos: " + oldposition + " NewPos: " + pos + " TabInx: " + tabbedPane.getSelectedIndex());

                    //attivalo solo nella transizione dal penultimo all ultimo (quando l'ultimo elemento si trova sulla destra)
                    if ((oldposition == (sizeList[tabbedPane.getSelectedIndex()] - 1)) && (pos == sizeList[tabbedPane.getSelectedIndex()])) {
                        activate = true;
                    }

                    oldposition = pos;


                } catch (Exception e) {

                    //#debug error
                    System.out.println("Exception: " + e.getMessage());

                }



            }


    }

    public void exitMIDlet() {

        try {

            ManageViewNavigation.getDisplay().setCurrent(null);
            ManageViewNavigation.getMidlet().notifyDestroyed();

        } catch (Exception e) {
            //#debug error
            System.out.println("Exception: " + e.getMessage());
        }

    }

    /**
     * @return the tabbedPane
     */
    public TabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * @param tabbedPane the tabbedPane to set
     */
    public void setTabbedPane(TabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    /**
     * @return the menuAnag
     */
    public List getMenuAnag() {
        return menuAnag;
    }

    /**
     * @param menuAnag the menuAnag to set
     */
    public void setMenuAnag(List menuAnag) {
        this.menuAnag = menuAnag;
    }

    /**
     * @return the menuDoc
     */
    public List getMenuDoc() {
        return menuDoc;
    }

    /**
     * @param menuDoc the menuDoc to set
     */
    public void setMenuDoc(List menuDoc) {
        this.menuDoc = menuDoc;
    }

    /**
     * @return the menuSet
     */
    public List getMenuSet() {
        return menuSet;
    }

    /**
     * @param menuSet the menuSet to set
     */
    public void setMenuSet(List menuSet) {
        this.menuSet = menuSet;
    }

   
}
