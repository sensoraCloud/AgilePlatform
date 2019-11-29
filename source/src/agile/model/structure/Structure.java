/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author ruego
 */
public interface Structure {
    
    public String getDefaultGroupIndex();//String defalut group index fieldName
    public Vector getListIndexFields();//list of String index fieldName
    public Vector getListTypeUIIndexFields(); //type UI of IndexFieldValue vector position sinc with getListIndexFields() 
    public String getType();//type of class structure (AnagObj,DocObj,..)
    public Vector getPropertyByKey(String key);
    public boolean hasProperty(String key);
 
}
