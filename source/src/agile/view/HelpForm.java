/*
 * guide.java
 *
 * Created on 9 marzo 2008, 15.27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package agile.view;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.Locale;
import java.io.InputStream;
import java.io.InputStreamReader;
import de.enough.polish.ui.Command;
import agile.control.ManageViewNavigation;


/**
 *
 * @author ruego
 */
public class HelpForm extends MainForm implements Viewable{
      
    /** Creates a new instance of guide */
    public HelpForm() {      
        
        super(Locale.get("HelpForm.title"), true,false,false,null);
       
    }
    
    public HelpForm prepareView(String helpFile) {

        this.deleteAll();
        
        if (helpFile != null) {

            String testo = readUnicodeFile("/" + helpFile + "_" + Locale.LANGUAGE);
            //#style input
            StringItem strItm = new StringItem("", testo, StringItem.PLAIN);
            this.append(strItm);
            
        }
        
        return this;

    }
 
    public void showView() {

         ManageViewNavigation.getDisplay().setCurrent(this);
     
    }
    
    public String readUnicodeFile(String filename) {
     StringBuffer buffer = null;
     InputStream is = null;
     InputStreamReader isr = null;
     try {
       Class c = this.getClass();
       is = c.getResourceAsStream(filename);     
       if (is == null)
         throw new Exception("File "+filename+" Does Not Exist");
       
       isr = new InputStreamReader(is);     
     
       buffer = new StringBuffer();
       int ch;
       while ((ch = isr.read()) > -1) {
         buffer.append((char)ch);
       }      
       if (isr != null) 
         isr.close();              
     } catch (Exception ex) {
       //#debug error
       System.out.println(ex);
     }
     return buffer.toString();      
   }
    
    
    public void itemStateChanged(Item item) {
        
            
    }

    
     public  void commandAction(Command cmd, Item item){
     
     }
    
}
