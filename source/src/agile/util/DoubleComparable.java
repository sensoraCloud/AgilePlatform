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
public class DoubleComparable  implements Comparable{

    private Double comp;

    public DoubleComparable(Double comparableDouble){

        comp=comparableDouble;
    }

     public int compareTo(Object compare) {
         
         double comp1=getComp().doubleValue();
         double comp2=((DoubleComparable)compare).getComp().doubleValue();

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
    public Double getComp() {
        return comp;
    }

    /**
     * @param comp the comp to set
     */
    public void setComp(Double comp) {
        this.comp = comp;
    }

   

}
