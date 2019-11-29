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
public class LongComparable  implements Comparable{

    private Long comp;

    public LongComparable(Long comparableLong){

        comp=comparableLong;
    }

     public int compareTo(Object compare) {
         
         long comp1=getComp().longValue();
         long comp2=((LongComparable)compare).getComp().longValue();

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
    public Long getComp() {
        return comp;
    }

    /**
     * @param comp the comp to set
     */
    public void setComp(Long comp) {
        this.comp = comp;
    }

   

}
