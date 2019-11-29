/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

/**
 *
 * @author ruego
 */
public interface Storeable {
    
     public String serialize(); //to Store format
     public String serializeToServer(); //to Server format
     public void unserialize(String JSONText) throws Exception ; //to UI format
     public String toString();//to sending interface format
     public Integer getNumRec();//recordID rms store
    

}
