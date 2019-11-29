/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import java.util.Vector;

/**
 *
 * @author ruego
 */
public interface Indexable {
    
       public Vector getListIndexFields(); //of IndexFieldValue      
       public IndexFieldValue getClusteredField();
       public void updateIndexInfo();
       public boolean isChild();//if Obj insert in other Obj
       public String getType();//type of Obj
       public String getSourceType();//type of Source Obj (Doc)
       public Integer getNumRec();//StoreID
       public Integer getSourceNumRec();//StoreID
       public String serialize(); //to Store format
       public void unserialize(String JSONText) throws Exception ; //to UI format
       public String getTitle();//title for small print description of Obj
}
