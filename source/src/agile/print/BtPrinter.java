/*
 * BtPrinter.java
 *
 * Created on 28 ottobre 2007, 18.52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package agile.print;

//import javax.microedition.io.*;
//import java.io.*;
//import javax.bluetooth.*;
//import javax.microedition.lcdui.*;
//
///**
// *
// * @author Julien Buratto
// * Print over bluetooth searching for printers
// *
// * - Check if bluetooth is available: if (BtPrinter.check())
// * - Create create object and pass text to print, current display and next object to display:
// * BtPrinter bp=new BtPrinter(new StringBuffer("Hello printer!",this.getDisplay(),displayable);
// *
// */
//public class BtPrinter extends List implements CommandListener,DiscoveryListener {
//    
//    private Command OK=new Command("Stampa",Command.OK,1);
//    private Command CANCEL=new Command("Ferma",Command.CANCEL,3);
//    private Displayable backScreen=null;
//    private Display d=null;
//    private RemoteDevice[] foundDevices= new RemoteDevice[15];
//    private int found=0;
//    private StringBuffer toBePrinted=null;
//    public StreamConnection con = null;
//    public OutputStream out = null;
//    private DiscoveryAgent DA=null;
//    
//    public void commandAction(Command c, Displayable d) {
//        if (c==CANCEL) goBack();
//        if (c==OK) {
//            this.printTo(this.foundDevices[this.getSelectedIndex()].getBluetoothAddress());
//        }
//    }
//    
//    public static boolean check(){
//        try {
//            Class.forName("javax.bluetooth.DiscoveryAgent");
//            return true;
//        } catch (Exception e){
//            return false;
//        }
//    }
//    
//    /**
//     * Creates a new instance of BtPrinter
//     */
//    public BtPrinter(String toBePrinted,Display d,Displayable nextDisplayable) {
//        super("Ricerca stampanti",List.IMPLICIT);
//        this.toBePrinted=new StringBuffer(toBePrinted);
//        this.addCommand(CANCEL);
//        this.d=d;
//        this.backScreen=nextDisplayable;
//        this.setCommandListener(this);
//        this.append("Ricerca in corso...",null);
//        run();
//    }
//    
//    private void printTo(String remoteAddress){
//        try{
//            
//            String URL="btspp://"+remoteAddress+":1";
//            
//            con = (StreamConnection)Connector.open(URL);
//            out = con.openOutputStream();
//            out.write(this.toBePrinted.toString().getBytes());
//            out.close();
//            con.close();
//            Alert printed=new Alert("Finito!","La stampa � stata inviata.",null,AlertType.CONFIRMATION);
//            printed.setTimeout(5);
//            this.d.setCurrent(printed,this.backScreen);
//            
//        } catch (Exception e){
//            Alert errorPrinting=new Alert("Errore di stampa","Verifica che il bluetooth sia acceso a la stampante vicina.",null,AlertType.ERROR);
//            errorPrinting.setTimeout(Alert.FOREVER);
//            this.d.setCurrent(errorPrinting,this.backScreen);
//        }
//    }
//    
//    private void goBack(){
//        DA.cancelInquiry(this);
//        d.setCurrent(this.backScreen);
//    }
//    
//    public void inquiryCompleted(int discType){
//        
//        if (found==0){
//            Alert notFound=new Alert("Nessuna stampante rilevata","Nessuna stampante trovata.\nVerifica che il bluetooth sia acceso.",null,AlertType.ERROR);
//            notFound.setTimeout(Alert.FOREVER);
//            this.d.setCurrent(notFound,this.backScreen);
//        }
//        this.setTitle("Finished!");
//    }
//    public void serviceSearchCompleted(int transID, int respCode){
//        //
//    }
//    
//    public void servicesDiscovered(int transID,ServiceRecord[] rec){
//        //
//    }
//    public void deviceDiscovered(RemoteDevice btDevice,DeviceClass cod){
//        // MinorDeviceClass for printers is 128
//        try {
//            
//            if ((cod.getMinorDeviceClass()&0x80) != 0) {
//                this.append(btDevice.getFriendlyName(false),null);
//                if (found==0) {
//                    this.delete(0);
//                    this.addCommand(OK);
//                }
//                foundDevices[found]=btDevice;
//                found++;
//            }
//            
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        
//        
//    }
//    public void run() {
//        try {
//            LocalDevice LD=LocalDevice.getLocalDevice();
//            DA=LD.getDiscoveryAgent();
//            DA.startInquiry(DiscoveryAgent.GIAC,this);
//            
//        } catch (BluetoothStateException ex) {
//            Alert notFound=new Alert("Errore bluetooth","Verifica che il bluetooth sia attivo.",null,AlertType.ERROR);
//            notFound.setTimeout(Alert.FOREVER);
//            this.d.setCurrent(notFound,this.backScreen);
//        }
//    }
//    
//}
