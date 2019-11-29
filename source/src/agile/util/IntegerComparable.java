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
public class IntegerComparable  implements Comparable{

    private Integer comp;

    public IntegerComparable(Integer comparableInteger){

        comp=comparableInteger;
    }

     public int compareTo(Object compare) {
         
         int comp1=getComp().intValue();
         int comp2=((IntegerComparable)compare).getComp().intValue();

         if (comp1 > comp2) {
             return 1;
         } else if (comp1 < comp2) {
             return -1;
         } else {
             return 0;
         }

         
    }

    /**
     * @return the comp
     */
    public Integer getComp() {
        return comp;
    }

    /**
     * @param comp the comp to set
     */
    public void setComp(Integer comp) {
        this.comp = comp;
    }

   

}
