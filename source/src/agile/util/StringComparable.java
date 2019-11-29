/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.util;

import de.enough.polish.util.Comparable;

/**
 *
 * @author Rocco
 */
public class StringComparable  implements Comparable{

    private String comp;

    public StringComparable(String comparableString){

        comp=comparableString;
    }

     public int compareTo(Object compare) {
         return getComp().compareTo( ((StringComparable)compare).getComp());
    }

    /**
     * @return the comp
     */
    public String getComp() {
        return comp;
    }

    /**
     * @param comp the comp to set
     */
    public void setComp(String comp) {
        this.comp = comp;
    }

   

}
