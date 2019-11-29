/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

/**
 *
 * @author ruego
 */
public interface Synchronized {
    
     public Integer getSystemID();//unique systemID (for server sincronization)     
     public void setSystemID(Integer systemID);
     public Long getLastMod();//unique systemID (for server sincronization)     
     //public void setLastMod(Long lastMod);
     public boolean isSync();

}
