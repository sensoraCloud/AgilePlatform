/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.control;

import java.util.Hashtable;

/**
 *
 * @author ruego
 */
public class Event {
   
    Hashtable listProperties;    
    
    public void setByName(String name,Object value){
    
        if (listProperties==null)
            listProperties=new Hashtable();
       
        listProperties.put(name, value);
    
    }
    
     public Object getByName(String name){
    
        if (listProperties!=null)
            return listProperties.get(name);
        else return null;
        
    }

}
